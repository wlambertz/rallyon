Square, icon-only control for toolbars, table row actions, and steppers — always pass `title` for the label/tooltip.

```jsx
<IconButton title="Bearbeiten"><Icon name="pencil" /></IconButton>
<IconButton title="Layout" active><Icon name="grid" /></IconButton>
<IconButton variant="ghost" title="Mehr"><Icon name="more-vertical" /></IconButton>
```

Matches Button sizing (30/38/44px). `active` reads as a pressed/selected toggle (accent-soft fill, accent border). Use `ghost` inside dense rows so it disappears until hover.
