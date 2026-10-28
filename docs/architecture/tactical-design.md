# Tactical design

How the business logic inside one bounded context is modelled and layered. It complements `docs/clean-architecture-and-clean-code.md` (Dependency Rule, Clean Code) with the Domain-Driven Design building blocks. Status: **draft**; the "Current state" section is a finding, not a decision. Decisions that change code need an ADR.

## Layers and the Dependency Rule

Inside each context, dependencies point inward only:

```
adapters (web, persistence, messaging)  →  application (use cases)  →  domain (model)
```

- **Domain**: aggregates, entities, value objects, domain events, domain services, repository _interfaces_. No Spring, JPA, Jackson or MapStruct imports.
- **Application**: one use case per intent (`CreateDraftUseCase`), orchestrates a single aggregate per transaction, publishes events. Depends on the domain only.
- **Adapters**: controllers, JPA entities and repositories, mappers, messaging. Translate at the edge; never leak adapter types inward.
- The Modulith `api` package is the context's published language; `internal` is not reachable from other contexts.

## Building blocks

- **Aggregate**: a consistency boundary with one root. One transaction changes one aggregate. Reference other aggregates by id, never by object. Enforce invariants inside the root so no caller can create an invalid state.
- **Entity vs value object**: an entity has identity that persists through change (`Tournament`); a value object is immutable and compared by value (`TimeWindow`, `Capacity`, `BracketId`, `TeamSize`). Prefer a value object over a primitive or a nullable pair of fields.
- **Invariants**: write them as `require`/`check` in constructors or factory functions, and make illegal states unrepresentable with sealed types (for example a lifecycle state with only the transitions that exist) instead of a status enum plus nullable fields.
- **Domain event**: a past-tense fact (`TournamentPublished`, `RegistrationClosed`). Use Spring Modulith application events across modules in the same deployable; the event type is part of the publishing context's `api`.
- **Repository**: an interface in the domain or application layer expressing what the model needs, implemented in the persistence adapter.
- **Domain service**: stateless logic that does not belong to one aggregate (for example pairing for a bracket format).
- **Factory**: a function or companion creating an aggregate in a valid state (draft creation).
- **Anti-corruption layer**: translate foreign models at the context boundary so another context's vocabulary never leaks in.

Kotlin idioms: `data class` for value objects, `sealed interface` for variants and states, `@JvmInline value class` for typed ids, `require`/`check` for invariants, `internal` visibility for non-published code. Names come from `docs/architecture/glossary.md`.

## Current state in `service/tournamentmgmt` (finding)

- `Tournament` (`setup.configuration.api`) is a data class with about 30 properties, nearly all nullable, serving as REST contract, use-case argument and domain model at once. Required-field rules live in Bean Validation annotations and in the use-case services rather than in the type, so an invalid `Tournament` can be constructed. This is the "anemic model" pattern the Clean Code docs warn about, and it is the main gap against the guidelines above.
- Some value types already follow the guideline: `ScoringRules` and `TieBreakRules` enforce preset consistency in `init`.
- JPA entities (`setup.configuration.internal.tournament.persistence.entity`) are mapped to and from `Tournament` with MapStruct; the dependency direction (adapter → api) is correct. `docs/clean-architecture-and-clean-code.md` records one deliberate exception: entities reuse `api` enums.
- Aggregate boundaries are implicit. Disciplines, brackets, courts and rosters all hang off one `Tournament`; whether participant rosters belong inside that aggregate is the first question to settle (see the context map: participants likely move to Player Management).

## Rules for new and changed code

1. New business rules go into the domain model as invariants or domain services, not into controllers, mappers or JPA entities.
2. A new aggregate, a change of aggregate boundary, or a domain model that is separate from the API type is an ADR, not a drive-by change.
3. Keep the REST DTO separate from the domain type in new modules; do not extend the wide `Tournament` shape.
4. Cross-context calls go through the other context's `api`; never through its entities or repositories.
5. Every invariant has a unit test in the domain layer that runs without Spring.

## Decisions needed (candidate ADRs)

- Domain model separate from the API type and the JPA entity, versus the current single wide type.
- Aggregate boundaries inside Tournament Management (tournament, discipline/bracket, roster).
- Events versus synchronous APIs between contexts.
- Enforcement: ArchUnit rules for the Dependency Rule (see Phase A plan), added next to `TournamentmgmtModuleStructureTest`.
