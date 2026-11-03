# G4 exact issue preview

Source of truth: [issue-drafts.json](../issue-drafts.json). This preview is generated from that payload; the approved hash binds the JSON payload and tasks.

- Repository: `wlambertz/rallyon`
- Stable draft ID: `retrieve-tournament-configuration`
- Title: feat(tournamentmgmt): retrieve saved tournament configurations
- Dependencies: none
- Labels: none
- Assignees: none
- Milestone: none requested

<!-- prettier-ignore-start -->

## Problem

An Organizer can create and update a tournament draft, but the existing `GET /api/tournamentmgmt/config/{tournamentId}` operation delegates to an unimplemented service method. An API client cannot reload saved configuration to continue setup.

Implement the Phase C pilot for #191 in the existing Tournament Management `setup.configuration` module.

## Specification and approach

OpenSpec change: `retrieve-tournament-configuration`, schema `rallyon-feature`.
Capability: `tournament-configuration-read`; requirements GET-CONFIG-001 through GET-CONFIG-004.
Artifacts: `openspec/changes/retrieve-tournament-configuration/{refinement,proposal,design,value,tasks}.md` and `specs/tournament-configuration-read/spec.md`. Independent G3 evidence: `evidence/g3-value-check.md`.

Add a pure `GetTournamentUseCase` and `GetTournamentService` using the existing `TournamentStore.findById` port. Wire the query in `TournamentUseCaseConfiguration`; implement the existing `ConfigurationServiceImpl.get(Long)` with a read-only transaction, existing API mapping and existing not-found exception translation.

## Acceptance criteria

- [ ] GET-CONFIG-001: an authenticated GET for an existing numeric ID returns 200 using the existing API `Tournament` representation. Support incomplete drafts, nested configuration/child identifiers and any stored lifecycle status without write-time validation or an If-Match requirement.
- [ ] GET-CONFIG-002: an unknown numeric ID returns 404 with the existing ProblemDetail convention, including status 404 and title Not Found. It does not return null, an empty tournament or an unimplemented-operation failure.
- [ ] GET-CONFIG-003: anonymous GET returns 401. Preserve authentication-only access: an authenticated caller without an organizer role can retrieve another creator's PRIVATE configuration. Add no creator, membership, visibility or new claim restriction; shared JWT validation stays unchanged.
- [ ] GET-CONFIG-004: retrieval never saves, advances version, modifies audit metadata, changes status or alters nested data. Repeated reads preserve stored values; reading after a committed replacement reflects persisted configuration/version/audit values without changing existing create/update response semantics.
- [ ] Preserve REST path, published service signature, JSON representation, JWT/role contracts, database schema and ro CLI behavior. No migration, new dependency/module or shared IAM change.
- [ ] Add pure query tests, Java service-boundary coverage, real-service persistence coverage and HTTP/security coverage, retaining ArchUnit and Modulith verification.
- [ ] Record actual implementation/test evidence, independent G5/G6 checks, three fresh-context G7 review lenses and observed lifecycle friction. Do not count file readiness, MockMvc JWT fixtures or H2 checks as evidence of real-token/PostgreSQL/UAT success.
- [ ] Prepare and run the local create -> retrieve -> update -> retrieve, repeated-read, unknown-ID and anonymous walkthrough for explicit witnessed human G8 acceptance. Redact tokens. Merge requires a separate human decision; the Phase B UAT waiver does not apply.

## Validation

Run from the repository root; install existing IAM artifacts when needed:

```bash
./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml -Dtest=GetTournamentUseCaseTest,ConfigurationServiceImplTest,DraftPersistenceIntegrationTest,ConfigurationControllerSecurityTest,LayerDependencyTest,TournamentmgmtModuleStructureTest test
./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml clean verify
python3 -m unittest discover -s .agents/skills/feature-lifecycle/scripts/tests -v
python3 -m unittest discover -s .claude/hooks/tests -v
python3 -m unittest discover -s openspec/schemas/rallyon-feature/tests -v
npx -y @fission-ai/openspec@1.14.2 schema validate rallyon-feature --json
npx -y @fission-ai/openspec@1.14.2 validate --all
npm run format:check
npm run format:check:changed
git diff --check
```

Verify local artifact links and record actual runtime/manual results and limitations. Do not regenerate committed architecture exports. H2 tests have Flyway disabled; the witnessed walkthrough must name its real environment. Docker availability must be established before UAT.

## Boundaries and delivery

No frontend, ownership/role-policy feature, organizer list/public search, other lifecycle operations, legacy-spec migration, CLI deprecation or dependency remediation.

Human G1 and G2 approvals and independent G3 checking are recorded in the change. G4 must approve this exact payload and continued implementation before creation through the lifecycle helper. Changed scope/value/architecture or substantial environment-recovery effort returns to the affected human gate.

Deliver a PR referencing this issue and #191 and observe applicable CI. After human acceptance and merge, sync/archive the change and mark Phase C complete; keep #191 open for its migration/deprecation follow-ups.

<!-- prettier-ignore-end -->
