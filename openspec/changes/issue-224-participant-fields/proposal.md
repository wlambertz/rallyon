# Proposal: creator-managed participant fields

## Why

The organizer needs an explicit retained decision about which accepted entries form the playing units of a bracket. The #219 entry book stores tournament-local keys and pairs; legacy roster IDs have different semantics. Accepted #185 research recommends a separate participant-field snapshot before drawing. Neither an entry's acceptance nor a format enum supplies this decision. Demand remains a persona hypothesis, not interview evidence.

Primary persona: Organizer, initially the recorded tournament creator. Owning bounded context: Tournament Management, within existing `setup.configuration` for this bounded preparation capability.

## What Changes

- Add a private participant field identified by tournament, discipline and bracket. The creator deliberately prepares or replaces its selected ACCEPTED entries, including zero entries for explicit clearing.
- Retain selected single/pair member snapshots, current bracket/discipline labels and team size, source entry IDs, source draft/book versions and an independent field version. Local player keys remain unverified; they never become global player/team IDs.
- Compare source and field versions in a consistent snapshot. A field read reports UNPREPARED, CURRENT or OUTDATED. Any newer draft/book version conservatively marks a prepared field outdated; saved contents do not change automatically.
- Permit explicit reconciliation by replacing the field using current source versions and valid entries, independent of entry intake state. Require the creator, current field version and both source versions; reject invalid/stale/concurrent operations atomically.
- Retain only the latest field contents plus minimal private preparation audit. No automatic deletion/retention period or revision-history API is introduced.

## Capabilities

New capability: `organizer-participant-fields`, requirements PF-001–007. No existing requirement is modified.

## Scope and exclusions

Backend/API only. No draw, seeding, byes, fixture/result/standings engine, public participant list, automatic capacity admission, global identity mapping, club delegation, new context, external integration, frontend or offline synchronization. Empty/single-unit fields are preparation data and do not assert draw readiness. Membership ordering has no seed or draw meaning.

## Compatibility, architecture and validation

Existing entry/draft/announcement behavior, REST endpoints, JWT claims/roles and CLI contracts remain unchanged. Add authenticated creator-only endpoints and an append-only Flyway migration. **Human review is required for the migration and ADR** covering independent field ownership, version/freshness semantics, transaction snapshot guarantees and audit retention. No shared IAM changes, dependency or module additions are proposed.

Validate pure selection/version/freshness rules, HTTP authority/error/privacy behavior, actual PostgreSQL V7-to-next migration and atomic concurrent field preparation, independent reads and source edits. Full backend `clean verify`, root formatting, strict OpenSpec validation and real local Keycloak UAT follow implementation and the remaining human gates. The proposed artifacts do not authorize implementation.
