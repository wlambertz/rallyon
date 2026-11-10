# Design

## Context and constraints

The service and IAM starter pin Spring Boot 4.1.2 and Kotlin 2.4.21. Before the change, service and IAM targets and the dev container used Java 21 while the service runtime used Temurin 25. PR #209 now contains a Java 25 candidate; local and hosted checks for head `bb3690b` are recorded in the change evidence. Issue #187 is closed; use its CI history to distinguish existing failures.

## Decisions and tradeoffs

Use Java 25 consistently rather than introducing separate development and compilation targets. This reduces baseline drift but raises the minimum runtime for consumers of both IAM artifacts. Inspect all repository consumers and document any external consumers that cannot be established. Preserve independent scanner `JAVA_HOME` handling if present in the scanner execution path. Add the Spring Boot 4.1.2-managed Flyway integration only if a controlled test confirms it causes existing migrations to run; add a clean-PostgreSQL regression test for startup and schema creation. Flyway will now execute existing migrations at startup; check existing databases for compatibility and fail startup on migration errors. No broad dependency upgrade is planned.

The local PostgreSQL 18 image defaults to a data directory outside the existing Compose volume. Set `PGDATA=/var/lib/postgresql/data` while retaining that mount: existing PostgreSQL 18 data stays visible and incompatible PostgreSQL 17 data must fail startup without reinitialization. This is not an automatic PostgreSQL major-version upgrade. Transfer the Keycloak profile through `docker compose exec` as the container user with restrictive permissions rather than copying a host-owned mode-600 file. Validate on isolated volumes and a separate Keycloak instance; never modify existing user data.

## Bounded contexts, invariants and dependency direction

Tournament Management and Identity & Access are affected as build consumers. No aggregate, event, port, adapter, business invariant, or published REST contract changes. Existing issuer, audience, roles and identity-claim validation remain strict; existing CLI behavior remains unchanged. No domain dependency direction changes.

## Compatibility and escalation assessment

**Human review required:** `3rd_party/iam` changes and the intentional Java 25 minimum for artifact consumers. A new Spring Boot Flyway integration dependency, explicit PostgreSQL data path, local Keycloak provisioning change and altered database startup are proposed; no new or modified Flyway migration, module or public REST/JWT/CLI behavior change is proposed. Escalate any newly required dependency or incompatible behavior before implementation. The service depends on the IAM starter, which depends on IAM core; discover any additional consumers before finalizing documentation.

## Validation, rollout and recovery

Use a JDK 25 environment and capture `java -version` and Maven wrapper version. Inspect effective POMs for compiler, Kotlin, Surefire, annotation processor and instrumentation versions; check representative Java and Kotlin class-file major versions (69). Run IAM install, then service clean verify, including kapt/MapStruct and the existing security, persistence, serialization and Modulith tests. Run formatting, build the dev container and service image, and perform isolated PostgreSQL, health and authenticated API smoke checks using the existing local auth flow. Verify relevant CI/SBOM and security outcomes, preserving scanner-specific Java settings; report unrelated prior failures separately.

The host initially had no JDK 25 and its sandbox blocked the Docker socket; validation subsequently used JDK 25 in isolated containers outside the sandbox. The maintainer explicitly approved applying a Java 25 candidate before hosted checks. If required checks fail or cannot be completed, document the exact evidence and defer promotion; do not claim successful upgrade based solely on configuration changes.

## ADR assessment

No separate ADR is proposed: the existing stack and architecture stay intact, and the baseline decision, consumer impact and recovery tradeoffs will be documented near developer setup. Reconsider if validation reveals an architectural or dependency decision beyond this scope.
