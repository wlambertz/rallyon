# G7 independent tests review: NVD-primary PR 1

Reviewer: native Codex agent `/root/ci187_review_tests`, independent of every implementation maker. Date: 2026-11-03. **PASS for the G7 tests lens** against frozen fingerprint `280121769094e9cd01f429cb2a855fb0dacc2d6448454e781c2ff88d8a7a30c3`. No unresolved blocking test finding remains within approved PR 1 scope. This grants no human UAT, merge approval, all-checks-green claim or completion of issue #187.

## Inputs, independence and resolved findings

Implementation HEAD is `93e491b1b08b708c4e00ddecbacbc09a19a164c8`; current frozen documentation/index additions are included in the fingerprint above. Independently computed `Session.fingerprint('G7')` with the actual lifecycle helper and successfully executed `Session.require('G6')`, confirming preceding gate records and report integrity are current. Read the final [G5](g5-nvd-completion.md), [G6](g6-nvd-verification.md), approved specification/design/tasks, exact ten-rule policy review and changed policy/results documents. Earlier review context already inspected root/IAM instructions, full implementation diff, workflow and real-decoder tests.

[Round 1](review-tests-nvd.md) found stale engine reports accepted as current. [Round 2](review-tests-nvd-round2.md) independently verified report cleanup and nonzero post-success exception handling, including real temporary child-process counterexamples. Both findings remain resolved. Current runner/test SHA-256 identities still equal the round-2 reviewed values:

- Runner: `01b168ca40e093b85f293cbbebd6217d787f34ed0e8709e057f98f158d912cbc`.
- Security tests: `317dffb8a8909d52d700114a8e707647a588bf23cc89e4f34edfe883373bd2f1`.

No implementation or gate records were edited by this reviewer. Only review reports were authored.

## Actual independent refresh

- Reran `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v`: **27 passed** in 1.130 seconds.
- Suppression validator: **10 rules passed**. Independently replayed all **74** current-policy positive/negative/boundary assertions and **20** selector/target bindings to actual XML. Exact identities match; changed versions/groups/paths and unrelated findings remain outside the exceptions. The dedicated independent [applicability review](review-new-finding-policy.md) supplies manufacturer/engine evidence rather than treating fixtures as vendor authority.
- Independently inspected downloaded raw run metadata and engine/SBOM artifacts under `/private/tmp/rallyon-187-g6-nvd/`. [Actual run 41623095316](https://github.com/wlambertz/rallyon/actions/runs/41623095316) and all three jobs completed successfully. Source head is `93e491b…`; artifact revision is the tested PR merge `7d5db89fc8a98746e3df4afc47bb2f3deededb1a`.
- Raw CI report SHA-256 independently computed as `b09a201f5440e59a232d350c809ffe9d427084f9a83387d43048601cbd2cbc90`: **162 entries, nine JavaScript entries, zero active vulnerability instances, 15 suppressed instances**. Each of the **145** prepared JAR paths and SHA-256 values appears in the raw report. Prepared inventory downloaded with the SBOM is identical to the scan inventory; own coordinate comparison finds all 145 runtime PURLs in CycloneDX, with none missing.
- Inspected actual CI execution metadata: exit 0, generated reports available, official pinned 13.0.0 image, fixed NIST JSON 2 feed, fresh official metadata date/hash and 601-second execution. Raw scanner log shows historical-year feed downloads and initialization, supporting the cold-data result; cache-miss observation is additionally recorded by G6. This is actual engine evidence, distinct from fake-process tests.
- Independently inspected G6's separate real warm execution/report: exit **0**, raw report hash `e9955e953a23957382c758426a6e9aa4addcded0fe429acab23de4d92076a9e7`, 162 entries and zero active instances. This reviewer inspected that execution rather than claiming to have run it. Earlier failed cold/warm results remain preserved; successful revised-policy runs do not rewrite them.
- `git diff --check` passed. Prior independent round-1 lifecycle/hook/readiness/OpenSpec/Actionlint checks remain applicable to unchanged workflow/helper/schema code; full current runtime execution is independently recorded by G6.

## Coverage assessment

| Requirement | Test/evidence assessment |
| --- | --- |
| SCA-001 | Generated RSA/local JWKS fixtures exercise the real configured decoder's valid, wrong-signature, issuer, audience and expired-token paths, followed by strict numeric-ID/role conversion. G6 independently reran IAM 12 tests and service 89 discovered/88 passed/one existing skip, plus full CLI tests and packaged dependency inspection. No auth policy relaxation was used. |
| SCA-002 | Exact-image, hash/revision and active-analyzer assertions are backed by actual report input hashes and nine analyzed embedded JS entries. Complete Action SHA pins and Actionlint complement workflow inspection. |
| SCA-003 | Observable child-process nonzero propagation, absent reports, stale-report rejection and post-scan verification exceptions are tested. CVSS remains 7; actual historical exit-15 finding evidence and final exit-0 results supplement the simulated exit codes. Infrastructure/metadata failures remain red. |
| SCA-004 | Exact ten-rule assertions, expiry/unsafe-rule tests, independent primary-source applicability review and raw actual suppressed-match records establish narrow corrections. Baseline 82 instances/13 entries/64 IDs and later observation inventory remain separately traceable in G5/G6. Tests do not replace source applicability review. |
| SCA-005 | Exact freshness boundaries, invalid/duplicate/missing/naive dates, bounded attempts/read deadline, fixed mode selection, warm-cache failure, event trust, secret redaction/retention cleanup and filesystem defenses have meaningful negative tests. Actual cold/warm keyless runs supplement the simulations. |
| SCA-006 | Sibling job dependencies and always-upload behavior have static/Actionlint coverage plus observed successful independent SBOM artifacts. Historical real red-scan/SBOM-success evidence remains retained. Preparation-failure blocking is source/fixture evidence, not an induced live GitHub run. |
| SCA-007 | Current independent G5/G6 records, these review rounds and actual local/GitHub evidence meet the tests-lens delivery requirement. Human G8 and separate merge approval remain outstanding; #187 still requires PR 2/final main verification. |

## Limits that remain visible

The separate **Sonar S8707 red quality-gate result still requires external disposition**; security-workflow success does not establish every PR check is green. No Sonar finding or remote status was changed here.

Upstream **OSS Index coverage is unavailable without its separate credentials**. The default engine was not disabled; required NVD and RetireJS analysis completed. Zero active findings describes these actual reports at their data/policy snapshot, not comprehensive vulnerability absence or live OSS Index coverage.

No real optional-NVD-key scan, malicious-fork run, trusted default-branch cache save or deliberate GitHub preparation failure was executed in this review. Their trust/secret/failure paths rely on inspected source and isolated fixtures. ShellCheck was unavailable; Actionlint ran without it. One existing opt-in documentation test remains skipped. H2/MockMvc/loopback JWKS checks do not establish deployed PostgreSQL/Flyway, live Keycloak or end-to-end production behavior. This final refresh did not duplicate G6's Maven/Go or real scanner executions; it independently inspected their current results and reran the affected security suite/policy counterexamples.
