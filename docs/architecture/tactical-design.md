# Tactical design

How the business logic inside one bounded context is modelled and layered. It complements `docs/clean-architecture-and-clean-code.md` (Dependency Rule, Clean Code) with the Domain-Driven Design building blocks. Status: **draft** guidance; implemented draft layering is recorded in ADR 0003. Further architectural decisions that change code need an ADR.

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

## Current state in `service/tournamentmgmt`

[ADR 0003](../adr/0003-separate-tournament-domain-and-adapters.md) introduces a separate framework-free draft model and application persistence port. API `Tournament` remains the wide, nullable published DTO for compatibility; use cases now exchange domain snapshots instead.

- `internal.tournament.domain` owns draft creation and replacement validation, including court identities, bracket uniqueness and roster rules. Pure tests exercise these operations without Spring or persistence.
- Domain snapshots retain partial draft fields and support rehydration of existing records. Operation-level validation preserves existing accepted inputs; this is not yet a lifecycle model with every invalid construction made impossible.
- `ConfigurationServiceImpl` converts API/domain values and owns the Spring transaction. Pure use cases use `TournamentStore` and an injected `Clock`; `JpaTournamentStore` implements the port and reconciles existing JPA children.
- MapStruct remains in persistence. Entities still reuse API enums as an adapter implementation detail; no API type enters the domain or application layers.
- The aggregate remains a tournament with disciplines, brackets, courts and rosters. Reassigning participant ownership to Player Management remains a separate architecture decision.
- [ArchUnit rules](layering-and-archunit.md) enforce inward dependencies; Modulith continues to enforce cross-module APIs.

## Rules for new and changed code

1. New business rules go into the domain model as invariants or domain services, not into controllers, mappers or JPA entities.
2. A new aggregate, a change of aggregate boundary, or a domain model that is separate from the API type is an ADR, not a drive-by change.
3. Keep the REST DTO separate from the domain type in new modules; do not extend the wide `Tournament` shape.
4. Cross-context calls go through the other context's `api`; never through its entities or repositories.
5. Every invariant has a unit test in the domain layer that runs without Spring.

## Decisions needed (candidate ADRs)

- Aggregate boundaries inside Tournament Management (tournament, discipline/bracket, roster).
- Events versus synchronous APIs between contexts.
- Future lifecycle state modelling beyond partial draft snapshots; preserve published DTO compatibility.
