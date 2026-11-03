# tournament-draft-update Specification

## Purpose

Describe full replacement of editable draft configuration through the existing REST and configuration-service contracts. This is not a partial merge; see [the migration inventory](../../../docs/legacy-spec-migration.md).

## Requirements

### Requirement: Replace a draft configuration

The service SHALL accept `PUT /api/tournamentmgmt/config/{tournamentId}/draft` with a Tournament body and numeric `If-Match` version, replace its editable fields, preserve stored identity and creation metadata, and record modification audit data.

#### Scenario: Replace existing configuration

- **WHEN** an authenticated caller with a resolvable principal submits valid replacement configuration for an existing DRAFT with its current If-Match version
- **THEN** the response is 200 OK with the saved tournament; its ID, organizer ID, status and creation metadata are preserved and editable fields come from the replacement

#### Scenario: Clear omitted optional configuration

- **WHEN** a valid replacement omits an optional field previously present in the stored draft
- **THEN** the omitted field is cleared according to the replacement representation rather than merged from the stored draft

#### Scenario: Reject anonymous replacement

- **WHEN** a caller submits a replacement without authentication
- **THEN** the security filter returns 401 Unauthorized

### Requirement: Reject conflicts and missing drafts

The service SHALL reject non-DRAFT tournaments and stale versions as conflicts and unknown tournament IDs as not found, after validating replacement basics.

#### Scenario: Reject non-DRAFT state

- **WHEN** a valid replacement targets a tournament outside DRAFT status
- **THEN** DraftUpdateConflictException is translated to 409 Conflict without saving

#### Scenario: Reject stale version

- **WHEN** a valid replacement for a DRAFT supplies an If-Match version different from its stored version
- **THEN** DraftUpdateConflictException is translated to 409 Conflict without saving

#### Scenario: Reject missing tournament

- **WHEN** a valid replacement targets an unknown tournament ID
- **THEN** TournamentNotFoundException is translated to 404 Not Found

### Requirement: Validate replacement values

The service SHALL reject invalid replacement basics or configuration with InvalidDraftUpdateException, translated to 400 Bad Request.

#### Scenario: Reject invalid schedule

- **WHEN** a replacement contains a schedule with an end before its start
- **THEN** the update fails with 400 Bad Request without saving

#### Scenario: Reject invalid basics

- **WHEN** a replacement has no name or visibility, a blank or excessive name, or unsupported nonempty phases
- **THEN** the update fails with 400 Bad Request before looking up the tournament
