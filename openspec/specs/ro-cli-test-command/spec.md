# ro-cli-test-command Specification

## Purpose

Describe the existing Maven test wrapper for the configured service root. This command does not automatically run the Go CLI suite.

## Requirements

### Requirement: Run Maven tests

`ro test` SHALL execute the configured Maven executable with test in paths.serviceRoot, stream output, and use a one-hour timeout, preserving its command surface.

#### Scenario: Run a successful suite

- **WHEN** Maven test exits successfully
- **THEN** ro test succeeds and exposes the test process output

#### Scenario: Report test failure

- **WHEN** Maven cannot start or test exits unsuccessfully
- **THEN** ro test returns a tests failed error including the exit code and underlying error and the CLI exits nonzero

#### Scenario: Preview tests

- **WHEN** --dry-run is supplied
- **THEN** the planned execution is logged without launching Maven
