package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository
import java.time.Instant
import java.util.Objects
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
internal class UpdateDraftService(
    private val tournamentRepository: TournamentRepository,
    private val tournamentMapper: TournamentMapper
) : UpdateDraftUseCase {

    override fun execute(tournamentId: Long, draftChanges: Tournament?, version: Long, actingUserId: Long): Tournament {
        val requiredDraftChanges = Objects.requireNonNull<Tournament>(
            draftChanges,
            "Tournament draft changes must not be null"
        )
        validateDraftChanges(requiredDraftChanges)

        val entity = tournamentRepository.findById(tournamentId)
            .orElseThrow { TournamentNotFoundException(tournamentId) }

        if (entity.status != TournamentStatus.DRAFT) {
            throw DraftUpdateConflictException("Only tournaments in DRAFT status can be updated")
        }
        val currentVersion = entity.version
        if (currentVersion == null || currentVersion != version) {
            throw DraftUpdateConflictException("Draft version mismatch: expected $currentVersion but got $version")
        }

        val now = Instant.now()
        entity.lastModifiedAt = now
        entity.lastModifiedByUserId = actingUserId
        tournamentMapper.applyDraftReplacement(entity, requiredDraftChanges, actingUserId, now)

        return tournamentMapper.toApi(tournamentRepository.save(entity))
    }
}

private fun validateDraftChanges(draftChanges: Tournament) {
    validateDraftName(draftChanges.name)
    if (draftChanges.visibility == null) {
        throw InvalidDraftUpdateException("Visibility must not be null")
    }
    val phases = draftChanges.phases
    if (phases != null && phases.isNotEmpty()) {
        throw InvalidDraftUpdateException("Phases are not supported by draft updates yet")
    }
}

private fun validateDraftName(name: String?) {
    if (name == null) {
        throw InvalidDraftUpdateException("Tournament name must not be null")
    }
    if (name.isBlank()) {
        throw InvalidDraftUpdateException("Tournament name must not be blank")
    }
    if (name.length > 200) {
        throw InvalidDraftUpdateException("Tournament name must be <= 200 characters")
    }
}
