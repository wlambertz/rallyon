"""Exercise actual pinned OpenSpec readiness for the research track."""

import json
import os
from pathlib import Path
import shlex
import shutil
import subprocess
import tempfile
import unittest


class ResearchSchemaReadinessTest(unittest.TestCase):
    def test_findings_need_charter_and_follow_ups_stay_optional(self):
        schema = Path(__file__).resolve().parents[1]
        command = shlex.split(
            os.environ.get("OPENSPEC_COMMAND", "npx -y @fission-ai/openspec@1.14.2")
        )
        with tempfile.TemporaryDirectory(prefix="rallyon-research-") as directory:
            root = Path(directory)
            shutil.copytree(schema, root / "openspec/schemas/rallyon-research")
            (root / "openspec/config.yaml").write_text("schema: spec-driven\n")

            def run(*arguments):
                result = subprocess.run(
                    [*command, *arguments],
                    cwd=root,
                    capture_output=True,
                    text=True,
                    timeout=60,
                    check=False,
                )
                self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
                return json.loads(result.stdout)

            def ready():
                state = run("status", "--change", "study", "--json")
                return [item["id"] for item in state["artifacts"] if item["status"] == "ready"]

            run("schema", "validate", "rallyon-research", "--json")
            run("new", "change", "study", "--schema", "rallyon-research", "--json")
            self.assertEqual(ready(), ["charter"])
            change = root / "openspec/changes/study"
            (change / "charter.md").write_text("# Research charter\n")
            self.assertEqual(ready(), ["findings"])
            (change / "findings.md").write_text("# Findings\n")
            self.assertEqual(set(ready()), {"acceptance", "follow-ups"})
            self.assertFalse((change / "specs").exists())
            self.assertFalse((change / "issue-drafts.json").exists())


if __name__ == "__main__":
    unittest.main()
