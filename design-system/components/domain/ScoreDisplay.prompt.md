Badminton set scores, two stacked rows (side A on top, B below). Winner of each set is bold ink; the set tally sits to the right.

```jsx
<ScoreDisplay sets={[{a:21,b:18},{a:19,b:21},{a:21,b:15}]} />        // inline, in lists
<ScoreDisplay variant="board" sets={[{a:14,b:11}]} />                // large Doto, Turnier-TV
```

`inline` for MatchCard/DataTable rows; `board` renders the current set in Doto numerals for the scoreboard. Always pass sets in play order — the last one is treated as current.
