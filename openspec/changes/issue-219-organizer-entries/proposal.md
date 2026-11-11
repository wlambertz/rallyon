# Proposal: organizer-managed entries

## Why

An organizer can configure ID rosters but cannot manage an entry request's disposition, corrections or withdrawal as a separate workflow. Accepted research #213 identifies this gap while leaving verified player identity and delegation unresolved. Primary persona: Organizer. Owning bounded context: Tournament Management.

## What Changes

- Add creator-only management of externally received singles/doubles entries and an independent open/closed intake gate.
- Identify players using explicit tournament-local keys and display-name snapshots supplied by the organizer. These are unverified references, not federation identities or Keycloak accounts.
- Distinguish RECEIVED, ACCEPTED, REJECTED and WITHDRAWN; content corrections return entries to RECEIVED, and withdrawal is terminal for that record.
- Detect active duplicate participant membership within a discipline, independently of pair order or bracket. Defer automatic capacity admission: the organizer decides acceptance; no waitlist or capacity guarantee is provided.
- Keep entry management private and independent of announcement publication, existing tournament lifecycle and draw rosters. Accepted entries do not automatically change roster data.

## Capabilities

### New Capabilities

- `organizer-tournament-entries`: private intake control, recording and disposition of tournament entries, corrections and withdrawals with explicit authority and concurrency handling.

### Modified Capabilities

- None. Existing draft, roster, announcement and JWT contracts remain intact.

## Impact and backward compatibility

Add authenticated APIs, pure domain/use-case logic and JPA persistence within existing `setup.configuration`. An append-only Flyway migration and an ADR for the independent entry boundary are expected and require explicit review. No frontend, shared IAM changes, new dependency/module, public self-registration, invitation, delegation, federation integration or automatic roster synchronization is proposed. The first scenario remains a maintainer-validated hypothesis; no organizer interview is claimed.
