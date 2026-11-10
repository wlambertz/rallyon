# G3 bootstrap value recheck

**Verdict: PASS for the corrected value assessment**, with a planning-document inconsistency for follow-up. Checker: independent Codex `/root/bootstrap_g3_delta`. This is a delta recheck of [the previous challenge](g3-bootstrap.md); it did not rerun the underlying validation.

| Severity | Finding | Action |
| --- | --- | --- |
| Medium | `design.md` still describes the service and IAM targets and dev container as currently using Java 21, although `value.md` correctly identifies the Java 25 candidate. Its validation section also describes the earlier host environment without the later successful container and CI checks. `refinement.md` describes initial observations in present tense. These are stale planning context, not a numerical value correction. | Reconcile the historical versus current wording using the lifecycle's staleness rules before implementation or downstream certification; retain the uncertainty about the proposed bootstrap changes. |
| Resolved | `value.md` now separates the initial Java 21 baseline from PR head `bb3690b`, records successful Flyway, dev-container and hosted CI checks, and limits those checks to the tested head. It identifies the proposed bootstrap repairs and existing real databases as not independently validated. | No further value-evidence correction identified. |
| Informational | User value 2, learning value 4, risk reduction 3, confidence 0.75 and effort 5 remain reasonable provisional estimates for the expanded scope. `(0.5 × 2 + 0.3 × 4 + 0.2 × 3) × 0.75 / 5 = 0.42`. | Reassess if the migration or rollout scope grows materially; the score is advisory. |

Commands: read the prior report, scoring guidance, current planning documents and recorded evidence with `cat`. No tests, hosted CI, real database migration or user-impact measurement were independently rerun.
