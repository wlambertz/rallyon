# Proposal: publish and correct a tournament announcement

## Why

An organizer cannot use the existing unimplemented `publish` operation to make an announcement available and correct it. The [research](../issue-212-publication-research/findings.md) identifies a club announcement as an illustrative case but does not validate demand or select a production target. The primary persona is the organizer; the candidate owning bounded context is Tournament Management.

## What Changes

Propose a small, independently authored announcement associated with a tournament: its creator explicitly publishes, corrects or withdraws it. The announcement has its own publication state, independent of the tournament draft's visibility. Prospective entrants receive a readable plain-text public link. No discovery listing, organizer frontend, registration, third-party submission or notification is included.

Limit structured content to title, event date/time, venue, and a bounded plain-text announcement body; the body can describe event-specific classes, eligibility, deadlines, fees and contact details. These are product choices for the first slice, not a claim of legal compliance. The creator explicitly reviews and supplies the content; the existing broadly editable draft is not silently treated as trusted public content.

## Capabilities

### New Capabilities

- `tournament-announcement-publication`: publish, correct, withdraw and explicitly republish an announcement; retrieve its readable public representation; reject invalid, unauthorized or conflicting requests.

### Modified Capabilities

- None proposed. Draft creation, replacement and authenticated retrieval keep their current contracts. The existing unimplemented `publish` method is not repurposed implicitly; publication here concerns an announcement, and the relationship to tournament status requires a future explicit decision.

## Impact and backward compatibility

Add a dedicated announcement API and persistence without changing existing REST or JWT contracts. Public access requires a narrowly scoped service security change, subject to explicit G2 approval. An append-only Flyway migration and architectural decision are expected and require escalation. No shared IAM, new library, module, organizer UI or external integration is proposed.
