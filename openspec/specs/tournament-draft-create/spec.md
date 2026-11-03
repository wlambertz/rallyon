# tournament-draft-create Specification

## Purpose

Describe creation in the tournament setup configuration module, preserving the existing REST contract. Historical acceptance conditions and corrected assumptions are recorded in [the migration inventory](../../../docs/legacy-spec-migration.md).

## Requirements

### Requirement: Create an initial draft

The configuration service SHALL create a DRAFT from organizer ID, name, visibility and acting user ID; dates SHALL NOT be required at creation.

#### Scenario: Create through the published endpoint

- **WHEN** an authenticated caller with a resolvable principal sends a valid name and visibility to `POST /api/tournamentmgmt/config/drafts?organizerId=5`
- **THEN** the response is 201 Created with the created tournament in DRAFT status

#### Scenario: Initialize audit information

- **WHEN** the create use case saves a new draft
- **THEN** its initial version is zero, its creation and modification timestamps use the injected clock, and its audit user IDs use the acting user ID

#### Scenario: Reject anonymous creation

- **WHEN** a caller sends a draft creation request without authentication
- **THEN** the security filter returns 401 Unauthorized

### Requirement: Validate creation basics

The domain SHALL reject a null, blank or longer-than-200-character name and a null visibility before saving.

#### Scenario: Reject invalid creation before persistence

- **WHEN** the create use case receives an invalid name or null visibility
- **THEN** it throws IllegalArgumentException for the invalid name or NullPointerException for null visibility, and does not save a tournament
