# Design

## Context and constraints

The organizer records entries received outside RallyOn. #213 supports distinguishing requests from accepted rosters and explicit doubles membership, but does not establish an authoritative player directory. The existing draft PUT is authentication-only and can change discipline/bracket configuration; it must not control entry intake, authority or dispositions implicitly.

## Decisions and tradeoffs

### Authority and identity

Only the authenticated positive `rallyon_user_id` equal to stored tournament `createdByUserId` may use any entry or intake operation, including list/read. Caller-supplied organizer IDs and roles alone are not ownership proof. No anonymous route is added. Keep existing shared JWT validation and announcement-only anonymous GET unchanged.

Each player reference has a tournament-local, case-sensitive ASCII key matching `[A-Za-z0-9][A-Za-z0-9_-]{0,63}` and a nonblank display-name snapshot of at most 200 UTF-16 code units, with U+0000 rejected. These keys are organizer-maintained labels; the same person under a different key cannot be detected automatically. Names may differ between historical entry snapshots. There is no verified identity, consent, federation eligibility or global player record. This avoids introducing Player Management while requiring the organizer to reconcile identity manually.

### Content, states and policy

Each entry refers to one existing discipline and bracket selected by ID. Record discipline/bracket display names and team size as immutable intake snapshots. Require one player for singles and two distinct keys for doubles. Explicitly correcting an entry creates fresh selection snapshots and returns it to RECEIVED. WITHDRAWN records cannot be edited or revived; a new record may be created if no active duplicate remains. ACCEPTED and REJECTED decisions are explicit creator actions; repeated decisions to the same state are conflicts. Withdrawal is allowed from any nonwithdrawn state.

An entry's disposition is independent of draw participation. No roster synchronization or automatic capacity enforcement is delivered: the organizer reviews capacity outside this slice. Document this limitation clearly; a request to enforce capacity or automate a draw becomes a separate decision.

Intake starts CLOSED for each tournament. Explicit opening allows new entries and content corrections; closing rejects them while still allowing reads, acceptance/rejection and withdrawal. Reopening is explicit. A successful intake or entry mutation increments a tournament-scoped entry-book version. Every write supplies that version. A single transactional mutation boundary serializes intake checks, duplicate checks and writes so concurrent closure/entry and competing duplicate entries cannot both violate the rules.

An active entry is RECEIVED or ACCEPTED. A player key can appear in at most one active entry per tournament discipline, regardless of bracket or pair order. REJECTED/WITHDRAWN release this constraint; later acceptance of a rejected record must recheck it. Correcting any nonwithdrawn record produces RECEIVED and rechecks active conflicts atomically. Same player may enter a different discipline.

### Existing configuration changes

Create/correct validates the selected discipline/bracket and captures a consistent snapshot. A later draft edit or removal does not change, delete, admit or withdraw entries. Decisions and withdrawals act on the stored snapshot, with no assertion of current eligibility. Configuration changes can therefore require manual organizer reconciliation. Define a PostgreSQL transaction/locking strategy in the ADR that prevents torn configuration reads without silently restricting the existing draft API.

### API and data exposure

Propose creator-only `/api/tournamentmgmt/entries/{tournamentId}` as an entry-book resource: GET returns current intake state, book version and entries; PUT `/intake` sets open/closed; POST `/records` creates; PUT `/records/{entryId}` corrects; PUT `/records/{entryId}/decision` accepts/rejects; DELETE `/records/{entryId}` withdraws. GET may include withdrawn/rejected records for management; use `Cache-Control: no-store`. All mutations require numeric book-version `If-Match`; responses include the new committed book version. No hard delete or public export.

Use 201 for create, 200 for successful reads/other mutations, 400 for missing/malformed preconditions, invalid input or nonexistent selection in a known tournament, 401 for unauthenticated access, 403 for another authenticated user or unusable positive actor identity, 404 for unknown tournament/entry, and 409 for stale version, closed intake, duplicate active membership, illegal transition or already-set intake state. No partial changes or version increments on rejection. A foreign entry ID must not address another tournament's record.

Store event audit information (actor, time, operation and prior/resulting state) with each successful change; do not put display names or payloads into logs. Historical complete content and unrestricted contact data are not needed. Use an injected clock. Timestamps come from the service, not untrusted clients. Keep retained audit and entry records; deletion/retention automation requires a later explicit policy. Only the creator can inspect them in this slice.

## Bounded contexts, invariants and dependency direction

Use the existing configuration module with framework-free entry domain and application ports, API mapping and JPA adapters. The entry book owns intake, entries, active-membership uniqueness and its version independently of the broad tournament DTO. Reference tournament/selection IDs without importing another module's internals. No new context, domain event or dependency is required. A database uniqueness/locking design must protect the same invariants as domain tests; do not rely only on a pre-insert lookup.

## Compatibility and escalation assessment

The new API is additive and authenticated. Existing draft access, roster replacement, lifecycle stubs, announcements and JWT claims do not change. An append-only migration for the entry book, member snapshots and audit plus an ADR are required and explicitly escalated at G2. No Player Management service, shared IAM edit, new dependency, module or frontend stack is proposed. Stop for any such need or an implementation-time spec ambiguity.

## Validation, rollout and recovery

Map every ENT scenario to domain/use-case, HTTP/security and PostgreSQL persistence evidence. Verify closure-versus-create, duplicate pairing and reversed pair order, concurrent decisions/corrections, complete winner snapshots and rollback/version preservation. Test current configuration replacement independently and existing endpoints as regressions. Validate V6-to-next migration on a dedicated PostgreSQL database without changing shipped migrations. Run IAM install if needed, service Maven `clean verify`, root formatting and strict OpenSpec checks. Use isolated local Keycloak/Compose and PostgreSQL for organizer UAT. Keep additive storage on application rollback; no destructive migration rollback or data deletion is assumed.

## ADR assessment

An ADR is required before implementation for the entry-book boundary, local identity/provenance, duplicate and capacity policy, transaction/locking strategy, audit retention and separation from mutable configuration, roster and future Player Management. This design proposes the decisions; implementation is not yet authorized.
