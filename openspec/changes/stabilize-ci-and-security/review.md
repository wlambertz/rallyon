# Review

## Source snapshot and independent identities

Current source/index fingerprint: `280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`. G5 and G6 prerequisites were checked by each final reviewer. Makers did not certify their own work. Helper G7 now passes all three required lenses.

## Domain and architecture

Independent `/root/ci187_review_architecture`: [final report](evidence/g7-architecture-nvd.md), PASS. Prepared input/source binding, sibling SBOM graph, trusted-only optional key/cache saves, SHA/digest pins and preserved contracts were checked. Actual cold CI artifacts were inspected. General Sonar CLI-path claims do not establish a remote/LLM trust boundary in this developer tool; Sonar's external status remains unchanged/red.

## Correctness and security

Independent `/root/ci187_review_correctness`: [final report](evidence/g7-correctness-nvd.md), PASS. Actual13.0.0 feed/matcher behavior, metadata freshness/timeout/fixed-mode behavior, actual cold/warm outcomes and all15 suppression instances were verified. The [new policy review](evidence/review-new-finding-policy.md) checked exact package/path/CPE/CVE bounds and manufacturer evidence, retaining the82-instance baseline plus8 additional observed instances.

## Tests and maintainability

Independent `/root/ci187_review_tests`: [final report](evidence/g7-tests-nvd.md), PASS.27 fixtures and current-policy counterexamples passed. Actual CI/warm raw hashes,145 prepared input hashes,9 JavaScript resources and matching SBOM were independently inspected. Full fresh runtime QA is in [the G6 report](evidence/g6-nvd-verification.md).

## Findings, resolutions and rechecks

The original CPE2.3 literal mismatch was corrected to engine-compatible bounded CPE2.2 and verified against actual DC13 classes. The local symlink-clobber hazard was reproduced before atomic output writes and overlap checks were added. Two independent P2 findings were reproduced red before fixing: stale engine reports accepted as current, and post-success verification exceptions retaining zero. Both were fixed at9bbeb3a and independently rechecked with27 tests and direct process probes. Historical failed reports remain alongside successful follow-ups; no error messages or security checks were filtered to create success.

## Review round and outcome

All final source lenses PASS after independently verified fixes. There is no unresolved reviewer disagreement. Sonar's external red status, default OSS Index credential limitation, unavailable ShellCheck and the existing opt-in test skip remain explicit in verification/UAT. G7 is not human G8 acceptance or merge approval.

## Subsequent Sonar disposition

After the frozen-source reviews, the maintainer explicitly authorized the four exact S8707 False Positive dispositions. The short approved rationale was stored in Sonar with optional analyzer-improvement sharing disabled. Public API confirms all four RESOLVED/FALSE-POSITIVE and quality gate OK with ignoredConditions=false; Safari visibly shows Quality gate Passed and GitHub Sonar check is SUCCESS. [Durable authorization/outcome evidence](evidence/sonar-false-positive-disposition.json) supersedes the historical red-status observations above. No rule/gate configuration or source implementation changed. This approval is not G8 acceptance or merge authorization.
