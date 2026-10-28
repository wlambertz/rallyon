One slot in a KO bracket — two competitors stacked with seed + score; the winner is highlighted (accent-soft fill), the loser dims. Draw the connector lines yourself in the bracket layout; the node is a fixed 200px block.

```jsx
<BracketNode
  label="VF 1"
  sides={[
    { seed: 1, name: 'M. Krüger', score: 2, winner: true },
    { seed: 8, name: 'T. Vogt', score: 0 },
  ]}
/>
```

Set `live` for the in-progress node. Empty slots use `{}` → renders "—".
