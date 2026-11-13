# Test and maintainability review

Verdict: **pass**, with nonblocking regression improvements below. Reviewer: `/root/review_tests`, fresh native Codex context; no implementation edits.

| Severity | Location | Finding |
| --- | --- | --- |
| Low | `PlayerSchedulingAdvisoryTest.kt` | Explicit assertions for affected match IDs and full overlap time values would protect the explanation contract better than checking only player/kind/time count. Add these when extending the advisory. |
| Low | `PlayerSchedulingAdvisoryTest.kt` | Estimated overlap/start, disjoint identifiers with incomplete membership, and invalid rule provenance are handled in inspected source but lack dedicated regression assertions. These are useful additions; no approved scenario is missing coverage. |

The seven tests exercise observable findings, half-open adjacency, exact recovery equality, proposed-time provenance, absent rule/membership/time facts, delayed-end correction, invalid intervals and duplicate IDs. The G5 regressions now prove actual-end recovery without an earlier start in both input orders and specifically prevent call-as-start recovery. Fixtures use fixed instants and an explicit supplied rule, so results do not depend on clock, network, database or an assumed default. Stateless reevaluation is tested by reassessing the original facts after a changed snapshot.

The implementation is small and confined to one internal domain file. Test helpers keep scenarios readable; no new dependency, runtime fixture or generated artifact increases maintenance cost. Reason-string assertions couple two tests to diagnostic wording, but they currently distinguish the recovery uncertainty from the separate interval uncertainty and are proportionate for this internal contract.

## Reviewed snapshot and checks

- Reviewed the current untracked `PlayerSchedulingAdvisory.kt` and `PlayerSchedulingAdvisoryTest.kt`, the approved spec/design/tasks, maker validation and independent QA report after the missing-start/call-time fixes.
- Independently inspected `/private/tmp/rallyon-advisory-verify.log`: Java 25 full `clean verify` passed, 98 tests, zero failures/errors, three skipped; all seven advisory tests passed. Runtime tests were not redundantly rerun for this read-only review.
- Two PostgreSQL integration tests and opt-in documentation generation remain skipped. Pure domain tests and H2 checks do not establish production PostgreSQL behavior or organizer workflow usability. Those limits are accurately documented; no persistence or UI behavior is introduced here.
- No helper gate result recorded. Human UAT and merge remain separate decisions.
