"""Local simulations; these do not contact Docker, NVD or GitHub Actions."""
import argparse
from datetime import datetime, timedelta, timezone
import importlib.util
import json
import os
from pathlib import Path
import re
import sys
import tempfile
import time
import unittest
from unittest.mock import patch, Mock
from urllib.error import URLError

SCRIPTS = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPTS))
import security_scan as scan
import validate_dependency_suppressions as policy

NOW = datetime(2026, 11, 3, tzinfo=timezone.utc)


def xml(identity='<packageUrl>pkg:maven/org.example/library@1.2.3</packageUrl>', target='<cve>CVE-2020-12345</cve>', expiry='2027-01-31Z'):
    return f'''<suppressions xmlns="{policy.NS}"><suppress until="{expiry}">
<notes>Wrong product: this exact Maven library is unrelated to the matched server. Primary identity evidence: https://example.org/vendor/source</notes>
{identity}{target}</suppress></suppressions>'''


class Suppressions(unittest.TestCase):
    def check(self, text):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / 'rules.xml'
            path.write_text(text)
            return policy.validate(path, NOW)

    def test_exact_identity_specific_target(self):
        self.assertEqual(self.check(xml()), 1)
        self.assertEqual(self.check(xml(target="<cpe>cpe:/a:keycloak:keycloak:0.1.0:snapshot:</cpe>")), 1)
        self.assertEqual(self.check(xml(identity=r'<filePath regex="true">^.*/library-1\.2\.3\.jar$</filePath>')), 1)

    def test_expiry_boundaries(self):
        self.assertEqual(self.check(xml(expiry='2027-02-01Z')), 1)
        for expiry in ('', '2026-11-03Z', '2027-02-02Z', '2027-01-01', '2027-01-01T00:00:00Z'):
            with self.subTest(expiry=expiry), self.assertRaises(ValueError):
                self.check(xml(expiry=expiry))
        with self.assertRaises(ValueError):
            self.check(xml().replace(' until="2027-01-31Z"', ''))

    def test_reject_broad_incomplete_or_unsupported_rules(self):
        for bad in (xml(identity=''), xml(target=''), xml(identity='<packageUrl regex="true">.*</packageUrl>'),
                    xml(identity='<filePath regex="true">.*spring.*</filePath>'),
                    xml(identity='<filePath regex="true">^.*/library-1.2.3.jar$</filePath>'), xml(target='<cvssBelow>10</cvssBelow>'),
                    xml(target='<cpe regex="true">cpe:/a:vendor:product</cpe>'), xml(target='<cpe>cpe:/a:vendor:*</cpe>'),
                    xml().replace('https://example.org/vendor/source', 'no source')):
            with self.subTest(rule=bad), self.assertRaises(ValueError):
                self.check(bad)

    def test_cpe_targets_match_dc13_converted_uri_at_field_boundaries(self):
        accepted = 'cpe:/a:prometheus:prometheus:1.17.2:'
        self.assertEqual(self.check(xml(target=f'<cpe>{accepted}</cpe>')), 1)
        for unsupported in ('cpe:2.3:a:prometheus:prometheus:1.17.2:*:*:*:*:*:*:*',
                            'cpe:/a:prometheus:prometheus',
                            'cpe:/a:prometheus:prometheus:1.17.2',
                            'cpe:/a:prometheus:prometheus:*:'):
            with self.subTest(target=unsupported), self.assertRaises(ValueError):
                self.check(xml(target=f'<cpe>{unsupported}</cpe>'))
        # Mirrors DC13 cpe22UriPrefixMatches after identifier.toCpe22Uri().
        self.assertTrue(('cpe:/a:prometheus:prometheus:1.17.2' + ':').startswith(accepted))
        for unrelated in ('cpe:/a:prometheus:prometheus:1.17.20',
                          'cpe:/a:prometheus:prometheus:1.17.3',
                          'cpe:/a:prometheus:client_java:1.17.2'):
            self.assertFalse((unrelated + ':').startswith(accepted))

    def test_exact_filepath_counterexamples(self):
        selector = r'^.*/library-1\.2\.3\.jar$'
        self.assertIsNotNone(re.fullmatch(selector, '/input/library-1.2.3.jar'))
        for other in ('/input/library-1.2.4.jar', '/input/other-library-1.2.3.jar', '/input/library-1.2.3.jar/other'):
            self.assertIsNone(re.fullmatch(selector, other))


class NvdMetadata(unittest.TestCase):
    def test_exact_freshness_boundaries_and_raw_hash(self):
        for modified in (NOW - timedelta(hours=48), NOW + timedelta(minutes=5), NOW):
            raw = f"lastModifiedDate:{modified.isoformat()}\nsize:123\n".encode()
            fetch = Mock(return_value=raw)
            evidence = scan.nvd_metadata(fetch=fetch, now=NOW)
            self.assertEqual(evidence['last_modified_utc'], modified.isoformat())
            self.assertEqual(evidence['sha256'], scan.hashlib.sha256(raw).hexdigest())
            fetch.assert_called_once_with(scan.NVD_METADATA, timeout=20)

    def test_reject_outside_boundaries_missing_duplicate_invalid_and_naive(self):
        samples = [b'size:123\n', b'lastModifiedDate:no-date\n', b'lastModifiedDate:2026-11-03T00:00:00\n',
                   b'lastModifiedDate:2026-11-03T00:00:00Z\nlastModifiedDate:2026-11-03T00:00:00Z\n', b'\xff',
                   f"lastModifiedDate:{(NOW-timedelta(hours=48,microseconds=1)).isoformat()}\n".encode(),
                   f"lastModifiedDate:{(NOW+timedelta(minutes=5,microseconds=1)).isoformat()}\n".encode()]
        for raw in samples:
            with self.subTest(raw=raw), self.assertRaises(ValueError):
                scan.nvd_metadata(fetch=Mock(return_value=raw), now=NOW)

    def test_retries_three_bounded_attempts_without_fallback(self):
        for error in (TimeoutError('timeout'), URLError('unavailable'), OSError('network')):
            fetch = Mock(side_effect=error)
            with self.subTest(error=error), self.assertRaises(OSError):
                scan.nvd_metadata(fetch=fetch, now=NOW)
            self.assertEqual(fetch.call_count, 3)
            for call in fetch.call_args_list:
                self.assertEqual(call.args, (scan.NVD_METADATA,))
                self.assertEqual(call.kwargs, {'timeout': 20})
        raw = f"lastModifiedDate:{NOW.isoformat()}\n".encode()
        fetch = Mock(side_effect=[TimeoutError(), OSError(), raw])
        self.assertEqual(scan.nvd_metadata(fetch=fetch, now=NOW)['sha256'], scan.hashlib.sha256(raw).hexdigest())
        self.assertEqual(fetch.call_count, 3)

    def test_wall_clock_deadline_also_bounds_response_download(self):
        class SlowResponse:
            def __enter__(self):
                return self
            def __exit__(self, *args):
                return False
            def read(self):
                time.sleep(0.5)
                return b'late'
        with patch.object(scan, 'urlopen', return_value=SlowResponse()):
            started = time.monotonic()
            with self.assertRaises(TimeoutError):
                scan.retrieve_metadata(scan.NVD_METADATA, timeout=0.02)
            self.assertLess(time.monotonic() - started, 0.4)


class Runner(unittest.TestCase):
    def setUp(self):
        metadata = f"lastModifiedDate:{datetime.now(timezone.utc).isoformat()}\n".encode()
        self.fetch_metadata = patch.object(scan, 'retrieve_metadata', return_value=metadata).start()
        self.addCleanup(patch.stopall)
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.dependencies = self.root / 'dependencies'
        jar = self.dependencies / 'org/example/library/1.2.3/library-1.2.3.jar'
        jar.parent.mkdir(parents=True)
        jar.write_bytes(b'JAR including embedded JS retained unchanged')
        self.rules = self.root / 'rules.xml'
        self.rules.write_text(f'<suppressions xmlns="{policy.NS}"/>')
        self.args = argparse.Namespace(dependencies=self.dependencies, data=self.root / 'data', reports=self.root / 'reports',
                                       suppressions=self.rules, revision='abc123', manifest=None, docker='docker',
                                       event='pull_request', ref='refs/pull/1/merge', default_branch='main')

    def run_fake(self, code, key='', leakage=False):
        self.args.data.mkdir(exist_ok=True)
        self.args.reports.mkdir(exist_ok=True)
        executable = self.root / 'fake-docker'
        # A real child process verifies the command, mount, environment and cleanup boundary.
        executable.write_text('''#!/usr/bin/env python3
import json, os, pathlib, sys
arguments = sys.argv[1:]
assert 'NVD_API_KEY' not in os.environ
assert 'JAVA_HOME' not in os.environ
pathlib.Path(os.environ['FAKE_CAPTURE']).write_text(json.dumps(arguments))
mounts = [arguments[i+1] for i,v in enumerate(arguments) if v == '--mount']
for mount in mounts:
    if 'dst=/nvd.properties' in mount:
        secret = pathlib.Path(mount.split('src=')[1].split(',')[0]).read_text().split('=',1)[1].strip()
        print(secret)
        if os.environ.get('FAKE_LEAK') == '1':
            pathlib.Path(os.environ['FAKE_DATA'], 'leak').write_text(secret)
pathlib.Path(os.environ['FAKE_REPORTS'], 'dependency-check-report.json').write_text('{}')
print('active update simulation')
sys.exit(int(os.environ['FAKE_EXIT']))
''')
        executable.chmod(0o700)
        self.args.docker = str(executable)
        capture = self.root / 'capture.json'
        with patch.dict(os.environ, {'NVD_API_KEY': key, 'JAVA_HOME': '/host-java', 'FAKE_CAPTURE': str(capture),
                                    'FAKE_REPORTS': str(self.args.reports), 'FAKE_DATA': str(self.args.data),
                                    'FAKE_LEAK': str(int(leakage)), 'FAKE_EXIT': str(code)}):
            result = scan.run_scan(self.args)
        return result, json.loads(capture.read_text())

    def test_success_and_blocking_feed_runtime_failures_propagate(self):
        for code in (0, 1, 13, 14, 127):
            with self.subTest(exit=code):
                result, command = self.run_fake(code)
                self.assertEqual(result, code)
                self.assertIn(scan.IMAGE, command)
                self.assertEqual(command[command.index('--failOnCVSS') + 1], '7')
                self.assertNotIn('--noupdate', command)
                self.assertIn('--nvdDatafeed', command)
                self.assertFalse(any(argument.startswith('--disable') for argument in command))
                self.assertTrue(json.loads((self.args.reports / 'execution.json').read_text())['reports_available'])

    def test_optional_key_cleanup_and_redaction_success_and_failure(self):
        self.args.event, self.args.ref = 'push', 'refs/heads/main'
        for code in (0, 14):
            result, command = self.run_fake(code, 'fixture-secret-123')
            self.assertEqual(result, code)
            mounts = [value for value in command if 'dst=/nvd.properties' in value]
            self.assertEqual(len(mounts), 1)
            self.assertFalse(Path(mounts[0].split('src=')[1].split(',')[0]).exists())
            for path in self.root.rglob('*'):
                if path.is_file() and path.name != 'capture.json':
                    self.assertNotIn(b'fixture-secret-123', path.read_bytes())

    def test_retained_credential_fails_closed(self):
        self.args.event, self.args.ref = 'schedule', 'refs/heads/main'
        result, _ = self.run_fake(0, 'fixture-secret-123', leakage=True)
        self.assertEqual(result, 1)
        self.assertFalse((self.args.data / 'leak').exists())

    def test_pr_does_not_receive_optional_key(self):
        _, command = self.run_fake(0, 'fixture-secret-123')
        self.assertNotIn('--propertyfile', command)
        self.assertNotIn('fixture-secret-123', ' '.join(command))

    def test_missing_runtime_is_failure_and_report_unavailable(self):
        self.args.docker = str(self.root / 'missing-docker')
        self.assertEqual(scan.run_scan(self.args), 1)
        self.assertFalse(json.loads((self.args.reports / 'execution.json').read_text())['reports_available'])

    def test_prepared_inputs_and_bom_fail_closed(self):
        manifest = scan.inventory(self.dependencies, 'abc123')
        self.assertEqual(scan.compare_bom(manifest, {'components': [{'purl': manifest['dependencies'][0]['purl']} ]})['runtime_coordinates_compared'], 1)
        with self.assertRaises(ValueError):
            scan.compare_bom(manifest, {'components': []})
        manifest['revision'] = 'other-revision'
        self.args.manifest = self.root / 'inventory.json'
        self.args.manifest.write_text(json.dumps(manifest))
        self.assertEqual(scan.run_scan(self.args), 1)

    def test_secret_detection_across_chunk_boundary(self):
        path = self.root / 'large-data'
        path.write_bytes(b'a' * (1024 * 1024 - 3) + b'secret' + b'z' * 32)
        self.assertTrue(scan.contains_secret(path, b'secret'))
        self.assertFalse(scan.contains_secret(path, b'other-secret'))

    def test_metadata_error_blocks_docker_even_with_warm_data(self):
        self.args.data.mkdir()
        (self.args.data / 'cached-data').write_bytes(b'previous snapshot')
        for failure in (TimeoutError('network deadline'),):
            self.fetch_metadata.side_effect = failure
            with patch.object(scan.subprocess, 'run') as docker:
                self.assertEqual(scan.run_scan(self.args), 1)
                docker.assert_not_called()
            self.assertEqual(self.fetch_metadata.call_count, 3)
            self.fetch_metadata.reset_mock()
        for raw in (b'lastModifiedDate:bad', b'lastModifiedDate:2000-01-01T00:00:00Z'):
            self.fetch_metadata.side_effect = None
            self.fetch_metadata.return_value = raw
            with patch.object(scan.subprocess, 'run') as docker:
                self.assertEqual(scan.run_scan(self.args), 1)
                docker.assert_not_called()
            evidence = json.loads((self.args.reports / 'execution.json').read_text())
            self.assertEqual(evidence['nvd_mode'], 'nist-json2-feed')
            self.assertIn('error', evidence)
            self.assertEqual(evidence['nvd_metadata']['sha256'], scan.hashlib.sha256(raw).hexdigest())
            if b'2000-' in raw:
                self.assertEqual(evidence['nvd_metadata']['last_modified_utc'], '2000-01-01T00:00:00+00:00')

    def test_trusted_optional_key_selects_api_without_metadata(self):
        self.args.event, self.args.ref = 'push', 'refs/heads/main'
        self.fetch_metadata.side_effect = AssertionError('API mode must not fetch feed metadata')
        for code in (0, 14):
            result, command = self.run_fake(code, 'fixture-secret-123')
            self.assertEqual(result, code)
            self.assertNotIn('--nvdDatafeed', command)
            self.assertIn('--propertyfile', command)
            evidence = json.loads((self.args.reports / 'execution.json').read_text())
            self.assertEqual(evidence['nvd_mode'], 'api')
            self.assertNotIn('nvd_metadata', evidence)
        self.fetch_metadata.assert_not_called()

    def test_keyless_feed_mode_evidence_on_scan_failure(self):
        self.args.event, self.args.ref = 'push', 'refs/heads/main'
        code, command = self.run_fake(14)
        self.assertEqual(code, 14)
        self.assertEqual(command[command.index('--nvdDatafeed') + 1], scan.NVD_DATAFEED)
        evidence = json.loads((self.args.reports / 'execution.json').read_text())
        self.assertEqual(evidence['nvd_mode'], 'nist-json2-feed')
        self.assertEqual(evidence['nvd_metadata']['sha256'], scan.hashlib.sha256(self.fetch_metadata.return_value).hexdigest())
        self.assertEqual(evidence['nvd_metadata']['url'], scan.NVD_METADATA)
        self.fetch_metadata.assert_called_once()

    def test_overlapping_writable_and_immutable_paths_fail_before_writes(self):
        original = self.args
        pairs = [('data', original.dependencies), ('reports', original.dependencies / 'nested'),
                 ('data', original.dependencies.parent), ('reports', original.data),
                 ('reports', original.suppressions.parent)]
        for name, value in pairs:
            with self.subTest(name=name, value=value):
                args = argparse.Namespace(**vars(original))
                setattr(args, name, value)
                with patch.object(scan.subprocess, 'run') as docker, patch.object(scan, 'atomic_write') as write:
                    self.assertEqual(scan.run_scan(args), 1)
                    docker.assert_not_called()
                    write.assert_not_called()
        args = argparse.Namespace(**vars(original))
        args.manifest = args.data / 'inventory.json'
        self.assertEqual(scan.run_scan(args), 1)

    def test_atomic_writes_replace_leaf_symlinks_without_following_them(self):
        unrelated = self.root / 'unrelated.txt'
        unrelated.write_text('preserve me')
        for name in ('execution.json', 'input-inventory.json', 'scanner.log', 'inventory.json'):
            path = self.root / name
            path.symlink_to(unrelated)
            scan.atomic_write(path, b'new artifact')
            self.assertEqual(path.read_bytes(), b'new artifact')
            self.assertEqual(unrelated.read_text(), 'preserve me')
            self.assertFalse(path.is_symlink())
        self.assertEqual(list(self.root.glob('.security-*')), [])

    def test_execution_leaf_symlink_preserves_unrelated_target(self):
        self.args.reports.mkdir()
        unrelated = self.root / 'unrelated.txt'
        unrelated.write_text('preserve me')
        (self.args.reports / 'execution.json').symlink_to(unrelated)
        self.run_fake(0)
        self.assertEqual(unrelated.read_text(), 'preserve me')
        self.assertFalse((self.args.reports / 'execution.json').is_symlink())

    def test_malformed_xml_is_diagnostic_failure(self):
        self.rules.write_text('<not-xml')
        self.assertEqual(scan.run_scan(self.args), 1)
        evidence = json.loads((self.args.reports / 'execution.json').read_text())
        self.assertIn('error', evidence)
        self.assertFalse(evidence['reports_available'])

    def test_trust_event_matrix(self):
        for event in ('pull_request', 'pull_request_target', 'workflow_dispatch', 'push', 'schedule'):
            for ref in ('refs/heads/main', 'refs/heads/feature', 'refs/pull/1/merge'):
                self.assertEqual(scan.trusted(event, ref, 'main'), event in {'push', 'schedule'} and ref == 'refs/heads/main')
        self.assertFalse(scan.trusted('push', 'refs/heads/', ''))


class Workflow(unittest.TestCase):
    def test_graph_permissions_pins_trust_and_always_artifacts(self):
        workflow = (SCRIPTS.parents[1] / '.github/workflows/security-scan.yaml').read_text()
        self.assertIn('contents: read', workflow)
        self.assertNotIn('continue-on-error', workflow)
        for action in re.findall(r'uses: (\S+)', workflow):
            self.assertRegex(action, r'@[a-f0-9]{40}$')
        scan_job = workflow.split('\n  dependency-scan:')[1].split('\n  sbom:')[0]
        bom_job = workflow.split('\n  sbom:')[1]
        for job in (scan_job, bom_job):
            self.assertIn('needs: prepare', job)
            self.assertIn('if: always()', job)
            self.assertIn('ref: ${{ github.sha }}', job)
            self.assertIn('persist-credentials: false', job)
        self.assertNotIn('needs: dependency-scan', bom_job)
        self.assertIn('cyclonedx-maven-plugin:2.9.1', bom_job)
        self.assertIn("github.event_name == 'schedule'", scan_job)
        self.assertIn("github.event_name == 'push'", scan_job)
        self.assertIn('github.event.repository.default_branch', scan_job)
        self.assertIn('actions/cache/restore@', scan_job)
        self.assertIn('actions/cache/save@', scan_job)


if __name__ == '__main__':
    unittest.main()
