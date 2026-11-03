# Independent G6 verification: NVD-primary PR 1

Checker: native Codex agent `/root/ci187_g6_nvd`, fresh context, no implementation edits. Date: 2026-11-03. Implementation checked: `93e491b1b08b708c4e00ddecbacbc09a19a164c8`. Final source/index was explicitly frozen by the coordinator before this finalized report. **PASS for G6 QA**, with the limitations stated below; human UAT and merge remain separate.

## Independently executed checks

Logs are task-owned under `/private/tmp/rallyon-187-g6-nvd/`; their SHA-256 and lengths are retained in `log-identities.json`. Initial sandbox-only Maven/cache and Go loopback failures were retried with approved access; these were environment failures, not successful test runs.

| Command/check | Actual result |
| --- | --- |
| `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install` | PASS; both modules installed, 12 tests |
| `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml test` | PASS; 12 tests, zero failures/errors/skips |
| `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` | PASS; 89 discovered, 88 pass, one existing opt-in Modulith documentation skip |
| `GOCACHE=/private/tmp/rallyon-187-g6-go-cache go test ./...` in `tools/cli/ro` | PASS, full CLI suite |
| `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v` | PASS, 27 tests |
| Lifecycle helper / Claude hook / schema readiness unittest discovery | PASS, 32 / 10 / 1 tests |
| Cached OpenSpec 1.14.2 `validate --all --strict` / `schema validate rallyon-feature` | PASS, 12 items / valid schema |
| `/tmp/actionlint -shellcheck= .github/workflows/security-scan.yaml` | PASS, Actionlint 1.7.12; ShellCheck unavailable and not run |
| `npm run format:check` / `git diff --check` | PASS before final evidence freeze; final recheck recorded below |
| Relative Markdown links in security docs and active proposal/design/spec files | PASS, 11 local targets exist; remote/anchor correctness not claimed |
| `python3 tools/scripts/validate_dependency_suppressions.py .github/dependency-check-suppressions.xml` | PASS, 10 rules |

Read repository/IAM/service instructions, approved proposal/design/tasks/spec and QA reference. No live Keycloak token, release, tag, registry push or deployment was performed. The Maven security checks use generated keys and isolated loopback JWKS. H2/MockMvc tests do not establish deployed PostgreSQL/Flyway/E2E behavior.

## Scenario traceability

- **SCA-001:** both POM parents are 4.1.2 and Kotlin properties 2.4.21. Independently inspected the newly built Boot JAR: stdlib/reflect 2.4.21, Boot 4.1.2, Framework 7.0.10, JDBC 42.7.14 and Swagger UI 5.34.1 are packaged. IAM valid-token/signature/issuer/audience/expiry and strict principal tests, service API/security/architecture tests and full Go suite pass.
- **SCA-002/003:** workflow and runner retain the official pinned 13.0.0 digest and CVSS 7; no disabling flag, ignored error, threshold change or artifact exclusion was introduced. Raw runtime input/report path plus SHA-256 comparison independently found all 145 prepared JARs in the 162-entry revised report, including nine embedded JavaScript entries. Fake failures remain simulations; actual engine execution is described separately below.
- **SCA-004:** independently compared original baseline and dispositions as `(filename, CVE)` multisets: all 82 instances / 13 entries / 64 IDs retained. Independently replayed all 74 identity/target/boundary counterexamples and bound each of the 10 cases to the actual XML selector and target. Narrow manufacturer applicability and current-policy review are documented in `review-new-finding-policy.md`; this QA check inspected that independent source review rather than treating fixtures as vendor evidence.
- **SCA-005:** all 27 fixtures pass, including fork/same-repository PR/default-push/schedule trust, fixed feed/API selection, exact metadata boundaries, bounded retries, stale/missing/duplicate/naive/future metadata rejection, credential cleanup/redaction, stale report rejection and post-scan failure propagation. Workflow restores compatible data but saves/receives optional key only for trusted default-branch push/schedule. Own actual keyless warm execution passes below; actual cold/CI outcome is recorded below.
- **SCA-006:** workflow job graph independently shows sibling scan and SBOM jobs after successful preparation, exact `github.sha` checkout/artifact identity, read-only permissions, always-run available artifact uploads and no ignored scan error. Downloaded actual run 41623095316 SBOM and prepared inventory: canonical Maven PURLs match all 145 inputs with none missing. CycloneDX `?type=jar` qualifiers were normalized only for coordinate comparison. Run head is `93e491b…`; PR artifact revision `7d5db89fc8a98746e3df4afc47bb2f3deededb1a` is the checked PR merge SHA, not an unrelated source claim. Preparation-failure behavior is simulated; previous real red-scan/SBOM-success evidence remains historical.
- **SCA-007:** independent commands/results and limitations are retained here. G5/G6 helper recording order is the coordinator’s responsibility; this report grants neither G8 acceptance nor merge approval. Issue #187 remains outstanding for PR 2.

## Independently executed real warm scan

Executed `SSL_CERT_FILE=/etc/ssl/cert.pem python3 tools/scripts/security_scan.py --dependencies /private/tmp/rallyon-187-nvd-runtime-inputs --revision 93e491b --data /private/tmp/rallyon-187-nist-reviewed-cold-data --reports /private/tmp/ci187-g6-own-warm-reports --suppressions .github/dependency-check-suppressions.xml`.

- Actual process exit **0**, from `2026-11-03T10:59:49.412831Z` to `10:59:59.883645Z` (10.47 seconds), with HTML/JSON produced.
- Official image `owasp/dependency-check:13.0.0@sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a`.
- Selected `nist-json2-feed`; official metadata timestamp `2026-11-03T06:00:12Z`, raw metadata SHA-256 `01eaab97a8bd8b9f9d6d26b9a67281e1ad2a22f8afdd3c27e75e9eea7b6399c2`.
- Own raw report SHA-256 `e9955e953a23957382c758426a6e9aa4addcded0fe429acab23de4d92076a9e7`: 162 dependency entries, nine JavaScript entries, zero active vulnerability instances, 15 suppressed instances. This is an actual result at the configured gate, not a claim of comprehensive vulnerability absence.
- Independently hashed the maker’s revised warm raw report: `051cba5b8b6d7887fbf2e3aa1783afb849c54487b2b9fb3b79d791f3e877add3`, matching durable summary, with the same counts and all 145 prepared paths/hashes present.

Earlier `9bbeb3a` cold/warm executions returned **15** with eight later-reviewed wrong product/version instances; those remain failed evidence. Cold initialized official data successfully and completed NVD/RetireJS analysis; revised-policy warm reuse does not rewrite that cold scan as success.

OSS Index reports unavailable because upstream 13.0.0 requires its separate credentials. No analyzer disabling flag was introduced; required NVD and RetireJS analysis completed. This satisfies the explicit NVD/embedded-JavaScript coverage intent, while general “all analyzers active” wording cannot truthfully imply OSS Index coverage. No OSS Index coverage or additional mandatory secret is claimed. The limitation must remain visible to human acceptance.

Sonar S8707 still requires external disposition; this QA report does not alter its red quality-gate state. Independent architecture review explains the intentional local CLI path authority, absence of an untrusted LLM boundary and fixed leaf-symlink/overlap hazards. Passing local tests do not dismiss external findings automatically.

## Actual current-source GitHub cold execution

Independently read [run 41623095316](https://github.com/wlambertz/rallyon/actions/runs/41623095316), then downloaded its scanner and SBOM artifacts. Run source head is `93e491b1b08b708c4e00ddecbacbc09a19a164c8`; `github.sha` and artifact inventories identify PR merge revision `7d5db89fc8a98746e3df4afc47bb2f3deededb1a`. Prepare, scanner and SBOM jobs all **completed successfully**. Full run log explicitly says the compatible cache was not found. This is actual current-policy cold initialization, not a fake-process result or assumed cache reuse.

Execution metadata reports exit **0**, official pinned image, `nist-json2-feed`, official timestamp/hash matching the own warm run, and `2026-11-03T10:55:10.617198Z` to `11:05:11.681068Z` (601.06 seconds). HTML/JSON, input inventory and scanner log are present. Independently hashed raw JSON report: `b09a201f5440e59a232d350c809ffe9d427084f9a83387d43048601cbd2cbc90`. Its 162 entries contain all 145 prepared JAR path/hash pairs and nine embedded JavaScript entries; zero active vulnerability instances and 15 suppressed instances. Scanner log contains 26 NVD feed download starts, completed NVD/RetireJS analysis and no unused-rule warning. OSS Index limitation remains exactly as disclosed above.

Actual SBOM comparison independently matches all 145 canonical Maven coordinates; the scanner and SBOM inventories identify the same merge revision. No scan/SBOM inventory source mixing was observed. Trusted cache save is not exercised by this PR run; trust-event matrix and inspected conditional remain its validation evidence.

## Final frozen snapshot and limits

Independently recomputed G5/G6 helper fingerprint: `280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`. Independently computed staged tree before this excluded report: `aef8284521a4fe032e4dcf6dfea6bc0597f48ea5`. All nine checked implementation/spec hashes remain unchanged from runtime verification; final source changes since `93e491b` are documentation/evidence and task completion status. All 12 implementation tasks are now checked; current suppression-match status and CI summary agree with actual evidence.

Final frozen-source `npm run format:check`, staged/unstaged whitespace checks and local link check pass. The expanded link check inspected 16 relative targets across security docs, active planning/specification and evidence Markdown, with none missing. Independently checked all three normalized scanner logs against preserved raw logs: only trailing whitespace/terminal blank lines differ, and both original/normalized SHA-256 records match `nist-log-normalization.json`. No log messages were removed.

No unresolved blocking implementation or test failure was found within this QA scope. OSS Index coverage is unavailable under upstream credential requirements; ShellCheck was unavailable; the existing optional documentation test was skipped. Deployed E2E, live Keycloak, production PostgreSQL/Flyway, trusted default-branch cache saving and real-key execution were not exercised. Trust/secret paths have inspected workflow and isolated negative fixtures; no actual secret or deployment was required. Sonar's separate red status remains visible and needs its normal external disposition. These limits must accompany G8 preparation rather than being converted into broader green claims.

The finalized report is excluded from the helper source fingerprint by the lifecycle implementation. The coordinator must record G5 before G6 and obtain current G7 reviews before human G8; this checker did not mutate gate records, Sonar findings, issue status or PR merge state.

### Checked source identities

| File | SHA-256 |
| --- | --- |
| `.github/workflows/security-scan.yaml` | `03e7effaa6c44052cf9b98c58f84f2417f08be14cecdec2b62403b1bafcab6ea` |
| `.github/dependency-check-suppressions.xml` | `050de994ea7c7f888a11e4cf02c40d62714088790c5f8e4f012457e6280eecf0` |
| `tools/scripts/security_scan.py` | `01b168ca40e093b85f293cbbebd6217d787f34ed0e8709e057f98f158d912cbc` |
| `tools/scripts/validate_dependency_suppressions.py` | `6e31c555376403cef413c4c90a1bdd4ccacdcd6b95cc8159df5200653c48ff48` |
| `tools/scripts/tests/test_security_pipeline.py` | `317dffb8a8909d52d700114a8e707647a588bf23cc89e4f34edfe883373bd2f1` |
| `service/tournamentmgmt/pom.xml` | `82ae30ab1f5922e81a752f40159ef73336dc822952f339cbcd7449862ba59efd` |
| `3rd_party/iam/keycloak-spring-starter/pom.xml` | `b774e442ecb9b3fb030f51b3eb33339c55c57e750f9568357214d40e919f9af9` |
| `openspec/changes/stabilize-ci-and-security/design.md` | `f7055b9ed052225f17736ec125321f2a86141c0367e4222159d02d112d980ebd` |
| `openspec/changes/stabilize-ci-and-security/specs/repository-dependency-security/spec.md` | `581cdacfba471a4fd448242cc83020474b6cd8585212d81836b7fc09bbade4e9` |

### Independent command-log identities

Task-owned logs are outside the repository; retained hashes make their exact local outcomes identifiable.

| Log                     | SHA-256                                                            | Bytes  |
| ----------------------- | ------------------------------------------------------------------ | ------ |
| `iam-install-retry.log` | `15414d3385e9b045059e624c5238848297ed8cce6f0e13f8f9b0cf9ea3db296d` | 6603   |
| `iam-test.log`          | `3e55689c206f6e8ccef1af9d535a65c97d523a365bc6a603807305d65ebdfc80` | 5036   |
| `service-verify.log`    | `24e62329997f154b3850e8f7f0be5324247f0f95cbb12d717117d9d2ecd6da50` | 723452 |
| `go-final.log`          | `411a44b4739a7e52cb4454e11d48e894bf01ef8920f91a89985370ae2066efbc` | 641    |
| `security.log`          | `a6651292d6bc27f6de64d15c165f6c99553d79c4298f74ea13a6d985af5c3301` | 5103   |
| `lifecycle.log`         | `14eac6d2d4909d478ee9241974b289fd4614cd58a386c5ef501990e562e6da4a` | 4498   |
| `hook.log`              | `9f9ea8c71d4573c061508c37f699f02e2f5ae1af7b38005877431a6a50263440` | 1552   |
| `readiness.log`         | `30d404228b7d31df77c8ddada052ced933a0c111d202ea571fc8d01dc3a92aca` | 265    |
| `openspec-all.log`      | `6a26e8d9739b92c2743e5aa138d47f3abf14faf8c1ad0542745c6f76e9ffe69d` | 438    |
| `openspec-schema.log`   | `f1a189c162b05b84fed0c2e24c0cf569106e9a9b4bccac40ecb2c750159d1772` | 93     |
| `actionlint.log`        | `e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855` | 0      |
| `format.log`            | `048ee577cc134e46aab04e9c8088c8dffa03ba10b1e04be3c8370f0c9d232ea4` | 528    |
| `own-warm.log`          | `f53f9daa699e2455324b9046b8f9ce7cd525703bebc3285811568b4fb38abe7e` | 2582   |
