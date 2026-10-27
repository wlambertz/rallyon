# Contributing to RallyOn

Thank you for contributing! This guide explains how to set up, develop, test, and submit changes.

## Repository structure

The repository is organized by bounded context (see `AGENTS.md`):

- `application/` — Frontend placeholders (`organizer`, `audience`); no frontend stack is currently in place
- `service/tournamentmgmt/` — Core service logic (Spring Boot Modulith)
- `3rd_party/iam/` — Shared Keycloak security libraries used by backend services
- `admin/keycloak/` — Local Keycloak provisioning script and operator notes
- `infrastructure/local/` — Local Docker Compose for Keycloak, Postgres, and the tournament management service
- `tools/cli/ro/` — Go developer CLI
- `docs/wiki/` — Wiki content in-repo (architectural notes)

## Development commands

All commands run from the repository root unless noted.

```bash
# Install the shared IAM modules (required before building the service)
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install

# Build, test, and package the tournament management service
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify

# Iterative test run
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml test

# Run the service locally (exposes REST endpoints and Modulith actuators)
./service/tournamentmgmt/mvnw -f service/tournamentmgmt/pom.xml spring-boot:run

# Go CLI unit tests
(cd tools/cli/ro && go test ./...)

# Check formatting of changed files
npm run format:check:changed
```

## Coding style

Follow `.editorconfig`:

- UTF-8, LF line endings, final newline
- Max line length: 120
- Two-space indentation for Kotlin, Java, YAML, JSON
- Classes: descriptive CamelCase; members: lowerCamelCase; resource paths: hyphenated
- Group packages by bounded context
- Annotate public APIs with Spring Modulith stereotypes

## Testing guidelines

- Use JUnit 5 with `spring-boot-starter-test` and `spring-modulith-starter-test`
- Place tests in `src/test/kotlin` or `src/test/java`, mirroring the main package structure
- Integration suites: `*Tests` (reserve `*IT` for heavier scenarios)
- Cover new endpoints and configuration flows with Modulith slice and controller MVC tests
- Use `src/test/resources` for test property overrides
- Ensure the service verify command above passes before opening a PR

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
