# Organizer-managed tournament entries

The entry book privately records requests received outside RallyOn. Only the authenticated user who created the tournament may inspect or change it. An entry is not a verified player identity, a public announcement or an accepted draw-roster member.

Each player has a case-sensitive tournament-local key (`[A-Za-z0-9][A-Za-z0-9_-]{0,63}`) and a nonblank display-name snapshot of at most 200 UTF-16 code units without NUL. The organizer maintains these keys; a different key for the same real person is not automatically detected. Singles need one player; doubles need two distinct keys. Selection uses the current discipline/bracket and captures its labels and team size as a snapshot.

All paths below are relative to `/api/tournamentmgmt/entries/{tournamentId}`. Every operation requires the tournament creator's authenticated positive `rallyon_user_id`. Reads return `Cache-Control: no-store`; none of these routes is public.

| Method and path | Purpose |
| --- | --- |
| `GET` | Read intake state, book version, records and audit. Initially CLOSED, version 0, empty. |
| `PUT /intake` | Set `{ "open": true }` or false. |
| `POST /records` | Record an explicit entry, initially RECEIVED. |
| `PUT /records/{entryId}` | Replace content; the resulting state is RECEIVED. |
| `PUT /records/{entryId}/decision` | Set `{ "state": "ACCEPTED" }` or REJECTED. |
| `DELETE /records/{entryId}` | Withdraw, retaining the record privately. |

Every mutation requires numeric `If-Match` containing the **book version**, not the tournament version. A successful response returns the new book version. On a conflict, read the current book and reconcile deliberately; no automatic retry adopts a newer version. Unknown tournament/entry is 404, unauthorized identity is rejected, invalid input is 400, and stale versions, illegal transitions, closed intake or active duplicate membership are 409.

An entry input contains `disciplineId`, `bracketId` and `players`, e.g. `{"disciplineId":1,"bracketId":"A","players":[{"key":"player-1","displayName":"Example Player"}]}`. Correcting an accepted entry requires another acceptance decision. WITHDRAWN is terminal; a new record can be created after release of the previous active membership. Rejected entries may be accepted later only if their players are not already active elsewhere in that discipline.

RECEIVED and ACCEPTED records reserve player keys in their discipline, across brackets and regardless of pair order. Rejection and withdrawal release them. The same player key may enter another discipline. **Capacity admission remains manual, and accepting an entry does not update draw rosters.** No waitlist, verified account binding, partner consent, self-registration or invitation is provided.

Intake starts closed. Opening allows new records and content corrections. Closing blocks those operations, while reads, decisions and withdrawal remain available. Intake is independent of tournament visibility/status and announcement publication. Draft edits do not change saved entry snapshots; reconciling removed brackets or changed rules remains an organizer task.

Successful changes retain private actor/time/operation/disposition audit records. Full historical content and contact information are not required. No deletion or retention automation is provided; define an explicit retention policy before expanding production use. See [ADR 0006](../../../docs/adr/0006-organizer-entry-book.md) for transaction and ownership decisions.
