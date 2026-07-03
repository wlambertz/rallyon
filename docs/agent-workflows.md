# RallyOn Agent Workflows

RallyOn keeps agent guidance small, shared, and inspectable. The canonical project instructions live in `AGENTS.md`; tool-specific files should point to that guidance or add only narrow tool behavior.

## Instruction Files

| Tool | Primary file | Notes |
| --- | --- | --- |
| Codex | `AGENTS.md` and nested `AGENTS.md` | Repo skills live in `.agents/skills/`. Use skills for repeatable workflows. |
| Cursor | `AGENTS.md`; `.cursorrules` as a pointer | Follow shared issue-planning rules in `AGENTS.md`; add `.cursor/rules/*.mdc` only when Cursor-specific metadata or path scoping is needed. |
| Claude Code | `CLAUDE.md` adapter plus `AGENTS.md` | Follow shared issue-planning rules in `AGENTS.md`; keep Claude-specific notes minimal. |
| GitHub Copilot | `.github/copilot-instructions.md` pointer plus `AGENTS.md` | Do not duplicate repository guidance in the Copilot file. |

## Where Guidance Belongs

- Put always-on repository rules, validation commands, safety boundaries, and the educational project principle in root `AGENTS.md`.
- Put subtree-specific rules in the closest nested `AGENTS.md`.
- Put repeatable multi-step workflows in `.agents/skills/<skill-name>/SKILL.md`.
- Put explanatory background, maintenance checklists, and cross-tool orientation in `docs/`.
- Keep one-off task instructions in the issue, PR, or prompt instead of making them durable.

## Educational Project Principle

RallyOn is an educational project for learning modern technologies, architecture practices, tooling, and agent-assisted delivery methods. Agents should favor explainable, incremental changes and document important tradeoffs when introducing new methods or tools.

Learning notes belong close to the decision:

- Use code comments only when they clarify non-obvious implementation choices.
- Use nearby docs when a workflow, tool, or architecture decision changes.
- Use issue comments or PR descriptions for temporary rationale that does not need to become durable guidance.

## Skills

Repo-scoped Codex skills live under `.agents/skills/`. Keep each skill focused on one workflow, with a concise description that clearly states when it should trigger. Prefer instructions over scripts unless deterministic behavior or external tooling is needed.

Use `.agents/skills/issue-implementation-planner/` when Codex is asked to create an implementation plan for a GitHub issue. Cursor and Claude do not load Codex skills directly, so the shared requirement lives in `AGENTS.md`: save the plan under `docs/issue-implementation-plans/`, comment it on the GitHub issue, and do not change implementation code while planning.

Use `.agents/skills/pencil-design/` for Pencil.dev CLI design generation, `.pen` iteration, and organizer UI mockups. Keep Pencil design artifacts under `application/organizer/design/pencil/` and keep accepted design changes aligned with Storybook and Angular.

When adding or changing a skill:

- Keep the front matter `name` stable unless replacing the workflow.
- Make the `description` specific enough for implicit invocation.
- Link only to references needed for that workflow.
- Validate relative links after moving files.

## MCP Policy

MCP is for external tools and context, not for local repo conventions. Use it when an agent needs live access to systems such as GitHub, design tools, docs servers, or knowledge bases.

Do not commit:

- secrets, tokens, or OAuth state,
- personal account identifiers,
- machine-specific local paths,
- private server URLs unless they are already documented public project infrastructure.

Commit only safe examples or documentation for recommended project-level MCP usage. Keep private MCP authentication in user-local configuration.

## Maintenance Checklist

Before changing agent guidance:

1. Check whether the rule belongs in `AGENTS.md`, a nested `AGENTS.md`, `.agents/skills/`, or docs.
2. Avoid duplicating the same rule in multiple tool-specific files.
3. Confirm adapters still point to canonical guidance.
4. Run `npm run format:check:changed`.
5. Run `git diff --check`.
6. Inspect the diff for secrets, local paths, stale links, and conflicting instructions.
