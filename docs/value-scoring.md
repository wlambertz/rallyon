# Value scoring

Use this advisory model to compare candidate work in RallyOn, an educational badminton project. Prepare the assessment before [G2 specification approval](feature-lifecycle-gates.md); a fresh-context checker verifies it at G3. The maintainer makes the go/no-go decision. There is no automatic approval threshold.

## Model

`priority = (0.5 × user value + 0.3 × learning value + 0.2 × risk reduction) × confidence / effort`

Score each value dimension from 0 to 5 using the anchors below. Record the primary persona, owning bounded context, supporting evidence, assumptions, scores, effort and confidence. Explain intermediate ratings and avoid treating a decimal score as precise evidence. Compare work assessed with the same assumptions; scores are not commitments or dates.

| Rating | User value | Learning value | Risk reduction |
| --- | --- | --- | --- |
| 0 | No identified benefit | No new understanding | No identified risk reduced |
| 1 | Cosmetic or rare convenience | Practice of familiar tools | Small local maintenance risk |
| 2 | Minor improvement to an occasional task | Small extension of existing knowledge | Recurring but easily recovered defect risk |
| 3 | Noticeable improvement to a recurring task | Useful new technique with documented findings | Material regression or maintenance risk |
| 4 | Removes a frequent, costly workaround | Reusable architecture/tooling insight validated in practice | Major reliability/security exposure reduced |
| 5 | Enables a critical blocked persona task | Resolves a central learning objective with transferable evidence | Critical failure mode demonstrably prevented |

A hypothesis can support a proposed rating, but must be labelled as such; use confidence to reflect the evidence. A hypothetical security benefit alone does not justify a high risk-reduction rating.

| Confidence | Evidence                                                                                   |
| ---------- | ------------------------------------------------------------------------------------------ |
| 0.5        | Assumptions or unvalidated persona hypotheses; record how to test them                     |
| 0.75       | Corroborating code inspection, maintainer experience or multiple consistent observations   |
| 1.0        | Direct validation, such as a reproduced failure, measured task, or confirmed user evidence |

Estimate relative effort across **implementation, tests, documentation and delivery**, including integration and review. Use 1 (smallest self-contained change), 2 (small change with a few coordinated edits), 3 (moderate work across one area), 5 (substantial integration/refactor), or 8 (large work needing decomposition). Compare against completed work; these are not day estimates. Effort cannot be zero. A score outside the listed ranges requires reassessment, not a custom denominator.

## Worked examples

These numbers are illustrations, not approved backlog decisions.

| Candidate | User | Learning | Risk | Confidence | Effort | Calculation | Priority |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Organizer draft update with clear validation feedback | 4 | 2 | 3 | 0.75 | 3 | `(2 + 0.6 + 0.6) × 0.75 / 3` | 0.80 |
| Framework-free draft model with executable layering rules | 2 | 5 | 4 | 0.75 | 5 | `(1 + 1.5 + 0.8) × 0.75 / 5` | 0.495 |

For the user feature, the assumed benefit is less correction work when configuring tournaments. Persona pains are currently hypotheses; confirm feedback usefulness with an organizer. Include controller, domain, persistence and review work in the effort estimate.

For the engineering improvement, direct code inspection establishes use-case dependencies on persistence; executable architecture tests can prevent their return. The claimed maintenance benefit and effort remain estimates. Learning value is high only if the decision, tradeoffs and verification are documented and understood.

The higher example score suggests an ordering discussion, not permission to implement. Dependencies, deadlines, mandatory fixes, learning priorities and unresolved risks can override the ranking. Record the human rationale, especially when selecting lower-ranked work. When G3 materially changes a score or its evidence, return to G2 for renewed go/no-go.
