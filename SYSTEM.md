# RallyOn System Policy

## Mission

Help engineers make safe, minimal, high-signal progress in the RallyOn monorepo for badminton tournament operations.

Optimize for:

- preserving working software and compatibility
- respecting bounded contexts in a mixed-language monorepo
- using the repo's real commands, tests, docs, and workflows
- keeping security-sensitive auth, deploy, and data paths trustworthy

## Scope

This policy applies repo-wide.

Active implementation areas today:

- `application/organizer`: Angular organizer portal
- `service/tournamentmgmt`: Spring Boot tournament management service
- `3rd_party/iam`: shared Keycloak auth libraries
- `tools/cli/ro`: Go developer CLI
- supporting ops/docs areas such as `admin/keycloak`, `infrastructure/local`, `persistence/db`, `.github/workflows`, and `wiki/`

Treat these as placeholders unless the task explicitly targets them:

- `application/audience`
- `service/playermgmt`
- `service/scoring`

## Priorities

1. Preserve compatibility of the current system surface.
2. Make the smallest change that solves the task.
3. Follow owning-area boundaries instead of spreading logic across runtimes.
4. Validate changes with the narrowest meaningful checks for each touched runtime.
5. Keep docs and automation aligned with implemented behavior.

## Hard Constraints

### Compatibility

- Preserve REST compatibility for endpoints under `/api/tournamentmgmt/...` unless a breaking change is explicitly requested.
- Preserve `ro` CLI commands, flags, config semantics, workflow references, and safety gates unless a breaking change is explicitly requested.
- Preserve Keycloak contract expectations: issuer, JWKS, audience, role mapping, and `rallyon_user_id` claim handling.
- Preserve database compatibility where practical; Flyway migrations in `service/tournamentmgmt/src/main/resources/db/migration/` are append-only.

### Security and secrets

- Do not weaken auth validation, permit-all behavior, role mapping, or user-id claim requirements without explicit approval.
- Never hardcode real secrets or introduce secret-bearing defaults. Prefer the repo's existing environment variables.
- Treat deploy, docker push, auth-token, workflow-dispatch, and telemetry behavior as high-risk changes.

### Boundaries

- Keep organizer UI concerns in `application/organizer`.
- Keep service logic inside existing Modulith boundaries in `service/tournamentmgmt`; public contracts live in `...api`, `...internal...` packages are implementation details.
- Keep shared Keycloak behavior in `3rd_party/iam`, not duplicated ad hoc in services.
- Do not edit generated or derived artifacts unless the task intentionally regenerates them.

## Tool and Repo Navigation Policy

### Read order

Before editing, identify the owning area and read the closest applicable guidance:

1. this `SYSTEM.md`
2. root `AGENTS.md`
3. the nearest subtree `AGENTS.md`
4. a relevant repo-local `skills/*/SKILL.md` when the task matches a specialized workflow

### Source of truth

- Use code, tests, manifests, config, scripts, and workflow files as the source of truth for current behavior.
- Use `wiki/` for intended architecture, workflow context, personas, and design direction unless code/tests/config contradict it.
- Treat the root `README.md` as helpful background, not the canonical description of the current repo state; parts of it are stale.

### Preferred commands

- Use repo-defined commands and wrappers instead of inventing new ones.
- Root formatting: `npm run format`, `npm run format:check`, `npm run format:check:changed`
- Organizer: root `npm run organizer:*` wrappers or local `npm` scripts in `application/organizer`
- Backend/IAM: `./service/tournamentmgmt/mvnw -B -f ...`
- CLI: `cd tools/cli/ro && go test ./...`
- Use `ro` for RallyOn-specific CLI workflows when the task concerns deploy, docker, docs generation, auth token helpers, or scaffold flows.

## Change Policy

- Prefer minimal diffs over broad cleanup or opportunistic refactors.
- Do not rename modules, packages, directories, routes, command surfaces, or workflow files just for consistency.
- Do not update `wiki/` unless the task explicitly calls for wiki edits or a generated wiki refresh.
- Do not refresh committed Modulith docs, generated CLI reference, Storybook output, or other derived assets unless the task intentionally changes them.
- When docs, automation, or command surfaces materially change, update the nearby authoritative docs in the same workstream.

## Testing and Validation Expectations

- Run the narrowest relevant checks for each touched area.
- If multiple runtimes are touched, validate each touched runtime.
- Minimum expectations:
  - docs/scripts-only: `npm run format:check`
  - organizer UI: `npm run organizer:lint` and `npm run organizer:test:ci`
  - organizer login/routing/shell changes: also `npm run organizer:test:e2e`
  - IAM changes: `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml test`
  - backend changes: install IAM first when needed, then `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
  - CLI changes: `cd tools/cli/ro && go test ./...`
- If you intentionally skip a heavier check, say so explicitly in the handoff.

## Documentation Policy

- Keep instructions aligned with actual repo commands and current behavior.
- Prefer updating the closest maintained doc rather than duplicating guidance in multiple places.
- Use `docs/cli-reference.md` as generated output, not as the design source for CLI behavior.
- Preserve the distinction between repo docs and the `wiki/` submodule; wiki updates are deliberate and may require separate commit handling.

## Uncertainty and Fallback Behavior

- If repo prose and implementation diverge, follow the implementation and tests for behavior, then update docs deliberately if needed.
- If code and wiki diverge, preserve working code unless the task explicitly asks to realign behavior with intended architecture.
- If a change touches security, deploy, database migrations, public API compatibility, or shared auth behavior and the intent is unclear, pause and surface the tradeoff instead of guessing.
- If the repo lacks a convention, use a minimal local default that matches surrounding code and label it as a proposed convention in the handoff.

## Instruction Precedence

Use this precedence order:

1. platform/system/developer instructions from the runtime
2. direct user instructions for the task
3. this `SYSTEM.md`
4. root `AGENTS.md`
5. the nearest subtree `AGENTS.md`
6. explicitly invoked or clearly applicable repo-local `SKILL.md`
7. other repo prose

Within repo content:

- a closer `AGENTS.md` overrides a broader one for that subtree
- a relevant `SKILL.md` provides workflow-specific guidance and should not rewrite repo-wide policy
- code/tests/config beat prose for executable behavior
- wiki beats prose docs for intended architecture and workflow framing unless contradicted by code/tests/config

## Boundaries: What Belongs in Skills Instead

Keep `SYSTEM.md` stable and global. Put specialized, fast-changing workflows in Skills instead, especially:

- organizer UI implementation and Pencil/Storybook sync
- tournament service changes, Modulith conventions, and migration workflows
- shared Keycloak/auth changes and local Keycloak bootstrap
- `ro` CLI command development, release packaging, and deploy flows
- cross-cutting repo routing for tasks spanning multiple active areas
