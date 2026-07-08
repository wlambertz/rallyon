# `ro` CLI Agent Guide

## Scope

- Go-based developer CLI for RallyOn workflows: app orchestration, build/test/run, Docker, auth token helpers, docs generation, deploy helpers, and scaffolding.
- This subtree is operationally sensitive because it can trigger deploy, docker push, workflow dispatches, and credentialed auth flows.

## Canonical Commands

- Unit tests: `go test ./...`
- Local build: `go build -o ../../bin/ro .`
- Multi-platform snapshot build: `goreleaser build --snapshot --clean`
- Release dry run: `goreleaser release --clean --skip=publish`
- Show command surface locally: `../../bin/ro --help` after building

## Local Conventions

- Keep command implementations under `pkg/cmd`.
- Shared helpers belong in the dedicated support packages (`pkg/config`, `pkg/execx`, `pkg/fsx`, `pkg/logx`, `pkg/prompt`, `pkg/telemetry`, `pkg/version`).
- Dependency Rule: `pkg/cmd` may import the support packages; a support package must never import `pkg/cmd`, and one subcommand file must not reach into another subcommand's internals directly. See `../../../docs/clean-architecture-and-clean-code.md`.
- Respect `ro.yaml` as the canonical project config for paths, workflows, deploy defaults, app scripts, and conventional-commit behavior.
- Read `../../wiki/CLI-Manual.md` before changing command semantics, workflow descriptions, config behavior, or docs generation expectations.
- Preserve command-line compatibility unless the task explicitly approves a breaking change.
- If the wiki and implementation diverge, preserve the implemented CLI behavior and update docs intentionally rather than changing behavior to match stale prose.

## Safety Boundaries

- Treat deploy, docker push, auth token, and telemetry behavior as high-risk.
- Prefer environment variables for secrets (`GITHUB_TOKEN`, `RALLYON_CLIENT_SECRET`, `RALLYON_DEV_PASSWORD`, etc.); do not add flags that encourage secrets in shell history unless the repo already does so.
- Keep default safety gates intact:
  - deploy clean-tree checks
  - branch/ref guards
  - green-build checks
- Do not change workflow filenames or repo identifiers in `ro.yaml` casually; other commands use them directly.

## Review Focus

- Verify tests around branch guards, API calls, auth token resolution, and output formatting when touching command behavior.
- Be careful with scaffolding/template changes because they affect newly generated backend modules.

## Done Criteria

- Run `go test ./...`.
- Rebuild `../../bin/ro` if the task needs a local smoke check.
- Mention clearly if you changed command flags, generated docs behavior, or deploy/auth defaults.
