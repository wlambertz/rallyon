The primary structural unit — treat every panel on the ops board as a Card module. Header carries an uppercase mono eyebrow + Space Grotesk title, divided by a hairline from the body.

```jsx
<Card eyebrow="FELD-MANIFEST" title="Hallen-Belegung" action={<IconButton title="Filter"><Icon name="filter"/></IconButton>}>
  … module content …
</Card>
```

`accent` adds a left seam for a live/priority module. `padded={false}` for flush DataTables. Keep radius low; never add a drop shadow beyond `raised`.
