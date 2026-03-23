# Organizer UI Agent Instructions

These instructions apply to `application/organizer/`.

## Stack And Scope

- This application is an Angular organizer UI with PrimeNG and Playwright-based end-to-end coverage.
- Follow the existing visual language and component patterns unless the task explicitly asks for redesign work.

## Commands

- Install dependencies from the repo root: `npm run organizer:install`
- Local dev server from the repo root: `npm run organizer:dev`
- Lint: `npm run organizer:lint`
- Unit tests: `npm run organizer:test`
- CI-style unit tests: `npm run organizer:test:ci`
- End-to-end smoke tests: `npm run organizer:test:e2e`

Equivalent local commands from this directory:

- `npm start`
- `npm run lint`
- `npm run test`
- `npm run test:ci`
- `npm run test:e2e`

## Implementation Rules

- Preserve established Angular and PrimeNG conventions already in the app.
- Keep changes consistent with existing routing, component structure, and styling decisions.
- Prefer small, focused template/component/service edits over broad directory reshuffles.
- Avoid introducing a new design language unless the task explicitly asks for it.

## Testing And Verification

- Run the smallest relevant npm checks for the files you changed.
- Use `npm run organizer:test:ci` for a reproducible headless verification when UI logic changes.
- Use `npm run organizer:test:e2e` when flows, navigation, or integration points change.
- Update or add tests when user-visible behavior changes.
