# Follow-up validation for G5

## Hosted CI

PR #209 head `bb3690b950decc77e5d6567d16d0852b14612b4b` completed Java CI, security preparation, Dependency-Check, SBOM, CodeQL, commit lint, lifecycle validation and SonarCloud successfully. [Java CI run](https://github.com/wlambertz/rallyon/actions/runs/41845201845) ran 91 service tests with one existing skip, including both PostgreSQL/Flyway cases. [Security/SBOM run](https://github.com/wlambertz/rallyon/actions/runs/41845202302) passed all three jobs. These results apply to the stated head; later source changes require fresh checks.

## Dev container

Dev Containers CLI 0.90.0 ran `devcontainer up` against an isolated clone of `bb3690b` whose workspace directory is named `rallyon`, with `DOCKER_DEFAULT_PLATFORM=linux/amd64` on an arm64 macOS host. Build, post-create and post-attach completed successfully; `devcontainer exec ... java -version` reported Temurin 25.0.5. The merged configuration reported `JavaSE-25` and `/opt/java/openjdk`. This verifies the configured editor runtime, not an interactive VS Code extension session. An initial temporary clone named differently failed because the configured workspace folder did not match the inferred mount; the successful run used the repository's expected directory name.

## Local Keycloak and PostgreSQL

An isolated copy of the repository's Compose stack and Keycloak provisioning script, with separate containers, ports and volumes, ran against a fresh PostgreSQL 18 database and Keycloak 26.6.4. The provisioned `dev.organizer` obtained a token through the documented password grant; an authenticated draft request returned HTTP 201 with `DRAFT` status. The service jar was built from the Java 25 candidate. No real credentials or shared user data were used.

Two temporary test-only changes were necessary, so this is not an unqualified pass of the existing local bootstrap:

1. The unmodified Compose PostgreSQL volume is mounted at `/var/lib/postgresql/data`; the fresh PostgreSQL 18.4 image refused to start, explaining that its data directory is incompatible with that mount. The isolated copy mounted `/var/lib/postgresql` instead. Existing volumes and upgrades need separate assessment; no repository infrastructure or existing data was changed.
2. The provisioning script's copied temporary user-profile file retained mode 600 and a host UID inaccessible to Keycloak's container user; `kcadm` reported `File not found: /tmp/rallyon-user-profile.json`. The isolated script set the temporary copy's mode to 644 before `docker cp`. The repository script was not changed.

The root `npm run format:check` remains red because of the unrelated pre-existing `openspec/changes/issue-203/.lifecycle.json`. The change's own files passed Prettier. These environment defects and the formatting failure remain visible for independent assessment; the clean CI result is not evidence that the unmodified local bootstrap succeeds.
