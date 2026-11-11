# PR 227 Sonar remediation

The SonarCloud issue API for `wlambertz_rallyon`, pull request 227, returned four open findings against `f837f47`: Python S5443 (fixed temporary executable path), two Python S1192 findings (repeated draft/intake URL suffixes), and Kotlin S107 (eight use-case parameters). Source: `https://sonarcloud.io/api/issues/search?componentKeys=wlambertz_rallyon&pullRequest=227&resolved=false&ps=100`, read 2026-11-11.

- The UAT script resolves `bin/ro` from its own repository location, replacing execution from a fixed shared temporary path. Build the unchanged CLI with `(cd tools/cli/ro && go build -o ../../../bin/ro .)` before running the walkthrough. The repository binary is ignored and is not part of the change.
- Named `draft_path` and `intake_path` resource URLs replace repeated suffix expressions.
- A framework-free `FieldPreparation` value bundles the five selection/source inputs; the use case retains separate tournament ID, expected field version and actor. Service mapping and the existing pure test helper use it without changing the public API or validation order/semantics.

No suppression, global mapper, migration, runtime configuration, public contract or requirement change is introduced. The four changed files are the script, use case, service adapter and pure-test call helper.

Actual maker validation: full backend `clean verify` on Java 25 with fresh isolated `rallyon_field227_sonar` and `rallyon_entry227_sonar` PostgreSQL databases, port 54739: **149 tests, zero failures/errors, three existing unrelated optional skips**. Both actual PostgreSQL suites executed. Log `/private/tmp/rallyon-227-sonar-verify.log`. Root formatting, strict OpenSpec validation, patch whitespace and Python AST parsing passed.

Independent G5/G6 and all three G7 delta checks passed (reports alongside this file). The corrected local Keycloak/HTTP walkthrough passed all four scenario groups on fresh database `rallyon_field227_sonar_uat`; details are in `../uat.md`. Refreshed Sonar/CI results are verified against the pushed commit. Earlier full-revision UAT acceptance is not silently reused for changed source.
