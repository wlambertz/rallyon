# G3 independent value challenge

Verdict: **fail; return to G2 after reassessing value.** The stated user and risk scores attribute practical benefit to an increment with no caller or organizer workflow. The numerical calculation is correct, but the scores do not follow the documented anchors for the work being approved.

| Severity | Location | Finding and suggested correction |
| --- | --- | --- |
| Material | `value.md`, user value | A score of 2 describes a minor improvement to an occasional user task. The proposal explicitly provides no organizer interface or integration, so this increment cannot yet improve that task. Score present user value separately from hypothetical future integrated value; 0 is a defensible current score. |
| Material | `value.md`, risk reduction | A score of 2 describes a recurring defect risk. No caller or existing scheduling behavior is identified, and the assessment cannot yet protect an operational schedule. Score its limited internal maintenance risk separately from future integrated protection; 1 is a defensible estimate. |
| Advisory | `value.md`, learning and effort | Learning 4 assumes reusable insight validated in practice; reuse and integration remain untested. A provisional 3 is reasonable unless the higher score is justified as a hypothesis with a validation plan. Effort 3 is plausible for the proposed narrow implementation, tests, docs and review, but cannot estimate excluded integration; test actual effort at G5. |
| Advisory | `spec.md`, overlap | The requirement says known occupied intervals produce an overlap, while proposed and estimated time facts also exist. Specify whether an overlap involving provisional times is a potential planned conflict rather than a confirmed actual overlap before applying; preserve time certainty in results. |

The recorded arithmetic reproduces: `(1 + 1.2 + 0.4) × 0.5 / 3 = 0.4333`. An illustrative reassessment using user 0, learning 3, risk 1, confidence 0.5 and effort 3 gives `(0 + 0.9 + 0.2) × 0.5 / 3 = 0.1833`. These are advisory estimates, not a competing priority threshold. The maintainer can still choose this foundational increment, but the materially different value assessment needs a renewed human go/no-go.

## Evidence and limits

Inspected `ParticipantsRoster.kt` and `SchedulingPolicy.kt`: the former contains player or team IDs, while the latter lists policy values. `planning` and `administration` contain only package markers under `src/main`. The #228 findings identify event-specific authority and unresolved applicability; they do not establish a live data source or user workflow. Confidence 0.5 appropriately reflects unvalidated integration and persona outcomes. No production files were changed for this review.

Checker: `/root/value_check`, 2026-11-11. This is a value and readiness challenge, not implementation or legal review.
