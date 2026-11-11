# G7 — Architecture review

**Verdict: PASS.** Independent checker `field-review-arch`, 2026-11-11. No blocking architecture finding. No maker files changed or lifecycle gate registered.

| Severity | Location                                 | Finding / action                                             |
| -------- | ---------------------------------------- | ------------------------------------------------------------ |
| None     | Participant-field slice, V8 and ADR 0007 | No architectural correction required for the approved scope. |

Reviewed the current staged participant-field additions against approved PF-001–007, refinement/design, ADR 0007, root/backend guides, context map, architecture conventions and current G5/G6 evidence. SHA-256 of `git diff --cached -- service/tournamentmgmt docs/adr/0007-participant-field-preparation.md`: `b7c82cd393d6362bd51df01deaab74abec4659e448631894e3cc1f41b40f2936`. Base revision is `dbab018feaa06fbf077bcde1fd521270e838cbef`.

- **Ownership:** the field is a separate versioned preparation boundary inside existing `setup.configuration`. It references accepted entry snapshots without introducing Player Management, Scoring, runnable phases, a global registry or a draw algorithm. Local key/pair provenance and manual capacity remain explicit.
- **Dependency direction:** domain and use case depend on JDK/domain/application-port types. The JPA adapter implements the field port and composes the existing internal entry-store read port within the same module. Spring transactions and API mapping stay in the service adapter; web imports the exposed API and existing IAM principal provider. No domain-to-API/framework, API-to-internal or cross-module internal dependency was found.
- **Transaction/source boundary:** REQUIRES_NEW/REPEATABLE_READ establishes one field/source observation. The field lock and expected-version/CAS protect its own mutation; source writes are deliberately not frozen. Post-rollback conflict mapping and the independent-caller-rollback consequence match ADR 0007. Coarse source-version equality reports divergence without source or field synchronization.
- **Snapshot ownership:** original entry labels/members and current target labels remain distinct. V8 ownership foreign keys reference tournament/field boundaries rather than mutable bracket/entry content, allowing retained reads after bracket removal and empty preparation without an entry-book row. Latest units/members, audit and root version are saved transactionally.
- **Compatibility/privacy:** the new creator-only management API is separate from the broadly readable legacy Tournament DTO. Existing entry/draft/announcement implementations, JWT configuration and CLI are unchanged. The strict JSON correction remains in the web adapter; it introduces no global mapper or public-DTO-to-web dependency. Audit omits historical full member payloads.
- **Decisions:** ADR 0007 explicitly records the reviewed ownership, identity, migration, snapshot-time consistency, caller rollback, retention and no-synchronization tradeoffs. No new dependency, module or additional architecture escalation was identified. Preserved #185 research was treated as dependency documentation, not a runtime change or re-review of sports rules.

## Validation and limits

- Inspected raw staged diffs and complete new production files/V8 plus the relevant tests and endpoint documentation; `git diff --cached --check` passed.
- Independently inspected `/private/tmp/rallyon-field224-fix-verify.log`: **149 tests, zero failures/errors, three unrelated optional skips, BUILD SUCCESS**; `LayerDependencyTest` and `TournamentmgmtModuleStructureTest` passed. No Maven rerun was needed for this source-review lens.
- Current G5 delta independently reran 15 endpoint tests; G6 independently ran 66 tests on fresh PostgreSQL databases and checked persisted invariants. Their reports disclose the mocked HTTP/service boundary and forced source-overlap evidence accurately.
- Whole-book/source loading and latest-field member replacement make work grow with stored entries/audit/selected units; no throughput guarantee is established. Historical field reconstruction and operational retention automation are intentionally deferred. Real Keycloak HTTP-to-database UAT and human G8 acceptance remain pending.
