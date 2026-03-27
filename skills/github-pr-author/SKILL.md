---
name: github-pr-author
description: draft GitHub pull request titles and Markdown bodies for the current RallyOn branch. Use for requests to write a PR body, summarize a branch for GitHub, draft reviewer-facing change notes, or produce Markdown for a pull request from the current diff and commit history.
---

# GitHub PR Author

Use this skill when the user wants a GitHub-style pull request summary for the current branch.

## Read before drafting

1. [SYSTEM.md](../../SYSTEM.md)
2. [AGENTS.md](../../AGENTS.md)
3. Relevant area skill when the diff clearly targets one active subsystem:
   - [skills/organizer-ui/SKILL.md](../../skills/organizer-ui/SKILL.md)
   - [skills/tournamentmgmt-service/SKILL.md](../../skills/tournamentmgmt-service/SKILL.md)
   - [skills/keycloak-auth/SKILL.md](../../skills/keycloak-auth/SKILL.md)
   - [skills/ro-cli/SKILL.md](../../skills/ro-cli/SKILL.md)
   - [skills/rallyon-repo-guide/SKILL.md](../../skills/rallyon-repo-guide/SKILL.md) for cross-cutting or repo-wide work

## Workflow

Before writing the PR content, inspect the branch itself:

1. Current branch name
2. Base branch
3. Commit list on the branch
4. Changed files
5. Diff stat
6. Key hunks when the summary needs more detail

Treat the actual diff and commit history as the source of truth. Do not infer changes from open tabs, stale docs, or user shorthand when the branch says otherwise.

## Writing rules

- Default to GitHub-flavored Markdown.
- Keep the writeup concise, factual, and reviewer-oriented.
- Prefer describing user-visible or maintainer-relevant outcomes over restating every file touched.
- Name the touched RallyOn subsystem when it helps orient reviewers:
  - organizer UI
  - tournament management service
  - shared IAM / Keycloak auth
  - `ro` CLI
  - repo-wide docs, policy, or skills
- Avoid overstating impact for docs-only, policy-only, or agent-guidance-only branches.
- If testing was not run, say so plainly. Never invent validation.
- Only include `## Risks / Notes` when the diff introduces meaningful reviewer context, follow-up items, compatibility concerns, or intentionally skipped checks.

## Default output

Use this structure unless the user asks for something shorter:

```md
## Summary

Short paragraph describing the branch outcome.

## What Changed

- Concise bullets grouped by behavior or subsystem

## Testing

- Commands run, or `Not run`
```

Optional section when warranted:

```md
## Risks / Notes

- Reviewer-relevant caveats, assumptions, or follow-up context
```

## RallyOn conventions

- Align the testing section with RallyOn expectations from [SYSTEM.md](../../SYSTEM.md) and [AGENTS.md](../../AGENTS.md).
- For docs-only or skills-only changes, it is usually enough to report `npm run format:check` when it was run.
- When the diff spans multiple active runtimes, reflect that in the summary and list the validation performed for each touched area.
- If the branch changes command surfaces, workflows, env vars, or setup expectations, mention the nearby docs updated to keep instructions aligned.

## Title guidance

If the user also wants a PR title, prefer a short title that reflects the reviewer-facing outcome of the branch, not just the most recent commit subject. Keep it specific to the subsystem and change type.
