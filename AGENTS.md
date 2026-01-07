# Repository Guidelines

## Project Structure & Module Organization

- The repository is organized by the RallyOn domain.
- Application shell is in `application/`:
  - `organizer/`: staff flows
  - `audience/`: read-only screens
- Core service logic is in `service/tournamentmgmt/`:
  - Spring Boot Modulith service
  - Contains API controllers, configuration domain objects, and integration boundaries
- Shared adapters (e.g., authentication, event bus, search) are in `3rd_party/`
- Operations scripts and infrastructure templates are in `admin/`
- Generated artifacts and local build outputs are in `build/`
- The GitHub wiki is vendored as the `wiki/` submodule (keep architectural notes there)

## Build, Test, and Development Commands

- On Windows, run all commands from WSL (e.g., `wsl.exe` shell) so paths and tooling match CI. On Unix-like systems, use the default shell.
- Use Maven wrappers in `service/tournamentmgmt/`
  - Full build, test, and package: `./mvnw clean verify`
  - Iterative test: `./mvnw test`
  - Run service: `./mvnw spring-boot:run` (exposes REST endpoints and Modulith actuators)
- From repo root, `./gradlew tasks` is available for legacy modules (optional)
- Update the wiki: `git submodule update --remote --merge`
- Organizer shell (Angular 20) lives in `application/organizer/`
  - Install/update deps: `npm run organizer:install`
  - Local dev server: `npm run organizer:dev`
  - Lint + unit tests: `npm run organizer:lint` and `npm run organizer:test`
  - Headless CI suite: `npm run organizer:test:ci` (ChromeHeadless)
  - Playwright smoke: `npm run organizer:test:e2e` (requires `npx playwright install --with-deps chromium`)
  - Equivalent shortcuts via CLI: `ro app install|start|lint|test|test-ci|test-e2e organizer`

## Coding Style & Naming Conventions

- Follow `.editorconfig`:
  - UTF-8, LF endings, final newline
  - Max line length: 120
  - Two-space indentation for Java, Kotlin, Gradle, YAML, JSON
- Naming:
  - Classes: Descriptive CamelCase (e.g., `ConfigurationServiceImpl`)
  - Members: lowerCamelCase
  - Resource paths: hyphenated
- Group packages by bounded context (e.g., `setup.configuration.api`)
- Use Lombok sparingly
- Annotate public APIs with Spring Modulith stereotypes for module slicing

## Testing Guidelines

- Use JUnit 5 with `spring-boot-starter-test` and `spring-modulith-starter-test`
- Place tests in `src/test/java`, mirroring main package structure
- Integration suites: `*Tests` (reserve `*IT` for heavier scenarios)
- Cover new endpoints and configuration flows with Modulith slice and controller MVC tests
- Use `src/test/resources` for test property overrides
- Ensure all tests pass with `./mvnw test` before PR

## Commit & Pull Request Guidelines

- Commits use Conventional Commit prefixes: `feat`, `chore`, `fix` (scope optional)
- Start commit messages in imperative, keep under 72 characters if possible
- Pull requests should:
  - Summarize changes
  - Link tracking issues
  - Outline test evidence
  - Attach UI/API artifacts (screenshots, curl samples) for behavior changes
  - Note wiki updates or migration steps for release managers
