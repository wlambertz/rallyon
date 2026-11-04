# Feature lifecycle gates

Phase A of [issue #191](https://github.com/wlambertz/rallyon/issues/191) defines the review contract for idea → UAT. Phase B supplies the `rallyon-feature` schema, shared lifecycle skill, evidence helper and session-scoped Claude and Codex issue hook. The [operation guide](agent-workflows.md#feature-lifecycle) explains their use and limits. Human decisions and independent checking remain explicit; file existence is not gate approval.

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

This table is the full track; see [Choosing a track](#choosing-a-track) for the reduced and research tracks. Value scoring is prepared **before G2** so the human go/no-go uses both the specification and value evidence. G3 independently verifies that assessment. If G3 changes the value conclusion, return to G2; it cannot override the human decision.

## Approval, rejection and escalation

Record the artifact/revision, evidence, approver or checker, date and outcome next to the change artifacts. Do not infer approval from silence or a score. Material changes to scope, requirements, architecture, compatibility, effort or evidence invalidate affected approvals and downstream evidence. Resume from the earliest affected gate. A rejection records actionable reasons and returns to that gate's preparation; it never advances automatically.

The maker cannot act as their own checker. Bounded briefs, short reports and delta-only rechecks keep checks affordable without weakening this: a recheck still comes from a non-maker, and gate staleness still covers every changed input. Automated checks and fresh-context reviewers support decisions; human gates remain human. A different person or fresh-context native agent performs independent checks. If no independent checker is available, record that the gate remains pending.

Escalate to the maintainer regardless of the current gate for:

- Changes in `3rd_party/iam`, a Flyway migration, or a public REST/JWT/CLI compatibility break.
- A new dependency or module, or a spec ambiguity discovered during implementation.
- Reviewer disagreement remaining after two review rounds, or exceeding the agreed work budget. Record the budget at G1; do not invent a numerical limit if none was agreed.

Local Docker Compose is the current backend UAT environment. UI features remain unsupported until an organizer frontend stack is selected. Evidence must name the actual environment and limitations.

## Reduced path for bugs and documentation

A narrow bug fix or documentation change can use one issue/PR record instead of refinement workshops, a numerical value debate and three separate reviewer reports. Record the problem, scope, acceptance criteria, applicable architecture/compatibility risks, checks and review evidence. Mark omitted artifacts as not applicable with a reason. Human scope/approval decisions, issue-creation approval, applicable escalation, independent checking and merge approval still apply. UAT may be a human documentation review or a focused reproduction check, with that choice recorded explicitly.

The reduced path is unsuitable for new capabilities, broad refactors, ambiguous scope or security-sensitive changes.

## Choosing a track

Choose the track before G1 and state it, with its reason, in the G1 artifact so the human approves the ceremony together with the scope. The helper fixes the mode for the session; a misclassified change starts a new change rather than switching modes.

| Track | Use when | Delivers | Schema / helper mode |
| --- | --- | --- | --- |
| Full | A new or changed capability, broad refactor or security-sensitive change | Code, accepted specs, tests | `rallyon-feature` / `full` |
| Reduced | A narrow bug or documentation change whose correct content is already known | A small fix or docs | `spec-driven` / `reduced` |
| Research | The goal is knowledge with an uncertain answer: requirements reconciliation, integrating external sources, a technical spike, option or ADR exploration | Source-traced findings, a recommendation, optional follow-up issues | `rallyon-research` / `research` |

Writing up information that is already settled is a reduced docs change. If research shows that production code, tests, migrations, runtime configuration or dependencies must change, record that in the findings and propose a follow-up feature; the follow-up runs its own full lifecycle.

## Research path

Agile spikes and SAFe exploration enablers treat a time-boxed investigation as done when it produces knowledge (findings, a decision or refined follow-up stories), not working software. ITIL 4 change enablement likewise scales approval to risk instead of applying one ceremony to every change. The research path therefore omits requirement specs, numeric value scoring, issue decomposition before work and three review lenses, but keeps the controls that catch research errors: an approved question and timebox, and an independent check of claims against their sources.

| Gate | Definition of Ready | Definition of Done / evidence | Decision owner |
| --- | --- | --- | --- |
| G1 — Charter | A question, the decision it informs and candidate sources | `charter.md` records question, informed decision, persona/context, sources, exclusions, deliverable, timebox or no agreed budget, track reason and no production changes; human explicitly accepts it | Human |
| G5 — Findings verification | Findings, and any follow-up drafts, ready for review | Fresh-context checker reproduces the claim-to-source mapping, checks claims about current specs/code, confirms changes stay inside the change (plus any docs the charter names) and records `npm run format:check` and strict `openspec validate` results | Independent checker |
| G8 — Acceptance | Current G5 verification | `uat.md` records the human review of findings, recommendation and limitations; human explicitly accepts | Human |
| G4 — Follow-up issues (optional) | G8 passed and `issue-drafts.json` reviewed at G5/G8 | Human approves the exact follow-up payload; the helper creates it and records the URLs | Human |
| Merge | All applicable gates current | Separate explicit human merge approval | Human |

G4 comes after acceptance because follow-up issues are an output of accepted findings, not a prerequisite for doing the research. Without `issue-drafts.json`, G4 is not applicable. G2, G3, G6 and G7 are not part of this track; the helper rejects them. The escalation list above still applies.

Candidate requirements stay in `findings.md` or follow-up issue bodies, never in the change's `specs/`, and research changes archive with `openspec archive <change> --skip-specs`. Accepted behavior is only specified by a later feature change.

## CI versus lifecycle gates

The existing Java CI workflow calls its Maven verification job **Gate 1**. That CI label is not lifecycle G1. Maven tests and architecture verification supply evidence for lifecycle G6; they do not approve scope, specifications, issue creation, UAT or merge. See [the actual testing strategy and gaps](TESTING.md).

## Phase A completion and deferred work

Phase A delivers layering enforcement, this gate contract, value scoring, issue/PR templates and an accurate testing inventory. Phase B delivers the schema, skill, helpers and focused workflow checks described in [ADR 0004](adr/0004-feature-lifecycle-orchestration.md). API, persistence, security and observability guideline expansion is deferred. Phase C's pilot was accepted and merged in #195, with its archive and main specification merged in #196. Issue #191 and ADR 0002 track the remaining legacy-spec/CLI closeout; [migration evidence](legacy-spec-migration.md) and [closeout status](issue-191-closeout.md) record its delivery and pending human decisions. Completing an implementation does not itself grant acceptance or merge approval.
