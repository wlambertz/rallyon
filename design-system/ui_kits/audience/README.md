# UI Kit — Turnier-TV (Spectator Display)

The public live surface, shown in-venue on big screens or online. Uses the **dark
"terminal" theme** (`data-theme="dark"`) for high-contrast legibility at distance.

`index.html` shows:
- Header with wordmark, tournament title/phase, and a live Doto clock.
- **LIVE · FELDER** — court cards with large **Doto scoreboard numerals**, serving
  indicator, current set + set history, blinking LIVE status.
- **TABELLE** — group standings.
- **ALS NÄCHSTES** — upcoming-matches ticker.

Meant for full-screen display. Composes ScoreDisplay/Doto styling, DataTable, and
the status system. Self-contained (inlines primitive source + Babel). German copy.
