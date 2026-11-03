# Reference case for the standings analysis: DBV A-RLT U11 singles

This is a concrete research case for tasks 1.1–1.2, not a decision that RallyOn targets this event or that the current candidate specification already covers it. The cited [DBV 2026/27 regulations](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf), updated 7 July 2026, identify the DBV as organizer of A-RLT U11 events. Jugendspielordnung Anlage I § 11(1), PDF pages 295–296, describes four-player preliminary groups for U11 singles and a knockout main round, with tournament-mode adjustments possible. Section 3 § 30, PDF page 301, defines group rankings and special treatment where an entrant cannot complete all group games. Confirm the effective edition, actual event announcement and any valid event-specific modification before using this case operationally.

## What the selected source says

- For a group ranking, § 30(1) lists number of matches won, then set difference, then point difference.
- Under § 30(2), if an entrant cannot play all group matches, that entrant's games are removed from the group-ranking calculation. Where two entrants in a group with unplayed matches then have equal match wins, direct comparison decides their order as an exception to § 30(1).
- Section 30(3)–(4) separately addresses comparing runners-up from equal or unequal group sizes; it is not automatically the same calculation as the order within a group.

The source describes the scope of calculation; it does not require deleting original results or prescribe a software correction/audit design. No product requirement to delete match history is inferred. The source excerpts do not fully resolve a missing result where no withdrawal has been confirmed, multiple simultaneous withdrawals, correction authority, or every residual tie; these remain unresolved and must not receive invented outcomes.

## Review examples

1. **Completed group:** A and B have equal match wins; A has a better set difference. For a group to which § 30(1) applies, A ranks ahead of B on that criterion. This example does not assert how to resolve a remaining tie after all three listed criteria.
2. **Entrant cannot complete the group:** Remove the affected entrant's games from the _ranking calculation_. When the stated § 30(2) conditions leave two entrants with equal match wins, their direct comparison determines their order. Retention of the original results for correction or audit is a proposed system decision, not an asserted DBV obligation.
3. **Missing result without a withdrawal decision:** The source excerpts checked here do not settle whether the group is complete or which result is eligible. Mark the calculation unresolved pending competition-specific clarification; do not silently apply the withdrawal exception.
4. **Corrected score:** The applicable ranking must be recomputed against the confirmed corrected inputs, but who can correct it, when it becomes official, and how prior results are retained need an explicit product and authority decision. This is a proposed design outcome, not a quoted DBV rule.

## Difference from the G2 candidate

The current G2 candidate's worked scenario explicitly names the distinct DBV Spielordnung Anlage IV Teil 2 § 10 pool provision. It must not be presented as a § 30 Jugendspielordnung scenario or silently changed to use this U11 case. This finding may require a specification change and renewed G2 review if the U11 case is chosen as the actual product target. The approved candidate remains unchanged while this is researched.

## Ownership alternatives for later decision

`docs/architecture/context-map.md` proposes Tournament Management as owner of competition configuration and Scoring as owner of results and standings; Scoring is currently a placeholder, and the transport between contexts is explicitly undecided. One option is to keep rule configuration in Tournament Management and expose confirmed results plus a rule version to a future Scoring context. A second is an internal Tournament Management module with a clear port while no separate service exists. The choice affects APIs, persistence, possible migrations and a new module; an ADR and explicit scope review are needed before implementation. No existing endpoint, token contract, schema or module is changed by this research.
