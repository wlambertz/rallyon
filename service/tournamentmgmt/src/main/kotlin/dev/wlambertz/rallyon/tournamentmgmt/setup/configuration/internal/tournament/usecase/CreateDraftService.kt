package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.createDraft
import java.time.Clock

class CreateDraftService(private val store: TournamentStore, private val clock: Clock) : CreateDraftUseCase {
    override fun execute(organizerId: Long, name: String?, visibility: Visibility?, actingUserId: Long): Tournament =
        store.save(createDraft(organizerId, name, visibility, actingUserId, clock.instant()), null)
}
