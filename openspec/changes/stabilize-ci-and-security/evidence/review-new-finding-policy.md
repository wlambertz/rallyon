# Independent review of the observed-finding suppression revision

Reviewer: Codex native independent agent `/root/ci187_review_correctness`, separate from the triage and implementation makers. Reviewed on 2026-11-03 against HEAD `9bbeb3af41814b03d381c7efa3f8c4d6165d54a8` plus the three policy/document files identified below. This reviewer changed only this report.

**PASS for the proposed ten-rule policy and disposition coverage.** No blocking policy finding was identified. This approves the narrow source correction for another real execution; it does not claim a successful revised-policy scan or complete any formal lifecycle gate.

## Exact inputs

| File | SHA-256 |
| --- | --- |
| `.github/dependency-check-suppressions.xml` | `050de994ea7c7f888a11e4cf02c40d62714088790c5f8e4f012457e6280eecf0` |
| `docs/security/dependency-dispositions.json` | `9b35b0b2bb676f8b888550432e695b29350511a44c9184ee4ed1e9c85a1a5ffa` |
| `docs/security/dependency-triage.md` | `8da65d3d6ad61b6c6035752de2b51bc54a24cdc5bfe98975febbbd42cd95039c` |
| Raw cold `dependency-check-report.json` at `/private/tmp/rallyon-187-nist-reviewed-cold-reports/` | `d92a35119ee00aa2ba1f3d34acffcc45b0ed31d77936d4e92dcfa3b8befc5d35` |

Read the approved design/specification, prior independent correctness reports, full current XML, policy/document diff, raw cold report and durable summary. The raw report contains eight reported instances across six entries, with four distinct CVEs. The separate post-upgrade observation inventory matches that exact `(filename, CVE)` multiset. The original baseline inventory still matches all 82 instances, 13 entries and 64 distinct IDs; no original finding was dropped or relabeled as a new observation.

## Applicability and scope

The new own-starter rule selects exactly `pkg:maven/dev.wlambertz.rallyon.iam/keycloak-spring-starter@0.1.0-SNAPSHOT` and only the wrong Keycloak `0.1.0:snapshot` CPE. Its canonical PURL and CPE are present in the raw report. Independently inspected the local starter POM, JWT auto-configuration source and prepared JAR: the JAR contains four RallyOn adapter classes under `dev/wlambertz/...`, with Spring/JWT dependencies rather than Keycloak server resource implementations. Its SHA-256 matches the observed report/disposition (`71192e426a6af46d1705ec77c1ba85906ee068fc687097e4a62e38ae2e613c7f`); both retained local source hashes also match. This is a product identity correction, not a claim that a genuine affected server feature is unused.

The original [Red Hat CNA records](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2014/3xxx/CVE-2014-3651.json), [social-callback record](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2014/3xxx/CVE-2014-3709.json) and [password-reset record](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2017/12xxx/CVE-2017-12161.json) describe Keycloak server QR-code generation, social callback and password reset vulnerabilities. The stored primary records were independently read and their canonical JSON hashes matched the durable source inventory. A fresh web request succeeded for the 2017 record; both 2014 source requests returned cache misses in this review environment. That retrieval limitation does not turn the unrelated RallyOn adapter into the affected product.

For the five new Spring rules, independently reopened the [manufacturer CVE-2018-1258 advisory](https://spring.io/security/cve-2018-1258/). It identifies Framework 5.0.5.RELEASE as affected and 5.0.6.RELEASE as fixed. The raw prepared scan contains `spring-core-7.0.10.jar`. Each added rule selects only its exact `/inputs/org/springframework/security/<artifact>/7.1.2/<artifact>-7.1.2.jar` path and CVE-2018-1258:

- `spring-security-config`
- `spring-security-crypto`
- `spring-security-oauth2-core`
- `spring-security-oauth2-jose`
- `spring-security-web`

Every path matches the actual reported artifact path. These rules do not suppress another CVE or embedded JavaScript. The same precise version-applicability rationale already covers core/resource-server; no broader Spring artifact/CPE exclusion was introduced.

The three retired baseline-version rules are absent from active XML and preserved in policy history. The raw actual report independently shows that the four retained rules already suppressed only the expected own-core, registry and two historical Spring matches. Ten rules remain after adding six and removing three. All retain `2027-01-31Z`, within 90 days of review; no automatic renewal, threshold change, analyzer disablement or artifact exclusion was added.

## Independent checks

- `python3 tools/scripts/validate_dependency_suppressions.py .github/dependency-check-suppressions.xml`: independently validated all ten active rules.
- Independently replayed all current JSON counterexamples against actual XML: **74 behavior assertions**, plus **20 checks tying selector/target values to the XML**, all passed. Exact identities match; changed versions, Maven groups, artifact aliases, path suffixes and unrelated findings do not. CPE positive and longer-version/update negative cases respect the previously verified DC13 prefix-plus-terminal-colon matcher.
- Compared each new active selector with raw reported identity/path, rather than accepting positive fixtures alone. All six new artifact identities match the intended observations.
- Independently compared both original-baseline and new-observation multisets; exact coverage passed.
- `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py'`: all 27 simulations passed.

## Remaining execution evidence

The cold/warm executions using the previous seven-rule policy remain failed vulnerability-gate evidence, with exit 15; successful data import and report generation do not make that policy green. This report does not rerun Docker or independently certify warm-cache timing. The revised policy must now run against current source and retain actual HTML/JSON, execution metadata, exact inputs, matched/unused-rule output and any remaining genuine findings. Independent G5/G6/G7 ordering, actual GitHub sibling SBOM observation, human UAT and merge approval remain required.
