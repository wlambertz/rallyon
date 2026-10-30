# Layering and ArchUnit

[ADR 0003](../adr/0003-separate-tournament-domain-and-adapters.md) separates configuration draft behavior from its API and persistence adapters. See [tactical design](tactical-design.md) for DDD vocabulary and [Clean Architecture](../clean-architecture-and-clean-code.md) for the Dependency Rule.

## Implemented structure

| Area inside `setup.configuration` | Responsibility | Allowed inward dependencies |
| --- | --- | --- |
| `api` | Published DTOs and service contract | Other explicitly published contracts; transport/validation annotations remain here |
| `web` | HTTP/security translation and existing error advice | Published API and same-module error types; no persistence or use-case dependency |
| `internal.ConfigurationServiceImpl`, `internal.mapping`, Spring configuration | API/domain conversion, error translation, transaction and bean wiring | API, application and domain |
| `internal.tournament.usecase` | Create/update orchestration and `TournamentStore` port | Own application types, domain, JDK/Kotlin |
| `internal.tournament.domain` | Partial draft snapshots, value types and operation invariants | Own domain, JDK/Kotlin |
| `internal.tournament.persistence` | Port implementation, JPA entity reconciliation, repositories and MapStruct | Application/domain plus API conversion at this boundary |

A draft create/update enters `ConfigurationServiceImpl`, which starts a transaction and converts inputs. Pure use cases read/write through `TournamentStore`; domain operations validate changes before persistence reconciliation. Domain errors are translated to existing API-facing exceptions, retaining HTTP behavior.

Creation and update audit times come from the injected `Clock`. Updates preserve organizer/creation metadata, retained child identities, collection ordering and replacement semantics. The adapter checks expected versions and Hibernate enforces `@Version` at flush. Save deliberately does not force an early flush; response version semantics remain compatible with the prior service.

The domain has its own representations of rule values rather than depending on `setup.rules.api` or annotated configuration DTOs. Published API DTOs retain their JSON/Bean Validation and Java builder contracts. Internal domain snapshots allow partial drafts and restoration of existing data; operation validation is explicit, rather than claiming every nullable construction is a complete tournament.

## Executable rules

`LayerDependencyTest` imports production classes only and checks:

- Domain dependencies stay within the domain, JDK/Kotlin and compiler annotations.
- Use-case dependencies stay within application/domain, JDK/Kotlin and compiler annotations.
- Published `api` types do not depend on implementation or web packages.
- Web types do not depend on persistence or use-case packages.

The test asserts that domain and application selections are populated. Test-only forbidden-dependency fixtures prove each rule detects a violation. ArchUnit comes from the existing Spring Modulith test dependency; no new library is introduced.

`TournamentmgmtModuleStructureTest` separately runs `ApplicationModules.verify()` and asserts public versus hidden module types. ArchUnit's layer rules do not replace module allowlists. Kotlin `internal` visibility alone is not the architectural test: Java bytecode and Java callers still make dependency checks valuable.

## Validation and limitations

From the repository root:

```bash
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=LayerDependencyTest,TournamentmgmtModuleStructureTest,DraftUseCaseTest test
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
npm run format:check
```

Install IAM only when local artifacts need it. Full verification includes existing serialization, validation, Java interoperability and security tests. H2 adapter integration checks are not PostgreSQL/Flyway migration tests; see [testing gaps](../TESTING.md).

Review remains responsible for semantic concerns: correct aggregate ownership, useful ports, business invariants, public compatibility and mapper completeness. These tests do not establish performance, concurrency behavior on PostgreSQL, or architectural compliance of the Go CLI and future placeholder services.
