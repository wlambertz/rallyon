# Independent G6 test check

Checker: `/root/phase_c_verification`, independent of all registered makers.

Outcome: **pass** for applicable automated QA. No failed check or blocking coverage defect was found. Human UAT was not performed; this is not G8 acceptance, deployed E2E evidence or a lifecycle state mutation.

## Snapshot and environment

Worktree: `/private/tmp/rallyon-phase-c-retrieve-tournament`; baseline HEAD `9ae7655`. Requirements and exact issue #194 acceptance criteria were read directly from approved artifacts and lifecycle state, with the raw staged implementation diff and maker results. Followed root/service `AGENTS.md` and `.agents/skills/feature-lifecycle/references/qa.md`.

Read-only imported lifecycle helper `Session('.', 'retrieve-tournament-configuration').fingerprint('G6')` returned `8cfadcd552127a571395c3b979192a3f686ac846457f61cd6b29a17887d938ac` before checks and after report preparation, equal to G5. No source/index change occurred.

Environment: macOS host, Temurin OpenJDK 21.0.4, Node v22.14.0, Python 3.11.0. Existing installed IAM artifacts were supplied by the maker's successful install; this checker did not reinstall unchanged IAM modules. Maven ran with escalation for existing Maven/Kotlin cache access. The cached OpenSpec package's `package.json` was read and confirms version 1.14.2; no CLI download or project dependency change was needed.

## Commands and actual results

All commands ran from the worktree root on 2026-11-02. Every command below exited 0.

```bash
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
python3 -m unittest discover -s .agents/skills/feature-lifecycle/scripts/tests -v
python3 -m unittest discover -s .claude/hooks/tests -v
OPENSPEC_COMMAND='node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js' python3 -m unittest discover -s openspec/schemas/rallyon-feature/tests -v
node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js schema validate rallyon-feature --json
node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js validate --all
npm run format:check
npm run format:check:changed
git diff --check
git diff --cached --check
```

- Full Maven: BUILD SUCCESS, 89 discovered, 88 passed, 0 failures/errors, 1 skipped, including packaging. Surefire XML independently confirmed `GetTournamentUseCaseTest` 3, Java `ConfigurationServiceImplTest` 7, `DraftPersistenceIntegrationTest` 7, `ConfigurationControllerSecurityTest` 13, `LayerDependencyTest` 2 and `TournamentmgmtModuleStructureTest` 1 all passed. Existing serialization, mapping, create/update, constraints and application tests also passed. The sole skip is the opt-in `TournamentmgmtDocumentationTests` export generator; no committed architecture exports were regenerated. Existing Kotlin safe-call/kapt warnings do not fail the build and concern unchanged code.
- Lifecycle helper: 32 passed; hooks: 10 passed, including fake-GitHub shell-behavior coverage; pinned-CLI readiness: 1 passed. No live GitHub issue or comment was created.
- Schema: valid with empty issues array. OpenSpec validation: 2 passed, 0 failed (main lifecycle specification and this change).
- Formatting: 441 tracked files and 21 changed files passed. Both working-tree and cached diff hygiene passed; no unstaged source differences were present.
- Additional read-only `python3` heredoc enumerated `git diff --cached --name-only --diff-filter=ACM`, extracted Markdown link destinations, ignored external schemes/anchors and resolved local paths relative to each document: 21 local link paths checked, 0 missing. This establishes target existence, not external URL availability or anchor correctness.

The full clean verification includes the focused suites, so no redundant focused Maven rerun was needed. No check failed during this independent run. No Go or frontend runtime was touched; their suites were inapplicable.

## Requirement and scenario traceability

| Requirement / scenario | Actual automated evidence and limits |
| --- | --- |
| GET-CONFIG-001 / partial draft | `returns an unchanged partial draft without saving`; Java `getMapsTheStoredSnapshotWithoutCallingWriteUseCases`; persistence `retrieves nullable draft without changing its stored snapshot`; HTTP `retrievesExistingConfigurationInThePublishedRepresentation` checks 200 and JSON/audit/null representation without If-Match. |
| GET-CONFIG-001 / another stored status | Pure `reads all stored statuses without validating draft completeness` covers every enum status; persistence nested retrieval explicitly stores PUBLISHED and preserves it. HTTP success is separately covered with DRAFT; there is no deployed per-status walkthrough. |
| GET-CONFIG-001 / nested configuration | Persistence `retrieves nested published configuration after transaction closes without changing child identities` checks courts, disciplines/brackets, rosters, schedule, windows, venue and identifiers with OSIV disabled and no transaction/entity-manager resource after return. |
| GET-CONFIG-002 / missing tournament | Pure missing-record test verifies requested ID, one lookup and zero saves; Java `getTranslatesTheDomainNotFoundException`; HTTP `mapsMissingConfigurationsToNotFoundProblemDetail` verifies 404, problem content type, title, status and detail. |
| GET-CONFIG-003 / anonymous | HTTP `rejectsAnonymousConfigurationReadsBeforeRetrieval` checks 401 and no service invocation. |
| GET-CONFIG-003 / different creator without organizer role | HTTP `permitsNonOrganizerToReadAnotherCreatorsPrivateConfiguration` uses no authorities and another creator's PRIVATE snapshot; additional test permits an authenticated principal without numeric user ID. These JWT fixtures prove request authorization behavior, not cryptographic token validation. |
| GET-CONFIG-003 / rejected JWT | HTTP `rejectsMalformedBearerTokenBeforeConfigurationRetrieval` uses the real decoder, checks 401 and no service invocation. Shared issuer/audience/signature configuration is unchanged by raw diff; exhaustive real-token issuer/audience/signature validation was not independently rerun here. Existing IAM installation/test results remain maker evidence, not this checker's fresh result. |
| GET-CONFIG-004 / repeated reads | Pure query test records zero saves and repeated exact snapshots. Nullable/nested persistence tests compare independent repository/mapper snapshots, child IDs, versions, audit metadata and lifecycle status before/after repeated real-service reads. |
| GET-CONFIG-004 / committed replacement | Persistence `retrieves committed replacement with persisted version and audit values` checks replacement content, incremented committed version, creator and modification fields, repeated GET and unchanged storage; explicitly retains existing pre-flush update-response version semantics. |
| Compatibility and architecture | Clean build includes Java interop, API JSON serialization/constraint, mapper, existing create/update and ArchUnit/Modulith tests. Raw diff confirms no public DTO/interface/controller/security, schema, migration, dependency or CLI edit. |
| Pilot workflow acceptance | Helper/hook/readiness and OpenSpec checks passed locally. G7 independent review lenses, live local create → retrieve → update → retrieve, repeated reads, unknown ID, anonymous request, witnessed G8 and merge approval are still required separately. No workflow task is certified by readiness alone. |

## Limitations and disposition

H2 integration tests disable Flyway and do not prove PostgreSQL migrations, production database behavior or a running deployment. MockMvc is not deployed E2E, and its successful `jwt()` requests bypass signature validation. The malformed-token check is real decoder rejection only. No live Keycloak/PostgreSQL API walkthrough or human UAT was performed by this checker; the placeholder frontend has no browser coverage. The maker's historical first-run failure/correction is not erased by this passing independent run.

These limits are explicit in approved design and leave the required witnessed G8 work pending; they do not conceal a failed automated check. Parent must review and record the actual G6 outcome only after this finalized report exists. No gates, source, approved artifacts, tasks or staging were modified by this checker.
