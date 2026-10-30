package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.mapping.TournamentApiMapping
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.DraftUpdateConflictException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentNotFoundException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.TournamentStore
import org.springframework.stereotype.Repository

@Repository
class JpaTournamentStore(
    private val repository: TournamentRepository,
    private val mapper: TournamentMapper
) : TournamentStore {
    override fun findById(id: Long): Tournament? = repository.findById(id).orElse(null)?.let {
        TournamentApiMapping.toDomain(mapper.toApi(it))
    }

    override fun save(tournament: Tournament, expectedVersion: Long?): Tournament {
        val api = TournamentApiMapping.toApi(tournament)
        val entity = if (expectedVersion == null) {
            mapper.toEntityForCreate(
                tournament.organizerId, tournament.name, api.visibility,
                tournament.createdByUserId, tournament.createdAt
            )
        } else {
            val existing = repository.findById(tournament.id).orElseThrow { TournamentNotFoundException(tournament.id) }
            if (existing.version != expectedVersion) {
                throw DraftUpdateConflictException(
                    "Draft version mismatch: expected ${existing.version} but got $expectedVersion"
                )
            }
            mapper.applyDraftReplacement(
                existing, api, tournament.lastModifiedByUserId, requireNotNull(tournament.lastModifiedAt)
            )
            existing.lastModifiedAt = tournament.lastModifiedAt
            existing.lastModifiedByUserId = tournament.lastModifiedByUserId
            existing
        }
        return TournamentApiMapping.toDomain(mapper.toApi(repository.save(entity)))
    }
}
