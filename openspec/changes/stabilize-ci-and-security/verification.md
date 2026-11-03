# Verification

## Source snapshot and maker/checker identities

Implementation commits:025a152 dependency alignment,349c0bd NVD feed,9bbeb3a fail-closed review fixes,93e491b actual new finding policy. Final staged source/index fingerprint: `280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`. Later evidence/gate records are excluded from the source they certify.

Makers: `codex-issue-187-maker`, `/root/ci187_dependencies`, `/root/ci187_pipeline`, `/root/ci187_triage`. Fresh independent completion checker `/root/ci187_g5_completion` and QA checker `/root/ci187_g6_nvd` authored the [G5 report](evidence/g5-nvd-completion.md) and [G6 report](evidence/g6-nvd-verification.md). Actual passes are recorded through the helper, not inferred from this summary.

## G5 requirement-to-implementation evidence

All12 implementation tasks have current evidence in the independent G5 report. Existing issue#200 and its exact payload remain unchanged; no duplicate issue was created. G1–G4 actual approvals/renewals remain in `.lifecycle.json`, including the explicit user instruction to implement the displayed NVD revision.

## G6 scenario-to-test evidence

| Requirement / scenario | Test or manual check | Command / environment | Actual result |
| --- | --- | --- | --- |
| SCA-001 aligned dependencies and preserved auth contracts | Effective graphs/JAR hashes; generated RSA/local JWKS regression; API/mapping/architecture tests | IAM install/test; service clean verify; full Go suite | Independent IAM12/12; service89 discovered,88 pass,1 existing opt-in documentation skip; Go passes |
| SCA-002 pinned image and complete inputs | Actual official13.0.0; prepared input SHA256s; JavaScript resources in engine report | Local cold/warm Docker and current-policy GitHub cold | 145 JAR coordinates;162 engine entries including9 JavaScript resources; no disabling flags introduced |
| SCA-003 blocking threshold/error propagation | Actual earlier cold/warm exit15; nonzero/missing/stale-report/post-success-error fixtures | 27 offline security tests plus actual scanner runs | CVSS7 retained; failures red; final active findings0 |
| SCA-004 exact manufacturer-backed rules | 82 baseline instances plus8 new misidentifications;10 exact expiring rules and counterexamples | Actual report, primary sources and independent policy review | 15 wrong-match instances suppressed; no active findings; no genuine affected-component waiver |
| SCA-005 trust, metadata and cache | Event matrix, credential cleanup, inclusive48h/5min limits, invalid/missing/duplicate dates, three20s attempts and fixed modes | Fake network/clock/process tests; actual cold cache miss and two independent warm runs | All27 tests pass; actual metadata hash/time retained; no key mandatory or supplied to PR |
| SCA-006 sibling preparation/scanner/SBOM | Actual failed scan with successful SBOM, then all-success current-policy run | GitHub41618424770 negative case and41623095316 current policy | Current prepare/scan/SBOM success;145/145 SBOM coordinates covered; artifacts downloaded and checked |
| SCA-007 reproducible checks and recovery | Helper32/hook10/readiness1; OpenSpec strict12/schema; Actionlint; formatting/links/diff | Fresh independent checks in G6 report | All pass; see limitations below |

The [actual CI summary](evidence/github-final-security-summary.json) distinguishes head93e491b from tested PR merge7d5db89f. It records actual cold exit0 (~601seconds), raw report hash and all three successful jobs. Local current-policy warm took8.7seconds; an independent repeated real warm took10.47seconds, also exit0. Both report162 entries/0active/15suppressed. The original no-key API failure and local Python CA failure remain retained as historical negative evidence. Local TLS rerun uses the system trust bundle without disabling certificate verification.

## Failures and limitations

- Sonar's S8707 CLI-path findings remain externally open/red. Independent architecture review finds no untrusted remote/LLM input boundary in this local developer CLI; real leaf-symlink and overlap hazards were fixed. No Sonar rule/status was suppressed. There is no usable local token/browser session for classification; a retry found browser host unavailable and Chrome profile selection.
- Upstream Sonatype OSS Index is unavailable without separate credentials; no new disable flag was introduced. Required NVD and RetireJS analyses completed; no broader OSS Index coverage is claimed.
- ShellCheck was unavailable; pinned Actionlint ran without it. One pre-existing opt-in Modulith documentation test is skipped. API-mode token handling is tested with fakes; no live NVD key was required.
- No live auth requests, releases, test tags, registry pushes or deployments were used. This is PR1 only; PR2, G8 and merge remain separate work/decisions.

## Independent outcome

G5 and G6 independently PASS for implementation/QA against the frozen source snapshot, with the actual coverage limits above. This does not declare every PR check green, approve G8 or authorize merge.

## Subsequent Sonar disposition

After the frozen-source reviews, the maintainer explicitly authorized the four exact S8707 False Positive dispositions. The short approved rationale was stored in Sonar with optional analyzer-improvement sharing disabled. Public API confirms all four RESOLVED/FALSE-POSITIVE and quality gate OK with ignoredConditions=false; Safari visibly shows Quality gate Passed and GitHub Sonar check is SUCCESS. [Durable authorization/outcome evidence](evidence/sonar-false-positive-disposition.json) supersedes the historical red-status observations above. No rule/gate configuration or source implementation changed. This approval is not G8 acceptance or merge authorization.
