#!/usr/bin/env python3
"""Reproducible runtime-JAR inventory and isolated Dependency-Check execution."""
import argparse
from datetime import datetime, timezone
import hashlib
import json
import os
from pathlib import Path
import subprocess
import tempfile
import xml.etree.ElementTree as ET
from urllib.parse import quote

from validate_dependency_suppressions import validate

IMAGE = "owasp/dependency-check:13.0.0@sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a"


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


def run_scan(args):
    reports, data, dependencies = (p.resolve() for p in (args.reports, args.data, args.dependencies))
    reports.mkdir(parents=True, exist_ok=True)
    data.mkdir(parents=True, exist_ok=True)
    evidence = {"revision": args.revision, "image": IMAGE, "started_utc": datetime.now(timezone.utc).isoformat(),
                "reports_available": False, "exit_code": 1}
    (reports / "execution.json").write_text(json.dumps(evidence, indent=2) + "\n")
    key = os.environ.get("NVD_API_KEY", "") if trusted(args.event, args.ref, args.default_branch) else ""
    try:
        validate(args.suppressions)
        inputs = inventory(dependencies, args.revision)
        if args.manifest and inputs != json.loads(args.manifest.read_text()):
            raise ValueError("prepared input hashes/revision differ")
        (reports / "input-inventory.json").write_text(json.dumps(inputs, indent=2) + "\n")
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
            environment = {k: v for k, v in os.environ.items() if k not in {"NVD_API_KEY", "JAVA_HOME"}}
            result = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, env=environment, check=False)
            output = result.stdout.replace(key.encode(), b"[REDACTED]") if key else result.stdout
            (reports / "scanner.log").write_bytes(output)
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
        message = str(error).replace(key, "[REDACTED]") if key else str(error)
        evidence["error"] = message
        print(message)
    finally:
        evidence["finished_utc"] = datetime.now(timezone.utc).isoformat()
        (reports / "execution.json").write_text(json.dumps(evidence, indent=2) + "\n")
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
        args.write_inventory.write_text(json.dumps(inventory(args.dependencies, args.revision), indent=2) + "\n")
        return 0
    if args.compare_bom:
        print(json.dumps(compare_bom(json.loads(args.manifest.read_text()), json.loads(args.compare_bom.read_text())), indent=2))
        return 0
    if not all((args.data, args.reports, args.suppressions)):
        parser.error("scan requires --data --reports --suppressions")
    return run_scan(args)


if __name__ == "__main__":
    raise SystemExit(main())
