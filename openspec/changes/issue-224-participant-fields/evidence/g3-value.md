# G3 — Independent value challenge

**Verdict: PASS.** Checker `field-value-check`, 2026-11-11. No material change to scores, confidence, effort, scope or architecture is required by this value check. This does not authorize implementation or issue creation.

| Severity | Location | Finding / action |
| --- | --- | --- |
| Advisory | `value.md`, user value / confidence | Recurring field preparation is a labelled persona hypothesis, not measured demand. Preserve confidence 0.5 and validate manual selection/reconciliation usefulness during the approved walkthrough. |
| Advisory | `design.md`, freshness and transaction validation | Coarse source versions can create extra reconciliation after unrelated edits; snapshot-time validation can produce an immediately outdated field. Both are disclosed, so do not claim permanent currentness or increase risk-reduction value without evidence. |
| Advisory | `value.md`, effort 5 | Plausible substantial-integration estimate, conditional on reusing existing entry reads and transaction patterns. Include forced source-overlap tests, first-write races, private API, migration/ADR, reviews and UAT in decomposition; it is not a small persistence-only task. |

## Calculation and anchors

Independently reproduced `(0.5 × 3 + 0.3 × 3 + 0.2 × 2) × 0.5 / 5 = 0.28` with Python. All scores use allowed ranges; no automatic threshold or time estimate is implied.

- **User 3:** plausible noticeable benefit to the proposed organizer selection task; no evidence supports a frequent costly workaround or critical blocked persona task. It explicitly delivers neither draw generation nor public information.
- **Learning 3:** dual-source consistency, independent field versions and executable overlap evidence can extend the existing snapshot approach usefully. The assessment appropriately avoids claiming a wholly new architecture or learning already achieved.
- **Risk 2:** local identity confusion and stale selection are plausible recoverable risks; no incident or major exposure is demonstrated. Creator-only privacy is a necessary design constraint, not evidence for a higher risk score.
- **Confidence 0.5:** code corroborates a representation gap, while organizer demand and the manual workflow remain unvalidated. This matches the hypothesis anchor.
- **Effort 5:** covers a new domain/store/API slice and delivery, with fewer state/admission mechanisms than #219's 8. The explicit dual-source overlap and privacy work prevents a lower estimate; no ignored external service/frontend dependency is assumed.

## Independently inspected evidence

- Read approved refinement/proposal, PF-001–007, design and value against `docs/value-scoring.md` and the G3 reference. Scope excludes generator, ranking, public output, automatic capacity and global identity; the migration/ADR and snapshot/caller-rollback tradeoffs are surfaced.
- Raw `EntryBookService.kt` confirms string player keys, explicit member lists, entry disposition and a separate book version; `ParticipantsRoster.kt` confirms the incompatible numeric player/team reference shape.
- Raw `EntryBookUseCase.kt` confirms creator authorization, version checks and mutations of the entry book only. `EntryBook.kt` confirms explicit single/pair member validation and entry disposition. The existing `EntryBookServiceImpl.kt` establishes reusable REQUIRES_NEW/REPEATABLE_READ adapter precedent rather than a claim that field behavior exists already.
- Compared accepted #185 `findings.md` / `code-reconciliation.md` and #219 `value.md`. They support investigating the bridge and the relative-effort rationale without proving product demand or automatically selecting a draw format.

## Limits and outcome

No implementation, runtime tests, organizer interviews, primary sports-rule revalidation or full design/security certification was performed for this advisory check. Maker mechanical checks were reported passed; this checker did not rerun Maven because there is no runtime change. No maker artifacts or gates were edited. Keep existing G2 decisions intact; a later material change to guarantees, ownership, retention or scope requires renewed human G2 review.
