# UI Kit — Organizer Ops Terminal

The organizer's live operations board. This is the primary RallyOn surface: a
sidebar-nav app shell reading as an **instrument panel**, not a marketing dashboard.

`index.html` is an interactive click-through:
- **Übersicht** — status metric strip, live **FELD-MANIFEST** (court grid), **SCHNELLAKTIONEN** desk, next-match checkpoint, and a scheduling-conflict banner.
- **Setup** — tournament configuration (Modus, points, fields), disciplines, phase progress.
- **Planung** — schedule DataTable + KO-Bracket view.
- Sidebar nav switches screens; "Ergebnis erfassen" opens the result-entry Dialog.

Composes the DS primitives (Button, Card, Metric, StatusPill, Tag, DataTable,
MatchCard, FieldTile, BracketNode, Tabs, Banner, ProgressBar, Dialog). The file is
self-contained (inlines the primitive source + Babel) so it renders offline and in
the Design System tab. Copy is German; theme is light "paper".
