# DBV scoring-transition evidence

This research note expands N6-02; it is not an accepted product requirement. Source: [DBV announcement of 25 June 2026](https://www.badminton.de/news/badminton/neue-zaehlweise-dbv-praesidium-beschliesst-einfuehrung-von-3-x-15/).

- For DBV-level individual competitions, the announcement makes the 3 × 15 format the standard from 4 January 2027, with two winning games, a two-point margin after 14–14 and a cap at 21.
- It permits organizers of DBV-level individual events to use that format from 1 August 2026 if the announcement explicitly states it.
- It says Bundesliga team competitions retain their separate 5 × 11 scoring and recommends that regional groups and state associations use the existing 3 × 21 format throughout 2026/27 and change in 2027/28. A recommendation is not evidence that every state association has adopted that date.

The existing `setup/rules/api/ScoringRules.kt` defines 21- and 15-point presets, including a 15-point preset with three games, win-by-two and cap 21. This represents a format; it does not demonstrate automatic selection by date or competition, nor a Bundesliga 5 × 11 preset. A custom rule type exists, but its mere presence does not establish a match-scoring implementation. Any future selection logic needs the actual event authority, effective rules and announced overrides; the source does not justify applying a single transition date to all tournaments.
