# Architecture Decision Records (ADRs)

ADRs capture significant architectural decisions and their context. They live alongside the code so they evolve with it.

## Format
Each ADR is a markdown file named `NNNN-title.md` (zero-padded, chronological). Use this structure:

```markdown
# NNNN — Title

## Status
Proposed / Accepted / Deprecated / Superseded

## Context
What problem are we solving? What forces are at play?

## Decision
What did we decide, and why?

## Consequences
Positive, negative, and neutral outcomes. Risks and how they are mitigated.

## Related
Links to requirements, issues, PRs, or other ADRs.
```

## Convention
- Create an ADR for any architecturally significant decision (module boundaries, data model changes, technology choices, integration patterns).
- Link ADRs from pull requests and commit messages when relevant.
- Keep ADRs in `docs/adr/`.

## Example
See `0001-use-spring-modulith.md` for a worked example.
