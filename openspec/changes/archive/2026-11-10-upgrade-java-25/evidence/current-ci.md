# Current CI evidence

For PR #209 head `0f2bb86`, GitHub checks passed: [Java CI](https://github.com/wlambertz/rallyon/actions/runs/41899289468), [security scan and SBOM](https://github.com/wlambertz/rallyon/actions/runs/41899289391), [lifecycle validation](https://github.com/wlambertz/rallyon/actions/runs/41899289453), [commit lint](https://github.com/wlambertz/rallyon/actions/runs/41899289523), CodeQL and SonarCloud. The independently checked [G5 report](g5-bootstrap-final.md) records the same status.

The root `npm run format:check` remains red due to `openspec/changes/issue-203/.lifecycle.json`; the G5 checker reproduced that issue in `origin/main`. `npm run format:check:changed` passed for this change. This pre-existing failure is not reported as a passed full formatting check. Existing PostgreSQL data compatibility and human UAT remain separate from CI.
