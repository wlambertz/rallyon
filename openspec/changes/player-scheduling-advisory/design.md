# Design

## Context and constraints

Tournament Management owns the assessment. The planning and administration packages currently contain no operational scheduling workflow; roster team IDs do not establish individual membership. The organizer frontend and Player Management are placeholders. The assessment cannot verify an external rule or establish identity authority on its own.

## Decisions and tradeoffs

Use a pure domain service over an immutable caller-supplied snapshot. Represent matches, individual participant membership with certainty, actual/estimated/proposed times and an optional rule selection with authority, edition and applicability reference. A selected rule is an input attestation, not proof verified by the service. Expose findings with affected matches, players when known, fact provenance and reasons; distinguish overlap, potential recovery concern and unresolved. Model unknown membership conservatively rather than assuming disjoint teams. Reject malformed intervals and contradictory timestamps or return explicit unresolved diagnostics; do not silently repair them.

Use half-open occupied intervals for overlap, labeling any overlap based on estimated or proposed times as potential; only confirmed actual intervals support an actual overlap. The recovery comparison uses actual end and next actual start if available, otherwise proposed start labelled provisional. Unknown actual end, rule or identity prevents a conclusive recovery result. Calls remain separate and do not substitute for a start. The supplied snapshot represents current facts; recomputation is stateless and the caller owns change notification and provenance history. Multiple findings may coexist; an overlap does not erase an unresolved rule concern. No finding authorizes an override.

This provides testable domain behavior without inventing fixture storage, rule registries or live integrations. It cannot by itself show anything to an organizer. A later change must supply trusted identities and rule applicability, lifecycle integration and an organizer interface.

## Bounded contexts, invariants and dependency direction

Keep the service and its values in an appropriate existing Tournament Management internal domain package, with no framework or adapter imports. No new Modulith module, aggregate or persisted state is needed. Do not consume another context's internal types. Canonical identity confirmation, double membership and authority validation remain caller responsibilities and are explicitly represented as uncertain when unavailable.

## Compatibility and escalation assessment

No changes to existing REST contracts, JWT/roles, `ro`, Flyway migrations, shared IAM, dependencies or published interfaces. If implementation needs any such changes, a new module or an aggregate-boundary change, stop and escalate. No automated claims of compliance are made.

## Validation, rollout and recovery

Use focused domain tests for overlap boundaries, rule provenance and equality, actual versus provisional time, uncertain membership/identity and missing/invalid facts, and recalculation after changes. Run the IAM install when required and tournamentmgmt Maven clean verify, formatting and strict OpenSpec validation. No production rollout or migration is part of this increment; later integration needs its own rollout and UAT.

## ADR assessment

No ADR is proposed for a stateless internal domain service with no new aggregate or cross-context dependency. Escalate and reassess if implementation changes those boundaries.
