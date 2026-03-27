---
name: ro-cli
description: maintain the RallyOn ro developer CLI and its workflow contracts. Use for changes in tools/cli/ro, ro.yaml, CLI docs generation, release packaging, deploy or docker wrappers, git helpers, auth token commands, or command-surface UX. Preserve command compatibility unless explicitly approved, keep docs in sync with the actual Cobra tree, and follow ro safety gates for deploy, docker, auth, and telemetry behavior.
---

# ro CLI

Use this skill for work on `tools/cli/ro`, `ro.yaml`, and CLI-adjacent docs.

## Read before editing

1. [tools/cli/ro/AGENTS.md](../../tools/cli/ro/AGENTS.md)
2. [wiki/CLI-Manual.md](../../wiki/CLI-Manual.md)
3. [tools/cli/ro/README.md](../../tools/cli/ro/README.md)
4. [ro.yaml](../../ro.yaml)

Use [docs/cli-reference.md](../../docs/cli-reference.md) as generated output, not as the design source.

## Working rules

- Keep command implementations under [tools/cli/ro/pkg/cmd](../../tools/cli/ro/pkg/cmd).
- Shared helpers belong in support packages like `pkg/config`, `pkg/execx`, `pkg/fsx`, `pkg/logx`, `pkg/prompt`, `pkg/telemetry`, and `pkg/version`.
- Preserve command-line compatibility unless the task explicitly allows a breaking change.
- If docs and implementation diverge, preserve implemented behavior and update docs intentionally instead of changing behavior to match stale prose.

## Safety focus

- Treat deploy, docker push, auth token, and telemetry behavior as high risk.
- Prefer environment variables for secrets such as `GITHUB_TOKEN`, `RALLYON_CLIENT_SECRET`, and `RALLYON_DEV_PASSWORD`.
- Keep default safety gates intact:
  - clean-tree checks
  - branch/ref guards
  - green-build checks

## Docs sync

When command semantics or config behavior change, check whether these also need updates:

- [wiki/CLI-Manual.md](../../wiki/CLI-Manual.md)
- [tools/cli/ro/README.md](../../tools/cli/ro/README.md)
- generated CLI reference via `ro docs generate`
- related workflow files under [.github/workflows](../../.github/workflows)

## Validation

- `cd tools/cli/ro && go test ./...`
- Rebuild `bin/ro` only when a local smoke check is useful:
  - `cd tools/cli/ro && go build -o ../../bin/ro .`

Use [tools/cli/ro/.goreleaser.yml](../../tools/cli/ro/.goreleaser.yml) and [ro-release.yml](../../.github/workflows/ro-release.yml) when packaging or release behavior is part of the task.
