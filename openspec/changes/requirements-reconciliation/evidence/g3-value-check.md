# Independent G3 value challenge

- Checker: Codex native agent `/root/value_check`, separate from maker `codex`.
- Outcome: **fail — material mismatch between the approved work and the work being scored**.
- Inputs: `refinement.md`, `proposal.md`, `design.md`, `value.md`, `specs/group-standings/spec.md`, `inventory.md`, `docs/value-scoring.md`, `docs/architecture/context-map.md` and `setup/rules/api/TieBreakRules.kt`. The checked G2 snapshot hash was `6915b6850db585fee2fcd7c7f8ed7cedc1824e18192709d5f629368ba31f8d2a`; the five artifact SHA-256s match its manifest.

## Calculation

The calculation is reproducible: `(0.5 * 3 + 0.3 * 4 + 0.2 * 3) * 0.5 / 8 = (1.5 + 1.2 + 0.6) * 0.5 / 8 = 0.20625`. All entered numbers use permitted scales. The decimal is advisory and should not be interpreted as measured precision or a go/no-go threshold.

## Material finding

G1 refinement and the G2 proposal expressly authorize **analysis and planning only**, excluding runtime implementation. `value.md` instead expressly scores a **possible future implemented standings capability**, includes implementation, tests, integration and delivery in effort 8, and assigns organizer benefit and ranking-error reduction contingent on implementation. Those are reasonable hypotheses for a future feature, but they are not an assessment of the actual approved analysis scope or of its effort. The selected work, score numerator and denominator consequently describe different deliverables. An accurate calculation does not repair the mismatch, and 0.20625 cannot be used to compare or justify prioritizing the approved analysis. The G2 artifact itself confirms the distinction.

Resolve the objective before proceeding: assess the analysis deliverable and its full remaining effort, while labeling the existing calculation separately as an unapproved future-feature estimate; or seek a separately scoped future-feature lifecycle. Compare against a completed example or state why comparison is not reliable. Because this changes value/effort framing materially, return to human G2 after the maker updates the artifacts. This finding does not authorize implementation.

## Evidence and rating challenge

- The checked Kotlin type stores a type plus set-difference, points-ratio and Buchholz flags; it does not encode an ordered ranking algorithm. This supports the identified model limitation but not a claim that a real organizer has experienced a ranking error.
- Independently consulted [DBV 2026/27 regulations, Anlage IV Teil 2 § 10](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf), PDF page 237: the stated ordering lists wins, set difference and point difference before direct comparison and lots. The scenario's selected-rule example is consistent with that sequence. Applicability to any actual RallyOn event remains unverified.
- User value 3 is plausible as a hypothesis for a delivered group-event feature, but there is no organizer validation or measured recurrence; the score cannot establish value of an analysis-only deliverable. Confidence 0.5 is appropriately cautious for the unvalidated persona benefit.
- Learning 4 presupposes a reusable insight **validated in practice** under the documented anchor. The current planning artifact has not validated architecture or a working implementation. Treat 4 as an explicitly prospective ceiling, or justify a lower assessment for analysis with observable evidence.
- Risk reduction 3 is contingent on delivered, correctly selected and tested behavior and supported source evidence. The analysis helps identify misapplied rules but has not demonstrated a prevented production failure. Do not attribute implementation risk reduction to analysis completion.
- Effort 8 is a defensible warning that a future end-to-end feature needs decomposition, given missing result capture, unresolved ownership and the placeholder scoring service. It is not a substantiated estimate of the analysis deliverable. It also has no comparison against completed work. G1 excludes implementation, so effort for the agreed deliverable must cover inventory completion, source verification, candidate specs, review and issue preparation rather than runtime construction.

## Further readiness limits

The specification does not decide how to determine eligible recorded results, group completion, withdrawal corrections, rule selection or ownership; the design acknowledges these. They are acceptable open research questions for an analysis artifact but prevent treating the candidate as implementation-ready or an exact deliverable. The broader #183 inventory calls itself provisional and still needs a source-by-source completeness audit. Issue #185 overlaps rule research; a future issue payload should avoid duplicating it. No follow-up issue creation or G4 approval is implied by this review.
