## Purpose

Enable a tournament creator to publish, correct and withdraw a deliberately authored, limited announcement, so prospective entrants can retrieve its current public version.

## ADDED Requirements

### Requirement: Publish deliberately authored content (ANN-001)

The service SHALL let the authenticated creator of an existing tournament publish a separately supplied announcement containing a nonblank title, event start and end, nonblank venue and nonblank plain-text body. The end MUST NOT precede the start. The publication SHALL be an atomic snapshot and MUST NOT silently copy the configuration's description, rosters or other private fields.

#### Scenario: First publication

- **WHEN** the authenticated creator publishes valid explicit content for an existing tournament with no announcement
- **THEN** the service saves one current announcement with a version and publication timestamp and returns its public link and version

#### Scenario: Invalid content

- **WHEN** the creator publishes missing, blank, over-limit or chronologically invalid content
- **THEN** the service rejects the request without changing any existing published version

#### Scenario: Existing announcement

- **WHEN** the creator attempts initial publication for a tournament with an existing announcement
- **THEN** the service returns a conflict without replacing it

### Requirement: Bound announcement input (ANN-005)

The service SHALL limit the title to 200, venue to 500 and body to 10,000 characters. These fields MUST NOT contain the NUL character (U+0000). Event instants SHALL be within the inclusive UTC range 4713-01-01 BC 00:00:00 to 294276-12-31 AD 23:59:59.999999; out-of-range requests SHALL be rejected with 400 without changing an announcement.

#### Scenario: Reject oversized text

- **WHEN** any supplied announcement text exceeds its limit
- **THEN** the service rejects the request without changing the published version

#### Scenario: Reject a NUL character

- **WHEN** a title, venue or body contains U+0000
- **THEN** the service rejects the request with 400 without creating or changing an announcement

#### Scenario: Reject an event time outside the supported range

- **WHEN** either supplied event instant is outside the supported range
- **THEN** the request is rejected and no announcement is created or changed

### Requirement: Restrict publication authority (ANN-002)

Only the authenticated user whose positive `rallyon_user_id` equals the tournament's recorded creator SHALL publish or correct its announcement. This authority MUST NOT be inferred from caller-supplied organizer ID or role alone. Existing draft and configuration authorization contracts SHALL remain unchanged.

#### Scenario: Another authenticated caller

- **WHEN** a caller other than the recorded creator attempts to publish or correct an announcement
- **THEN** the operation is rejected and no announcement changes

#### Scenario: Anonymous or missing identity

- **WHEN** a caller without a valid authenticated numeric user ID attempts a write
- **THEN** the operation is rejected and no announcement changes

#### Scenario: Recover the current withdrawn version

- **WHEN** the creator requests the current announcement through the authenticated management operation
- **THEN** the service returns its current content, publication state and version, including after withdrawal
- **AND** another caller cannot use that operation to retrieve withdrawn content

### Requirement: Replace a current announcement safely (ANN-003)

The creator SHALL be able to replace the complete published content by supplying its current version. A successful replacement SHALL atomically advance that version and timestamp; a stale version or concurrent losing write MUST be rejected without altering the winning version. Replacement of a withdrawn announcement SHALL explicitly republish it.

#### Scenario: Correct the venue

- **WHEN** the creator submits valid replacement content with the current version and a corrected venue
- **THEN** subsequent public reads return the corrected content and advanced version

#### Scenario: Competing corrections

- **WHEN** two writes use the same current version concurrently
- **THEN** at most one succeeds and the other reports a conflict without losing the successful update

### Requirement: Return only intended public information (ANN-004)

Anyone SHALL be able to retrieve the current published announcement at its public link as readable UTF-8 plain text. The response MUST contain only the intentionally supplied announcement, its version and publication timestamp, and MUST NOT expose configuration, rosters, identity claims, audit user IDs or withdrawn material. Nonexistent, unpublished and withdrawn announcements SHALL be indistinguishable as not found.

#### Scenario: Public read

- **WHEN** an unauthenticated reader opens the link for a published announcement
- **THEN** the current intended announcement is readable as plain text without authentication or unrelated data

#### Scenario: Tournament visibility changes

- **WHEN** any caller changes the associated tournament's draft visibility
- **THEN** the announcement's independent publication state does not change

#### Scenario: Unpublished or missing tournament

- **WHEN** the reader requests a missing or unpublished announcement
- **THEN** the service returns not found without distinguishing these cases

### Requirement: Withdraw and republish deliberately (ANN-006)

Only the authenticated creator SHALL withdraw a published announcement by supplying its current version. Withdrawal SHALL atomically advance the version and hide the public content; it MUST NOT destroy the retained snapshot. Only an explicit creator-authorized replacement with the current withdrawn version SHALL republish it. Tournament draft visibility SHALL NOT publish or withdraw an announcement.

#### Scenario: Withdraw and explicitly republish

- **WHEN** the creator withdraws the current version
- **THEN** the public link returns not found until the creator explicitly republishes with the current withdrawn version

#### Scenario: Reject stale withdrawal

- **WHEN** a withdrawal uses a stale version or loses a concurrent write
- **THEN** it reports a conflict without changing the winning version or publication state

#### Scenario: Unauthorized withdrawal

- **WHEN** an anonymous caller or another authenticated user attempts to withdraw
- **THEN** the operation is rejected and the published state is unchanged
