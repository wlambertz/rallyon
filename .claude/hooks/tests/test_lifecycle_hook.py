import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest


HOOK = Path(__file__).resolve().parents[1] / "check-lifecycle-issues.py"
spec = importlib.util.spec_from_file_location("lifecycle_hook", HOOK)
hook = importlib.util.module_from_spec(spec)
spec.loader.exec_module(hook)


class CommandDetectionTest(unittest.TestCase):
    def test_issue_creation_forms_are_blocked(self):
        commands = [
            "gh issue create --title example",
            "gh --hostname github.com issue create",
            "gh issue create -R owner/repo",
            "gh issue create --repo=owner/repo",
            "/usr/local/bin/gh issue create -Rowner/repo",
            "env GH_TOKEN=example gh issue create",
            "sudo -u developer gh issue create",
            "command gh issue create",
            "cd somewhere && gh issue create",
            "true; gh issue create",
            "true\ngh issue create",
            "true | xargs gh issue create",
            "bash -lc 'gh issue create'",
            "env bash -c 'gh issue create'",
            "eval 'gh issue create'",
            'echo "$(gh issue create)"',
            'printf "%s" "`gh issue create`"',
            "RALLYON_LIFECYCLE_HELPER=1 gh issue create",
            "gh issue create -R '$REPOSITORY'",
            "gh issue create --repo owner/repo --repo other/repo",
            "gh issue create --title '-Rother/repo' --body example",
            "gh issue create --body '-Rother/repo' --title example",
            "gh issue create --repo '$OWNER/$REPO'",
            "gh issue --repo owner/repo create",
        ]
        for command in commands:
            with self.subTest(command=command):
                self.assertTrue(hook.blocked(command, "owner/repo"))

    def test_unrelated_and_documentation_commands_are_allowed(self):
        commands = [
            "echo '$(gh issue create)'",
            "echo '`gh issue create`'",
            "gh issue list",
            "gh issue view 191",
            "gh pr create --title example",
            "gh issue create -R other/repo",
            "gh issue create --repo https://github.com/other/repo",
            'echo "gh issue create"',
            "printf '%s\\n' 'gh issue create'",
            'python3 -c "print(\'gh issue create\')"',
            "python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change demo create-issues",
            "git status # gh issue create",
        ]
        for command in commands:
            with self.subTest(command=command):
                self.assertFalse(hook.blocked(command, "owner/repo"))

    def test_unreadable_session_blocks_uncertain_repository(self):
        self.assertTrue(hook.blocked("gh issue create -R other/repo", ""))


class HookIntegrationTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.cwd = Path(self.temporary.name)
        subprocess.run(["git", "init", "-q", str(self.cwd)], check=True)
        self.pointer = self.cwd / ".git" / "rallyon-lifecycle.json"

    def tearDown(self):
        self.temporary.cleanup()

    def invoke(self, command):
        return subprocess.run(
            [sys.executable, str(HOOK)],
            input=json.dumps({"cwd": str(self.cwd), "tool_input": {"command": command}}),
            text=True, capture_output=True,
        )

    def test_only_active_session_is_guarded(self):
        self.assertEqual(self.invoke("gh issue create").returncode, 0)
        self.pointer.write_text(json.dumps({"change": "openspec/changes/demo", "repository": "owner/repo"}))
        result = self.invoke("gh issue create")
        self.assertEqual(result.returncode, 2)
        self.assertIn("create-issues", result.stderr)
        self.assertEqual(self.invoke("gh issue create -R other/repo").returncode, 0)
        self.pointer.unlink()
        self.assertEqual(self.invoke("gh issue create").returncode, 0)

    def test_corrupt_pointer_does_not_silently_disable_guard(self):
        self.pointer.write_text("invalid json")
        self.assertEqual(self.invoke("gh issue create").returncode, 2)
        self.assertEqual(self.invoke("git status").returncode, 0)

    def test_worktree_pointer_is_isolated(self):
        # Git's per-worktree git-path, rather than the shared common directory,
        # determines the session to guard.
        subprocess.run(["git", "-C", str(self.cwd), "-c", "user.name=Test", "-c", "user.email=test@example.com", "commit", "--allow-empty", "-qm", "initial"], check=True)
        child = self.cwd / "child"
        subprocess.run(["git", "-C", str(self.cwd), "worktree", "add", "-qb", "child", str(child)], check=True)
        self.pointer.write_text(json.dumps({"repository": "owner/repo"}))
        self.assertIsNone(hook.active_session(str(child)))
        self.assertIsNotNone(hook.active_session(str(self.cwd)))


if __name__ == "__main__":
    unittest.main()
