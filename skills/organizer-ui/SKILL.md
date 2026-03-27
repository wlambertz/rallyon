---
name: organizer-ui
description: work on the Angular organizer portal in application/organizer. Use for components, routes, auth-stub flows, shell or navigation changes, styling, PrimeNG theme work, Storybook stories, Playwright smoke coverage, or Pencil-backed design sync. Follow organizer subtree rules, preserve the current stub-auth reality, and keep Angular, Storybook, and Pencil assets aligned.
---

# Organizer UI

Use this skill for work in `application/organizer/`.

## Read before editing

1. [application/organizer/AGENTS.md](../../application/organizer/AGENTS.md)
2. [wiki/design/frontend-spaceport-theme.md](../../wiki/design/frontend-spaceport-theme.md) for visual direction
3. [application/organizer/src/styles/README.md](../../application/organizer/src/styles/README.md) for style-layer conventions
4. [application/organizer/design/pencil/README.md](../../application/organizer/design/pencil/README.md) when the task changes reusable UI, layout, or tokens

Treat code as authoritative if docs and implementation diverge.

## Working rules

- Keep feature code under `src/app/features`, shell/navigation under `src/app/layout`, and cross-cutting logic under `src/app/core`.
- Use standalone Angular patterns and existing routes from [application/organizer/src/app/app.routes.ts](../../application/organizer/src/app/app.routes.ts).
- Keep feature styling next to the component. Only promote reusable styling into `src/styles/**`.
- Respect ESLint selector rules with the `app` prefix.
- Do not present the current browser-only auth stub as production auth.

## Design sync

When changing shared UI primitives, tokens, or screen layouts, keep these aligned when applicable:

- [application/organizer/design/pencil/screens](../../application/organizer/design/pencil/screens)
- [application/organizer/src/stories](../../application/organizer/src/stories)
- [application/organizer/src/styles/settings/\_tokens.scss](../../application/organizer/src/styles/settings/_tokens.scss)
- [application/organizer/src/app/rallyonpreset.ts](../../application/organizer/src/app/rallyonpreset.ts)
- [application/organizer/src/styles/elements/\_typography.scss](../../application/organizer/src/styles/elements/_typography.scss)

## Validation

- `npm run organizer:lint`
- `npm run organizer:test:ci`

Also run `npm run organizer:test:e2e` when the task changes login flow, route guards, shell navigation, or dashboard text that Playwright depends on.

Update or add the matching Storybook story when practical for reusable UI changes.
