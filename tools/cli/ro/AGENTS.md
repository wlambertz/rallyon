# RallyOn CLI Agent Instructions

These instructions apply to `tools/cli/ro/`.

## Stack And Scope

- `ro` is a Go CLI built with Cobra-style commands.
- Keep command behavior, help text, and documentation aligned whenever the CLI surface changes.

## Commands

- Run tests: `go test ./...`
- Build local binary for this repo: `go build -o ../../bin/ro .`
- Show command surface: `go run . --help`

## Implementation Rules

- Prefer adding focused commands and helpers inside the existing `pkg/cmd` structure.
- Keep stdout clean for commands intended for piping or scripting.
- Avoid baking secrets, machine-specific paths, or local credentials into command behavior.
- Preserve backward-compatible command names and flags unless the task explicitly allows a breaking change.

## Testing And Documentation

- Run `go test ./...` after changing Go code.
- Rebuild `../../bin/ro` when the CLI surface changes and local usage depends on the new binary.
- Update `tools/cli/ro/README.md` when commands, flags, or workflow expectations change.
- If the CLI manual in the wiki documents the changed behavior, update the wiki copy as well.
