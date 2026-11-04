# Value assessment

## Persona, context and evidence

Maintainers and contributors work across Tournament Management and shared Identity & Access. Before this change, source configuration selected Java 21 for builds and Java 25 for the service runtime; the candidate in PR #209 aligns these settings on 25. No contributor time loss has been measured. The [preflight](evidence/java25-preflight.md) reproduced missing Flyway execution on clean PostgreSQL, and the [repair evidence](evidence/flyway-repair.md) records a successful isolated fix. The [follow-up](evidence/g5-validation-followup.md) records successful hosted CI and dev-container lifecycle for PR head `bb3690b`, as well as local bootstrap failures requiring the newly proposed repairs.

## Scores and calculation

- User value: **2/5** — a maintenance improvement, with indirect organizer benefit rather than a new user capability.
- Learning value: **4/5** — a documented, reusable cross-toolchain upgrade and validation exercise, if completed.
- Risk reduction: **3/5** — the reproduced empty-database startup failure is a material operational risk; the proposed bootstrap repair and existing database handling remain to be independently validated, and the newer minimum adds a consumer risk.
- Confidence: **0.75** — the build, Flyway repair and local failures have corroborating evidence; existing-database impact and contributor benefit remain uncertain.
- Effort: **5** — coordinated build, IAM, CI, container, documentation, smoke testing and independent review, including Flyway and local bootstrap repairs. Existing-volume safety and cross-platform provisioning need independent validation. Reassess if the remedy or migration compatibility proves more involved.

Priority = `(0.5 × 2 + 0.3 × 4 + 0.2 × 3) × 0.75 / 5 = 0.42`. This is advisory and is not approval or a delivery estimate.

## Assumptions and uncertainty

The Flyway integration passed isolated clean-database tests; effects on real existing databases remain unverified. The dev-container lifecycle succeeded with amd64 emulation, and hosted CI/security/SBOM passed for `bb3690b`; neither result covers the proposed bootstrap repair or a later PR head. The PostgreSQL and provisioning candidates were tested in isolated copies, not yet implemented or independently verified. IAM external-consumer impact is unknown. Reassess if these findings materially change the work.

## G2 go/no-go

Pending renewed human approval of the corrected value evidence and specification.

## G3 independent challenge

The [bootstrap delta review](evidence/g3-bootstrap.md) rejected the prior assessment because it omitted successful Flyway, dev-container and hosted CI evidence. A fresh independent check of this corrected assessment is pending.
