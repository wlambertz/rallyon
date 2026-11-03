# Tasks: retrieve a saved tournament configuration

## 1. Implement the query with focused coverage

- [x] 1.1 Implement pure `GetTournamentUseCase`/`GetTournamentService` using `TournamentStore.findById` once, returning existing snapshots or the existing domain not-found exception and never saving (GET-CONFIG-001, GET-CONFIG-002, GET-CONFIG-004). Add `GetTournamentUseCaseTest` for partial drafts, another stored status, missing records and no writes. Validate: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=GetTournamentUseCaseTest test`.
- [x] 1.2 Wire the query in `TournamentUseCaseConfiguration` and implement the existing service `get(Long)` with a read-only transaction, API mapping and not-found translation (GET-CONFIG-001, GET-CONFIG-002, GET-CONFIG-004). Adapt the Java constructor mocks and service-boundary tests without changing public signatures. Validate: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=ConfigurationServiceImplTest test`.
- [x] 1.3 Extend real-service persistence coverage for nullable drafts, nested values/identifiers, repeated reads and reads after committed replacement (GET-CONFIG-001, GET-CONFIG-004). Compare persisted versions/audit values and verify mapping works after the query transaction closes. Validate: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=DraftPersistenceIntegrationTest test`.
- [x] 1.4 Extend HTTP/security tests for existing-record 200, missing-record 404 ProblemDetail, anonymous 401 and an authenticated non-organizer caller reading another creator's PRIVATE configuration (GET-CONFIG-001, GET-CONFIG-002, GET-CONFIG-003). Preserve shared JWT validation and distinguish MockMvc authorization evidence from token cryptography. Validate: `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=ConfigurationControllerSecurityTest test`.

## 2. Validate and prepare a stable implementation snapshot

- [x] 2.1 Install existing IAM modules when needed, run the focused behavior/adapter/security suite and architecture checks, then full service verification (GET-CONFIG-001–GET-CONFIG-004). Validate: `./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install`; `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=GetTournamentUseCaseTest,ConfigurationServiceImplTest,DraftPersistenceIntegrationTest,ConfigurationControllerSecurityTest,LayerDependencyTest,TournamentmgmtModuleStructureTest test`; `./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify`. Do not enable generated architecture exports.
- [x] 2.2 Check the lifecycle tooling and OpenSpec artifacts used by this pilot (GET-CONFIG-001–GET-CONFIG-004 traceability and #191 pilot obligation). Validate: `python3 -m unittest discover -s .agents/skills/feature-lifecycle/scripts/tests -v`; `python3 -m unittest discover -s .claude/hooks/tests -v`; `python3 -m unittest discover -s openspec/schemas/rallyon-feature/tests -v`; `npx -y @fission-ai/openspec@1.14.2 schema validate rallyon-feature --json`; `npx -y @fission-ai/openspec@1.14.2 validate --all`.
- [x] 2.3 Check formatting, artifact links and diff hygiene without unrelated generated output (GET-CONFIG-001–GET-CONFIG-004 documentation/compatibility evidence). Validate: `npm run format:check`; `npm run format:check:changed`; `git diff --check`; verify local Markdown links in changed artifacts resolve.
- [x] 2.4 Prepare requirement-to-implementation/test mapping and actual maker results under `evidence/implementation-map.md`, naming environment and limitations (GET-CONFIG-001–GET-CONFIG-004). Finish the implementation checkboxes and stage the final source/tasks before independent G5 checking so both working-tree and index content are stable. Completion check: each requirement maps to actual code/test evidence and commands/results; `git diff --cached --check` passes. Do not mark independent gates or UAT passed in the maker report.

## Workflow follow-up

These are separate lifecycle decisions and delivery obligations, not implementation checkboxes that can be pre-certified.

- G4 must first approve the exact issue payload and continued implementation; creation uses the helper only.
- After the stable implementation snapshot, record independent G5 implementation and G6 testing evidence for GET-CONFIG-001–GET-CONFIG-004, with separate finalized reports and actual checker identities.
- Obtain three fresh-context G7 review lenses, resolve blocking findings and repeat affected checks after changes. Stage source changes before renewing source-bound evidence.
- After current review, prepare `uat.md` and run the local create → retrieve → update → retrieve, repeated-read, unknown-ID and anonymous scenarios for witnessed G8 acceptance. Preserve/redact actual observations appropriately; no token is written into evidence.
- Record observed workflow friction; expand or change approved scope only through the applicable human gate. A substantially larger environment-recovery effort returns to G2.
- Commit/push and open a PR referencing the approved pilot issue and #191; observe applicable CI. Merge requires its own human decision.
- After acceptance and merge, sync/archive the OpenSpec change and mark Phase C complete. Keep #191 open for legacy-spec migration and CLI-deprecation follow-ups.
