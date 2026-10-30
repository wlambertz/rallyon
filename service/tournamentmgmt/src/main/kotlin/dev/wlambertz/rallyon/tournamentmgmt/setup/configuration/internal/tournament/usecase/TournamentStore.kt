package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament

interface TournamentStore {
    fun findById(id: Long): Tournament?
    fun save(tournament: Tournament, expectedVersion: Long?): Tournament
}
