# Issue #102: [Kotlin migration 04] Migrate public immutable API value types

## Summary

Convert the immutable public API records and enums in `setup.configuration.api` and `setup.rules.api` of `service/tournamentmgmt` to Kotlin in one pass, preserving compatibility for Java callers, Jackson JSON shape, Bean Validation behavior, and Spring Modulith named interfaces. The Kotlin build foundation (#116/#118/#129) is already in place; these become the first production Kotlin sources in the service.

**Critical constraint:** Spring Boot 4.1 runs Jackson 3 (`tools.jackson`) for MVC; the declared `jackson-module-kotlin` is a Jackson 2 module and is not registered in the runtime mapper. Plain Kotlin data classes would therefore break JSON handling. `@JvmRecord` is mandatory so the compiled classes are genuine Java records that Jackson 3, Jackson 2 (tests), MapStruct, and springdoc introspect via record components, keeping the JSON shape identical.

## Scope

Convert to Kotlin (new files under `service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/`, delete the 21 Java originals):

- `setup/configuration/api/` (16): `Category`, `RegistrationPolicy`, `SchedulingPolicy`, `TeamSize`, `TournamentFormat`, `TournamentStatus`, `Visibility`, `BracketId`, `Capacity`, `TimeWindow`, `Court`, `ParticipantsRoster`, `Venue`, `BracketConfig`, `DisciplineConfig`, `Tournament`
- `setup/rules/api/` (5): `CourtAllocationPolicy`, `MatchDurationPolicy`, `SeedingPolicy`, `ScoringRules`, `TieBreakRules`

Stays Java (out of scope): `package-info.java` files (Modulith `@NamedInterface("api")`), `ConfigurationService`/`RuleService`/`PhasesService`, `Phase` marker interface, `TimeWindowRange` + `TimeWindowRangeValidator`, `ConfigurationController` request records (#103), `TournamentMapper` (#106), JPA entities (#105). Lombok stays in the pom (entities use it); only `Tournament`'s `@Builder` goes away.

## Design rules

1. Records become `@JvmRecord data class` so Java callers (`TournamentMapper`, `ConfigurationController`, `UpdateDraftService`, Java tests) keep `t.name()`-style record accessors unchanged.
2. Nullability: a constructor parameter is non-null in Kotlin only where the Java compact constructor enforced it (`BracketId.value`; `BracketConfig.id/displayName/format`; `DisciplineConfig.category/displayName/teamSize/brackets`; `ScoringRules.type`; `TieBreakRules.type`). Everything else stays nullable, including `@NotNull`-annotated fields, because the Java records accepted null there and MapStruct/tests pass null.
3. Bean Validation uses explicit `@field:` use-site targets (`@field:NotBlank`, `@field:NotNull`, `@field:Positive`, `@field:Size`, `@field:Valid`). The existing `-Xannotation-default-target=param-property` flag places untargeted annotations where Hibernate Validator does not read them; Kotlin does not replicate javac's record-component propagation. `@AssertTrue` validator methods stay as functions (`@AssertTrue(message=...) fun isXxx(): Boolean`). The class-level `@TimeWindowRange` annotation goes on the class as-is.
4. Static factories become companion-object functions with `@JvmStatic` (`BracketId.of`, `DisciplineConfig.of`, `ScoringRules.twoByTwentyOne/threeByFifteen/custom`, `TieBreakRules.headToHead/pointsRatio/swissStrength/custom`).
5. Compact-constructor validation logic moves to `init { require(...) }` blocks with the same messages.
6. `Tournament` (28 components, exact order preserved): all reference fields nullable defaulting to null; the four primitives default `Long = 0`. A hand-written builder replaces Lombok, preserving `Tournament.builder().id(42L).build()` for Java tests and MapStruct's builder detection: companion `@JvmStatic fun builder(): Builder` plus a `Builder` class with 28 fluent component-named setters and `build()`.
7. Must-preserve specifics:
   - `BracketId`: `override fun toString(): String = value` (Map-key serialization depends on it); blank-check in `init`.
   - `TeamSize`: `enum class TeamSize(@JvmField val size: Int)` preserves public field access.
   - `ScoringRules`/`TieBreakRules`: nested `enum class Type(internal val presetSpec: PresetSpec?)` plus `internal data class PresetSpec` (Kotlin outer classes cannot access nested-class `private`; `internal` keeps it out of the public API). Preset equality via data-class `equals` matches record semantics.
   - `DisciplineConfig`: `List.copyOf(brackets)` cannot happen in a `@JvmRecord` primary constructor; the defensive copy moves into the `of()` factory (accepted semantic delta for direct-constructor callers).
   - `Venue`: nested `@JvmRecord data class Address` with the `@field:Size` postal-code constraint.
   - `Court`, `Capacity`: nested enums stay nested.

## Other changes

- `src/test/kotlin/.../usecase/UpdateDraftServiceTest.kt`: Kotlin callers cannot invoke Kotlin properties as functions, so record-accessor calls become property access (`updated.name()` → `updated.name`); mechanical and compiler-guided. Java tests need no changes.
- `service/tournamentmgmt/pom.xml`: add test-scoped `org.springframework.boot:spring-boot-starter-validation`.
- New test `src/test/kotlin/.../setup/configuration/api/ApiConstraintPlacementTest.kt`: today no runtime path exercises these constraints (the controller has no `@Valid` and no validator is on the classpath), so a mis-targeted annotation would go unnoticed. The test builds a `Validator` via `Validation.buildDefaultValidatorFactory()` and asserts violations for a field constraint (`Court` blank label), an `@AssertTrue` method (`Capacity` unit inconsistency), the class-level `@TimeWindowRange`, the `Address` postal-code `@Size`, and one cascading `@field:Valid` case through `DisciplineConfig.brackets`. This is the one deliberate scope addition, directly serving the issue's acceptance criterion that validation annotations remain effective.

## Accepted semantic deltas

- Data-class `toString()`/`hashCode()` formats differ from Java records (logging only; `BracketId` keeps its override).
- Java passing null into a non-null parameter throws a Kotlin-intrinsic NPE instead of `requireNonNull` NPE / IAE; no caller does this.
- Deserialization failures on non-null parameters surface as different exception types but still map to HTTP 400.

## Execution order

Checkpoint command: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean test-compile`

1. All 10 enums (both packages), delete Java originals, checkpoint, commit.
2. Leaf records: `BracketId`, `Capacity`, `TimeWindow`, `Court`, `ParticipantsRoster`, `Venue`; checkpoint (first MapStruct-against-Kotlin-records signal), commit.
3. Composites and rules: `BracketConfig`, `DisciplineConfig`, `ScoringRules`, `TieBreakRules`; checkpoint, commit.
4. `Tournament` + Builder; fix `UpdateDraftServiceTest.kt`; add pom test dependency and `ApiConstraintPlacementTest.kt`; full verify, commit.
5. Push branch, open PR to `main` referencing #102.

## Validation

- If local shared IAM artifacts are missing:

```bash
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
```

- Full service verification:

```bash
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
```

- Key gates: `TournamentMapperTest` (MapStruct constructor + builder mapping, `BracketId` map-key equality), `ConfigurationControllerSecurityTest` (Jackson round trip / JSON parity), `UpdateDraftServiceTest`, `ConfigurationServiceImplTest` (builder), `TournamentmgmtModuleStructureTest` (Modulith named interfaces), `TournamentmgmtDocumentationTests`, new `ApiConstraintPlacementTest`.
- Bytecode spot-check: `javap -v -p` on `Court.class` (expect the `Record` attribute and `NotBlank` on the `label` field) and `Tournament.class` (28 record components).
- `npm run format:check:changed`.

## Risks

- MapStruct reading binary Kotlin records: caught earliest at step 2 by `TournamentMapperImpl` generation.
- If kotlinc rejects default parameter values on `@JvmRecord`: drop the defaults; the Builder alone preserves every call site.
- Duplicate-class hazard: a type must never exist in both source trees simultaneously; delete each `.java` in the same step its `.kt` replacement is created.
