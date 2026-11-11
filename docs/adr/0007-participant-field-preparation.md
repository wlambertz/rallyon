# 0007 — Private participant-field preparation

## Status

Accepted for #224, #225 and #226 through the explicit G2 approval **“Go”** of the presented [design](../../openspec/changes/issue-224-participant-fields/design.md), followed by G4 **“Ja”** authorizing the exact implementation issues and continued implementation. This ADR records those already reviewed decisions; it grants no merge or UAT approval.

## Context

Accepted entries retain local keys and explicit pair membership. Legacy rosters use numeric player/team references and broader configuration access. Entry acceptance therefore cannot safely imply field membership or public disclosure. A creator needs an explicit bracket selection before any future draw.

## Decision

Keep a separate participant-field consistency boundary within existing `setup.configuration`. Only the stored tournament creator with a positive authenticated identity can inspect or prepare it. Identify a field by tournament, discipline and bracket; require the expected field version and both observed source draft/book versions. The new API remains separate from the legacy Tournament DTO and preserves existing JWT/REST/CLI contracts.

Select explicit ACCEPTED entries with compatible selection IDs/team size. Retain original entry selection/member snapshots and a distinct current target selection. Local keys remain unverified; no fabricated global/team IDs, automatic capacity admission or roster synchronization is introduced. Empty and one-unit selections are valid preparation, not draw readiness. Canonical entry-ID order carries no seed significance.

Use independent REQUIRES_NEW/REPEATABLE_READ transactions for source observation and field reads/writes. Every observation belongs to one consistent snapshot; joining a weaker caller transaction is forbidden. A committed preparation is independent of a suspended caller rollback. Lock only the field row for mutation, using lazy transactional initialization and expected-version/CAS checks; rollback includes all content, audit and initialization. Translate PostgreSQL serialization/deadlock/uniqueness failures only after rollback, without silent retries.

Source rows are not frozen until field commit. A concurrent source edit can make a successful preparation immediately outdated; expose observed versions and describe this guarantee explicitly. CURRENT means saved source versions equal both observed versions; any difference conservatively means OUTDATED, including unrelated edits. Source operations never rewrite field contents/version/audit. Reconciliation is explicit replacement against current source/field versions. No cross-field reservation or running-draw lock exists.

Add append-only migration V8 for field roots, copied units/members and minimal private audit, with tournament/field ownership foreign keys. Do not tie snapshots to mutable bracket rows or require an entry-book row for an empty field. Unprepared reads create no storage. Saved fields survive bracket removal; only fresh preparation requires a current compatible bracket.

Keep latest full content only. Audit successful PREPARE actor, time, field version, unit count and source versions; do not retain earlier full member payloads or contact data. No retention/deletion automation or revision-history reconstruction is implied. Keep additive storage on application rollback; future operational retention needs an explicit decision.

## Alternatives and tradeoffs

- Reusing numeric legacy rosters would invent identity mapping and inherit excessive read access.
- Automatically maintaining fields from entry changes would erase the creator's decision and couple workflows.
- Per-entry freshness could reduce unnecessary reconciliation but adds versioning and comparison machinery; whole-source versions are deliberately conservative.
- Locking all source writes until preparation commits would alter existing transaction contracts. Snapshot-time validation is smaller and makes divergence observable, at the cost of possible immediate outdatedness.
- Retaining every full revision is deferred to limit duplicated private payloads. This slice cannot reconstruct historical fields from audit alone.

## Validation

PF-001–007 require pure rules, authenticated API tests and real PostgreSQL migration/atomicity evidence. Force source commits between observation and field completion/read; verify complete winning state under first-write/replacement races, private reads after removal, explicit clearing and reconciliation. Run full backend verification and real local Keycloak UAT. Independent gates and human acceptance remain required.
