# AGENTS Audit

## Proposed Files

- `AGENTS.md`
- `application/organizer/AGENTS.md`
- `service/tournamentmgmt/AGENTS.md`
- `3rd_party/iam/AGENTS.md`
- `tools/cli/ro/AGENTS.md`

## Why Each File Exists

- `AGENTS.md`
  - Gives repo-wide routing, safety rules, and canonical cross-project commands.
  - Needed because this repo is a real monorepo with separate Node, Maven, and Go workflows plus infra/admin areas.
- `application/organizer/AGENTS.md`
  - Organizer has its own Angular/Storybook/Playwright/Tailwind/PrimeNG workflow and local UI/design conventions.
- `service/tournamentmgmt/AGENTS.md`
  - Backend service has distinct architecture rules, Flyway migration discipline, security boundaries, and Maven validation expectations.
- `3rd_party/iam/AGENTS.md`
  - Shared authentication modules are high-risk and affect backend security across the repo.
- `tools/cli/ro/AGENTS.md`
  - CLI work is operationally sensitive and uses a separate Go toolchain and compatibility surface.

## Repo Evidence Used

- Top-level purpose and layout:
  - `README.md`
  - `wiki/Home.md`
  - `wiki/Architecture/Modules.md`
- Root tooling:
  - `package.json`
  - `tools/scripts/check-format.sh`
  - `tools/scripts/prettier-files.mjs`
- CI and release workflows:
  - `.github/workflows/build.yaml`
  - `.github/workflows/Tournamentmgmt-docker.yaml`
  - `.github/workflows/codeql.yml`
  - `.github/workflows/ro-release.yml`
- Organizer app evidence:
  - `application/organizer/package.json`
  - `application/organizer/README.md`
  - `application/organizer/angular.json`
  - `application/organizer/eslint.config.mjs`
  - `application/organizer/playwright.config.ts`
  - `application/organizer/src/app/app.routes.ts`
  - `application/organizer/src/app/core/services/auth.service.ts`
  - `application/organizer/src/styles/README.md`
  - `application/organizer/design/pencil/README.md`
  - `application/organizer/tests/login.spec.ts`
  - `wiki/Personas.md`
  - `wiki/design/frontend-spaceport-theme.md`
- Tournament service evidence:
  - `service/tournamentmgmt/pom.xml`
  - `service/tournamentmgmt/src/main/resources/application.properties`
  - `service/tournamentmgmt/src/main/resources/application-local.properties`
  - `service/tournamentmgmt/src/main/resources/db/migration/V1__create_tournamentmgmt_schema.sql`
  - `service/tournamentmgmt/src/main/resources/db/migration/V2__allow_nullable_fields_for_draft.sql`
  - `service/tournamentmgmt/src/main/resources/db/migration/V3__extend_configuration_persistence.sql`
  - `service/tournamentmgmt/src/main/resources/db/migration/V4__add_bracket_rosters.sql`
  - `service/tournamentmgmt/src/main/resources/db/migration/V5__drop_legacy_venue_address.sql`
  - `service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/TournamentmgmtModuleStructureTest.java`
  - `service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationControllerSecurityTest.java`
  - `service/tournamentmgmt/docs/modulith/README.md`
  - `wiki/Architecture/Tournamentmgmt-Modulith.md`
- IAM evidence:
  - `3rd_party/iam/pom.xml`
  - `3rd_party/iam/keycloak-core/src/main/java/.../KeycloakPrincipalFactory.java`
  - `3rd_party/iam/keycloak-spring-starter/src/main/java/.../KeycloakSecurityAutoConfiguration.java`
- CLI evidence:
  - `tools/cli/ro/README.md`
  - `tools/cli/ro/go.mod`
  - `tools/cli/ro/pkg/cmd/auth_test.go`
  - `tools/cli/ro/pkg/cmd/deploy_test.go`
  - `ro.yaml`
  - `wiki/CLI-Manual.md`
- Infra/admin evidence:
  - `infrastructure/local/docker-compose.yml`
  - `persistence/db/Dockerfile`
  - `service/tournamentmgmt/Dockerfile`
  - `admin/keycloak/README.md`
  - `.devcontainer/devcontainer.json`
  - `.devcontainer/post-create.sh`

## Uncertainties And Assumptions

- `application/audience`, `service/playermgmt`, and `service/scoring` appear to be placeholders only (`.keep`), so no subtree-specific `AGENTS.md` was added there.
- The root `README.md` mentions Angular 20, but `application/organizer/package.json` currently uses Angular `^21.2.x`; the instructions follow the manifest/config rather than the stale README wording.
- No root `Makefile`, Gradle build, pnpm workspace, or repo-wide task runner was found, so none were documented as canonical.
- `codeql.yml` currently analyzes only GitHub Actions, so it was treated as a repo security signal rather than a source of build commands.
- The instruction structure now treats `wiki/` as an explicit agent research source for architecture, workflows, personas, and design context, while code/config remain the source of truth for executable behavior.

## Commands Discovered

- Root:
  - `npm ci`
  - `npm run format`
  - `npm run format:check`
  - `npm run format:check:changed`
  - `npm run organizer:install`
  - `npm run organizer:dev`
  - `npm run organizer:lint`
  - `npm run organizer:test`
  - `npm run organizer:test:ci`
  - `npm run organizer:test:e2e`
- Organizer:
  - `npm install`
  - `npm start`
  - `npm run build`
  - `npm run lint`
  - `npm test`
  - `npm run test:ci`
  - `npm run test:e2e`
  - `npm run storybook`
  - `npm run build-storybook`
  - `npx playwright install --with-deps chromium`
- IAM / backend:
  - `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`
  - `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`
  - `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean package`
  - `./mvnw -B clean verify` from `service/tournamentmgmt/`
  - `./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests`
- CLI:
  - `cd tools/cli/ro && go test ./...`
  - `cd tools/cli/ro && go build -o ../../bin/ro .`
  - `cd tools/cli/ro && goreleaser build --snapshot --clean`
  - `cd tools/cli/ro && goreleaser release --clean --skip=publish`
- Local infra/docs from repo docs:
  - `docker compose -f infrastructure/local/docker-compose.yml up -d keycloak tournamentmgmt-db`
  - `ro doctor`
  - `ro run service tournamentmgmt --env local --port 8080`
  - `bash admin/keycloak/provision_keycloak.sh`

## Notable Risks And Boundaries

- Security and identity:
  - Shared Keycloak libraries in `3rd_party/iam`
  - Keycloak provisioning in `admin/keycloak`
  - JWT audience/issuer/role/user-id-claim contract in backend config and tests
- Database:
  - Flyway migrations in `service/tournamentmgmt/src/main/resources/db/migration`
  - PostgreSQL schema and enum evolution are compatibility-sensitive
- Public API and compatibility:
  - REST endpoints in `service/tournamentmgmt/.../web/ConfigurationController.java`
  - Go CLI commands and flags in `tools/cli/ro/pkg/cmd`
- Delivery and ops:
  - GitHub workflows under `.github/workflows`
  - `ro.yaml` deploy workflow references and safety gates
  - Dockerfiles and `infrastructure/local/docker-compose.yml`
- Docs and generated architecture artifacts:
  - `service/tournamentmgmt/docs/modulith/generated/`
  - `wiki/` is a submodule, so edits there should be deliberate
