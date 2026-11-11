# Value assessment

## Persona, context and evidence

Organizer; Tournament Management. The published Bommern example and the unimplemented service methods show a plausible gap, but no organizer interviews or measured time savings exist. The public-link choice is a maintainer preference, not direct audience validation.

## Scores and calculation

| Dimension | Score | Rationale |
| --- | --- | --- |
| User value | 3/5 | A plausible recurring announcement and correction task, not proven to be a frequent or costly workaround. |
| Learning | 4/5 | A reviewed public read boundary, concurrency and security tradeoffs provide reusable architecture experience if documented. |
| Risk reduction | 2/5 | Explicit publication and narrow projection can reduce accidental disclosure versus exposing a wide DTO; no measured existing exposure is claimed. |
| Confidence | 0.5 | Case and code are corroborated, but demand, audience and actual operator workflow remain hypotheses. |
| Effort | 5 | Substantial service, independent publication state and withdrawal, security, migration, concurrency tests, documentation and independent review; estimate, not a date. |

Priority = `(0.5 × 3 + 0.3 × 4 + 0.2 × 2) × 0.5 / 5 = 0.31`. The score is advisory, not approval or a meaningful comparison without equally assessed alternatives.

## Assumptions and uncertainty

Validate the intended organizer and content with a real user; verify applicable obligations for any chosen event. A plain-text public link can be insufficient if users need a formatted printable document or an existing federation platform. Security authorization, current draft trust, independent publication state and the PostgreSQL migration add uncertainty and must be reviewed. In particular, an organizer may mistakenly assume that making a tournament private also withdraws its announcement; the workflow must communicate that the two states are separate.

## G2 go/no-go

Pending renewed explicit human approval of the revised proposal, specification, design and value evidence. The earlier approval applied to a materially different visibility model.

## G3 independent challenge

The initial [independent challenge](evidence/g3-value.md) failed on the prior visibility model; a fresh delta check is needed after renewed G2 approval. The calculation itself was reproduced.
