# Independent correctness review: NVD revision, round 1

Reviewer: Codex native independent agent `/root/ci187_review_correctness`, fresh context, not an implementation maker. Reviewed source `349c0bd23aae64f0bfbae7fee6793378bd6f4ebc` against `main` on 2026-11-03. Only this review report was authored by this reviewer.

**FAIL: two blocking P2 correctness findings.** This is a source-review result, not a formal G7 gate record; G5/G6 completion and actual cold/warm/GitHub evidence remain prerequisites. No scanner execution pass is claimed.

## Findings

### C1: Previous report can satisfy the current missing-report guard

`tools/scripts/security_scan.py:145,196-199` reuses the reports directory and accepts any existing `dependency-check-report.json` as the current result. The documented local cold/warm reproduction uses the same reports directory. A prior report survives even if the next invocation never produces one, and may also be retained beside diagnostics for an early metadata failure. With an exit-zero scanner that produces no report, the wrapper returns zero and records the new revision with `reports_available: true`, while the report still identifies the old revision. This defeats the explicit missing-report safeguard in SCA-003 and mixes execution evidence under SCA-002/SCA-007. Fresh CI directories reduce exposure but do not correct supported local behavior.

Reproduction below produced `C1 0 True old-revision`. Expected: nonzero and no old engine report presented as current. Use a fresh per-invocation engine-output directory, or safely reject/remove prior engine outputs before a scan starts. Preserve historical evidence separately. Add both stale-report plus zero/no-output and stale-report plus metadata-failure regressions.

### C2: Post-scan validation errors preserve an earlier zero exit status

`tools/scripts/security_scan.py:187,188-203` assigns the scanner's success before retained-file credential inspection. If that inspection raises a caught `OSError` or `ValueError`, the handler records an error without replacing zero. A failure to read a retained file therefore permits success even though credential non-disclosure could not be verified. This violates SCA-003/SCA-005's fail-closed behavior.

The reproduction below produced `C2 0 cannot inspect retained file`, and execution evidence had `exit_code: 0`, `reports_available: false` and the error. Expected: nonzero. Set a failing status in the exception path, retaining the diagnostic and temporary-key cleanup, and add a regression for errors after scanner success.

## Reproduction

Run from the repository root; this uses temporary files and mocks, without Docker, NVD, real credentials or repository edits:

```sh
python3 - <<'PY'
import argparse, json, os, pathlib, subprocess, sys, tempfile
from datetime import datetime, timezone
from unittest.mock import patch
sys.path.insert(0, 'tools/scripts')
import security_scan as scan
with tempfile.TemporaryDirectory() as directory:
    root = pathlib.Path(directory)
    dependencies = root / 'inputs'
    jar = dependencies / 'org/example/a/1.0/a-1.0.jar'
    jar.parent.mkdir(parents=True)
    jar.write_bytes(b'jar')
    rules = root / 'rules.xml'
    rules.write_text('<suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd"/>')
    reports = root / 'reports'
    reports.mkdir()
    report = reports / 'dependency-check-report.json'
    report.write_text('{"revision":"old-revision"}')
    args = argparse.Namespace(dependencies=dependencies, data=root / 'data',
        reports=reports, suppressions=rules, revision='new-revision',
        manifest=None, docker='docker', event='local', ref='', default_branch='main')
    metadata = f'lastModifiedDate:{datetime.now(timezone.utc).isoformat()}\n'.encode()
    with patch.object(scan.subprocess, 'run', return_value=subprocess.CompletedProcess([], 0, b'no report generated')):
        result = scan.run_scan(args, fetch_metadata=lambda *a, **kw: metadata)
    evidence = json.loads((reports / 'execution.json').read_text())
    print('C1', result, evidence['reports_available'], json.loads(report.read_text())['revision'])
    args.event, args.ref = 'push', 'refs/heads/main'
    with patch.dict(os.environ, {'NVD_API_KEY': 'fake-review-key'}), \
         patch.object(scan.subprocess, 'run', return_value=subprocess.CompletedProcess([], 0, b'ok')), \
         patch.object(scan, 'contains_secret', side_effect=OSError('cannot inspect retained file')):
        result = scan.run_scan(args)
    evidence = json.loads((reports / 'execution.json').read_text())
    print('C2', result, evidence['error'])
PY
```

## Passing checks and source assessment

- Read root AGENTS.md, lifecycle skill/review instructions, approved design and delta specification, complete changed implementation/workflow/test files, dependency dispositions, baseline and supporting evidence. Reviewed the `main...HEAD` change inventory; historical and pending evidence was not promoted to current successful execution.
- `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v`: 25 tests passed independently. These are local simulations.
- Suppression validator: seven rules passed. Independently ran the exact baseline multiset comparison and the existing external counterexample probe: 82/82 instances, 13 entries, 64 distinct IDs, 48 selector assertions. No baseline instance was omitted. Dispositions explicitly retain pending actual scanner clearance.
- Metadata logic enforces one timezone-aware date, exact 48-hour/five-minute boundaries, three network attempts, raw-byte SHA-256 and POSIX wall-clock deadlines. Metadata errors block Docker even with restored data; feed/API selection is fixed before invocation. Existing negative fixtures passed.
- Workflow has independent prepared-input scan/SBOM siblings, no ignored scanner failure, always-run artifact retention, restricted default-branch key/cache writes and unchanged threshold 7. Container input mounts are read-only; full workspace and Docker socket are absent. Atomic owned writes and overlap checks cover the specified accidental filesystem-corruption cases.
- Independently opened [DC13 NvdApiDataSource](https://raw.githubusercontent.com/dependency-check/DependencyCheck/v13.0.0/core/src/main/java/org/owasp/dependencycheck/data/update/NvdApiDataSource.java): a configured feed selects the feed path before API operation; the exact official NIST URL pattern and yearly/modified metadata fallback are supported. Normal engine update windows remain active. Source compatibility does not prove successful import or completion within 60 minutes.
- Independently opened [DC13 SuppressionRule](https://raw.githubusercontent.com/dependency-check/DependencyCheck/v13.0.0/core/src/main/java/org/owasp/dependencycheck/xml/suppression/SuppressionRule.java): artifact selection precedes target removal; literal CPE matching converts to 2.2 URI and terminal-colon matching respects part boundaries. Current exact version/product rules match these semantics; unrelated artifacts/versions/products remain outside their selectors. Actual matched/unused-rule output still requires a completed scan.

## Limitations and required refresh

The actual cold scan was still running during review. An earlier local attempt recorded a Python trust-store failure and no reports; the system-trust retry had fresh official metadata but no final outcome yet. No warm success or current GitHub scan/report observation was available to this reviewer. IAM/service/Go suites were not rerun by this correctness lens; independent G6 must establish those checks. This review does not certify every vendor advisory afresh; it confirms baseline disposition coverage and reviews suppression identity/engine semantics. Resolve C1/C2, rerun affected fixtures, then request an independent bounded source refresh. Record actual cold/warm findings, suppression behavior and current GitHub sibling SBOM results before acceptance.
