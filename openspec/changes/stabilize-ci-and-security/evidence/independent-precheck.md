# Independent issue #187 PR 1 precheck

Reviewed the in-progress worktree `/private/tmp/rallyon-issue-187-security` on 2026-11-03. This is a defect-focused precheck, not frozen G5/G7 evidence or an approval. I was not a maker and changed no production or approval files.

## Blocking findings

### P1: Three CPE corrections cannot match Dependency-Check 13 CPE identifiers

Location: `.github/dependency-check-suppressions.xml:6`, `:11`, `:16`; related permissive syntax check at `tools/scripts/validate_dependency_suppressions.py:55`.

All three CPE correction rules use literal `cpe:2.3:...` targets. Dependency-Check 13.0.0 handles a normal CpeIdentifier by converting it with `cpe.toCpe22Uri()`, then prefix-matching that CPE 2.2 URI against the literal rule. A candidate beginning `cpe:/a:` cannot start with `cpe:2.3:`. SuppressionHandler preserves rule text without normalization. Therefore the intended own-IAM and Micrometer product corrections remain unused and their false-positive findings remain visible/blocking after feed operation is repaired. The CVE-only Spring rules do not have this particular defect.

Authoritative implementation: [SuppressionRule.java lines 671–691](https://github.com/dependency-check/DependencyCheck/blob/v13.0.0/core/src/main/java/org/owasp/dependencycheck/xml/suppression/SuppressionRule.java#L671), [SuppressionHandler.java lines 271–284](https://github.com/dependency-check/DependencyCheck/blob/v13.0.0/core/src/main/java/org/owasp/dependencycheck/xml/suppression/SuppressionHandler.java#L271).

Reproduction: parse the actual XML into the real engine and apply its first/second/third rule to a Dependency with the corresponding valid CpeIdentifier and matching package/path identity; `isMatched()` stays false. At the decisive comparison, `"cpe:/a:prometheus:prometheus:1.17.2".startswith("cpe:2.3:a:prometheus:prometheus:1.17.2:*:*:*:*:*:*:*")` is false. This conclusion follows directly from the pinned engine source; I could not execute a direct Java/Docker probe because my sandbox denied the Docker socket.

Use engine-compatible CPE 2.2 selectors with explicitly reviewed whole-part/version bounds, and add an actual engine matcher regression rather than another Python equality model. Preserve the exact artifact selectors and unrelated-finding counterexamples.

### P1: Actual keyless cold execution fails before analysis

Location: `tools/scripts/security_scan.py:85–89` and pinned image at `:16`.

The coordinator supplied the actual cold run; I independently read `/private/tmp/rallyon-187-cold-reports/scanner.log`. It contains an NVD update exception, `Invalid API Key, length of 0 too short to provided a masked partial key`, followed by `Unable to continue dependency-check analysis` and `No documents exist`. Thus the required keyless cold path is currently unfulfilled. This is a known upstream/image compatibility blocker, not permission to inject a mandatory secret, disable updates or manufacture green status. The approved design already requires a human design decision for this outcome.

Potential amendment research: the exact 13.0.0 [NvdApiDataSource](https://github.com/dependency-check/DependencyCheck/blob/v13.0.0/core/src/main/java/org/owasp/dependencycheck/data/update/NvdApiDataSource.java#L551) supports a configured JSON datafeed and metadata fallback when cache.properties is unavailable. An official NVD JSON 2 feed can therefore be evaluated as an approval option, with actual cold/warm and active-update evidence; source inspection alone does not prove successful operation.

## Checks and boundaries

- `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v`: all 14 tests passed. They simulate Docker and do not cover real CPE matcher behavior.
- Suppression validator: seven current rules accepted.
- Local prepared inventory versus existing service CycloneDX JSON: 145 runtime coordinates compared, none missing. This is coordinate coverage, not byte identity or proof of a GitHub run.
- Workflow graph: scanner and SBOM are independent siblings requiring prepare. Failure propagation, always-upload paths, pinned Action references, default-branch key/cache save conditions and PR key exclusion are structurally present.
- Manufacturer CVE-2018-1258 advisory confirms the Framework 5.0.5-only applicability; the Spring suppression rationale is consistent with that primary source.
- No new concrete SBOM graph or secret/cache boundary defect found in this precheck. Real GitHub outcomes and real cold/warm engine/report semantics remain required acceptance evidence.

## Follow-up engine probe

Checker `/root/ci187_precheck` confirmed against the cached real DC13 classes with network disabled: `cpe:/a:prometheus:prometheus:1.17.2` matches the corrected colon-terminated CPE2.2 rule; versions `1.17.20` and `1.17.2-extra` do not. Original CPE2.3 rule does not match. The engine deliberately appends a field separator for this prefix comparison. This follow-up resolves the rule-format blocker; the real keyless initialization blocker remains. This precheck is not a formal frozen-source G5/G7 certification.
