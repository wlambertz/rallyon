---
name: tournamentmgmt-service
description: change the Spring Boot tournament management service in service/tournamentmgmt. Use for controllers, API DTOs, use cases, persistence, Flyway migrations, Modulith boundaries, OpenAPI-adjacent work, or service-level security changes. Preserve public API compatibility, respect exposed api vs internal packages, keep Flyway append-only, and validate with the Maven verify flow.
---

# Tournament Management Service

Use this skill for work in `service/tournamentmgmt/`.

## Read before editing

1. [service/tournamentmgmt/AGENTS.md](../../service/tournamentmgmt/AGENTS.md)
2. [wiki/Architecture/Modules.md](../../wiki/Architecture/Modules.md)
3. [wiki/Architecture/Tournamentmgmt-Modulith.md](../../wiki/Architecture/Tournamentmgmt-Modulith.md)
4. [service/tournamentmgmt/docs/modulith/README.md](../../service/tournamentmgmt/docs/modulith/README.md) when the task affects architecture docs or module boundaries

## Architecture rules

- Preserve Modulith boundaries checked by `TournamentmgmtModuleStructureTest`.
- Exposed contracts live in:
  - `...setup.configuration.api`
  - `...setup.rules.api`
  - `...setup.phases.api`
- Treat `...internal...` packages as implementation details.
- Keep controllers in `...web`, use cases/services in `...internal...usecase`, and persistence in `...internal...persistence`.
- Treat `/api/tournamentmgmt/config` as compatibility-sensitive.

## Database and migration rules

- Migrations live in [service/tournamentmgmt/src/main/resources/db/migration](../../service/tournamentmgmt/src/main/resources/db/migration).
- Add a new `V{n}__...sql` file for schema changes.
- Do not rewrite shipped migrations unless the task explicitly says to repair an unreleased migration.
- Favor backward-compatible schema changes.

## Security rules

- The service is an OAuth2 resource server backed by shared Keycloak code in `3rd_party/iam`.
- Do not weaken issuer, audience, role mapping, or `rallyon_user_id` claim handling.
- Security-sensitive endpoint changes should keep or extend test coverage similar to [service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationControllerSecurityTest.java](../../service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationControllerSecurityTest.java).

## Validation

If the change depends on shared IAM artifacts, install them first:

`./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`

Then run:

`./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`

Regenerate Modulith docs only when intentionally refreshing them:

`./service/tournamentmgmt/mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests`
