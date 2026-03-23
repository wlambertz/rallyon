# RallyOn AGENT Instructions

This file is the canonical instruction source for coding agents working in this repository.

## Purpose And Precedence

- Apply these rules to the whole repository unless a nearer `AGENTS.md` file overrides them for a subtree.
- Keep child files focused on workflow differences. Do not duplicate large sections of this file into child files.
- Treat `.github/copilot-instructions.md` as a compatibility pointer, not as a second source of truth.

## Repo Map

- `service/tournamentmgmt/`: Spring Boot Modulith service, Maven wrapper, generated Modulith docs.
- `application/organizer/`: Angular organizer UI, npm-based workflow.
- `tools/cli/ro/`: Go-based RallyOn developer CLI.
- `wiki/`: GitHub wiki as a git submodule. Treat it as a separate repository with its own history and push flow.
- `admin/`: operational helpers and environment bootstrap scripts.
- `docs/` and `service/**/docs/`: committed documentation and generated artifacts that may need refresh after changes.

## Working Rules

- Inspect the existing code and docs before editing. Prefer adapting to established patterns over inventing new ones.
- Preserve user changes. Never revert, overwrite, or clean unrelated edits unless explicitly asked.
- Avoid destructive git commands such as hard resets, forced checkouts, or broad cleanup against tracked files.
- Treat the wiki submodule carefully:
  - commits inside `wiki/` are separate from commits in the main repo
  - do not assume wiki changes are pushed just because the parent repo is clean
  - if the wiki content changes, the parent repo may also need a submodule pointer update
- Prefer stable, repository-local commands and scripts over ad hoc shell pipelines when both exist.
- Keep instructions, docs, and generated artifacts aligned with the behavior you change.

## Validation Expectations

- Run the smallest relevant validation for the area you touched before finishing.
- Report exactly what you validated and what you did not validate.
- Use these defaults unless a child `AGENTS.md` says otherwise:
  - Java service changes: relevant `./mvnw test` scope from `service/tournamentmgmt/`
  - Angular organizer changes: relevant npm lint/test commands from `application/organizer/`
  - Go CLI changes: `go test ./...` from `tools/cli/ro/`
  - Docs-only changes: check links, commands, and referenced paths for correctness
- If a change affects generated documentation or assets, regenerate the derived outputs that are expected to stay committed.

## Change Expectations

- Add or update tests when behavior changes.
- Update nearby documentation when developer workflows, architecture docs, auth flows, CLI commands, or generated docs change.
- Keep commits scoped and use gitmoji-prefixed conventional commit messages such as `✨ feat: ...`, `🐛 fix: ...`, and `🧹 chore: ...`.
- Prefer concise, decision-complete edits over speculative refactors.
- Keep repository instructions in English and portable across coding agents.

## Escalate Before Proceeding

- Ask before making hidden migrations, broad renames, or architecture-level reorganizations.
- Ask before resolving ambiguous product behavior, security-sensitive choices, or irreversible data changes.
- Ask before deleting non-generated content when ownership or intent is unclear.
- Ask before introducing new top-level tooling or instruction files that could conflict with the existing hierarchy.

## Source Of Truth Notes

- Human-oriented overview and onboarding live primarily in `README.md` and the wiki.
- Agent-oriented execution guidance lives in `AGENTS.md`.
- Keep `AGENTS.md` focused on actionable rules, validation, safety, and workflow facts that help an agent work correctly.
