package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TournamentRepository : JpaRepository<TournamentEntity, Long>
