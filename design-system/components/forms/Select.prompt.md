Native dropdown wrapped in the RallyOn control shell — use for tournament format, phase, and other closed choice sets.

```jsx
<Select
  label="Turniermodus"
  options={['Kaiser-System', 'Schweizer System', 'KO-Bracket']}
  defaultValue="Schweizer System"
/>
```

Options accept plain strings or `{value,label}`. Custom ▼ caret in mono; focus behaves like Input.
