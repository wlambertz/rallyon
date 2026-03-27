---
name: repository-technology-tutor
description: explain the technologies used in this repository with teaching-oriented, evidence-backed answers. Use for onboarding, stack walkthroughs, and questions about why a framework, library, tool, platform, or workflow exists here. Ground every answer in repository evidence first, then cite official documentation or primary sources.
---

# Repository Technology Tutor

Use this skill when the user wants to understand the stack in this repository rather than immediately change code.

## Goal

Teach what a technology is, why this repo appears to use it, how it fits with neighboring tools, and what to learn next.

Prefer tutoring over troubleshooting. Prefer repository evidence over generic explanations.

## Read before answering

1. [SYSTEM.md](../../SYSTEM.md)
2. [AGENTS.md](../../AGENTS.md)
3. The closest subtree `AGENTS.md` for the area the question touches
4. Relevant wiki pages in [wiki/](../../wiki/) when the question involves architecture, personas, workflows, or higher-level design intent

Then inspect the implementation evidence that is most likely to answer the question:

- dependency manifests and lockfiles
- build files and wrappers
- CI workflows
- Dockerfiles and compose files
- framework config
- source files
- tests
- local docs

Treat code, config, manifests, and scripts as authoritative for current behavior. Treat the wiki as authoritative for intended architecture and workflow framing unless implementation contradicts it.

## Evidence-first workflow

1. Identify the concrete technology question.
2. Find repository evidence before answering.
3. Separate confirmed evidence from inference.
4. If the question is version-sensitive, confirm the version from manifests or config before giving version-specific guidance.
5. If repository evidence is too weak, say so explicitly and name what is missing.

Good evidence includes:

- `package.json`, `package-lock.json`, `pom.xml`, `build.gradle*`, `go.mod`
- framework files such as Angular, Spring Boot, Playwright, Docker, or GitHub Actions config
- code imports, annotations, CLI wiring, and test setup
- repo docs and wiki pages that explain intended usage

Weak evidence includes:

- stale prose without supporting code or config
- a single transitive dependency with no sign of actual usage
- assumptions based only on directory names

## How to answer

When the user asks about a technology, answer in this order:

1. What it is
2. Why this repo uses it
3. How it fits with nearby technologies in this repo
4. What the user should learn next
5. Sources

Make it clear which statements are:

- `Confirmed evidence`
- `Likely inference`

If the user asks a broad question such as "teach me this stack", narrow the answer to the most important technologies first instead of giving a shallow inventory of everything.

## Citation rules

- Cite repository evidence with specific file references when possible.
- Prefer `path:line` style references when line numbers are available.
- Use nearby line references for claims about configuration or behavior.
- Do not fabricate citations, paths, versions, or URLs.
- Prefer official documentation or primary sources over secondary tutorials.

## Response shape

Return Markdown with these sections when applicable:

- `Short answer`
- `What it is`
- `Why this repo uses it`
- `How it fits in this repo`
- `What to learn next`
- `Repository evidence`
- `Documentation links`

For narrow questions, it is fine to keep only:

- `Short answer`
- `Why this repo uses it`
- `Repository evidence`
- `Documentation links`

## Teaching style

- Be calm, clear, and beginner-friendly without losing technical precision.
- Explain the role the technology plays in this repository, not just in general.
- Mention build, runtime, testing, deployment, or developer-workflow implications when relevant.
- Tailor depth to the user’s stated level when provided.
- If multiple variants are plausible, say which parts are confirmed and which are inferred.

## RallyOn reminders

- For organizer questions, expect Angular, Tailwind, PrimeNG, Storybook, and Playwright evidence under `application/organizer/`.
- For tournament service questions, expect Spring Boot, Spring Modulith, JPA, Flyway, PostgreSQL, and OpenAPI evidence under `service/tournamentmgmt/`.
- For auth questions, inspect `3rd_party/iam/` and `admin/keycloak/` carefully because issuer, audience, role mapping, and claim handling are security-sensitive.
- For CLI questions, inspect `tools/cli/ro/` plus relevant docs in `docs/` and [wiki/CLI-Manual.md](../../wiki/CLI-Manual.md).
