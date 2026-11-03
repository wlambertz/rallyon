# Dependency finding triage (issue #187, PR 1)

Reviewed 2026-11-03 against the recorded main baseline at `a5834c703a86774cae04a04757890a516ae1ec74`. This is implementation evidence. An upgraded cold scan completed with exit15 and eight new wrong-match instances; the revised active suppression policy still awaits independent review and a successful actual rescan. This document does not establish a green security gate.

The [machine-readable dispositions](dependency-dispositions.json) preserve every baseline `(file, finding ID)` instance, original severity/CVSS, scanner package/CPE identity, actual Maven/embedded-JavaScript identity, primary evidence, affected/fixed versions and action. The [baseline](../../openspec/changes/stabilize-ci-and-security/evidence/baseline-findings.json) contains 141 dependencies, 13 finding entries, 82 instances and 64 distinct IDs. Exact multiset comparison passes with no dropped or extra instances.

There are 75 upgraded instances and 7 proven wrong matches. “Updated” means that the resolved target exceeds the manufacturer’s published fix; actual scanner clearance remains pending. A genuine finding is retained even when its described vulnerable feature may be unused. Original scores are retained rather than replaced by a vendor’s different rating.

| Baseline entry                                     | Resolved target  | Instances | Disposition        |
| -------------------------------------------------- | ---------------- | --------: | ------------------ |
| `jackson-databind-2.21.4.jar`                      | `2.21.7`         |         1 | 1 updated          |
| `keycloak-core-0.1.0-SNAPSHOT.jar`                 | `0.1.0-SNAPSHOT` |         3 | 3 provenwrongmatch |
| `kotlin-stdlib-2.3.21.jar`                         | `2.4.21`         |         1 | 1 updated          |
| `log4j-api-2.25.4.jar`                             | `2.25.5`         |         1 | 1 updated          |
| `micrometer-registry-prometheus-1.17.0.jar`        | `1.17.2`         |         2 | 2 provenwrongmatch |
| `postgresql-42.7.11.jar`                           | `42.7.14`        |         1 | 1 updated          |
| `spring-core-7.0.8.jar`                            | `7.0.10`         |        17 | 17 updated         |
| `spring-data-jpa-4.1.0.jar`                        | `4.1.2`          |         1 | 1 updated          |
| `spring-security-core-7.1.0.jar`                   | `7.1.2`          |         1 | 1 provenwrongmatch |
| `spring-security-oauth2-resource-server-7.1.0.jar` | `7.1.2`          |         1 | 1 provenwrongmatch |
| `swagger-ui-5.32.2.jar: swagger-ui-bundle.js`      | `5.34.1`         |        17 | 17 updated         |
| `swagger-ui-5.32.2.jar: swagger-ui-es-bundle.js`   | `5.34.1`         |        17 | 17 updated         |
| `tomcat-embed-core-11.0.22.jar`                    | `11.0.27`        |        19 | 19 updated         |

The dependency maker verified the Boot 4.1.2 graph: Framework 7.0.10, Security 7.1.2, Tomcat 11.0.27, pgJDBC 42.7.14, Jackson Databind 2.21.7, Log4j API 2.25.5, Spring Data JPA 4.1.2 and Micrometer registry 1.17.2. Kotlin runtime/compiler/plugins are aligned at 2.4.21. Swagger UI 5.34.1 contains DOMPurify 3.4.17; its JAR and inspected bundle hashes are retained in the JSON. Independent graph verification remains part of lifecycle acceptance.

Primary-source verification used manufacturer security pages, project-owned GitHub advisories and original Red Hat CNA records preserved in the official CVE Program archive. HTTPS was verified using the system trust store after Python’s local certificate store failed. Source response hashes and review date are recorded in the JSON. Historical Red Hat pages for two 2014 CVEs returned 404; their original Red Hat CNA records supply the affected product/version evidence instead.

The Kotlin manufacturer record identifies build-cache metadata deserialization, resolved in 2.4.20. The baseline runtime stdlib match alone does not prove runtime exploitability. Both runtime and build tooling are nevertheless upgraded beyond that fix; there is no Kotlin suppression.

DOMPurify advisory structured affected-version metadata is inconsistent with the baseline or advisory narrative for CVE-2026-41240, CVE-2026-65899 and CVE-2026-65900. Exact baseline applicability stays uncertain in the JSON. These records are retained without suppression; target 3.4.17 exceeds each advisory’s published patched version. Both embedded bundle entries remain independently accounted for.

Only demonstrated product/version mistakes receive rules in [the suppression file](../../.github/dependency-check-suppressions.xml):

- RallyOn’s own `dev.wlambertz.rallyon.iam:keycloak-core:0.1.0-SNAPSHOT` contains framework-agnostic helpers and Nimbus, rather than Keycloak server code. Its source/POM and manufacturer CNA product descriptions prove the wrong-product match for the three historical Keycloak CVEs. The exact canonical PURL removes only the erroneous Keycloak CPE. A JAR hash would become stale on a SNAPSHOT rebuild; the PURL is stable. Nimbus and other transitive artifacts remain scanned. The same product-identity correction now applies separately to the exact own `keycloak-spring-starter:0.1.0-SNAPSHOT` PURL: its POM and auto-configuration source contain RallyOn Spring JWT adapters, rather than Keycloak server resource implementations.
- Micrometer’s registry uses the Prometheus Java client. It does not contain the Prometheus Go server remote-read endpoint or server UI addressed by the two reported CVEs. Exact Maven repository paths and exact versioned server CPEs bound the correction to current registry version 1.17.2.
- CVE-2018-1258 requires Framework 5.0.5.RELEASE; the manufacturer fixes it in 5.0.6.RELEASE. Framework 7.0.8 in the baseline and resolved 7.0.10 do not satisfy that requirement. Exact repository paths for current Security 7.1.2 core, resource-server, config, crypto, oauth2-core, oauth2-jose and web suppress only this historical CVE. Other CVEs against these artifacts stay in coverage.

All ten active rules expire at `2027-01-31Z`, within 90 days of review. No artifact is excluded, no CVSS is changed, and there is no unused-feature waiver. The three unused baseline-version rules for Micrometer1.17.0 and Security core/resource-server7.1.0 were removed from active XML after the real upgraded report; their exact prior XML and checks remain in JSON history. The raw82-instance baseline is unchanged. Do not auto-renew them.

Primary affected/fixed evidence for every distinct ID follows; repeated DOMPurify instances share evidence but retain separate dispositions in JSON.

| Finding | Relevant manufacturer affected range | Published fix | Primary source |
| --- | --- | --- | --- |
| CVE-2026-54515 | >= 2.8.0, < 2.18.9 >= 2.19.0, < 2.21.5 >= 2.22.0, < 2.22.1 | 2.18.9 2.21.5 2.22.1 maven tools.jackson.core:jackson-databind ( Maven ) >= 3.1.0, < 3.1.4 3.1.4 | [Evidence](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-5jmj-h7xm-6q6v) |
| CVE-2014-3709 | <1.0.3.Final | 1.0.3.Final | [Evidence](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2014/3xxx/CVE-2014-3709.json) |
| CVE-2017-12161 | <3.4.2.Final | 3.4.2.Final | [Evidence](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2017/12xxx/CVE-2017-12161.json) |
| CVE-2014-3651 | <1.0.3.Final | 1.0.3.Final | [Evidence](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2014/3xxx/CVE-2014-3651.json) |
| CVE-2026-53914 | Kotlin before2.4.20; build cache metadata deserialization | 2.4.20 | [Evidence](https://resources.jetbrains.com/storage/issues-fixed/data.json) |
| CVE-2026-49844 | 2.13.1–2.25.4;2.26.0 | 2.25.5 or2.26.1 | [Evidence](https://logging.apache.org/security.html#CVE-2026-49844) |
| CVE-2026-42154 | < 3.5.3 ; >= 3.6.0 and <3.11.3 | 3.5.3 and 3.11.3 | [Evidence](https://github.com/prometheus/prometheus/security/advisories/GHSA-8rm2-7qqf-34qm) |
| CVE-2019-3826 | Prometheus server <2.7.1 | 2.7.1 | [Evidence](https://raw.githubusercontent.com/CVEProject/cvelistV5/main/cves/2019/3xxx/CVE-2019-3826.json) |
| CVE-2026-54291 | >= 42.7.4, < 42.7.12 | 42.7.12 | [Evidence](https://github.com/pgjdbc/pgjdbc/security/advisories/GHSA-j92g-9f8w-j867) |
| CVE-2026-47884 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47884) |
| CVE-2026-47890 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47890) |
| CVE-2026-47891 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47891) |
| CVE-2026-47892 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47892) |
| CVE-2026-59313 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-59313) |
| CVE-2026-59283 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-59283) |
| CVE-2026-47885 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47885) |
| CVE-2026-47886 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47886) |
| CVE-2026-47888 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47888) |
| CVE-2026-47889 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47889) |
| CVE-2026-47893 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47893) |
| CVE-2026-59282 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-59282) |
| CVE-2026-47883 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47883) |
| CVE-2026-47887 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-47887) |
| CVE-2026-59281 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-59281) |
| CVE-2026-59280 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-59280) |
| CVE-2026-59314 | Framework 7.0.0–7.0.8 (relevant7.x line) | 7.0.9 | [Evidence](https://spring.io/security/cve-2026-59314) |
| CVE-2026-47834 | 4.1.0 (relevant4.1.x line) | 4.1.1 | [Evidence](https://spring.io/security/cve-2026-47834) |
| CVE-2018-1258 | Framework 5.0.5.RELEASE only | Framework 5.0.6.RELEASE | [Evidence](https://spring.io/security/cve-2018-1258) |
| CVE-2026-116803 | <= 3.4.16 | 3.4.17 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-mrv2-3pqr-j5jr) |
| CVE-2026-49978 | <3.4.6 | 3.4.7 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-rp9w-3fw7-7cwq) |
| CVE-2026-49458 | <= 3.4.5 | 3.4.6 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-hpcv-96wg-7vj8) |
| CVE-2026-49459 | <= 3.4.5 | 3.4.6 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-r47g-fvhr-h676) |
| CVE-2026-41240 | <= 3.2.6 | 3.4.0 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-h7mw-gpvr-xq4m) |
| CVE-2026-65902 | <= 3.4.5 | 3.4.7 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-76mc-f452-cxcm) |
| CVE-2026-65898 | <= 3.4.10 | 3.4.11 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-cmwh-pvxp-8882) |
| CVE-2026-65899 | 3.0.0 | 3.4.9 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-vxr8-fq34-vvx9) |
| CVE-2026-65900 | =3.0.0 <3.4.7 | 3.4.8 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-gvmj-g25r-r7wr) |
| CVE-2026-65901 | <= 3.4.6 | 3.4.7 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-x4vx-rjvf-j5p4) |
| CVE-2026-65903 | <= 3.3.3 | 3.4.0 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-39q2-94rc-95cp) |
| CVE-2026-66010 | <= 3.4.11 | 3.4.12 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-c2j3-45gr-mqc4) |
| CVE-2026-41238 | 3.0.1 through 3.3.3 | 3.0.0 and all 2.x versions, 3.4.0 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-v9jr-rg53-9pgp) |
| CVE-2026-41239 | < 3.3.3 | 3.4.0 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-crv5-9vww-q3g8) |
| CVE-2026-75838 | <= 3.4.12 | 3.4.13 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-55q2-fjhq-7xh7) |
| CVE-2026-109345 | <= 3.4.15 | 3.4.16 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-6688-9rhm-gjv2) |
| CVE-2026-116802 | <= 3.4.16 | 3.4.17 | [Evidence](https://github.com/cure53/DOMPurify/security/advisories/GHSA-c6gr-qvv4-5v3f) |
| CVE-2026-65637 | 11.0.20–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-65905 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-53434 | 11.0.0-M1–11.0.22 | 11.0.23 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-55276 | 11.0.0-M1–11.0.22 | 11.0.23 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-59083 | 11.0.0-M1–11.0.23 | 11.0.24 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-59084 | 11.0.0-M1–11.0.23 | 11.0.24 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-65182 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-68525 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-65183 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-66422 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-68569 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-65927 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-68763 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-53404 | 11.0.0-M1–11.0.22 | 11.0.23 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-73180 | 11.0.0-M1–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-55955 | 11.0.0-M1–11.0.22 | 11.0.23 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-55956 | 11.0.0-M1–11.0.22 | 11.0.23 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-50229 | 11.0.0-M1–11.0.22 | 11.0.23 | [Evidence](https://tomcat.apache.org/security-11.html) |
| CVE-2026-66299 | 11.0.0-M20–11.0.24 | 11.0.25 | [Evidence](https://tomcat.apache.org/security-11.html) |

Validation performed: exact baseline multiset comparison (82/82), entry/unique-ID counts (13/64), and 48 assertions over the initial seven-rule XML. Following the observed cold report, 74 assertions validate the revised ten-rule policy, including exact positive identities, changed versions, Maven group/artifact aliases, unrelated CVEs and CPE whole-part boundaries. All active selectors and counterexamples are retained in JSON. Each rule has a positive identity match; changed versions, paths, Maven groups/artifacts and unrelated CPEs/CVEs are counterexamples. The policy validator and pipeline security tests cover expiry, exact CPE syntax and rule structure. The artifact-selector assertions alone did not establish CPE matcher compatibility: independent review found that Dependency-Check 13 converts candidates to CPE 2.2 URIs before literal prefix matching. The product rules now use effective CPE 2.2 literals; original baseline CPE 2.3 identifiers remain unchanged in the disposition inventory. The checks model the documented matcher semantics; actual Dependency-Check report behavior still requires the real scan and independent review.

Reproduce the durable inventory comparison with:

```sh
python3 - <<'PY'
import collections, json
baseline = json.load(open('openspec/changes/stabilize-ci-and-security/evidence/baseline-findings.json'))
triage = json.load(open('docs/security/dependency-dispositions.json'))
expected = collections.Counter((e['file'], v['name']) for e in baseline['findings'] for v in e['vulnerabilities'])
actual = collections.Counter((e['file'], v['id']) for e in triage['entries'] for v in e['instances'])
assert expected == actual
assert len(actual) == 82
assert len({finding for _, finding in actual}) == 64
print('82/82 baseline instances mapped; 64 distinct IDs')
PY
python3 tools/scripts/validate_dependency_suppressions.py .github/dependency-check-suppressions.xml
python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v
```

Remaining acceptance blockers: real updated cold/warm scans, actual suppression match/unused-rule output, complete runtime verification and independent lifecycle checking. Any genuine unresolved CVSS >= 7 finding or scanner/feed error must keep the gate red. The resolved graph and manufacturer fixes support remediation, but do not substitute for these outcomes.

Effective mappings, verified against [`SuppressionRule.identifierMatches`](https://github.com/dependency-check/DependencyCheck/blob/v13.0.0/core/src/main/java/org/owasp/dependencycheck/xml/suppression/SuppressionRule.java), are recorded with the upstream source hash in JSON: Keycloak `cpe:/a:keycloak:keycloak:0.1.0:snapshot:` and current Prometheus `cpe:/a:prometheus:prometheus:1.17.2:`. The retired1.17.0 mapping is preserved in JSON history. Exact artifact selectors continue to bound these prefix matches to the reviewed versions. Dates and waiver scope are unchanged. An earlier attempted real cold scan failed on upstream no-key issue #8715. A subsequent reviewed cold execution completed with exit15 after440 seconds and reported the eight wrong-match instances below; the latest revised policy has not yet produced an actual successful scan.

A terminal colon requests a whole-part match through `cpePartMatchingSuffixFor`; six additional positive/negative assertions reproduce the upstream prefix/suffix behavior and reject longer version/update prefixes such as `1.17.20` for `1.17.2:`. These are matcher-model checks; the real scanner outcome remains pending.

The observed post-upgrade cold report is separate from the original82-instance baseline. Its SHA-256, raw package/CPE identities, exact files, manufacturer evidence and proposed dispositions are recorded under `post_upgrade_observations` in JSON. No original genuine-component finding remains reported in that execution, but the scanner gate remains red:

| Observed current artifact | Findings | Proposed exact correction |
| --- | --- | --- |
| `dev.wlambertz.rallyon.iam:keycloak-spring-starter:0.1.0-SNAPSHOT` | CVE-2014-3651, CVE-2014-3709, CVE-2017-12161 | Own exact PURL plus effective wrong Keycloak CPE; primary Red Hat CNA evidence and local manufacturer source hashes retained |
| `spring-security-config:7.1.2` | CVE-2018-1258 | Exact Maven repository filepath plus this CVE only |
| `spring-security-crypto:7.1.2` | CVE-2018-1258 | Exact Maven repository filepath plus this CVE only |
| `spring-security-oauth2-core:7.1.2` | CVE-2018-1258 | Exact Maven repository filepath plus this CVE only |
| `spring-security-oauth2-jose:7.1.2` | CVE-2018-1258 | Exact Maven repository filepath plus this CVE only |
| `spring-security-web:7.1.2` | CVE-2018-1258 | Exact Maven repository filepath plus this CVE only |

The unchanged manufacturer advisory requires Framework5.0.5 for the historical Spring CVE; the verified graph contains Framework7.0.10. These five additional artifact matches do not justify broader CVE or artifact exclusions. The current active policy has ten rules after adding six exact rules and retiring three unused old-version rules. Independent review and a new actual scan are pending; a warm execution of the earlier policy cannot establish acceptance of this revision.
