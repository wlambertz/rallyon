# G7 correctness review: final NVD evidence refresh

Reviewer: Codex native independent agent `/root/ci187_review_correctness`, separate from all registered implementation makers. Reviewed 2026-11-03. This reviewer authored only this report during the final refresh.

**PASS for G7 correctness at source fingerprint `280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`.** No unresolved blocking correctness finding was identified in the approved PR 1 scope. The previously reported stale-report and post-success exception defects are fixed; the exact new-finding suppression policy has independent review and actual matching evidence. Sonar and analyzer limitations below remain visible; this is neither an all-checks-green declaration nor human G8/merge approval.

## Current inputs and gate prerequisites

Implementation HEAD is `93e491b1b08b708c4e00ddecbacbc09a19a164c8`, with final documentation/evidence/task-status changes in the reviewed tree. Independently recomputed the actual lifecycle helper's `Session.fingerprint('G7')` and obtained the fingerprint above. Independently invoked `Session.require('G6')` successfully: preceding gate records are current. Read the finalized independent [G5 completion report](g5-nvd-completion.md), [G6 verification report](g6-nvd-verification.md), final source/document diff, task checkboxes and scanner/CI evidence.

This is a bounded continuation of [round 1](review-correctness-nvd.md), [P2 correction review](review-correctness-nvd-round2.md) and [new-finding policy review](review-new-finding-policy.md). Those reports preserve the original failures, exact reproductions, primary applicability evidence and policy counterexamples. Independently checked all nine frozen implementation/spec file hashes against G6's recorded source identities; all match. No implementation, policy, workflow or approved-spec change invalidates those earlier source reviews. Final documentation/status changes distinguish historical red executions from actual current results and retain the original 82-instance baseline plus the separate eight new observations.

## Actual final cold and warm evidence

Independently read the downloaded raw GitHub run metadata, full run log and scanner/SBOM artifacts under `/private/tmp/rallyon-187-g6-nvd/`, rather than relying only on the durable summary. [Security run 41623095316](https://github.com/wlambertz/rallyon/actions/runs/41623095316) has successful preparation, vulnerability scan and SBOM jobs. The source head is `93e491b…`; tested PR merge revision `7d5db89fc8a98746e3df4afc47bb2f3deededb1a` agrees across execution metadata, prepared inputs and SBOM comparison.

The actual log explicitly records a compatible-cache miss. Cold scanner execution returned **0** in **601.06 seconds**, within the 60-minute bound, using the approved official 13.0.0 digest and `nist-json2-feed`. Official modified metadata is `2026-11-03T06:00:12Z`, with raw SHA-256 `01eaab97a8bd8b9f9d6d26b9a67281e1ad2a22f8afdd3c27e75e9eea7b6399c2`. Raw report SHA-256 independently matches `b09a201f5440e59a232d350c809ffe9d427084f9a83387d43048601cbd2cbc90`: **162 dependency entries, zero active vulnerability instances and 15 suppressed instances**. All 145 prepared JAR path/hash pairs are present. Nine embedded JavaScript entries remain analyzed, including Swagger bundles. NVD and RetireJS analysis completed, HTML/JSON are present and no unused-rule warning was found.

Independently inspected the actual 15 suppressed instances: three Keycloak CVEs on each of the two exact own IAM artifacts, two Prometheus-server mismatches on the registry, and CVE-2018-1258 on the seven exact Security 7.1.2 artifacts. They agree with the ten independently reviewed rules; no unrelated reported finding was suppressed. This actual matcher output closes the earlier engine-semantics execution limitation for the current inputs.

Independently hashed and recounted G6's separately executed warm raw report at `/private/tmp/ci187-g6-own-warm-reports/`: SHA-256 `e9955e953a23957382c758426a6e9aa4addcded0fe429acab23de4d92076a9e7`, also 162 entries, zero active and 15 suppressed. Its execution returned **0** in **10.47 seconds**, identified source `93e491b` and recorded the same official metadata hash/date. This reviewer inspected that independent execution rather than claiming to have launched another Docker run. Earlier seven-rule cold/warm executions exited 15 and remain failed historical evidence.

Independently compared downloaded CycloneDX components with its prepared inventory using the actual comparison function: **145 runtime coordinates, none missing**. The SBOM job completed before the longer cold scan, corroborating its sibling execution. Current PR shared-cache saving was correctly skipped; actual trusted default-branch saving is not claimed.

## Correctness checks and findings disposition

Independently reran `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py'`: **27 tests passed**. Existing fixtures still verify fixed feed/API selection, metadata age/skew boundaries and wall timeout/retries, nonzero finding/runtime/feed propagation, optional-key trust/redaction/cleanup, stale-output rejection, post-scan validation failure, exact suppression/expiry policy and filesystem boundaries. Prior direct P2 reproductions and primary-source/policy counterexamples remain applicable to the unchanged source bytes.

No new blocking finding was identified. Actual report/hash/revision checks now substantiate the previously pending successful current-policy cold/warm and SBOM outcomes. G6 supplies independent IAM/service/Go/full workflow validation; this lens did not redundantly rerun those heavier checks.

## Limits retained for human acceptance

The default OSS Index analyzer reports unavailable without its separately required credentials. No disabling flag, broad exclusion or new mandatory secret was introduced; NVD and RetireJS coverage actually completed. The approved general wording about active analyzers cannot be presented as proof of OSS Index coverage. That upstream limitation must remain explicit in G8 and delivery notes. Zero active findings describes these actual configured executions and available sources, not comprehensive absence of vulnerabilities.

Sonar S8707 remains a separate red external finding requiring its normal disposition. Prior architecture review explains intentional local CLI path authority and the implemented overlap/leaf-symlink safeguards; this correctness pass does not mark the external finding resolved or make every PR check green.

Real optional-key execution and default-branch cache publishing were not exercised; inspected trust conditions and isolated fake-key/event tests supply their evidence. Live Keycloak, deployed E2E and production migration checks were not performed; ShellCheck was unavailable and the existing opt-in documentation test remained skipped as recorded by G6. The broader issue #187 PR 2 and final main verification remain outstanding. This report records only the correctness lens and grants no human UAT, merge, release or issue-closure authorization.
