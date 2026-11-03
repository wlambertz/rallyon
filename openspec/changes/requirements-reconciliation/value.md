# Value assessment: requirements reconciliation

## Persona, context and evidence

The actual work is the analysis and planning approved at G1, not implementation of group standings. Its direct beneficiary is the maintainer deciding which Organizer needs to pursue in Tournament Management; players and spectators benefit only indirectly if later work is selected and delivered. The analysis has identified differences between the supplied reference and specific DBV provisions and an incomplete ordered-rule representation in `TieBreakRules`. No organizer study or measured improvement establishes the practical benefit.

## Scores and calculation

Using `docs/value-scoring.md`, provisional ratings for completing the analysis are user value 2/5 (aids an occasional maintainer decision; no delivered organizer workflow), learning 3/5 (source comparison and applicability provide a useful research method), risk reduction 2/5 (identifies discrepancies before adoption, without a demonstrated prevented production error), confidence 0.5 (no independently established completeness or user benefit), and effort 5 (substantial inventory, source checks, overlap reconciliation, candidate specifications, independent review and issue preparation; no implementation). The advisory score is `(0.5 × 2 + 0.3 × 3 + 0.2 × 2) × 0.5 / 5 = 0.23`.

This is not a priority threshold or implementation commitment. The archived retrieval pilot involved implementation and UAT, so its score is not reliably comparable with this research task. No equivalent completed analysis has been identified for calibration.

## Assumptions and uncertainty

No numerical budget was agreed. Completeness, source access, regional differences and deduplication with #184–#186 could change effort. The group-standings specification remains a candidate requiring domain refinement, not implementation readiness. A future implementation's value, cost and architecture require a separate assessment.

The earlier assessment scored a hypothetical future implementation at 0.20625, with effort 8. The independent checker found that this mismatched the approved analysis scope; see `evidence/g3-value-check.md`. It must not be used as the score for this analysis.

## G2 go/no-go

Pending renewed explicit human approval because the value framing and estimate changed materially after the independent challenge. Approval covers analysis and planning only.

## G3 independent challenge

The previous independent challenge failed on the scope mismatch. A fresh independent checker must assess this revised evidence and calculation after renewed G2 approval.
