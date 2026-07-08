# Issue #131: Simplify @JvmRecord API value types to idiomatic Kotlin data classes

## Context

Issues #102–#107 migrated `service/tournamentmgmt` to Kotlin. During that work, the public API
value types under `setup.configuration.api` and `setup.rules.api` were kept as `@JvmRecord data
class`es because, at the time, Jackson 3 (Spring Boot 4.1 MVC) had no Kotlin module — `@JvmRecord`
was required so the compiled classes stayed genuine `java.lang.Record`s that Jackson 3, MapStruct,
and springdoc could introspect via record components.

The Jackson 3 Kotlin module (`tools.jackson.module:jackson-module-kotlin`, `pom.xml`) is now
registered and auto-picked-up by Boot (`spring.jackson.find-and-add-modules` defaults `true`), so
the app's HTTP request/response path already deserializes/serializes idiomatic Kotlin data classes
correctly — that capability was added specifically to unblock this issue. This plan drops
`@JvmRecord` from the remaining value types and fixes the two things that *do* still depend on the
Java-record shape: MapStruct's constructor-based mapping and one Java test's accessor style.

Branch: `feature/131-simplify-jvmrecord-api-types` (already created off `origin/main`).

## Inventory: every `@JvmRecord` in scope

12 declarations across 11 files (`grep -rn "@JvmRecord" setup/configuration/api setup/rules/api`):

| Type | File | Fields | Constructed by |
| --- | --- | --- | --- |
| `TimeWindow` | `configuration/api/TimeWindow.kt` | `start`, `end` | MapStruct `@Mapping` (`toTimeWindow`) |
| `Court` | `configuration/api/Court.kt` | `id`, `label`, `availability`, `type` | MapStruct `@Mapping` (`toCourt`) |
| `DisciplineConfig` | `configuration/api/DisciplineConfig.kt` | `id`, `category`, `displayName`, `teamSize`, `brackets` | MapStruct `@Mapping` (`toDisciplineConfig`) |
| `BracketConfig` | `configuration/api/BracketConfig.kt` | `id`, `displayName`, `format`, `capacity` | MapStruct `@Mapping` (`toBracketConfig`) |
| `Venue` | `configuration/api/Venue.kt` | `name`, `address`, `peopleCapacity` | hand-written Kotlin in `TournamentMapper.toVenue` |
| `Venue.Address` (nested) | `configuration/api/Venue.kt` | `streetWithNumber`, `postalCode`, `city` | hand-written Kotlin |
| `Capacity` | `configuration/api/Capacity.kt` | `amount`, `unit` | hand-written Kotlin |
| `BracketId` | `configuration/api/BracketId.kt` | `value` | hand-written Kotlin / `of()` factory |
| `ParticipantsRoster` | `configuration/api/ParticipantsRoster.kt` | `playerIds`, `teamIds` | hand-written Kotlin |
| `ScoringRules` | `rules/api/ScoringRules.kt` | `type`, `pointsPerGame`, `gamesPerMatch`, `winByTwo`, `capPoints` | hand-written Kotlin / preset factories |
| `TieBreakRules` | `rules/api/TieBreakRules.kt` | `type`, `useSetDifference`, `usePointsRatio`, `useBuchholz` | hand-written Kotlin / preset factories |
| `Tournament` | `configuration/api/Tournament.kt` | 21 fields | hand-written `Tournament.Builder` (MapStruct calls the builder, not the constructor) |

Enums referenced by these types (`Category`, `TeamSize`, `TournamentStatus`, `Visibility`,
`TournamentFormat`, `RegistrationPolicy`, `SchedulingPolicy`, `SeedingPolicy`,
`CourtAllocationPolicy`, `MatchDurationPolicy`) are already plain `enum class`es — not records,
nothing to change.

## Verified facts that anchor the design

- **The real risk is narrow and compile-time-visible, not four-corners-of-the-app.** Only the 4
  types MapStruct constructs via `@Mapping`/`@BeanMapping` reflection (`TimeWindow`, `Court`,
  `DisciplineConfig`, `BracketConfig`) depend on record-component reflection. Inspecting today's
  generated `target/generated-sources/kapt/compile/.../TournamentMapperImpl.java` confirms
  MapStruct currently resolves real parameter names (`start`, `end`, `id`, `label`, ...) from the
  compiled `.class` files' `Record` attribute. Once `@JvmRecord` is gone, that attribute disappears
  and MapStruct falls back to whatever parameter-name info the compiled bytecode carries — which
  requires the `-java-parameters` kotlinc flag (not currently set; today's `kotlin-maven-plugin`
  `<args>` only has `-Xannotation-default-target=param-property`). `TournamentMapperConfig` sets
  `unmappedTargetPolicy = ReportingPolicy.ERROR`, so if parameter names are *not* resolved, the
  build fails loudly at the `kapt`/annotation-processing step — this cannot silently produce wrong
  runtime behavior, only a build break to fix before merging.
- **`Tournament` is constructed via its hand-written `Builder`, not directly by MapStruct
  reflection** (`toApi` calls `Tournament.builder().id(...).version(...)...build()`). The builder's
  setter methods are plain Kotlin functions with explicit parameter names in source — MapStruct's
  builder detection is unaffected by `@JvmRecord`. Converting `Tournament` itself carries no
  MapStruct risk.
- **7 of the 12 types are constructed by hand-written Kotlin code inside `TournamentMapper.kt`**
  (`Venue(...)`, `Capacity(...)`, `TimeWindow(...)` in `toSchedule`, etc.) rather than MapStruct
  reflection — Kotlin constructor calls are resolved at compile time regardless of `@JvmRecord`, so
  these carry no MapStruct risk either. (`TimeWindow` is both hand-constructed in one place and
  MapStruct-mapped in `toTimeWindow`; it counts as a MapStruct-risk type.)
- **Bean Validation (`@field:...`) placement is unaffected.** Every constrained property already
  uses an explicit `@field:` use-site target (e.g. `@field:NotBlank val name`), which pins the
  annotation to the JVM field regardless of whether the class is a record. `ApiConstraintPlacementTest`
  exists specifically to lock this in and should keep passing unchanged — it is a regression gate,
  not something this migration needs to touch.
- **`ConfigurationControllerSecurityTest`'s hand-rolled Jackson 2 `ObjectMapper` only *serializes*
  (`writeValueAsString`) a `Tournament` built via the Java-facing `Builder`.** Jackson's write path
  uses standard JavaBean getter introspection (`getName()`, `getVisibility()`, ...) for both real
  records and plain classes — it does not require the Jackson 2 Kotlin module for serialization,
  only for deserializing into non-default constructors. This test is expected to keep passing
  unchanged once `@JvmRecord` is dropped from `Tournament`; this must be verified empirically
  (Phase 4 gate), not just asserted, since it directly answers issue #134's open question.
- **37 Java record-accessor call sites** (`tournament.name()`, `venue.address()`, `court.label()`,
  etc.) in `TournamentMapperTest.java` will become invalid once the corresponding type drops
  `@JvmRecord` (Kotlin's default JVM property ABI is `getName()`/`getXxx()`, not the Java-record
  `name()` convention). These are updated per-phase, alongside the type each accessor belongs to.
  `ConfigurationServiceImplTest.java` and `ConfigurationControllerSecurityTest.java` only ever call
  `Tournament.builder()...build()` — no record accessors — so they need no accessor-style edits.
- **`DisciplineConfig`'s companion `of()` defensive-copy factory and `BracketId`'s `init` block**
  are unrelated to `@JvmRecord` and stay as-is; MapStruct already bypasses `of()` and calls the
  primary constructor directly (confirmed in the generated impl), matching today's behavior.

## Phase 0 — repo convention

Write this plan to `docs/issue-implementation-plans/issue-131-simplify-jvmrecord-api-types.md`,
commit it alone (`Co-authored-by` trailer), post it as a comment on issue #131.

## Phase 1 — build-config spike: prove the MapStruct mitigation on the smallest type

- Add `-java-parameters` to the `kotlin-maven-plugin`'s existing `<args>` block in
  `service/tournamentmgmt/pom.xml` (alongside `-Xannotation-default-target=param-property`) so
  compiled Kotlin constructors retain real parameter names for annotation processors.
- Drop `@JvmRecord` from `TimeWindow` only (2 fields, MapStruct-mapped via `toTimeWindow` — the
  smallest and cheapest place to fail fast if the mitigation doesn't work).
- Update `TournamentMapperTest.java`: `schedule.start()` → `schedule.getStart()`,
  `schedule.end()` → `schedule.getEnd()` (2 call sites; leave `tournament.schedule()` as-is —
  `Tournament` is still `@JvmRecord` at this point).
- **Gate:** `bash service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`.
  Additionally inspect the regenerated
  `target/generated-sources/kapt/compile/.../TournamentMapperImpl.java`'s `toTimeWindow` method —
  confirm it still assigns `start`/`end` by name (not `arg0`/`arg1`, and not a compile failure).
  If parameter names are *not* resolved, fall back to adding
  `@ConstructorProperties("start", "end")` (`java.beans.ConstructorProperties`) directly on
  `TimeWindow`'s constructor before proceeding to Phase 2 — do not carry an unresolved spike
  forward.
- Commit.

## Phase 2 — remaining MapStruct-mapped types

Now that Phase 1 has proven (or adjusted) the mitigation, drop `@JvmRecord` from `Court`,
`DisciplineConfig`, `BracketConfig`.

- Update `TournamentMapperTest.java` accessors: `mappedCourt.label()` → `.getLabel()`,
  `mappedCourt.type()` → `.getType()`, `mappedDiscipline.id()` → `.getId()`,
  `mappedDiscipline.brackets()` → `.getBrackets()`,
  `mappedDiscipline.brackets().get(0).displayName()` → `.getDisplayName()`.
- **Gate:** full `clean verify`; spot-check the regenerated `toCourt`, `toDisciplineConfig`,
  `toBracketConfig` methods in `TournamentMapperImpl.java` the same way as Phase 1.
- Commit.

## Phase 3 — hand-constructed leaf types (no MapStruct risk)

Drop `@JvmRecord` from `Venue` (+ nested `Address`), `Capacity`, `BracketId`,
`ParticipantsRoster`, `ScoringRules`, `TieBreakRules`. These are never built through MapStruct
reflection, so this phase is mechanical.

- Update `TournamentMapperTest.java` accessors: `venue.name()` → `.getName()`,
  `venue.address()` → `.getAddress()`, `venue.address().city()` → `.getAddress().getCity()`,
  `venue.peopleCapacity()` → `.getPeopleCapacity()`, `tournament.capacity().amount()` →
  `tournament.getCapacity().getAmount()` (leave `tournament.capacity()` itself for Phase 4),
  `scoringRules.pointsPerGame()` → `.getPointsPerGame()`, `scoringRules.type()` → `.getType()`,
  `tieBreakRules.type()` → `.getType()`, `roster.playerIds()` → `.getPlayerIds()`.
- **Gate:** full `clean verify`.
- Commit.

## Phase 4 — `Tournament` itself

Drop `@JvmRecord` from `Tournament`. The hand-written `Builder` needs no changes (setter methods
already use explicit parameter names and are unrelated to `@JvmRecord`).

- Update the remaining `TournamentMapperTest.java` accessors on `tournament.*()`: `id`, `version`,
  `name`, `description`, `locale`, `schedule`, `venue`, `registrationWindows`, `courts`,
  `disciplines`, `capacity`, `registrationPolicy`, `seedingPolicy`, `matchDurationPolicy`,
  `scoringRules`, `tieBreakRules`, `participants`, `bracketRosters` → `getXxx()` equivalents.
- **Gate:** full `clean verify`, and explicitly confirm two things by name (don't just rely on the
  aggregate test count):
  1. `ConfigurationControllerSecurityTest` passes unchanged — this is the empirical answer to
     issue #134's open question ("does the Jackson 2 rationale still hold"). Capture the answer
     for the PR description / issue #134 comment either way.
  2. `ApiConstraintPlacementTest` passes unchanged — confirms `@field:` validation placement
     survived the conversion.
- Commit.

## Phase 5 — cleanup, verification, follow-through

- Confirm no `@JvmRecord` remains: `grep -rn "@JvmRecord" service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/api service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/rules/api` returns nothing.
- Re-read `service/tournamentmgmt/AGENTS.md` for any statement tied to `@JvmRecord`/Java-record
  behavior of these API types; update only if something has gone stale (a quick check during
  Phase 0 found no such statement today — the file only mentions `TournamentMapper`/kapt and the
  two named Java test interop gates, neither of which changes).
- Comment on issue #134 with the empirical finding from Phase 4 about
  `ConfigurationControllerSecurityTest` (whichever way it landed), since #134 was explicitly
  waiting on this issue.
- `npm run format:check:changed`.
- **Gate:** full `clean verify` + `clean package` (Docker CI parity, matching the #103–107 plan's
  convention).

## Finish

Push, open one PR to `main`: "Simplify @JvmRecord API value types to idiomatic Kotlin data
classes" with `Closes #131`. Watch CI incl. SonarCloud gate.

## Verification summary

1. Per phase: `bash service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
   (IAM snapshot already installed; run
   `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install` first if missing).
2. Phases 1–2: additionally inspect the regenerated `TournamentMapperImpl.java` for the touched
   mapping method to confirm real parameter names were resolved.
3. Phase 4: explicitly confirm `ConfigurationControllerSecurityTest` and
   `ApiConstraintPlacementTest` pass, not just the aggregate test count.
4. Final: `clean package`, `npm run format:check:changed`, `git grep -n "@JvmRecord"` under
   `setup/configuration/api` and `setup/rules/api` returns nothing, PR checks green.

## Key risks

| Risk | Mitigation |
| --- | --- |
| MapStruct can't resolve Kotlin constructor parameter names for `TimeWindow`/`Court`/`DisciplineConfig`/`BracketConfig` once they stop being real Java records | `-java-parameters` kotlinc flag, spiked on the smallest type (Phase 1) before touching the other three; `@ConstructorProperties` fallback if the flag alone doesn't work; `unmappedTargetPolicy = ERROR` makes any failure a loud compile break, not a silent runtime bug |
| `ConfigurationControllerSecurityTest`'s hand-rolled Jackson 2 mapper stops serializing `Tournament` correctly | Analysis indicates serialization (write-path, getter-based) is unaffected by dropping `@JvmRecord`; verified empirically in Phase 4, not assumed — result feeds back into issue #134 |
| 37 Java record-accessor call sites in `TournamentMapperTest.java` missed during a phase, causing a compile break | Each phase's accessor updates are scoped exactly to the types converted in that phase; `clean verify` gate per phase catches any miss immediately (Java won't compile against a removed record accessor) |
| Sonar new-code gate | No `!!`/unsafe patterns introduced by this migration; existing patterns in `TournamentMapper.kt` are untouched |

## Explored/ruled out

- Converting `TournamentMapperTest.java` to Kotlin instead of updating its accessors: rejected —
  it's a documented interop gate (`service/tournamentmgmt/AGENTS.md`); converting it would remove
  the cross-language coverage it exists to provide, and the accessor-style edit is mechanical and
  small (37 call sites across a single file).
- Reordering phases to do `Tournament` first: rejected — `Tournament` is the largest/most central
  type and carries no MapStruct risk (builder-based), so it's more valuable as the last, easiest
  phase after the risky spike (Phase 1) is already resolved.
