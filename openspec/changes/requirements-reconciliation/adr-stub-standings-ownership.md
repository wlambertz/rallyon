# ADR stub: ownership of match results and group standings

Status: **proposed for review; no decision or implementation authorized**. Related: #183, #203, `docs/architecture/context-map.md`, `standings-reference-case.md`, `standings-event-investigation.md` and STANDINGS-001–002 in the candidate spec.

## Decision needed

Which bounded context owns a confirmed match result, the selected competition rule version and the authoritative derived standings, and how are corrections communicated without coupling to another context's internal storage?

## Current evidence

`setup.configuration` stores a tournament draft and rule configuration. Its existing `TieBreakRules` is not an ordered standings implementation. The context map describes Tournament Management as owner of competition configuration and proposes Scoring for results and standings, but `service/scoring` has no runnable implementation and transport between contexts has not been decided. Public Information is also proposed rather than implemented. No runtime result-event contract has been established.

## Options for a future change

1. **Scoring context owns confirmed results and derived standings.** Tournament Management owns the selected competition configuration and supplies an exposed versioned contract. This follows the context map's intended ownership but requires an explicitly approved module or service boundary, integration design, persistence and operational model.
2. **An internal module in the current tournament service owns the first bounded slice.** A named API/port isolates result and standings contracts, with a later move requiring an explicit migration decision. This may reduce immediate infrastructure but risks conflating long-term context ownership and current deployment; Modulith and Dependency Rule checks must remain effective.
3. **Postpone durable standings.** Continue research and manually calculate results until a real organizer workflow and rule set are selected. This avoids premature architecture but delivers no automated ranking.

No option has been chosen. In each implementation option, authorize result entry and correction explicitly, identify the applicable rule version and result state, decide how incomplete results or unresolved ties appear, and avoid inferring a history-retention obligation solely from DBV's calculation rule.

## Compatibility and escalation

Any new module, dependency, migration, REST surface, JWT/role change or cross-context integration requires the applicable human review. Existing `/api/tournamentmgmt/...`, JWT and CLI behavior must remain compatible unless a break is separately approved. A new table or event schema is not selected by this stub.

## Evidence and validation needed before acceptance

- Select an actual target competition and confirm the specific binding announcement and rule edition; do not generalize the U11 example.
- Trace result entry, correction, incomplete matches and any lot outcome to accepted scenarios and governing sources.
- Compare the selected rule with related #185 research and obtain a fresh independent architecture review.
- Define tests for deterministic calculation, unsupported rules, incomplete inputs, correction behavior, security where applicable and the selected integration. Record the actual runtime verification and migration strategy if proposed.
