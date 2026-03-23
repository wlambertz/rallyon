# Tournament Management Agent Instructions

These instructions apply to `service/tournamentmgmt/` and override the root file where they differ.

## Stack And Scope

- This module is a Spring Boot Modulith service built with Maven.
- Java version is 25.
- Keep package organization aligned with bounded contexts such as `setup.configuration`, `setup.rules`, and `setup.phases`.

## Commands

- Iterative test run: `./mvnw test`
- Full verification: `./mvnw clean verify`
- Local service run: `./mvnw spring-boot:run`
- Opt-in Modulith source generation:
  - `./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests`

## Implementation Rules

- Prefer established Spring Boot and Spring Modulith patterns already present in the service.
- Keep module boundaries explicit and avoid leaking implementation details across module packages.
- Favor focused controller, service, mapper, and persistence changes over broad package reshaping.
- Keep public REST paths and API semantics stable unless the task explicitly changes them.

## Testing And Verification

- For code changes, run the smallest relevant Maven test scope, and use full `./mvnw test` when the affected area spans multiple modules or web/security behavior.
- Add or update JUnit 5 tests for changed behavior.
- Prefer Modulith slice tests, MVC/controller tests, and focused unit tests over large integration changes when sufficient.
- If you change auth, OpenAPI, or Swagger-related behavior, verify the documented developer flow still makes sense.

## Modulith Docs Workflow

- Canonical PlantUML and AsciiDoc outputs live in `docs/modulith/generated/`.
- Do not regenerate Modulith docs during routine test runs; the generation test is intentionally opt-in.
- If you change module structure or committed architecture outputs, regenerate:
  - `./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests`
- If the wiki-rendered diagrams must stay in sync, also run:
  - `./docs/modulith/render-wiki-diagrams.sh`

## Documentation Expectations

- Update `docs/modulith/README.md` when the generation or render workflow changes.
- If diagrams or architecture pages in the wiki depend on your change, update the corresponding wiki content and remember that `wiki/` is a separate git repository.
