Inline, persistent system message pinned into a layout region — scheduling conflicts, phase-transition notices, maintenance. Left status bar carries the tone color.

```jsx
<Banner tone="danger" title="Zeitplan-Konflikt" action={<Button size="sm" variant="secondary">Beheben</Button>}>
  Feld 3 ist zweifach belegt um 15:00.
</Banner>
<Banner tone="ok" title="Ergebnis gespeichert" onDismiss={hide} />
```

Tones: info / live / ok / warn / danger. Terse German copy, no punctuation on confirmations. For a floating auto-dismiss message, animate a Banner into a corner.
