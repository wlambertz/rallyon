# Research charter

## Question and decision it informs

For [issue #184](https://github.com/wlambertz/rallyon/issues/184), which claims in the supplied _Regel-Kompendium Badminton: Der Leitfaden für Nachwuchsathleten und Betreuer_ are reliable, applicable to a defined competition, and useful to RallyOn? Decide what belongs in a provenance-labelled information reference, what remains unverified or deferred, and which distinct gaps warrant a later feature lifecycle. The supplied guide is a secondary source, not an approved rule set.

## Primary persona and owning app/context

The Organizer is the primary product audience; the Player benefits from accurate match-day information. Tournament Management owns this research. The maintainer benefits directly from a source-traced inventory rather than implicit adoption of generated requirements. Officials retain technical and rules decisions; the research does not assign their authority to software.

The organizer app has no selected frontend stack. Scoring and Player Management remain placeholder services; the context map is intended architecture, not proof of implemented workflows. Device and connectivity assumptions in the personas remain hypotheses.

## Sources

- The complete German source, caveats and acceptance criteria in issue #184.
- Applicable primary BWF Laws of Badminton, General Competition Regulations and Instructions to Technical Officials; DBV regulations; and the cited NRW/Hövelhof briefing or venue rules where identifiable. Record authority, competition scope, edition, effective date, section and source URL. Do not substitute a different event's rules without marking the limitation.
- Existing `requirements-reconciliation` claim inventory and dispositions, the draw/seeding research associated with #185, the scheduling research in #228 and advisory change associated with #230/#231, and organizer-compliance issue #186. Reuse evidence and link overlapping claims instead of creating conflicting rule inventories.
- Current specifications in `openspec/specs/`, migration evidence in `docs/legacy-spec-migration.md`, and relevant Tournament Management source and tests. Legacy `docs/wiki/specs/` and `ro spec` commands are not the current Markdown specification workflow.
- `docs/wiki/Personas.md`, `docs/architecture/context-map.md`, relevant wiki architecture pages and accepted architecture decisions.

## Scope and exclusions

1. Subject to explicit approval of this charter's wiki edit, store the complete supplied German text verbatim at `docs/wiki/References/Badminton-Regel-Kompendium.md`, with a separate provenance banner stating its secondary, unverified and mixed-authority status. This is the only proposed wiki edit.
2. Inventory every source claim using stable IDs grouped by the guide's five sections. Retain German terms with English glosses. Classify claims as software-relevant, informational or international deferred, with separate authority and verification status.
3. Check software-relevant claims against applicable primary sources before recommending adoption. Specifically examine call and no-show sequences, warm-up timing, scoring-dependent intervals, result confirmation and scoreboard-operator responsibilities, and coach limits. Investigate conflicting one-versus-two-coach claims without selecting a universal default. Keep unsupported equipment, hall, medical, sanction and timing claims explicitly unverified or authority-specific.
4. Reconcile software-relevant claims as Covered, Conflicts, Gap or Not applicable, citing concrete current specification paths or code types. Existing published `ScoringRules` defines scoring parameters and presets; `SchedulingPolicy` and `MatchDurationPolicy` are enums. These declarations alone do not prove live calls, timers, coaching enforcement or result-confirmation workflows. Read relevant behavior and tests before claiming coverage.
5. Separate product candidates from human officiating guidance and international-only penalties. Deduplicate overlaps with #185, #186 and existing scheduling research. Record candidate requirements, unresolved ownership and potential ADR needs in findings; draft distinct follow-up issues only where supported by evidence.

No production code, tests, migrations, runtime configuration, dependencies, public REST/JWT/CLI contracts or main specifications change. No new modules, frontend, automated officiating, medical decisions, enforcement timers or universal rule defaults are introduced. Candidate requirements do not enter a change's `specs/` or `openspec/specs/`. A later approved feature lifecycle must specify accepted behavior. No live follow-up issues, commits, archive or merge are authorized by this charter.

## Deliverable and success criteria

- A verbatim source reference with provenance and authority caveats, plus source-traced findings and a recommendation under this change.
- Every source claim has a stable ID, classification, authority and verification outcome. Every software-relevant claim has a concrete coverage reference or an explicit gap/inapplicability rationale; unverified claims do not become requirements.
- The evidence distinguishes primary-source content, the secondary guide, observed current behavior and proposed behavior. Inaccessible sources, conflicting editions and unresolved applicability remain visible limitations rather than assumed rules.
- Findings identify overlaps, open decisions, potential ADRs and optional exact follow-up drafts; no duplicate issues are created for existing scope.
- Run `npm run format:check`, explicit `npx prettier --check` for every new Markdown file, and `npx -y @fission-ai/openspec@1.14.2 validate analyze-match-day-rules --strict`. No Maven check is required for documentation-only research.
- After research, a fresh independent checker verifies claim-to-source traceability, claims about current code/specs, file scope and documentation checks at G5. Human findings acceptance at G8, optional exact follow-up creation approval at G4, and separate merge approval remain required.

## Timebox or agreed budget

No timebox or numerical work budget has been agreed. Do not invent one. Escalate any proposed expansion beyond this source and its relevant overlaps, or a need for production changes, for a new scope decision.

## Track choice

Use schema `rallyon-research` and helper mode `research`: applicability, correctness and software value are uncertain, and the deliverable is knowledge rather than a capability. A reduced documentation track would incorrectly treat the content as settled; a full feature track would prematurely specify behavior. No production code, tests, migrations, runtime configuration or dependencies change.

## G1 decision

Pending explicit human approval of this charter, including the named wiki reference addition. Selecting issue #184 authorizes preparation, not scope approval. Record the actual approval quote and source in lifecycle evidence before research proceeds.
