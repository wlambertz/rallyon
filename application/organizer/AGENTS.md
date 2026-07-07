# Organizer Frontend Agent Guide

## Scope

- This directory is intentionally empty. The Angular organizer portal that previously lived here was removed while the frontend stack is reconsidered.
- Do not introduce a frontend framework, UI library, design tooling, or test harness here without an explicit decision from the maintainer, made outside of routine feature work.
- Until that decision is made, treat this as a placeholder: no runnable app, no build tooling, no dependencies.

## Working here

- If asked to add organizer UI features, first surface that the frontend stack is unresolved rather than reintroducing Angular, PrimeNG, Storybook, Pencil, or Playwright by default.
- If a new frontend stack is chosen, document the decision and its tradeoffs close to the change (see the Educational Project Principle in the root `AGENTS.md`), and update root docs/CI/devcontainer wiring that reference this app in the same change.

## History

- The prior Angular 21 implementation (login/dashboard/navigation stub, PrimeNG theme, Storybook stories, Playwright smoke tests, committed Pencil `.pen` design sources) was removed. See git history for the prior implementation if it is needed for reference.
