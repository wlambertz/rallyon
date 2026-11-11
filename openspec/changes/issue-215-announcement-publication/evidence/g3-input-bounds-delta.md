# G3 input-bounds delta challenge

Verdict: **pass**. Checker: `g3-value`, 2026-11-11. This rechecks only the ANN-005 and design additions against [the previous G3 value challenge](g3-value-delta.md); it does not approve implementation or replace G5–G7 checks.

The addition requires controlled rejection of NUL text and event instants outside the specified inclusive PostgreSQL-compatible UTC range, preserving any existing announcement. It addresses the already assessed input-validation and persistence risk without adding a new persona, workflow, integration or dependency. The maker reports that the human approved the exact additional bounds; the lifecycle record is authoritative for that decision.

The assessment remains plausible: user value 3, learning 4, risk reduction 2, confidence 0.5 and effort 5, yielding `(1.5 + 1.2 + 0.4) × 0.5 / 5 = 0.31`. These unusual inputs and their validation tests do not materially change the relative effort or expected user value by themselves. Organizer demand, the API-client-only authoring experience, independent withdrawal and the caution against interpreting the score as approval remain unchanged.

The production bounds numerically match the proposed astronomical-year endpoints (`-4712` = 4713 BC). The prior [G5 delta](g5-g7-fixes-delta.md) reports PostgreSQL tests for invalid inputs, but did not independently round-trip the precise endpoints. Actual storage behavior, `400` mapping, endpoint precision and G5–G7 freshness remain matters for their own checks, not conclusions of this value review.
