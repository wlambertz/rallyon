# QA traceability at G6

Provide a fresh checker with the requirements/scenarios, approved issue acceptance criteria, implementation diff, relevant runtime instructions and actual maker results. Record a mapping from each scenario or criterion to automated test or manual evidence; identify uncovered items explicitly.

Run the applicable commands from the nearest `AGENTS.md`. Record command, result, environment and limitations, including skipped checks. Verify rejection/failure scenarios and security cases where applicable. H2 tests do not prove PostgreSQL/Flyway behavior, MockMvc is not deployed E2E, and a placeholder UI has no automated browser coverage.

Write evidence to `verification.md` or a separate finalized report under the change's `evidence/` directory. In reduced mode, keep `lifecycle.md` as the planning/omission record and use a separate checker report. Record a helper G6 check only after the independent work occurs. Failed tests remain failed evidence; fix the cause and rerun affected checks before passing. Content changes invalidate downstream review and UAT evidence.

Keep the report within the skill's [token budget](../SKILL.md#token-budget): verdict and findings first, no restated inputs, delta-only rechecks.
