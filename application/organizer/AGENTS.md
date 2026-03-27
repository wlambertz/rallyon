# Organizer Portal Agent Guide

## Scope

- Angular standalone organizer portal for login, dashboard, navigation, and UX-spike flows.
- This subtree includes UI code, Storybook stories, Playwright smoke tests, and committed Pencil design sources.

## Canonical Commands

- Install: `npm install`
- Dev server: `npm start`
- Production build: `npm run build`
- Lint: `npm run lint`
- Unit tests: `npm test`
- Headless unit tests: `npm run test:ci`
- E2E smoke: `npm run test:e2e`
- Storybook: `npm run storybook`
- Static Storybook build: `npm run build-storybook`
- Install Playwright browser when needed: `npx playwright install --with-deps chromium`

## Local Conventions

- Keep feature code under `src/app/features`, shell/navigation under `src/app/layout`, and cross-cutting logic under `src/app/core`.
- Use standalone Angular components and existing route structure from `src/app/app.routes.ts`.
- Keep feature-specific styling next to the component; only promote reusable styling into `src/styles/**` following the ITCSS-like layers documented in `src/styles/README.md`.
- Use the wiki for product and art-direction context before changing major flows or visual language:
  - `../../wiki/Personas.md` for audience and role framing
  - `../../wiki/design/frontend-spaceport-theme.md` for the current organizer visual/theme brief
- Respect the ESLint selector rules:
  - directives: attribute selectors with `app` prefix
  - components: element selectors with `app` prefix
- Storybook is part of the workflow. If you add or materially change reusable UI, update or add the matching story when practical.

## Boundaries

- Current auth is a browser-only stub:
  - `src/app/core/services/auth.service.ts` accepts `organizer / rallyon`
  - session state is stored in `localStorage`
- Do not present the current stub as production auth.
- If wiki design/product guidance conflicts with code, package manifests, tests, or route behavior, follow the code and update the UI guidance around that reality.
- Keep Pencil assets, Storybook references, and code tokens aligned:
  - `design/pencil/*.pen`
  - `src/stories/pencil-registry.ts`
  - `src/styles/settings/_tokens.scss`
  - `src/app/rallyonpreset.ts`
  - `src/styles/elements/_typography.scss`

## Risk Notes

- Route guards and auth service changes affect the whole organizer shell.
- Playwright smoke assumes the stub login flow and dashboard text; update tests with UI copy changes.
- Tailwind/PrimeNG/theme changes can ripple broadly. Prefer validating with both unit tests and a quick app/storybook sanity check when feasible.

## Done Criteria

- Run `npm run lint` and `npm run test:ci` after code changes.
- Run `npm run test:e2e` for login, routing, or shell-navigation changes.
- If you changed shared visual primitives or design tokens, update the relevant Storybook story and committed Pencil source in the same branch when applicable.
