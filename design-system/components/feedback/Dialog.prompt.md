Modal for confirmations and short forms (create tournament, confirm delete, edit result). Scrim dims the board; panel has a 2px seam and soft pop shadow. Closes on Esc or scrim click.

```jsx
<Dialog
  open={open} eyebrow="AKTION" title="Ergebnis erfassen" onClose={close}
  footer={<>
    <Button variant="secondary" onClick={close}>Abbrechen</Button>
    <Button variant="primary" onClick={save}>Speichern</Button>
  </>}
>
  <Input label="Satz 1" mono placeholder="21 : 18" />
</Dialog>
```

Keep body short; put actions in `footer` (primary rightmost). Not for transient messages — use Banner/Toast patterns instead.
