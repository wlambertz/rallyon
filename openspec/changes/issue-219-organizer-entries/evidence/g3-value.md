# G3 independent value challenge

Verdict: **pass**. Checker: `entry-value-check`, 2026-11-11. No material revision of the value conclusion is required before decomposition. This is not implementation or issue-creation approval.

| Severity | Location | Finding / consequence |
| --- | --- | --- |
| Advisory | `value.md`, confidence and user value | User value 3 is a labelled hypothesis, not validated demand. Confidence 0.5 correctly reflects the absence of interviews or measured workload. Preserve that limitation in follow-up issues and UAT. |
| Advisory | `value.md`, effort; `design.md`, entry-book transaction | Effort 8 is plausible relative to the completed announcement slice's 5: entry dispositions, membership uniqueness, intake races, configuration snapshots and audit add coordinated work. Decompose implementation, tests, documentation and delivery; do not treat 8 as a schedule or budget. |
| Advisory | `design.md`, capacity and roster separation | Manual capacity review, local unverified keys, creator-only access and no roster synchronization materially limit the first slice's utility. They are consistently disclosed and were presented for human approval; do not add automation or verified identity during apply without revisiting scope. |
| Advisory | `design.md`, ADR assessment | The concrete transaction/locking strategy remains an explicit pre-implementation ADR decision. Decomposition must resolve that dependency before code, especially configuration consistency and closure-versus-entry races. |

## Calculation and anchors

`(0.5 × 3 + 0.3 × 4 + 0.2 × 2) × 0.5 / 8 = 3.1 × 0.5 / 8 = 0.19375` (about 0.19), matching `value.md`.

- User 3: reasonable for a plausible recurring organizer task, with no claim of a frequent costly workaround or critical blocked task.
- Learning 4: credible reusable modelling and transaction-boundary learning, conditional on documented decisions and executable verification; not already achieved learning.
- Risk reduction 2: proportionate to anticipated duplicate/lost-update problems; it does not claim a demonstrated security incident or critical prevention.
- Confidence 0.5 and effort 8 use the catalog's allowed values. The decimal is advisory and explicitly not precise empirical evidence.

## Independently inspected evidence

- Read refinement, proposal, specification ENT-001–007, design and value assessment against `docs/value-scoring.md` and the G3 reference.
- Inspected current `ParticipantsRoster.kt`, `RegistrationPolicy.kt`, lifecycle stubs in `ConfigurationServiceImpl.kt`, and roster validation in `DraftReplacement.kt`. These establish the configuration/workflow distinction and support the gap without proving demand.
- Checked the accepted #213 findings and context-map statements about unresolved identity/Player Management ownership. The proposal does not promote their candidate scenarios into universal federation rules.
- Compared completed announcement `value.md` (effort 5). The larger relative estimate includes persistence, migration, concurrency, security, audit and delivery rather than code alone.

## Limits

No organizer interview, new primary-source rule verification or production/test execution was performed for this value-only check. The bounded raw-code review and prior accepted research support the assessment; they do not establish adoption or legal compliance. No blocking cross-artifact contradiction was found in the approved value/scope decision.
