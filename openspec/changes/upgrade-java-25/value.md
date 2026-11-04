# Value assessment

## Persona, context and evidence

Maintainers and contributors work across Tournament Management and shared Identity & Access. Source configuration confirms a Java 21 build versus Java 25 service runtime and conflicting dev-container documentation. No contributor time loss has been measured. The [preflight](evidence/java25-preflight.md) records successful Java 25 IAM and service tests, and a reproduced failure on clean PostgreSQL: shipped migrations do not run and authenticated draft creation fails. The same failure occurs with Java 21 bytecode on the Java 25 runtime.

## Scores and calculation

- User value: **2/5** — a maintenance improvement, with indirect organizer benefit rather than a new user capability.
- Learning value: **4/5** — a documented, reusable cross-toolchain upgrade and validation exercise, if completed.
- Risk reduction: **3/5** — the reproduced empty-database startup failure is a material operational risk; the proposed repair remains unverified and the newer minimum adds a consumer risk.
- Confidence: **0.75** — build configuration and the failure have corroborating evidence; the proposed remedy, scope of existing database impact and contributor benefit remain uncertain.
- Effort: **5** — coordinated build, IAM, CI, container, documentation, smoke testing and independent review, including Flyway startup integration and a clean-PostgreSQL regression test. Reassess if the remedy or migration compatibility proves more involved.

Priority = `(0.5 × 2 + 0.3 × 4 + 0.2 × 3) × 0.75 / 5 = 0.42`. This is advisory and is not approval or a delivery estimate.

## Assumptions and uncertainty

The exact Flyway remedy and its effect on existing databases have not been validated. IAM consumer impact, the complete dev-container lifecycle and CI/security/SBOM outcomes also remain unverified. Test the smallest integration in isolation and inspect migration compatibility before promotion; revisit effort or score if findings materially alter the work.

## G2 go/no-go

Pending renewed human approval of the corrected value evidence and specification.

## G3 independent challenge

The [Flyway delta review](evidence/g3-flyway-delta.md) rejected the prior assessment because it omitted the preflight evidence. A fresh check of this corrected assessment is pending.
