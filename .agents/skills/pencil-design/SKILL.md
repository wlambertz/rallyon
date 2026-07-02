---
name: pencil-design
description: create or iterate Pencil.dev visual designs for RallyOn organizer UI screens, reusable primitives, layout mockups, or design-token explorations. Use when the user asks for Pencil, .pen files, visual mockups, UI design generation, or AI-assisted design work that should stay aligned with Angular and Storybook.
---

# Pencil Design

Use this skill for Pencil-backed organizer UI design work.

This skill is adapted from `@pencil.dev/cli` `SKILL.md` version `0.2.8`:

`https://unpkg.com/@pencil.dev/cli@0.2.8/SKILL.md`

## Read before designing

1. [application/organizer/AGENTS.md](../../../application/organizer/AGENTS.md)
2. [application/organizer/design/pencil/README.md](../../../application/organizer/design/pencil/README.md)
3. [wiki/design/frontend-spaceport-theme.md](../../../wiki/design/frontend-spaceport-theme.md)
4. [application/organizer/src/stories/pencil-registry.ts](../../../application/organizer/src/stories/pencil-registry.ts)

## Setup checks

Before running Pencil commands, check whether the CLI is available:

```bash
which pencil || npx pencil version
```

If Pencil is not available, do not install it silently. Explain the options:

- global install: `npm install -g @pencil.dev/cli`
- local install: `npm install @pencil.dev/cli`, then run via `npx pencil`

The Pencil CLI requires a logged-in Pencil user. Check with:

```bash
pencil status
```

If the user is not logged in, use one of the upstream-supported auth paths:

- `pencil signup --email <email> --username <name> --name "<display name>"`
- `pencil login --email <email> [--code <code>]`
- `PENCIL_CLI_KEY` in the current shell

The Pencil AI agent also depends on authenticated Claude Code access. If that is unavailable, report the blocker and the setup options instead of fabricating design output.

## RallyOn workspace rules

- Save durable organizer designs under `application/organizer/design/pencil/`.
- Use `application/organizer/design/pencil/organizer-ui.lib.pen` for shared primitives, variables, and reusable module seeds.
- Use `application/organizer/design/pencil/screens/` for screen-level designs.
- Keep Pencil, Storybook, and Angular sync surfaces aligned:
  - `application/organizer/src/stories/pencil-registry.ts`
  - `application/organizer/src/stories/**`
  - `application/organizer/src/styles/settings/_tokens.scss`
  - `application/organizer/src/app/rallyonpreset.ts`
  - `application/organizer/src/styles/elements/_typography.scss`
- Do not use temp directories for design files the user will need later.
- Do not commit exported preview images unless the task explicitly asks for committed visual artifacts.

## Creating a design

Use the Pencil CLI command shape from the upstream skill:

```bash
pencil --out <output.pen> --prompt "<design description>" --export <output.png> --export-scale 2
```

Key flags:

- `--out`, `-o`: output `.pen` file
- `--prompt`, `-p`: user design request
- `--prompt-file`, `-f`: attach a reference image or text file
- `--export`, `-e`: export an image preview
- `--export-scale`: export resolution multiplier; use `2` by default
- `--export-type`: `png`, `jpeg`, `webp`, or `pdf`
- `--in`, `-i`: start from an existing `.pen` file for iteration
- `--model`, `-m`: Claude model for the Pencil agent

Pass the user's design request directly as the prompt. Do not add invented layout, palette, typography, or content details unless the user provided them or RallyOn docs require them. Pencil's own AI designer handles creative decisions.

## Organizer output locations

Prefer these paths:

- screen draft: `application/organizer/design/pencil/screens/<screen-name>.pen`
- shared primitive/library work: `application/organizer/design/pencil/organizer-ui.lib.pen`
- temporary export for inspection: matching `.png` next to the `.pen`, unless the user requests another location

Example:

```bash
pencil \
  --out application/organizer/design/pencil/screens/events.pen \
  --prompt "Refine the organizer events screen" \
  --export application/organizer/design/pencil/screens/events.png \
  --export-scale 2
```

## Timing expectations

Pencil generation can take several minutes:

- simple component: 1-2 minutes
- app screen or section: 2-3 minutes
- detailed page or dashboard: 3-5+ minutes

Tell the user before starting a generation command and use a generous timeout, at least 10 minutes.

## Showing and iterating

Always inspect or show the exported image after generation. Visual review is part of the workflow.

For iteration, load the prior `.pen` file with `--in` and write a new version unless the user asks to overwrite:

```bash
pencil \
  --in application/organizer/design/pencil/screens/events.pen \
  --out application/organizer/design/pencil/screens/events-v2.pen \
  --prompt "Tighten spacing and make the filter controls easier to scan" \
  --export application/organizer/design/pencil/screens/events-v2.png \
  --export-scale 2
```

## Sync back to code

When a Pencil design changes a UI that exists in Angular:

1. Identify the matching Storybook story and sync surfaces in `pencil-registry.ts`.
2. Update Angular, styles, and Storybook in the same branch when the design is accepted for implementation.
3. Keep code authoritative for shipped runtime behavior.
4. Keep Pencil authoritative for in-progress visual exploration.

## Validation

For design-only skill changes:

`npm run format:check:changed`

For organizer UI implementation based on a Pencil design:

- `npm run organizer:lint`
- `npm run organizer:test:ci`
- `npm run organizer:test:e2e` when login, routing, shell navigation, or Playwright-dependent copy changes

If shared visual primitives, tokens, or screen layouts change, update the relevant Storybook story when practical.
