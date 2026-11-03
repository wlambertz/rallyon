# UAT

## Persona and success measures

Primary persona: RallyOn maintainer/contributor reviewing trustworthy dependency security and retained inventory evidence. Success means a real current-policy cold scan and warm scan complete, CVSS7/errors remain blocking, all required runtime/embedded-resource inputs are covered, SBOM survives a red scan, and source/trust limitations are visible. This document records the prepared maintainer walkthrough and the subsequent explicit human acceptance below. Commands and observations retain their actual agent/checker provenance; no unobserved human command execution is claimed.

## Current revision, environment and setup

Implementation head93e491b; actual successful CI tested PR merge7d5db89fc8a98746e3df4afc47bb2f3deededb1a. Final reviewed source/index fingerprint280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3. G5/G6 and all G7 lenses passed independently. Later evidence-only commits preserve that fingerprint.

Use a POSIX terminal at the existing isolated worktree. GitHub CLI reads public/authorized repository artifacts; Python tests use fake external processes/network. Docker was used for the already recorded actual runs; no deployment or product auth is required. Use a new task-owned temporary folder for downloaded artifacts. A local real metadata rerun, if needed, uses `SSL_CERT_FILE=/etc/ssl/cert.pem` on this host with certificate verification enabled; CI uses Ubuntu trust normally.

## Scenarios

| Scenario | Actions | Expected outcome | Actual observed outcome |
| --- | --- | --- | --- |
| Current cold scan and source identity | Open [run41623095316](https://github.com/wlambertz/rallyon/actions/runs/41623095316), inspect preparation/scanner/SBOM logs and [durable CI summary](evidence/github-final-security-summary.json) | All three jobs success; cache miss, official image13.0.0, head/merge identities distinguished | Agent and independent QA observed success, cold~601s; maintainer acceptance recorded below |
| Real current report and complete inputs | Download dependency-check-report artifact; inspect JSON counts and input-inventory.json | 162 engine entries,0 active findings,15 exact wrong-match instances suppressed,145 prepared JARs,9 JS resources including both Swagger UI bundles | Agent and independent QA verified raw report hashb09a201f…cbc90; maintainer acceptance recorded below |
| Warm behavior and source freshness | Read [local warm execution](evidence/nist-final-execution.json), [summary](evidence/nist-final-report-summary.json) and G6 own real warm evidence | Same policy/inputs; mode nist-json2-feed; actual current metadata hash/time; no offline fallback | Root warm0/~8.7s and fresh checker warm0/~10.47s, matching0active/15suppressed; maintainer acceptance recorded below |
| Blocking vulnerabilities/errors are real | Inspect preserved earlier cold/warm logs and run the27-test command below | Earlier real scans exit15, not green; invalid/stale metadata, nonzero engine exits, missing/stale reports and verification exceptions fail | Actual exit15 preserved; all27 negative/positive fixtures pass; maintainer acceptance recorded below |
| SBOM independent of failure | Open actual negative run41618424770, then current success run; download current SBOM and compare prepared PURLs | Earlier scan failure still permits successful SBOM; current SBOM covers145/145 prepared coordinates | Actual jobs/artifacts independently verified; maintainer acceptance recorded below |
| Trust and contracts | Inspect workflow's key/cache conditions and test event/secret fixtures; review independent runtime results | PRs get no NVD key/shared save, trusted default-branch push/schedule only; no public contract changes | Fixtures pass; IAM12,service88pass+1existing skip,Go pass; no live key/auth test claimed |
| Remaining Sonar finding | Review [architecture rationale](evidence/g7-architecture-nvd.md) and [Sonar PR dashboard](https://sonarcloud.io/dashboard?id=wlambertz_rallyon&pullRequest=201) | A maintainer classifies the concrete CLI-path claims against actual caller trust; no blanket rule disabling or fabricated green status | Sonar remains externally red; no status mutation; subsequent approved classification and G8 decision recorded below |

Witnessed local negative-path command (no live external calls):

```sh
python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v
```

Optional current-CI artifact walkthrough:

```sh
gh run view 41623095316 --repo wlambertz/rallyon
gh run download 41623095316 --repo wlambertz/rallyon --name dependency-check-report-41623095316-7d5db89fc8a98746e3df4afc47bb2f3deededb1a --dir /private/tmp/rallyon-187-uat-report
```

Inspect JSON `scanInfo`, dependency vulnerability arrays and input inventory; the durable summaries record expected hashes/counts. Download the sibling SBOM artifact from the same run for the145-coordinate comparison. Cleanup is optional removal of only the explicitly created temporary download folders after review; no tracked files, tags or releases are changed.

## Limitations and failures

At the original review, Sonar was red and browser access was unavailable. The subsequent explicitly approved disposition below resolved those four findings and Sonar passed; those earlier observations are retained as history. The current evidence-only commit triggers CI again, and normal merge readiness is not inferred from acceptance alone. Upstream OSS Index is unavailable without separate credentials; required NVD+RetireJS completed. ShellCheck was unavailable; one pre-existing opt-in documentation test is skipped. Trusted-key/default-cache-save/fork/preparation-failure paths use isolated simulations rather than live credentials or fake real PRs. No product deployment or live auth/release/tag/registry publication occurred.

## G8 human decision

Accepted by the maintainer for PR1 of issue#187, PR#201.

Actual quote: **“G8 für PR1 abnehmen”**.

Source: conversation reply to `request_user_input_async` call `call_666090c0de5541598dd505069535698b`, item0. The displayed question explicitly covered the recorded cold/warm results,145/145 SBOM, IAM/backend/Go and27 security tests, current G5–G7, the approved Sonar false positives, OSS Index/ShellCheck/opt-in-test limitations, and the ongoing CI retry for the evidence-only commit. The user expressly accepted this package. Record that actual decision through the lifecycle helper; no additional acceptance is inferred.

Acceptance applies to PR1 only. Merge and PR2 remain separate. The current source/index review fingerprint remains unchanged; no implementation code changed for this decision.

## Separate merge decision

Pending explicit approval of the final reviewed revision and normal repository checks. No protection bypass. PR2 and final main/Docker observation remain required before closing#187.

## Subsequent Sonar disposition

After the frozen-source reviews, the maintainer explicitly authorized the four exact S8707 False Positive dispositions. The short approved rationale was stored in Sonar with optional analyzer-improvement sharing disabled. Public API confirms all four RESOLVED/FALSE-POSITIVE and quality gate OK with ignoredConditions=false; Safari visibly shows Quality gate Passed and GitHub Sonar check is SUCCESS. [Durable authorization/outcome evidence](evidence/sonar-false-positive-disposition.json) supersedes the historical red-status observations above. No rule/gate configuration or source implementation changed. That earlier four-finding approval was not G8 acceptance or merge authorization. The subsequent explicit G8 decision is recorded above; merge remains pending.
