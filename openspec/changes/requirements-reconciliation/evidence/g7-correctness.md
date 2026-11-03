# Independent G7 correctness and security review

- Reviewer: native agent `/root/g7_correctness`, independent of the registered makers.
- Outcome: **PASS for the approved analysis-only scope**, with the limitations below. This is not an implementation, runtime-security, UAT or merge approval.
- Sources reviewed: live issue [#183](https://github.com/wlambertz/rallyon/issues/183), its national scope, six-section reference and acceptance criteria; approved proposal, design, candidate specification and tasks; the source traces, topic inventory, dispositions, open questions and unverified rules; and the G5/G6 reports.

## Reviewed snapshot

| Artifact                                 | SHA-256                                                            |
| ---------------------------------------- | ------------------------------------------------------------------ |
| `analysis.md`                            | `971b9468189cff327c8e6bfc4b2b7cc54af723e199a1c58b11550c487951d20b` |
| `inventory.md`                           | `cdf18891b33128618b2f5caab5b6edcee1ceb9fe3bd911b7c8de9ef79e3584f4` |
| `dispositions.md`                        | `f0600805abc8fc31fbf800dc765f7f59d9954b82324a222b492afcc6e07c5bcf` |
| `open-questions-and-unverified-rules.md` | `416963f4372d87ad94c60bd6820b6546dabcdbc268f91e78052b60355429f285` |
| `trace-sections-1-3.md`                  | `43df3f62bbed04e154b5a1a7da67abc3d450b0c9eb6d9c6438c05cc147ad42f4` |
| `trace-sections-4-6.md`                  | `3361746bdb4bf87bfd3e8c2ae4d4fe834cdb274cbde95cb58601457f7930cded` |
| `proposal.md`                            | `06d3b2a0a5d1200b2fc124cf320373c06bbc139df8c785ce15388b7ddfe651bb` |
| `design.md`                              | `c773b505af2d2f716fdcc6f8815ddf4f9f5f3757eddd922ad92f0ea166ebba51` |
| `tasks.md`                               | `ab065e7686fdd608e5eb04589b2d83029b69bbdaa672af4c6ba49b01f60300d0` |
| `specs/group-standings/spec.md`          | `de001c2c8afe543b50450a294ea93552f53f36adfdddae2df74f6a0021741ff6` |

## Findings

**Blocking findings:** none found for the approved analysis-only deliverable. The two statement traces distinguish international-only deferrals from national analogues and attach explicit reasons to skipped claims. In particular, the source's no-show outcome is described as an option, not a mandatory automated disqualification; removal of a withdrawn entrant's games from a ranking is distinguished from deleting historical results. The inventory's `G` and `P` topic labels are expressly provisional, while `dispositions.md` narrows its confirmed representation and unimplemented-method findings to named code and avoids treating a data type as a complete workflow.

The independently consulted [DBV 2026/27 regulations](https://www.badminton.de/fileadmin/dateien_upload/dateien_2026/juli_2026/dbv_satzungswerk_2026-27_stand_13.06.2026.pdf) support the cited edition and the knockout seed-count distinction in Anlage IV Teil 2 § 3. The candidate's pool-ranking example is limited to the expressly selected Anlage IV Teil 2 § 10 provision. These checks do not establish that the provision governs a particular RallyOn event. The separate national age-cutoff, youth and scoring examples must retain their competition and effective-date qualifiers.

**Nonblocking limitations:** Most national claims are explicitly skipped rather than proven implemented or definitively classified. Their documented reasons and open-rule list are necessary context for the accepted analysis and must not be described as verification of every rule. Priorities and adopt/adapt/defer labels are proposals, not observed user value or authorization to implement. Issue #203 is an analysis follow-up; its reference to this local untracked change will not be accessible to remote readers until the artifacts are delivered. This review does not resolve the actual target competition, result ownership, corrections, concurrent writes, or the administrative process for future rule selection.

## Security and compatibility

The reviewed work is documentation and candidate specifications in an untracked change directory. No runtime, JWT, role, database, public API or dependency edit was identified by the bounded working-tree status. The source's BWF rights assignment and international sanctions are deferred; the documents do not infer a national legal basis from them. A future result-entry implementation requires its own actor, authorization, correction/concurrency, privacy and security scenarios; the existing authentication-only configuration-read behavior does not establish write permission.

## Validation limits

I inspected the current working-tree status and the checked artifact hashes and independently consulted the cited DBV primary source. I did not run backend, IAM, database, Docker or browser tests, and did not reverify every national or regional rule or legal interpretation. G6 separately records the documentation and OpenSpec checks. Any material change to the reviewed snapshot requires a fresh appropriate review.
