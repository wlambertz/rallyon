## Purpose

Keep the backend's build, development and delivery environments on a validated Java baseline while preserving service behavior and making consumer requirements explicit.

## ADDED Requirements

### Requirement: Validated Java 25 build baseline

The Tournament Management service and both shared IAM modules SHALL compile for Java 25, with the service Kotlin JVM target aligned to 25, only after their effective build tooling and verification pass on JDK 25.

#### Scenario: Compatible toolchain and tests

- **WHEN** a contributor builds and verifies the IAM reactor and service with JDK 25
- **THEN** the effective compiler and Kotlin targets are 25 and the IAM install and service clean verify succeed, including mapper generation and existing persistence, security, serialization and module checks

#### Scenario: Upgrade blocked by incompatibility

- **WHEN** a required compatibility or validation check cannot pass
- **THEN** Java 21 compilation remains the baseline and the exact failure, affected version, smallest remedy and deferral criteria are recorded

### Requirement: Consistent development and delivery environments

The supported dev container, Java editor runtime, backend build and security/SBOM workflows SHALL select JDK 25 when the baseline is upgraded, and current developer instructions SHALL describe that baseline and its tradeoffs.

#### Scenario: Contributor starts the dev container

- **WHEN** a contributor builds and starts the dev container after the upgrade
- **THEN** Java reports version 25 and the editor runtime and setup instructions agree

#### Scenario: Security scan uses its own runtime

- **WHEN** the security workflow invokes a scanner with an independently configured Java runtime
- **THEN** that scanner's runtime configuration remains intact while the workflow's build JDK is 25

### Requirement: Runtime and consumer compatibility

The published IAM build instructions SHALL disclose that Java 25 bytecode requires Java 25 or newer, and the service image SHALL start on its existing Java 25 runtime without changing public application contracts.

#### Scenario: Service starts with existing migrations

- **WHEN** the built service image starts against isolated PostgreSQL with shipped migrations
- **THEN** health and a representative authenticated API request succeed

#### Scenario: Invalid token is presented

- **WHEN** a request presents an invalid issuer, audience or required identity claim
- **THEN** the existing strict JWT validation and authorization behavior remains unchanged

#### Scenario: Consumer selects its runtime

- **WHEN** a downstream consumer adopts the Java 25 IAM artifacts
- **THEN** current documentation states the Java 25 minimum and known consumers have been reviewed

### Requirement: Existing migrations run on startup

The Tournament Management service SHALL execute the shipped Flyway migrations on startup against an empty supported PostgreSQL database and SHALL fail startup if a required migration fails. Existing migrations SHALL remain unchanged.

#### Scenario: Empty database

- **WHEN** the service starts against an empty supported PostgreSQL database
- **THEN** Flyway applies the shipped migrations before the service handles persistent operations and an authenticated draft can be created

#### Scenario: Migration fails

- **WHEN** a required migration fails
- **THEN** service startup fails rather than reporting readiness against an unmigrated schema
