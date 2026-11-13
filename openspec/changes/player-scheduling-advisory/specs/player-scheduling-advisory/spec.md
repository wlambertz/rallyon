## ADDED Requirements

### Requirement: Assess player overlap from supplied facts

The assessment SHALL report an overlap for each confirmed player in two distinct matches whose supplied occupied intervals overlap, including a confirmed doubles member. It SHALL identify the player, matches, supporting times and their certainty. An overlap based on proposed or estimated times SHALL be labelled potential; only confirmed actual intervals support an actual overlap. Adjacent intervals do not overlap. It SHALL NOT infer that different identifiers denote the same player.

#### Scenario: Doubles and singles overlap

- **WHEN** a confirmed doubles member also enters a singles match and their confirmed actual occupied intervals overlap
- **THEN** the result identifies that player and both matches as an actual overlap with the relevant times

#### Scenario: Proposed times overlap

- **WHEN** confirmed membership is known but overlapping occupied intervals use proposed or estimated times
- **THEN** the result identifies a potential overlap and labels the supporting times as provisional

#### Scenario: Adjacent intervals

- **WHEN** a player's first known occupied interval ends exactly when the second starts
- **THEN** the assessment does not report an overlap for those intervals

### Requirement: Assess recovery using a selected rule

The assessment SHALL compare a confirmed player's actual match end and next actual or proposed start against a caller-selected recovery interval with authority, edition and applicability reference. It SHALL label the start as actual or proposed and report a potential concern if recovery is shorter than the selected duration. It SHALL NOT select a default, determine applicability, infer waivers or treat call as start.

#### Scenario: Proposed start has insufficient recovery

- **WHEN** a selected applicable rule specifies 30 minutes, the first match ends at 10:30 and the next proposed start is 10:45
- **THEN** the assessment reports a potential recovery concern, identifies the proposed time and the supplied rule provenance

#### Scenario: Call is not start

- **WHEN** a call is known but the next match start is unknown
- **THEN** the assessment does not use the call as the start and reports recovery as unresolved

#### Scenario: Exact recovery boundary

- **WHEN** the known elapsed time equals the supplied recovery interval
- **THEN** the assessment does not report insufficient recovery for that pair under that supplied interval

### Requirement: Represent uncertainty without declaring safety

The assessment SHALL report unresolved assessments and their reasons when relevant identity, team membership, time facts or rule applicability are missing or uncertain. It SHALL NOT report a pair as clear on the basis of missing evidence, and SHALL distinguish known actual facts from estimates and proposals. Unsupported, contradictory or invalid input SHALL be rejected or reported as unresolved rather than silently normalized.

#### Scenario: Unknown doubles membership

- **WHEN** a doubles entry has incomplete membership and could involve a player in another match
- **THEN** the assessment reports the uncertainty instead of concluding that those entries are conflict-free

#### Scenario: Unknown rule

- **WHEN** no applicable rule with its provenance is supplied
- **THEN** recovery is unresolved and no numeric default is assumed

#### Scenario: Estimated end

- **WHEN** the first match has only an estimated end and a subsequent start is known
- **THEN** the result labels the timing uncertainty and does not represent a recovery calculation as confirmed

### Requirement: Reassess changed facts without enforcing scheduling

The assessment SHALL evaluate the current supplied snapshot each time it is requested, without persisting or changing the schedule. Findings SHALL be advisory and SHALL NOT block match scheduling. A caller SHALL be responsible for requesting a new assessment when facts change.

#### Scenario: Delayed match and correction

- **WHEN** a caller reassesses after an actual match end changes from 10:30 to 10:50 and the next proposed start is 11:00 under a supplied 30-minute interval
- **THEN** the result reflects the revised ten-minute interval and identifies a potential recovery concern
