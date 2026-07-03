---
name: issue-implementation-planner
description: create durable implementation plans for RallyOn GitHub issues before coding. Use when the user asks to plan an issue, generate an implementation plan, add a plan to an issue, prepare issue work, or otherwise wants GitHub issue implementation planning that must be saved under docs/issue-implementation-plans and commented on the issue without changing code.
---

# Issue Implementation Planner

Use this skill to plan GitHub issue work before implementation.

## Boundaries

- Do create or update plan files under `docs/issue-implementation-plans/`.
- Do add the plan as a GitHub issue comment.
- Do inspect code, docs, tests, wiki pages, issue comments, and current git state.
- Do not modify production code, tests, migrations, runtime config, dependency files, generated app artifacts, or implementation docs outside the plan folder.
- Stop after publishing the plan unless the user separately asks to implement.

## Workflow

1. Resolve the issue number from the user prompt, URL, current branch, or GitHub context.
2. Read the issue body and relevant issue comments.
3. Inspect current git state and relevant repo sources before planning:
   - root `AGENTS.md`
   - closest nested `AGENTS.md` for affected paths
   - relevant RallyOn skill for the area
   - relevant `wiki/` pages when the issue involves architecture, CLI workflows, personas, docs, or UX flow
4. Identify scope, non-goals, affected subsystems, compatibility constraints, and validation commands.
5. Write the plan to `docs/issue-implementation-plans/issue-<number>-<short-slug>.md`.
6. Add the same plan as a GitHub issue comment. If the plan is unusually long, post a concise summary and include the repo path to the saved plan.
7. Report the plan path, issue comment target, and any assumptions.

## Plan Format

Use this structure for the stored plan and the issue comment:

```md
# Issue #<number>: <title>

## Summary

Brief goal and intended outcome.

## Implementation Changes

Concrete implementation approach grouped by subsystem.

## Validation

Exact commands or checks required.

## Risks / Compatibility

Only meaningful constraints, migration concerns, API behavior, or rollout notes.

## Assumptions

Defaults chosen where the issue or repo does not decide.
```

## File Naming

- Use lowercase slugs.
- Prefer `issue-<number>-<short-slug>.md`.
- Keep the slug short but recognizable, for example `issue-101-kotlin-test-interop.md`.
- If a plan file already exists for the issue, update it instead of creating a duplicate.

## GitHub Comments

Prefer the GitHub connector for issue comments. If the connector cannot comment and `gh` is available, use `gh issue comment <number> --body-file <plan-file>`.

Never invent issue state. If GitHub cannot be reached, write the local plan and clearly report that the issue comment was not posted.
