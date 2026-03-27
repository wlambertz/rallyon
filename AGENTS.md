# RallyOn Agent Guide

## Scope

- `rallyon` is a mixed-language monorepo for badminton tournament tooling.
- Active implementation areas today:
  - `application/organizer`: Angular organizer portal
  - `service/tournamentmgmt`: Spring Boot tournament management service
  - `3rd_party/iam`: shared Keycloak security libraries used by backend services
  - `tools/cli/ro`: Go developer CLI
- Placeholder areas exist (`application/audience`, `service/playermgmt`, `service/scoring`) but currently contain no runnable app/service code.

## Read This First

- Check for a closer `AGENTS.md` before changing files in:
  - `application/organizer/`
  - `service/tournamentmgmt/`
  - `3rd_party/iam/`
  - `tools/cli/ro/`
- Use the repo wiki as a normal research source, not just a submodule to avoid touching:
  - read relevant pages in `wiki/` before making architecture, CLI workflow, docs, persona, or UX-flow assumptions
  - treat code, config, manifests, and scripts as authoritative for commands, runtime behavior, and current implementation details
  - treat the wiki as authoritative for intended architecture, workflow context, personas, and higher-level system framing unless contradicted by code or config
- Prefer the smallest change that solves the task.
- Do not clean up unrelated files, generated docs, or placeholders unless the task explicitly asks for it.
- Preserve existing public APIs, CLI behavior, DB schema compatibility, and Keycloak contract unless the change explicitly authorizes a break.

## Canonical Commands

- Install root formatting deps: `npm ci`
- Check tracked-file formatting: `npm run format:check`
- Check changed-file formatting: `npm run format:check:changed`
- Format changed files: `npm run format`
- Organizer install: `npm run organizer:install`
- Organizer dev server: `npm run organizer:dev`
- Organizer lint: `npm run organizer:lint`
- Organizer unit tests: `npm run organizer:test`
- Organizer CI unit tests: `npm run organizer:test:ci`
- Organizer e2e smoke: `npm run organizer:test:e2e`
- Build shared IAM modules first when backend code depends on them:
  - `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`
- Backend verify: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
- CLI unit tests: `cd tools/cli/ro && go test ./...`

## Validation Contract

- Run the narrowest relevant checks for the area you changed.
- If you touch multiple runtimes, validate each touched runtime.
- Minimum expectations:
  - Docs/scripts-only changes: `npm run format:check`
  - Organizer UI changes: `npm run organizer:lint` and `npm run organizer:test:ci`
  - Spring backend or IAM changes: install IAM if needed, then `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
  - Go CLI changes: `cd tools/cli/ro && go test ./...`
- If a heavier check is skipped, say so explicitly in your handoff.

## File Map

- `application/organizer/`: Angular 21 standalone app, Tailwind v4, PrimeNG, Storybook, Playwright
- `service/tournamentmgmt/`: Spring Boot 4 service, Spring Modulith, JPA, Flyway, PostgreSQL, OpenAPI
- `3rd_party/iam/`: shared Keycloak core and Spring security auto-configuration
- `admin/keycloak/`: local Keycloak provisioning script and operator notes
- `infrastructure/local/`: local Docker Compose for Keycloak, Postgres, and tournament management service
- `persistence/db/`: Postgres image used by local compose
- `tools/cli/ro/`: Go Cobra CLI for build/test/run/deploy/docs/scaffolding workflows
- `wiki/`: git submodule and expected agent research source for architecture, CLI workflows, personas, and design context

## Architecture Boundaries

- Keep organizer UI concerns in `application/organizer`; do not add backend or auth-server behavior there.
- Keep service module boundaries intact in `service/tournamentmgmt`; `setup.configuration.api` is exposed, `internal` packages are not.
- Treat `3rd_party/iam` as shared platform code. Changes there affect backend authentication behavior across services.
- Do not add assumptions about `application/audience`, `service/playermgmt`, or `service/scoring`; they are placeholders today.

## Safety Boundaries

- Auth and identity:
  - Keycloak issuer, JWKS, audience, role mapping, and `rallyon_user_id` claim handling are security-sensitive.
  - Never hardcode real secrets. Prefer environment variables already used in docs/config.
- Database and migrations:
  - Flyway SQL in `service/tournamentmgmt/src/main/resources/db/migration/` is append-only. Add a new `V{n}__...sql`; do not rewrite shipped migrations.
  - Favor backward-compatible schema changes unless explicitly asked otherwise.
- Deploy and infra:
  - `ro.yaml`, `.github/workflows/`, Dockerfiles, compose files, and `admin/keycloak/` impact delivery environments. Keep diffs intentional and minimal.
- Wiki:
  - `wiki/` is both a submodule and an expected research source.
  - Read relevant wiki pages before planning architecture, CLI, or UX changes.
  - Only update wiki content when the task explicitly requires wiki edits or generated docs there.

## Change Policy

- Prefer minimal diffs over broad restructures.
- Preserve backward compatibility for:
  - REST endpoints under `/api/tournamentmgmt/...`
  - JWT claim expectations and role names
  - `ro` CLI commands and flags
- Do not rename directories, packages, or modules just for consistency.
- Update nearby docs when command surfaces, env vars, workflows, or developer setup materially change.

## Done Criteria

- Relevant local checks pass, or skipped checks are called out.
- New instructions/docs match actual repo commands.
- No unrelated generated output, secrets, or environment-specific edits are introduced.
