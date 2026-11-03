# Independent G5 completion check

- Checker: native agent `/root/g5_complete_check`, independent of the registered makers.
- Outcome: **FAIL for task 1.4 readiness on this snapshot.** The two concrete findings in `inventory-revised-audit.md` are corrected, but one source-to-ID cross-reference remains inaccurate. This is not approval of implementation, UAT or merge.
- Source: live [issue #183](https://github.com/wlambertz/rallyon/issues/183), including its reference and acceptance criteria, and live [issue #203](https://github.com/wlambertz/rallyon/issues/203). I inspected the traces, inventory, dispositions, separate questions/rules, code-search evidence, prior audit and the cited `Venue`, `RegistrationPolicy` and `ParticipantsRoster` types. I did not independently verify every DBV/regional provision or legal interpretation.
- Reviewed SHA-256 hashes:
  - `tasks.md`: `82f095d774c8cf94507fd55dd30d555af77386997f2c591a91537d2c400a549c`
  - `inventory.md`: `cdf18891b33128618b2f5caab5b6edcee1ceb9fe3bd911b7c8de9ef79e3584f4`
  - `trace-sections-1-3.md`: `43df3f62bbed04e154b5a1a7da67abc3d450b0c9eb6d9c6438c05cc147ad42f4`
  - `trace-sections-4-6.md`: `578f1b6a722ee7bf51d12fedd165597a9b5df6f566be7a02ef2144d936af5fef`
  - `analysis.md`: `971b9468189cff327c8e6bfc4b2b7cc54af723e199a1c58b11550c487951d20b`
  - `dispositions.md`: `f0600805abc8fc31fbf800dc765f7f59d9954b82324a222b492afcc6e07c5bcf`
  - `open-questions-and-unverified-rules.md`: `416963f4372d87ad94c60bd6820b6546dabcdbc268f91e78052b60355429f285`
  - `code-search-evidence.md`: `a4f7c056d1dfe3a17c51fa42bf3361d74592822a641bebac8601ddedc2f99a6a`

## Previous blockers

The inventory now maps the Major Events three-match spacing to I61, and its §4 checklist includes both I27 and I61. The §2 BWF export interface is separately defined as I65 and mapped to I65 in the §1–3 trace; daily BWF reporting remains I30. The earlier Grade 2/3 sanction and 120/15-day/force-majeure findings are split into I8/I62 and I9/I63/I64. Separate unresolved-question and unverified-rule lists exist. The G4-rescoped task 1.4 is accurately unchecked.

## Finding to correct

In `trace-sections-4-6.md`, S6-001 describes accurate results aggregation and says its **world-ranking consequences are I30**. The canonical `inventory.md` defines I30 as _daily BWF results submission_, while I29 defines BWF confirmed-result eligibility for prize money and world-ranking points. These are distinct claims and the current reference conflates them. Correct the mapping or explain a supported different boundary, then obtain a fresh independent review against the new hashes. The distinction matters only for accurate traceability: both BWF-specific claims remain deferred.

## Other observations and limits

- National statement traces identify skipped items with reasons. A skip is not a confirmed Gap. The narrow findings table identifies actual represented fields and the explicitly unimplemented operations, with proposed persona value, effort and context. Those broader product recommendations are not evidence that the maintainer accepted implementation.
- #203 is open, refers to #183 and STANDINGS-001–002, and explicitly asks for analysis and specification without production changes. Its link to the local untracked OpenSpec change will become durable for remote readers only when the artifacts are delivered.
- The current six-section mapping is substantially more granular. The issue permits a reasoned skip; no claim is made here that those skipped rules have been validated or that every regional rule applies. A complete source review should be repeated after fixing the concrete mapping above, rather than reusing this report as a pass.
- `git status --short` showed only the untracked change directory; this is a bounded working-tree observation, not runtime/security validation.

## Checks performed

- `npm run format:check`: passed for tracked files.
- Explicit local Prettier check over the untracked change Markdown, evidence, spec and JSON files: passed.
- Cached OpenSpec 1.14.2 `validate requirements-reconciliation --strict`: passed.

The report was written after those checks, so its own formatting was checked separately. G5 must not be marked passed from this report.
