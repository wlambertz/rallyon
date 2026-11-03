## Purpose

Propose a rule-aware group standings capability for an organizer, subject to explicit selection of the applicable competition rule set and a later architecture decision.

## ADDED Requirements

### Requirement: Identify the selected group ranking rule (STANDINGS-001)

The standings capability SHALL identify the rule set and version selected for the competition when presenting a derived group ranking. It MUST NOT silently substitute a different rule set when the selected one is unavailable.

#### Scenario: Display an identified rule

- **WHEN** a group ranking is calculated under a selected and supported rule version
- **THEN** the result identifies that rule version alongside the ranking

#### Scenario: Missing or unsupported rule

- **WHEN** no applicable rule version has been selected or the selected version is unsupported
- **THEN** the capability reports that it cannot establish the ranking under the selected rules
- **AND** it does not present a substituted ordering as authoritative

### Requirement: Apply the selected ordered criteria (STANDINGS-002)

The standings capability SHALL apply the ordered criteria of the selected and supported competition rule to eligible recorded match results. It SHALL report an unresolved ordering when the supplied results and selected criteria do not determine a final order.

#### Scenario: DBV pool provision is explicitly selected

- **WHEN** the competition explicitly uses DBV Spielordnung 2026/27 Anlage IV Teil 2 § 10 and two group entrants have equal match wins but unequal set differences
- **THEN** the entrant with the better set difference ranks first under that provision
- **AND** direct comparison is not applied before set difference

#### Scenario: Results cannot establish a final order

- **WHEN** required match results or a required lot outcome are unavailable and the selected criteria cannot resolve the order
- **THEN** the result identifies the affected order as unresolved rather than inventing a winner

#### Scenario: Repeat a calculation

- **WHEN** the eligible results, selected rule version and any recorded lot outcome are unchanged
- **THEN** repeating the calculation yields the same ranking and unresolved status
