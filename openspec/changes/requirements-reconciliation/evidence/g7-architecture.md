# Independent G7 architecture review

- Reviewer: native agent `/root/g7_architecture`, independent of the registered makers.
- Outcome: **PASS for the approved analysis-only scope**, with the nonblocking future-design questions below. This is not a choice of architecture, authorization to implement, or approval of UAT or merge.
- Reviewed scope: live issue [#183](https://github.com/wlambertz/rallyon/issues/183); `refinement.md`, `proposal.md`, `design.md`, `tasks.md`, `specs/group-standings/spec.md`, `inventory.md`, `analysis.md`, `dispositions.md`, the open-questions list, ADR stub, approved issue draft, context map, tournament service guide, and `evidence/g5-current-verify.md` and `evidence/g6-qa.md`. I did not rerun their checks or independently certify every regulatory citation.
- Snapshot: `git status --short` showed only untracked `openspec/changes/requirements-reconciliation/`. Reviewed SHA-256 hashes below are relative to that directory.

| File                                     | SHA-256                                                            |
| ---------------------------------------- | ------------------------------------------------------------------ |
| `refinement.md`                          | `1b131e4ac77cc375fdc808c29abc361d5eb1440be33482d2b74305f4106fc276` |
| `proposal.md`                            | `06d3b2a0a5d1200b2fc124cf320373c06bbc139df8c785ce15388b7ddfe651bb` |
| `design.md`                              | `c773b505af2d2f716fdcc6f8815ddf4f9f5f3757eddd922ad92f0ea166ebba51` |
| `tasks.md`                               | `ab065e7686fdd608e5eb04589b2d83029b69bbdaa672af4c6ba49b01f60300d0` |
| `specs/group-standings/spec.md`          | `de001c2c8afe543b50450a294ea93552f53f36adfdddae2df74f6a0021741ff6` |
| `inventory.md`                           | `cdf18891b33128618b2f5caab5b6edcee1ceb9fe3bd911b7c8de9ef79e3584f4` |
| `analysis.md`                            | `971b9468189cff327c8e6bfc4b2d7cc54af723e199a1c58b11550c487951d20b` |
| `dispositions.md`                        | `f0600805abc8fc31fbf800dc765f7f59d9954b82324a222b492afcc6e07c5bcf` |
| `open-questions-and-unverified-rules.md` | `416963f4372d87ad94c60bd6820b6546dabcdbc268f91e78052b60355429f285` |
| `adr-stub-standings-ownership.md`        | `f64dda3bf2e1f5fec76ced09d8e46aa0260b7396bacaeaeb2b5f49252c2b1165` |

## Findings

**Blocking: none found within analysis scope.** The deliverable is documentation and a conditional candidate specification. The proposal explicitly excludes runtime implementation, a new service, UI, migration and federation integration; it does not silently promote an unimplemented placeholder to an existing capability. This review does not confer readiness for a future implementation.

**Ownership and boundaries:** The context map distinguishes existing `setup.configuration` from the proposed Scoring context. The candidate design and ADR stub preserve that distinction, leave calculation/result ownership and transport undecided, and call for an ADR and explicit approval before a new context or integration is chosen. I inspected `TieBreakRules.kt`: its type and flags do not establish an ordered standings algorithm. `Phase.kt` is an empty interface. The recommendation does not rely on a nonexistent completed service or algorithm.

**Dependency Rule and contracts:** The design forbids dependencies on another module's internal packages, and the stub identifies a named interface/port as a candidate rather than proof of an enforced boundary. No runtime source, Flyway migration, shared IAM library, dependency or REST/JWT/CLI contract changes appear in the scoped working tree. A future feature must verify those boundaries against its actual design.

**Scope and authority:** The Organizer is the primary persona; participants and spectators are downstream audiences. Candidate STANDINGS requirements require an explicit supported rule/version; the DBV pool scenario names its provision rather than prescribing that rule universally. The U11 and general pool examples are kept distinct. National applicability, an event announcement and regional variants remain open. This review relies on earlier bounded source checking and does not independently assert legal or regulatory applicability.

## Nonblocking observations for future work

- **Ownership and lifecycle:** Decide who owns authoritative results and selected rules, permitted correction and confirmation transitions, concurrency, versioning and stale dependencies. The stub presents alternatives; it is not a resolved decision.
- **Security and privacy:** Existing authentication-only configuration read behavior does not decide who may enter, correct or publish results. Future work needs explicit read/write authorization and data minimization while preserving the existing JWT issuer, audience, role and claim contract.
- **Compatibility and delivery:** Future schemas, append-only migrations, modules, events/APIs or dependencies need their own approval and migration/rollback approach. Issue #203 refers to this currently untracked change; remote readers cannot rely on that artifact reference until delivery.
- **Verification:** G5/G6 reports state that format and OpenSpec checks passed; no scoring runtime, backend, IAM, PostgreSQL or end-to-end behavior was implemented or tested. Separate correctness and tests lenses, human acceptance and a merge decision remain required.

Any material change to reviewed artifacts requires an appropriate fresh review and lifecycle evidence check.
