# Refinement: creator-managed participant fields

## Problem and evidence

Accepted [#185 findings](../issue-185-draw-structures/findings.md) recommend a deliberate participant-field boundary before drawing. The implemented #219 entry book stores local string player keys, explicit one/two-member entries, selection snapshots and book versions. Acceptance does not populate draw rosters. Existing `ParticipantsRoster` instead carries numeric player/team references; mapping local keys into those IDs would invent identity semantics. Format enums and phase stubs do not supply an executable draw workflow.

An organizer needs to state which accepted playing units are intended for one bracket, retain that decision and recognize when later entry or configuration changes require reconciliation. A saved field must remain distinct from received/accepted entries, a mutable legacy roster, verified player eligibility and an actual draw. This scenario is a product hypothesis supported by the code gap and research, not measured organizer demand or a federation obligation.

## Primary persona and owning app/context

Primary persona: the tournament creator acting as organizer. Candidate owning context: Tournament Management. Determine the internal consistency/module boundary through the design and ADR before implementation. Player Management and Scoring remain placeholders. No organizer frontend stack is selected; the first slice is backend/API with local authenticated walkthroughs. Hall-device and connectivity assumptions from the personas are unvalidated and do not justify offline synchronization in this slice.

## Scope and exclusions

Propose creator-only preparation, private inspection and explicit replacement of a versioned participant field for one tournament discipline/bracket. The creator deliberately selects current ACCEPTED entry records; preserve their source references, member keys/display names and selection snapshots. One singles entry is one playing unit; a doubles entry retains both members as one playing unit. Identity remains tournament-local and unverified. Never cast/hash these keys into legacy numeric roster IDs.

Before implementation, decide field identity and revisions, current-source checks, configuration compatibility, duplicate membership, selection limits and manual capacity policy. Define observable handling of entry correction/withdrawal and bracket edits after preparation. Saved field content must not silently follow those changes; the creator must be able to detect divergence and deliberately reconcile it. Specify stale/invalid/foreign-actor rejection and complete-state preservation for failed competing writes. Preserve private authority rather than inheriting the broad legacy configuration DTO's access.

Keep preparation independent of entry acceptance and intake state. No automatic inclusion of newly accepted records, removal of withdrawn records or rewriting of a previously saved field is assumed. A field is preparation data, not proof of eligibility, readiness to play or publication. Exact operation/status names and how divergence is represented belong to G2; adding a draw lock or running-match lifecycle is outside this scope.

Exclude draw generation/preview, seeding/ranking/bye algorithms, group advancement, match fixtures/results/standings, scheduling, automatic capacity admission/waitlists, federation integration, verified accounts/global player or team registries, club delegation, public participant publication, frontend and offline sync. Preserve existing entry, draft, announcement, REST/JWT and CLI contracts. Assess any additive migration, transaction boundary, new API/module/dependency and privacy/retention decision explicitly; obtain an ADR before implementation.

## Success criteria

- The creator can deliberately save and privately inspect a participant-field selection for one compatible bracket, retaining singles/doubles membership and unverified local identity without altering entries or legacy rosters.
- The specification gives source/field revisions and explicit replacement semantics; stale, invalid, duplicate and unauthorized requests do not overwrite or disclose data.
- Entry correction/withdrawal and draft changes have observable divergence/reconciliation scenarios; existing field snapshots are not silently rewritten.
- Capacity remains an organizer decision, with any selection limit clearly specified rather than inferred from international rules. The feature does not claim compliance or generate a draw.
- Resolve ownership, consistency, retention and backward compatibility through reviewed design/ADR. Validate the approved scenarios through domain, persistence, API/security and concurrent-write checks plus real local authenticated UAT. Tests must prove full-state preservation on losing/rejected writes.

## Assumptions and agreed budget

The initial actor is the recorded tournament creator; delegation and verified participant identity are future decisions. Manual selection from accepted entries is the proposed first workflow. Empty selections, required source-version inputs, incompatible selection handling, revision retention and the meaning of “current” remain explicit G2 decisions rather than implementation assumptions. No timebox or numerical budget has been agreed.

Track: **full** (`rallyon-feature`), because this is new private, versioned behavior with persistence and authorization implications. G1 approves the investigation/feature scope, not implementation details, migrations, further issue creation, deployment or merge. Starting this lifecycle does not constitute its scope approval.

## G1 decision

Pending explicit human approval of this scope. Record the actual quote and source through the lifecycle helper before preparing specifications.
