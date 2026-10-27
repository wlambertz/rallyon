# 0001 — Use Spring Modulith for module structure

## Status
Accepted

## Context
The `tournamentmgmt` service is growing beyond a single package. Without enforced module boundaries, coupling between concerns (setup/configuration, rules, phases, planning, administration) creeps in, making the codebase harder to maintain and test.

## Decision
Use [Spring Modulith](https://docs.spring.io/spring-modulith/reference/) to:
- Verify application module structure via `spring-modulith-starter-test`
- Expose module documentation and events through actuators
- Annotate public APIs with Modulith stereotypes for module slicing

## Consequences
+ Clear, bounded-context-aligned packages (`setup.configuration`, `setup.rules`, `setup.phases`, ...)
+ Automated verification that modules do not bypass each other's internals
+ Runtime module documentation via Actuator
- Build-time verification adds a small step to CI (`verify -Pmodulith`)

## Related
- `AGENTS.md` — coding style & naming conventions
- `docs/TESTING.md` — Modulith slice test guidance
