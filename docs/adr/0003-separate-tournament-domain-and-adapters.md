# 0003 — Separate tournament domain and adapters

## Status

Accepted as part of Phase A of issue #191.

## Context

The configuration module's draft use cases depended directly on Spring Data repositories, MapStruct and JPA entities. Its API DTOs also served as its model, carrying transport and validation annotations. Modulith protected module exposure but did not enforce the Dependency Rule inside the module.

The maintainer selected a framework-free domain and persistence ports rather than grandfathering these dependencies. This is a behavior-preserving refactor: the nullable published DTOs, API signatures, stored schema and aggregate ownership remain compatible.

## Decision

- Keep the tournament and its courts, disciplines, brackets, registration windows and rosters in the existing aggregate. Do not move participant ownership between bounded contexts in this change.
- Put local domain values and draft validation/replacement operations under `internal.tournament.domain`. The model represents partial drafts and persisted snapshots; required rules apply at the operation boundary, without inventing stricter construction rules for existing data.
- Keep framework-free application orchestration and the `TournamentStore` port in `internal.tournament.usecase`. A port loads/saves domain snapshots and carries expected versions; no persistence handle escapes it. Use a JDK `Clock` for reproducible audit timestamps.
- Make `ConfigurationServiceImpl` the inbound API adapter, translating DTOs and errors. Its Spring transaction encloses the complete lookup, domain operation, entity reconciliation and save. Spring configuration constructs the pure use cases.
- Implement the port in `JpaTournamentStore`. Retain managed child entities by their existing identifiers and preserve orphan removal, collection replacement and non-flushing save behavior. Hibernate's `@Version` remains the concurrent-write backstop. A returned update snapshot retains the version observed before transaction commit, matching the prior implementation.
- Keep MapStruct and the existing entity/API projection in persistence. Explicit Kotlin mapping bridges the published DTOs and domain values. The persistence adapter may compose those boundary mappings; the domain never imports either adapter or the API. Existing entity references to API enums remain adapter details, not domain dependencies.
- Enforce domain and application isolation with ArchUnit, and continue using Modulith for cross-module exposure. Compiler-generated Kotlin/JetBrains annotations and JDK/Kotlin runtime dependencies are permitted in the pure layers.

## Consequences

Business checks can run without Spring, and executable rules prevent framework and adapter dependencies from returning. API annotations and Java interoperability remain unchanged. Validation before mutation also makes rejected replacements leave the aggregate untouched.

The cost is additional local value types and explicit bidirectional mapping. Changes to published values must be assessed at both boundaries; round-trip and contract tests detect omissions. Reusing the existing persistence projection avoids rewriting its storage normalization in the same refactor, but adds a mapping step. A later direct entity/domain projection can replace it behind the port without changing use cases.

This does not add lifecycle operations, events, migration testing, an orchestrator, or new context boundaries. See [layering and enforcement](../architecture/layering-and-archunit.md) for the concrete dependencies and validation commands.
