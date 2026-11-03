# Independent G5 completion check: issue #187 PR 1

Checker: native Codex agent `/root/ci187_g5_completion`, fresh context, separate from every registered implementation maker. Date: 2026-11-03. This checker authored only this report.

**PASS for G5 implementation completion.** All twelve implementation tasks have inspectable source and execution evidence. No unresolved implementation blocker was identified in the approved PR 1 scope. This finding does not grant G6/G7, human UAT acceptance, merge approval, all-checks-green status, or completion of issue #187.

## Frozen inputs and method

Implementation HEAD: `93e491b1b08b708c4e00ddecbacbc09a19a164c8`. The coordinator identified the frozen pre-report index tree as `aef8284521a4fe032e4dcf6dfea6bc0597f48ea5`. Independently computed both G5 and G6 fingerprints with the actual helper's `Session.fingerprint` after the final freeze:

`280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`

Read root/IAM/service instructions, lifecycle G5 contract, approved proposal/design/specification/tasks, exact issue payload and renewed NVD authorization, source/index diff, scanner/workflow/policy/tests, dependency identities, operation/triage documents, historical failures, final local reports, independent review reports and actual downloaded GitHub evidence. Independently executed helper status and `Session.require('G4')`: G1–G4 current, issue #200 recorded, no unresolved issue-creation attempt. No additional issue was created. Completion boxes were checked against evidence rather than treated as proof.

## Task completion and requirement traceability

| Task | Independent completion evidence |
| --- | --- |
| 1.1 / SCA-004 | Compared raw baseline and current disposition `(filename, finding ID)` multisets: exact equality across 82 instances, 13 entries and 64 IDs. Every instance has action, affected-range evidence, primary sources and rationale. The six-entry/eight-instance post-upgrade inventory is separate, preserving the original baseline. |
| 2.1 / SCA-001 | Inspected both POM diffs and `dependency-identity.json`: Boot parents 4.1.2, Kotlin compiler/runtime/plugins 2.4.21 and managed Swagger UI 5.34.1. Resolved graph and bundle/JAR hashes retain packaged DOMPurify 3.4.17 evidence. Independent QA additionally inspected the newly built Boot JAR. |
| 2.2 / SCA-001 | Read the real-decoder tests: generated RSA/local loopback JWKS, valid signed token, other signature, issuer, audience, expiry, numeric user-ID and mapped-role cases. No production auth policy/source or shipped migration changed. Actual IAM and service verification passed, including existing principal/API/security/architecture expectations. |
| 3.1 / SCA-002,006 | Read workflow: exact `github.sha`, credential-free checkout, full-SHA Actions, read-only permissions, successful dependency preparation and run/SHA-bound artifacts. Actual current run preparation succeeded; prepared inventory and both consumer revisions agree. |
| 3.2 / SCA-002,003 | Read runner arguments: approved official 13.0.0 digest, runtime JAR inputs, active updates, CVSS 7, no exclusion/ignored error/analyzer-disabling flag. Independently inspected raw reports with 162 entries and nine embedded JavaScript resources, all 145 prepared JAR hashes present. Fake-process cases preserve failures and missing-report failure; actual current-policy executions return 0. |
| 3.3 / SCA-004 | Active XML contains ten exact package/version and CPE/CVE rules expiring `2027-01-31Z`; no unused-feature waiver. Read independent exact-policy/manufacturer/engine-matcher review and version/identity/target counterexamples. Six narrowly justified rules were added after actual observations and three unused historical-version rules retired. Expiry and unsafe-selector tests pass. |
| 3.4 / SCA-005 | Workflow and runner independently restrict optional key/shared saves to actual default-branch push/schedule; PRs cannot receive/save them. Fixed feed/API selection, official metadata hash/date, three 20-second attempts and exact age/skew boundaries are implemented. Fixtures cover event trust, cleanup/redaction and rejection paths; real cold/warm keyless operations and metadata evidence are inspectable. Trusted-key behavior is simulated with fake credentials, not claimed as live execution. |
| 3.5 / SCA-006 | Scan and CycloneDX are sibling jobs depending only on preparation. Available artifacts upload after failure without masking exits. Historical real red-scan/SBOM-success run remains evidence; current actual SBOM succeeds with 145 runtime coordinates and none missing. Preparation-failure behavior has fixture/job-graph evidence, not a claimed live induced GitHub failure. |
| 4.1 / SCA-003–007 | Independently reran all 27 security tests successfully. They cover metadata boundaries/retries/timeouts, mode selection, failures, trust, secret handling, stale reports, post-scan verification errors, filesystem safeguards, expiry/policy and workflow graph. Fake process/network results remain explicitly simulations. |
| 4.2 / SCA-002,003,005,007 | Inspected actual raw cold/warm/final execution evidence and current CI artifact. Historical cold and unchanged-policy warm `9bbeb3a` both exited 15 for eight subsequently reviewed misidentifications. Revised-policy local warm and independent repeated warm return 0. Current-policy GitHub cache-miss cold returns 0 in 601 seconds; these results do not relabel the earlier failures as successful. |
| 4.3 / SCA-001,002,007 | Inspected independent QA logs/identities: IAM install/test 12 pass; service 89 discovered/88 pass/one existing opt-in documentation skip; full Go suite; 27 security, 32 lifecycle, 10 hook and one readiness tests; OpenSpec 1.14.2 strict 12 items/schema; Actionlint 1.7.12; formatting, links and whitespace. This G5 checker reran the 27 security tests and lifecycle checks rather than duplicating full Maven/Go QA. ShellCheck was unavailable and not run. |
| 4.4 / SCA-004–007 | Current operation/triage documents explain reproduction, optional-key/feed/cache trust, failure recovery, exact exceptions/expiry and actual outcomes. PR #201 and actual security run/artifacts exist. No release/tag/registry publication is used as evidence. Parent #187 remains open for separately scoped PR 2 and final main verification. |

## Actual report and CI observations

Independently read the downloaded run metadata and raw engine report under `/private/tmp/rallyon-187-g6-nvd/`, not only the maker's summary. [Run 41623095316](https://github.com/wlambertz/rallyon/actions/runs/41623095316) completed **success**, with all three jobs successful. Its source head is `93e491b…`; execution/artifacts correctly identify tested PR merge revision `7d5db89fc8a98746e3df4afc47bb2f3deededb1a`. Raw report SHA-256 is `b09a201f5440e59a232d350c809ffe9d427084f9a83387d43048601cbd2cbc90`: 162 entries, zero active vulnerability instances and 15 suppressed wrong-match instances. The SBOM comparison records all 145 runtime coordinates with none missing.

Independently hashed the separate QA warm report: `e9955e953a23957382c758426a6e9aa4addcded0fe429acab23de4d92076a9e7`, with the same 162/0/15 counts and all prepared JAR hashes present. Maker final warm summary/raw hash agree. Official metadata timestamp and SHA-256 agree across the inspected executions. Durable selected-field report summaries preserve original raw-report hashes; log whitespace normalization is explicitly recorded in `nist-log-normalization.json`, with original task-owned logs retained. A summarized report is not claimed to be the full raw report.

## Limits and remaining decisions

- Sonar S8707 remains an external red quality-gate result requiring disposition. Independent architecture review finds no untrusted LLM path boundary in this local developer CLI, and concrete symlink/overlap hazards were fixed. No finding status or credential was mutated. Security-workflow success does not establish every PR check is green.
- OSS Index is unavailable without its separate upstream-required credentials. No disabling flag was added; required NVD and RetireJS analysis completed. No OSS Index coverage or new mandatory secret is claimed.
- Cache/secret trust and failure branches combine source/fixture proofs with real keyless executions. No real optional-key scan, deliberate GitHub preparation failure or malicious-fork execution was performed by this checker.
- G6 must independently certify testing and the three G7 lenses must remain current. G8 requires an actual maintainer walkthrough/acceptance; merge requires separate explicit approval. This G5 report grants neither, and PR 2/final main verification remain outstanding.

No scope, public REST/JWT/CLI, migration, runtime-auth-policy or additional product-module deviation was found. The observed-misidentification policy adjustment implements the already-approved narrow correction policy and was independently reviewed before successful rescans.
