#!/usr/bin/env python3
"""Reproducible runtime-JAR inventory and isolated Dependency-Check execution."""
import argparse
from datetime import datetime, timedelta, timezone
import hashlib
import json
import os
from pathlib import Path
import subprocess
import signal
import tempfile
import xml.etree.ElementTree as ET
from urllib.parse import quote
from urllib.request import urlopen
from urllib.error import URLError

from validate_dependency_suppressions import validate

IMAGE = "owasp/dependency-check:13.0.0@sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a"

NVD_DATAFEED = "https://nvd.nist.gov/feeds/json/cve/2.0/nvdcve-2.0-{0}.json.gz"
NVD_METADATA = "https://nvd.nist.gov/feeds/json/cve/2.0/nvdcve-2.0-modified.meta"


def retrieve_metadata(url, timeout):
    # A socket timeout alone resets on reads and does not bound a slow download.
    # CI and the supported local runner use POSIX; the alarm bounds DNS and reads.
    def expired(signum, frame):
        raise TimeoutError("NVD metadata attempt exceeded its wall-clock deadline")

    previous_handler = signal.signal(signal.SIGALRM, expired)
    previous_timer = signal.setitimer(signal.ITIMER_REAL, timeout)
    try:
        with urlopen(url, timeout=timeout) as response:
            return response.read()
    finally:
        signal.setitimer(signal.ITIMER_REAL, 0)
        signal.signal(signal.SIGALRM, previous_handler)
        if previous_timer[0] or previous_timer[1]:
            signal.setitimer(signal.ITIMER_REAL, *previous_timer)


def nvd_metadata(fetch=None, now=None, evidence=None):
    fetch = fetch or retrieve_metadata
    evidence = evidence if evidence is not None else {}
    evidence["url"] = NVD_METADATA
    for attempt in range(3):
        try:
            raw = fetch(NVD_METADATA, timeout=20)
            break
        except (OSError, URLError) as error:
            if attempt == 2:
                raise OSError(f"official NVD metadata retrieval failed after three attempts: {type(error).__name__}: {error}") from error
    evidence["sha256"] = hashlib.sha256(raw).hexdigest()
    dates = [line.partition(":")[2].strip() for line in raw.decode("utf-8").splitlines()
             if line.partition(":")[0].strip() == "lastModifiedDate"]
    if len(dates) != 1:
        raise ValueError("NVD metadata needs exactly one lastModifiedDate")
    try:
        modified = datetime.fromisoformat(dates[0].replace("Z", "+00:00"))
    except ValueError as error:
        raise ValueError("invalid NVD lastModifiedDate") from error
    if modified.tzinfo is None or modified.utcoffset() is None:
        raise ValueError("NVD lastModifiedDate must include a timezone")
    evidence["last_modified_utc"] = modified.astimezone(timezone.utc).isoformat()
    now = now or datetime.now(timezone.utc)
    age = now - modified
    if age > timedelta(hours=48) or age < -timedelta(minutes=5):
        raise ValueError("NVD metadata outside 48-hour age / five-minute future bounds")
    return evidence


def trusted(event, ref, default_branch):
    return event in {"push", "schedule"} and bool(default_branch) and ref == f"refs/heads/{default_branch}"


def inventory(dependencies, revision):
    entries = []
    for path in sorted(dependencies.rglob("*.jar")):
        relative = path.relative_to(dependencies)
        parts = relative.parts
        if len(parts) < 4:
            raise ValueError("dependencies must use Maven repository layout")
        group, artifact, version = ".".join(parts[:-3]), parts[-3], parts[-2]
        entries.append({"path": relative.as_posix(), "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
                        "purl": f"pkg:maven/{quote(group)}/{quote(artifact)}@{quote(version)}"})
    if not entries:
        raise ValueError("no runtime JAR inputs")
    return {"revision": revision, "dependencies": entries}


def compare_bom(manifest, bom):
    expected = {entry["purl"] for entry in manifest["dependencies"]}
    actual = {entry.get("purl", "").split("?")[0] for entry in bom.get("components", [])}
    missing = sorted(expected - actual)
    if missing:
        raise ValueError("SBOM missing prepared runtime coordinates: " + ", ".join(missing))
    return {"revision": manifest["revision"], "runtime_coordinates_compared": len(expected), "missing": []}


def contains_secret(path, secret):
    overlap = b""
    with path.open("rb") as stream:
        while chunk := stream.read(1024 * 1024):
            combined = overlap + chunk
            if secret in combined:
                return True
            overlap = combined[-(len(secret) - 1):] if len(secret) > 1 else b""
    return False


def atomic_write(path, content):
    path = Path(path)
    temporary = None
    try:
        with tempfile.NamedTemporaryFile(dir=path.parent, prefix=".security-", delete=False) as stream:
            temporary = Path(stream.name)
            stream.write(content.encode() if isinstance(content, str) else content)
        os.replace(temporary, path)
    finally:
        if temporary is not None:
            temporary.unlink(missing_ok=True)


def check_paths(args):
    writable = [args.data.resolve(), args.reports.resolve()]
    immutable = [args.dependencies.resolve(), args.suppressions.resolve()]
    if args.manifest:
        immutable.append(args.manifest.resolve())

    def overlaps(first, second):
        return first.is_relative_to(second) or second.is_relative_to(first)

    if overlaps(*writable) or any(overlaps(write, read) for write in writable for read in immutable):
        raise ValueError("writable data/reports must not overlap each other or immutable inputs")


def run_scan(args, fetch_metadata=None, now=None):
    try:
        check_paths(args)
    except ValueError as error:
        print(str(error))
        return 1
    reports, data, dependencies = (p.resolve() for p in (args.reports, args.data, args.dependencies))
    reports.mkdir(parents=True, exist_ok=True)
    data.mkdir(parents=True, exist_ok=True)
    evidence = {"revision": args.revision, "image": IMAGE, "started_utc": datetime.now(timezone.utc).isoformat(),
                "reports_available": False, "exit_code": 1}
    key = os.environ.get("NVD_API_KEY", "") if trusted(args.event, args.ref, args.default_branch) else ""
    evidence["nvd_mode"] = "api" if key else "nist-json2-feed"
    atomic_write(reports / "execution.json", json.dumps(evidence, indent=2) + "\n")
    try:
        # Reused output directories must not relabel old engine reports as current.
        for name in ("dependency-check-report.json", "dependency-check-report.html"):
            (reports / name).unlink(missing_ok=True)
        validate(args.suppressions)
        inputs = inventory(dependencies, args.revision)
        if args.manifest and inputs != json.loads(args.manifest.read_text()):
            raise ValueError("prepared input hashes/revision differ")
        atomic_write(reports / "input-inventory.json", json.dumps(inputs, indent=2) + "\n")
        if not key:
            evidence["nvd_metadata"] = {}
            nvd_metadata(fetch=fetch_metadata, now=now, evidence=evidence["nvd_metadata"])
            atomic_write(reports / "execution.json", json.dumps(evidence, indent=2) + "\n")
        command = [args.docker, "run", "--rm", "--user", f"{os.getuid()}:{os.getgid()}",
                   "--mount", f"type=bind,src={dependencies},dst=/inputs,readonly",
                   "--mount", f"type=bind,src={data},dst=/usr/share/dependency-check/data",
                   "--mount", f"type=bind,src={reports},dst=/reports",
                   "--mount", f"type=bind,src={args.suppressions.resolve()},dst=/suppressions.xml,readonly"]
        with tempfile.TemporaryDirectory(prefix="rallyon-nvd-") as temporary:
            properties = Path(temporary) / "nvd.properties"
            if key:
                if any(c in key for c in "\r\n\\"):
                    raise ValueError("invalid NVD key format")
                properties.write_text("nvd.api.key=" + key + "\n")
                properties.chmod(0o600)
                command += ["--mount", f"type=bind,src={properties},dst=/nvd.properties,readonly"]
            command += [IMAGE, "--project", "rallyon-tournamentmgmt", "--scan", "/inputs", "--data", "/usr/share/dependency-check/data",
                        "--out", "/reports", "--format", "HTML", "--format", "JSON", "--failOnCVSS", "7",
                        "--suppression", "/suppressions.xml", "--nvdMaxRetryCount", "3"]
            if key:
                command += ["--propertyfile", "/nvd.properties"]
            else:
                command += ["--nvdDatafeed", NVD_DATAFEED]
            environment = {k: v for k, v in os.environ.items() if k not in {"NVD_API_KEY", "JAVA_HOME"}}
            result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, env=environment, check=False)
            output = result.stdout.replace(key.encode(), b"[REDACTED]") if key else result.stdout
            atomic_write(reports / "scanner.log", output)
            print(output.decode(errors="replace"), end="")
            evidence["exit_code"] = result.returncode if result.returncode >= 0 else 128 - result.returncode
        if key:
            # Engine error output must not accidentally persist configuration secrets.
            for directory in (reports, data):
                for path in directory.rglob("*"):
                    if path.is_file() and contains_secret(path, key.encode()):
                        path.unlink()
                        evidence["credential_leak_removed"] = True
                        evidence["exit_code"] = 1
        evidence["reports_available"] = (reports / "dependency-check-report.json").is_file()
        if evidence["exit_code"] == 0 and not evidence["reports_available"]:
            evidence["exit_code"] = 1
            evidence["error"] = "scanner returned success without its JSON report"
    except (OSError, ValueError, ET.ParseError) as error:
        evidence["exit_code"] = 1
        message = str(error).replace(key, "[REDACTED]") if key else str(error)
        evidence["error"] = message
        print(message)
    finally:
        evidence["finished_utc"] = datetime.now(timezone.utc).isoformat()
        atomic_write(reports / "execution.json", json.dumps(evidence, indent=2) + "\n")
    return evidence["exit_code"]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dependencies", type=Path, required=True)
    parser.add_argument("--revision", default=os.environ.get("GITHUB_SHA", "local"))
    parser.add_argument("--manifest", type=Path)
    parser.add_argument("--write-inventory", type=Path)
    parser.add_argument("--compare-bom", type=Path)
    parser.add_argument("--data", type=Path)
    parser.add_argument("--reports", type=Path)
    parser.add_argument("--suppressions", type=Path)
    parser.add_argument("--docker", default="docker")
    parser.add_argument("--event", default=os.environ.get("GITHUB_EVENT_NAME", "local"))
    parser.add_argument("--ref", default=os.environ.get("GITHUB_REF", ""))
    parser.add_argument("--default-branch", default=os.environ.get("DEFAULT_BRANCH", ""))
    args = parser.parse_args()
    if args.write_inventory:
        atomic_write(args.write_inventory, json.dumps(inventory(args.dependencies, args.revision), indent=2) + "\n")
        return 0
    if args.compare_bom:
        print(json.dumps(compare_bom(json.loads(args.manifest.read_text()), json.loads(args.compare_bom.read_text())), indent=2))
        return 0
    if not all((args.data, args.reports, args.suppressions)):
        parser.error("scan requires --data --reports --suppressions")
    return run_scan(args)


if __name__ == "__main__":
    raise SystemExit(main())
