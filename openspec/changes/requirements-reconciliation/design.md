# Design: group standings candidate

## Context and constraints

The current service stores tournament configuration and rosters; a result capture and standings capability is not established. `docs/architecture/context-map.md` places scoring in a proposed Scoring context, currently a placeholder. The Organizer is the primary user; players and spectators may later consume results.

## Decisions and tradeoffs

Represent the competition rule selection and its version explicitly in the candidate contract instead of silently applying the issue's international hierarchy. Restrict the worked rule example to DBV Spielordnung Anlage IV Teil 2 § 10 and do not infer its applicability to another competition. Human selection and confirmation of applicable authority are required. Exact treatment of withdrawal, unfinished games and unresolved ties requires separate domain refinement; the candidate refuses to declare a final order when the selected rules or input do not determine one.

## Bounded contexts, invariants and dependency direction

Competition, group membership and results are conceptual inputs; no new aggregate, event or storage design is selected here. The output is a derived ordering, its rule identification and whether it is determined. The same inputs and rule version must lead to the same result, except an explicitly authorized lot outcome is an input. Configuring a competition and calculating standings may ultimately belong in different contexts; no dependency on another context's internal packages is permitted.

## Compatibility and escalation assessment

There is no runtime, public API or event change in this proposal. A future implementation could require new persistence, migrations, API surface or a module; these are explicitly unresolved and need the relevant review. No shared IAM change or new dependency is proposed. Existing REST, JWT and CLI contracts remain intact. The Organizer frontend stack remains undecided.

## Validation, rollout and recovery

Review each example against the cited DBV edition and applicability; verify OpenSpec syntax and repository formatting for this documentation. Future implementation needs independently checked examples, including ties, missing results and withdrawal, plus appropriate runtime integration and compatibility checks. No rollout occurs with this analysis.

## ADR assessment

No architecture decision is made by this draft. An ADR is needed before choosing a new scoring context, moving ownership, adding a cross-context integration or establishing durable result history.
