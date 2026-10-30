# Feature lifecycle gates

Phase A of [issue #191](https://github.com/wlambertz/rallyon/issues/191) defines the review contract for idea → UAT. These gates are a manual workflow today. Phase B will implement the OpenSpec schema, orchestrator, approval records and hooks; this document does not claim that automation exists.

Read the [personas](wiki/Personas.md), [context map](architecture/context-map.md), [tactical design](architecture/tactical-design.md), [layering rules](architecture/layering-and-archunit.md) and [value model](value-scoring.md). OpenSpec artifacts live in `openspec/`, following ADR 0002. Existing behavior, commands and constraints come from code and the nearest `AGENTS.md`.

## Ready and done at each gate

Ready means the required inputs are available. Done means the evidence has been checked and the named approver has accepted it. A missing artifact, failed check or unresolved ambiguity means the gate has not passed.

| Gate | Definition of Ready | Definition of Done / evidence | Decision owner |
| --- | --- | --- | --- |
| G1 — Refinement | An idea, primary persona, owning app/context and problem evidence | Scope, exclusions, success measures and open assumptions recorded; human explicitly accepts scope | Human |
| G2 — Specification | Approved scope; proposal, testable requirements/scenarios, design and draft value score | Success and failure scenarios, compatibility impact, architecture/ADR assessment, value evidence and validation approach accepted; explicit go/no-go | Human |
| G3 — Value check | Approved specification and its scored value assessment | Fresh-context checker reproduces calculation, challenges evidence and effort, and confirms readiness for issue decomposition | Independent checker |
| G4 — Issues | G3 passed; concrete proposed issue titles/bodies, dependencies and ordering | Human approves the exact proposed issues, each with requirement references, acceptance criteria and validation commands, before creation; created issue IDs recorded afterward | Human |
| G5 — Implementation | Approved issue scope, dependencies satisfied and applicable architecture decisions recorded | Acceptance criteria implemented; changes trace to requirements/issues; implementation evidence and deviations recorded | Maker, checked independently |
| G6 — Testing | Reviewable implementation and agreed test scenarios | Required runtime checks pass; scenario-to-test mapping, manual evidence and limitations recorded; failures resolved | Independent checker |
| G7 — Review | Diff, specification, test evidence and decision records available | Independent domain/architecture, correctness/security and test/maintainability reviews complete; blocking findings resolved | Fresh-context reviewers |
| G8 — UAT | Reviewed implementation, reproducible local environment and persona-oriented UAT script | Human executes or witnesses agreed scenarios and explicitly accepts results; failures return to implementation/testing/review | Human |
| Merge | G8 passed; final diff and applicable checks still current | Separate explicit human merge approval for the reviewed revision | Human |

Value scoring is prepared **before G2** so the human go/no-go uses both the specification and value evidence. G3 independently verifies that assessment. If G3 changes the value conclusion, return to G2; it cannot override the human decision.

## Approval, rejection and escalation

Record the artifact/revision, evidence, approver or checker, date and outcome next to the change artifacts. Do not infer approval from silence or a score. Material changes to scope, requirements, architecture, compatibility, effort or evidence invalidate affected approvals and downstream evidence. Resume from the earliest affected gate. A rejection records actionable reasons and returns to that gate's preparation; it never advances automatically.

The maker cannot act as their own checker. Automated checks and fresh-context reviewers support decisions; human gates remain human. Phase A can be followed manually, with a different person or fresh-context agent performing independent checks. If no independent checker is available, record that the gate remains pending.

Escalate to the maintainer regardless of the current gate for:

- Changes in `3rd_party/iam`, a Flyway migration, or a public REST/JWT/CLI compatibility break.
- A new dependency or module, or a spec ambiguity discovered during implementation.
- Reviewer disagreement remaining after two review rounds, or exceeding the agreed work budget. Record the budget at G1; do not invent a numerical limit if none was agreed.

Local Docker Compose is the current backend UAT environment. UI features remain unsupported until an organizer frontend stack is selected. Evidence must name the actual environment and limitations.

## Reduced path for bugs and documentation

A narrow bug fix or documentation change can use one issue/PR record instead of refinement workshops, a numerical value debate and three separate reviewer reports. Record the problem, scope, acceptance criteria, applicable architecture/compatibility risks, checks and review evidence. Mark omitted artifacts as not applicable with a reason. Human scope/approval decisions, issue-creation approval, applicable escalation, independent checking and merge approval still apply. UAT may be a human documentation review or a focused reproduction check, with that choice recorded explicitly.

The reduced path is unsuitable for new capabilities, broad refactors, ambiguous scope or security-sensitive changes.

## CI versus lifecycle gates

The existing Java CI workflow calls its Maven verification job **Gate 1**. That CI label is not lifecycle G1. Maven tests and architecture verification supply evidence for lifecycle G6; they do not approve scope, specifications, issue creation, UAT or merge. See [the actual testing strategy and gaps](TESTING.md).

## Phase A completion and deferred work

Phase A delivers layering enforcement, this gate contract, value scoring, issue/PR templates and an accurate testing inventory. API, persistence, security and observability guideline expansion is deferred. Phase B's schema, skills and hooks, Phase C's pilot, legacy YAML migration and `ro spec` deprecation remain tracked by issue #191 and ADR 0002; completing Phase A does not close the issue.
