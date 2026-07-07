---
name: organizer-ui
description: work on the organizer frontend in application/organizer. Use for requests to build, extend, or theme the organizer portal UI. application/organizer is currently an intentionally empty placeholder (the prior Angular/PrimeNG/Storybook/Playwright implementation was removed), so this skill's job is to stop new frontend work from silently reintroducing that stack before a decision is made.
---

# Organizer UI (Placeholder)

`application/organizer` is intentionally empty. The prior Angular standalone implementation (login/dashboard/navigation stub, PrimeNG theme, Storybook stories, Playwright smoke tests, committed Pencil `.pen` sources) was removed while the frontend stack is reconsidered. See [application/organizer/AGENTS.md](../../../application/organizer/AGENTS.md).

## Read before doing any organizer UI work

1. [application/organizer/AGENTS.md](../../../application/organizer/AGENTS.md)
2. This file

## Working rules

- Do not scaffold Angular, PrimeNG, Storybook, Playwright, Pencil, or any other frontend framework/tooling here without an explicit decision from the maintainer made outside routine feature work.
- If asked to implement an organizer UI feature, first surface that no frontend stack is currently selected and ask how to proceed, rather than assuming the prior Angular stack.
- If a new frontend stack is chosen and adopted, rewrite this skill (including the `description`), `application/organizer/AGENTS.md`, and the root docs/CI/devcontainer wiring that reference this app in the same change.
- The [pencil-design](../pencil-design/SKILL.md) skill is paused for the same reason; do not invoke it for organizer work until a stack exists.

## Validation

Not applicable while this directory is a placeholder. Root `npm run format:check` is the relevant check for edits to this skill or to `application/organizer/AGENTS.md`/`README.md`.
