# Runtime dependency scanning and SBOM

The [security workflow](../../.github/workflows/security-scan.yaml) prepares resolved runtime JARs once, then starts independent vulnerability-scan and CycloneDX jobs. Both require successful preparation; SBOM generation does not wait for scan success. Preparation includes the installed shared IAM modules. Existing scan and SBOM check names are retained.

The official Dependency-Check 13.0.0 image is pinned by digest. Embedded JavaScript remains eligible for analysis; CVSS scores of 7 or higher block delivery. Network, feed, container and missing-report failures also fail the scan. See the [complete baseline dispositions](dependency-triage.md), including primary-source evidence and uncertainty, before changing exceptions.

## Current validation blocker

The actual keyless cold scan currently fails because of an upstream 13.0.0 regression. [Evidence and proposed correction](../../openspec/changes/stabilize-ci-and-security/evidence/scanner-keyless-blocker.md) record the failure and pending design decision. There is no successful post-upgrade findings report or warm-cache validation yet. Do not treat passing local simulations or the separate SBOM as a successful security gate.

## Local reproduction

Install IAM and prepare a new task-owned input directory with Maven repository layout:

```sh
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml dependency:copy-dependencies -DincludeScope=runtime -Dmdep.useRepositoryLayout=true -DoutputDirectory="$PWD/security-local/dependencies"
python3 tools/scripts/security_scan.py --dependencies security-local/dependencies --revision "$(git rev-parse HEAD)" --write-inventory security-local/inventory.json
python3 tools/scripts/security_scan.py --dependencies security-local/dependencies --manifest security-local/inventory.json --revision "$(git rev-parse HEAD)" --data security-local/data --reports security-local/reports --suppressions .github/dependency-check-suppressions.xml
```

Use an empty data directory for a cold scan and the same compatible directory for a later warm scan. Keep logs, execution metadata, input inventories and available HTML/JSON reports. A failed initial population does not establish a usable warm cache. Do not commit local cache or generated reports.

## Trust, recovery and exceptions

PRs can restore default-branch cache data but cannot publish shared data or receive `NVD_API_KEY`. Only push/schedule runs on the repository's actual default branch can use that optional secret and save successful cache data. The scanner version/digest partitions cache identity. Data updates stay enabled; there is no unconditional offline fallback. If the cache is incompatible or corrupt, remove that task-owned cache and reproduce a cold initialization; preserve the failed logs first.

A trusted optional key is mounted in a temporary properties file, removed after execution and checked against retained report/cache bytes. Scanner arguments and fake-process tests enforce these boundaries; failures must remain visible.

Validate exceptions before scanning:

```sh
python3 tools/scripts/validate_dependency_suppressions.py .github/dependency-check-suppressions.xml
python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v
```

Rules need exact package identity plus a specific wrong CPE/CVE, source-linked justification and an expiry no more than 90 days away. Missing, expired or broad rules fail validation. Renewal requires checking the current artifact and manufacturer evidence again; do not merely extend dates. Real vulnerable components receive upgrades, not unused-feature waivers.

The SBOM job generates `bom.json` even when scanning is red and checks all prepared runtime PURLs. GitHub retains available SBOM/comparison/input artifacts and scanner diagnostics. Failed preparation blocks both downstream jobs; a red scan keeps the overall workflow red.
