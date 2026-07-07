---
name: pencil-design
description: create or iterate Pencil.dev visual designs for RallyOn organizer UI. Currently paused: application/organizer is an intentionally empty placeholder (no Angular/Storybook implementation) while the frontend stack is reconsidered, so there is nothing for Pencil designs to stay aligned with. Use this skill only to explain that status; do not generate or iterate .pen files for the organizer until a frontend stack is chosen.
---

# Pencil Design (Paused)

This skill is paused. `application/organizer` is an intentionally empty placeholder — the prior Angular implementation, its Storybook stories, and the committed Pencil `.pen` design sources under `application/organizer/design/pencil/` were all removed while the frontend stack is reconsidered. See [application/organizer/AGENTS.md](../../../application/organizer/AGENTS.md).

## If asked for Pencil/`.pen` design work on the organizer

- Explain that the organizer frontend is currently a placeholder with no chosen stack, so there is no Angular/Storybook surface for a Pencil design to sync with.
- Do not install, log in to, or run the Pencil CLI for organizer work until a frontend stack exists.
- If the user wants to resume this workflow, that requires first choosing a frontend stack for `application/organizer` (see [organizer-ui/SKILL.md](../organizer-ui/SKILL.md)), then restoring or rewriting this skill's setup, workspace rules, and validation steps to match the new implementation.

## Validation

Not applicable while this skill is paused. Root `npm run format:check:changed` is the relevant check for edits to this file.
