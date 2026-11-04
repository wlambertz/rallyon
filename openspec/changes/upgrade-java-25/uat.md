# UAT

## Persona and success measures

The maintainer verifies a consistent Java 25 development baseline and a functioning local tournament service with secure authentication and persistent draft creation.

## Current revision, environment and setup

PR #209 implementation head `0f2bb86`; local checks used Temurin 25.0.5 and Docker on macOS arm64, with the dev container run under amd64 emulation. Use a dedicated empty PostgreSQL volume and isolated Keycloak test instance; never point these scenarios at existing data. Local setup and credentials are described in `admin/keycloak/README.md` and `infrastructure/local/docker-compose.yml`.

## Scenarios

| Scenario | Actions | Expected outcome | Actual observed outcome |
| --- | --- | --- | --- |
| Java baseline | Start the dev container and run `java -version`; inspect its configured Java runtime. | Java 25 and `JavaSE-25`. | Isolated `devcontainer up` completed; Java 25.0.5 and `JavaSE-25` were observed. Interactive editor extension not checked. |
| Local service | On a dedicated clean stack, run the documented Keycloak provisioning twice, obtain a token and create a draft with `organizerId=42`. | Provisioning succeeds repeatedly and the authenticated draft is created. | In isolated containers: two provisioning runs passed, token HTTP 200 and draft HTTP 201. |
| Existing database protection | In an isolated test volume containing PostgreSQL 17 data, attempt startup with PostgreSQL 18, then reopen with PostgreSQL 17. | PostgreSQL 18 refuses incompatible files without reinitialization; original data remains readable. | Startup failed with an incompatible-version error; the original marker was read successfully with PostgreSQL 17. |
| Security failure | Call the protected draft endpoint without a token. | HTTP 401. | Isolated service returned HTTP 401. |

For a witnessed rerun, use the isolated setup described in [bootstrap evidence](evidence/bootstrap-validation.md); the temporary stack used alternative ports and was removed after testing. Remove only the dedicated test containers and volumes after use.

### Reproduce the local service scenario

Use a disposable Docker daemon or a dedicated machine with no existing RallyOn containers, networks or volumes. With JDK 25 selected, build the IAM modules and service first:

```sh
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
docker compose -f infrastructure/local/docker-compose.yml up -d --build
export RALLYON_CLIENT_SECRET='temporary-test-secret'
export RALLYON_DEV_PASSWORD='temporary-test-password'
bash admin/keycloak/provision_keycloak.sh
bash admin/keycloak/provision_keycloak.sh
```

Use the password-grant request in `admin/keycloak/README.md` with those temporary values, then call `POST /api/tournamentmgmt/config/drafts?organizerId=42` with bearer authentication and JSON `{"name":"UAT draft","visibility":"PRIVATE"}`; expect HTTP 201. Repeat the request without a token and expect HTTP 401. Confirm `/actuator/health` returns `UP`. Remove the temporary stack and its volumes only on the dedicated disposable Docker daemon: `docker compose -f infrastructure/local/docker-compose.yml down --volumes`.

The major-version protection scenario requires separate disposable PostgreSQL 17 and 18 test volumes; never attempt it against an existing volume. The exact observed test and its limitation are recorded in [bootstrap validation](evidence/bootstrap-validation.md).

## Limitations and failures

The actual results above were observed by the implementing agent; human execution or witnessing and acceptance are still required. Existing real databases and interactive editor behavior were not tested. The full root format check fails on an unrelated file already failing on `origin/main`; changed-file formatting and PR CI pass.

## G8 human decision

Pending explicit acceptance of executed or witnessed results.

## Separate merge decision

Pending explicit approval of the final reviewed revision.
