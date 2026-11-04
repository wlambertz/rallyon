# Java 25 preflight and blocker

## Outcome

Blocked: retain the committed Java 21 compilation targets. A clean PostgreSQL database does not receive the shipped Flyway migrations, so an authenticated draft creation fails. The same failure was reproduced with Java 21 and Java 25 service bytecode, both running on the existing Java 25 runtime; it is therefore not evidence of a Java 25 bytecode regression. No baseline configuration has been changed.

## Environment and successful checks

- Isolated Linux arm64 container: Temurin 25.0.5+8, Maven 3.9.14; source copied to a temporary directory, Maven cache copied from the host.
- Maven offline with `-Djava.version=25 -Dmaven.compiler.release=25`: IAM `install` passed (12 tests); service `clean verify` passed (89 tests, one skipped), including kapt, mapper, security, persistence and Modulith tests. Mockito warned about dynamic self-attachment on future JDKs.
- Effective service model: Spring Boot 4.1.2, Kotlin 2.4.21, compiler 3.15.0, Surefire 3.5.6, MapStruct 1.6.3, Byte Buddy 1.18.15, Mockito 5.23.0. Representative IAM Java and service Kotlin class files inspected with `javap -verbose` reported major version 69.
- A service image built from that jar starts against isolated PostgreSQL 17; `/actuator/health` returns 200/UP. An unauthenticated draft request returns 401. The authenticated request uses an ephemeral RSA test key, local JWKS endpoint, expected issuer/audience and organizer claims; it reaches persistence but returns 500.
- A temporary dev-container Dockerfile differing only in its Java toolchain image was built for `linux/amd64`; `docker run --rm --platform linux/amd64 rallyon-devcontainer:java25 java -version` reported Temurin 25.0.5. Editor configuration and the complete dev-container lifecycle were not changed or validated.

## Reproduction and control

1. Build IAM and service on JDK 25 with `-Djava.version=25 -Dmaven.compiler.release=25`, then build the service Dockerfile using the resulting jar.
2. Start an empty PostgreSQL database and the image with `SPRING_PROFILES_ACTIVE=local` and matching `TOURNAMENTMGMT_DB_*` variables. Check `/actuator/health`; submit authenticated `POST /api/tournamentmgmt/config/drafts?organizerId=42` with `{"name":"Java 25 smoke","visibility":"PRIVATE"}`.
3. The response is HTTP 500; PostgreSQL reports `SQLState 42P01: relation "tournamentmgmt.tournaments" does not exist`. Querying `pg_tables` shows no tables in `tournamentmgmt`. No Flyway execution appears in service startup logs.
4. Rebuild the same source with `-Djava.version=21 -Dmaven.compiler.release=21` (service class major 65), run it on the same Java 25 image against a second empty database, and repeat the request. It returns the same HTTP 500 and missing-relation error. This control used existing cached IAM artifacts and skipped tests; it establishes a pre-existing baseline failure, not a complete Java 21 validation.

The first attempted authenticated organizer-list request also returned 500 because `ConfigurationServiceImpl.listByOrganizer` is explicitly unimplemented; the draft request above is the relevant implemented-path reproduction.

## Smallest candidate remedy and deferral

The packaged service contains Flyway core 12.4.0 and its PostgreSQL module but no `spring-boot-flyway` integration jar; Boot 4.1.2's BOM manages `spring-boot-flyway` and `spring-boot-starter-flyway`. Investigate adding the appropriate Boot integration and a PostgreSQL migration regression test. This is a candidate, not a verified fix; it introduces a dependency and changes database startup behavior, both outside the approved scope and requiring human review. Do not alter shipped migrations.

Resume the Java baseline upgrade after the migration startup problem is resolved and a clean-database authenticated smoke test passes. The complete dev-container lifecycle and actual GitHub CI/security/SBOM results remain pending; no CI run for this change has been claimed. The repository format check also reports an unrelated existing formatting problem in `openspec/changes/issue-203/.lifecycle.json`.
