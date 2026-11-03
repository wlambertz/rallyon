# Tasks: issue #187 PR 1 dependency security and SBOM

G1 and G2 are human-approved; independent G3 passed. These are reviewable implementation tasks, not evidence of completed work. Apply remains blocked until actual G4 approval of the exact issue payload and continued implementation, followed by helper-based issue creation. Complete the groups in dependency order; implementation/test work within a group may be delegated to registered native makers with bounded ownership.

## 1. Finding traceability

- [x] 1.1 Classify every baseline package/finding instance using exact identities and primary affected/fixed-version sources; distinguish real affected components from proven product/version mismatches (SCA-004). Validate by comparing the disposition inventory against all 13 baseline entries / 82 finding instances / 64 distinct IDs, with no unmatched or silently dropped item.

## 2. Coordinated dependency updates and compatibility

- [x] 2.1 Align the service and IAM starter on Boot 4.1.2, all Kotlin runtime/compiler/plugins on 2.4.21, and the existing Swagger UI artifact on 5.34.1 (SCA-001). Validate with effective dependency/plugin graphs, resolved-JAR/embedded-resource identity and the full Maven checks in task 4.3.
- [x] 2.2 Add focused isolated JWT decoder/principal regression coverage for valid tokens and wrong signature/issuer/audience/expiry, required numeric user-ID and mapped-role behavior; preserve existing API/mapping/architecture expectations (SCA-001). Validate with IAM reactor tests and service clean verify; use generated test keys/local fake JWKS only, without live Keycloak requests.

## 3. Security pipeline and policy

- [ ] 3.1 Add successful dependency preparation for the exact run revision, preserve the security workflow/check names, and pin changed security Actions by verified full SHA with minimum read permissions (SCA-002, SCA-006). Validate with pinned Actionlint, input-artifact/source identity checks and actual workflow job outcomes.
- [ ] 3.2 Replace mutable scanner execution with the approved official 13.0.0 digest, resolved runtime-JAR/embedded-JavaScript coverage and --failOnCVSS 7; preserve nonzero finding/infrastructure/feed exits and retain available reports/diagnostics (SCA-002, SCA-003). Validate with fake-Docker error fixtures, exact scanner argument/version evidence and real scanner output; no ignored errors or analyzer disabling.
- [x] 3.3 Implement narrow source-linked suppression rules only for demonstrated misidentifications, with expiry validation rejecting missing/expired/>90-day rules and counterexamples showing unrelated artifacts/findings stay covered (SCA-004). Validate using `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v` plus independent review of each active rule and its vendor/source evidence.
- [ ] 3.4 Restore compatible scanner data while restricting shared saves and optional NVD credentials to trusted default-branch push/schedule runs; keep PR/keyless updates functional and credentials out of logs/caches/reports with cleanup on failure (SCA-005). Validate with fork/same-repo-PR/default-push/schedule and fake-secret fixtures in the security test suite, then real keyless cold/warm runs.
- [ ] 3.5 Make CycloneDX generation a sibling of scanning after successful preparation, compare its inventory with prepared runtime coordinates, and upload available SBOM/reports even on scanner failure (SCA-006). Validate with job-graph review/Actionlint, simulated blocking scanner and failed-preparation cases, and actual GitHub job/artifact inspection; a red scan must not become overall green.

## 4. Runtime checks and operational evidence

- [x] 4.1 Complete the fake-process and policy regression suite for successful, blocked, feed/runtime-failed, credential-cleanup, trust-event, expiry and SBOM/preparation branches (SCA-003 through SCA-007). Run `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v`; clearly distinguish local simulation from GitHub execution.
- [ ] 4.2 Exercise real scanner runs with empty and restored compatible data; record source/input hashes, image/version, data freshness, exit/result, timings and limitations without secrets or unsupported success claims (SCA-002, SCA-003, SCA-005, SCA-007). Observable check: both cold/warm run logs and reports are inspectable, all update failures remain visible, and any genuine unresolved blocking finding prevents acceptance.
- [ ] 4.3 Run affected verification: `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`, IAM tests, service `clean verify`, CLI `go test ./...`, lifecycle/helper/hook/readiness suites, OpenSpec 1.14.2 schema/spec validation, pinned Actionlint, `npm run format:check`, links and `git diff --check` (SCA-001, SCA-002, SCA-007). Record exact outcomes and distinguish environment blockers/skips from passing checks.
- [ ] 4.4 Document actual triage, reproduction/recovery commands, optional-key/trust/cache policy, exception expiry and negative outcomes; publish the implementation PR and observe its actual CI/artifacts without release/tag/registry publication during validation (SCA-004 through SCA-007). Observable check: current operation/evidence documents and CI links match the reviewed source; #187 remains open for PR 2.

## Post-implementation workflow obligations

These are not implementation completion checkboxes and must not be fabricated to complete G5:

- Obtain fresh independent G5/G6 checks against the completed source/index snapshot, then the three G7 review lenses; preserve actual identities and reports.
- Prepare the maintainer's report/CI walkthrough after G7, obtain actual G8 acceptance and separate merge approval.
- After accepted/merged PR 1, preserve/archive the completed lifecycle evidence through the normal documentation path and continue the separately required PR 2 from current main. Close #187 only after both deliveries and final main/Docker verification.
