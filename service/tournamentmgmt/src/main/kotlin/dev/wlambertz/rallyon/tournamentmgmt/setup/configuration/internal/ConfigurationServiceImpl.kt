package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftUseCase
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.UpdateDraftUseCase
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
import java.time.Instant
import java.util.Locale
import java.util.Objects
import org.springframework.stereotype.Service

@Service
class ConfigurationServiceImpl(
    private val createDraftUseCase: CreateDraftUseCase,
    private val updateDraftUseCase: UpdateDraftUseCase
) : ConfigurationService {

    override fun createDraft(organizerId: Long, name: String?, visibility: Visibility?, actingUserId: Long): Tournament {
        Objects.requireNonNull(name, "Tournament name must not be null")
        Objects.requireNonNull(visibility, "Visibility must not be null")
        return createDraftUseCase.execute(organizerId, name, visibility, actingUserId)
    }

    override fun updateDraft(tournamentId: Long, draftChanges: Tournament?, version: Long, actingUserId: Long): Tournament {
        Objects.requireNonNull(draftChanges, "Tournament draft changes must not be null")
        return updateDraftUseCase.execute(tournamentId, draftChanges, version, actingUserId)
    }

    override fun publish(tournamentId: Long, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun openRegistration(tournamentId: Long, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun closeRegistration(tournamentId: Long, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun lockConfiguration(tournamentId: Long, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun start(tournamentId: Long, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun complete(tournamentId: Long, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun cancel(tournamentId: Long, version: Long, reason: String?, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setBasics(
        tournamentId: Long,
        name: String?,
        description: String?,
        locale: Locale?,
        visibility: Visibility?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setSchedule(
        tournamentId: Long,
        schedule: TimeWindow?,
        registrationWindows: List<TimeWindow>?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setVenueAndCourts(
        tournamentId: Long,
        venue: Venue?,
        courts: List<Court>?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setDisciplines(
        tournamentId: Long,
        disciplines: List<DisciplineConfig>?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setCapacity(tournamentId: Long, capacity: Capacity?, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setPolicies(
        tournamentId: Long,
        registrationPolicy: RegistrationPolicy?,
        schedulingPolicy: SchedulingPolicy?,
        courtAllocationPolicy: CourtAllocationPolicy?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setRules(
        tournamentId: Long,
        scoringRules: ScoringRules?,
        tieBreakRules: TieBreakRules?,
        matchDurationPolicy: MatchDurationPolicy?,
        seedingPolicy: SeedingPolicy?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setParticipantsRoster(
        tournamentId: Long,
        roster: ParticipantsRoster?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun setBracketRoster(
        tournamentId: Long,
        bracketId: BracketId?,
        roster: ParticipantsRoster?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun addParticipant(
        tournamentId: Long,
        playerId: Long?,
        teamId: Long?,
        disciplineId: Long,
        bracketId: BracketId?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun removeParticipant(
        tournamentId: Long,
        playerId: Long?,
        teamId: Long?,
        disciplineId: Long,
        bracketId: BracketId?,
        version: Long,
        actingUserId: Long
    ): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun definePhases(tournamentId: Long, phases: List<Phase>?, version: Long, actingUserId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun validateConfiguration(tournamentId: Long) {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun get(tournamentId: Long): Tournament {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun listByOrganizer(
        organizerId: Long,
        statuses: Set<TournamentStatus>?,
        visibilityFilter: Visibility?
    ): List<Tournament> {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun findPublic(search: String?, locale: Locale?, from: Instant?, to: Instant?): List<Tournament> {
        throw UnsupportedOperationException("Not yet implemented")
    }
}
