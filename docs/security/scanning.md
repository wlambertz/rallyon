# Runtime dependency scanning and SBOM

The [security workflow](../../.github/workflows/security-scan.yaml) prepares resolved runtime JARs once, then starts independent vulnerability-scan and CycloneDX jobs. Both require successful preparation; SBOM generation does not wait for scan success. Preparation includes the installed shared IAM modules. Existing scan and SBOM check names are retained.

The official Dependency-Check 13.0.0 image is pinned by digest. Embedded JavaScript remains eligible for analysis; CVSS scores of 7 or higher block delivery. Network, feed, container and missing-report failures also fail the scan. See the [complete baseline dispositions](dependency-triage.md), including primary-source evidence and uncertainty, before changing exceptions.

## Data source selection and freshness

NVD remains the automated primary source. Without a trusted optional key, the runner selects the official NIST JSON 2 datafeed, avoiding the documented 13.0.0 no-key API regression. Trusted default-branch push/schedule runs with `NVD_API_KEY` use the existing API mode. Selection happens once; failure does not switch modes or disable updates.

Before each keyless scan, including restored-cache runs, retrieve official `nvdcve-2.0-modified.meta` with three attempts of at most 20 seconds each. Require exactly one timezone-aware `lastModifiedDate`, no older than 48 hours and no more than five minutes in the future; exact boundaries pass. Retrieval, parse or freshness failures keep the scan red. Execution metadata records mode, source timestamp and SHA-256 of the metadata bytes; no credential is recorded. The runner uses POSIX alarms to bound the entire metadata attempt, including DNS and response reads; supported execution is Linux CI or a POSIX local host. Successful metadata validation is not a successful vulnerability scan.

BSI/CERT-Bund's [Warn- und Informationsdienst](https://wid.cert-bund.de/) can supplement manual triage, alongside manufacturer advisories. It is not a second scan engine or replacement CI gate. There is no BSI adapter, mandatory service or new secret.

The [initial cold failure](../../openspec/changes/stabilize-ci-and-security/evidence/scanner-keyless-blocker.md) is historical evidence, retained unchanged. New real feed cold/warm outcomes must be recorded separately; passing simulations or the separate SBOM do not establish security clearance.

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

PRs can restore default-branch cache data but cannot publish shared data or receive `NVD_API_KEY`. Only push/schedule runs on the repository's actual default branch can use that optional secret and save successful cache data. The scanner version/digest partitions cache identity. Data updates stay enabled; there is no unconditional offline fallback. If the cache is incompatible or corrupt, remove that task-owned cache and reproduce a cold initialization; preserve the failed logs first. If metadata is stale or inaccessible, preserve diagnostics and retry after the official source recovers; do not replace its timestamp, extend the freshness window or force offline success.

A trusted optional key is mounted in a temporary properties file, removed after execution and checked against retained report/cache bytes. Scanner arguments and fake-process tests enforce these boundaries; failures must remain visible.

Validate exceptions before scanning:

```sh
python3 tools/scripts/validate_dependency_suppressions.py .github/dependency-check-suppressions.xml
python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v
```

Rules need exact package identity plus a specific wrong CPE/CVE, source-linked justification and an expiry no more than 90 days away. Missing, expired or broad rules fail validation. Renewal requires checking the current artifact and manufacturer evidence again; do not merely extend dates. Real vulnerable components receive upgrades, not unused-feature waivers.

The SBOM job generates `bom.json` even when scanning is red and checks all prepared runtime PURLs. GitHub retains available SBOM/comparison/input artifacts and scanner diagnostics. Failed preparation blocks both downstream jobs; a red scan keeps the overall workflow red.

## Filesystem boundaries

Local arguments intentionally select task-owned absolute directories, including temporary paths; the script is not an API that grants hostile callers filesystem access. CI supplies fixed repository/runner paths. Script-owned outputs are written atomically so an existing leaf symlink cannot overwrite its unrelated target. Writable data/reports must not overlap immutable dependency inputs, suppression rules or the prepared manifest, or each other. These checks prevent accidental corruption; they do not authorize arbitrary untrusted CLI execution.
