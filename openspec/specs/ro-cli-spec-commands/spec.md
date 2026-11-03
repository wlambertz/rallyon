# ro-cli-spec-commands Specification

## Purpose

Preserve legacy YAML ticket helpers during migration to OpenSpec 1.14.2. Current project specifications live in openspec/specs; these helpers remain compatibility tools and do not invoke the OpenSpec CLI.

## Requirements

### Requirement: Retain both legacy command routes

The CLI SHALL preserve `ro create|validate|view` and support the documented `ro spec create|validate|view` routes through shared handlers, each taking exactly one positional argument.

#### Scenario: Create a legacy ticket

- **WHEN** either create route is called with a name and an existing writable specs.directory
- **THEN** the CLI writes <name>.yaml using the configured basic or auth template (unknown templates fall back to basic) and reports the path and template

#### Scenario: Validate a legacy ticket

- **WHEN** either validate route is called with a YAML-readable file containing the required keys and nonempty checked fields and arrays
- **THEN** the CLI reports Spec validation PASSED

#### Scenario: Reject malformed or structurally invalid ticket

- **WHEN** either validate route receives malformed YAML or a file failing its structural checks
- **THEN** the CLI returns an error and reports structural failures when parsing succeeded

#### Scenario: Display a legacy file

- **WHEN** either view route receives a readable file
- **THEN** the CLI prints its existing OpenSpec Specification heading, separator and raw file content without format conversion

#### Scenario: Retain file and argument errors

- **WHEN** either route receives unreadable input, an unwritable output destination or the wrong argument count
- **THEN** the command returns an error rather than claiming success

### Requirement: Expose compatibility routes in help and reference

Both legacy command forms SHALL remain visible in CLI help and the generated CLI reference, including their deprecation guidance.

#### Scenario: Discover both command routes

- **WHEN** a caller inspects root and spec-group help or the generated CLI reference
- **THEN** create, validate and view are discoverable under both routes, with guidance to OpenSpec 1.14.2

### Requirement: Deprecate legacy helpers without changing payloads

Each legacy leaf invocation SHALL emit exactly one deprecation notice to stderr directing users to OpenSpec 1.14.2 (openspec.dev) and the shared openspec skill, while preserving existing successful stdout and file formats except for removal of the automatic Schema line.

#### Scenario: Keep warning out of stdout

- **WHEN** a create, validate or view operation runs through either route, including an argument, flag or operation failure
- **THEN** one common deprecation warning appears on stderr

#### Scenario: Remove automatic schema URL

- **WHEN** create runs without an explicit specs.schemaURL
- **THEN** no Schema line is printed and no schema URL is defaulted

#### Scenario: Retain explicit schema configuration

- **WHEN** create runs with an explicit nonempty specs.schemaURL
- **THEN** the configured Schema line is printed after the existing creation and template lines
