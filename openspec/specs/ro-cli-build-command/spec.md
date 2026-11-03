# ro-cli-build-command Specification

## Purpose

Describe the existing Maven build wrapper. It builds the configured service root; this command does not select arbitrary Go or Java modules.

## Requirements

### Requirement: Run configured Maven build

`ro build` SHALL execute the configured Maven executable in paths.serviceRoot with build.defaultGoals and a two-hour timeout, preserving its command and flags.

#### Scenario: Build successfully

- **WHEN** the configured Maven build exits successfully
- **THEN** ro build succeeds and streams process output

#### Scenario: Report a build error

- **WHEN** Maven cannot start or the build exits unsuccessfully
- **THEN** ro build returns a build failed error including the exit code and underlying error

#### Scenario: Select build goals

- **WHEN** --fast is supplied
- **THEN** goals are clean package -DskipTests; if --ci is also supplied, clean verify -B takes precedence

#### Scenario: Preview build

- **WHEN** --dry-run is supplied
- **THEN** the command logs its planned execution and does not launch Maven
