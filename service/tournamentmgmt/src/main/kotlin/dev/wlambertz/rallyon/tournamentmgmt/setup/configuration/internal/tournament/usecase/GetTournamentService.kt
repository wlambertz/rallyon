package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentNotFoundException

class GetTournamentService(private val store: TournamentStore) : GetTournamentUseCase {
    override fun execute(tournamentId: Long): Tournament =
        store.findById(tournamentId) ?: throw TournamentNotFoundException(tournamentId)
}
