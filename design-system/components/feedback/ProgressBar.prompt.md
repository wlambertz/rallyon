Flat determinate progress for tournament/phase/round completion. Hard-seam track; use `segmented` when the count is discrete (rounds).

```jsx
<ProgressBar label="Turnierfortschritt" value={62} />
<ProgressBar label="Runde" value={2} max={5} segmented />
```

Continuous fill shows %, segmented shows count. No indeterminate/spinner variant — this system reports concrete state.
