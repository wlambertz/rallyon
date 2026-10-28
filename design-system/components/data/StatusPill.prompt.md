Communicates match/court/phase status. Always color + dot + short UPPERCASE German label — never color alone.

```jsx
<StatusPill tone="live" label="Läuft" />
<StatusPill tone="ok" label="Fertig" />
<StatusPill tone="warn" label="Wartet" />
<StatusPill tone="danger" label="Konflikt" />
<StatusPill tone="idle" label="Geplant" />
```

`live` blinks its dot (2-step, mechanical). `solid` for the dark Turnier-TV surface. Keep labels to one word where possible.
