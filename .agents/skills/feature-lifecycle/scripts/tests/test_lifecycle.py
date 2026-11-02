import importlib.util
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch

HELPER = Path(__file__).resolve().parents[1] / 'lifecycle.py'
spec = importlib.util.spec_from_file_location('lifecycle', HELPER)
lifecycle = importlib.util.module_from_spec(spec)
spec.loader.exec_module(lifecycle)


class LifecycleTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.directory = Path(self.temp.name)
        self.root = self.directory / 'repo'
        self.root.mkdir()
        self.git('init', '-q')
        self.git('config', 'user.name', 'Test')
        self.git('config', 'user.email', 'test@example.com')
        (self.root / 'source.txt').write_text('original\n')
        self.git('add', '.')
        self.git('commit', '-qm', 'initial')
        self.change = self.root / 'openspec/changes/demo'
        self.change.mkdir(parents=True)
        for name in ['refinement.md', 'proposal.md', 'design.md', 'value.md', 'tasks.md', 'lifecycle.md', 'uat.md']:
            (self.change / name).write_text(f'# {name}\nConcrete test evidence\n')
        specs = self.change / 'specs/example'
        specs.mkdir(parents=True)
        (specs / 'spec.md').write_text('## ADDED Requirements\n### Requirement: Example\n#### Scenario: Success\n- WHEN requested\n- THEN succeeds\n')
        self.payload = {'repository': 'owner/repo', 'issues': [
            {'id': 'first', 'title': 'First', 'body': 'First body', 'depends_on': [], 'labels': ['feature'], 'assignees': ['maintainer'], 'milestone': 'Phase B'},
            {'id': 'second', 'title': 'Second', 'body': 'Second body', 'depends_on': ['first']},
        ]}
        self.write_payload()
        (self.change / 'evidence').mkdir()
        for gate in ['G3', 'G5', 'G6', 'G7', 'architecture', 'correctness', 'tests']:
            (self.change / f'evidence/{gate}.md').write_text(f'Checked {gate}: passed\n')
        self.session = lifecycle.Session(self.root, 'demo')
        self.session.start('owner/repo', 'maker')
        fakebin = self.directory / 'bin'
        fakebin.mkdir()
        self.log = self.directory / 'gh.jsonl'
        fake = fakebin / 'gh'
        fake.write_text(f'#!{sys.executable}\n' + '''import json, os, pathlib, sys
args = sys.argv[1:]
body = pathlib.Path(args[args.index('--body-file') + 1]).read_text()
with open(os.environ['FAKE_GH_LOG'], 'a') as log:
    log.write(json.dumps({'args': args, 'body': body}) + '\\n')
if args[args.index('--title') + 1] == os.environ.get('FAKE_GH_FAIL_TITLE'):
    sys.exit(1)
number = len(pathlib.Path(os.environ['FAKE_GH_LOG']).read_text().splitlines())
print(f'https://github.com/owner/repo/issues/{number}')
''')
        fake.chmod(0o755)
        self.environment = patch.dict(os.environ, {'PATH': str(fakebin) + os.pathsep + os.environ['PATH'], 'FAKE_GH_LOG': str(self.log)})
        self.environment.start()

    def tearDown(self):
        self.environment.stop()
        self.temp.cleanup()

    def git(self, *args):
        return subprocess.check_output(['git', '-C', str(self.root), *args], stderr=subprocess.PIPE)

    def write_payload(self):
        (self.change / 'issue-drafts.json').write_text(json.dumps(self.payload))

    def approve(self, gate, **kwargs):
        self.session.record(gate, 'maintainer', quote='I approve these displayed artifacts', source='chat:test-approval', **kwargs)

    def check(self, gate, lens=None, actor='checker'):
        self.session.record(gate, actor, evidence=f'openspec/changes/demo/evidence/{lens or gate}.md', lens=lens)

    def planning(self):
        self.approve('G1')
        self.approve('G2')
        self.check('G3')
        self.approve('G4')

    def implementation(self):
        self.planning()
        self.session.create_issues()
        self.check('G5')
        self.check('G6')

    def calls(self):
        return [json.loads(line) for line in self.log.read_text().splitlines()] if self.log.exists() else []

    def test_full_workflow_and_exact_issue_payload(self):
        self.implementation()
        for lens in lifecycle.LENSES:
            self.check('G7', lens=lens)
        self.approve('G8')
        self.approve('merge')
        self.session.require('merge')
        self.assertEqual([call['body'] for call in self.calls()], ['First body', 'Second body'])
        self.assertEqual(self.calls()[0]['args'][:6], ['issue', 'create', '--repo', 'owner/repo', '--title', 'First'])
        for flag in ['--label', '--assignee', '--milestone']:
            self.assertIn(flag, self.calls()[0]['args'])
        self.session.create_issues()
        self.assertEqual(len(self.calls()), 2)

    def test_reduced_progression(self):
        self.session.pointer.unlink()
        self.session.path.unlink()
        self.session = lifecycle.Session(self.root, 'demo')
        self.session.start('owner/repo', 'maker', mode='reduced')
        for path in ['specs/example/spec.md', 'value.md', 'design.md']:
            (self.change / path).unlink()
        self.implementation()
        self.check('G7')
        self.approve('G8')
        self.approve('merge')
        self.session.require('merge')

    def test_missing_approval_blocks_creation_and_apply(self):
        with self.assertRaises(lifecycle.LifecycleError):
            self.session.create_issues()
        with self.assertRaises(lifecycle.LifecycleError):
            self.session.record('G1', 'maintainer')
        self.approve('G1')
        with self.assertRaises(lifecycle.LifecycleError):
            self.session.require('G4')
        self.assertEqual(self.calls(), [])

    def test_rejected_approval_retains_history(self):
        self.approve('G1', outcome='fail')
        with self.assertRaises(lifecycle.LifecycleError):
            self.approve('G2')
        self.approve('G1')
        self.approve('G2')
        self.assertEqual([r['outcome'] for r in self.session.state['records'][:2]], ['fail', 'pass'])

    def test_changed_scope_invalidates_downstream(self):
        self.planning()
        (self.change / 'refinement.md').write_text('Changed scope\n')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G1 evidence is stale'):
            self.session.create_issues()
        self.assertEqual(len(self.session.state['records']), 4)

    def test_changed_issue_payload_invalidates_g4(self):
        self.planning()
        self.payload['issues'][0]['body'] = 'unapproved replacement'
        self.write_payload()
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G4 evidence is stale'):
            self.session.create_issues()
        self.assertEqual(self.calls(), [])

    def test_checker_identity_and_report_integrity(self):
        self.approve('G1')
        self.approve('G2')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'independent'):
            self.check('G3', actor='maker')
        self.check('G3')
        (self.change / 'evidence/G3.md').write_text('Edited checker report\n')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'report changed'):
            self.approve('G4')

    def test_three_review_lenses_required(self):
        self.implementation()
        with self.assertRaises(lifecycle.LifecycleError):
            self.check('G7')
        self.check('G7', lens='architecture')
        self.check('G7', lens='correctness')
        with self.assertRaises(lifecycle.LifecycleError):
            self.approve('G8')
        self.check('G7', lens='tests')
        self.approve('G8')

    def test_tracked_source_changes_invalidate(self):
        self.implementation()
        (self.root / 'source.txt').write_text('changed\n')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G5 evidence is stale'):
            self.session.require('G6')

    def test_staged_source_changes_invalidate(self):
        self.implementation()
        (self.root / 'source.txt').write_text('staged\n')
        self.git('add', 'source.txt')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G5 evidence is stale'):
            self.session.require('G6')

    def test_index_only_changes_invalidate(self):
        self.implementation()
        (self.root / 'source.txt').write_text('index-only\n')
        self.git('add', 'source.txt')
        (self.root / 'source.txt').write_text('original\n')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G5 evidence is stale'):
            self.session.require('G6')

    def test_untracked_source_changes_invalidate(self):
        self.implementation()
        (self.root / 'new-source.txt').write_text('new code\n')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G5 evidence is stale'):
            self.session.require('G6')

    def test_excluded_reports_do_not_invalidate(self):
        self.implementation()
        (self.change / 'evidence/additional.md').write_text('Additional evidence\n')
        (self.change / 'uat.md').write_text('UAT preparation\n')
        self.session.require('G6')

    def test_partial_creation_resume_and_reconcile(self):
        self.planning()
        with patch.dict(os.environ, {'FAKE_GH_FAIL_TITLE': 'Second'}):
            with self.assertRaisesRegex(lifecycle.LifecycleError, 'ambiguous'):
                self.session.create_issues()
        resumed = lifecycle.Session(self.root, 'demo')
        resumed.start('owner/repo', 'maker')
        self.assertEqual(resumed.state['issues'], {'first': 'https://github.com/owner/repo/issues/1'})
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'Ambiguous prior'):
            resumed.create_issues()
        self.assertEqual(len(self.calls()), 2)
        resumed.reconcile('second', 'https://github.com/owner/repo/issues/22', False, 'GitHub lookup confirmed payload')
        resumed.create_issues()
        self.assertEqual(len(self.calls()), 2)

    def test_verified_absence_allows_explicit_retry(self):
        self.planning()
        with patch.dict(os.environ, {'FAKE_GH_FAIL_TITLE': 'First'}):
            with self.assertRaises(lifecycle.LifecycleError):
                self.session.create_issues()
        with self.assertRaises(lifecycle.LifecycleError):
            self.session.reconcile('first', None, True, '')
        self.session.reconcile('first', None, True, 'GitHub search confirms absence')
        self.session.create_issues()
        self.assertEqual(len(self.calls()), 3)
        self.assertEqual(len(self.session.state['issues']), 2)

    def test_session_isolation_and_resume_identity(self):
        (self.root / 'openspec/changes/other').mkdir()
        other = lifecycle.Session(self.root, 'other')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'Deactivate'):
            other.start('owner/repo', 'maker')
        self.approve('G1')
        self.session.pointer.unlink()
        with self.assertRaises(lifecycle.LifecycleError):
            self.approve('G2')
        self.session.start('owner/repo', 'maker')
        self.session.require('G1')
        with self.assertRaises(lifecycle.LifecycleError):
            self.session.start('owner/repo', 'another-maker')
        self.session.pointer.unlink()
        other.start('owner/repo', 'maker')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'Another change'):
            self.session.active()

    def test_g5_requires_all_approved_issues(self):
        self.planning()
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'Create or reconcile'):
            self.check('G5')

    def test_cli_lock_contention_preserves_holder_and_release_allows_progress(self):
        lock = Path(self.git('rev-parse', '--git-path', 'rallyon-lifecycle.lock').decode().strip())
        lock = lock if lock.is_absolute() else self.root / lock
        holder = {'pid': os.getpid(), 'change': 'demo', 'command': 'create-issues'}
        lock.write_text(json.dumps(holder))
        command = [sys.executable, str(HELPER), '--change', 'demo', 'status']
        blocked = subprocess.run(command, cwd=self.root, text=True, capture_output=True, timeout=10)
        self.assertEqual(blocked.returncode, 1)
        self.assertIn('Another helper holds', blocked.stderr)
        self.assertEqual(json.loads(lock.read_text()), holder)
        lock.unlink()
        resumed = subprocess.run(command, cwd=self.root, text=True, capture_output=True, timeout=10)
        self.assertEqual(resumed.returncode, 0, resumed.stderr)
        self.assertEqual(json.loads(resumed.stdout)['mode'], 'full')
        self.assertFalse(lock.exists())

    def test_cli_releases_lock_after_failed_gate_check(self):
        result = subprocess.run(
            [sys.executable, str(HELPER), '--change', 'demo', 'check', 'G4', '--require'],
            cwd=self.root, text=True, capture_output=True, timeout=10,
        )
        self.assertEqual(result.returncode, 1)
        self.assertIn('pending or rejected', result.stderr)
        lock = Path(self.git('rev-parse', '--git-path', 'rallyon-lifecycle.lock').decode().strip())
        lock = lock if lock.is_absolute() else self.root / lock
        self.assertFalse(lock.exists())

    def test_completed_openspec_task_checkboxes_preserve_g4_and_allow_g5(self):
        tasks = self.change / 'tasks.md'
        tasks.write_text('# Tasks\n\n- [ ] 1.1 Implement behavior\n- [ ] 1.2 Validate behavior\n')
        self.planning()
        self.session.create_issues()
        tasks.write_text('# Tasks\n\n- [x] 1.1 Implement behavior\n- [x] 1.2 Validate behavior\n')
        self.session.require('G4')
        self.check('G5')
        self.session.require('G5')

    def test_changed_openspec_task_definition_or_order_invalidates_g4(self):
        tasks = self.change / 'tasks.md'
        original = '# Tasks\n\n- [ ] 1.1 Implement behavior\n- [ ] 1.2 Validate behavior\n'
        tasks.write_text(original)
        self.planning()
        for replacement in [
            original.replace('Implement behavior', 'Implement additional behavior'),
            '# Tasks\n\n- [ ] 1.2 Validate behavior\n- [ ] 1.1 Implement behavior\n',
        ]:
            with self.subTest(replacement=replacement):
                tasks.write_text(replacement)
                with self.assertRaisesRegex(lifecycle.LifecycleError, 'G4 evidence is stale'):
                    self.session.require('G4')
                tasks.write_text(original)
                self.session.require('G4')

    def test_successful_issue_payload_cannot_be_replaced_after_reapproval(self):
        self.planning()
        self.session.create_issues()
        self.payload['issues'][0]['body'] = 'Different accepted scope'
        self.write_payload()
        self.approve('G4')
        with self.assertRaises(lifecycle.LifecycleError):
            self.session.create_issues()
        with self.assertRaises(lifecycle.LifecycleError):
            self.check('G5')
        self.assertEqual(len(self.calls()), 2)

    def test_additional_maker_cannot_check(self):
        self.session.start('owner/repo', 'maker', additional_makers=['second-maker'])
        self.approve('G1')
        self.approve('G2')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'independent'):
            self.check('G3', actor='second-maker')

    def test_registering_checker_as_maker_invalidates_prior_check(self):
        self.planning()
        self.session.start('owner/repo', 'maker', additional_makers=['checker'])
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'checker is an implementation maker'):
            self.session.require('G4')

    def test_source_symlink_is_snapshotted_without_reading_target(self):
        outside = self.directory / 'outside.txt'
        outside.write_text('external content\n')
        link = self.root / 'source-link'
        link.symlink_to(outside)
        self.git('add', 'source-link')
        self.implementation()
        outside.write_text('external change does not change repository source\n')
        self.session.require('G6')
        link.unlink()
        link.symlink_to('source.txt')
        with self.assertRaisesRegex(lifecycle.LifecycleError, 'G5 evidence is stale'):
            self.session.require('G6')


if __name__ == '__main__':
    unittest.main()
