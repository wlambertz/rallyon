# G6 independent QA traceability

**Verdict: PASS for runtime and scenario QA, with one unresolved pre-existing full-format check failure and rollout limits.** Checker: `g6_qa`; source head `0f2bb8619cb7f0ec8c5527ba5d5d25383191928a`. This is not UAT or a merge decision.

| Finding | Evidence and disposition |
| --- | --- |
| Existing formatting failure | `npm run format:check` exits 1 solely on `openspec/changes/issue-203/.lifecycle.json`. Independently extracted that file from `origin/main` and reproduced its Prettier failure. `npm run format:check:changed` passes. The full check remains **failed**, not waived or reported green. |
| Data rollout | Isolated PostgreSQL 17/18 volumes do not establish safety for every real existing volume or Flyway history. Inspect and back up actual databases before rollout. |
| Test environment | Dev-container evidence uses amd64 emulation; editor configuration was inspected, not an interactive extension session. OSS Index was unavailable without credentials in the local scanner run. |

## Requirement and scenario traceability

| Requirement / scenario | Evidence |
| --- | --- |
| Java 25 toolchain, tests and blocked upgrade | Independently ran JDK 25 IAM clean install and service clean verify against PostgreSQL: 12 IAM tests and 91 service tests, 0 failures; one existing documentation test skipped. Maven targets and representative class major 69 are recorded in [preflight](java25-preflight.md). The historical blocker/retain-21 behavior and later explicit candidate exception are recorded there and in [repair evidence](flyway-repair.md). |
| Dev container and scanner runtime | [Dev-container follow-up](g5-validation-followup.md) records full `devcontainer up`, post-create/attach, Java 25 and `JavaSE-25`; `.github/workflows/security-scan.yaml` selects 25 and `tools/scripts/security_scan.py` removes inherited `JAVA_HOME` before invoking the pinned scanner container. [Current CI](current-ci.md) includes passing scan and SBOM. |
| Image, authenticated/invalid requests and IAM consumer minimum | [Bootstrap validation](bootstrap-validation.md) records PostgreSQL-backed real Keycloak token and draft HTTP 201; [repair evidence](flyway-repair.md) records health 200 and unauthenticated 401. Independently reran the eight `KeycloakJwtDecoderTest` cases, including bad issuer, audience and missing/non-numeric user ID. README states the IAM minimum Java 25; service consumes starter, starter consumes core. Existing tests cover serialization and security, without proving all deployed API behavior. |
| Existing migrations and failure | Independently ran both `FlywayPostgresIntegrationTest` cases on fresh PostgreSQL 17: existing migration history/table creation and deliberate invalid migration startup failure passed. No shipped Flyway SQL changed in the PR. |
| Local database restart and incompatible version | [Bootstrap validation](bootstrap-validation.md) records isolated PostgreSQL 18 restart preserving a marker and PostgreSQL 18 refusal of PostgreSQL 17 data, followed by successful PostgreSQL 17 marker read. Actual customer volumes were not tested. |
| Local identity provisioning | [Bootstrap validation](bootstrap-validation.md) records two isolated Keycloak provisioning runs, an old root-owned mode-600 file remaining untouched, successful token/draft and removal of new temporary files. This is maker evidence, not an independently rerun end-to-end test. |

## Independently executed checks

- Docker Linux arm64 Temurin 25.0.5, isolated source copy of head `0f2bb86` and separate PostgreSQL 17 container: `./service/tournamentmgmt/mvnw -B -o -f 3rd_party/iam/pom.xml clean install` and `./service/tournamentmgmt/mvnw -B -o -f service/tournamentmgmt/pom.xml clean verify` passed; the test-only PostgreSQL environment variables were set and both Flyway tests ran.
- macOS host: `GOCACHE=/tmp/rallyon-g6-go-cache go test ./...` passed outside the sandbox for local test sockets; `bash -n admin/keycloak/provision_keycloak.sh`, `docker compose -f infrastructure/local/docker-compose.yml config --quiet`, `git diff --check`, changed-file formatting and strict OpenSpec validation passed.
- Independently queried PR #209: Java CI, security preparation, vulnerability scan, SBOM, CodeQL, lifecycle validation, commit lint and SonarCloud all reported success for the stated source head. The full local formatting failure above remains distinct from those checks.
