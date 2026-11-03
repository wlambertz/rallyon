# Legacy specification migration

The nine historical YAML tickets below were reverse-engineered descriptions, not authoritative runtime contracts. Their original `specification` identifiers are retained as capability directories. Each acceptance condition is accounted for in source order (AC1, AC2, …). **Actual** retains implemented behavior, **Replaced** corrects a stale or overly broad claim, and **Not applicable** records an unimplemented assumption rather than introducing new behavior. The source paths are historical identifiers, intentionally not links to files scheduled for removal.

This migration changes documentation only. It preserves existing REST paths, JWT claims and role names, and CLI behavior except for the separately approved legacy helper compatibility/deprecation work. The existing `feature-lifecycle` and `tournament-configuration-read` specifications are outside this migration and remain unchanged.

## tournament-draft-create

Historical source: `docs/wiki/specs/tournamentmgmt/tournament-draft-create.yaml`.

Replacement: [tournament-draft-create](../openspec/specs/tournament-draft-create/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid name and date range → POST → 201 DRAFT | **Replaced** → Create an initial draft / Create through the published endpoint. Actual path is `POST /api/tournamentmgmt/config/drafts?organizerId=…`; input is name and visibility, with no dates. | [controller][controller], [create-use-case][create-use-case], [domain-create][domain-create], [web-tests][web-tests], [draft-tests][draft-tests] |
| AC2: missing name/dates → 400 with validation errors | **Replaced** → Validate creation basics / Reject invalid creation before persistence. Missing or invalid name and missing visibility are rejected by the domain. Dates are not creation input. A 400 validation response is not established. | [domain-create][domain-create], [advice][advice], [draft-tests][draft-tests] |
| AC3: end before start → POST → 400 | **Not applicable** to creation: no date fields exist in CreateDraftRequest. The implemented date check is captured under tournament-draft-update / Validate replacement values / Reject invalid schedule. | [controller][controller], [domain-replace][domain-replace], [draft-tests][draft-tests] |

`DraftUseCaseTest` covers initialization and rejection before persistence; `ConfigurationControllerSecurityTest.acceptsOrganizerRole` checks the current route and 201 using a mocked service. Creation throws IllegalArgumentException for invalid names and NullPointerException for null visibility; the controller advice has no handler for these exceptions. The migration therefore does not promise a new HTTP validation mapping.

## tournament-draft-update

Historical source: `docs/wiki/specs/tournamentmgmt/tournament-draft-update.yaml`.

Replacement: [tournament-draft-update](../openspec/specs/tournament-draft-update/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid ID/configuration → PUT → 200 | **Replaced** → Replace a draft configuration / Replace existing configuration and Clear omitted optional configuration. Actual path is `/api/tournamentmgmt/config/{tournamentId}/draft`, with numeric If-Match; this replaces editable configuration rather than partially merging it. | [controller][controller], [domain-replace][domain-replace], [update-use-case][update-use-case], [update-tests][update-tests], [draft-tests][draft-tests], [web-tests][web-tests] |
| AC2: non-DRAFT → 409 | **Actual** → Reject conflicts and missing drafts / Reject non-DRAFT state. Domain conflict is translated through the API facade and controller advice. | [domain-replace][domain-replace], [service][service], [advice][advice], [update-tests][update-tests], [draft-tests][draft-tests], [web-tests][web-tests] |
| AC3: stale version → 409 DraftUpdateConflictException | **Actual** → Reject conflicts and missing drafts / Reject stale version. | [domain-replace][domain-replace], [service][service], [advice][advice], [update-tests][update-tests], [web-tests][web-tests] |
| AC4: unknown ID → 404 TournamentNotFoundException | **Actual** → Reject conflicts and missing drafts / Reject missing tournament. This assumes a valid replacement body; invalid basics are checked before lookup. | [update-use-case][update-use-case], [service][service], [advice][advice], [update-tests][update-tests], [web-tests][web-tests] |

The historical non-DRAFT 409 remains correct in current code. It must not be confused with InvalidDraftUpdateException (invalid submitted values), which maps to 400. `updatesDraftAndReplacesOwnedFields` and `replacement preserves identity and creation metadata` cover replacement and preserved metadata; route/error tests mock the service and are not full persistence tests.

## keycloak-auth-integration

Historical source: `docs/wiki/specs/tournamentmgmt/keycloak-auth-integration.yaml`.

Replacement: [keycloak-auth-integration](../openspec/specs/keycloak-auth-integration/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid JWT with rallyon_user_id → protected endpoint → 200 with user context | **Replaced** → Resolve the application principal / Resolve valid application identity; Keep default role mapping and authorization scope / Authenticate a protected application operation. A user-ID claim alone is insufficient: token validation, nonblank subject and a mapped role are required. Endpoint semantics determine 200, 201 or another result. | [iam-filter][iam-filter], [iam-principal][iam-principal], [iam-config][iam-config], [iam-tests][iam-tests], [web-tests][web-tests] |
| AC2: expired/invalid JWT → 401 | **Actual** → Validate bearer tokens / Reject invalid bearer token. Decoder validates signature, issuer, standard time claims and the configured nonblank audience. | [iam-filter][iam-filter], [web-tests][web-tests] |
| AC3: no JWT → 401 | **Actual** → Validate bearer tokens / Require authentication, excluding configured permit-all routes. | [iam-filter][iam-filter], [web-tests][web-tests] |
| AC4: ADMIN → admin-only endpoint → 200 | **Not applicable**: no ADMIN default mapping or admin-only controller endpoint exists here. Reconciled under Keep default role mapping and authorization scope / Retain authenticated-only default authorization; no replacement admin API is invented. | [iam-config][iam-config], [iam-filter][iam-filter], [controller][controller] |
| AC5: lacks required role → protected endpoint → 403 | **Not applicable** as a blanket endpoint policy. Reconciled under Resolve the application principal / Reject missing or malformed identity and default authentication scope. No mapped roles causes factory rejection; the starter does not install per-endpoint role checks or establish a 403 mapping for that factory exception. | [iam-principal][iam-principal], [iam-tests][iam-tests], [iam-filter][iam-filter] |

`KeycloakPrincipalFactoryTest` proves realm-role mapping, missing user-ID rejection and rejection without mapped roles. `KeycloakSecurityAutoConfigurationTest` checks bean registration only. `ConfigurationControllerSecurityTest` proves anonymous and malformed-bearer 401 and authenticated controller behavior; its `jwt()` requests supply authentication directly and do not prove signature, expiry, issuer or audience validation. Those decoder contracts are supported by production code inspection, not a real-token integration test. No ownership or new role policy is inferred from this migration; the existing read capability owns its documented read behavior.

## ro-cli-build-command

Historical source: `docs/wiki/specs/ro-cli/build-command.yaml`.

Replacement: [ro-cli-build-command](../openspec/specs/ro-cli-build-command/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid project → ro build → successful build | **Replaced** → Run configured Maven build / Build successfully. Builds the configured service root with Maven, not arbitrary Go/Java modules or an entire multi-runtime project. | [build][build], [exec][exec] |
| AC2: source errors → build fails with details | **Actual** → Run configured Maven build / Report a build error. Maven output is streamed and returned errors include exit and cause. | [build][build], [exec][exec] |

No dedicated build command tests exist. Evidence for command, goal overrides, timeout and error propagation is production code inspection; external Maven execution is not demonstrated by this documentation migration.

## ro-cli-test-command

Historical source: `docs/wiki/specs/ro-cli/test-command.yaml`.

Replacement: [ro-cli-test-command](../openspec/specs/ro-cli-test-command/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid project/tests → ro test → tests report results | **Replaced** → Run Maven tests / Run a successful suite. Executes Maven test at the configured service root, not all runtime suites. | [test][test], [exec][exec] |
| AC2: failing tests → nonzero with details | **Actual** → Run Maven tests / Report test failure. Process output is streamed; command returns a wrapped error and main exits nonzero. | [test][test], [exec][exec], [root][root], [main][main] |

No dedicated test-command tests exist. `pkg/cmd/test.go` is implementation, despite its filename; it is not a Go test file. Runtime output and failure contracts are verified by inspection, not by running a failing Maven suite.

## ro-cli-run-command

Historical source: `docs/wiki/specs/ro-cli/run-command.yaml`.

Replacement: [ro-cli-run-command](../openspec/specs/ro-cli-run-command/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid config → ro run → application starts | **Replaced** → Run the configured tournament service / Start the service process and Apply runtime options. The run group displays help; its service command launches Maven spring-boot:run. Cobra uses the first Use word (`service`) as the subcommand; the documented invocation includes tournamentmgmt. | [run][run], [exec][exec] |
| AC2: missing dependencies → details | **Replaced** → Run the configured tournament service / Report a runtime failure. No separate dependency preflight exists; process-start errors are wrapped and Maven dependency diagnostics are streamed. | [run][run], [exec][exec] |

No dedicated run tests exist. This migration does not assert service readiness merely because Maven was launched; production code inspection establishes process orchestration only.

## ro-cli-deploy-command

Historical source: `docs/wiki/specs/ro-cli/deploy-command.yaml`.

Replacement: [ro-cli-deploy-command](../openspec/specs/ro-cli-deploy-command/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid deployment config → deploy succeeds | **Replaced** → Prepare and dispatch deployment / Deploy successfully and Honor deployment safety controls. Actual operation dispatches GitHub Actions after token/config/preflight/confirmation; it is not a direct local Docker deployment. | [deploy][deploy], [deploy-tests][deploy-tests] |
| AC2: invalid deployment target → configuration error | **Replaced** → Prepare and dispatch deployment / Reject invalid deployment configuration. Concrete errors cover missing workflow/repo/token, detached HEAD and configured branch mismatch; arbitrary environment names are not universally rejected. | [deploy][deploy], [deploy-tests][deploy-tests] |

`TestBuildDeployContextBranchGuard`, `TestBuildDeployContextAllowsYesOverride`, `TestEnsureCommitGreen` and `TestAwaitWorkflow` cover guards, overrides, HTTP status checks and workflow polling using stubs/local HTTP servers. They do not perform a live deployment. Waiting can be disabled; a successful dispatch then does not establish workflow completion.

## ro-cli-docker-command

Historical source: `docs/wiki/specs/ro-cli/docker-command.yaml`.

Replacement: [ro-cli-docker-command](../openspec/specs/ro-cli-docker-command/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid Docker config → ro docker → successful operation | **Replaced** → Execute Docker workflows / Build an image and Operate the local stack. The docker group shows help; build and compose up/down/logs are the concrete operations. | [docker][docker], [docker-tests][docker-tests], [exec][exec] |
| AC2: Docker absent → availability details | **Replaced** → Execute Docker workflows / Report unavailable Docker. There is no availability preflight in this command: executable-not-found is returned as a wrapped process error. | [docker][docker], [exec][exec] |

`docker_test.go` covers image references, tag sanitization/selection and Compose base arguments. It does not launch Docker, prove daemon health or directly exercise the missing-executable path.

## ro-cli-spec-commands

Historical source: `docs/wiki/specs/ro-cli/spec-commands.yaml`.

Replacement: [ro-cli-spec-commands](../openspec/specs/ro-cli-spec-commands/spec.md).

| Condition | Disposition and requirement / scenario | Implementation and test evidence |
| --- | --- | --- |
| AC1: valid name → ro spec create → file in configured directory | **Actual**, with approved route repair → Retain both legacy command routes / Create a legacy ticket. Also retains root-level ro create. Writes YAML to an already-existing writable destination; it does not create the directory. | [spec-registration][spec-registration], [spec-create][spec-create], [spec-tests][spec-tests] |
| AC2: valid file → ro spec validate → passes | **Replaced** → Retain both legacy command routes / Validate a legacy ticket. This is a YAML parser plus limited local key/array checks, not external JSON Schema or OpenSpec Markdown validation. | [spec-validate][spec-validate], [spec-tests][spec-tests] |
| AC3: valid file → ro spec view → displays content | **Actual**, with approved route repair → Retain both legacy command routes / Display a legacy file. View reads raw bytes regardless of extension; it does not validate or convert YAML/JSON/TOON. | [spec-view][spec-view], [spec-tests][spec-tests] |

The original implementations registered leaf commands at the root despite advertising `ro spec ...`. The approved compatibility repair supports both command paths using shared handlers. Additional requirements cover visibility of both routes in help and generated reference, exactly one stderr deprecation warning including failures, the OpenSpec 1.14.2 pointer, absent default schemaURL/automatic Schema line, and retained explicit schema configuration. `TestLegacySpecRoutes` and the error/default tests in `spec_test.go` exercise these additions, stdout payloads and formats. The compatibility directory fallback remains historical; authors create current project specs under openspec/.

## Validation scope

The acceptance mapping above distinguishes implementation evidence from automated coverage. Specification syntax is checked with the repository-pinned OpenSpec 1.14.2 `validate --all`; formatting is checked with the root formatter. This migration adds no runtime code or tests and does not claim new backend, real-token, Maven, deployment or Docker integration coverage. Runtime validation for the separate CLI compatibility change is recorded in the closeout evidence.

## Evidence links

[controller]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationController.kt
[advice]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationControllerAdvice.kt
[domain-create]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/domain/Tournament.kt
[domain-replace]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/domain/DraftReplacement.kt
[service]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/ConfigurationServiceImpl.kt
[create-use-case]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/usecase/CreateDraftService.kt
[update-use-case]: ../service/tournamentmgmt/src/main/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/usecase/UpdateDraftService.kt
[draft-tests]: ../service/tournamentmgmt/src/test/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/usecase/DraftUseCaseTest.kt
[update-tests]: ../service/tournamentmgmt/src/test/kotlin/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/internal/tournament/usecase/UpdateDraftServiceTest.kt
[web-tests]: ../service/tournamentmgmt/src/test/java/dev/wlambertz/rallyon/tournamentmgmt/setup/configuration/web/ConfigurationControllerSecurityTest.java
[iam-filter]: ../3rd_party/iam/keycloak-spring-starter/src/main/java/dev/wlambertz/rallyon/iam/keycloak/spring/KeycloakSecurityAutoConfiguration.java
[iam-principal]: ../3rd_party/iam/keycloak-core/src/main/java/dev/wlambertz/rallyon/iam/keycloak/core/KeycloakPrincipalFactory.java
[iam-config]: ../3rd_party/iam/keycloak-core/src/main/java/dev/wlambertz/rallyon/iam/keycloak/core/KeycloakConfig.java
[iam-tests]: ../3rd_party/iam/keycloak-core/src/test/java/dev/wlambertz/rallyon/iam/keycloak/core/KeycloakPrincipalFactoryTest.java
[iam-starter-tests]: ../3rd_party/iam/keycloak-spring-starter/src/test/java/dev/wlambertz/rallyon/iam/keycloak/spring/KeycloakSecurityAutoConfigurationTest.java
[exec]: ../tools/cli/ro/pkg/execx/execx.go
[root]: ../tools/cli/ro/pkg/cmd/root.go
[main]: ../tools/cli/ro/main.go
[build]: ../tools/cli/ro/pkg/cmd/build.go
[test]: ../tools/cli/ro/pkg/cmd/test.go
[run]: ../tools/cli/ro/pkg/cmd/run.go
[deploy]: ../tools/cli/ro/pkg/cmd/deploy.go
[deploy-tests]: ../tools/cli/ro/pkg/cmd/deploy_test.go
[docker]: ../tools/cli/ro/pkg/cmd/docker.go
[docker-tests]: ../tools/cli/ro/pkg/cmd/docker_test.go
[spec-registration]: ../tools/cli/ro/pkg/cmd/spec.go
[spec-create]: ../tools/cli/ro/pkg/cmd/spec_create.go
[spec-validate]: ../tools/cli/ro/pkg/cmd/spec_validate.go
[spec-view]: ../tools/cli/ro/pkg/cmd/spec_view.go
[spec-tests]: ../tools/cli/ro/pkg/cmd/spec_test.go
[config]: ../tools/cli/ro/pkg/config/config.go
