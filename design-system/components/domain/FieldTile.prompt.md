One court in the hall manifest — big Space Grotesk field number, a colored top seam, and a state chip. Grid several into the "FELD-MANIFEST" module.

```jsx
<FieldTile field={5} state="live" match="Krüger vs. Sørensen" onClick={openField} />
<FieldTile field={2} state="free" note="verfügbar" />
<FieldTile field={7} state="off" note="gesperrt · Reparatur" />
```

States: `live` (accent number + blink), `free`, `next`, `off` (dimmed). Court number goes big; occupant text below the divider.
