package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Visibility

interface CreateDraftUseCase {
    fun execute(organizerId: Long, name: String?, visibility: Visibility?, actingUserId: Long): Tournament
}
