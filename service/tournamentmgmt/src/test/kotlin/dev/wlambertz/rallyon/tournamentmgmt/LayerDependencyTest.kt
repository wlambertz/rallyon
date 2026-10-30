package dev.wlambertz.rallyon.tournamentmgmt

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LayerDependencyTest {
    private val domain = classes().that().resideInAPackage("..tournament.domain..")
        .should().onlyDependOnClassesThat().resideInAnyPackage(
            "dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain..", "java..", "kotlin..", "org.jetbrains.annotations.."
        )
    private val application = classes().that().resideInAPackage("..tournament.usecase..")
        .should().onlyDependOnClassesThat().resideInAnyPackage(
            "dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain..", "dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase..",
            "java..", "kotlin..", "org.jetbrains.annotations.."
        )
    private val api = noClasses().that().resideInAPackage("..api..")
        .should().dependOnClassesThat().resideInAnyPackage("dev.wlambertz.rallyon.tournamentmgmt..internal..", "dev.wlambertz.rallyon.tournamentmgmt..web..")
    private val web = noClasses().that().resideInAPackage("..web..")
        .should().dependOnClassesThat().resideInAnyPackage("..persistence..", "..usecase..")

    @Test
    fun `production follows the dependency rule`() {
        val imported = ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("dev.wlambertz.rallyon.tournamentmgmt")
        assertThat(imported.filter { it.packageName.contains(".tournament.domain") }).isNotEmpty()
        assertThat(imported.filter { it.packageName.contains(".tournament.usecase") }).isNotEmpty()
        listOf(domain, application, api, web).forEach { it.check(imported) }
    }

    @Test
    fun `rules reject representative forbidden dependencies`() {
        rejects(domain, architecturefixtures.tournament.domain.DomainLeak::class.java)
        rejects(application, architecturefixtures.tournament.usecase.ApplicationLeak::class.java)
        rejects(api, architecturefixtures.api.ApiLeak::class.java)
        rejects(web, architecturefixtures.web.WebLeak::class.java)
    }

    private fun rejects(rule: ArchRule, fixture: Class<*>) {
        assertThat(rule.evaluate(ClassFileImporter().importClasses(fixture)).hasViolation()).isTrue()
    }
}
