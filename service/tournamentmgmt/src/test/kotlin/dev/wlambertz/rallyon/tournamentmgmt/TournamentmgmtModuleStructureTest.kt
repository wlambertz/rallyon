package dev.wlambertz.rallyon.tournamentmgmt

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.ConfigurationServiceImpl
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftUseCase
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.PhasesService
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.RuleService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class TournamentmgmtModuleStructureTest {

    @Test
    fun `verifies nested setup modules and exposed interfaces`() {
        val modules = ApplicationModules.of(TournamentmgmtApplication::class.java)

        val configurationModule = modules.getModuleByName("setup.configuration").orElseThrow()
        val rulesModule = modules.getModuleByName("setup.rules").orElseThrow()
        val phasesModule = modules.getModuleByName("setup.phases").orElseThrow()

        assertThat(modules.getModuleByType(ConfigurationService::class.java)).contains(configurationModule)
        assertThat(modules.getModuleByType(RuleService::class.java)).contains(rulesModule)
        assertThat(modules.getModuleByType(PhasesService::class.java)).contains(phasesModule)

        assertThatCode { modules.verify() }.doesNotThrowAnyException()

        assertThat(configurationModule.isExposed(ConfigurationService::class.java)).isTrue()
        assertThat(rulesModule.isExposed(RuleService::class.java)).isTrue()
        assertThat(phasesModule.isExposed(PhasesService::class.java)).isTrue()

        assertThat(configurationModule.isExposed(ConfigurationServiceImpl::class.java)).isFalse()
        assertThat(configurationModule.isExposed(CreateDraftUseCase::class.java)).isFalse()
        assertThat(configurationModule.isExposed(TournamentRepository::class.java)).isFalse()
    }
}
