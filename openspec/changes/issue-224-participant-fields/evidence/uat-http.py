"""Creator walkthrough against the isolated localhost service and test realm only."""
import copy
import json
import os
import subprocess
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

BASE = "http://127.0.0.1:18080"
RO_BINARY = Path(__file__).resolve().parents[4] / "bin" / "ro"


def token(user):
    env = dict(os.environ, KEYCLOAK_SERVER="http://localhost:18081",
               RALLYON_CLIENT_ID="rallyon-api", RALLYON_REALM="rallyon",
               RALLYON_CLIENT_SECRET="rallyon-uat-only", RALLYON_DEV_USERNAME=user,
               RALLYON_DEV_PASSWORD="rallyon-uat-password")
    result = subprocess.run([str(RO_BINARY), "auth", "token", "--format", "raw"],
                            env=env, capture_output=True, text=True, check=True)
    return result.stdout.strip()


def call(method, path, expected, credential=None, data=None, version=None, raw=None):
    headers = {}
    if credential:
        headers["Authorization"] = "Bearer " + credential
    if version is not None:
        headers["If-Match"] = str(version)
    body = None
    if data is not None or raw is not None:
        body = raw.encode() if raw is not None else json.dumps(data).encode()
        headers["Content-Type"] = "application/json"
    request = urllib.request.Request(BASE + path, data=body, headers=headers, method=method)
    try:
        response = urllib.request.urlopen(request)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        status, text = response.status, response.read().decode()
        assert status == expected, (method, path, expected, status, text[:500])
        if "/participant-fields/" in path:
            assert "no-store" in response.headers.get("Cache-Control", "")
        return json.loads(text) if text else None


creator, other = token("dev.organizer"), token("uat.other")
draft = call("POST", "/api/tournamentmgmt/config/drafts?organizerId=42", 201, creator,
             {"name": "Issue 224 UAT", "visibility": "PRIVATE"})
tid = draft["id"]
config = f"/api/tournamentmgmt/config/{tid}"
entry_path = f"/api/tournamentmgmt/entries/{tid}"
field_path = f"/api/tournamentmgmt/participant-fields/{tid}"
draft_path = config + "/draft"
intake_path = entry_path + "/intake"
bracket_a = {"id": {"value": "A"}, "displayName": "Initial A", "format": "ROUND_ROBIN",
             "capacity": {"amount": 1, "unit": "PARTICIPANTS"}}
draft["disciplines"] = [
    {"id": 1, "category": "SINGLES", "displayName": "Singles", "teamSize": "SINGLES",
     "brackets": [bracket_a]},
    {"id": 2, "category": "DOUBLES", "displayName": "Doubles", "teamSize": "DOUBLES",
     "brackets": [{"id": {"value": "B"}, "displayName": "Doubles B", "format": "ROUND_ROBIN", "capacity": None}]},
]
call("PUT", draft_path, 200, creator, draft, draft["version"])


def book():
    return call("GET", entry_path, 200, creator)


def field(discipline=1, bracket="A"):
    query = urllib.parse.urlencode({"disciplineId": discipline, "bracketId": bracket})
    return call("GET", field_path + "?" + query, 200, creator)


def payload(ids, discipline=1, bracket="A"):
    return {"disciplineId": discipline, "bracketId": bracket, "entryIds": ids,
            "sourceTournamentVersion": call("GET", config, 200, creator)["version"],
            "sourceEntryBookVersion": book()["version"]}


def prepare(ids, version, expected=200, discipline=1, bracket="A", credential=creator):
    return call("PUT", field_path, expected, credential, payload(ids, discipline, bracket), version)


def saved(view):
    keys = ["prepared", "version", "selection", "sourceTournamentVersion", "sourceEntryBookVersion", "units", "audit"]
    return {key: view[key] for key in keys}


def single(name="Alpha"):
    return {"disciplineId": 1, "bracketId": "A", "players": [{"key": "p1", "displayName": name}]}


call("PUT", intake_path, 200, creator, {"open": True}, 0)
inputs = [single(), {"disciplineId": 1, "bracketId": "A", "players": [{"key": "p2", "displayName": "Beta"}]},
          {"disciplineId": 2, "bracketId": "B", "players": [{"key": "p1", "displayName": "Alpha"},
                                                             {"key": "p3", "displayName": "Gamma"}]}]
ids = []
for item in inputs:
    created = call("POST", entry_path + "/records", 201, creator, item, book()["version"])
    eid = created["records"][-1]["id"]
    ids.append(eid)
    call("PUT", entry_path + f"/records/{eid}/decision", 200, creator, {"state": "ACCEPTED"}, book()["version"])
call("PUT", intake_path, 200, creator, {"open": False}, book()["version"])

empty = field()
assert not empty["prepared"] and empty["version"] == 0 and empty["freshness"] == "UNPREPARED"
read_path = field_path + "?disciplineId=1&bracketId=A"
call("GET", read_path, 401)
call("GET", read_path, 403, other)
prepare(ids[:2], 0, 403, credential=other)
invalid = payload(ids[:2])
for key in ["disciplineId", "sourceTournamentVersion", "sourceEntryBookVersion"]:
    malformed = dict(invalid, **{key: 1.9})
    call("PUT", field_path, 400, creator, malformed, 0)
malformed = dict(invalid, entryIds=[1.9])
call("PUT", field_path, 400, creator, malformed, 0)
assert field() == empty
print("1. Privat/UNPREPARED: Creator liest Version 0; anonym 401, fremder Benutzer 403; gebrochene IDs/Versionen 400 ohne Änderung.")

source_before = book()
prepared = prepare(list(reversed(ids[:2])), 0)
assert [u["entryId"] for u in prepared["units"]] == ids[:2]
assert prepared["freshness"] == "CURRENT" and prepared["capacityReview"] == "MANUAL"
assert prepared["version"] == 1 and not book()["intakeOpen"]
assert book() == source_before
paired = prepare([ids[2]], 0, discipline=2, bracket="B")
assert [p["key"] for p in paired["units"][0]["players"]] == ["p1", "p3"]
assert field() == prepared
prepare(ids[:2], 0, 409)
prepare([999999], 1, 400)
assert field() == prepared
print("2. Einzel- und Doppelfeld explizit vorbereitet; geschlossene Aufnahme und Kapazität 1 bleiben manuell; stale/ungültige Auswahl ohne Änderung.")

call("PUT", intake_path, 200, creator, {"open": True}, book()["version"])
corrected = call("PUT", entry_path + f"/records/{ids[0]}", 200, creator, single("Alpha corrected"), book()["version"])
assert corrected["records"][0]["state"] == "RECEIVED"
outdated = field()
assert saved(outdated) == saved(prepared) and outdated["freshness"] == "OUTDATED"
prepare(ids[:2], 1, 409)
assert saved(field()) == saved(prepared)
call("PUT", entry_path + f"/records/{ids[0]}/decision", 200, creator, {"state": "ACCEPTED"}, book()["version"])
assert saved(field()) == saved(prepared)
call("PUT", intake_path, 200, creator, {"open": False}, book()["version"])
reconciled = prepare(ids[:2], 1)
assert reconciled["version"] == 2 and reconciled["freshness"] == "CURRENT"
assert reconciled["units"][0]["players"][0]["displayName"] == "Alpha corrected"
call("DELETE", entry_path + f"/records/{ids[1]}", 200, creator, version=book()["version"])
assert saved(field()) == saved(reconciled) and field()["freshness"] == "OUTDATED"
print("3. Meldung korrigiert/erneut angenommen: Feld bleibt erhalten und OUTDATED; ausdrücklicher Abgleich übernimmt Korrektur. Rückzug schreibt Feld ebenfalls nicht um.")

current = call("GET", config, 200, creator)
current["disciplines"][0]["brackets"][0]["displayName"] = "Renamed A"
call("PUT", draft_path, 200, creator, current, current["version"])
renamed = prepare([ids[0]], 2)
assert renamed["selection"]["bracketName"] == "Renamed A"
assert renamed["units"][0]["selection"]["bracketName"] == "Initial A"
current = call("GET", config, 200, creator)
current["disciplines"][0]["brackets"] = []
if current.get("bracketRosters"):
    current["bracketRosters"].pop("A", None)
call("PUT", draft_path, 200, creator, current, current["version"])
assert saved(field()) == saved(renamed) and field()["freshness"] == "OUTDATED"
prepare([], 3, 400)
assert saved(field()) == saved(renamed)
current = call("GET", config, 200, creator)
restored = copy.deepcopy(bracket_a)
restored["displayName"] = "Restored A"
current["disciplines"][0]["brackets"] = [restored]
call("PUT", draft_path, 200, creator, current, current["version"])
cleared = prepare([], 3)
assert cleared["version"] == 4 and not cleared["units"] and cleared["freshness"] == "CURRENT"
identical = prepare([], 4)
assert identical["version"] == 5 and len(identical["audit"]) == 5
assert all(a["actorId"] == 42 and a["operation"] == "PREPARE" and a["at"] for a in identical["audit"])
assert saved(field(2, "B")) == saved(paired)
assert field(2, "B")["freshness"] == "OUTDATED"
print("4. Alte/neue Bezeichnung unterscheidbar; entferntes Feld privat lesbar; Wiederherstellung und ausdrückliches Leeren erfolgreich, Audit vollständig, Doppelfeld unverändert.")
print(f"PASS: Turnier {tid}, Einzelfeldversion 5, Doppelfeldversion 1; nur synthetische Daten in rallyon_field224_uat.")
