# ro-cli-run-command Specification

## Purpose

Describe local tournament management execution through Maven. The run group itself shows help; execution belongs to its service subcommand.

## Requirements

### Requirement: Run the configured tournament service

The CLI SHALL preserve `ro run service tournamentmgmt` and execute spring-boot:run with the configured Maven executable in paths.serviceRoot, with interactive stdin and no timeout.

#### Scenario: Start the service process

- **WHEN** the service subcommand is invoked with valid configuration and available dependencies
- **THEN** the CLI starts Maven interactively, forwards process output and waits for completion or interruption

#### Scenario: Apply runtime options

- **WHEN** the subcommand uses its default options or explicit --port and --env
- **THEN** positive port values set SERVER_PORT and nonempty profile values set SPRING_PROFILES_ACTIVE; defaults are 8080 and dev

#### Scenario: Report a runtime failure

- **WHEN** Maven cannot start or exits unsuccessfully, including a dependency failure reported by Maven
- **THEN** the CLI returns run failed with the exit code and underlying error; dependency details come from process output

#### Scenario: Preview runtime

- **WHEN** --dry-run is supplied
- **THEN** the CLI logs planned execution without starting the process
