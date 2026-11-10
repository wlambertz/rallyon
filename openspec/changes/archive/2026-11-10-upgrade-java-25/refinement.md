# Refinement

## Problem and evidence

Issue #199 identified a split Java baseline: the service and shared IAM compiled for Java 21, while the service image ran Java 25; the dev container and backend workflows also selected Java 21. The service Kotlin target follows its `java.version` property. PR #209 now contains a Java 25 candidate; its successful local and hosted checks and remaining bootstrap limitations are recorded in the change evidence.

## Primary persona and owning app/context

The maintainer and contributors need a consistent, reproducible development and CI baseline. The work spans the Tournament Management service, shared Identity & Access libraries, and repository delivery tooling. A more consistent build indirectly helps maintain reliable software for organizers; it does not add an organizer-facing feature.

## Scope and exclusions

- Repair local PostgreSQL 18 startup while retaining the existing named volume path; make Keycloak provisioning transfer its profile file as the container user. Validate fresh and existing-volume safety and the real token flow.
- Repair the observed Flyway startup failure by evaluating and, if effective, adding the Spring Boot Flyway integration and a PostgreSQL regression test; keep shipped migrations unchanged.
- Assess the effective Maven, Kotlin, compiler, test, instrumentation and annotation-processing toolchain on JDK 25 before changing the baseline.
- If validation passes, align service Java and Kotlin targets, both IAM modules, relevant CI workflows, dev container and current setup documentation with Java 25. Document the minimum Java 25 runtime for consumers of shared IAM artifacts and the baseline tradeoffs.
- Coordinate validation with issue #187 and preserve the Dependency-Check action's independent Java configuration.
- Exclude application features, frontend work, new or rewritten database migrations, preview features, broad dependency upgrades, Gradle migration, and REST, JWT or CLI behavior changes. Preserve historical plans and archived evidence.

This is the **full** lifecycle track: the change spans multiple components and security-sensitive shared IAM artifacts, so the reduced path is inappropriate.

## Success criteria

- Effective Maven configuration shows compatible tools and consistent Java/Kotlin 25 targets; IAM install and service clean verify pass on JDK 25, including generated mapping, persistence, security, serialization and Modulith checks.
- Relevant backend CI and security/SBOM jobs pass or pre-existing failures are distinguished from regressions. The dev container builds and reports JDK 25, and editor settings and current instructions agree.
- The service image builds and starts with isolated PostgreSQL and shipped migrations; health and a representative authenticated request succeed. Existing REST, JWT, schema and CLI contracts remain compatible.
- If a blocker prevents validation, record the reproducible failure, affected version, smallest remedy and deferral criteria; retain Java 21 compilation until resolved.

## Assumptions and agreed budget

- Issue #199's compatibility assessment is provisional, not evidence of a successful JDK 25 build. Availability of JDK 25, Docker, CI and authenticated smoke-test prerequisites must be checked during validation.
- Raising shared IAM bytecode to 25 intentionally raises the minimum consumer runtime; identify downstream consumers and explicitly assess that compatibility impact before implementation.
- No work budget has been agreed.

## G1 decision

Pending explicit human approval; record the quote and source in lifecycle evidence.
