The primary action control — use for any committed operation (submit, confirm, generate schedule); one `primary` per view, everything else `secondary`/`ghost`.

```jsx
<Button variant="primary" size="md" onClick={save}>Ergebnis erfassen</Button>
<Button variant="secondary">Abbrechen</Button>
<Button variant="ghost" size="sm" iconLeft={<Icon name="plus" />}>Feld hinzufügen</Button>
<Button variant="danger">Turnier löschen</Button>
```

Variants: `primary` (**ink/black** fill + offset print-block that presses down on click — weight comes from contrast, not color, so the orange accent stays reserved for live signals), `secondary` (paper fill + ink seam; also the right choice for a primary-weight action sitting on a dark surface), `ghost` (borderless until hover), `danger` (only for destructive ops). Sizes `sm` (30px) / `md` (38px) / `lg` (44px, touch). Labels are sentence-case German. No bounce — press nudges 2px on the diagonal.
