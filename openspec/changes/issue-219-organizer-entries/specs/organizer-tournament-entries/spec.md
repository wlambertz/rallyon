## Purpose

Let an organizer manage externally received tournament entries privately, with explicit player references, disposition and intake control, independently of draw rosters and public announcements.

## ADDED Requirements

### Requirement: Restrict entry management to the creator (ENT-001)

All intake and entry operations SHALL require a valid authenticated positive user ID equal to the tournament's stored creator. Another user's role or supplied organizer ID MUST NOT authorize access. No entry management data SHALL be public.

#### Scenario: Another user attempts access

- **WHEN** another authenticated user attempts to read or change the entry book
- **THEN** access is rejected without exposing its content or changing data

#### Scenario: Anonymous management request

- **WHEN** an unauthenticated caller requests an entry operation
- **THEN** the request is rejected before reading or writing entry data

### Requirement: Record explicit local identity and selection (ENT-002)

An entry SHALL contain a current discipline/bracket selection and one player for singles or two distinct players for doubles. Players SHALL have case-sensitive tournament-local keys matching `[A-Za-z0-9][A-Za-z0-9_-]{0,63}` and nonblank display names of at most 200 UTF-16 code units without U+0000. These references MUST NOT imply verified identity, consent or account ownership.

#### Scenario: Record singles or doubles

- **WHEN** the creator supplies valid player references and an existing compatible selection while intake is open
- **THEN** a RECEIVED entry with a stable record ID and selection/name snapshots is created

#### Scenario: Reject invalid input

- **WHEN** a player count, key, display name, distinctness or selection is invalid
- **THEN** the request is rejected without creating an entry or advancing the book version

### Requirement: Manage entry disposition deliberately (ENT-003)

The creator SHALL be able to accept or reject any nonwithdrawn entry, correct it while intake is open, and withdraw it. A correction SHALL replace its content and reset it to RECEIVED. Withdrawal SHALL be terminal for that record. A repeated same-state decision or modification of a withdrawn record SHALL be a conflict.

#### Scenario: Correct an accepted entry

- **WHEN** the creator corrects an ACCEPTED entry with a current version and valid replacement while intake is open
- **THEN** the replaced entry becomes RECEIVED and requires a new acceptance decision

#### Scenario: Withdraw and preserve the record

- **WHEN** the creator withdraws a nonwithdrawn entry
- **THEN** its state becomes WITHDRAWN and it remains inspectable privately
- **AND** later correction or acceptance of that record is rejected

#### Scenario: Accept or reject

- **WHEN** the creator chooses a different ACCEPTED or REJECTED disposition for a nonwithdrawn entry
- **THEN** the state changes if all active-membership rules hold

### Requirement: Control intake independently (ENT-004)

Intake SHALL initially be CLOSED. Only explicit creator actions SHALL open, close or reopen it. Closed intake SHALL reject new entries and content corrections while allowing management reads, disposition decisions and withdrawal. Draft visibility, announcement publication and tournament status MUST NOT implicitly change intake.

#### Scenario: Close before a new entry

- **WHEN** intake has closed before a new entry commits
- **THEN** the new entry is rejected without changing the entry book

#### Scenario: Withdraw after closure

- **WHEN** intake is closed and the creator withdraws a current nonwithdrawn entry
- **THEN** withdrawal succeeds without reopening intake

### Requirement: Prevent duplicate active membership (ENT-005)

A player key SHALL belong to at most one RECEIVED or ACCEPTED entry per tournament discipline, across brackets and pair ordering. REJECTED and WITHDRAWN entries SHALL release that restriction. Acceptance and correction SHALL recheck it atomically. Different disciplines MAY reuse a key. Automatic capacity admission or roster synchronization SHALL NOT occur.

#### Scenario: Reversed doubles or shared player

- **WHEN** a new or corrected entry overlaps an active player key in the same discipline
- **THEN** it is rejected regardless of bracket or ordering of the pair

#### Scenario: Reaccept a rejected duplicate

- **WHEN** a rejected entry is accepted while its player key is active in another entry in the same discipline
- **THEN** acceptance is rejected without changing either entry

### Requirement: Preserve atomic versions and audit (ENT-006)

Every mutation SHALL require the current tournament-scoped entry-book version. Successful mutations SHALL atomically advance it and retain actor, time, operation and prior/resulting disposition in a private audit record. Stale, invalid or losing concurrent mutations SHALL preserve the complete winning book and version. All management responses SHALL request no caching.

#### Scenario: Concurrent corrections or admission

- **WHEN** two writes use the same current book version
- **THEN** at most one commits and the other reports a conflict without losing any winning content

#### Scenario: Concurrent closure and entry creation

- **WHEN** closure and creation compete on the same book version
- **THEN** at most one commits and no entry commits against a closed intake state

### Requirement: Preserve recorded selection and existing contracts (ENT-007)

Entries SHALL retain their recorded selection and player snapshots after unrelated draft edits. Reading, deciding or withdrawing them MUST NOT silently adopt changed configuration. New or corrected entries SHALL validate a consistent current selection. Entry operations MUST NOT mutate existing draw rosters, public announcements or existing REST/JWT behavior.

#### Scenario: Draft selection is removed

- **WHEN** another draft edit removes an entry's referenced bracket
- **THEN** the recorded entry remains privately inspectable and withdrawable with its prior snapshot
- **AND** correcting it requires a valid current selection

#### Scenario: Accept without roster synchronization

- **WHEN** the creator accepts an entry
- **THEN** its disposition changes without adding players or teams to an existing draw roster or publishing their data
