# G7 — Correctness and security review

**Verdict: PASS.** No blocking correctness or security finding. Independent checker: `entry_review_correct`, 2026-11-11. Reviewed the staged entry-book production code, V7 migration, API documentation and tests against ENT-001–007, the approved design and ADR 0006. Base revision: `b7f0d6a5246f579e5ce570203576a2901b24a2fd`. No maker files changed or gate registered.

| Severity | Location                                  | Finding / action                               |
| -------- | ----------------------------------------- | ---------------------------------------------- |
| None     | Entry-book implementation and additive V7 | No correction required for the approved scope. |

## Independent assessment

- Authority derives from the existing authenticated principal and positive user ID. The use case compares the stored creator before loading entry data or ensuring its book. Existing draft replacement preserves that creator; caller-supplied organizer or creator fields cannot transfer entry access.
- The existing security chain requires authentication for every new route; its anonymous announcement matcher cannot match an entry route. No shared JWT validation or security configuration changed. Scoped advice sanitizes forbidden/not-found errors, and successful/mapped-error responses request no-store. Entry payloads are not logged by the added code.
- Singles/doubles cardinality, distinct ASCII keys, case sensitivity, UTF-16 name limits and NUL rejection match the contract. Selection is parameterized SQL, constrained by tournament, discipline and bracket. Local record lookup cannot address another tournament's entry.
- State rules implement explicit disposition, terminal withdrawal, correction to RECEIVED and independent closed intake. Membership checks cover all active entries within a discipline, independently of bracket and pair order; reacceptance/correction recheck them. PostgreSQL `COLLATE "C"` active-claim uniqueness provides a database backstop.
- Each command establishes REQUIRES_NEW/REPEATABLE_READ before its first authority/configuration read. First-write initialization and subsequent `FOR UPDATE` book locking, expected-version checking and final compare-and-set prevent two writes on one version from committing. Serialization, deadlock and uniqueness failures are mapped after transaction rollback; there is no retry that silently changes a user's decision.
- Member replacement, active claims, record snapshots, one audit append and version advancement occur in the same transaction. The returned view is built after transaction completion. Reads also use a single repeatable snapshot, avoiding a book assembled from different committed versions.
- New/corrected selections are captured with one JOIN in the transaction snapshot. Existing records and decisions/withdrawals use retained snapshots after configuration removal. V7 references the tournament/book/record boundaries without foreign keys to mutable discipline/bracket rows. Existing roster, announcement and shipped migration code is unchanged.
- The independent command boundary, manual reconciliation/capacity policy, unverified local identity and retained private audit are explicitly documented. Full-book loading and membership rewriting impose linear per-book work; no throughput or automatic admission guarantee is claimed in this slice.

## Validation and limits

- Inspected `git diff --cached --stat`, the raw staged production diff, full new production files and the HTTP/PostgreSQL tests; inspected existing draft replacement/persistence and the security matcher for compatibility/authority reasoning.
- `git diff --cached --check`: passed.
- Reviewed G5 and G6 evidence: independent focused PostgreSQL-backed runs each report 38 tests, zero failures/errors/skips. This lens did not rerun Maven or repeat formatting/OpenSpec checks already recorded by G5; full `clean verify` remains maker evidence.
- The race tests start competitors together but do not force every SQL interleaving. Simultaneous draft replacement and concurrent multi-query reads were assessed from isolation and query structure, not independently forced here. Actual Keycloak HTTP-to-database UAT remains G8 work; mocked HTTP security tests do not replace it.
