import json
import os
from pathlib import Path
import re
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[3]
SCRIPT = re.compile(r"\.claude/hooks/[\w.-]+")


def bash_commands(path):
    groups = json.loads(path.read_text())["hooks"]["PreToolUse"]
    return [hook["command"] for group in groups if group.get("matcher") == "Bash" for hook in group["hooks"]]


def run(command, cwd, tool_command):
    environment = os.environ.copy()
    environment.pop("CLAUDE_PROJECT_DIR", None)
    payload = json.dumps({"cwd": str(cwd), "tool_name": "Bash", "tool_input": {"command": tool_command}})
    return subprocess.run(["sh", "-c", command], cwd=cwd, input=payload, text=True,
                          capture_output=True, env=environment)


class CodexHookParityTest(unittest.TestCase):
    # Codex reads .codex/hooks.json and Claude reads .claude/settings.json; both
    # must run the same guard scripts so the lifecycle rules do not diverge by agent.
    def test_codex_runs_the_same_guards_as_claude(self):
        claude = {m for c in bash_commands(ROOT / ".claude/settings.json") for m in SCRIPT.findall(c)}
        codex = {m for c in bash_commands(ROOT / ".codex/hooks.json") for m in SCRIPT.findall(c)}
        self.assertTrue(claude)
        self.assertEqual(claude, codex)

    def test_commit_guard_blocks_skipped_hooks(self):
        command = next(c for c in bash_commands(ROOT / ".codex/hooks.json") if "check-commit" in c)
        self.assertEqual(run(command, ROOT, "git commit --no-verify -m x").returncode, 2)
        self.assertEqual(run(command, ROOT, "git status").returncode, 0)

    def test_issue_guard_uses_the_repository_session(self):
        command = next(c for c in bash_commands(ROOT / ".codex/hooks.json") if "check-lifecycle" in c)
        with tempfile.TemporaryDirectory() as directory:
            repo = Path(directory)
            subprocess.run(["git", "init", "-q", str(repo)], check=True)
            (repo / ".claude").symlink_to(ROOT / ".claude")
            self.assertEqual(run(command, repo, "gh issue create -R owner/repo").returncode, 0)
            (repo / ".git" / "rallyon-lifecycle.json").write_text(json.dumps({"repository": "owner/repo"}))
            result = run(command, repo, "gh issue create -R owner/repo")
            self.assertEqual(result.returncode, 2)
            self.assertIn("create-issues", result.stderr)
            self.assertEqual(run(command, repo, "git status").returncode, 0)


if __name__ == "__main__":
    unittest.main()
