package dev.wlambertz.rallyon.tournamentmgmt;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships;

class TournamentmgmtDocumentationTests {

  private static final String OUTPUT_FOLDER = Path.of("docs", "modulith", "generated")
      .normalize()
      .toString();

  @Test
  @EnabledIfSystemProperty(named = "modulith.docs", matches = "true")
  void writesModulithDocumentationToMainRepo() {
    ApplicationModules modules = ApplicationModules.of(TournamentmgmtApplication.class);
    DiagramOptions diagramOptions = DiagramOptions.defaults()
        .withElementsWithoutRelationships(ElementsWithoutRelationships.VISIBLE);

    // Keep docs generation opt-in so routine test runs stay side-effect free.
    new Documenter(modules, Documenter.Options.defaults().withOutputFolder(OUTPUT_FOLDER))
        .writeDocumentation(diagramOptions, Documenter.CanvasOptions.defaults());
  }
}
