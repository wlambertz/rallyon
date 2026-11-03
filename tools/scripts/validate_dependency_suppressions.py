#!/usr/bin/env python3
"""Fail closed on broad, undocumented or stale Dependency-Check exceptions."""
import argparse
from datetime import datetime, timedelta, timezone
from pathlib import Path
import re
import xml.etree.ElementTree as ET

NS = "https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd"


def validate(path, now=None):
    now = now or datetime.now(timezone.utc)
    root = ET.parse(path).getroot()
    if root.tag != f"{{{NS}}}suppressions":
        raise ValueError("expected official suppression namespace")
    for rule in root:
        if rule.tag != f"{{{NS}}}suppress" or set(rule.attrib) != {"until"}:
            raise ValueError("each suppression needs only an explicit UTC until")
        expiry = rule.attrib["until"]
        if not re.fullmatch(r"\d{4}-\d{2}-\d{2}Z", expiry):
            raise ValueError("until must be YYYY-MM-DDZ")
        end = datetime.strptime(expiry, "%Y-%m-%dZ").replace(tzinfo=timezone.utc)
        if not now < end <= now + timedelta(days=90):
            raise ValueError("suppression expired or extends beyond 90 days")
        children = {}
        for node in rule:
            name = node.tag.removeprefix(f"{{{NS}}}")
            if name not in {"notes", "packageUrl", "filePath", "cpe", "cve"}:
                raise ValueError(f"unsafe suppression selector: {name}")
            children.setdefault(name, []).append(node)
        notes = children.get("notes", [])
        if len(notes) != 1 or len(notes[0].text or "") < 60 or not re.search(r"https://\S+", notes[0].text or ""):
            raise ValueError("notes need specific wrong-product rationale and primary HTTPS evidence")
        identities = children.get("packageUrl", []) + children.get("filePath", [])
        if len(identities) != 1:
            raise ValueError("one exact package/version identity required")
        identity = identities[0]
        value = identity.text or ""
        if identity.tag.endswith("packageUrl"):
            if identity.attrib or not re.fullmatch(r"pkg:maven/[A-Za-z0-9_.%-]+/[A-Za-z0-9_.%-]+@[A-Za-z0-9_.+%-]+", value):
                raise ValueError("packageUrl must be exact Maven package and version")
        else:
            # File paths are only a fallback for engine dependencies without purls.
            literal = value[1:-1] if value.startswith("^") and value.endswith("$") else ""
            pattern = literal.removeprefix(".*/").replace(r"\-", "-")
            literal = pattern.replace(r"\.", ".")
            if identity.attrib != {"regex": "true"} or not re.fullmatch(r"[A-Za-z0-9_./%-]+-[0-9][A-Za-z0-9_.%-]*\.jar", literal) or ".." in literal or pattern != literal.replace(".", r"\."):
                raise ValueError("filePath must anchor one exact versioned JAR basename")
        targets = children.get("cpe", []) + children.get("cve", [])
        if not targets:
            raise ValueError("specific wrong CPE or CVE required")
        for node in targets:
            value = node.text or ""
            if node.tag.endswith("cve"):
                specific = re.fullmatch(r"CVE-\d{4}-\d{4,}", value)
            else:
                # DC 13 compares converted CPE 2.2 URIs. A terminal colon makes
                # its prefix comparison stop at a field boundary (2 != 20).
                specific = re.fullmatch(
                    r"cpe:/[aoh]:[a-z0-9_.-]+:[a-z0-9_.-]+:[a-zA-Z0-9_.~-]+(?::[a-zA-Z0-9_.~-]+)*:",
                    value,
                )
            if node.attrib or not specific:
                raise ValueError("target needs a literal CVE or version-specific CPE 2.2 URI with terminal colon")
    return len(root)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("suppressions", type=Path)
    args = parser.parse_args()
    try:
        print(f"Validated {validate(args.suppressions)} suppression rules")
    except (ValueError, ET.ParseError, OSError) as error:
        parser.exit(1, f"Suppression validation failed: {error}\n")
