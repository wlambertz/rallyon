package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftUseCase
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.TournamentStore
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.UpdateDraftService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.UpdateDraftUseCase
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class TournamentUseCaseConfiguration {
    @Bean
    fun tournamentClock(): Clock = Clock.systemUTC()

    @Bean
    fun createDraftUseCase(store: TournamentStore, tournamentClock: Clock): CreateDraftUseCase =
        CreateDraftService(store, tournamentClock)

    @Bean
    fun updateDraftUseCase(store: TournamentStore, tournamentClock: Clock): UpdateDraftUseCase =
        UpdateDraftService(store, tournamentClock)
}
