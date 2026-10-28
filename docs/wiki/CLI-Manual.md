# RallyOn CLI Manual

This page documents the RallyOn developer CLI (`ro`) in depth. For a quick cheat sheet of build/install commands, refer to the repository README at `tools/cli/ro/README.md`.

## 1. Overview

- `ro` is a Cobra-based CLI that orchestrates builds, tests, deployments, and common developer workflows (`tools/cli/ro/pkg/cmd/root.go`).
- Commands are grouped by workflow (build/test/run, auth, Docker, Git helpers, deploy, docs, scaffold, telemetry, diagnostics, completion).
- The CLI reads `ro.yaml` plus `RO_*` environment variables, then merges CLI flags at runtime.

## 2. Prerequisites & Installation

### 2.1 Required tooling

Ensure the following are available on your PATH:

- Go toolchain (used to build the CLI and by `ro doctor`).
- Java + Maven wrapper inside `service/tournamentmgmt`.
- Docker CLI/daemon.
- Git CLI.

Validate your environment any time with:

```bash
ro doctor
```

The command runs Go/Java/Docker/Git checks and fails fast if tooling is missing (`tools/cli/ro/pkg/cmd/doctor.go`).

### 2.2 Build from source

```powershell
cd tools/cli/ro
go mod tidy
go build -o ..\..\..\bin\ro.exe .
```

(From `tools/cli/ro/README.md`, adjust paths for non-Windows shells.) Place the resulting binary on your PATH, then verify:

```bash
ro version
```

### 2.3 Shell completion

Install completion for the current shell:

```bash
ro completion install
```

The command detects your shell (bash, zsh, fish, PowerShell) and writes the appropriate script under your home directory (`tools/cli/ro/pkg/cmd/completion_install.go`). Re-run after upgrading the CLI.

## 3. Configuration & Global Controls

### 3.1 `ro.yaml` structure

Configuration lives at the repo root and is parsed by `config.Load` (`tools/cli/ro/pkg/config/config.go`):

- `project` – name and root.
- `paths` – service/app/3rd-party/admin roots.
- `build` – Maven wrapper path and default goals.
- `docker` – workflow file, registry, repo, context, Dockerfile, compose file.
- `deploy` – GitHub repo/workflow, default refs, environment refs, safety gates, poll intervals, default wait, workflow inputs.
- `git` – conventional commit enforcement, default remote/branch.
- `output` – default JSON/verbose/quiet logging toggles.
- `docs` – CLI reference output paths, wiki settings, commit message.
- `scaffold` – template directory, default package, base path.
- `telemetry` – enable flag, endpoint, client ID, command allow list.

Paths are normalized to absolute paths, and defaults allow the CLI to run even without a custom `ro.yaml`.

### 3.2 Environment overrides

All config keys can be overridden via `RO_*` env vars (periods converted to underscores). Examples:

- `RO_DEPLOY_ENVREFS__PROD=main`
- `RO_DOCKER_IMAGEREPO=my-org/tournamentmgmt`
- `RO_TELEMETRY_ENABLED=true`

Set `GITHUB_TOKEN` before running `ro deploy` so GitHub API calls succeed.

### 3.3 Global flags

These options apply to every command (`tools/cli/ro/pkg/cmd/root.go`):

| Flag        | Description |
|-------------|-------------|
| `--verbose` | Force verbose logging (overrides config). |
| `--json`    | Emit JSON-formatted logs. |
| `--dry-run` | Preview actions without executing external processes where supported. |
| `--yes`     | Auto-confirm interactive prompts and bypass certain safeguards. |
| `--quiet`   | Suppress info logs (errors only). |

Combining `--json` and `--quiet` is useful for CI pipelines; `--dry-run` affects build/test/deploy/docker commands and is surfaced in deployment summaries.

## 4. Workflow Guides

### 4.1 Build & Test

- **Build**: `ro build [--fast|--ci]`
  - Default goals come from `build.defaultGoals`.
  - `--fast` swaps in `clean package -DskipTests`.
  - `--ci` forces `clean verify -B`.
  - Runs Maven from `paths.serviceRoot` via `config.MavenExecutablePath` (`tools/cli/ro/pkg/cmd/build.go`).
- **Test**: `ro test`
  - Executes `mvn test` with a one-hour timeout (`tools/cli/ro/pkg/cmd/test.go`).

Use `--dry-run` to print the Maven command without running it.

### 4.2 Running services locally

```bash
ro run service tournamentmgmt --env dev --port 8080
```

Sets `SPRING_PROFILES_ACTIVE` and `SERVER_PORT` before invoking `spring-boot:run` through Maven (`tools/cli/ro/pkg/cmd/run.go`). Leave the command running until interrupted.

### 4.3 Local auth token for Swagger UI

```bash
read -rsp "Keycloak client secret: " RALLYON_CLIENT_SECRET && export RALLYON_CLIENT_SECRET && echo
read -rsp "Developer password: " RALLYON_DEV_PASSWORD && export RALLYON_DEV_PASSWORD && echo
ro auth token --format bearer
```

- Requests a local-development access token from the RallyOn Keycloak realm and prints it to stdout (`tools/cli/ro/pkg/cmd/auth.go`).
- Defaults target the local bootstrap described in `admin/keycloak/README.md`: Keycloak at `http://localhost:8081`, realm `rallyon`, client `rallyon-api`, user `dev.organizer`.
- If your shell cannot reach Keycloak at `http://localhost:8081` (for example from a containerized dev environment), override the endpoint with `--server` or `KEYCLOAK_SERVER`.
- Output formats:
  - `--format raw` prints only the access token.
  - `--format bearer` prints `Bearer <token>` for direct Swagger paste.
  - `--format json` prints token metadata (`access_token`, `token_type`, `expires_in`, `scope`).
- Override connection and credentials with `--server`, `--realm`, `--client-id`, `--client-secret`, `--username`, `--password`.
- Prefer `RALLYON_CLIENT_SECRET` and `RALLYON_DEV_PASSWORD` environment variables over `--client-secret` and `--password` so secrets do not land in shell history.
- The command is intentionally local-dev only. It does not provision Keycloak, persist tokens, or replace production auth flows.
- Typical Swagger flow:
  - `docker compose -f infrastructure/local/docker-compose.yml up -d`
  - `read -rsp "Keycloak client secret: " RALLYON_CLIENT_SECRET && export RALLYON_CLIENT_SECRET && echo`
  - `read -rsp "Developer password: " RALLYON_DEV_PASSWORD && export RALLYON_DEV_PASSWORD && echo`
  - `bash admin/keycloak/provision_keycloak.sh`
  - `ro auth token --format bearer`
  - Open `http://localhost:8080/swagger-ui/index.html`, click **Authorize**, paste the value.

### 4.4 Docker workflows

- **Image build/push**: `ro docker build [flags]`
  - Tags: `--branch-tag`, `--sha-tag`, `--tag <value>`, `--latest`.
  - `--push` pushes every computed tag.
  - Image references combine `docker.registry` and `docker.imageRepo`.
- **Compose**: `ro docker compose (up|down|logs)`
  - Shared flags: `--profile`, `--file`, `--env-file`.
  - `logs` accepts an optional service name.

All commands delegate to the Docker CLI and respect `--dry-run` where possible (`tools/cli/ro/pkg/cmd/docker.go`).

### 4.5 Git helpers

`ro git` wraps common flows with sensible defaults (`tools/cli/ro/pkg/cmd/git.go`):

- `ro git status [--long]` – short or full status.
- `ro git branch [--verbose]` – branch summary plus optional `git branch -vv`.
- `ro git rebase --onto origin/main [--autostash]`.
- `ro git commit` – guided Conventional Commit builder (prompts for type/scope/summary/body, supports `--breaking`, `--wip`, `--all`).
- `ro git push [--force] [--remote <name>] [--branch <name>]` – uses default remote/branch if omitted and enforces clean worktree unless `--yes`.
- `ro git sync` – pulls with rebase against configured upstream.
- `ro git lint-message [file] [--stdin] [--range base..head]` – rejects generic commit messages (Conventional Commit header, concrete summary, body for large changes); used by `.githooks/commit-msg` and CI. `ro git commit` runs the same checks. Thresholds: `git.commitLint` in `ro.yaml`.
- `ro git hooks install` – sets `core.hooksPath` to `.githooks` so the commit-msg hook runs locally.

### 4.6 Deployments (GitHub Actions)

```bash
GITHUB_TOKEN=... ro deploy --env dev --dry-run
```

- Validates `deploy.repo` and `deploy.workflow`, ensures a clean worktree (unless `--yes`), and resolves branch/ref/inputs from config (`tools/cli/ro/pkg/cmd/deploy.go`).
- `--input key=value` may be repeated to override workflow inputs.
- `--wait` toggles workflow monitoring (default honors `deploy.defaultWait`).
- `--check-only` runs safety checks without dispatching the workflow.
- Summaries are printed in JSON-friendly form; telemetry emits command metrics if enabled.

### 4.7 Documentation generation

```bash
ro docs generate --wiki --commit-wiki
```

- Generates `docs/cli-reference.md` (or `docs.output`) plus optional wiki mirrors (`docs.wikiOutput`).
- `--wiki` writes to the vendored `wiki/` directory; `--commit-wiki` stages/commits via `docs.wikiCommitMessage` unless overridden (`tools/cli/ro/pkg/cmd/docs.go`).
- Content is derived from the actual Cobra command tree, ensuring up-to-date help.

### 4.8 Module scaffolding

```bash
ro scaffold module registration \
  --package com.rallyon.tournament.registration \
  --template-set module
```

- Generates Modulith slices using templates under `tools/cli/ro/templates`.
- Flags: `--package`, `--base`, `--template-set`, `--dry-run`, `--force`.
- When `--dry-run` is provided, the command only prints pending writes (`tools/cli/ro/pkg/cmd/scaffold.go`).

### 4.9 Telemetry & diagnostics

- `ro telemetry status|enable|disable` – shows or prints how to toggle telemetry (`tools/cli/ro/pkg/cmd/telemetry.go`).
- `RO_TELEMETRY_ENABLED=true` overrides config for one-off opt-ins.
- `ro doctor` – see §2.1 for details.

### 4.10 Completion & release packaging

- `ro completion install` – installs completion scripts (see §2.3).
- Packaging via GoReleaser:

```bash
cd tools/cli/ro
goreleaser build --snapshot --clean
goreleaser release --clean --skip=publish
```

CI publishes tagged releases through `.github/workflows/ro-release.yml` (`tools/cli/ro/README.md`).

## 5. Environment Variable Reference

| Variable | Purpose |
|----------|---------|
| `RO_PROJECT_ROOT`, `RO_PATHS_SERVICEROOT`, etc. | Override corresponding `ro.yaml` keys. |
| `RO_BUILD_MAVENWRAPPER` | Point to a custom Maven wrapper script. |
| `RALLYON_CLIENT_SECRET`, `RALLYON_DEV_PASSWORD`, `RALLYON_DEV_USERNAME`, `RALLYON_CLIENT_ID`, `RALLYON_REALM`, `KEYCLOAK_SERVER` | Inputs for `ro auth token` local Keycloak access-token requests. |
| `RO_DOCKER_IMAGEREPO`, `RO_DOCKER_REGISTRY`, `RO_DOCKER_COMPOSEFILE` | Control Docker defaults. |
| `RO_DEPLOY_*` | Override deploy repo/workflow/refs/inputs/polling flags. |
| `RO_DOCS_*` | Redirect documentation outputs and wiki behavior. |
| `RO_SCAFFOLD_*` | Adjust template directory, default package, base path. |
| `RO_TELEMETRY_*` | Enable telemetry, set endpoint/client ID, command filters. |
| `GITHUB_TOKEN` | Required for `ro deploy` to call GitHub APIs. |

Unset variables fall back to values defined in `ro.yaml` or internal defaults.

## 6. Troubleshooting & Tips

- **Config errors**: Run `ro config show --json` to inspect the merged configuration (honors env overrides).
- **Dirty worktree**: `ro deploy` and `ro git push` require a clean state unless `--yes` is passed. Run `git status -sb` or `ro git status`.
- **Detached HEAD**: Deploys fail if `HEAD` is detached; ensure you are on a named branch.
- **Docker auth**: If `docker push` fails, log in to the target registry (`docker login ghcr.io`, etc.) before running `ro docker build --push`.
- **Templates missing**: When scaffolding, verify `scaffold.templateDir` and template names; the command errors if no templates are found.

## 7. Regenerating This Manual & CLI Reference

- To refresh the programmatic CLI reference, run `ro docs generate --wiki`; it writes `docs/cli-reference.md` plus the wiki file configured in `docs.wikiOutput`.
- This manual (`wiki/CLI-Manual.md`) is handwritten; keep it in sync with the README and regenerated reference when commands change.

---

_Last reviewed: {{DATE}}. Update this manual whenever command behavior or configuration defaults change._
