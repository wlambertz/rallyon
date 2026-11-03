# Independent correctness review: NVD revision, round 2

Reviewer: Codex native independent agent `/root/ci187_review_correctness`, separate from implementation makers. Reviewed on 2026-11-03. This is a bounded refresh of [round 1](review-correctness-nvd.md), which remains preserved.

**PASS for the bounded C1/C2 source correction.** Both previously blocking P2 findings are resolved; no new blocking source defect was identified in the changed code. This does not constitute a formal G7 record, successful real scanner execution, G8 acceptance or merge approval. G5/G6 and real cold/warm/GitHub execution evidence remain required.

## Reviewed snapshot

The requested changes were initially inspected as the working-tree delta over `349c0bd23aae64f0bfbae7fee6793378bd6f4ebc`. At the end of independent reproduction HEAD was `9bbeb3af41814b03d381c7efa3f8c4d6165d54a8`. The exact reviewed bytes are:

| File                                            | SHA-256                                                            |
| ----------------------------------------------- | ------------------------------------------------------------------ |
| `tools/scripts/security_scan.py`                | `01b168ca40e093b85f293cbbebd6217d787f34ed0e8709e057f98f158d912cbc` |
| `tools/scripts/tests/test_security_pipeline.py` | `317dffb8a8909d52d700114a8e707647a588bf23cc89e4f34edfe883373bd2f1` |

This reviewer authored only this new report during the refresh. No implementation or test edits were made.

## Independent verification

Read the complete changed hunks and surrounding scanner exception/output handling. The runner now unlinks its two known prior engine report paths inside the protected failure-handling block before suppression, input or metadata validation, and every caught execution/validation exception explicitly sets nonzero status. Cleanup unlinks a leaf symlink rather than following its target. Failure to remove an old report fails closed. Successful current reports and normal nonzero scanner exit propagation retain their existing handling.

Recreated both original findings with fresh temporary directories and separate direct mocks, rather than relying only on maker logs:

- **C1, scanner exits zero without writing a report:** seeded old JSON/HTML, invoked the current runner with fresh metadata and a zero/no-output scanner. Observed exit 1, `reports_available: false`, missing-current-report diagnostic and both old engine reports removed.
- **C1, early metadata failure:** seeded old JSON/HTML, supplied stale metadata and asserted Docker was never invoked. Observed exit 1, `reports_available: false` and both old reports removed. Thus early failure does not relabel old engine reports as current.
- **C2, post-success credential verification failure:** trusted fake-key invocation, scanner exits zero, retained-file inspection raises `OSError('cannot inspect retained file')`. Observed exit 1 and matching error in final execution evidence. Temporary credential cleanup remains scoped by the existing context manager.

`python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v` independently passed all **27 tests**, including the two new regressions and the existing failure, metadata, credential, trust, path and policy cases. `git diff --check` passed. These checks use simulations; no real key, NVD request or Docker invocation was made by this reviewer.

The original feed/API compatibility, exact CPE matcher assessment and 82/82 baseline disposition coverage in round 1 were unaffected by this four-line runner correction. This bounded refresh does not replace their stated limitations.

## Remaining evidence limits

The real cold execution of source `349c0bd` was ongoing when this refresh was requested. Its eventual result cannot by itself establish execution of the corrected snapshot above. No successful current-source cold/warm scan or actual current GitHub scanner/SBOM outcome was presented or certified here. Runtime verification and formal gate ordering remain the independent G5/G6/coordinator responsibilities. Only the two source findings are closed by this report.
