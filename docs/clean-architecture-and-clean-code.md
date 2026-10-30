# Clean Architecture and Clean Code — RallyOn Conventions

## Why this exists

RallyOn is an educational project (see `AGENTS.md`'s Educational Project Principle): the goal is to make architecture practice visible and explainable, not just enforced by tooling nobody can point to. Spring Modulith's `api`/`internal` module boundary in `service/tournamentmgmt` already implements Robert C. Martin's Clean Architecture **Dependency Rule** — this doc names that principle explicitly, generalizes it to the Go CLI, and pairs it with a small set of Clean Code rules so both are reviewable rather than tacit.

The quotable, enforceable one-liners derived from this doc live in `AGENTS.md`'s "Architecture Boundaries" section and the "Architecture Rules"/"Local Conventions" sections of the two subtree `AGENTS.md` files — that is what the `/code-review` skill's Conventions lens actually checks diffs against. This file is the background and reasoning behind those one-liners, not itself a review target.

## The Dependency Rule (Clean Architecture)

> Source-code dependencies must point only inward, toward higher-level policy. Nothing in an inner circle (business rules, contracts) can know anything about an outer circle (frameworks, databases, UI, other modules' internals).

**How it already applies here.** `service/tournamentmgmt` is a Spring Modulith application. Each `setup.*` submodule is declared `CLOSED` with an explicit allowlist of what it may depend on, and each has exactly one `@NamedInterface("api")` package that is exposed — everything else is hidden by default:

```java
// service/tournamentmgmt/src/main/java/.../setup/configuration/package-info.java
@org.springframework.modulith.ApplicationModule(
    id = "setup.configuration",
    type = org.springframework.modulith.ApplicationModule.Type.CLOSED,
    allowedDependencies = {"setup.rules::api", "setup.phases::api"}
)
```

This isn't aspirational — it's a passing test. `TournamentmgmtModuleStructureTest.kt` calls `ApplicationModules.verify()` and asserts the boundary directly:

```kotlin
// service/tournamentmgmt/src/test/kotlin/.../TournamentmgmtModuleStructureTest.kt
assertThat(configurationModule.isExposed(ConfigurationService::class.java)).isTrue()
...
assertThat(configurationModule.isExposed(ConfigurationServiceImpl::class.java)).isFalse()
assertThat(configurationModule.isExposed(CreateDraftUseCase::class.java)).isFalse()
assertThat(configurationModule.isExposed(TournamentRepository::class.java)).isFalse()
```

**The vertical slice, traced.** [ADR 0003](adr/0003-separate-tournament-domain-and-adapters.md) separates the draft model from its published DTOs and persistence:

```
ConfigurationController (web) -> ConfigurationService (api)
  <- ConfigurationServiceImpl (API mapping, error translation, Spring transaction)
       -> CreateDraftUseCase / UpdateDraftUseCase (pure application)
            -> domain draft operations
            -> TournamentStore (application port)
                 <- JpaTournamentStore (persistence adapter)
                      -> TournamentRepository / TournamentMapper / JPA entities
```

Dependencies from pure application code point to its own port and the domain. The persistence adapter implements the port; no entity or mapper crosses it. `ConfigurationServiceImpl` converts DTOs at the boundary and translates domain errors to the existing web-facing exceptions. The full transaction includes lookup, validation, reconciliation and save.

**Published DTOs versus domain values.** Existing API types retain Jackson and Bean Validation annotations for compatibility. Domain values have no such dependencies, including no dependency on another module's annotated API types. Explicit boundary mapping costs code but makes that separation visible and testable. JPA entities continue to reuse API enums; both are adapter-side representations, and this reuse does not create an inward dependency from the domain. See [layering and ArchUnit](architecture/layering-and-archunit.md) for the actual enforcement and limitations.

**Generalized rule for new modules:** a new module's `internal` types must never be imported from outside that module. Only `*.api` packages (or an equivalent explicitly-exposed interface) are a stable contract other modules — or other layers, like `web` — are allowed to depend on.

## The Dependency Rule — Go CLI analogue

`tools/cli/ro` doesn't use Modulith, but it has the same shape: `pkg/cmd` holds one file per subcommand (the "outer," user-facing layer — flags, prompts, output formatting), and dedicated single-purpose packages hold the shared logic those commands orchestrate: `pkg/config`, `pkg/execx`, `pkg/fsx`, `pkg/logx`, `pkg/prompt`, `pkg/telemetry`, `pkg/version`.

The Dependency Rule here: **`pkg/cmd` may import the support packages; a support package must never import `pkg/cmd`, and one subcommand file should not reach into another subcommand's internals directly** — shared behavior belongs in a support package, not in a sibling command file. Unlike the Modulith boundary, nothing currently enforces this with a test — it's convention only (see the table below).

## Clean Code — repo-wide (Kotlin, Go, and any future Angular code)

These apply regardless of language; they're the ones cheap enough to check in a code review without deep domain context.

- **Naming reveals intent.** A reader shouldn't need to open the implementation to guess what a name means. Boolean names should read as predicates (`isExposed`, `isUnitConsistent`), not as ambiguous nouns.
- **Functions should do one thing, at one level of abstraction.** `CreateDraftService`/`UpdateDraftService` orchestrate one operation through domain operations and `TournamentStore`; mapping and JPA reconciliation stay in the persistence adapter.
- **Comments explain WHY, never WHAT.** This is already the top-level instruction for this project, and the codebase has a useful example:

  ```kotlin
  // service/tournamentmgmt/.../setup/configuration/web/ConfigurationController.kt
  // !! reproduces the Java record's implicit unboxing/argument NPE on null input
  request.disciplineId!!,
  ```

  This explains a non-obvious _reason_ for code that would otherwise look like a mistake, rather than explaining what the surrounding code does — that's left to be self-evident from names and structure. A comment that only restates the next line in English is a candidate for deletion, not a model to copy.

- **Error handling is a deliberate boundary decision, not an accident.** Exceptions that cross the `web`/`internal` boundary (e.g. `TournamentNotFoundException`, `InvalidDraftUpdateException`) should be mapped intentionally (see `ConfigurationControllerAdvice`), not left to leak a framework- or persistence-specific exception type into the API surface.
- **Tests are documentation of the contract, not just regression insurance.** `TournamentmgmtModuleStructureTest` is the canonical example: reading it tells you the module boundary rules faster than reading the annotations across five files.

## Where this is enforced today vs. aspirational

| Rule | Status |
| --- | --- |
| Modulith `api`/`internal` boundary (`service/tournamentmgmt`) | **Enforced** — `TournamentmgmtModuleStructureTest` fails the build if broken. |
| Domain/application/adapter direction (`service/tournamentmgmt`) | **Enforced** — `LayerDependencyTest`, with populated production selections and forbidden-dependency fixtures. |
| `pkg/cmd` / support-package direction (`tools/cli/ro`) | Convention only — no test today. |
| Naming, function size/SRP, comments-for-WHY, error-handling boundaries | Review-time only — checked by `/code-review`'s Conventions lens against the `AGENTS.md` rules derived from this doc, not by a compiler or test. |

## See also

- `AGENTS.md` — "Architecture Boundaries" (the enforceable one-liners derived from this doc)
- `service/tournamentmgmt/AGENTS.md` — "Architecture Rules"
- `tools/cli/ro/AGENTS.md` — "Local Conventions"
- `docs/wiki/Architecture/Modules.md`, `docs/wiki/Architecture/Tournamentmgmt-Modulith.md`
