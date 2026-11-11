# Design

## Context and constraints

Tournament Management already has an independent entry book in `setup.configuration` (ADR 0006) and mutable legacy configuration rosters. Entry keys/pair members are not numeric global roster identities. Draft/book versions exist, but no field/draw aggregate or immutable source-revision API does. Configuration reads/updates have broader authority than entry reads. No Player Management, Scoring or organizer UI implementation may be assumed.

PF-001–007 define a preparation snapshot, not a draw. Keep this small boundary in existing `setup.configuration`; introducing a phase module would suggest runnable phases that remain unsupported. An ADR must confirm ownership, source consistency and retention before apply.

## Decisions and tradeoffs

### Identity, selection and lifecycle

Use `(tournamentId, disciplineId, bracketId)` as field identity, with its own monotonic version starting at virtual 0. A successful first preparation creates version 1; every later explicit preparation increments it, including unchanged selections and empty selections. Empty means deliberate clearing/preparation, not absence; no delete endpoint or draw-ready status is introduced. Sort selected units by entry ID to avoid implying draw/seed order; preserve each pair's original member ordering.

Copy current target labels/team size and each selected ACCEPTED entry's ID, original selection snapshot and ordered local members. Require identical discipline/bracket IDs and team size, positive/distinct record IDs, valid member cardinality/content and no overlapping selected member keys. Labels may have changed since acceptance: retain old entry selection and new target selection separately rather than requiring an unrelated correction/reacceptance merely for a rename. Do not manufacture a player/team identity, reinterpret bracket names as IDs or import ranking policy.

Capacity is explicitly manual, including when configured limits are exceeded. Include a stable `capacityReview: MANUAL` response value and document that preparation is not eligibility/capacity approval. Do not duplicate mutable capacity data or apply a numerical selection maximum that masquerades as admission policy. Intake does not gate preparation. Zero/one selected unit is valid; malformed/null selections are not.

### Versions and observable divergence

PUT requires `If-Match` for field version and body `sourceTournamentVersion`, `sourceEntryBookVersion`, `disciplineId`, `bracketId`, `entryIds`. All versions are nonnegative integers; IDs/selection constraints follow existing entry contracts. Missing body/preconditions or invalid syntax return 400. The command compares versions and resolves entries/current selection inside one repeatable-read snapshot. Source records absent from this tournament return invalid selection 400; nonaccepted/incompatible selections, stale versions and competing writes return 409. Unknown/malformed bracket selection on PUT is 400, even if a previously saved field exists. Saved-field reads survive removal.

GET includes `prepared`, field version, target identity, saved target selection, saved source versions, units, minimal audit, observed current source versions and freshness UNPREPARED/CURRENT/OUTDATED, plus manual-capacity review. For UNPREPARED, saved selection/source versions are absent and units/audit empty. A current existing bracket is required only when no field exists. Any unequal draft/book version conservatively makes a saved field OUTDATED, including label, intake or unrelated changes. A source row never has to be created by a read or field command. Returned freshness is evaluated against the response snapshot and may become outdated after any concurrent/later source commit.

Reconciliation is ordinary explicit PUT with current source/field versions and a valid desired selection. It replaces the latest field contents and appends audit. The old full content is not retained; saved provenance versions identify the observation but do not imply that historical source revisions can be reconstructed. Revisions increase monotonically, so reverting source content does not silently make an older field current. No per-member divergence diff or reasons API is needed in this first slice.

### Transactions, persistence and concurrency

Propose Spring `TransactionTemplate` with REQUIRES_NEW/REPEATABLE_READ for both reads and writes, matching the established entry boundary. Suspend caller transactions rather than inheriting weaker isolation; document that a committed preparation survives a caller rollback. Establish one snapshot across authority, draft version/current choice, entry book, saved field and audit. Use a field-row `FOR UPDATE` lock for writes; lazily insert virtual-zero storage with `INSERT ... ON CONFLICT DO NOTHING`, and check expected field version before replacement. A failed command rolls back even first-row initialization. Competing first preparations follow the same path. Lock/CAS and database uniqueness ensure at most one winner per field version.

Do not lock or mutate entry-book/configuration rows merely to freeze their future state. The source is current at the transaction snapshot, not guaranteed unchanged until commit. A concurrent entry withdrawal or bracket edit may commit alongside a preparation based on its earlier consistent snapshot; the next read must then expose OUTDATED. This avoids changing existing entry/draft write contracts and states the actual guarantee. The response must expose its observed source versions; no automatic retry is allowed to silently adopt newer ones. Different fields have independent versions; no cross-field selection reservation or tournament-wide draw lock is introduced.

Add the next available append-only Flyway migration (currently V8) for field roots, copied units/members and minimal audit. Foreign keys reference tournament and field ownership, not mutable discipline/bracket rows or entry-book root (which can be absent for an empty selection). Field rows must survive bracket removal. Retain source entry IDs as provenance rather than dependencies on mutable row contents. Use deterministic case-sensitive key comparison compatible with entries. Save latest units/members, source versions, one audit event and field-version advance atomically; map serialization/deadlock/unique/CAS failures to 409 only after rollback. No silent source mutation, partial field or failed-operation audit is permitted.

Audit retains field version, actor, microsecond-normalized time, PREPARE, unit count and both source versions. No historical member payload or contact data. No deletion/retention automation; keep additive storage on application rollback.

## Bounded contexts, invariants and dependency direction

Propose pure `ParticipantField` values/operations and `ParticipantFieldUseCase` with a source/store port under configuration `internal.tournament`. The persistence adapter may compose existing internal entry-store read logic within the same transaction; avoid calling the public EntryBookService, whose independent transaction would lose the shared snapshot. Keep API DTO/error mapping, transactions and principal extraction in adapters; domain/use cases import neither API DTOs nor JPA/Spring/web. Reuse the existing authenticated-principal provider and strict JWT validation unchanged. Inject the established clock.

Published new contract: `ParticipantFieldService` under `setup.configuration.api`; controller under `web`. Suggested additive base `/api/tournamentmgmt/participant-fields/{tournamentId}`: GET with disciplineId/bracketId query parameters; PUT with those selectors, entry IDs and source versions in JSON plus field If-Match. Query/body selectors avoid assuming all valid existing bracket IDs are safe as path segments. Positive creator authority is checked before any private field/source content is loaded. Responses and mapped errors request `no-store`; sanitize errors. Existing configuration DTO/endpoints must not acquire field/member data. No new module, dependency, context, event or public route is proposed.

## Compatibility and escalation assessment

**Escalated for G2 review:** additive private API, append-only migration, independent transaction/caller-rollback semantics, field snapshot storage and private audit retention. An ADR is required before implementation. No existing REST/JWT/CLI contract, shared IAM code, shipped migration, roster/entry/announcement operation or module allowlist changes. Changes beyond those assumptions return to the lifecycle's approval gates. #185 research delivery remains separately unmerged; this planning change references its accepted findings without changing them.

## Validation, rollout and recovery

Map PF-001–007 success/failure scenarios to pure tests, MockMvc security/body/version/privacy tests and a fresh dedicated PostgreSQL integration test. Actual database checks must cover V7-to-V8 upgrade, zero storage after reads/failed first writes, empty/single/double selections, complete-state equality after rejection, competing first/replacement commands with distinct payloads, independent fields, source edits during preparation/read, retained snapshots after bracket removal and conservative freshness. Force a source commit between source observation and field completion to demonstrate the documented snapshot guarantee; don't claim a start latch proves that interleaving. Test no audit/version/source side effects from losers and no fabricated identities or old-endpoint regression.

After implementation run focused participant-field plus existing entry/configuration/announcement security tests, then IAM install if needed and `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`. Run `npm run format:check` and strict pinned OpenSpec validation. Local Keycloak/PostgreSQL UAT uses creator/foreign users and includes source withdrawal/draft removal, outdated read, explicit reconciliation, stale write and closed-intake manual-capacity behavior. Obtain independent G5/G6 and three G7 lenses before UAT acceptance; merge remains separate.

Roll out additive migration/service together, with no implicit fields for existing tournaments and no backfill. Application rollback leaves field/audit data intact. Do not remove storage or rewrite shipped migrations; a later retention policy needs its own explicit decision. No runtime tests have been executed for these planning artifacts.

## ADR assessment

Required before apply: record field ownership in configuration, local identity/provenance, complete-source-version freshness, snapshot-versus-commit guarantee, independent field locking, latest-content/minimal-audit retention and no legacy roster synchronization. This design proposes those concrete decisions for G2; no ADR acceptance or implementation is inferred from G1.
