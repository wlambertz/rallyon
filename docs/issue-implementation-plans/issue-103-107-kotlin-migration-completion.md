# Issues #103–#107: Complete the tournamentmgmt Kotlin migration (one go)

## Context

Issue #102 (API value types → Kotlin `@JvmRecord`) is merged. This plan executes the remaining five milestone issues in one branch/PR, one commit-phase per issue, full `clean verify` at each phase. End state: **no production Java except the 9 `package-info.java` files** (Modulith annotation carriers).

**User decision (Jackson/DTO style):** add the Jackson 3 Kotlin module — `tools.jackson.module:jackson-module-kotlin` (GA, version-managed at 3.1.4 by Boot 4.1's imported Jackson BOM; Boot auto-registers it, `spring.jackson.find-and-add-modules` defaults `true`). New request DTOs in #103 become **plain idiomatic `data class`es** (no `@JvmRecord`). The existing `@JvmRecord` API types stay as-is; simplifying them is a follow-up issue to file at the end.

## Verified facts that anchor the design

- Boot parent 4.1.0, Kotlin 2.3.21, MapStruct 1.6.3; kotlin-maven-plugin has `spring`+`jpa` presets, allopen+noarg deps already declared; no kapt yet.
- `ConfigurationServiceImplTest` asserts **NPE type + exact messages** ("Tournament name must not be null" …) with null passed from Java → keep `Objects.requireNonNull(x, msg)` verbatim; type null-checked params as nullable so Kotlin intrinsics never fire first. Never substitute `requireNotNull` (IAE) / `checkNotNull` (ISE) — wrong exception types.
- `TournamentMapperTest` (Java) uses entity setters/getters; `UpdateDraftServiceTest` (Kotlin) uses property syntax and asserts ~5 exact exception messages → entity `var` properties satisfy both; mapper messages byte-identical.
- `@ManyToOne(LAZY)` targets (`TournamentEntity`, `DisciplineEntity`, `BracketEntity`) need Hibernate proxies → entities must be open: the `spring` all-open preset does NOT cover `@Entity`.
- Un-targeted Java annotations on class-**body** Kotlin properties land on the FIELD (no Kotlin property target; `@Id/@Column/...` all have FIELD) → matches today's field-access JPA; no `@field:` needed on entities.
- `Mappers.getMapper(TournamentMapper::class.java)` used by both mapper tests → generated impl keeps public no-arg ctor (mapper has no deps).
- kapt-generated `TournamentMapperImpl.java` lands in `target/generated-sources/kapt/compile` and is compiled by the existing `java-compile` execution → that execution must survive #107.

## Phase 0 — repo convention

Write this plan to `docs/issue-implementation-plans/issue-103-107-kotlin-migration-completion.md`, commit it alone (Co-authored-by trailer), post it as a comment on issues #103, #104, #105, #106, #107. Branch: `feature/103-107-complete-kotlin-migration`.

## Phase 1 (#103) — web layer

Convert [ConfigurationController.java](service/tournamentmgmt/src/main/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationController.java) and `ConfigurationControllerAdvice.java` → Kotlin under `src/main/kotlin`.

- **pom:** add `tools.jackson.module:jackson-module-kotlin` (no version — BOM-managed). Boot auto-registers it.
- 9 nested request DTOs → plain `data class` (NOT `@JvmRecord`), **all fields nullable** to preserve the Java records' pass-through-null behavior (absent JSON field must not become a new 400/500).
- Controller: constructor injection (`private val configurationService`, `principalProvider`); endpoints annotation-for-annotation identical (paths, verbs, `@ResponseStatus(NO_CONTENT)` on `validate` returning Unit, `ResponseEntity<Tournament>` with 201 on create). Optional `@RequestParam(required=false)` → nullable types; path vars/If-Match `long` → non-null `Long`. `actingUserId()`: `principalProvider.requirePrincipal().userId().orElseThrow { IllegalStateException("Token missing numeric rallyon_user_id claim.") }` (`userId()` returns `OptionalLong`).
- `request.disciplineId!!` (2 sites) reproduces Java's unboxing NPE; if the Sonar gate objects, use `?: throw NullPointerException("...")`.
- Advice: `@RestControllerAdvice(assignableTypes = [ConfigurationController::class]) internal class`, 3 `@ExceptionHandler`s returning `ProblemDetail.forStatusAndDetail(status, exception.message)` with `title = status.reasonPhrase` — 400/404/409 unchanged. Handler methods public.
- Accepted delta: with the Kotlin module registered, deserialization failures on non-null params of existing `@JvmRecord` body types surface as Kotlin-aware exceptions instead of `ValueInstantiationException` — both map to HTTP 400; no test pins the cause.

Gate: `ConfigurationControllerSecurityTest` + full verify. Commit.

## Phase 2 (#104) — application, config, services, exceptions

Convert: `TournamentmgmtApplication` (`@SpringBootApplication @Modulith class` + top-level `fun main` with `runApplication<...>(*args)`), `OpenApiConfiguration`, `PostgresDataSourceCustomizer` (instanceof → `is` smart cast; keep 3-step `isPostgres` fallback chain exactly), `ConfigurationService` + `ConfigurationServiceImpl` (2 real methods with verbatim `Objects.requireNonNull` messages; 21 `UnsupportedOperationException` stubs), `CreateDraftUseCase`/`UpdateDraftUseCase`, `CreateDraftService`/`UpdateDraftService` (`@Component @Transactional internal class`; static validate helpers → file-level `private fun`; keep every exception message byte-identical), 3 exceptions (`class X(message: String) : RuntimeException(message)`; `TournamentNotFoundException(tournamentId: Long)` message "Tournament $id was not found" — asserted in test), `RuleService`/`PhasesService`/`Phase` (keep `@Service` on interfaces), `TimeWindowRange` (annotation class, `@Target(CLASS, TYPE)`, `@Constraint(validatedBy=[...])`, defaults incl. `KClass` arrays) + `TimeWindowRangeValidator`.

**Nullability contract:** every reference-typed param in `ConfigurationService`, use-case interfaces, and impls becomes nullable; Java primitives stay non-null `Long`. Keep all 9 `package-info.java`.

Gate: `ConfigurationServiceImplTest` (exact NPE messages), `UpdateDraftServiceTest`, `TournamentmgmtModuleStructureTest`, `ModulithActuatorEndpointTest`, `ApiConstraintPlacementTest` + full verify. Commit.

## Phase 3 (#105) — JPA entities

Convert 7 entities + `TournamentRepository` (`interface TournamentRepository : JpaRepository<TournamentEntity, Long>`). Template: [TournamentEntity.java](service/tournamentmgmt/src/main/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/persistence/entity/TournamentEntity.java).

- Plain `class` (NOT data class), `var` properties, bare JPA annotations (field-targeted automatically):
  - boxed `Long id` → `var id: Long? = null`; primitives → `var organizerId: Long = 0`, `var sortOrder: Short = 0`; all reference/boxed fields → nullable `= null` (incl. `@Version var version: Long? = null`)
  - collections → `var courts: MutableList<CourtEntity> = ArrayList()` (mapper mutates in place via `clear()/add()/removeIf()`)
- **pom:** add to kotlin-maven-plugin `<configuration>`:
  ```xml
  <pluginOptions>
      <option>all-open:annotation=jakarta.persistence.Entity</option>
      <option>all-open:annotation=jakarta.persistence.MappedSuperclass</option>
      <option>all-open:annotation=jakarta.persistence.Embeddable</option>
  </pluginOptions>
  ```
  (no-arg ctors already covered by the `jpa` preset). Same commit as the entities.
- Mapper is still Java this phase — works because kotlinc runs before javac within `compile`.
- Check boot-test logs for Hibernate proxy warnings (lazy relations on final classes).

Gate: `TournamentMapperTest` + `UpdateDraftServiceTest` unchanged, context-load tests (H2+Flyway validates mappings) + full verify. Commit.

## Phase 4 (#106) — MapStruct mapper on kapt

Convert `TournamentMapperConfig` (annotations verbatim) and `TournamentMapper` → Kotlin **abstract class**, **1:1 mechanical conversion** (tests pin messages/state, not idioms — an idiomatic rewrite risks silent drift where coverage is thin):

- `@Mapping`/`@BeanMapping`/`@Named` annotations verbatim; `expression = "java(...)"` stays valid (generated impl is Java).
- `protected` helpers stay `protected` (final fine — impl only calls, never overrides them); private helpers private.
- Keep java streams/`Collectors.toMap(..., LinkedHashMap::new)` verbatim where duplicate-key/order semantics matter (NO `associateBy` — different duplicate-key behavior); `Map.put` return-value duplicate detection stays `.put(...)` (not `[]=`); `(short) i` → `i.toShort()`; string templates OK but messages byte-identical.
- `private enum class RosterKind`; `private data class BracketRosterTarget(val bracket: BracketEntity, val teamSize: TeamSize?)`.
- Helper param nullability mirrors Java implicit nullability (e.g. `toLocale(localeValue: String?): Locale?`).
- **pom:** add kapt execution as first kotlin-maven-plugin execution:
  ```xml
  <execution>
      <id>kapt</id>
      <goals><goal>kapt</goal></goals>
      <configuration>
          <sourceDirs>
              <sourceDir>${project.basedir}/src/main/kotlin</sourceDir>
              <sourceDir>${project.basedir}/src/main/java</sourceDir>
          </sourceDirs>
          <annotationProcessorPaths>
              <annotationProcessorPath>
                  <groupId>org.mapstruct</groupId>
                  <artifactId>mapstruct-processor</artifactId>
                  <version>${mapstruct.version}</version>
              </annotationProcessorPath>
          </annotationProcessorPaths>
      </configuration>
  </execution>
  ```
  Pipeline: kapt (process-sources) generates `TournamentMapperImpl.java` → kotlin compile → `java-compile` compiles the generated impl. Leaving javac `annotationProcessorPaths` in place this phase is harmless (no Java `@Mapper` sources left); removal is #107.
- Fallback if K2 kapt stub issues: `-Xuse-kapt3` arg; worst case keep mapper as sole `.java` and report.

Gate: `TournamentMapperTest`, `UpdateDraftServiceTest`, verify exactly one generated impl exists, full verify. Commit.

## Phase 5 (#107) — cleanup + docs

- **pom removals:** `org.projectlombok:lombok` dependency, `lombok-mapstruct-binding.version` property, entire javac `<annotationProcessorPaths>` block, lombok `<excludes>` in spring-boot-maven-plugin. **Keep** `java-compile`/`java-test-compile` executions (compile package-info.java + kapt output + Java tests) and `src/main/java` sourceDirs.
- Narrow `com.fasterxml.jackson.module:jackson-module-kotlin` (Jackson 2) to `<scope>test</scope>` — only the security test's hand-built Jackson 2 mapper uses it.
- Docs: update `service/tournamentmgmt/AGENTS.md` (Kotlin-first note, kapt/MapStruct pipeline, Java tests kept deliberately as interop gates); add outcome note to `service/tournamentmgmt/docs/kotlin-migration-build-tool-decision.md`; root `AGENTS.md` only if command surfaces changed (they didn't).
- Run `TournamentmgmtDocumentationTests` with `-Dmodulith.docs=true`; commit regenerated Modulith docs only if diffed.
- File follow-up GitHub issue: "Simplify @JvmRecord API value types to idiomatic data classes now that Jackson 3 Kotlin module is registered" (needs Java-test accessor updates; out of scope here).
- `npm run format:check:changed`.

Gate: full verify + `clean package` (Docker CI parity). Commit.

## Finish

Push, open one PR to `main`: "Complete tournamentmgmt Kotlin migration (#103–#107)" with `Closes #103/#104/#105/#106/#107`. Watch CI incl. SonarCloud gate (expect possible findings on `!!`; fix reactively as in #102).

## Verification summary

1. Per phase: `bash service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` (IAM snapshot already installed).
2. Final: `clean package`, `npm run format:check:changed`, `git grep -l "\.java$"`-style audit that only package-info.java remain under src/main/java, one generated MapperImpl, PR checks green.

## Key risks

| Risk | Mitigation |
| --- | --- |
| kapt (maintenance mode) + MapStruct 1.6.3 stub fidelity (param names for `toEntityForCreate` @Mapping sources) | battle-tested pairing; fallback `-Xuse-kapt3`; caught at phase-4 compile |
| Lazy proxies on final Kotlin entities | all-open @Entity option in same commit; watch boot logs |
| Exact-message parity (3 test classes) | verbatim `Objects.requireNonNull` + literal messages; no Kotlin require/check |
| Kotlin module changes deser error shape for @JvmRecord body types | still HTTP 400; no test pins cause |
| Sonar new-code gate | avoid `!!` outside 2 documented parity sites |
