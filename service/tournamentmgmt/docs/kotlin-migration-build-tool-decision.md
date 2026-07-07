# Kotlin Migration Build-Tool Decision

## Status

Accepted for the tournamentmgmt Kotlin migration.

## Decision

The `service/tournamentmgmt` Kotlin migration starts on Maven. Gradle is not a prerequisite for adding mixed Java/Kotlin support or for converting service code incrementally.

## Rationale

- The tournamentmgmt service already builds through the Maven wrapper in `service/tournamentmgmt/mvnw`.
- Existing repo automation is Maven-oriented: `ro` build and run commands, GitHub Actions, Sonar analysis, Docker packaging, and the local IAM install flow all expect Maven.
- Maven supports mixed Java/Kotlin projects, which matches the intended migration path: add Kotlin support first, then convert code in small compatibility-preserving steps.
- Keeping Maven reduces the blast radius of the first migration PRs. The language migration can be validated independently from any future build-tool migration.

## Deferred Gradle Evaluation

Revisit Gradle only after the Maven-based Kotlin migration is stable, or if Maven becomes a measured blocker. A separate Gradle spike should include evidence for at least one of these criteria:

- Clean or incremental Maven builds are materially too slow for local or CI feedback.
- Remote build cache support becomes necessary for reliable developer or CI throughput.
- Gradle convention plugins would meaningfully reduce duplicated JVM build configuration.
- A repo-wide JVM multi-project build strategy becomes more valuable than preserving the current Maven flow.

Any Gradle proposal must compare build, test, package, Sonar, Docker, `ro`, IAM install, CI, and onboarding impact before replacing Maven.

## Outcome

The full production Kotlin migration (issues #102–#107) completed on Maven without a build-tool change. `TournamentMapper` (MapStruct) moved to kapt via `kotlin-maven-plugin`'s `kapt` goal, JPA entities use the `jpa`/`all-open` compiler-plugin presets for no-arg constructors and lazy-proxy support, and Lombok was removed once no code depended on it. No Gradle spike was needed; none of the deferred-evaluation criteria above have been triggered.

## Validation

This decision note does not change build behavior. Validate it with:

```bash
npm run format:check:changed
```

Run service verification only if a follow-up PR changes build files or service code.
