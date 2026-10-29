The core badminton unit — one tie shown as a card: meta line (round · field · time), status indicator, two sides, and inline score. A `live` match gets the accent left seam + blinking dot.

```jsx
<MatchCard
  round="VIERTELFINALE" court={5} time="14:30" status="live"
  sides={[{ name: 'M. Krüger', seed: 1 }, { name: 'L. Sørensen', seed: 4 }]}
  sets={[{a:21,b:18},{a:14,b:11}]}
  onClick={openMatch}
/>
```

Winner (on `status="ok"`) is bold; loser dims. Composes ScoreDisplay internally — just pass `sets`. Target width ~320–380px.
