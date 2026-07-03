# Issue #101: [Kotlin migration 03] Prove Kotlin test interop before production migration

## Summary

Prove that Kotlin test sources compile, run, and interoperate with the current Java tournament management service before any production classes are migrated. Keep the work test-only and use the Java 21 Kotlin build baseline already present in `service/tournamentmgmt/pom.xml`; this issue should not change Java versions, dependency versions, production APIs, persistence mappings, or runtime configuration.

## Implementation Changes

### Tournament management tests

- Add `service/tournamentmgmt/src/test/kotlin` as the Kotlin test source location by placing converted tests under the same package paths that the existing Maven Kotlin plugin already compiles.
- Convert `service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/usecase/UpdateDraftServiceTest.java` to `service/tournamentmgmt/src/test/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/usecase/UpdateDraftServiceTest.kt`.
  - Keep the existing test method names and assertion intent behavior-equivalent.
  - Keep Mockito and JUnit 5; do not introduce MockK.
  - Preserve the current mock `TournamentRepository` interaction checks.
  - Preserve direct calls to `UpdateDraftService`, Java record-style API DTOs, entity classes, and `Mappers.getMapper(TournamentMapper::class.java)` so the Kotlin test proves service, repository, Java record, and generated MapStruct mapper interop.
  - Use Kotlin syntax only where needed for Java interop, such as Java record component calls, class literals, collection construction, nullable values, and Mockito `any`/`thenAnswer` handling.
- Convert `service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/TournamentmgmtApplicationTests.java` to `service/tournamentmgmt/src/test/kotlin/dev/wlambertz/rallyon/tournamentmgmt/TournamentmgmtApplicationTests.kt`.
  - Keep the existing `@SpringBootTest` context-load behavior and test name.
  - Do not add unrelated assertions or change Spring test configuration.
- Remove only the two replaced Java test files after their Kotlin equivalents are compiling.

### Non-goals

- Do not migrate production Java classes to Kotlin.
- Do not change database migrations, Modulith module boundaries, REST endpoints, security behavior, generated Modulith docs, or Keycloak contracts.
- Do not change the Java baseline away from 21. Java 21 remains the more compatible Kotlin baseline for this repository than Java 25, and issue #101 should validate tests against that baseline rather than revisit it.
- Do not add MockK unless the Mockito conversion exposes a concrete blocker that cannot be solved cleanly.

## Validation

- If local shared IAM artifacts are missing:

```bash
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
```

- Run the full tournament management service verification:

```bash
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
```

- Confirm the final diff contains only the two Java-to-Kotlin test conversions and any required formatting for those files.

## Risks / Compatibility

- Kotlin and Mockito can require careful handling of Java generics and nullable matchers; keep the conversion minimal and prefer the existing Mockito API over a mocking-library change.
- Java records are exposed to Kotlin through generated accessor methods, so assertions may need explicit accessor calls rather than Kotlin property-style access where ambiguity appears.
- The MapStruct mapper is generated from Java annotation processing; the converted unit test should keep resolving it through `Mappers.getMapper(TournamentMapper::class.java)` to prove the generated Java mapper remains callable from Kotlin.
- Maven Surefire must discover compiled Kotlin `*Test` classes. Keeping class names and JUnit 5 annotations unchanged reduces discovery risk.

## Assumptions

- Issue #100's Kotlin build foundation is already merged into this branch.
- The current Maven configuration for `src/test/kotlin` is authoritative; no new dependency or plugin configuration is expected for this issue.
- The existing Spring Boot context test remains the smallest useful Spring integration proof point for Kotlin test discovery.
- Local full verification may still depend on available IAM snapshots and external artifact resolution, so any environmental failure should be reported separately from Kotlin interop failures.
