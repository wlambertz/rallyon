# G7 — Tests and maintainability review

**Verdict: PASS.** Independent checker `field-review-tests`, 2026-11-11. No blocking test or maintainability finding. Reviewed approved PF-001–007/design/ADR 0007, raw staged field changes and current G5/G6 evidence; no maker files changed or gate registered.

| Severity | Location | Finding / action |
| --- | --- | --- |
| Advisory, nonblocking | PF-004/PF-005; G6 scenario trace | Exact correction and acceptance-after-preparation sequences are covered compositionally by entry version changes and field freshness/immutability checks, rather than distinct field integration phases. Preserve this stated limit; include those persona actions in the planned authenticated walkthrough where practical. |

## Assessment

- The 12 pure use-case tests exercise observable selection/replacement behavior: creator checks before private loads, distinct field/source versions, empty/single/pair units, omitted accepted entries, canonical ID order, original/current labels, case-sensitive keys, member/selector/ID boundaries, nonaccepted/incompatible records and complete saved-state preservation on rejection. They do not merely mirror persistence statements.
- The 15 MockMvc tests exercise actual endpoint binding, response serialization and security filters with a mocked service/JWT decoder. The G5 correction now has explicit fractional, quoted, exponent, boolean, null, shape and overflow regressions for every numeric scalar/list position; malformed cases require 400/no-store and zero service calls. Valid Long.MAX_VALUE and normal mapping controls prevent a blanket rejection from passing.
- The PostgreSQL test requires an explicitly opted-in fresh database, migrates to V7 before the real application applies V8, and runs the production service/store/transaction adapters. Named phase helpers make the sequential workflow understandable. Earlier phase failure still prevents later phases, but repeated independent successful executions and focused helper names make this proportionate for one shared migration fixture.
- First-write and replacement races use distinct candidate selections, demand exactly one winner plus one conflict, and compare the entire persisted view with the winner. Unexpected exceptions propagate through futures. These start latches do not guarantee every SQL interleaving, and the evidence says so.
- The two source-overlap phases use a test-only port proxy after a delegated real source read. The real service constructor still establishes REQUIRES_NEW/REPEATABLE_READ through TransactionTemplate; no production hook or substituted transaction implementation is used. The competing source/field commits complete before release, and assertions distinguish the coherent earlier response from a later OUTDATED/new-version read.
- Persistence assertions cover virtual-zero/no writes, rollback of incompatible first preparation, independent fields, retained removed-target snapshots, explicit clearing/reconciliation, manual capacity with closed intake and no legacy roster/publication writes. G6's additional SQL verifies contiguous audit ranges, actor/time/operation, member cardinality/key uniqueness and latest audit counts.
- The initial lossy-binding defect remains documented as FAIL, with a separately executed passing delta recheck. Current evidence does not misrepresent original green tests as sufficient or mocked HTTP as real authentication. Existing entry/configuration/announcement regression and architecture tests remain in the full suite.

## Evidence checked

- Inspected all three new test classes, the relevant domain/use-case/controller/service and V8, approved specification/design/ADR, maker verification, G5 initial/delta reports and G6 trace/SQL output.
- `git diff --cached --check`: **PASS**. SHA-256 of staged diff for `service/tournamentmgmt` and ADR 0007: `b7c82cd393d6362bd51df01deaab74abec4659e448631894e3cc1f41b40f2936`.
- Inspected `/private/tmp/rallyon-field224-fix-verify.log`: **149 tests, zero failures/errors, three unrelated optional skips; BUILD SUCCESS**. Both actual PostgreSQL suites executed.
- Inspected `/private/tmp/rallyon-field224-g6-qa.log`: **66 tests, zero failures/errors/skips; BUILD SUCCESS**, including field 12/15/1 and entry 8/8/1 plus configuration/announcement security 13/8.
- Inspected `/private/tmp/rallyon-field224-g6-sql.txt`: three roots have contiguous version/event counts 8/8, 1/1 and 1/1; invalid cardinality, duplicate field-local keys and latest-count mismatches are zero.
- No additional Maven run was needed for this review lens: the unchanged source already has full maker verification, independent endpoint execution and fresh independent PostgreSQL QA. This is an evidence/source review, not another claimed independent test execution.

## Limits

Real Keycloak HTTP-to-database UAT remains pending and must cover the actual creator workflow plus representative failures before human acceptance. No load/throughput, all-interleaving concurrency, verified identity, historical reconstruction or automatic admission guarantee follows from these tests. No browser coverage applies to the organizer placeholder. Separate accepted #185 research was outside this test-review scope.
