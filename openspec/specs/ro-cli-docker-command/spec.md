# ro-cli-docker-command Specification

## Purpose

Describe existing Docker image and Compose wrappers. The docker group itself shows help; operations require a subcommand.

## Requirements

### Requirement: Execute Docker workflows

The CLI SHALL preserve `ro docker build` and `ro docker compose up|down|logs`, delegating to the Docker CLI using configured paths and existing flags.

#### Scenario: Build an image

- **WHEN** ro docker build has a configured image repository and Docker succeeds
- **THEN** the CLI builds using configured context, Dockerfile and computed tags, and pushes each tag only when --push is supplied

#### Scenario: Operate the local stack

- **WHEN** a Compose up, down or logs subcommand is invoked with available Docker and valid configuration
- **THEN** the CLI delegates respectively to compose up -d, down or logs --follow with file, env-file and profile arguments, and succeeds when Docker succeeds

#### Scenario: Report unavailable Docker

- **WHEN** an operation attempts to launch Docker and its executable is unavailable
- **THEN** the CLI returns docker command failed with the exit code and underlying process-start error

#### Scenario: Preview Docker operation

- **WHEN** --dry-run is supplied to an operation
- **THEN** the CLI logs the planned Docker invocation without launching Docker
