# Test strategy

This document describes the testing approach for the RallyOn / BadTourn repository,
following the skills catalog for the QA & testing phase of the SDLC.

## Test pyramid
Tests are organized in three layers, favoring fast, low-level tests:

1. **Unit tests** — JUnit 5, pure logic, mocked collaborators. Fast, run on every commit.
2. **Integration tests** — Spring Boot slice tests with H2, Testcontainers, `*Tests` suffix. Run on every PR (Gate 1).
3. **End-to-end / API tests** — controller MVC tests, OpenAPI contract checks. Run in Gate 2 / nightly.

## Naming & placement
- Unit tests: `src/test/java/.../ExampleTest.java`
- Integration suites: `*Tests` (reserve `*IT` for heavier scenarios)
- Mirror the main package structure under `src/test/java`
- Test property overrides in `src/test/resources/application.properties`

## CI gates (`.github/workflows/ci.yaml`)
- **Gate 1 — blocks merge**: unit + integration tests (`./mvnw test`), must run in under ~5 minutes on PRs.
- **Gate 2 — nightly / pre-release**: full regression suite incl. E2E and performance tests, does not block development.

## Test design principles
- Start small; keep tests independent
- Use descriptive test names
- Maintain test data separately
- Handle flaky tests: quarantine after 3 consecutive failures, weekly flake review
- Integrate with CI/CD; never automate everything (risk-based selection)
- Do not automate exploratory testing; automate repeatable findings from it

## Coverage & metrics
- Track execution, coverage, and maintenance metrics
- Flaky tests are removed from the pipeline until fixed with a real ticket
- DORA and DevOps metrics tracked via Jira / Git / Grafana dashboards

## Tools
- JUnit 5, AssertJ, Mockito, Spring Boot Test, spring-modulith-starter-test
- H2 (test DB), PostgreSQL (runtime)
- Playwright / Selenium for UI (when a frontend is added)
- REST Assured / springdoc-openapi for API contract tests
