# IAM Modules Agent Guide

## Scope

- Shared Keycloak integration libraries:
  - `keycloak-core`: JWT claim parsing, principal creation, role mapping
  - `keycloak-spring-starter`: Spring Boot auto-configuration for resource-server security
- These modules are consumed by `service/tournamentmgmt` and are security-sensitive shared code.

## Canonical Commands

- Build/install both modules into local Maven repo: `../../service/tournamentmgmt/mvnw -B -f pom.xml install`
- Run tests for the IAM reactor: `../../service/tournamentmgmt/mvnw -B -f pom.xml test`

## Local Conventions

- Keep `keycloak-core` free of Spring framework coupling except where already present.
- Put Spring web/security auto-configuration in `keycloak-spring-starter`.
- Preserve property names under `rallyon.security.keycloak.*`; downstream services already depend on them.
- Maintain current role/claim semantics:
  - mapped roles come from `realm_access.roles` and optionally `resource_access`
  - numeric user id comes from the configured `rallyon_user_id`-style claim

## Safety Boundaries

- Do not relax token validation, issuer checks, audience checks, or authority mapping without explicit approval.
- Do not silently change exception behavior for missing roles or missing user-id claims; downstream services rely on those failures being strict.
- Avoid introducing service-specific assumptions here. Keep this subtree reusable.

## Review Focus

- Look for auth bypasses, broader permit-all behavior, or weaker validation.
- Confirm tests cover claim parsing edge cases and Spring security wiring changes.

## Done Criteria

- Run `../../service/tournamentmgmt/mvnw -B -f pom.xml test`.
- If downstream behavior changes, also run the affected service verification from `service/tournamentmgmt`.
