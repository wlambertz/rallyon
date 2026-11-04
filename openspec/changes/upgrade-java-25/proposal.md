# Proposal

## Why

The maintainer and contributors currently build the Tournament Management service and shared Identity & Access libraries for Java 21 while the service image uses Java 25. A validated common baseline reduces environment drift and provides a documented JVM-upgrade learning exercise; the indirect beneficiary is the Organizer relying on the service.

## What Changes

Repair missing Flyway startup integration and cover migration execution on a clean PostgreSQL database. Validate the actual pinned build and test toolchain on JDK 25, then align the service, shared IAM, relevant workflows, dev container and current setup instructions if the acceptance checks pass. Explicitly document the raised IAM consumer runtime minimum. If validation is blocked, retain Java 21 compilation and document a reproducible deferral. Coordinate CI evidence with #187, which is now closed.

Out of scope: application features, frontend, migration changes, preview features, broad dependency upgrades, Gradle migration and behavioral changes to REST, JWT or CLI contracts.

## Capabilities

### New Capabilities

- `java-build-baseline`: validated Java 25 build, development, CI and runtime compatibility baseline and its blocked-upgrade behavior.

### Modified Capabilities

None. Existing authentication and CLI specifications retain their behavior; no current specification defines the JVM build baseline.

## Impact and backward compatibility

The service and both shared IAM modules, development container, four CI workflows and setup documentation are affected. IAM artifacts built for Java 25 require consumers to run Java 25 or newer: this is an intentional runtime compatibility change requiring review. REST shapes, JWT validation and claims, role names, existing migration contents, and `ro` commands and flags remain unchanged. Adding the Spring Boot Flyway integration changes database startup behavior and introduces a dependency. Existing shipped migrations remain unchanged; no domain aggregates or API contracts change.
