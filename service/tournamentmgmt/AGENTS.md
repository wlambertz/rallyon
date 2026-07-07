# Tournament Management Service Agent Guide

## Scope

- Spring Boot 4 service for tournament configuration and lifecycle management.
- Uses Spring Modulith, Spring Web, JPA, Flyway, PostgreSQL, Actuator, OpenAPI, and shared Keycloak auth from `3rd_party/iam`.
- Production code is Kotlin-first: `src/main/java` contains only `package-info.java` files (Spring Modulith `@NamedInterface`/`@ApplicationModule` annotation carriers). Write new production code in `src/main/kotlin` unless a change is specifically about those package-info files.
- `TournamentMapper` (MapStruct) is compiled via the `kotlin-maven-plugin` `kapt` goal, not javac annotation processing. Lombok is not a dependency of this service.
- Some tests intentionally stay Java (e.g. `TournamentMapperTest`, `ConfigurationServiceImplTest`) as cross-language interop gates alongside Kotlin tests under `src/test/kotlin`. Do not convert a Java test to Kotlin unless the task asks for it.

## Canonical Commands

- Install shared IAM artifacts first when needed: `./mvnw -B -f ../../3rd_party/iam/pom.xml install`
- Run full verification: `./mvnw -B clean verify`
- Package jar: `./mvnw -B clean package`
- Generate Modulith docs only when intentionally refreshing them:
  - `./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests`

## Architecture Rules

- Read wiki architecture context before restructuring modules or exposing new boundaries:
  - `../../wiki/Architecture/Modules.md`
  - `../../wiki/Architecture/Tournamentmgmt-Modulith.md`
- Preserve Modulith boundaries verified by `TournamentmgmtModuleStructureTest`.
- Exposed contracts live in `...setup.configuration.api`, `...setup.rules.api`, and `...setup.phases.api`.
- Treat `...internal...` packages as non-public implementation details.
- Keep web adapters in `...web`, use cases/services in `...internal...usecase`, and persistence in `...internal...persistence`.
- The controller surface under `/api/tournamentmgmt/config` is compatibility-sensitive.
- If wiki architecture intent conflicts with the actual module/test structure, follow the code and tests, then update docs deliberately if needed.

## Database And Migration Rules

- Flyway is enabled and migrations live in `src/main/resources/db/migration`.
- Add new versioned SQL files for schema changes; do not edit existing `V1`-`V5` migrations unless the task is explicitly repairing an unreleased migration.
- Default schema is `tournamentmgmt`; preserve schema qualification patterns.
- Be careful with enum changes, constraints, and not-null transitions because existing draft lifecycle flows rely on them.

## Security Rules

- The service is an OAuth2 resource server via the shared Keycloak starter.
- Do not weaken audience, issuer, role mapping, or `rallyon_user_id` claim requirements without explicit authorization.
- Permit-all endpoints are intentionally limited to health/info, Swagger/OpenAPI, and local modulith actuator endpoints.
- Security-sensitive controller changes should keep or extend test coverage similar to `ConfigurationControllerSecurityTest`.

## Local Environment

- Local defaults come from `src/main/resources/application.properties` plus `application-local.properties`.
- Local stack:
  - Keycloak on `:8081`
  - Postgres on `:5432`
  - Service on `:8080`
- Compose file: `../../infrastructure/local/docker-compose.yml`

## Risk Notes

- Changes to `ConfigurationController`, API DTOs, persistence entities, or mapper logic can affect stored drafts and public API behavior.
- Authentication changes here may actually belong in `../../3rd_party/iam`; check there before duplicating security code.
- `docs/modulith/generated/` is committed generated architecture output. Update it only when intentionally regenerating Modulith docs.

## Done Criteria

- Run `./mvnw -B clean verify`.
- If you changed authentication behavior, security config, or exposed endpoints, inspect or extend relevant tests.
- If you regenerated Modulith docs, include the generated files intentionally and mention that in the handoff.
