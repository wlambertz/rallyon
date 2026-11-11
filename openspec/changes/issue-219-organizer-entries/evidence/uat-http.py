"""Run only against the isolated local UAT service and test realm."""
import copy
import json
import urllib.error
import urllib.parse
import urllib.request

BASE = "http://127.0.0.1:18080"
TOKEN = "http://localhost:18081/realms/rallyon/protocol/openid-connect/token"


def token(user):
    body = urllib.parse.urlencode({
        "grant_type": "password", "client_id": "rallyon-api",
        "client_secret": "rallyon-uat-only", "username": user,
        "password": "rallyon-uat-password",
    }).encode()
    with urllib.request.urlopen(urllib.request.Request(TOKEN, data=body)) as response:
        return json.load(response)["access_token"]


def call(method, path, expected, credential=None, data=None, version=None):
    headers = {}
    if credential:
        headers["Authorization"] = "Bearer " + credential
    if version is not None:
        headers["If-Match"] = str(version)
    body = None
    if data is not None:
        body = json.dumps(data).encode()
        headers["Content-Type"] = "application/json"
    request = urllib.request.Request(BASE + path, data=body, headers=headers, method=method)
    try:
        response = urllib.request.urlopen(request)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        status, text = response.status, response.read().decode()
        assert status == expected, (method, path, expected, status, text[:500])
        if "/entries/" in path:
            assert "no-store" in response.headers.get("Cache-Control", "")
        return json.loads(text) if text else None


creator, other = token("dev.organizer"), token("uat.other")
draft = call("POST", "/api/tournamentmgmt/config/drafts?organizerId=42", 201,
             creator, {"name": "Issue 219 UAT", "visibility": "PRIVATE"})
tid = draft["id"]
config = f"/api/tournamentmgmt/config/{tid}"
draft["disciplines"] = [
    {"id": 1, "category": "SINGLES", "displayName": "Einzel", "teamSize": "SINGLES",
     "brackets": [{"id": {"value": "A"}, "displayName": "Feld A", "format": "ROUND_ROBIN", "capacity": None}]},
    {"id": 2, "category": "DOUBLES", "displayName": "Doppel", "teamSize": "DOUBLES",
     "brackets": [{"id": {"value": "B"}, "displayName": "Feld B", "format": "ROUND_ROBIN", "capacity": None}]},
]
call("PUT", config + "/draft", 200, creator, draft, draft["version"])
path = f"/api/tournamentmgmt/entries/{tid}"
book = call("GET", path, 200, creator)
assert book["version"] == 0 and not book["intakeOpen"] and not book["records"]
call("GET", path, 401)
call("GET", path, 403, other)
single = {"disciplineId": 1, "bracketId": "A", "players": [{"key": "p1", "displayName": "Anna"}]}
call("POST", path + "/records", 409, creator, single, 0)
assert call("GET", path, 200, creator) == book
print("1. Privat: Creator liest CLOSED/0; anonym 401, fremder Benutzer 403; geschlossene Aufnahme 409 ohne Änderung.")

book = call("PUT", path + "/intake", 200, creator, {"open": True}, 0)
book = call("POST", path + "/records", 201, creator, single, book["version"])
sid = book["records"][0]["id"]
double = {"disciplineId": 2, "bracketId": "B", "players": [
    {"key": "p1", "displayName": "Anna"}, {"key": "p2", "displayName": "Berta"}]}
book = call("POST", path + "/records", 201, creator, double, book["version"])
duplicate = copy.deepcopy(double)
duplicate["players"].reverse()
call("POST", path + "/records", 409, creator, duplicate, book["version"])
call("DELETE", path + f"/records/{sid}", 403, other, version=book["version"])
call("PUT", path + "/intake", 409, creator, {"open": False}, 0)
assert call("GET", path, 200, creator) == book
print("2. Einzel und Doppel erfasst; umgekehrtes Paar, fremde Änderung und veraltete Version abgewiesen; vollständiges Buch unverändert.")

book = call("PUT", path + f"/records/{sid}/decision", 200, creator, {"state": "ACCEPTED"}, book["version"])
assert book["records"][0]["state"] == "ACCEPTED"
single["players"][0]["displayName"] = "Anna korrigiert"
book = call("PUT", path + f"/records/{sid}", 200, creator, single, book["version"])
assert book["records"][0]["state"] == "RECEIVED"
book = call("PUT", path + "/intake", 200, creator, {"open": False}, book["version"])
call("POST", path + "/records", 409, creator, single, book["version"])
call("PUT", path + f"/records/{sid}", 409, creator, single, book["version"])
assert call("GET", path, 200, creator) == book
book = call("PUT", path + f"/records/{sid}/decision", 200, creator, {"state": "ACCEPTED"}, book["version"])
print("3. Annahme und Korrektur: Zustand erneut RECEIVED; Schließung blockiert Erfassung/Korrektur, erlaubt bewusste Annahme.")

draft = call("GET", config, 200, creator)
draft["disciplines"] = []
draft["bracketRosters"] = {}
call("PUT", config + "/draft", 200, creator, draft, draft["version"])
assert call("GET", path, 200, creator) == book
assert book["records"][0]["selection"]["bracketName"] == "Feld A"
book = call("DELETE", path + f"/records/{sid}", 200, creator, version=book["version"])
assert book["records"][0]["state"] == "WITHDRAWN"
call("DELETE", path + f"/records/{sid}", 409, creator, version=book["version"])
assert call("GET", path, 200, creator) == book
assert len(book["audit"]) == book["version"]
assert all(event["actorId"] == 42 and event["at"] for event in book["audit"])
print("4. Draft-Felder entfernt: Meldesnapshots bleiben erhalten; Rückzug bei geschlossener Aufnahme möglich und terminal; privater Audit vollständig.")
print(f"PASS: Turnier {tid}, Buchversion {book['version']}, {len(book['records'])} Meldungen, {len(book['audit'])} Auditereignisse; Testdaten nur in rallyon_entry219_uat.")
