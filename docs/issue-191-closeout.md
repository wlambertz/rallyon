# Issue #191 closeout evidence

## Reduced workflow and authorization

Primary user: the RallyOn maintainer. This change migrates existing behavior documentation and corrects the legacy CLI grouping/deprecation contract; it adds no backend feature, auth policy, dependency, migration or module. It uses the reduced issue/PR record permitted by `docs/feature-lifecycle-gates.md`, rather than a new full feature or numerical value exercise. Existing issue #191 is reused; there is no new issue payload or issue creation. Existing Phase C evidence is preserved unchanged.

The actual conversation decisions were “Beide Wege erhalten (Recommended)” (support both grouped and historical root commands) and “Standard entfernen (Recommended)” (remove automatic schema URL; preserve explicit configuration). The maintainer then supplied the complete concrete plan under “PLEASE IMPLEMENT THIS PLAN:” and requested implementation. That supplied plan is the scope/specification and continued-implementation authorization for this reduced record; no new approval is inferred from silence. A separate numerical G3 score is not applicable to this bounded compatibility/documentation closeout.

G1/G2 scope and go/no-go: authorized by that explicit supplied implementation plan. G4: no issue creation; continuation authorized by the same plan. G5/G6/G7 independent checks: passed as recorded below. G8: pending actual human documentation/CLI walkthrough acceptance. Merge: pending separate human approval. Closing #191 and ticking its final GitHub checklists happen only after acceptance and merge.

## Acceptance criteria

- All nine historical YAML specifications and every legacy acceptance condition map to current Markdown requirements/scenarios with source evidence and explained corrections.
- Existing feature-lifecycle and tournament-configuration-read specifications remain byte-identical.
- New grouped and preserved root CLI routes share handlers, print one stderr notice, preserve ordinary output/errors, and remain visible in help/reference docs.
- Automatic schema URL defaults/fallbacks are removed; explicit configuration remains supported.
- Replacements are validated before removing originals in a separate commit. Active instructions/documentation point to current specifications; historical evidence remains frozen.

## Validation and review

The replacements were committed first in `845fcb0`; legacy CLI compatibility and regenerated docs were committed in `4343d10`. The nine YAML originals are removed only in the subsequent retirement commit after successful replacement validation.

Independent CLI checker `/root/legacy_cli_check` found no functional findings. Its full `GOCACHE=/private/tmp/rallyon-191-go-cache go test ./...` passed with escalation for existing local HTTP test listeners. It independently built and exercised the actual CLI root and grouped routes using discovered `ro.yaml`, auth templates, absent/explicit schema configuration, argument/flag/file errors, all six inherited root-config error paths and all six leaf help routes. Generated reference comparison matched live generator output apart from its timestamp. No files were edited by the checker, and no live external/auth/deploy operations were invoked. Durable regression-test limitation: committed tests construct a synthetic Cobra root, while actual inherited root behavior was independently covered by the built-binary smoke run.

Independent migration checker `/root/legacy_migration_check` found no findings. It read all nine original YAMLs and replacements, confirmed all 25 acceptance conditions map to 25 inventory rows, checked 43 local Markdown links, and ran cached OpenSpec 1.14.2 `validate --all`: 11 passed, zero failed. The two existing specifications were byte-identical to `origin/main`. Its review distinguishes code inspection and mocked tests from real-runtime evidence. An initial npx validation attempt stalled on network access; the pinned cached CLI succeeded. No backend/live-token/deploy/Docker operation was performed.

Maker validation: full Go tests passed; the built-CLI walkthrough passed nine executions and six help checks; OpenSpec validation passed 11 specifications; formatting, active local Markdown links and Git whitespace checks passed. The schema and lifecycle tooling are unchanged. Backend verification is not applicable: no backend/IAM source changes are in scope. CLI unit tests use temporary files and fake external operations; no deployments, Docker pushes or live auth requests are permitted. CI must report the actual dependency-scan outcome; issue #187 owns dependency remediation, and this change supplies no suppression or gate bypass.

## Human walkthrough

The [recorded local walkthrough](issue-191-cli-walkthrough.json) ran against a CLI built from this branch, using only temporary files and telemetry disabled. The exact token-free commands and stdout are recorded there, with temporary paths normalized.

| Scenario | Observed result |
| --- | --- |
| Root create → validate → view | All exit 0; generated YAML accepted and raw contents displayed |
| Grouped spec create → validate → view | All exit 0; same legacy behavior |
| Missing file via both view routes | Both exit 1 with their existing file error |
| Each of those eight invocations | Exactly one common deprecation notice on stderr |
| Creation without schemaURL | No Schema line |
| Grouped creation with explicit schemaURL | Exit 0; supplied Schema line retained, exactly one stderr notice |
| All six leaf help routes | Exit 0; OpenSpec 1.14.2 pointer visible in help, no stderr warning for help |

Review [the migration inventory](legacy-spec-migration.md), especially corrected creation/date assumptions, full draft replacement and unsupported blanket ADMIN authorization claims. Existing lifecycle and retrieval requirements remain unchanged.

G8 remains pending the maintainer's explicit acceptance of this documentation and CLI walkthrough. Automated success and checker reports do not establish human acceptance. Separate merge approval remains pending; #191 is not closed by implementation alone.
