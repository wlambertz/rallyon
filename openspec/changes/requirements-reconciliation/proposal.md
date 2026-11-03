# Proposal: evidence-based group standings candidate

## Why

Issue #183 asks which national tournament requirements merit adoption. Its group standings hierarchy differs from the DBV provisions checked in `inventory.md`. Existing `TieBreakRules` exposes flags but no ordered standings calculation. A reviewable candidate is needed before choosing a competition or promising implementation.

## What Changes

Prepare an illustrative domain specification for explicitly selected, versioned group-standings rules, including a DBV pool example and an unresolved-rule case. This is a candidate extracted from N6-01 and N6-03, not a decision that any event in RallyOn is governed by that rule. The requirements-document assessment, proposed dispositions and source comparisons remain in `analysis.md`, `inventory.md` and `dispositions.md`.

The Organizer is the primary persona. Tournament Management configures competitions; proposed calculation ownership between it and the placeholder Scoring context remains a decision for a future feature. Scope is planning and analysis only. No runtime implementation, new service, UI, migration or federation integration is authorized by this proposal.

## Capabilities

### New Capabilities

- `group-standings`: candidate observable behavior for identifying an applicable rule set and deriving a group ranking from results under a selected rule set.

### Modified Capabilities

- None. Existing tournament draft specifications describe configuration, not group standings.

## Impact and backward compatibility

This proposal changes no REST endpoint, JWT claim, CLI behavior, database schema or shared IAM code. A later implementation requires its own explicit scope and architecture decisions. The DBV source is evidence for a particular provision, not a universal default or a claim that unverified parts of the reference are correct.
