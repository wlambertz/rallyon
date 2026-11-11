# 0006 — Organizer-managed entry book

## Status

Accepted by the maintainer for [#219](https://github.com/wlambertz/rallyon/issues/219), [#221](https://github.com/wlambertz/rallyon/issues/221) and [#222](https://github.com/wlambertz/rallyon/issues/222). Actual quote: **“Ja”**, in response to the presented architecture decision in the conversation. Behavior is defined in the [approved OpenSpec change](../../openspec/changes/issue-219-organizer-entries/design.md).

## Context

The existing configuration roster is not an entry-request workflow. Its draft can be replaced by authenticated callers without creator ownership checks. An entry workflow therefore needs its own authority, intake state, version and player/selection snapshots. No verified player directory, organization delegation, public registration or player-account linkage exists for this slice.

## Decision

### Ownership and identity

Keep an independent entry-book consistency boundary inside Tournament Management's existing `setup.configuration` module. It refers to a tournament; only its recorded creator, matched to a valid positive authenticated user ID, may read or mutate the book. The creator ID is read from the stored tournament, never a supplied organizer ID. Existing draft and announcement contracts remain unchanged.

Players use organizer-maintained tournament-local case-sensitive keys and display-name snapshots. Store keys with deterministic case-sensitive PostgreSQL comparison, e.g. `COLLATE "C"`. This does not verify identity, willingness, eligibility or external account ownership. Reusing another key for the same real person cannot be detected automatically. No new Player Management context or global identity registry is introduced.

### Consistency and storage

One entry-book row per tournament owns `intakeOpen` and a monotonically increasing book version. With no stored row, reads report CLOSED/version 0 and no entries; reads do not create storage. First mutation creates a CLOSED/version-0 row transactionally, then follows the same precondition and transition rules as later mutations. No existing tournament receives implicit entries.

Use an append-only Flyway migration for the book, entry records, explicit member snapshots, active-membership claims and audit events. Foreign keys prevent orphan records. Active claims have a unique `(tournament, discipline, playerKey)` constraint across all brackets. RECEIVED/ACCEPTED entries own claims; rejection/withdrawal releases them. Acceptance and correction acquire or replace them atomically. Unsuccessful mutations retain the complete previous state, claims, version and audit history.

All book mutations use one Spring transaction with PostgreSQL REPEATABLE READ isolation and an exclusive lock on the entry-book row. Require the caller's book version, check authority, ensure/lock the book, check intake and domain rules, mutate entry/member/active-claim rows, append the audit event and advance the book version before commit. Competing book versions, serialization/deadlock failures and uniqueness conflicts become 409 after rollback; do not silently retry a user decision using a newer version. The book lock intentionally serializes mutations within a tournament; different tournaments can proceed independently.

Entry commands and reads establish their own transaction (`REQUIRES_NEW`) so an existing caller transaction cannot silently weaken the required isolation. They are independent command boundaries: rolling back a suspended caller transaction does not undo a committed entry mutation. Callers must not assume atomic composition with unrelated work.

REPEATABLE READ gives a consistent configuration snapshot when resolving discipline, bracket, team size and labels for a new/corrected entry, even if another transaction edits the draft. The selection is current at that transaction snapshot, not guaranteed to remain unchanged until commit. A later draft edit does not invalidate or mutate saved entry snapshots. This permits manual reconciliation without changing the existing draft update contract. Private multi-query management reads also use a consistent read-only snapshot, so the returned book version, records and audit belong to one committed view.

### State, privacy and integration

Use RECEIVED, ACCEPTED, REJECTED and terminal WITHDRAWN. Corrections replace content and return the record to RECEIVED; decisions and corrections recheck active claims. Intake gates new records and corrections independently of draft status/visibility and announcement publication. Capacity admission remains a manual organizer decision; no waitlist or capacity guarantee is implied. An accepted entry does not automatically enter a draw roster.

Retain private audit records of successful operation, actor, time and before/after disposition. Do not store historical full content or unnecessary contact data in the audit and do not log payloads or player names. The management API can return retained current records and audit to the creator with no-store responses. No automatic deletion or retention period is selected here; a future retention policy requires an explicit decision and must preserve required accountability.

Business rules and persistence ports remain framework-free. JPA adapters implement the relational transaction and locks; Spring API adapters own transaction configuration and error mapping. Use an injected clock for audit timestamps. Keep existing JWT validation and public announcement route intact; every new endpoint is authenticated and creator-authorized. No new dependencies or modules are required.

## Alternatives and tradeoffs

- Extending the mutable draft roster is rejected: it conflates request/disposition with draw membership and inherits broad draft access.
- Separate per-entry locks are deferred: they complicate cross-entry duplicate membership and intake-versus-create consistency. A single book lock is easier to explain and test, at the cost of one writer per tournament.
- Verified external identities, self-registration and club delegation are deferred pending evidence and explicit ownership/authorization decisions.
- Automatic capacity enforcement and roster synchronization are deferred to preserve a bounded first workflow; the organizer must reconcile them manually.

## Validation and recovery

Test all state/identity rules without frameworks and test actual PostgreSQL transactions for simultaneous first mutations, competing duplicate pairs, acceptance, correction, withdrawal and closure. Use distinct competing payloads and compare complete winning snapshots, version, claims and audit. Demonstrate consistent selection capture during draft changes, unchanged snapshots afterward, migration from V6 and rollback of failed mutations. Verify new private endpoints, foreign IDs, old API/JWT behavior and local real-Keycloak UAT.

Roll out the additive migration and service together. Keep new storage when rolling the application back; do not rewrite shipped migrations or automatically delete entries. Audit records, player snapshots and entry-book storage are private data requiring an explicit future operational retention policy.
