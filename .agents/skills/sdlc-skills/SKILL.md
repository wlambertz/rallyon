---
name: sdlc-skills
description: Adopt and apply the full software-development-lifecycle skills catalog (personas, requirements, design, implementation, QA/testing, CI/CD, operations) in the RallyOn monorepo. Use when the task involves setting up dev workflows, agent instructions, CI pipelines, testing strategy, docs, or cross-cutting engineering practices.
---

# SDLC Skills

Curated, adoptable skills, tools, and practices supporting the whole software development lifecycle — from setting up personas and requirements through QA and CI/CD. Organized by SDLC phase so any agent (Codex, Claude, Cline) can pick the relevant subset for a task.

## Phase 1 — Discovery: Personas & Requirements

Focus: understand who the users are and what to build before writing code.

| Skill / Practice | Representative tools & artifacts |
| --- | --- |
| Persona creation & user research | User-interview templates, empathy maps, Jobs-to-be-Done (JTBD), Figma/Miro persona boards |
| Requirements elicitation & analysis | BRD, functional/non-functional requirements, user-story mapping |
| User-story writing & acceptance criteria | INVEST criteria, Given/When/Then, Definition of Ready (DoR) |
| Backlog management & prioritization | MoSCoW, RICE/WSJF scoring, Jira, Azure Boards, Trello, GitHub Issues |
| Requirement refinement & grooming | Refinement sessions, story-point / T-shirt sizing, dependency mapping |
| Requirement traceability | Traceability matrix linking requirements → design → tests → code |

## Phase 2 — Planning & Architecture

Focus: design the solution, plan delivery, set quality gates.

| Skill / Practice                         | Representative tools & artifacts                                       |
| ---------------------------------------- | ---------------------------------------------------------------------- |
| Solution & system architecture           | C4 model, ADRs, sequence/class diagrams, draw.io/Structurizr           |
| API/contract design                      | OpenAPI/Swagger, AsyncAPI, GraphQL schema design, API-first workflow   |
| Data modeling                            | ER diagrams, schema migration planning, data dictionaries              |
| SDLC model selection                     | Waterfall / Agile (Scrum/Kanban) / DevOps / V-Model fit to the project |
| Sprint & release planning                | Capacity planning, sprint goals, release trains, roadmaps              |
| Definition of Done (DoD) & quality gates | Shared DoD doc, entry/exit criteria per stage                          |
| Playbooks & process documentation        | Runbooks, onboarding guides, decision logs (kept in repo `docs/`)      |

## Phase 3 — Implementation (Coding)

Focus: write maintainable, standards-compliant code collaboratively.

| Skill / Practice | Representative tools & artifacts |
| --- | --- |
| Clean code & coding standards | Style guides (Google/Airbnb), `.editorconfig`, ESLint/Checkstyle/SpotBugs, Prettier |
| Version control & branching | Git, GitFlow / trunk-based development, conventional commits, PR templates |
| Code review | Pull-request reviews, CODEOWNERS, review checklists, approval requirements |
| Modular architecture | Package-by-feature/bounded-context, Modulith/slice annotations, interface segregation |
| Dependency & package management | Maven/Gradle/npm/pip, lockfiles, SBOM generation, vulnerability scanning |
| Local dev environment parity | Docker Compose dev services, devcontainers, `.env.example`, setup scripts |
| Logging, telemetry & observability hooks | Structured logging, correlation IDs, OpenTelemetry instrumentation |
| Pair programming / mobbing | VS Code Live Share, mobbing rotations |

## Phase 4 — Testing & QA

Focus: prevent defects early, automate regression, measure quality.

| Skill / Practice | Representative tools & artifacts |
| --- | --- |
| Test strategy & automation planning | Risk-based automation, test pyramid (unit → integration → E2E), automation goals |
| Unit testing | JUnit 5 / pytest / Jest, mocking frameworks, TDD/BDD, coverage targets |
| Integration & contract testing | Testcontainers, Pact, WireMock, API contract tests |
| UI / E2E automation | Playwright, Selenium, Cypress; Page Object Model (POM), data-driven tests |
| API testing | REST Assured, Postman/Newman, OpenAPI compliance checks |
| Performance & load testing | k6, JMeter, Gatling, Lighthouse |
| Accessibility testing | axe-core, WCAG checklists, screen-reader testing, keyboard navigation |
| Exploratory testing & bug charters | Session-based exploratory testing, bug reports with repro steps |
| Defect management | Bug triage, severity/priority, linked to requirements/tests |
| Test metrics & flaky-test handling | Execution/pass-rate trends, coverage metrics, quarantine flaky tests, weekly flake review |
| QA fundamentals | ISTQB principles, test design (equivalence partitioning, boundary value), defect lifecycle |

## Phase 5 — CI/CD & DevOps

Focus: automate build, test, and delivery; measure delivery performance.

| Skill / Practice | Representative tools & artifacts |
| --- | --- |
| CI pipeline design | GitHub Actions, GitLab CI, Jenkins, Azure Pipelines; matrix builds, caching |
| Automated test gating | Gate 1: unit + smoke (<5 min, blocks PR merge); Gate 2: full regression nightly/pre-release |
| Build & artifact management | Artifact versioning, semantic versioning, container image builds (Docker/Kaniko), artifact repos |
| CD / deployment strategies | Blue-green, canary, rolling, feature flags; Helm/Kustomize for K8s, Terraform/OpenTofu for IaC |
| Environment promotion | Dev → staging → prod, environment config separation, secrets management (Vault, AWS Secrets Manager) |
| Pipeline security scanning | SAST (CodeQL/SonarQube), SCA/dependency scanning (Snyk/Dependabot), secret scanning, container scanning |
| DORA & DevOps metrics | Deployment frequency, lead time for changes, change failure rate, MTTR; Jira/Git/Grafana dashboards |
| Incident response & rollback | Runbooks, rollback procedures, postmortems, on-call rotation |
| Infrastructure as Code | Terraform/OpenTofu, Pulumi, Ansible; state management, drift detection |

## Phase 6 — Operations, Monitoring & Maintenance

Focus: run it in production, observe health, evolve continuously.

| Skill / Practice                 | Representative tools & artifacts                                              |
| -------------------------------- | ----------------------------------------------------------------------------- |
| Observability & monitoring       | Prometheus/Grafana, ELK stack, Datadog/New Relic, alerting rules, SLOs/SLIs   |
| Logging & tracing                | Centralized logs, distributed tracing (Jaeger/Tempo), log retention           |
| Health checks & uptime           | Readiness/liveness probes, synthetic monitoring, status pages                 |
| Backup & disaster recovery       | Backup schedules, restore testing, RPO/RTO planning                           |
| Security operations              | Patch management, CVE monitoring, pen testing cadence, WAF, incident response |
| Maintenance & tech-debt tracking | Refactoring backlog, dependency upgrades, deprecation schedules               |
| Knowledge sharing & onboarding   | Architecture docs, ADRs, onboarding checklist, team wikis                     |

## Cross-Cutting Practices (apply to every phase)

- **Security by design (DevSecOps)** — threat modeling, secure coding standards, regular scanning, least-privilege.
- **Documentation as code** — `docs/` in repo, markdown, versioned alongside code, final newline, UTF-8/LF per `.editorconfig`.
- **Metrics-driven improvement** — DORA metrics, test-coverage trends, defect escape rate, cycle time.
- **Team rituals** — sprint planning, daily standups, refinement, retrospectives, blameless postmortems.

## How to adopt this skill in the repository

1. **Start small**: Pick 2–3 skills per phase most relevant to your stack (e.g., personas + user stories; clean code + PR reviews; Playwright E2E + GitHub Actions CI).
2. **Encode in repo**: Add `CONTRIBUTING.md`, `CODEOWNERS`, PR templates, a `docs/` folder with ADRs and runbooks, and CI config at the repo root.
3. **Define gates**: Write a shared Definition of Done and a two-gate CI strategy (fast PR gate + nightly regression gate).
4. **Measure**: Instrument DORA metrics and test-coverage dashboards from day one.

## Related repo artifacts (already in place)

- `.github/workflows/ci.yaml` — Java CI: Gate 1 tests + Gate 2 Modulith verification
- `.github/workflows/security-scan.yaml` — OWASP Dependency-Check + CycloneDX SBOM
- `.github/CODEOWNERS` — code ownership by bounded context
- `.github/pull_request_template.md` — PR template with test evidence & checklist
- `CONTRIBUTING.md` — dev commands, style, testing, commit/PR guidelines
- `docs/TESTING.md` — test pyramid, gates, flaky-test handling
- `docs/adr/` — Architecture Decision Records (template + `0001-use-spring-modulith.md`)
- `AGENTS.md` — nearest agent guide for each area (root + per-area)

## Sources

- Splunk SDLC overview — phases, playbooks, DORA metrics, security/coding standards
- QAPractices Test Automation Guide — strategy, Playwright/Selenium/Cypress, POM, CI/CD gating, flaky tests
- Public skills repositories (`philipz/skills`, `viktorbezdek/skillstack`) — requirements engineering, CI/CD pipeline skills
- ISTQB / DORA / OWASP — testing principles, delivery metrics, application security
