package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository
import java.time.Instant
import java.util.Objects
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
internal class CreateDraftService(
    private val tournamentRepository: TournamentRepository,
    private val tournamentMapper: TournamentMapper
) : CreateDraftUseCase {

    override fun execute(organizerId: Long, name: String?, visibility: Visibility?, actingUserId: Long): Tournament {
        val requiredName = validateName(name)
        val requiredVisibility = Objects.requireNonNull(visibility, "Visibility must not be null")

        val now = Instant.now()
        val entity = tournamentMapper.toEntityForCreate(
            organizerId,
            requiredName,
            requiredVisibility,
            actingUserId,
            now
        )
        return tournamentMapper.toApi(tournamentRepository.save(entity))
    }
}

private fun validateName(name: String?): String {
    if (name == null) {
        throw IllegalArgumentException("Tournament name must not be null")
    }
    if (name.isBlank()) {
        throw IllegalArgumentException("Tournament name must not be blank")
    }
    if (name.length > 200) {
        throw IllegalArgumentException("Tournament name must be <= 200 characters")
    }
    return name
}
