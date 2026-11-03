# Refinement: issue #187 CI stabilization and hardening

## Problem and observed evidence

The current main revision is `a5834c7`. Its Java CI, lifecycle tooling, CodeQL and Docker workflow completed successfully; Security scan & SBOM failed on reported dependency vulnerabilities and skipped the downstream SBOM job. The [baseline findings](evidence/baseline-findings.json) preserve exact package/CPE/CVE evidence from the uploaded JSON report, including source revision and report SHA-256. The report contains 141 scanned dependencies. Findings include both candidate misidentifications (the project's own IAM core mapped to Keycloak server) and vendor-confirmed affected dependencies (PostgreSQL JDBC 42.7.11). A failed scanner is not automatically a flaky CI job, and a matching CPE alone does not prove applicability.

The existing scanner wrapper is pinned as an Action but loads an internal latest image. A read-only [registry inspection](evidence/scanner-image.md) established the requested 13.0.0 image digest. No remediation or post-implementation tests have run.

## Primary user and owning context

Primary audience: RallyOn maintainer and contributors. Owning context: repository delivery/tooling across tournamentmgmt, shared IAM dependency management and developer CLI workflows. Product users benefit indirectly from trustworthy validation and releases; this is not an Organizer UI feature or a new business bounded context.

## Supplied decisions and delivery boundary

The maintainer selected all-workflow overhaul, patch updates plus targeted security fixes, two PRs, and CodeQL for Actions/Java-Kotlin/Go, then supplied the exact implementation plan with the instruction “PLEASE IMPLEMENT THIS PLAN:”. That plan is saved at `docs/issue-implementation-plans/issue-187-ci-stabilization.md` and published in issue #187. Task authorization and selected preferences are recorded as facts; they are not fabricated G1/G2/G4 gate approvals.

The scope of issue #187 covers both sequential packages below. This lifecycle change prepares the first package; the second package remains required after the first is accepted and merged, with its own implementation/check/acceptance evidence. Issue #187 stays open until both are delivered and main is verified.

## Scope

1. PR 1: baseline CVE triage with vendor evidence; Boot 4.1.2 together in service and IAM starter, Kotlin 2.4.21 and Swagger UI 5.34.1; digest-pinned Dependency-Check 13.0.0; blocking CVSS >= 7; narrowly justified suppressions with at most 90-day expiry; trusted cache writes and active feed updates; optional NVD_API_KEY with no mandatory new secret; independent SBOM generation after successful dependency resolution; reports available on failures.
2. PR 2: preserve workflow file/check names while pinning actions and tools, limiting permissions/timeouts and controlling concurrency; Temurin Java 21 / Node 24.22.2 / Go 1.26.10 / Python 3.12 / OpenSpec 1.14.2; PR/main Go tests; secret-free validation and safe conditional Sonar; Actions/Java-Kotlin/Go CodeQL; PR Docker build without login/push and retained trusted publication; GoReleaser 2.19.0 packaging with existing six targets, ro/v* tag and draft-release contract, tested publish wrapper and no paid monorepo dependency.

## Exclusions and compatibility

No REST, JWT, role/claim, database or ro CLI contract change. Shared IAM changes are dependency metadata only; token validation and authority policy remain strict. No Flyway migration, local bootstrap repair, product feature, frontend stack, repository protection bypass, broader secret access or vulnerability-threshold reduction. Keep the Go module minimum and Java-25 container runtime. Validation creates no real release/test tag or registry push. Existing trusted main/manual Docker publication remains an intentional production workflow after approved delivery.

## Observable success criteria

- Every baseline finding is classified with exact package identity and vendor evidence. Genuine unresolved blocking findings prevent acceptance; broad suppressions, disabled analyzers or ignored scanner errors cannot manufacture green checks.
- Cold and warm scanner runs behave correctly; expired rules fail; simulated blocking findings keep the scan red while reports and an independently generated SBOM remain available.
- IAM/service full verification, Go tests, lifecycle/hook/readiness, OpenSpec validation, Actionlint, formatting and whitespace checks pass for affected changes; independent checker identities and limitations are recorded.
- PR/fork/no-secret paths run applicable tests without write credentials. Docker PRs do not publish, CodeQL analyzes the selected code languages, release dry-runs contain the six expected targets/files/checksums and reject invalid/unsafe publication paths.
- Both PRs receive actual human UAT and separate merge decisions. Final main workflows including Docker are observed green; only then are #187 and its checklist closed.

## Assumptions and budget

The existing issue is reused. No new issue is required unless approved decomposition later demonstrates a concrete need; no live issue creation is authorized by this artifact. NVD_API_KEY is optional; repository secret-name inspection found SONAR_TOKEN only. External advisory/feed/image availability can fail and must remain visible rather than be converted to success. The numerical value assessment and detailed requirements/design belong to G2 after G1. No numerical work budget or completion-date commitment was agreed.

## G1 decision

Pending explicit maintainer scope approval of this concrete artifact. The supplied plan and resume request do not replace the full workflow's explicit gate decision. No implementation, specification, post-implementation verification/review/UAT evidence or issue creation has been performed in this lifecycle session.
