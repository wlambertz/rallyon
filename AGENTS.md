# RallyOn Agent Guide

## Scope

- `rallyon` is a mixed-language monorepo for badminton tournament tooling.
- RallyOn is also an educational project for learning modern technologies, architecture practices, tooling, and agent-assisted delivery methods.
- Active implementation areas today:
  - `application/organizer`: placeholder; the Angular frontend was removed while the frontend stack is reconsidered, see `application/organizer/AGENTS.md`
  - `service/tournamentmgmt`: Spring Boot tournament management service
  - `3rd_party/iam`: shared Keycloak security libraries used by backend services
  - `tools/cli/ro`: Go developer CLI
- Placeholder areas exist (`application/audience`, `service/playermgmt`, `service/scoring`) but currently contain no runnable app/service code.

## Educational Project Principle

- Prefer explainable, incremental changes that preserve learning value for the maintainer.
- When introducing a new technology, architecture pattern, workflow, or agent-facing convention, document the reason and tradeoffs close to the change.
- Do not hide important decisions behind unexplained automation; make the method and validation path visible.
- Keep experiments scoped and reversible unless the task explicitly promotes them to the project baseline.

## Issue Implementation Planning

- When asked to create an implementation plan for a GitHub issue, write the plan under `docs/issue-implementation-plans/`, add the same plan as a comment on the issue, and commit the plan file.
- Planning work may inspect code, docs, tests, wiki pages, and issue comments, but must not modify production code, tests, migrations, runtime config, dependency files, or generated app artifacts.
- Plan commits must contain only the relevant plan file and include the agent `Co-authored-by` trailer required by this guide.
- Codex should use `.agents/skills/issue-implementation-planner/`; Cursor and Claude should follow this shared rule through their `AGENTS.md` adapters.

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
- Build shared IAM modules first when backend code depends on them:
  - `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`
- Backend verify: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
- CLI unit tests: `cd tools/cli/ro && go test ./...`

## Validation Contract

- Run the narrowest relevant checks for the area you changed.
- If you touch multiple runtimes, validate each touched runtime.
- Minimum expectations:
  - Docs/scripts-only changes: `npm run format:check`
  - Spring backend or IAM changes: install IAM if needed, then `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
  - Go CLI changes: `cd tools/cli/ro && go test ./...`
- If a heavier check is skipped, say so explicitly in your handoff.

## File Map

- `application/organizer/`: placeholder; no frontend stack is currently in place
- `service/tournamentmgmt/`: Spring Boot 4 service, Spring Modulith, JPA, Flyway, PostgreSQL, OpenAPI
- `3rd_party/iam/`: shared Keycloak core and Spring security auto-configuration
- `admin/keycloak/`: local Keycloak provisioning script and operator notes
- `infrastructure/local/`: local Docker Compose for Keycloak, Postgres, and tournament management service
- `persistence/db/`: Postgres image used by local compose
- `tools/cli/ro/`: Go Cobra CLI for build/test/run/deploy/docs/scaffolding workflows
- `.agents/skills/`: repo-scoped Codex skills for repeatable RallyOn workflows
- `docs/issue-implementation-plans/`: durable implementation plans created from GitHub issues before coding
- `docs/clean-architecture-and-clean-code.md`: Dependency Rule and Clean Code conventions behind the Architecture Boundaries rules below
- `wiki/`: git submodule and expected agent research source for architecture, CLI workflows, personas, and design context

## Architecture Boundaries

- Keep organizer UI concerns in `application/organizer`; do not add backend or auth-server behavior there. Do not introduce a frontend stack there without an explicit decision.
- Keep service module boundaries intact in `service/tournamentmgmt`; `setup.configuration.api` is exposed, `internal` packages are not.
- Dependency Rule (Clean Architecture): within any module boundary in this repo (Modulith `api`/`internal`, or the Go CLI's `cmd`/`pkg` split), inner/contract code must never import outer/detail code. See `docs/clean-architecture-and-clean-code.md`.
- In `tools/cli/ro`, new subcommands belong in `pkg/cmd`; do not add subcommand logic directly inside a shared support package (`pkg/config`, `pkg/execx`, etc.).
- Comments explain WHY a line of code deviates from the obvious approach, never WHAT the code does; see `TournamentMapper.kt`'s `@Suppress("SENSELESS_COMPARISON")` and `ConfigurationController.kt`'s null-unboxing comment as the pattern to follow.
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
- When an agent creates a Git commit, it must add itself as a `Co-authored-by` trailer in the commit message using its agent name and configured noreply/contact address.

## Done Criteria

- Relevant local checks pass, or skipped checks are called out.
- New instructions/docs match actual repo commands.
- No unrelated generated output, secrets, or environment-specific edits are introduced.
