---
name: commit-message-writer
description: write accurate commit messages from the current RallyOn diff, staged changes, or a user-provided summary. Detect and follow the repository's existing commit style when it is clear, prefer concise imperative subjects, and add a body only when reviewer or maintainer context matters.
---

# Commit Message Writer

Use this skill when the user wants a strong commit message for current changes, a staged diff, or a summarized patch.

## Goal

Produce a commit message that matches the actual change, fits RallyOn's local conventions, and stays honest about uncertainty.

Prefer repository evidence over guesswork. If the diff mixes unrelated concerns, say so and recommend splitting the commit instead of hiding that problem in the wording.

## Read before drafting

1. [SYSTEM.md](../../SYSTEM.md)
2. [AGENTS.md](../../AGENTS.md)
3. The closest subtree `AGENTS.md` for the area touched by the change
4. Relevant wiki pages in [wiki/](../../wiki/) when the commit affects CLI workflow, architecture framing, docs semantics, personas, or UX flow

Then inspect the change itself:

1. Staged diff when available
2. Changed file list
3. Diff stat
4. Recent commit history for style cues
5. Any user-provided summary or constraints

Treat the actual diff as the source of truth. Use user summaries to clarify intent, not to replace what the patch shows.

## RallyOn commit-style guidance

Do not assume a convention blindly. Detect it from nearby evidence.

Current repository history suggests:

- Conventional Commits are common, especially for feature and dependency changes
- Plain Git-style subjects also appear in history
- Short, specific, imperative subjects fit best

Follow this decision order:

1. If the user explicitly asks for a format, use it.
2. If the touched area or recent history clearly uses Conventional Commits, follow that pattern.
3. Otherwise, write a clean standard Git subject without inventing a type or scope.

## Workflow

1. Inspect the staged diff or provided patch summary.
2. Confirm whether the changes form one coherent commit.
3. Infer the primary subsystem from changed files.
4. Check recent commit subjects for local style and scope patterns.
5. Draft the shortest subject that accurately states what the change does.
6. Add a body only when the reason, tradeoff, compatibility note, or reviewer context is important.
7. If information is incomplete, label assumptions clearly.

## Writing rules

- Use imperative mood in the subject.
- Keep the subject specific and outcome-focused.
- Avoid vague subjects such as `update stuff`, `misc fixes`, or `changes`.
- Do not claim fixes, features, refactors, or compatibility effects that the diff does not support.
- Prefer one dominant concern per commit message.
- If the patch spans unrelated concerns, recommend splitting it into multiple commits.
- Keep the body factual and brief.
- Use the body to explain why the change exists, what tradeoff it makes, or what reviewers should notice.
- Do not restate the diff line-by-line in the body.

## Scope hints

When Conventional Commits are appropriate, use scopes only when they genuinely help. Good RallyOn-oriented scope candidates often match the affected area, for example:

- `organizer`
- `tournamentmgmt`
- `iam`
- `ro`
- `docs`
- `wiki`
- `repo`

Do not force a scope when the area is unclear.

## Incomplete input

If the user provides only a partial summary or file list:

- say what is missing
- provide the safest draft you can
- clearly label assumptions

If there is not enough evidence to distinguish between a `feat`, `fix`, `refactor`, or plain subject, prefer the safer wording instead of overstating the change type.

## Default response shape

Return Markdown with these sections in order:

1. `Recommended commit message`
2. `Why this works`
3. `Assumptions`
4. `Optional alternatives`

In `Recommended commit message`, include a fenced `text` block containing either:

- a subject only, or
- a subject, a blank line, and a body

Provide 1 to 3 alternatives only when there is a real naming choice, a different emphasis, or a plausible Conventional Commit variant.

## Suggested prompt template

```text
You are writing commit messages for a software repository.

Your goal is to produce commit messages that accurately describe the change, follow the repository's existing conventions, and adhere to strong commit-message best practices.

Before writing the message:
- inspect the staged diff, changed files, and any provided summary
- check whether the repository already follows a commit-message convention
- if prior commit history or explicit repo rules are available, follow them
- only use Conventional Commits when the repository already uses them or the user explicitly asks for them

Best-practice rules:
- summarize the change in a short, specific subject line
- prefer imperative mood in the subject
- avoid vague subjects such as `update stuff` or `fix issues`
- keep the subject focused on what the change does
- when needed, add a body separated by a blank line
- use the body to explain the problem, the reason for the change, important tradeoffs, and anything reviewers or future maintainers should know
- if the change mixes unrelated concerns, say so and recommend splitting it into multiple commits instead of hiding that problem in one message
- do not claim effects that are not supported by the diff or provided context

When deciding the format:
- if the repository clearly uses Conventional Commits, follow `<type>[optional scope]: <description>`
- if the repository uses scoped prefixes or area-based subjects, follow that pattern
- otherwise write a clean standard Git-style subject and optional body

When the input is incomplete:
- say what is missing
- provide the best safe draft you can
- clearly label assumptions

Return the answer in Markdown with these sections:

1. `Recommended commit message`
- provide the final commit message in a fenced `text` block

2. `Why this works`
- briefly explain how the message reflects the change
- mention which convention or style was followed

3. `Assumptions`
- list assumptions or uncertainties
- use `none` if there are no meaningful gaps

4. `Optional alternatives`
- provide 1 to 3 alternatives only when there is a real naming choice, different emphasis, or a plausible Conventional Commit variant
```

## RallyOn reminders

- Use file paths and subsystem boundaries to infer whether the change belongs to organizer UI, tournament management service, shared IAM, `ro` CLI, or repo-level docs and policy.
- Do not describe changes to placeholder areas as if they were active subsystems unless the diff truly adds them.
- If the change touches commands, workflows, auth behavior, or migrations, consider whether the body should mention reviewer-relevant risk or compatibility context.
