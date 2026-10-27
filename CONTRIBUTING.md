# Contributing to RallyOn / BadTourn

Thank you for contributing! This guide explains how to set up, develop, test, and submit changes.

## Repository structure
The repository is organized by bounded context (see `AGENTS.md`):

- `application/` — App shell (organizer staff flows, audience read-only screens)
- `service/tournamentmgmt/` — Core service logic (Spring Boot Modulith)
- `3rd_party/` — Shared adapters (authentication, event bus, search, IAM)
- `admin/` — Operations scripts and infrastructure templates
- `infrastructure/` — Local infrastructure templates
- `wiki/` — GitHub wiki submodule (architectural notes)

## Development commands
All commands run from the repository root unless noted.

```bash
# Build, test, and package the tournament management service
./service/tournamentmgmt/mvnw clean verify

# Iterative test run
./service/tournamentmgmt/mvnw test

# Run the service locally (exposes REST endpoints and Modulith actuators)
./service/tournamentmgmt/mvnw spring-boot:run
```

## Coding style
Follow `.editorconfig`:
- UTF-8, LF line endings, final newline
- Max line length: 120
- Two-space indentation for Java, Gradle, YAML, JSON
- Classes: descriptive CamelCase; members: lowerCamelCase; resource paths: hyphenated
- Group packages by bounded context
- Use Lombok sparingly
- Annotate public APIs with Spring Modulith stereotypes

## Testing guidelines
- Use JUnit 5 with `spring-boot-starter-test` and `spring-modulith-starter-test`
- Place tests in `src/test/java`, mirroring the main package structure
- Integration suites: `*Tests` (reserve `*IT` for heavier scenarios)
- Cover new endpoints and configuration flows with Modulith slice and controller MVC tests
- Use `src/test/resources` for test property overrides
- Ensure all tests pass with `./mvnw test` before opening a PR

## Commit & pull request guidelines
- Commits use Conventional Commit prefixes: `feat`, `fix`, `chore` (scope optional)
- Start commit messages in imperative mood, keep under 72 characters
- Pull requests should:
  - Summarize changes
  - Link tracking issues
  - Outline test evidence
  - Attach UI/API artifacts (screenshots, curl samples) for behavior changes
  - Note wiki updates or migration steps for release managers

Use the pull request template at `.github/pull_request_template.md`.

## Documentation
Keep architectural notes and ADRs in `docs/adr/`. Update the wiki submodule when module responsibilities change:

```bash
git submodule update --remote --merge
```

## Code of conduct
Be respectful and inclusive. Follow blameless postmortem principles for incidents.
