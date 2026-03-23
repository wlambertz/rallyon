# Pencil Workspace

This directory holds the committed `pencil.dev` sources for the RallyOn organizer UI.

## Layout

- `organizer-ui.lib.pen` contains reusable organizer variables, primitives, and module seeds.
- `screens/` contains screen-level `.pen` files for the current organizer routes and layout shell.

## Working Rules

- Keep `.pen` files in the repo next to the Angular code so Pencil, Storybook, and Codex can all see the same workspace context.
- Treat these files like source code: commit them, diff them, and update them in the same branch as the related UI work.
- Keep shared variant names aligned across Pencil, Storybook, and Angular: `tone`, `size`, `state`, `density`, and `status`.
- Code remains authoritative for shipped token values and runtime behavior.
- Pencil remains authoritative for in-progress visual exploration and layout changes.

## Token Sync

Mirror the current organizer token surfaces into Pencil variables:

- `src/styles/settings/_tokens.scss`
- `src/app/rallyonpreset.ts`
- `src/styles/elements/_typography.scss`

Recommended sync loop:

1. Open the relevant `.pen` file.
2. Import or recreate the current code tokens as Pencil variables.
3. Make the design change.
4. Sync the agreed token or component updates back into Angular and Storybook.

## Library Import

Pencil design libraries are imported from `.lib.pen` files in the Pencil UI.

1. Open the target screen file in Pencil.
2. In the Layers panel, open `Libraries`.
3. Import `organizer-ui.lib.pen`.
4. Reuse the shared assets from the `Assets` panel.

## MCP Check

When Pencil is running, open Codex and run:

```text
/mcp
```

Pencil should appear in the MCP server list before you rely on AI-assisted design edits.
