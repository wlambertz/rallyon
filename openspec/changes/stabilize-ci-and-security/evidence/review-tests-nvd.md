# Independent tests review: NVD revision

Reviewer: native Codex agent `/root/ci187_review_tests`, fresh context, not a maker. Reviewed source `349c0bd23aae64f0bfbae7fee6793378bd6f4ebc` against `main`, approved specification/design/tasks, root and IAM instructions, and feature-lifecycle review contract on 2026-11-03. No implementation edits made.

Outcome: **changes requested; formal G7 pending** current G5/G6 completion and required real execution evidence. This report is a bounded tests-lens review, not G5/G6 certification, UAT, or merge approval.

## Blocking finding

**P2 — A reused reports directory can certify a previous scan's JSON as current evidence** (`tools/scripts/security_scan.py:196`). `reports_available` checks only file existence. The runner does not remove or reject pre-existing engine reports before execution. I independently reproduced: create a valid input/rules fixture, seed `reports/dependency-check-report.json` with `{"oldRevision":"previous"}`, then invoke `run_scan` for revision `current` using a fake Docker executable that exits zero without writing any report and a fresh injected metadata response. Actual result: exit **0**, `reports_available: true`, and the old report remains unchanged. This defeats the explicit success-without-report guard and can mislabel evidence in repeated local scans. Normal fresh CI checkouts reduce occurrence but do not repair the runner's reusable-directory contract.

The 25-test suite misses this because its fake Docker always creates `{}`. Remove/reject old engine outputs before invocation or otherwise prove newly generated identity; add fresh-directory and reused-directory success-without-report cases, plus reused-directory failure evidence. Recheck SCA-003/SCA-007 after the fix. No real vulnerability threshold bypass was demonstrated; the defect is current-report provenance.

## Independently executed checks

| Check | Actual outcome |
| --- | --- |
| `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v` | 25 passed |
| Suppression validator on committed XML | 7 rules passed |
| Lifecycle helper unittest discovery | 32 passed |
| Claude lifecycle hook unittest discovery | 10 passed |
| Schema readiness via cached OpenSpec 1.14.2 executable | 1 passed |
| Cached OpenSpec `validate --all --strict` | 12 items passed |
| `/private/tmp/actionlint -shellcheck= .github/workflows/security-scan.yaml` | Passed; ShellCheck deliberately unavailable/disabled |
| Current prepared inventory comparison | `/private/tmp/ci187-nvd-manifest.json` exactly equals recomputed inventory/hashes for `/private/tmp/rallyon-187-nvd-runtime-inputs`, revision `349c0bd`, 145 JARs |
| Current CycloneDX comparison | All 145 prepared runtime PURLs present in existing `service/tournamentmgmt/target/bom.json`; no missing coordinate |
| Active exception counterexamples | All six actual file-path selectors match their precise positive path and reject altered JAR suffix/version or appended path; exact IAM PURL inspected; all three CPE targets reject a changed version/update field boundary |
| Embedded-resource inspection | Current Swagger UI 5.34.1 JAR contains `swagger-ui-bundle.js` |

Initial readiness invocation using its default `npx -y` launcher timed out after 60 seconds in this restricted environment. Repeating with `OPENSPEC_COMMAND='node /Users/wl/.npm/_npx/8aea7483029d0f87/node_modules/@fission-ai/openspec/bin/openspec.js'` passed in 1.069 seconds. This is a launcher/network limitation, not a failed schema assertion.

## Meaningfulness and remaining proof

Metadata tests cover exact 48-hour/five-minute boundaries, one-microsecond failures, raw-byte hashes, missing/duplicate/invalid/naive timestamps, three attempts with 20-second arguments, and an actual bounded slow response. Runner tests launch a real child process and verify exit propagation, fixed feed/API selection, no update/analyzer disabling, CVSS argument 7, warm-cache metadata failure, cleanup/redaction, path overlap and symlink boundaries. These provide meaningful offline behavior evidence. The fixture's arbitrary exit codes do not prove the real engine's CVSS scoring, feed ingestion, suppression matching or embedded-JavaScript findings.

Workflow tests are source-string assertions, supported by Actionlint and manual graph/conditions inspection. They do not simulate GitHub scheduler/cache permissions. Same-repository and fork PR events share the inspected event guard; neither is an observed current GitHub execution here. Real sibling SBOM success during scanner failure and preparation-failure downstream blocking remain execution evidence obligations.

The added eight IAM tests construct the actual configured decoder using generated RSA keys and a loopback JWKS server. They exercise valid signature, same-key-ID wrong signature, issuer, audience, expiry beyond skew, numeric-ID failures and strict mapped roles. This is materially stronger than mocking decoder success. Existing Surefire XML independently inspected: IAM 12 tests, zero failures/errors/skips; service 89 tests, zero failures/errors, one existing skip. Corresponding current maker logs show successful builds. I did not rerun Maven or Go; their full fresh execution belongs to current G6. No policy weakening or live Keycloak fixture was observed.

Historical committed cold/retry evidence is explicitly failed keyless API evidence, not successful cold/warm validation. At inspection, `/private/tmp/rallyon-187-nist-cold-system-trust-reports/execution.json` has start metadata, `reports_available: false`, provisional exit 1 and no finish timestamp; this does not establish a completed scan. I neither started another real scan nor infer its outcome. Successful/failed actual cold and compatible warm executions, engine report/input identity, real GitHub jobs/artifacts, independent G5/G6, and resolution/recheck of the finding above remain necessary before formal G7 approval. Real unresolved CVSS >=7 findings must remain blocking.
