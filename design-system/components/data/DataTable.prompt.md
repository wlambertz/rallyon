Dense tabular data — standings, field manifests, registration lists. Hard 1.5px header seam, hairline rows, hover reveals a left accent seam when clickable.

```jsx
<DataTable
  onRowClick={openMatch}
  columns={[
    { key: 'pos', header: '#', width: 40, mono: true, align: 'right' },
    { key: 'player', header: 'Spieler' },
    { key: 'w', header: 'S', mono: true, align: 'right', width: 48 },
    { key: 'pts', header: 'Punkte', mono: true, align: 'right', width: 72 },
    { key: 'status', header: 'Status', render: (v) => <StatusPill tone={v.tone} label={v.label} /> },
  ]}
  rows={standings}
/>
```

Put IDs/scores/times in `mono` columns, right-aligned. Use `render` to drop StatusPill/Tag into cells. `compact` for long lists.
