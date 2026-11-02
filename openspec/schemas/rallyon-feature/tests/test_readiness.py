"""Exercise actual pinned OpenSpec readiness without touching repository changes."""

import json
import os
from pathlib import Path
import shlex
import shutil
import subprocess
import tempfile
import unittest


class SchemaReadinessTest(unittest.TestCase):
    def test_apply_needs_planning_but_not_post_implementation_reports(self):
        schema = Path(__file__).resolve().parents[1]
        command = shlex.split(
            os.environ.get("OPENSPEC_COMMAND", "npx -y @fission-ai/openspec@1.14.2")
        )
        with tempfile.TemporaryDirectory(prefix="rallyon-schema-") as directory:
            root = Path(directory)
            shutil.copytree(schema, root / "openspec/schemas/rallyon-feature")
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

            run("schema", "validate", "rallyon-feature", "--json")
            run("new", "change", "readiness", "--schema", "rallyon-feature", "--json")
            state = run("status", "--change", "readiness", "--json")
            self.assertEqual(
                [item["id"] for item in state["artifacts"] if item["status"] == "ready"],
                ["refinement"],
            )
            change = root / "openspec/changes/readiness"
            for name in ["refinement", "proposal", "design", "value"]:
                (change / f"{name}.md").write_text("# Planning evidence\n")
            (change / "tasks.md").write_text("# Tasks\n\n- [ ] 1.1 Implement and test\n")
            spec = change / "specs/example/spec.md"
            spec.parent.mkdir(parents=True)
            spec.write_text(
                "## ADDED Requirements\n\n### Requirement: Example\n"
                "The system SHALL provide an example.\n\n#### Scenario: Example\n"
                "- **WHEN** requested\n- **THEN** the example is returned\n"
            )
            blocked = run("instructions", "apply", "--change", "readiness", "--json")
            self.assertEqual(blocked["state"], "blocked")
            self.assertIn("issue-drafts", blocked["missingArtifacts"])
            (change / "issue-drafts.json").write_text(
                '{"repository":"wlambertz/rallyon","issues":[]}\n'
            )
            ready = run("instructions", "apply", "--change", "readiness", "--json")
            self.assertEqual(ready["state"], "ready")
            self.assertEqual(
                set(ready["contextFiles"]),
                {"refinement", "proposal", "specs", "design", "value", "tasks", "issue-drafts"},
            )
            for name in ["verification", "review", "uat"]:
                self.assertFalse((change / f"{name}.md").exists())


if __name__ == "__main__":
    unittest.main()
