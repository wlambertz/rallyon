# Test strategy

This is an inventory of the checks currently implemented, plus explicit gaps. The [SDLC catalog](../.agents/skills/sdlc-skills/SKILL.md) describes possible practices; its tools and metrics are not automatically present in RallyOn.

## Current coverage

- **Pure behavior tests:** JUnit, AssertJ and Mockito exercise Kotlin/Java logic. Draft domain/application tests use a memory implementation of `TournamentStore` and a fixed JDK `Clock`, without Spring or persistence.
- **Mapping and Java interoperability:** Java mapper and service tests remain alongside Kotlin tests. API tests check JSON shape and Bean Validation placement.
- **Spring and HTTP:** Spring Boot context/actuator tests and MockMvc security/controller tests run in the normal Maven test phase. MockMvc is in-process HTTP-boundary testing, not a deployed end-to-end environment.
- **Persistence:** `DraftPersistenceIntegrationTest` exercises real JPA persistence on H2, retained child identities, replacement, rollback, stale versions and concurrent-write detection. Tests use `src/test/resources/application.properties`: H2 in PostgreSQL mode, Hibernate `create-drop`, and **Flyway disabled**. They do not validate PostgreSQL migrations or production database behavior.
- **Architecture:** `TournamentmgmtModuleStructureTest` runs Modulith verification. `LayerDependencyTest` checks production domain/application/API/web dependencies and verifies representative forbidden-dependency fixtures. See [layering enforcement](architecture/layering-and-archunit.md).
- **Go CLI:** `go test ./...` under `tools/cli/ro` runs the CLI unit suite separately from Java CI.

## Discovery and execution

Tests live under `service/tournamentmgmt/src/test/kotlin` and `src/test/java`. Both `*Test` and `*Tests` names occur and are discovered by Maven Surefire; the suffix alone does not distinguish unit from integration coverage. No separate Failsafe `*IT` pipeline is configured. Existing Java tests intentionally provide interoperability coverage and should not be converted just for naming consistency.

From the repository root:

```bash
# Install IAM when its local artifacts are missing or changed.
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
npm run format:check
```

For focused work, use Maven `-Dtest=TestName` before full verification. `TournamentmgmtDocumentationTests` produces committed architecture exports only when intentionally invoked with `-Dmodulith.docs=true`; ordinary validation should not regenerate those artifacts.

## Current CI

`.github/workflows/ci.yaml` runs on pushes to `main` and PRs targeting `main`. One build/test job installs IAM and runs service Maven `verify`, including unit, Spring, persistence and architecture tests. It uploads Surefire XML results even on failure. The job's label calls it **Gate 1**; this is a CI label, not refinement gate G1 in the [feature lifecycle](feature-lifecycle-gates.md).

There is no configured nightly/pre-release Gate 2 regression suite, performance suite or separate E2E stage. Do not report those as passed or required checks that already exist.

## Gaps and future work

- PostgreSQL/Testcontainers testing and Flyway migration execution are absent. H2 compatibility mode cannot prove migration or PostgreSQL-specific behavior.
- No automated UI suite is present; the organizer app is a placeholder with no selected stack.
- No dedicated deployed E2E, REST Assured contract suite, performance suite or nightly full-regression workflow is configured.
- No JaCoCo coverage gate, detekt/ktlint enforcement, or DORA/Grafana metrics pipeline is established by these tests.
- UAT currently uses the local Docker Compose environment and explicit manual evidence, not a dedicated staging environment.

Prefer focused, deterministic tests for behavior and boundaries. Record commands, outcomes and remaining limitations in the PR. For flaky checks, retain failure evidence and track the cause; a skipped or quarantined check must be reported, never silently counted as passing coverage.
