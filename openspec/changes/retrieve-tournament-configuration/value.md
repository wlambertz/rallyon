# Value assessment: retrieve a saved tournament configuration

## Persona, context and evidence

- Primary persona: Organizer, returning to a saved tournament configuration.
- Owning bounded context: Tournament Management, existing `setup.configuration` module.
- Direct code evidence: the controller exposes GET-by-ID, its service method throws an unimplemented-operation exception, and the existing application port/JPA adapter already support lookup. The query can close an observed API gap without introducing a storage or integration design.
- Maintainer evidence: Phase C was explicitly requested; the maintainer selected this pilot, authentication-only access and witnessed API acceptance, then approved G1 scope.
- Hypothesis: reloading saved configuration reduces repeated setup work after interruptions. No organizer usability study, task-time measurement or delivered frontend benefit is claimed.
- Learning evidence will come from actually using the full lifecycle and documenting friction. The complete pilot has not yet run; expected learning is an estimate.

## Scores and calculation

Apply the advisory model in `docs/value-scoring.md`:

`priority = (0.5 × user value + 0.3 × learning value + 0.2 × risk reduction) × confidence / effort`

| Dimension | Rating | Reason |
| --- | --- | --- |
| User value | 3 / 5 | Enables reloading configuration in a recurring setup workflow; frequency and saved effort remain persona hypotheses |
| Learning value | 5 / 5 | Exercises the central outstanding Phase C learning objective: a real capability through refinement, specification, issues, implementation, independent checks and witnessed acceptance |
| Risk reduction | 2 / 5 | Adds reproducible retrieval, not-found, authentication and non-mutation checks against future regressions; no broad security improvement is claimed |
| Confidence | 0.5 | The implementation gap and reuse opportunities are observed, but organizer benefit and end-to-end workflow effectiveness remain unvalidated |
| Effort | 3 | Moderate coordinated work across query, service wiring, Java interoperability, persistence/HTTP tests, lifecycle evidence, local environment and delivery; no schema, UI or new dependency work |

Calculation: `(0.5 × 3 + 0.3 × 5 + 0.2 × 2) × 0.5 / 3 = 1.7 / 3 ≈ 0.567`.

This is a relative ordering aid, not a pass threshold, deadline or authorization. The maintainer chose finishing the lifecycle phases before other backlog work. No numerical work budget has been agreed.

## Assumptions and uncertainty

- The user-value rating assumes reloading saved configurations is useful in organizer setup; witnessed acceptance will check the API behavior, not establish a production usability metric.
- Learning value is realized only if gates, independent evidence, resumption and observed limitations remain inspectable. A merged endpoint alone does not complete Phase C.
- Effort includes tests, documentation/evidence, review, environment setup and delivery. Docker was unavailable during refinement; time to restore a usable local stack is not yet measured. A material effort or scope change returns to G2.
- Authentication-only access is an explicit pilot choice. This score does not claim ownership protection or justify expanding the query into a new access-control policy.
- H2 checks and MockMvc have known limits. A real local Keycloak/PostgreSQL API walkthrough is required before human G8 acceptance; no UAT success is assumed.

## G2 go/no-go

Pending explicit human approval of the proposal, capability specification, design and this value assessment. Approval will be recorded against their current hashes in `.lifecycle.json`. The score does not advance the gate.

## G3 independent challenge

Pending G2 approval. A fresh independent checker will reproduce the calculation, challenge evidence and effort, and record its result in a separate report under `evidence/`. Material changes return to G2. This planning file will not be edited merely to append a checker outcome, because doing so would stale the approved value assessment.
