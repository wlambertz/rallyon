# RallyOn CLI Reference

_Generated on 2026-11-03T08:42:42+01:00_

## Table of Contents

- [ro](#ro)
- [ro app](#ro-app)
- [ro app build-storybook](#ro-app-build-storybook)
- [ro app install](#ro-app-install)
- [ro app lint](#ro-app-lint)
- [ro app start](#ro-app-start)
- [ro app storybook](#ro-app-storybook)
- [ro app test](#ro-app-test)
- [ro app test-ci](#ro-app-test-ci)
- [ro auth](#ro-auth)
- [ro auth token](#ro-auth-token)
- [ro build](#ro-build)
- [ro completion](#ro-completion)
- [ro config](#ro-config)
- [ro config show](#ro-config-show)
- [ro create](#ro-create)
- [ro deploy](#ro-deploy)
- [ro docker](#ro-docker)
- [ro docker build](#ro-docker-build)
- [ro docker compose](#ro-docker-compose)
- [ro docker compose down](#ro-docker-compose-down)
- [ro docker compose logs](#ro-docker-compose-logs)
- [ro docker compose up](#ro-docker-compose-up)
- [ro docs](#ro-docs)
- [ro docs generate](#ro-docs-generate)
- [ro doctor](#ro-doctor)
- [ro format](#ro-format)
- [ro git](#ro-git)
- [ro git branch](#ro-git-branch)
- [ro git commit](#ro-git-commit)
- [ro git hooks](#ro-git-hooks)
- [ro git hooks install](#ro-git-hooks-install)
- [ro git lint-message](#ro-git-lint-message)
- [ro git push](#ro-git-push)
- [ro git rebase](#ro-git-rebase)
- [ro git status](#ro-git-status)
- [ro git sync](#ro-git-sync)
- [ro run](#ro-run)
- [ro run service](#ro-run-service)
- [ro scaffold](#ro-scaffold)
- [ro scaffold module](#ro-scaffold-module)
- [ro spec](#ro-spec)
- [ro spec create](#ro-spec-create)
- [ro spec validate](#ro-spec-validate)
- [ro spec view](#ro-spec-view)
- [ro telemetry](#ro-telemetry)
- [ro telemetry disable](#ro-telemetry-disable)
- [ro telemetry enable](#ro-telemetry-enable)
- [ro telemetry status](#ro-telemetry-status)
- [ro test](#ro-test)
- [ro validate](#ro-validate)
- [ro version](#ro-version)
- [ro view](#ro-view)

## ro

RallyOn developer CLI

RallyOn developer CLI to streamline builds, tests, deployments, and common workflows.

```bash
ro [flags]
```

**Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app

Work with application shells (e.g. organizer portal)

```bash
ro app
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app build-storybook

Build the static Storybook bundle

```bash
ro app build-storybook <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app install

Install npm dependencies for the app

```bash
ro app install <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app lint

Run the app linter

```bash
ro app lint <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app start

Start the local development server

```bash
ro app start <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app storybook

Run the Storybook dev server

```bash
ro app storybook <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app test

Run the default app unit tests

```bash
ro app test <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro app test-ci

Run the CI headless test target

```bash
ro app test-ci <name>
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro auth

Local development authentication helpers

Local development authentication helpers for RallyOn workflows such as Swagger UI testing.

```bash
ro auth
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro auth token

Request a local Keycloak bearer token for Swagger UI

Request a local development access token from the RallyOn Keycloak realm. This command is intended for local Swagger UI and manual API testing only.

```bash
ro auth token [flags]
```

**Examples**

```bash
read -rsp "Keycloak client secret: " RALLYON_CLIENT_SECRET && export RALLYON_CLIENT_SECRET && echo
read -rsp "Developer password: " RALLYON_DEV_PASSWORD && export RALLYON_DEV_PASSWORD && echo
ro auth token --format bearer
```

**Flags**

```
--client-id string       client ID (default: RALLYON_CLIENT_ID or rallyon-api)
      --client-secret string   client secret (default: RALLYON_CLIENT_SECRET; env var
                               preferred to avoid shell history)
      --format string          output format: raw, bearer, or json (default "raw")
      --password string        password (default: RALLYON_DEV_PASSWORD; env var preferred to
                               avoid shell history)
      --realm string           realm name (default: RALLYON_REALM or rallyon)
      --server string          Keycloak server base URL (default: KEYCLOAK_SERVER or
                               http://localhost:8081)
      --username string        username (default: RALLYON_DEV_USERNAME or dev.organizer)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro build

Build the project or specific services

```bash
ro build [flags]
```

**Flags**

```
--ci     CI mode (batch, non-interactive)
      --fast   skip tests for faster build
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro completion

Install shell completion script for the current shell

```bash
ro completion install
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro config

Configuration commands

```bash
ro config
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro config show

Show merged configuration

```bash
ro config show
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro create

Create a legacy YAML specification ticket (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

Create a legacy specification ticket in YAML format.

The command generates a spec file based on the configured default template
and writes it to the configured specs directory.

Example:
  ro spec create legacy-ticket

```bash
ro create [name]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro deploy

Trigger a deploy via GitHub Actions with safety checks

```bash
ro deploy [flags]
```

**Flags**

```
--check-only      run preflight checks without triggering workflow
      --env string      deployment environment (e.g., dev, prod) (default "dev")
      --input strings   additional workflow input (key=value, repeatable)
      --notes string    optional notes for the deployment
      --wait            wait for workflow completion (overrides deploy.defaultWait) (default true)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker

Docker workflows: build, push, compose

```bash
ro docker
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker build

Build (and optionally push) Docker image for tournamentmgmt

```bash
ro docker build [flags]
```

**Flags**

```
--branch-tag    include branch-based tag (default true)
      --latest        include latest tag
      --push          push images after build
      --sha-tag       include short SHA tag (default true)
      --tag strings   extra tag(s) to apply (without registry unless fully qualified)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker compose

Compose up/down the local stack

```bash
ro docker compose
```

**Flags**

```
--env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --profile strings   compose profile(s) to enable
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docker compose down

docker compose down

```bash
ro docker compose down
```

**Inherited Flags**

```
--dry-run           show actions without executing
      --env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --json              emit JSON-formatted output
      --profile strings   compose profile(s) to enable
      --quiet             suppress info logs (errors only)
      --verbose           enable verbose output
      --yes               auto-confirm prompts and bypass interactive checks
```

## ro docker compose logs

docker compose logs

```bash
ro docker compose logs
```

**Inherited Flags**

```
--dry-run           show actions without executing
      --env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --json              emit JSON-formatted output
      --profile strings   compose profile(s) to enable
      --quiet             suppress info logs (errors only)
      --verbose           enable verbose output
      --yes               auto-confirm prompts and bypass interactive checks
```

## ro docker compose up

docker compose up

```bash
ro docker compose up
```

**Inherited Flags**

```
--dry-run           show actions without executing
      --env-file string   compose env file to load
      --file string       compose file to use (defaults to docker.composeFile)
      --json              emit JSON-formatted output
      --profile strings   compose profile(s) to enable
      --quiet             suppress info logs (errors only)
      --verbose           enable verbose output
      --yes               auto-confirm prompts and bypass interactive checks
```

## ro docs

Documentation utilities

```bash
ro docs [flags]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro docs generate

Generate CLI reference markdown

```bash
ro docs generate [flags]
```

**Flags**

```
--commit-message string   commit message when publishing to wiki (overrides
                                docs.wikiCommitMessage)
      --commit-wiki             commit wiki changes after generation
  -h, --help                    help for generate
      --output string           file to write (defaults to docs.output)
      --wiki                    also write to wiki docs file
      --wiki-output string      wiki docs path (defaults to docs.wikiOutput)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro doctor

Run diagnostics to validate local environment

```bash
ro doctor
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro format

Format documentation and config assets with Prettier

Run the repo's Prettier formatter (write mode by default) against Markdown, MDX, JSON, YAML, and other text-based assets.

```bash
ro format [flags]
```

**Flags**

```
--check   run in verification mode (no writes)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git

Git helper commands (status, branch, rebase, commit)

```bash
ro git
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git branch

Display branch information and ahead/behind summary

```bash
ro git branch [flags]
```

**Flags**

```
--verbose   show verbose branch listing
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git commit

Guide conventional commits

```bash
ro git commit [flags]
```

**Flags**

```
--all                     stage tracked files before committing (git add -A)
      --body string             body/description (supports newlines)
      --breaking                mark commit as breaking change (adds '!')
      --breaking-notes string   details for BREAKING CHANGE footer
      --scope string            optional commit scope
      --summary string          short summary line
      --type string             commit type (feat, fix, chore, ...)
      --wip                     mark commit as work in progress
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git hooks

Manage repository git hooks

```bash
ro git hooks
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git hooks install

Point git at .githooks so commit-msg linting runs locally

```bash
ro git hooks install
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git lint-message

Reject generic commit messages (commit-msg hook, agents, CI)

Validate commit messages against RallyOn's rules: Conventional Commit header,
concrete (non-generic) summary, and a body for large changes.

Inputs: a message file (the commit-msg hook passes one), --stdin, or
--range base..head to check every commit in a range (used by CI).

```bash
ro git lint-message [file] [flags]
```

**Flags**

```
--range string   lint every commit in base..head
      --stdin          read the message from stdin
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git push

Push current branch to configured remote (with safeguards)

```bash
ro git push [flags]
```

**Flags**

```
--branch string   branch to push (defaults to current)
      --force           use --force-with-lease
      --remote string   remote name (defaults to git.defaultRemote)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git rebase

Pull with rebase against the specified upstream

```bash
ro git rebase [flags]
```

**Flags**

```
--autostash     auto-stash local changes during rebase (default true)
      --onto string   upstream (remote/branch) to rebase against (default "origin/main")
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git status

Show git status (short by default)

```bash
ro git status [flags]
```

**Flags**

```
--long   show long git status output
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro git sync

Fetch and rebase current branch onto upstream

```bash
ro git sync [flags]
```

**Flags**

```
--autostash       auto stash during rebase (default true)
      --branch string   upstream branch (defaults to git.defaultBranch)
      --remote string   upstream remote (defaults to git.defaultRemote)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro run

Run local services

```bash
ro run
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro run service

Run tournament management service locally

```bash
ro run service tournamentmgmt [flags]
```

**Flags**

```
--env string   spring profile (default "dev")
      --port int     server port (default 8080)
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro scaffold

Generate boilerplate for RallyOn modules

```bash
ro scaffold
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro scaffold module

Create a Modulith slice skeleton (controller/service/domain/test)

```bash
ro scaffold module <name> [flags]
```

**Flags**

```
--base string           base path override (defaults to scaffold.basePath)
      --dry-run               preview files without writing
      --force                 overwrite existing files
      --package string        base Java package for the module
      --template-set string   template set name (e.g., module, adapter) (default "module")
```

**Inherited Flags**

```
--json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro spec

Legacy YAML specification helpers (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

```bash
ro spec
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro spec create

Create a legacy YAML specification ticket (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

Create a legacy specification ticket in YAML format.

The command generates a spec file based on the configured default template
and writes it to the configured specs directory.

Example:
  ro spec create legacy-ticket

```bash
ro spec create [name]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro spec validate

Validate basic legacy specification structure (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

Validate the basic structure of a legacy YAML specification file.

The YAML parser also accepts JSON-compatible input; there is no TOON-specific decoder.

The validation checks that the spec has the required hierarchical structure:
- specification root
- title and status
- epic
- ticket with title, description, acceptanceCriteria, implementationSteps

Example:
  ro spec validate /path/to/legacy.yaml

```bash
ro spec validate [spec-file]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro spec view

Display raw legacy specification contents (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

Display the contents of an OpenSpec specification file.

The command reads and displays the spec file content, supporting YAML, JSON,
and TOON formats. This is useful for inspecting spec files.

Example:
  ro spec view /path/to/legacy.yaml
  ro spec view /path/to/legacy.json
  ro spec view /path/to/legacy.toon

```bash
ro spec view [spec-file]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro telemetry

Manage CLI telemetry

```bash
ro telemetry
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro telemetry disable

Print instructions to disable telemetry

```bash
ro telemetry disable
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro telemetry enable

Print instructions to enable telemetry

```bash
ro telemetry enable
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro telemetry status

Show telemetry configuration

```bash
ro telemetry status
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro test

Run test suites

```bash
ro test
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro validate

Validate basic legacy specification structure (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

Validate the basic structure of a legacy YAML specification file.

The YAML parser also accepts JSON-compatible input; there is no TOON-specific decoder.

The validation checks that the spec has the required hierarchical structure:
- specification root
- title and status
- epic
- ticket with title, description, acceptanceCriteria, implementationSteps

Example:
  ro spec validate /path/to/legacy.yaml

```bash
ro validate [spec-file]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro version

Show CLI version information

```bash
ro version
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

## ro view

Display raw legacy specification contents (deprecated)

Warning: legacy YAML specification commands are deprecated; use OpenSpec 1.14.2 (openspec.dev) and .agents/skills/openspec/SKILL.md.

Display the contents of an OpenSpec specification file.

The command reads and displays the spec file content, supporting YAML, JSON,
and TOON formats. This is useful for inspecting spec files.

Example:
  ro spec view /path/to/legacy.yaml
  ro spec view /path/to/legacy.json
  ro spec view /path/to/legacy.toon

```bash
ro view [spec-file]
```

**Inherited Flags**

```
--dry-run   show actions without executing
      --json      emit JSON-formatted output
      --quiet     suppress info logs (errors only)
      --verbose   enable verbose output
      --yes       auto-confirm prompts and bypass interactive checks
```

