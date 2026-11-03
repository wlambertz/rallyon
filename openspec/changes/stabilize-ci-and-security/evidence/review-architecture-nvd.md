# Independent architecture and security-boundary source review

Reviewer: Codex native agent `/root/ci187_review_architecture`, fresh context separate from implementation makers. Date: 2026-11-03. Reviewed source: `349c0bd23aae64f0bfbae7fee6793378bd6f4ebc`, PR #201, diff `origin/main...HEAD` in `/private/tmp/rallyon-issue-187-security`.

**PASS for the architecture/source-review lens: no blocking source finding identified.** This is substantive source review prepared ahead of formal G7 recording. G5/G6 completion, successful current cold/warm scans, real GitHub job/artifact observations, UAT and merge approval are not established by this report. Formal G7 recording must wait for current G5/G6 evidence and any necessary bounded refresh.

## Inputs and checks

Read root and IAM/service AGENTS.md, feature-lifecycle skill and fresh-context review reference, repository routing skill, wiki module context, approved change design/refinement/specification, NVD-revision authorization/G3 evidence, workflow, scripts/tests, suppression XML, operational guidance, dependency metadata diff and added decoder tests. Inspected existing local-validation and historical scan records as dated maker/historical evidence, not current clearance.

Independently ran:

- `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v`: 25 tests passed. These use fake processes/network boundaries and are not real scanner or GitHub executions.
- `git diff --name-only origin/main...HEAD -- '*src/main*' '*migration*' 'tools/cli/ro/*'`: no changed product source, migrations or CLI implementation in these paths.
- `npm run format:check`, explicit Prettier check of this new report and `git diff --check`: passed.
- Direct raw diff and source inspection, including POM version alignment and real-decoder test wiring.

Independently opened the upstream Dependency-Check 13.0.0 suppression implementation and manufacturer applicability sources linked below. Did not modify implementation, planning artifacts, lifecycle records or Sonar status; this report is the only authored file.

## Architecture and trust findings

| Boundary | Outcome and evidence |
| --- | --- |
| Ownership and published contracts | Changes stay in repository delivery/security tooling, dependency metadata and isolated tests. No new domain coupling, module, public REST/JWT/ro signature, schema migration or product runtime library is introduced. IAM starter and service both select Boot 4.1.2/Kotlin 2.4.21; Swagger UI is explicitly managed at 5.34.1. Shared IAM metadata change is called out in approved design; no runtime policy weakening is present. |
| Source/input identity | Each job checks out explicit `github.sha` without persisted credentials. Prepared artifact names bind run ID and SHA; the scanner recomputes exact JAR paths/hashes/PURLs and revision against the manifest before invocation. Repository-layout inputs retain coordinates alongside bytes. The image has the approved immutable digest and receives read-only input/suppression mounts, writable task-owned data/reports, no full workspace or Docker socket mount. |
| SBOM graph | Both scan and SBOM need only successful preparation. SBOM generation reruns the pinned 2.9.1 Maven plugin on the same explicit revision and installs IAM metadata independently. Comparison requires every prepared runtime PURL to occur in the SBOM. It establishes coordinate coverage, not byte-for-byte equivalence of independently resolved artifacts; that is consistent with the approved design. A red scanner cannot skip its sibling job; failed preparation blocks both. Always-run uploads preserve available evidence without changing failed step status. |
| Key and cache | Workflow secret expression and runner trust predicate both require push/schedule on the actual default branch. PRs get neither the key nor the shared cache-save step. Separate cache restore/save, scanner-version/digest compatibility prefix, unique run/attempt keys and GitHub branch cache scopes avoid publishing PR data into trusted default-branch scope. Cache-save also requires prior success. No publication/security-events write permission is added. |
| Key retention | Key is mounted from a restrictive temporary properties file outside cache/report paths, omitted from process arguments/environment, redacted from captured output and checked against retained report/cache files. Temporary-directory context removes the credential on normal/error exits. Fake-key tests exercise success/failure cleanup and retained-file leak rejection. Host JAVA_HOME is removed from the child environment. |
| NVD/update behavior | Mode selection is fixed before execution. Keyless official metadata retrieval is bounded by three 20-second attempts including reads/DNS via POSIX alarm; parsing rejects absent/duplicate/naive/invalid/out-of-window dates. Exact specified boundaries pass. Failure prevents Docker even with cached data. Active updates, CVSS 7 and analyzers remain enabled; no fallback to offline success exists. Nonzero runtime/feed/finding exits and success without a JSON report fail. |
| Standard-library design | Added scripts depend only on Python standard library and the sibling validator. Network/clock/process boundaries permit isolated tests. POSIX support restriction is documented near the local workflow. No new runtime dependency or ADR-worthy product architecture decision is hidden in the scripts. |

## Narrow exception review

All seven current rules expire at explicit UTC `2027-01-31Z`, within 90 days of this review date. Each uses an exact Maven PURL or fully anchored repository-layout artifact path plus a literal CPE/CVE. No artifact exclusion or feature-unreachable waiver is introduced. Baseline-only version rules remain bounded rather than silently expanding to future versions.

The IAM rule identifies the project's own `dev.wlambertz.rallyon.iam:keycloak-core@0.1.0-SNAPSHOT`; its POM describes a framework-independent helper with Nimbus, not the Keycloak server. The Micrometer rules restrict the wrong server CPE to registry versions 1.17.0/1.17.2. Manufacturer documentation identifies the registry as using the Prometheus Java client. This supports removing the server identity while retaining client/transitive coverage. [Micrometer source](https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html).

Four exact Spring Security artifact/version rules target only CVE-2018-1258. The manufacturer's affected-version interval is Spring Framework 5.0.5.RELEASE; the reviewed 7.x Framework family is outside it. Other CVEs and artifacts remain covered. [Spring advisory](https://spring.io/security/cve-2018-1258/).

Independently inspected actual engine source rather than relying solely on mirrored tests: `SuppressionRule.process` checks file/PURL identity before removal; `identifierMatches` converts CPEs to URI form, and `cpe22UriPrefixMatches` appends a delimiter for rules ending in a colon. The current terminal-colon rules respect version-field boundaries, excluding adjacent-version counterexamples such as 1.17.20. [Dependency-Check 13.0.0 implementation](https://raw.githubusercontent.com/dependency-check/DependencyCheck/v13.0.0/core/src/main/java/org/owasp/dependencycheck/xml/suppression/SuppressionRule.java).

Actual rule use/unused-rule output, post-update findings and analyzable embedded-JavaScript outcomes still require the current real engine reports. This review does not substitute fixture results for those acceptance requirements.

## Sonar S8707 path-traversal claim

The coordinator requested independent assessment of a reported new-code S8707 finding at `atomic_write`, `run_scan` output creation and manifest reading. On this source snapshot, the remaining general claim that an LLM could supply faulty local CLI paths does **not** demonstrate a vulnerability under the actual architecture:

- The tool is a local developer CLI executed with the caller's authority. The same caller explicitly selects input/output directories and the `--docker` executable and can already run arbitrary repository/Maven commands. It is not a filesystem-isolating service or an interface granting an untrusted remote principal access to privileged files.
- The checked-in workflow supplies fixed repository and `RUNNER_TEMP` paths. No LLM invocation, AI-generated path ingestion or remotely supplied path parameter flows into these calls in this implementation.
- Task-owned absolute paths outside the checkout are intentional for cold/warm scans and CI. Restricting outputs to cwd would break those authorized uses without establishing a meaningful privilege boundary. Reading the caller-selected manifest is likewise intended CLI behavior.
- The earlier concrete accidental leaf-symlink overwrite hazard is distinct and is addressed by atomic temporary-file replacement; writable/immutable directory overlap is rejected before writes. The independently passing fixtures cover both protections.

Verdict on this specific reported general path claim: no actionable security defect demonstrated by the reviewed call sites. This assessment does not certify arbitrary hostile filesystem layouts, malicious CLI execution or a future server/AI integration. Such an integration would need a new trust-boundary review. No Sonar finding or quality-gate status was changed by this reviewer.

## Limitations and delivery status

Current real feed cold/warm executions were not complete inputs to this review and are not assumed successful. Historical API failures remain failures. Current actual reports, unused-rule diagnostics, GitHub scheduler/cache behavior and artifact outcomes must be checked by the responsible implementation/QA reviewers before acceptance. Full IAM/service Maven verification, Go tests, Actionlint and lifecycle/OpenSpec checks were not rerun by this architecture reviewer; existing maker records are explicitly dated and G6 must provide current independent evidence.

No blocking source finding requires maker changes from this lens. Changed source or materially different G5/G6 evidence requires a bounded review refresh. This report grants neither formal gate advancement nor human acceptance/merge authorization; issue #187 PR 2 remains outstanding.
