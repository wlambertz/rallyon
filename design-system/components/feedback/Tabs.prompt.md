Switches views within a module or page region — Setup / Planung / Verwaltung, or Gruppen / Bracket. Uppercase mono labels; the active tab carries a 2px accent underline that sits on the module's seam.

```jsx
<Tabs
  defaultValue="planung"
  onChange={setView}
  tabs={[
    { id: 'setup', label: 'Setup' },
    { id: 'planung', label: 'Planung', count: 12 },
    { id: 'verwaltung', label: 'Verwaltung' },
  ]}
/>
```

Works controlled (`value`) or uncontrolled (`defaultValue`). Count badge for open items per view.
