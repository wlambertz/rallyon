# Tasks

## 0. Restore database initialization

- [x] 0.1 For **Existing migrations run on startup**, validate the minimal Spring Boot Flyway integration in an isolated copy, add the confirmed dependency and a clean-PostgreSQL regression test, and verify startup, schema, authenticated draft creation and failure behavior. Preserve shipped migrations.

- [x] 0.2 For **Safe local database startup** and **Local identity provisioning**, retain the existing database volume mount while setting PostgreSQL 18 `PGDATA`; transfer the Keycloak profile as the container user with restrictive permissions. Verify clean and existing PostgreSQL 18 volumes, safe refusal of PostgreSQL 17 data, idempotent Keycloak provisioning and a real authenticated draft in isolated Compose; run `bash -n admin/keycloak/provision_keycloak.sh`, `docker compose -f infrastructure/local/docker-compose.yml config` and `npm run format:check`. Document the required manual major-version migration.

## 1. Validate and align the baseline

- [x] 1.1 For **Validated Java 25 build baseline**, capture `java -version`, `./service/tournamentmgmt/mvnw -version` and effective POMs; inspect compiler, Kotlin, Surefire, kapt/MapStruct and instrumentation compatibility. Record exact blockers and keep target 21 if validation cannot pass.
- [x] 1.2 For **Validated Java 25 build baseline**, align service Java/Kotlin and both IAM compiler targets only when compatible. Run `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install` and `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify` under JDK 25; inspect representative class files with `javap -verbose` for major version 69.
- [x] 1.3 For **Runtime and consumer compatibility**, inventory IAM consumers with `rg 'keycloak-(core|spring-starter)'` and document the Java 25 minimum and tradeoffs; verify existing security tests and documented public behavior remain intact.

## 2. Align environments and verify delivery

- [x] 2.1 For **Consistent development and delivery environments**, align the four named workflows, dev container, editor runtime and current setup documentation, retaining scanner-specific Java handling. Run `npm run format:check` and inspect the workflow and documentation diff.
- [x] 2.2 For **Consistent development and delivery environments**, build and start the dev container and observe `java -version` 25; record CI and security/SBOM job outcomes and compare failures with #187 history.
- [x] 2.3 For **Runtime and consumer compatibility**, run `docker build -t rallyon-tournamentmgmt:java25 service/tournamentmgmt`, start it against isolated PostgreSQL with shipped migrations, and record health plus authenticated API results using the existing local authentication flow.
- [x] 2.4 For all requirements, record command output, versions, class-file checks, CI results and any reproducible blocker or deferred validation; keep Java 21 compilation if required checks cannot pass.

## Workflow follow-up

- Record independent implementation/testing evidence and three review lenses.
- Obtain explicit human UAT acceptance and separate merge approval.
