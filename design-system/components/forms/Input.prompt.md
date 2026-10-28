Single-line text field. Label is uppercase mono; focus thickens the border to 2px ink with an accent-soft ring. Use `mono` for any numeric/ID input so it aligns with the data type.

```jsx
<Input label="Turniername" placeholder="z. B. Stadtmeisterschaft 2026" />
<Input label="Punkte pro Satz" mono suffix="Pkt" defaultValue="21" size="sm" />
<Input label="E-Mail" invalid hint="Ungültige Adresse" />
```

`prefix`/`suffix` for icons or units. `invalid` + `hint` shows the error message in danger red.
