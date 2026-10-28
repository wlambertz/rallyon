Small square chip for metadata — formats, categories, seeds, active filters. Mono, low radius, neutral.

```jsx
<Tag>Herren-Einzel</Tag>
<Tag accent>Gesetzt #1</Tag>
<Tag removable onRemove={clearFilter}>Gruppe A</Tag>
```

Distinct from StatusPill: Tag is a static label/filter, StatusPill communicates operational state. Neutral by default; `accent` for at most one per group.
