## Purpose

Give the RallyOn maintainer reproducible, actionable dependency-security evidence without weakening the vulnerability gate, application contracts or CI trust boundaries. This change covers issue #187 PR 1; broader workflow/release hardening remains the sequential PR 2.

## ADDED Requirements

### Requirement: Align security dependency updates (SCA-001)

The service and shared IAM starter SHALL use Spring Boot 4.1.2 together. Kotlin compiler, runtime and compiler plugins SHALL use 2.4.21, and the existing Swagger UI artifact SHALL resolve to 5.34.1. The resulting application SHALL preserve existing REST, JWT issuer/audience/expiry/signature validation, role/claim, database and ro contracts.

#### Scenario: Resolve the coordinated dependency set

- **WHEN** dependencies are resolved for the reviewed source revision
- **THEN** the effective service and IAM starter parents, Kotlin artifacts/plugins and Swagger UI match the approved versions, without mixed Kotlin plugin/runtime versions
- **AND** the resolved inventory and source revision are retained as evidence

#### Scenario: Retain authentication and API behavior

- **WHEN** IAM and service checks execute after the updates
- **THEN** existing valid requests, anonymous rejection, token validation failures, principal claim/role failures and API response/error behavior remain consistent with the existing contracts
- **AND** dependency compatibility is not obtained by weakening validation or rewriting shipped migrations

### Requirement: Reproduce scanner execution and keep coverage (SCA-002)

The dependency scanner SHALL run official Dependency-Check 13.0.0 using OCI index digest sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a against resolved runtime JARs, retaining embedded-JavaScript analysis. Changed security-workflow Actions SHALL be pinned to complete commit SHAs.

#### Scenario: Identify an execution

- **WHEN** a scan starts
- **THEN** its evidence identifies the source revision, scanner version/image digest and resolved input inventory
- **AND** execution does not obtain a mutable latest scanner or disable analyzers to avoid findings

#### Scenario: Include embedded web resources

- **WHEN** a resolved runtime JAR contains supported embedded JavaScript resources
- **THEN** those resources remain eligible for the scanner's existing JavaScript vulnerability analysis

### Requirement: Keep vulnerability and infrastructure failures blocking (SCA-003)

Dependency-Check's CVSS threshold SHALL remain 7. A blocking finding or nonzero scanner/feed/runtime exit SHALL fail the scan and workflow; the system MUST NOT lower the threshold, filter away real findings, or use ignored errors to manufacture success.

#### Scenario: Reject blocking findings

- **WHEN** the scanner reports a vulnerability meeting its CVSS >= 7 gate and returns its blocking status
- **THEN** the scan remains failed while its generated reports are retained

#### Scenario: Reject infrastructure or feed failure

- **WHEN** image startup, data update, network access or scanner execution fails
- **THEN** the failing status propagates and the evidence distinguishes an execution failure from a clean result
- **AND** absence of a report is shown as unavailable rather than zero findings

#### Scenario: Accept a successful scan

- **WHEN** the scanner completes successfully with no remaining blocking findings after reviewed corrections
- **THEN** the scan succeeds and retains its actual report, without claiming complete absence of vulnerabilities below the configured threshold

### Requirement: Correct only demonstrated misidentifications (SCA-004)

Every baseline finding SHALL have a source-linked disposition naming the package, detected identity, affected-version evidence and action. Suppressions SHALL select the exact package/version identity and the specific demonstrably incorrect CPE or CVE, include rationale and primary evidence, and expire within at most 90 days. Lack of application exposure alone SHALL NOT justify suppressing a real affected component.

#### Scenario: Correct a wrong product match

- **WHEN** evidence proves the project's IAM artifact or another package was matched to an unrelated product
- **THEN** a reviewed rule removes only that erroneous match and keeps the artifact, its transitive libraries and unrelated findings in coverage

#### Scenario: Reject unsafe or expired rules

- **WHEN** a rule lacks evidence, narrow targeting or expiry, is expired, or extends beyond the permitted 90-day interval
- **THEN** validation fails before a clean scan can be claimed

#### Scenario: Retain a genuine affected dependency

- **WHEN** a finding applies to the actual package/version, even if its vulnerable feature is believed unused by this application
- **THEN** it is fixed or remains visible/blocking according to the scanner gate, rather than receiving a product-mismatch waiver

### Requirement: Bound cache and credential trust (SCA-005)

All scan runs SHALL retain active data-update behavior and may restore compatible scanner data. Only trusted default-branch push/schedule runs SHALL save the shared cache or receive optional NVD_API_KEY. Pull requests, including same-repository PRs, SHALL NOT publish the shared trusted cache or receive that optional key. No new secret SHALL be mandatory.

#### Scenario: Run from cold or warm data

- **WHEN** compatible cached data is absent or present
- **THEN** the scan respectively initializes or reuses it while honoring active update/freshness behavior
- **AND** an update failure is not converted into an offline green result

#### Scenario: Process a pull request without privileged credentials

- **WHEN** a fork or same-repository pull request is scanned
- **THEN** it may restore compatible default-branch data but cannot save the shared trusted cache or receive the NVD key

#### Scenario: Process a trusted default-branch run

- **WHEN** the security workflow runs from a push to the repository default branch or its configured schedule
- **THEN** the scan may use the optional key and save updated compatible data
- **AND** an absent key still selects supported keyless operation

#### Scenario: Keep credentials out of retained data

- **WHEN** a key is supplied or a scan fails
- **THEN** it does not appear in logs, reports, cache contents or uploaded artifacts, and temporary credential material is removed

### Requirement: Generate independent SBOM evidence (SCA-006)

Successful dependency preparation SHALL feed independent scan and CycloneDX SBOM jobs for the same source revision. SBOM generation SHALL NOT depend on scanner success. Existing Dependency vulnerability scan and Generate SBOM check names and the security workflow filename SHALL remain available.

#### Scenario: Preserve SBOM on a vulnerability failure

- **WHEN** dependency preparation succeeds and the scan fails with blocking findings
- **THEN** the SBOM job still generates and uploads its CycloneDX JSON inventory
- **AND** the overall workflow remains failed because the scan is red

#### Scenario: Block downstream work after preparation failure

- **WHEN** dependency preparation fails
- **THEN** neither scan nor SBOM claims successful prepared input or a clean inventory
- **AND** the preparation failure remains visible

#### Scenario: Retain useful artifacts

- **WHEN** scan or SBOM execution finishes, including failure
- **THEN** generated scanner HTML/JSON, diagnostic metadata/logs and CycloneDX JSON are uploaded when available
- **AND** artifact names identify the run/revision sufficiently to avoid mixing unrelated inputs

### Requirement: Require current validation before delivery (SCA-007)

Delivery SHALL include finding traceability, cold/warm execution evidence, negative failure/credential/cache tests, full affected-runtime checks and fresh independent implementation/test/review evidence. Human UAT acceptance and separate merge approval SHALL remain required; successful file creation or CI alone grants neither.

#### Scenario: Observe real CI and limitations

- **WHEN** the implementation PR is reviewed
- **THEN** actual GitHub job outcomes and artifacts are inspected, including independent SBOM execution, and local fake-process checks are not represented as real GitHub or NVD executions

#### Scenario: Keep the second delivery outstanding

- **WHEN** PR 1 has been accepted and merged
- **THEN** issue #187 remains open for PR 2 and final main verification, rather than claiming all-workflow completion
