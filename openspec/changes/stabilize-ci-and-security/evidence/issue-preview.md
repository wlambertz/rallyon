# G4 issue payload preview

## Decision requested

Create exactly one real PR-1 implementation issue in `wlambertz/rallyon` and continue the approved implementation after checked helper creation. The issue references existing parent #187, which remains open. No labels, assignees, milestone or draft dependencies are requested.

## Decomposition and observed workflow constraint

The approved program has two sequential delivery packages. One child makes the first package's acceptance/delivery traceable while keeping #187 as the parent. In addition, the current helper requires at least one nonempty issue draft and has no supported existing-issue binding: `.agents/skills/feature-lifecycle/scripts/lifecycle.py` rejects an empty `issues` list. This is recorded workflow friction, not permission to forge an issue attempt/reconciliation, silently bypass G4 or expand scope by changing the helper. The concrete draft below needs actual human approval before any creation. It is implementation work, not a live test issue.

The canonical exact structured payload is [issue-drafts.json](../issue-drafts.json). This preview is generated from it; only that checked payload may be submitted.

## Exact title

fix(ci): remediate dependencies and decouple SBOM generation

## Exact body

<!-- prettier-ignore-start -->
## Problem and parent scope

Refs #187. This is the first implementation package of its approved two-PR plan. Main security scans fail on a mixture of genuine affected dependency versions and candidate product mismatches; SBOM generation currently depends on scanner success. The parent issue remains open for the separate all-workflow/CodeQL/Docker/release package and final main verification.

## Approved contract

G1 and G2 are explicit maintainer decisions. [G2 review package](https://github.com/wlambertz/rallyon/tree/4a43143/openspec/changes/stabilize-ci-and-security) contains the proposal, seven SCA requirements / 19 scenarios, design and advisory value assessment. Fresh independent G3 passed with no material correction. [Implementation checklist](https://github.com/wlambertz/rallyon/blob/fix/issue-187-security-sbom/openspec/changes/stabilize-ci-and-security/tasks.md) is prepared for G4 approval; it is not completed implementation evidence.

## Acceptance criteria

- SCA-001: service and IAM starter use Boot 4.1.2 together; Kotlin compiler/runtime/plugins use 2.4.21; the existing Swagger UI artifact uses 5.34.1. Effective dependencies and packaged resources prove alignment, with existing API/JWT/role/claim/database/ro contracts preserved.
- SCA-002: official Dependency-Check 13.0.0 is pinned to OCI index sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a; runtime-JAR and embedded-JavaScript analysis remain active; changed security Actions use verified full SHAs.
- SCA-003: CVSS >= 7 and all scanner/feed/runtime failures remain blocking. No ignored exits, reduced threshold, disabled analyzers or missing-report-as-clean behavior.
- SCA-004: every baseline entry/finding is accounted for using exact identities and primary affected/fixed-version evidence. Only demonstrated wrong-product/version matches receive narrowly scoped, source-backed, <=90-day rules; genuine affected components are not waived because a feature may be unused.
- SCA-005: cold/warm/keyless updates work as specified; PRs (including same-repository PRs) receive no optional NVD key and do not save the trusted shared cache. Only trusted default-branch push/schedule runs may use the optional key/save data; credentials never enter logs, reports or caches.
- SCA-006: successful dependency preparation feeds sibling scanner and CycloneDX jobs at the same revision. A failed scan still permits SBOM generation/upload and available report retention, while the overall workflow stays red. Preparation failure blocks both consumers. Existing workflow/check names remain.
- SCA-007: current local/CI evidence, negative fixtures, operation/recovery guidance, fresh independent G5/G6 and three G7 reviews are recorded. G8 and merge remain explicit separate human decisions. No live auth requests, release/test-tag publication or registry pushes are used for validation.

## Validation commands and observations

- ./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
- ./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml test
- ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
- cd tools/cli/ro && go test ./...
- python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v
- Lifecycle helper, hook and schema-readiness Python suites; OpenSpec 1.14.2 schema validate rallyon-feature and validate --all.
- Pinned Actionlint; npm run format:check; active links and git diff --check.
- Real cold/warm scanner evidence plus fake-process blocking/feed/error/secret/trust/expiry regressions; actual GitHub job/artifact inspection proves independent SBOM behavior. Simulations are identified as such.

## Boundaries and delivery

No product feature, public contract, auth policy, migration, Java container runtime, Go-module minimum or repository protection change. Optional NVD credentials remain optional. Genuine unresolved blocking findings prevent acceptance. Workflow hardening outside this security package remains PR 2 under #187. Do not close #187 when this child is complete.

<!-- prettier-ignore-end -->
