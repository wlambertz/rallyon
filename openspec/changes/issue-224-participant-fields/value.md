# Value assessment

## Persona, context and evidence

Organizer; Tournament Management. Accepted #185 research and current #219 code demonstrate that accepted entries and legacy numeric rosters have distinct identity semantics. The proposed field makes a deliberate selection and its source revisions visible before any future draw. This establishes a technical/product gap, not measured demand, a frequent costly workaround or a verified security incident. No organizer interview or timing measurement exists.

## Scores and calculation

| Dimension | Score | Evidence and anchor |
| --- | --- | --- |
| User value | 3/5 | Hypothesized noticeable improvement to recurring field preparation: explicit accepted-unit selection and visible source divergence. Does not yet deliver a draw or public schedule. |
| Learning value | 3/5 | Useful extension of the existing snapshot/transaction approach to a separately versioned selection across two mutable sources, with explicit concurrency evidence. Reuses #219 patterns rather than claiming a wholly new architectural technique. |
| Risk reduction | 2/5 | Can reduce recoverable selection/identity confusion and stale-input mistakes. No production incident or major security exposure is demonstrated. |
| Confidence | 0.5 | Unvalidated persona/product hypothesis despite corroborating code evidence. Confirm the actual selection/reconciliation workflow with the maintainer and in UAT; do not confuse technical evidence with demand. |
| Effort | 5 | Substantial backend integration across domain/ports, latest-snapshot/audit persistence, private API, dual-source consistency, PostgreSQL race tests, docs, independent reviews and delivery. Smaller than #219's 8 because entry intake/state/active-claim machinery is reused, but not a few-file change. |

`priority = (0.5 × 3 + 0.3 × 3 + 0.2 × 2) × 0.5 / 5 = 2.8 × 0.5 / 5 = 0.28`.

This advisory estimate is not a precise empirical ranking, deadline, agreed budget or automatic go decision. Compare #219's 0.19375 only with its documented assumptions; a higher decimal does not establish greater demand.

## Assumptions and uncertainty

The first actor is the creator; identity remains local and capacity manual. Coarse freshness can require reconciliation after unrelated edits. Latest-only contents cannot reproduce older fields, and snapshot-time validation permits a concurrent source change to make a new field immediately outdated. Empty/one-unit fields are allowed without draw readiness. Confirm these tradeoffs at G2; changing them may increase effort and require revised specifications/value evidence.

## G2 go/no-go

Pending explicit approval of proposal, PF-001–007, design, migration/ADR assessment and value evidence. No implementation is authorized by this file or G1.

## G3 independent challenge

Pending a fresh-context checker after G2. Challenge arithmetic, anchors, evidence, relative effort and whether the proposed bridge justifies its scope; record actual identity/outcome through the helper. Material changes return to G2.
