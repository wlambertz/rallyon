# G7 — Architecture review

**Verdict: PASS.** Independent checker `entry_review_arch`, 2026-11-11. No blocking architecture finding; no maker files changed or gates registered.

| Severity | Location                          | Finding / action                     |
| -------- | --------------------------------- | ------------------------------------ |
| None     | Entry-book slice, V7 and ADR 0006 | No architecture correction required. |

Reviewed the staged issue-219 additions against the approved proposal/design/ENT-001–007, ADR 0006, root/service guides, architecture wiki, Dependency Rule, and current G5/G6 evidence. SHA-256 of `git diff --cached -- service/tournamentmgmt docs/adr/0006-organizer-entry-book.md`: `9d28eeca3cf01f7a0afb78bb96847775a9103aae6641c225732bad25f5449d10`. Lifecycle state was not inspected.

- **Ownership:** the book stays inside `setup.configuration`, independently owns intake/disposition/version, and references tournament-local selection snapshots. It introduces neither a player registry nor dependencies on placeholder services. Player keys are explicitly unverified local references.
- **Dependency direction:** domain and use case depend only on JDK/domain/application-port types. The JPA adapter implements `EntryBookStore`; Spring transactions, API mapping and exception translation remain in `EntryBookServiceImpl`. Web imports only the published API and existing IAM principal abstraction. No new module or dependency is introduced.
- **Consistency boundary:** row locking and book versions serialize all mutations, including intake and duplicate membership. Case-sensitive database claims back the domain invariant. REQUIRES_NEW/REPEATABLE_READ explicitly establishes independent command/read snapshots; its caller-rollback tradeoff is documented. Database errors are translated after the transaction boundary rolls back.
- **Mutable configuration:** one joined selection query and repeatable-read snapshot capture compatible labels/team size. Entry records intentionally have no discipline/bracket foreign key, so later draft replacement cannot delete or rewrite their snapshots. The tournament/book/record ownership foreign keys remain intact.
- **Contracts and privacy:** only additive routes/storage are present; existing configuration, announcement, JWT and roster implementations are unchanged. Creator authority precedes book loading. Retained audit contains actor/time/transition rather than historical payload. No public entry route or identity delegation is introduced.
- **Decisions and rollout:** accepted ADR 0006 names the single-writer, local-identity, manual-capacity, retention and rollback tradeoffs. V7 is additive; shipped migrations, shared IAM and existing configuration security are untouched. No further architectural escalation was identified.

## Checks and limits

- Read raw `git diff --cached` for service/ADR additions and inspected complete production files, V7 and nearby docs.
- `git diff --cached --check`: passed.
- Inspected `/private/tmp/rallyon-219-verify.log`: full verification reports 121 tests, zero failures/errors, three unrelated optional skips, including passing architecture enforcement. G5/G6 independently reproduced 38 focused tests against fresh PostgreSQL databases.
- No Maven rerun for this source-inspection lens; no claim of independent full-build execution. G6 documents unforced concurrent draft/read interleavings. Real authenticated HTTP UAT remains pending.
- Loading every member list and rewriting all member/claim rows makes command work grow with the book. This simple aggregate strategy matches the first slice; no load/capacity guarantee is established by this review. Revisit with measured tournament sizes before optimizing.
