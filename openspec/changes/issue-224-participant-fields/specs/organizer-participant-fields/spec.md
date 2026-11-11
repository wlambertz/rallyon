## Purpose

Let a tournament creator deliberately retain a private, versioned selection of accepted single/pair entries for one bracket, independently of entry disposition, legacy rosters and a future draw.

## ADDED Requirements

### Requirement: Restrict participant-field management to the creator (PF-001)

Only the authenticated positive user identity matching the stored tournament creator SHALL inspect or prepare a participant field and its audit. Anonymous, missing/nonpositive identity and unrelated authenticated actors MUST NOT obtain private field contents or mutate them. A caller-supplied organizer identity MUST NOT confer authority.

#### Scenario: Creator reads the field

- **WHEN** the stored creator reads a field with a valid identity
- **THEN** its saved contents, versions and snapshot freshness are returned privately

#### Scenario: Anonymous or unrelated actor

- **WHEN** an anonymous or unrelated actor reads or prepares a field
- **THEN** access is rejected without disclosing private contents or changing field state, sources or audit

### Requirement: Prepare an explicit bracket selection from accepted entries (PF-002)

A field SHALL be identified by tournament, discipline and bracket. Preparation SHALL explicitly select distinct positive tournament-local ACCEPTED entry IDs, optionally none, matching current selection IDs and team size in one source snapshot. It SHALL preserve single/pair members and distinguish original entry provenance from the current target selection. Local keys MUST NOT imply verified global identities or seed/placement order.

#### Scenario: Explicit singles or doubles subset

- **WHEN** the creator prepares a valid subset of current accepted entries for a compatible bracket
- **THEN** only those units are retained with their original members/provenance and current target selection
- **AND** other accepted entries are not implicitly included
- **AND** singles retain one member, doubles two distinct keys, and every key/name satisfies existing entry validity rules
- **AND** units are saved in ascending entry-ID order, preserving each entry's member order and source ID
- **AND** the current target labels/team size and each entry's original selection and key/display-name snapshots remain distinguishable even after a label-only rename

#### Scenario: Rejected or mismatched selection

- **WHEN** a selected record is unknown, nonaccepted, from another bracket, or incompatible with current team size, or the request repeats an ID/member
- **THEN** the complete preparation is rejected without partial contents, audit or version changes
- **AND** missing/null/nonpositive ID input and overlapping player keys across selected units are rejected too

#### Scenario: Empty or one-unit selection

- **WHEN** the creator explicitly prepares an empty or one-unit selection for an existing bracket
- **THEN** it is saved as preparation data without claiming a draw is ready

### Requirement: Keep independent field versions and source provenance (PF-003)

Preparation SHALL atomically advance an independent field version and retain the observed draft/book versions. The expected field and both source versions MUST match one consistent command snapshot. An unprepared field SHALL have virtual version 0 without storage. Reads/commands SHALL expose their observed source versions without promising that sources stay unchanged after the snapshot.

#### Scenario: Initial virtual state

- **WHEN** an existing bracket has no prepared field
- **THEN** its read reports UNPREPARED/version 0 with no saved selection/source versions, empty units/audit and no created rows
- **AND** a missing entry-book row is observed as book version 0 without being created by field operations

#### Scenario: First preparation and explicit reconciliation

- **WHEN** a valid first preparation uses field version 0 and current source versions, or a replacement uses the current field/source versions
- **THEN** the complete field is saved with the next field version and observed source versions
- **AND** identical content and explicit clearing also increment the field version

#### Scenario: Concurrent source commit

- **WHEN** a source edit commits after the preparation's consistent snapshot
- **THEN** preparation may commit from that earlier snapshot and become outdated immediately
- **AND** its response identifies the versions it observed rather than claiming to freeze future source changes

#### Scenario: Stale or missing version input

- **WHEN** a required expected version is absent, invalid or differs from the snapshot's version
- **THEN** preparation is rejected without changing the field, sources or audit

#### Scenario: Competing field preparations

- **WHEN** two preparations target the same field version
- **THEN** at most one commits and a loser reports conflict without changing any winning selection, member, provenance, version or audit

### Requirement: Expose conservative snapshot freshness without silent mutation (PF-004)

A prepared field SHALL report CURRENT only when its saved draft/book versions equal both observed versions in one read snapshot; otherwise OUTDATED. Source changes MUST NOT advance its field version or rewrite saved content/audit. Explicit preparation SHALL reconcile it using current valid sources. Freshness SHALL describe the response snapshot, not draw readiness or continued currentness.

#### Scenario: Source withdrawal, correction or draft edit

- **WHEN** a source operation advances the draft/book version after preparation
- **THEN** the next field read reports OUTDATED while preserving its complete saved version/content/audit
- **AND** no historical source reconstruction or automatic future-draw invalidation is implied

#### Scenario: Unrelated intake or draft edit

- **WHEN** only intake state or an unrelated draft field changes and advances its source version
- **THEN** the prepared field is conservatively reported OUTDATED

#### Scenario: Bracket removal and replacement

- **WHEN** the configured bracket is removed
- **THEN** an existing field remains readable and outdated, while preparation is rejected
- **AND** after a compatible bracket exists again the creator may deliberately reconcile using current source versions
- **AND** the retained private read includes the original saved selection and units

### Requirement: Keep intake, admission and existing contracts independent (PF-005)

Field preparation SHALL be available with open or closed intake. It SHALL NOT change entry disposition, intake, draft/legacy rosters, announcements, other participant fields or existing REST/JWT/CLI behavior. Acceptance/withdrawal SHALL gain no field synchronization side effect. Capacity admission remains manual; this feature SHALL NOT enforce configured capacity, create a waitlist or certify eligibility/consent. No field SHALL become public or cause draw/seed/match creation.

#### Scenario: Closed intake and manual capacity

- **WHEN** the creator prepares otherwise valid accepted entries with closed intake or a selected count exceeding configured capacity
- **THEN** preparation succeeds without reopening intake or claiming capacity compliance
- **AND** the response identifies capacity review as manual

#### Scenario: Acceptance does not prepare a field

- **WHEN** an entry is accepted after a field was prepared
- **THEN** the field's saved units/version/audit remain unchanged and its freshness follows PF-004

### Requirement: Retain minimal private preparation accountability (PF-006)

Each successful preparation SHALL atomically retain field version, actor, time, operation PREPARE, selected-unit count and source versions in private audit. Only the latest full field contents SHALL be retained by this capability. Earlier complete contents, contact data and public audit SHALL NOT be introduced. Failed commands SHALL create no audit event. No retention/deletion automation is selected; operational retention requires a future explicit decision.

#### Scenario: Replacement preserves accountability

- **WHEN** the creator replaces or clears a field
- **THEN** its latest content replaces the earlier content and the new successful operation is appended to private audit with its actor, time and revisions
- **AND** audit does not reproduce historical member names or full field contents

### Requirement: Provide explicit private API outcomes (PF-007)

All field responses SHALL request no caching and expose explicit authentication, authority, missing-target, invalid-input and conflict outcomes. Errors MUST NOT echo private member contents or database internals. The management API SHALL be additive with no publicly readable field endpoint.

#### Scenario: Authentication and authority outcomes

- **WHEN** authentication is absent/invalid or a valid authenticated identity lacks creator authority
- **THEN** the response is respectively 401 or 403 without private contents

#### Scenario: Missing field versus missing target

- **WHEN** the creator reads an existing bracket without a prepared field
- **THEN** the response is 200 with UNPREPARED/version 0
- **AND** an unknown unprepared bracket or tournament returns 404

#### Scenario: Invalid or conflicting request

- **WHEN** malformed syntax, invalid selection or a stale/conflicting command is submitted
- **THEN** the corresponding 400 or 409 response requests no caching and exposes no private member content
- **AND** malformed input/selection/version syntax and unknown selected IDs return 400 without crossing tournament boundaries
- **AND** stale versions, nonaccepted/incompatible source records and concurrent writes return 409

#### Scenario: Private read after removal

- **WHEN** the creator reads an existing saved field whose bracket was removed
- **THEN** the response is 200 with its retained private snapshot and OUTDATED status
