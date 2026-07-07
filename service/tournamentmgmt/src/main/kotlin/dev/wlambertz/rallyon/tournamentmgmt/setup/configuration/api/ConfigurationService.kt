package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
import java.time.Instant
import java.util.Locale
import org.springframework.stereotype.Service

@Service
interface ConfigurationService {

    // Lifecycle
    fun createDraft(organizerId: Long, name: String?, visibility: Visibility?, actingUserId: Long): Tournament

    fun updateDraft(tournamentId: Long, draftChanges: Tournament?, version: Long, actingUserId: Long): Tournament

    fun publish(tournamentId: Long, version: Long, actingUserId: Long): Tournament

    fun openRegistration(tournamentId: Long, version: Long, actingUserId: Long): Tournament

    fun closeRegistration(tournamentId: Long, version: Long, actingUserId: Long): Tournament

    fun lockConfiguration(tournamentId: Long, version: Long, actingUserId: Long): Tournament

    fun start(tournamentId: Long, version: Long, actingUserId: Long): Tournament

    fun complete(tournamentId: Long, version: Long, actingUserId: Long): Tournament

    fun cancel(tournamentId: Long, version: Long, reason: String?, actingUserId: Long): Tournament

    // Core configuration
    fun setBasics(
        tournamentId: Long,
        name: String?,
        description: String?,
        locale: Locale?,
        visibility: Visibility?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun setSchedule(
        tournamentId: Long,
        schedule: TimeWindow?,
        registrationWindows: List<TimeWindow>?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun setVenueAndCourts(
        tournamentId: Long,
        venue: Venue?,
        courts: List<Court>?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun setDisciplines(
        tournamentId: Long,
        disciplines: List<DisciplineConfig>?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun setCapacity(tournamentId: Long, capacity: Capacity?, version: Long, actingUserId: Long): Tournament

    fun setPolicies(
        tournamentId: Long,
        registrationPolicy: RegistrationPolicy?,
        schedulingPolicy: SchedulingPolicy?,
        courtAllocationPolicy: CourtAllocationPolicy?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun setRules(
        tournamentId: Long,
        scoringRules: ScoringRules?,
        tieBreakRules: TieBreakRules?,
        matchDurationPolicy: MatchDurationPolicy?,
        seedingPolicy: SeedingPolicy?,
        version: Long,
        actingUserId: Long
    ): Tournament

    // Roster
    fun setParticipantsRoster(
        tournamentId: Long,
        roster: ParticipantsRoster?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun setBracketRoster(
        tournamentId: Long,
        bracketId: BracketId?,
        roster: ParticipantsRoster?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun addParticipant(
        tournamentId: Long,
        playerId: Long?,
        teamId: Long?,
        disciplineId: Long,
        bracketId: BracketId?,
        version: Long,
        actingUserId: Long
    ): Tournament

    fun removeParticipant(
        tournamentId: Long,
        playerId: Long?,
        teamId: Long?,
        disciplineId: Long,
        bracketId: BracketId?,
        version: Long,
        actingUserId: Long
    ): Tournament

    // Phases & validation
    fun definePhases(tournamentId: Long, phases: List<Phase>?, version: Long, actingUserId: Long): Tournament

    fun validateConfiguration(tournamentId: Long)

    // Queries
    fun get(tournamentId: Long): Tournament

    fun listByOrganizer(
        organizerId: Long,
        statuses: Set<TournamentStatus>?,
        visibilityFilter: Visibility?
    ): List<Tournament>

    fun findPublic(search: String?, locale: Locale?, from: Instant?, to: Instant?): List<Tournament>
}
