---
name: rallyon-repo-guide
description: route and execute work in the RallyOn monorepo. Use when a task is repo-wide, spans multiple runtimes, or does not clearly belong to organizer UI, tournamentmgmt backend, shared Keycloak auth, or the ro CLI. Follow the nearest AGENTS.md, read the relevant wiki pages before architecture or workflow changes, choose the smallest compatible change, and run the narrowest required validation for each touched area.
---

# RallyOn Repo Guide

Use this skill first when the request is ambiguous, cross-cutting, or touches more than one active area.

## Start here

1. Read [AGENTS.md](../../../AGENTS.md).
2. Identify the owning area:
   - `application/organizer`
   - `service/tournamentmgmt`
   - `3rd_party/iam`
   - `tools/cli/ro`
3. Read the closer `AGENTS.md` for each touched area before editing.
4. Read relevant wiki pages before changing architecture, CLI semantics, docs, personas, or UX framing.

## Source of truth

- Treat code, manifests, configs, scripts, and tests as authoritative for current behavior.
- Treat `wiki/` as authoritative for intended architecture, workflows, personas, and design direction unless code/tests contradict it.
- Do not rely on the root `README.md` for current structure; it is stale.

## Active areas

- Organizer UI: [application/organizer/AGENTS.md](../../../application/organizer/AGENTS.md)
- Tournament service: [service/tournamentmgmt/AGENTS.md](../../../service/tournamentmgmt/AGENTS.md)
- Shared auth: [3rd_party/iam/AGENTS.md](../../../3rd_party/iam/AGENTS.md)
- Developer CLI: [tools/cli/ro/AGENTS.md](../../../tools/cli/ro/AGENTS.md)

Ignore placeholder domains unless the user explicitly wants to create them:

- `application/audience`
- `service/playermgmt`
- `service/scoring`

## Validation contract

- Docs or scripts only: `npm run format:check`
- Organizer UI: `npm run organizer:lint` and `npm run organizer:test:ci`
- Backend or IAM: `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install` when needed, then `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
- Go CLI: `cd tools/cli/ro && go test ./...`

If multiple runtimes are touched, validate each one. If a heavier check is skipped, call it out explicitly.

## Safety rules

- Prefer the smallest diff that solves the task.
- Preserve REST compatibility under `/api/tournamentmgmt/...`, JWT claim expectations, role names, DB compatibility, and `ro` CLI flags unless the task explicitly allows a break.
- Never rewrite shipped Flyway migrations.
- Never hardcode secrets; prefer the env vars already documented in the repo.
