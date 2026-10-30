package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentNotFoundException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.replaceDraft
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.validateDraftBasics
import java.time.Clock
import java.util.Objects

class UpdateDraftService(private val store: TournamentStore, private val clock: Clock) : UpdateDraftUseCase {
    override fun execute(tournamentId: Long, draftChanges: Tournament?, version: Long, actingUserId: Long): Tournament {
        val changes = Objects.requireNonNull(draftChanges, "Tournament draft changes must not be null")!!
        changes.validateDraftBasics()
        val existing = store.findById(tournamentId) ?: throw TournamentNotFoundException(tournamentId)
        return store.save(existing.replaceDraft(changes, version, actingUserId, clock.instant()), version)
    }
}
