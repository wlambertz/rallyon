# G6 — Sonar remediation delta QA

**Verdict: PASS.** Independent checker `sonar-qa`, 2026-11-11. No blocking finding. This delta recheck supplements [prior QA](g6-qa.md) and follows the passing [G5 delta](g5-sonar-delta.md).

| Changed area | Independent trace |
| --- | --- |
| Pure preparation input and service mapping | Inspected all five nullable values, their original ordering and separate tournament/version/actor arguments. No validation branch or persistence operation changed. Twelve actual pure tests still cover creator authorization, explicit single/pair subsets, invalid input, source/field versions, retained snapshots, clearing and independent fields. |
| Controller boundary | Fifteen actual MockMvc tests still pass, including independent versions, strict numeric tokens/array shapes, sanitized private failures, identity rejection and successful retained reads. The public DTO/controller is unchanged. |
| UAT script executable and URLs | Independently parsed the script and checked that `parents[4]` is this repository root. The executable uses an argument list without a shell. Draft/intake URLs preserve the former expressions; workflow expectations and assertions are unchanged. Actual authenticated HTTP execution is separate UAT evidence. |

## Independent execution

```sh
env JAVA_HOME=/private/tmp/rallyon-jdk25/Contents/Home \
  ./service/tournamentmgmt/mvnw -B -f service/tournamentmgmt/pom.xml \
  '-Dtest=ParticipantFieldUseCaseTest,ParticipantFieldControllerSecurityTest' test
```

**27 tests, zero failures/errors/skips; BUILD SUCCESS.** Pure/controller counts: 12/15. Log: `/private/tmp/rallyon-227-sonar-g6.log`. An initial sandbox attempt could not write the standard Kotlin daemon cache; the successful rerun used escalation. Maven finished; shared target is idle.

Read the complete four-file staged delta against `f837f47`; SHA-256 `ae2d96e77f6c9a619756032ed75897e21ae7d1110b4539bddd3b8a6938aa5635`. `git diff --cached --check`, Python AST parsing and repository path assertion passed. Inspected the maker full-verification log `/private/tmp/rallyon-227-sonar-verify.log`: 149 tests, zero failures/errors, three optional skips; both actual participant-field and entry-book PostgreSQL suites executed.

## Limits

This checker reran the affected pure/controller suites, not full verification or PostgreSQL suites. Unchanged storage/concurrency scenarios retain the detailed prior independent QA trace and are also covered by the inspected current maker PostgreSQL run. MockMvc uses mocked service/JWT decoding; it does not prove authenticated deployed HTTP. No new UAT acceptance or current Sonar/CI success is claimed. No production files, tests or lifecycle gates were changed by this checker.
