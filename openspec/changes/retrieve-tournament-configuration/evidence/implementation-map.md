# Maker implementation and validation evidence

- Change: `retrieve-tournament-configuration`, issue [#194](https://github.com/wlambertz/rallyon/issues/194), parent [#191](https://github.com/wlambertz/rallyon/issues/191).
- Baseline: `9ae7655`; branch `feat/issue-191-phase-c-retrieve-tournament` in the isolated worktree.
- Makers: `codex-phase-c-maker` (query, wiring, unit/service coverage and integration), `/root/phase_c_persistence` (persistence tests), `/root/phase_c_http` (HTTP/security tests). All are registered in the lifecycle record and cannot certify their implementation.
- This is a maker report of actual implementation/checks, not independent G5/G6/G7 evidence or human UAT acceptance.

## Requirement traceability

| Requirement | Implementation | Actual automated evidence |
| --- | --- | --- |
| GET-CONFIG-001: stored configuration | Pure `GetTournamentUseCase`/`GetTournamentService` delegates one lookup to the existing port; the existing service maps its snapshot inside a read-only transaction | `GetTournamentUseCaseTest` covers partial snapshots and every stored status; Java `ConfigurationServiceImplTest` covers boundary mapping/delegation; HTTP coverage checks existing JSON/audit representation; persistence tests cover nullable and nested PUBLISHED data after the query transaction closes |
| GET-CONFIG-002: missing ID | Existing domain not-found error translated to the existing API-boundary error/advice | Unit requested-ID exception; Java service exception translation; HTTP 404 with ProblemDetail status/title/detail |
| GET-CONFIG-003: existing authentication-only access | Controller, published interface and shared security configuration remain unchanged | HTTP anonymous GET is 401 without service invocation; a caller without organizer authority can read another creator's PRIVATE configuration; an authenticated principal without a numeric user-ID claim can read; a malformed bearer token is rejected by the real decoder before the service |
| GET-CONFIG-004: no mutation | Query has no save/clock/write-validation path; service boundary is `@Transactional(readOnly = true)` | Pure store records zero saves and requested IDs; persistence tests compare stored configuration, child IDs, versions, audit values and status across repeated reads and after committed update |

The persistence suite sets OSIV off for its test context and checks no transaction/entity-manager resource remains after `service.get` returns. Its independent repository/mapper reads establish expected stored values rather than comparing two calls to the new query alone. It preserves the existing distinction between an update response's pre-flush version and a later persisted version.

## Checks actually run

From the isolated repository root, using Java 21 and the existing Maven wrapper:

```bash
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=GetTournamentUseCaseTest,ConfigurationServiceImplTest,DraftPersistenceIntegrationTest,ConfigurationControllerSecurityTest,LayerDependencyTest,TournamentmgmtModuleStructureTest test
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
python3 -m unittest discover -s .agents/skills/feature-lifecycle/scripts/tests -q
python3 -m unittest discover -s .claude/hooks/tests -q
OPENSPEC_COMMAND='node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js' python3 -m unittest discover -s openspec/schemas/rallyon-feature/tests -q
node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js schema validate rallyon-feature --json
node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js validate --all
npm run format:check
npm run format:check:changed
git diff --check
```

- IAM: 4 tests passed; existing local artifacts installed.
- Focused backend: 33 tests passed, including both architecture checks.
- Full backend: 89 discovered, 88 passed, 0 failures/errors, 1 skipped. The skipped test is the opt-in committed architecture-export generator; no architecture output was regenerated.
- Lifecycle tooling: 32 helper tests, 10 hook tests and 1 real pinned-CLI readiness test passed. The hook suite includes 33 fake-GitHub Bash execution cases; no live test issue was created.
- OpenSpec: schema valid; main lifecycle spec and this change both valid without warnings.
- Tracked/changed formatting and diff checks passed. Artifact links are checked separately before staging.

The cached CLI path above records the actual command used on this machine, not a project configuration requirement. The package was verified as OpenSpec 1.14.2; portable commands remain `npx -y @fission-ai/openspec@1.14.2 ...`.

## Failed attempts and correction

An agent's first unprivileged Maven attempt could not write the Kotlin daemon cache outside the sandbox. The parent ran canonical Maven commands with cache access; no compiler/project configuration was changed.

The first focused run had one new HTTP assertion failure: a standalone test mapper generated numeric timestamps, whereas the unchanged Spring HTTP representation uses ISO strings. The fixture now asserts the actual existing JSON contract explicitly. The subsequent focused run and full verify passed; production serialization was not changed or weakened.

## Compatibility and remaining evidence

No endpoint/interface/DTO, shared IAM, JWT validation, database migration/schema, dependency, `ro` command or bounded-context change was made. Retrieval adds behavior to the existing stub and leaves other service operations unchanged.

H2 persistence tests have Flyway disabled; MockMvc `jwt()` fixtures test authorization without signature verification. The malformed-token test uses the actual decoder but is not an exhaustive issuer/audience/signature acceptance test. Existing IAM validation tests were run, and real local Keycloak/PostgreSQL behavior still requires the API walkthrough.

Independent G5/G6 checks and G7 reviews remain pending. Docker Desktop was started; an existing unrelated database occupies host port 5432, so the new local Compose project uses temporary overrides outside the repository to expose its database on 55432. Environment preparation is not UAT success. No witnessed G8 acceptance or merge approval has been recorded, and the Phase B waiver does not apply.
