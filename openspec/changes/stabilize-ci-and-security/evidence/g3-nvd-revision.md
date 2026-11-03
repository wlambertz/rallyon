# Independent G3 check: NVD-primary revision

## Identity, scope and verdict

Checker: Codex native independent agent `/root/ci187_nvd_g3`, fresh context separate from the artifact/implementation maker. Checked on 2026-11-03 at worktree HEAD `070327ff995068ad12226f1d2a50e50b7867e7da` plus the uncommitted NVD revision identified below. The checker authored only this report and did not change production code, planning artifacts or lifecycle records.

**PASS for the revised value/readiness assessment.** No material score, effort, scope or architecture correction requiring another G2 decision was identified. This is an independent planning challenge, not scanner validation, implementation completion, UAT acceptance or merge approval. The recorded user instruction approves the displayed NVD-primary revision; renewed gate records remain the coordinator's responsibility.

## Inputs and direct checks

Read root AGENTS.md, the feature-lifecycle skill/value-check reference, `docs/value-scoring.md`, refinement, proposal, design, delta specification, value, tasks and `evidence/nvd-revision-authorization.md`. Inspected baseline finding JSON/index, recorded main security runs, scanner identity, original cold/retry execution JSON and scanner logs, and the keyless-blocker record. Also inspected the current security workflow, relevant wiki architecture context and the saved NIST metadata/cache properties. Historical failure records were not treated as successful revised-feed executions.

Recounting the raw baseline produced 13 finding-bearing entries, 82 finding instances and 64 distinct vulnerability IDs; dependency count is recorded as 141. Both original executions exited 13 without reports. Their logs identify the empty-key API exception; the second execution reused incomplete data and is not a successful warm-cache test. The recorded eight failed main runs corroborate recurrence but do not measure maintainer time or prove identical failure causes.

Primary upstream sources were independently opened:

- [Dependency-Check v13.0.0 NvdApiDataSource](https://raw.githubusercontent.com/dependency-check/DependencyCheck/v13.0.0/core/src/main/java/org/owasp/dependencycheck/data/update/NvdApiDataSource.java): `update` selects `processDatafeed` before `processApi` when a feed URL is configured. Feed URL handling explicitly supports the exact official NIST JSON 2 pattern in the revision. The implementation reads cache properties or builds timestamps from yearly/modified metadata and retains its normal freshness window. This supports feasibility; it does not prove a completed cold import within 60 minutes.
- [Upstream issue 8715](https://github.com/dependency-check/DependencyCheck/issues/8715): reports the same no-key failure in 13.0.0 and links its fix. The revision avoids that API path without claiming the pinned image has been fixed.
- [pgJDBC manufacturer advisory](https://github.com/pgjdbc/pgjdbc/security/advisories/GHSA-j92g-9f8w-j867): baseline 42.7.11 falls within the affected version interval, fixed in 42.7.12. Exploit conditions concern channel-binding configuration; this is package/version remediation evidence, not proof of application exploitation.

## Reproduced value assessment

`(0.5 × 4 + 0.3 × 4 + 0.2 × 3) × 0.75 / 5 = 0.57`.

- User value 4 remains a defensible prospective rating: recurring failed checks and skipped inventory are evidenced, while frequent costly manual workaround/time savings remain hypotheses. No direct product-persona benefit is invented.
- Learning value 4 is conditional on documented, verified reusable insight into identity triage, fail-closed feeds, trust and independent evidence. A planned feed adapter alone is not completed learning evidence.
- Risk reduction 3 fits demonstrated maintenance/regression concerns and affected versions. Application reachability or critical failure prevention is not established; 4–5 would overclaim.
- Confidence 0.75 fits corroborating inspection and failure records. The reproduced old failure does not justify confidence 1.0 in the new feed path.
- Effort 5 remains plausible for the entire PR 1 package: dependency/IAM compatibility, scanner and workflow integration, policy and negative tests, actual cold/warm runs, documentation, review, CI and delivery. The bounded metadata preflight adds work within this substantial integration estimate; no additional engine, dependency or CI job is introduced. No measured completed-work comparator is supplied, so this remains a relative estimate rather than a time/cost commitment. PR 2 remains separately required and the whole-program effort 8 is explicitly decomposed.

No numeric approval threshold or work budget was inferred. The decimal is arithmetic, not empirical precision.

## Revised specification readiness and qualifications

The design and SCA-005 coherently specify keyless direct NIST feed mode versus trusted keyed API mode, fixed before execution, with no failure fallback. They preserve the official 13.0.0 digest, CVSS 7 gate, active updates, trust restrictions, suppression boundaries and independent SBOM. BSI remains supplementary manual context; a replacement engine/adapter is excluded.

The preflight has testable boundaries: at most three 20-second attempts; exactly one timezone-aware date; age at most 48 hours and future skew at most five minutes, inclusive; required retrieval/parse/freshness failures block cold and warm execution. Recorded mode/timestamp/metadata hash supports diagnosis. The 48-hour policy is a user-selected acceptance constraint, not a guarantee made by NIST. A fresh metadata response also does not by itself prove the scanner database is current; engine update behavior and its failure status must still be checked. The design permits the normal engine freshness window rather than asserting an exact metadata-to-database snapshot match.

Remaining proof obligations are material acceptance conditions, not current blockers to this bounded implementation plan: actual cold and warm feed runs within the 60-minute job; retained update/runtime failure status with warm data; embedded-JavaScript coverage; trusted API credential cleanup; isolated boundary/error/retry tests; workflow/SBOM behavior; full affected checks and independent review. Failed cold initialization, materially increased effort or a required new architecture/scope change must return to G2 rather than be waived by this pass.

Nonblocking documentation qualification: refinement/value and the preserved blocker note contain historical pending/pre-implementation prose. The current authorization record and revised tasks explicitly identify renewed G2–G4 work and earlier completed implementation. Those older paragraphs must not be used as evidence of current gate status or successful execution. No substantive approval or scope ambiguity was found in the actual revised data-path requirements.

## Checked artifact fingerprints

SHA-256 values relative to this change directory:

| Artifact                                     | SHA-256                                                          |
| -------------------------------------------- | ---------------------------------------------------------------- |
| refinement.md                                | 850eee25db1039271413320980f3c71046026f2b1be5df96635d587bda9fe6a1 |
| proposal.md                                  | 7a8b1931035e7402734c95992a90df0444230fdfb63a36ccebef68dc98ee88ef |
| design.md                                    | f7055b9ed052225f17736ec125321f2a86141c0367e4222159d02d112d980ebd |
| specs/repository-dependency-security/spec.md | 581cdacfba471a4fd448242cc83020474b6cd8585212d81836b7fc09bbade4e9 |
| value.md                                     | 1e21efcf80801279c2b2912e0307ccd46400aa8bdbaac611decb8421c53296f5 |
| tasks.md                                     | 0a6471f63a53c585a784e044d3f991dca7be780b46be9446eff4c6bfa59ec356 |
| evidence/nvd-revision-authorization.md       | e9bc65e588ef4e17cff7fd9719641c1ed4747aecb743dc6d593d2e1c77ed37a6 |

## Validation limits

Reproduced arithmetic and raw finding counts; inspected upstream source and vendor evidence. No scanner/container execution, Maven/Go tests, live keyed API request, registry lookup, GitHub workflow replay or deployment/publication was performed. Those remain later-stage evidence. The report passes its focused Prettier check. Repository `npm run format:check` failed only on concurrently modified `.lifecycle.json`; the checker did not alter that coordinator-owned record. No implementation pass is implied.
