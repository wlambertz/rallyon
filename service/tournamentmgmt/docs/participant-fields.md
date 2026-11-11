# Private participant-field preparation

A participant field is the tournament creator's explicit selection of accepted entries for one discipline/bracket. It is preparation data, not a draw, verified eligibility decision or public participant list. An entry's acceptance does not automatically add it to a field or to legacy draw rosters.

Only the stored tournament creator with a valid positive authenticated `rallyon_user_id` may read or prepare a field. Every field response requests `Cache-Control: no-store`. Existing configuration reads and draft updates keep their current access contract; private field members are not added to the legacy `Tournament` response.

## Read and prepare

Paths are relative to `/api/tournamentmgmt/participant-fields/{tournamentId}`:

| Method | Inputs | Result |
| --- | --- | --- |
| `GET` | Query `disciplineId` and `bracketId` | Current retained field, observed source versions, freshness and private audit. |
| `PUT` | JSON selectors, explicit entry IDs, both source versions; numeric `If-Match` field version | Atomically prepare or replace the latest field selection. |

An example preparation body:

```json
{
  "disciplineId": 1,
  "bracketId": "open",
  "entryIds": [8, 12],
  "sourceTournamentVersion": 4,
  "sourceEntryBookVersion": 17
}
```

The header `If-Match: 2` in this example means **participant-field version 2**. It is independent of draft version 4 and entry-book version 17. Read current sources and deliberately reconcile after a conflict; an automatic retry must not silently adopt new versions.

Body IDs and source versions require integer JSON tokens within the signed 64-bit range. Numeric strings, fractional or exponent notation, booleans and null/missing values are rejected rather than coerced. `entryIds` must be an array of integer tokens; `bracketId` must be a JSON string. These parsing rules apply only to participant-field preparation.

Select distinct positive tournament-local IDs of currently ACCEPTED entries matching the selected current discipline/bracket IDs and team size. `entryIds: []` explicitly prepares an empty field or clears the latest field. A one-unit field is also valid; neither case asserts draw readiness. Missing/null ID lists, unknown/repeated IDs and invalid values are rejected. Singles retain one player; doubles retain both distinct local members. Saved units use ascending entry-ID order, which has no seed or draw-position meaning; member order within a pair is preserved.

Player keys and names remain unverified organizer-recorded snapshots. No local key is cast or hashed into a global player or team ID. The field's target `selection` contains current labels/team size, while each unit's `selection` retains its original entry selection. A label-only rename does not require an artificial entry correction; both provenances remain distinguishable.

## Versions and freshness

An existing bracket without a prepared field reads as `prepared: false`, version 0, `freshness: UNPREPARED`, no saved selection/source versions and empty units/audit. This read creates no storage. A missing entry-book row is observed as book version 0 without creating it. A successful first preparation creates field version 1; every later successful preparation increments it, including identical selections and explicit clearing.

A prepared field reads as CURRENT only when its saved draft and entry-book versions equal both observed source versions. Any version difference produces OUTDATED, including an unrelated draft edit or intake change. This intentionally conservative check does not identify which member changed. A source change does not rewrite saved field members, version or audit. Explicit PUT with current source/field versions reconciles the selection.

The versions and freshness describe one consistent transaction snapshot. Source rows are not frozen until preparation commits: a concurrent withdrawal or draft edit may make a just-prepared field immediately outdated. A subsequent read exposes that difference. CURRENT is not a promise that sources will remain unchanged or that a draw is ready. Commands establish an independent transaction; an outer caller's later rollback does not undo a committed preparation.

An existing saved field remains readable after its configured bracket is removed, retaining its original snapshots and reporting OUTDATED. Preparation requires a current compatible bracket and valid accepted entries. An unknown bracket with no saved field returns 404. Fields have independent versions; there is no tournament-wide draw lock or cross-field participant reservation.

## Manual decisions, audit and outcomes

Preparation is available with open or closed entry intake and returns `capacityReview: MANUAL`. It does not enforce configured capacity, create a waitlist, verify identity or partner consent, or synchronize accepted/withdrawn entries automatically. The creator remains responsible for reviewing admission and reconciling later changes. No draw, seeds, matches, results or public publication are created.

Only the latest full field contents are retained. The private audit appends each successful PREPARE with field version, actor, time, unit count and both source versions; it does not reproduce historical names or full member payloads. Failed commands create no event and preserve the complete prior/winning field. Saved source versions do not imply that historical drafts or entry books can be reconstructed. A future operational retention/deletion policy requires an explicit decision.

Responses distinguish 401 for absent/invalid authentication, 403 for missing/nonpositive or unrelated identity, 404 for an unknown tournament or unprepared bracket, 400 for malformed input/preconditions or unknown selected IDs, and 409 for stale versions, nonaccepted/incompatible selected records or competing writes. Unknown/malformed bracket selection on PUT is 400. Error bodies are sanitized; no member contents or database internals are echoed. See [ADR 0007](../../../docs/adr/0007-participant-field-preparation.md).
