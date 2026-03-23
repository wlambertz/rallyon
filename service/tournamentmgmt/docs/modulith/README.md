# Tournament Management Modulith

For `service/tournamentmgmt`, Spring Modulith documentation is generated as committed PlantUML and AsciiDoc source files.

## Generation

The files are intentionally not generated during a normal `./mvnw test`. To refresh the documentation:

```bash
cd service/tournamentmgmt
./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests
```

## Location

Generated files live under `service/tournamentmgmt/docs/modulith/generated/`.

- Overview diagram as PlantUML: `service/tournamentmgmt/docs/modulith/generated/components.puml`
- Aggregated document: `service/tournamentmgmt/docs/modulith/generated/all-docs.adoc`

In addition, Spring Modulith generates one PlantUML file `module-<module>.puml` and one AsciiDoc file `module-<module>.adoc` per discovered module.

The PlantUML files are committed directly so the architecture sources stay versioned in the main repository.

## Wiki Rendering

The committed `.puml` files in this directory are the canonical source. The wiki embeds rendered SVG copies as derived artifacts for readability.

To refresh the wiki diagrams after regenerating the `.puml` files:

```bash
./service/tournamentmgmt/docs/modulith/render-wiki-svg.sh
```

That command writes SVG files to `wiki/Architecture/generated/tournamentmgmt-svg/`.
