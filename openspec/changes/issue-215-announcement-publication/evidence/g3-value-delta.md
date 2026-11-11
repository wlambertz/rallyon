# G3 independent delta challenge

Verdict: **pass for the revised value assessment, conditional on renewed G2 approval**. Checker: `g3-value`, 2026-11-11. This delta rechecks [the initial failed challenge](g3-value.md); it does not approve the changed scope or certify an implementation.

| Finding | Delta assessment |
| --- | --- |
| Draft visibility could re-expose an announcement | Resolved in the proposed contract: publication/withdrawal is an independently versioned state, and only the creator can publish, correct, withdraw or explicitly republish. Draft visibility cannot toggle that state. Concurrent announcement changes still need the specified database and authorization tests. |
| Public link was a JSON API | Resolved for basic readability: a public GET produces UTF-8 `text/plain` with `nosniff`, rather than requiring a JSON client. The organizer still has no UI and must use an API client; formatting, printing and real-user suitability remain unvalidated. |
| Effort uncertainty and comparison | Score 5 is plausible, but preliminary. The completed [configuration-read change](../../archive/2026-11-03-retrieve-tournament-configuration/value.md) estimated 3 without schema or public access; this proposal adds a migration, anonymous security boundary, creator-only writes, withdrawal and concurrent mutation tests. The completed [Java 25 upgrade](../../archive/2026-11-10-upgrade-java-25/value.md) estimated 5 for coordinated build, IAM, CI and delivery work. These are rough comparisons, not proof that effort cannot rise to 8. Reassess after the ADR and PostgreSQL test approach; material changes return to G2. |

The previously reproduced priority remains **0.31**, conditional on 3/4/2, confidence 0.5 and effort 5. Neither this score nor this report approves the work. Organizer demand and regulatory applicability remain unverified.

**Decision-critical tradeoff:** changing a tournament to PRIVATE will not hide its independently published announcement. A creator must explicitly withdraw it, and a creator-authorized replacement republishes a withdrawn version. The new model must be clearly presented to and explicitly accepted by the maintainer at renewed G2; notification to an organizer is currently a design intention without an organizer UI. User misunderstanding is a residual disclosure risk, not eliminated by the state separation.

No maker files were changed for this check. Validate the exact HTTP/security behavior, rendering, data projection, database race behavior and no-store response in later independent implementation review.
