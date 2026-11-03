# Final G7 architecture review: NVD-primary PR 1

Reviewer: Codex native independent agent `/root/ci187_review_architecture`, separate from the registered makers. Date: 2026-11-03. **PASS for the G7 architecture lens; no blocking architecture or security-boundary finding identified.** This is a bounded final refresh of [the earlier source review](review-architecture-nvd.md), after current G5/G6 completion, not human UAT or merge approval.

## Reviewed snapshot and prerequisites

Implementation HEAD: `93e491b1b08b708c4e00ddecbacbc09a19a164c8`, plus frozen staged documentation/evidence/task updates. Independently computed the helper G7 fingerprint:

`280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`

Independently executed the actual helper's `Session.require('G6')`: passed, establishing current prerequisite records rather than inferring approval from report files. G5 fingerprint independently matches the value above. Read [G5 completion](g5-nvd-completion.md), [G6 verification](g6-nvd-verification.md), the current design/specification, final tasks/triage/results, the exact policy review and all source changes since my `349c0bd` review. This reviewer authored only this new report during the refresh; no implementation, gate record or external finding status was changed.

## Bounded source and policy recheck

The earlier review's ownership, published-contract, standard-library, source/artifact identity, independent SBOM graph, cache/key trust and POSIX deadline conclusions remain valid. Workflow, POMs, product auth source and approved design/specification have no new behavior change in the refresh.

The two runner corrections since the earlier review close concrete failure cases without changing ownership or trust:

- Old engine HTML/JSON reports are unlinked before validation/execution, so an otherwise successful process cannot reuse a previous report as evidence for this source/run. Symlink unlinking removes the leaf rather than its target.
- The caught error path explicitly forces exit 1, so a post-process inspection failure cannot inherit an earlier process exit 0.

Read the added regressions and independently reran all **27 security tests: PASS**. The tests remain process/network simulations, distinct from actual scan evidence.

Current XML has ten rules with exact package/version and literal CPE/CVE targeting, all expiring `2027-01-31Z`. Relative to my earlier review, six exact observed-finding rules were added and three unused historical-version rules removed. The new own-starter PURL addresses RallyOn's Spring JWT adapter, preserving Nimbus/Spring and every unrelated product match. The five additional current Spring Security artifact paths target only CVE-2018-1258 with the same manufacturer version-applicability rationale already reviewed. No broader artifact exclusion, analyzer disablement, threshold reduction or unused-feature waiver was introduced. The earlier independently opened [Spring advisory](https://spring.io/security/cve-2018-1258/) identifies Framework 5.0.5.RELEASE as affected and 5.0.6.RELEASE as fixed; the reviewed resolved graph is Framework 7.0.10.

Read [the independent exact-policy review](review-new-finding-policy.md), including actual JAR/identity matching, original baseline multiset preservation, 74 selector/boundary assertions and 20 XML-binding checks. Its XML SHA-256 `050de994ea7c7f888a11e4cf02c40d62714088790c5f8e4f012457e6280eecf0` matches the current G6 checked source. Actual current-engine evidence below now supplements that policy/source review. The policy implements the approved misidentification-correction scope and introduces no public contract or architecture deviation.

## Actual execution evidence assessed

Independently read the task-owned downloaded `github-run.json`, full run log, raw engine JSON, execution/input JSON and SBOM comparison under `/private/tmp/rallyon-187-g6-nvd/`, alongside durable summaries and G5/G6 reports. No live scanner was rerun by this architecture reviewer.

[GitHub run 41623095316](https://github.com/wlambertz/rallyon/actions/runs/41623095316) has head `93e491b…`, conclusion success, and successful preparation, scan and SBOM. The full log explicitly reports compatible-cache miss. Actual execution is exit 0 in approximately 601 seconds with the approved immutable scanner, official keyless feed and metadata hash/date. Scanner inventory and sibling SBOM comparison both identify tested PR merge revision `7d5db89fc8a98746e3df4afc47bb2f3deededb1a`; this is expected PR merge-SHA identity, not a mismatched branch source.

Independently hashed raw report JSON: `b09a201f5440e59a232d350c809ffe9d427084f9a83387d43048601cbd2cbc90`. Independently counted **162 entries, zero active vulnerability instances and 15 suppressed instances**. Prepared inventory contains 145 JARs; the actual SBOM comparison records all 145 runtime coordinates and none missing. G6 additionally verifies all prepared JAR hashes and nine embedded JavaScript entries. These observations support the source/input and sibling-job architecture, rather than relying only on string-based graph fixtures.

G6 independently executed a real current-policy warm scan with exit 0 and raw-report hash `e9955e953a23957382c758426a6e9aa4addcded0fe429acab23de4d92076a9e7`, also 162/0/15. Its reported runtime verification covers IAM/service/API/security/architecture and Go contracts. I assessed that independent evidence rather than rerunning full Maven/Go checks. Earlier-policy cold/warm exit-15 scans and upstream API/TLS failures remain historical failures, not relabeled successes.

## Sonar and remaining limits

Sonar S8707 remains externally red and requires normal external disposition. My technical assessment is unchanged: caller-selected local paths do not cross an untrusted LLM or remote-privilege boundary in this implementation. The local caller can already select `--docker` and run repository code; CI supplies fixed paths, and no AI path-ingestion integration exists. Atomic writes and overlap checks address the distinct concrete accidental filesystem hazards. Restricting outputs to cwd would break authorized temporary/runner paths without creating a security sandbox. No Sonar status, suppression or quality-gate setting was changed. Successful security CI does not establish all PR checks are green.

OSS Index coverage remains unavailable under the upstream engine's separate credential requirement; NVD and RetireJS complete, and no analyzer-disabling flag or mandatory new secret was added. This limitation must accompany human acceptance and must not be presented as comprehensive analyzer coverage. Real optional-key execution, trusted default-branch cache save, hostile-fork execution and deliberate live preparation-failure injection were not performed; those paths have inspected source and isolated trust/failure fixtures. ShellCheck was unavailable, and the existing opt-in documentation test remains skipped as disclosed by G6. Deployed E2E/live Keycloak/production PostgreSQL verification is not claimed.

Final G7 architecture outcome is PASS for this fingerprint with these explicit limits. Any implementation/input change requires affected review refresh. This report grants no G8 acceptance, merge approval, Sonar closure, all-workflow completion or closure of issue #187; PR 2 and final main verification remain outstanding.
