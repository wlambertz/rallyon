# 0002 — Adopt OpenSpec (openspec.dev) as the spec-driven change workflow

## Status

Accepted

## Context

The repo already has an `openspec` skill, `ro spec create|validate|view`, and reverse-engineered specs under `docs/wiki/specs/`. These implement the **openspec.tech v1.1 YAML ticket standard** (Specification → Epic → Ticket). That is a different project from [OpenSpec](https://openspec.dev) ([Fission-AI/OpenSpec](https://github.com/Fission-AI/OpenSpec)), the Markdown-based, agent-oriented workflow (`explore → propose → apply → archive`) that was the intended adoption. The shared name has already caused inconsistencies: the skill tells people to install the Fission-AI CLI but validates against the openspec.tech schema.

The existing integration also has gaps for agent-driven delivery: `ro spec validate` is only a structural key check, there is no lifecycle beyond a `status` field, no change/delta concept, and all existing specs describe already-implemented code.

## Decision

- Adopt OpenSpec from openspec.dev, pinned to **1.14.2**, initialised with `openspec init --tools agents`.
- Use the generic `agents` target. It writes one agent-neutral set of skills to `.agents/skills/openspec-*`, which Claude Code reads through the `.claude/skills` symlink and Codex reads directly. The `claude` and `codex` targets each emit differently worded copies (`/opsx:apply` vs `$openspec-apply-change`); with the symlink they would overwrite each other.
- Specs live in top-level `openspec/` (`openspec/specs/` for current behaviour, `openspec/changes/` for proposed work), not under `docs/wiki/`, so the wiki-edit rule in `AGENTS.md` is not in play.
- Generated skills (`.agents/skills/openspec-*`) are Prettier-ignored, because `openspec update` regenerates them.
- The openspec.tech YAML integration (`openspec` skill, `ro spec`, `docs/wiki/specs/`) is **superseded but not removed yet**. `ro` command compatibility is preserved; removal or deprecation messaging and migration of the existing YAML specs are follow-up steps.

## Consequences

Positive:

- Proposal, delta specs, design and tasks per change; behaviour specs stay current via archive/sync.
- Specs are Markdown Requirement/Scenario (WHEN/THEN), which map directly to acceptance criteria and test scenarios.
- Customisable through `openspec/config.yaml` and custom schemas, so project artifacts (refinement, review, UAT) can be added later without forking the tool.
- Built-in planning boundary: propose/explore stop before writing code.

Negative:

- A new external dependency with fast version churn (1.14 → 1.15 within days); generated skills change on `openspec update`.
- Two spec systems coexist until the YAML specs are migrated, so the name `openspec` is ambiguous in this repo during the transition.
- OpenSpec enforces no approval gates itself; gates must come from repo workflow and hooks.
- Without the `claude` target there are no `/opsx:*` shortcuts; skills are invoked as `/openspec-propose` etc.

## Open follow-ups

These are part of this decision and must not be dropped; this ADR stays the tracking point until they are done.

- [ ] Migrate the 8 reverse-engineered YAML specs in `docs/wiki/specs/{tournamentmgmt,ro-cli}/` to `openspec/specs/<capability>/spec.md` (Requirement/Scenario format), verify with `openspec validate --all`, then remove the originals in a separate commit.
- [ ] Deprecate `ro spec create|validate|view` with a pointer message (keep the commands working; `ro` compatibility rule), regenerate `docs/cli-reference.md`.
- [ ] Remove the remaining openspec.tech references (`specs.schemaURL` default, wiki pages) once the migration is done.

## Related

- `.agents/skills/openspec/SKILL.md` — legacy openspec.tech skill, to be rewritten
- `docs/agent-workflows.md` — why `.claude/skills` is a symlink to `.agents/skills`
- ADR 0001 — module boundaries that specs should be organised around
