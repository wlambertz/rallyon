---
name: keycloak-auth
description: work on RallyOn authentication, shared Keycloak integration, and local auth bootstrap. Use for changes in 3rd_party/iam, Keycloak issuer or audience handling, role or claim mapping, rallyon_user_id behavior, local realm provisioning, ro auth token flows, or service auth failures that may originate in shared auth code. Preserve strict validation and reuse the existing provisioning and local-token workflow.
---

# Keycloak Auth

Use this skill for security-sensitive auth work spanning `3rd_party/iam`, local Keycloak bootstrap, and related service integration.

## Read before editing

1. [3rd_party/iam/AGENTS.md](../../../3rd_party/iam/AGENTS.md)
2. [admin/keycloak/README.md](../../../admin/keycloak/README.md)
3. [infrastructure/local/docker-compose.yml](../../../infrastructure/local/docker-compose.yml)
4. [wiki/CLI-Manual.md](../../../wiki/CLI-Manual.md) section on `ro auth token` when the task touches token retrieval or local testing

Inspect these implementation points as needed:

- [admin/keycloak/provision_keycloak.sh](../../../admin/keycloak/provision_keycloak.sh)
- [tools/cli/ro/pkg/cmd/auth.go](../../../tools/cli/ro/pkg/cmd/auth.go)

## Safety rules

- Do not relax token validation, issuer checks, audience checks, role mapping, or `rallyon_user_id` claim requirements without explicit approval.
- Preserve property names under `rallyon.security.keycloak.*`.
- Do not silently change failures for missing roles or missing user-id claims.
- Keep shared IAM code reusable; do not bake service-specific assumptions into `3rd_party/iam` unless the repo already does.

## Local bootstrap flow

For local auth verification, reuse the repo’s existing flow:

1. `docker compose -f infrastructure/local/docker-compose.yml up -d`
2. Export required secrets such as `RALLYON_CLIENT_SECRET`
3. `bash admin/keycloak/provision_keycloak.sh`
4. `ro auth token --format bearer`
5. Test via Swagger UI or service endpoints

Prefer env vars over CLI flags for secrets so they do not land in shell history.

## Validation

- IAM module tests: `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml test`
- If downstream behavior changes, also run: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
