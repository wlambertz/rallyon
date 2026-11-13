# Proposal

## Why

Organizers need to identify player conflicts and recovery concerns across disciplines. Existing configuration contains policy and roster fields but does not establish an operational fixture or match-time workflow. Research in #228 establishes that rules depend on authority and event; 15 minutes is unverified.

## What Changes

Add a framework-free, stateless assessment within Tournament Management that accepts a caller-supplied snapshot of matches, confirmed participant membership, time facts and a selected rule supported by provenance. It returns explainable advisory findings and unresolved assessments. A caller requests a fresh assessment after changes; no background updates or persistence are implied. This first increment is a domain capability, not an organizer-facing workflow: integration, authoritative sources, a UI and operational capture are future work.

## Capabilities

### New Capabilities

- `player-scheduling-advisory`: assess player overlaps and recovery from supplied facts, preserve uncertainty and explain findings.

### Modified Capabilities

None.

## Impact and backward compatibility

Tournament Management gains internal domain behavior and tests. No existing REST endpoint, DTO, JWT claim, role, CLI, database schema or frontend changes. No new external API or dependency is proposed. This increment alone does not allow an organizer to use the advisory without later integration. Match creation, identity resolution, rule verification and operational interfaces remain outside scope.
