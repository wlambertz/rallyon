# Independent tests review: bounded round 2

Reviewer: native Codex agent `/root/ci187_review_tests`, independent of implementation. Date: 2026-11-03. This preserves [round 1](review-tests-nvd.md) and rechecks only its stale-report finding and the separately reported post-scan verification exception finding. Reviewed working-tree changes over HEAD `349c0bd23aae64f0bfbae7fee6793378bd6f4ebc`; this is not a frozen-source formal G7 approval.

Reviewed file SHA-256 identities:

- `tools/scripts/security_scan.py`: `01b168ca40e093b85f293cbbebd6217d787f34ed0e8709e057f98f158d912cbc`
- `tools/scripts/tests/test_security_pipeline.py`: `317dffb8a8909d52d700114a8e707647a588bf23cc89e4f34edfe883373bd2f1`

Outcome: **both bounded findings resolved**. The protected scan block now removes old engine JSON/HTML before invocation, and its handled exception path explicitly restores a failing exit code. No additional blocking defect found in these changes.

I independently ran all **27 security tests**, which passed in 1.012 seconds. The two added regressions prove that old engine reports cannot satisfy a new successful process with no report, and that an I/O exception during post-success retained-secret inspection returns nonzero with diagnostic evidence. I also inspected the maker's red-run log: these tests failed against the old source, so the new assertions distinguish the corrected behavior.

Additional independent counterexamples used a real temporary executable that writes no report:

| Scenario | Observed result |
| --- | --- |
| Fresh report directory, process exit 0 | Runner exit 1; reports unavailable |
| Reused JSON/HTML, process exit 0 | Runner exit 1; old engine reports removed; reports unavailable |
| Reused JSON/HTML, process exit 14 | Runner exit 14; old engine reports removed; reports unavailable |
| Directory occupying engine JSON filename | Cleanup fails before subprocess invocation; runner exit 1 with diagnostics |

`git diff --check` passed. No source edits made by this reviewer.

This bounded pass resolves the two implementation findings only. Full Maven/Go and broader runtime checks were not rerun here. Round 1's current-source G5/G6, real cold/warm scanner/report, and actual GitHub job/artifact obligations remain. The running cold scan originated at `349c0bd`; no completed success, refreshed-source scan, formal G7, UAT or merge approval is claimed.
