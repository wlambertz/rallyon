package dev.wlambertz.rallyon.tournamentmgmt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import org.springframework.modulith.docs.Documenter.DiagramOptions
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships
import java.nio.file.Path

class TournamentmgmtDocumentationTests {

    companion object {
        private val OUTPUT_FOLDER = Path.of("docs", "modulith", "generated").normalize().toString()
    }

    @Test
    @EnabledIfSystemProperty(named = "modulith.docs", matches = "true")
    fun `writes modulith documentation to main repo`() {
        val modules = ApplicationModules.of(TournamentmgmtApplication::class.java)
        val diagramOptions = DiagramOptions.defaults()
            .withElementsWithoutRelationships(ElementsWithoutRelationships.VISIBLE)

        // Keep docs generation opt-in so routine test runs stay side-effect free.
        Documenter(modules, Documenter.Options.defaults().withOutputFolder(OUTPUT_FOLDER))
            .writeDocumentation(diagramOptions, Documenter.CanvasOptions.defaults())
    }
}
