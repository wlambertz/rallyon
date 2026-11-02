import importlib.util
import json
import os
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
    def test_aliases_and_nested_substitutions_are_guarded(self):
        commands = [
            'gh issue new --repo owner/repo',
            'echo "$(gh issue --repo owner/repo create)"',
            'printf "%s" "`gh issue --repo owner/repo new`"',
            'echo "$(printf "%s" "$(gh issue --repo owner/repo new)")"',
            'echo "$(cat <<EOF\n$(gh issue --repo owner/repo new)\nEOF\n)"',
            'gh iss\\\nue create -R owner/repo',
            'echo "$(gh issue \\\n--repo owner/repo create)"',
            'cat <<EOF\n$(gh issue \\\n--repo owner/repo new)\nEOF\n',
        ]
        for command in commands:
            with self.subTest(command=command):
                self.assertTrue(hook.blocked(command, 'owner/repo'))
        self.assertFalse(hook.blocked('echo "$(gh issue --repo other/repo new)"', 'owner/repo'))
        self.assertFalse(hook.blocked("echo '$(gh issue --repo owner/repo new)'", 'owner/repo'))

    def test_heredoc_literals_and_executable_expansions(self):
        allowed = [
            "cat <<'EOF'\ngh issue create\n$(gh issue new)\nEOF\n",
            'cat <<"EOF"\ngh issue create\nEOF\n',
            'cat <<\\EOF\ngh issue create\nEOF\n',
            'cat <<EOF\nUse gh issue create after approval.\nEOF\n',
            'cat <<EOF\n\\$(gh issue create)\nEOF\n',
            "cat <<-'EOF'\n\tgh issue create\n\tEOF\n",
            "cat <<'ONE' <<'TWO'\ngh issue create\nONE\ngh issue new\nTWO\n",
            'cat <<EOF\n$(gh issue new -R other/repo)\nEOF\n',
        ]
        denied = [
            'cat <<EOF\n$(gh issue --repo owner/repo create)\nEOF\n',
            "cat <<EOF\n'$(gh issue --repo owner/repo new)'\nEOF\n",
            'cat <<EOF\n`gh issue --repo owner/repo new`\nEOF\n',
            "cat <<'ONE' <<TWO\ngh issue create\nONE\n$(gh issue new)\nTWO\n",
            "cat <<'EOF'\ngh issue create\nEOF\ngh issue new\n",
            'cat <<-EOF\n\t$(gh issue new)\n\tEOF\n',
        ]
        for command in allowed:
            with self.subTest(allowed=command):
                self.assertFalse(hook.blocked(command, 'owner/repo'))
        for command in denied:
            with self.subTest(denied=command):
                self.assertTrue(hook.blocked(command, 'owner/repo'))

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
            "gh issue --title hello create -R owner/repo --body example",
            "gh issue --recover '-Rother/repo' new --body example",
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
            "gh issue list --search new",
            "gh issue list --search create",
            "gh issue view 191 --jq new",
            "gh pr create --title example",
            "gh issue create -R other/repo",
            "gh issue create --repo https://github.com/other/repo",
            'echo "gh issue create"',
            "printf '%s\\n' 'gh issue create'",
            'python3 -c "print(\'gh issue create\')"',
            "python3 .agents/skills/feature-lifecycle/scripts/lifecycle.py --change demo create-issues",
            "git status # gh issue create",
            "cat <<< 'gh issue create'",
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

    def invoke(self, command, cwd=None, project=None):
        environment = os.environ.copy()
        environment.pop('CLAUDE_PROJECT_DIR', None)
        if project is not None:
            environment['CLAUDE_PROJECT_DIR'] = str(project)
        return subprocess.run(
            [sys.executable, str(HOOK)],
            input=json.dumps({"cwd": str(cwd or self.cwd), "tool_input": {"command": command}}),
            text=True, capture_output=True, env=environment,
        )

    def test_project_session_survives_external_cwd_and_has_priority(self):
        self.pointer.write_text(json.dumps({'repository': 'owner/repo'}))
        with tempfile.TemporaryDirectory() as directory:
            self.assertEqual(self.invoke('gh issue new -R owner/repo', cwd=directory, project=self.cwd).returncode, 2)
            self.assertEqual(self.invoke('gh issue new -R other/repo', cwd=directory, project=self.cwd).returncode, 0)
            self.pointer.write_text('invalid json')
            self.assertEqual(self.invoke('gh issue new -R other/repo', cwd=directory, project=self.cwd).returncode, 2)
            self.pointer.unlink()
            self.assertEqual(self.invoke('gh issue new -R owner/repo', cwd=directory, project=self.cwd).returncode, 0)
        inactive = self.cwd / 'inactive'
        inactive.mkdir()
        subprocess.run(['git', 'init', '-q', str(inactive)], check=True)
        self.pointer.write_text(json.dumps({'repository': 'owner/repo'}))
        self.assertEqual(self.invoke('gh issue new -R owner/repo', project=inactive).returncode, 2)

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
        self.assertEqual(self.invoke('gh issue new -R owner/repo', cwd=child, project=child).returncode, 0)
        self.assertEqual(self.invoke('gh issue new -R owner/repo', cwd=child, project=self.cwd).returncode, 2)


class ShellBehaviorTest(unittest.TestCase):
    def test_detection_matches_bash_execution_with_fake_github(self):
        new = ['issue', 'new']
        owner_new = ['issue', '--repo', 'owner/repo', 'new']
        commands = [
            ('gh issue new -R owner/repo', [['issue', 'new', '-R', 'owner/repo']], True),
            ('gh issue --title hello create -R owner/repo --body example', [['issue', '--title', 'hello', 'create', '-R', 'owner/repo', '--body', 'example']], True),
            ('gh iss\\\nue create -R owner/repo', [['issue', 'create', '-R', 'owner/repo']], True),
            ('echo "$(gh issue \\\n--repo owner/repo create)"', [['issue', '--repo', 'owner/repo', 'create']], True),
            ('cat <<EOF\n$(gh issue \\\n--repo owner/repo new)\nEOF\n', [owner_new], True),
            ("echo 'gh issue \\\nnew'", [], False),
            ("cat <<'EOF'\n$(gh issue \\\nnew)\nEOF\n", [], False),
            ('echo "$(gh issue --repo owner/repo create)"', [['issue', '--repo', 'owner/repo', 'create']], True),
            ('echo "$(gh issue --repo other/repo new)"', [['issue', '--repo', 'other/repo', 'new']], False),
            ('echo "$(printf "%s" "$(gh issue --repo owner/repo new)")"', [owner_new], True),
            ('echo "`gh issue --repo owner/repo new`"', [owner_new], True),
            ('echo `echo \\`gh issue new\\``', [new], True),
            ("echo '$(gh issue new)'", [], False),
            ("echo '`gh issue new`'", [], False),
            ('gh issue list --search new', [['issue', 'list', '--search', 'new']], False),
            ('cat <<< "$(gh issue new)"', [new], True),
            ("cat <<< 'gh issue create'", [], False),
            ("cat <<'EOF'\n$(gh issue new)\ngh issue create\nEOF\n", [], False),
            ('cat <<\\EOF\n$(gh issue new)\nEOF\n', [], False),
            ('cat <<E"OF"\n$(gh issue new)\nEOF\n', [], False),
            ('cat <<EOF\nUse gh issue create after approval.\nEOF\n', [], False),
            ('cat <<EOF\n\\$(gh issue new)\nEOF\n', [], False),
            ("cat <<EOF\n'$(gh issue new)'\nEOF\n", [new], True),
            ('cat <<EOF\n`gh issue new`\nEOF\n', [new], True),
            ("cat <<-'EOF'\n\t$(gh issue new)\n\tEOF\n", [], False),
            ('cat <<-EOF\n\t$(gh issue new)\n\tEOF\n', [new], True),
            ("cat <<'ONE' <<'TWO'\ngh issue create\nONE\ngh issue new\nTWO\n", [], False),
            ("cat <<'ONE' <<TWO\ngh issue create\nONE\n$(gh issue new)\nTWO\n", [new], True),
            ("cat <<'EOF'\ngh issue create\nEOF\ngh issue new\n", [new], True),
            ('echo "$(cat <<EOF\n$(gh issue new)\nEOF\n)"', [new], True),
            ('echo "$(cat <<\'EOF\'\n$(gh issue new)\nEOF\n)"', [], False),
            ('cat <<EOF\n$(cat <<\'INNER\'\n$(gh issue new)\nINNER\n)\nEOF\n', [], False),
            ('# $(gh issue new)\necho documentation', [], False),
        ]
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            fake = root / 'gh'
            log = root / 'calls.jsonl'
            fake.write_text(f'#!{sys.executable}\n' + "import json,os,sys\nwith open(os.environ['FAKE_GH_LOG'], 'a') as stream:\n    stream.write(json.dumps(sys.argv[1:]) + '\\n')\nprint('fake-result')\n")
            fake.chmod(0o755)
            environment = dict(os.environ, PATH=str(root) + os.pathsep + os.environ['PATH'], FAKE_GH_LOG=str(log))
            for command, expected_calls, expected_blocked in commands:
                with self.subTest(command=command):
                    log.unlink(missing_ok=True)
                    result = subprocess.run(['/bin/bash', '-c', command], env=environment, cwd=root, capture_output=True, text=True, timeout=10)
                    self.assertEqual(result.returncode, 0, result.stderr)
                    calls = [json.loads(line) for line in log.read_text().splitlines()] if log.exists() else []
                    self.assertEqual(calls, expected_calls)
                    self.assertEqual(hook.blocked(command, 'owner/repo'), expected_blocked)


if __name__ == "__main__":
    unittest.main()
