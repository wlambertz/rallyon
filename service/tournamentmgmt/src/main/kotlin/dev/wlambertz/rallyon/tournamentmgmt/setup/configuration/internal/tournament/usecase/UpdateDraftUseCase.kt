package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament

interface UpdateDraftUseCase {

    fun execute(tournamentId: Long, draftChanges: Tournament?, version: Long, actingUserId: Long): Tournament
}
