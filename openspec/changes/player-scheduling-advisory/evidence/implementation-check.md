# Independent implementation check

Verdict: **fail — G5 remains pending**. Checker: `/root/advisory_check`, an independent native Codex context; no implementation edits.

| Severity | Location | Finding and required action |
| --- | --- | --- |
| Medium | `PlayerSchedulingAdvisory.kt:70–75` | Recovery requires both match starts before using the actual end. For a confirmed player with first start unknown, first actual end 10:30, and another match starting 10:45 with end 11:15, a supplied 30-minute rule produces only unresolved findings and omits the known recovery concern. The recovery requirement needs the actual end and next actual/proposed start; the first start is not needed for this comparison. Determine order from sufficient supplied facts, preserve the unresolved occupied interval, and add a regression test. If requiring both starts is intentional, reconcile that extra prerequisite with the approved requirement rather than declaring completion. |
| Medium | `PlayerSchedulingAdvisoryTest.kt:40–47` | The call-without-start scenario asserts only that some player-level unresolved finding exists. The incomplete occupied interval already supplies that finding; substituting the call for recovery start could regress without failing this assertion. Assert recovery specifically remains unresolved and no recovery calculation uses the call; exercise it with a supplied rule. |

The implementation otherwise preserves internal domain ownership, uses half-open overlap checks, retains certainty/rule provenance, and introduces no adapter, persistence, public API or dependency changes. Known shared participants can generate overlap plus unresolved recovery findings. Stateless reevaluation is covered.

Reviewed proposal, design, four requirements, task list, both new Kotlin files and `evidence/maker-validation.md`. The five implementation tasks are still unchecked. Scenario traceability is now recorded in the maker report; the findings above remain gaps in completion evidence.

## Checks and limitations

- Inspected numbered source/tests and the authoritative change artifacts; the missing-first-start counterexample follows the explicit early-return branch and was not executed as a new test.
- Independently inspected `/private/tmp/rallyon-advisory-verify.log`: backend Java 25 `clean verify` succeeded, 96 tests, zero failures/errors, three skipped. PostgreSQL behavior and opt-in documentation generation remain unverified.
- Maker reports root formatting, explicit new-Markdown Prettier and pinned strict OpenSpec validation passed. These commands were not rerun by this checker.
- No lifecycle gate approval recorded. Recheck the fixes and their regression assertions before passing G5.
