package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Instant
import java.util.Locale

data class Tournament(
    val id: Long = 0,
    val version: Long? = null,

    val organizerId: Long = 0,
    val visibility: Visibility? = null,

    val name: String? = null,
    val description: String? = null,
    val locale: Locale? = null,

    val schedule: TimeWindow? = null,
    val registrationWindows: List<TimeWindow>? = null,
    val venue: Venue? = null,
    val courts: List<Court?>? = null,

    val disciplines: List<DisciplineConfig?>? = null,
    val capacity: Capacity? = null,

    val registrationPolicy: RegistrationPolicy? = null,
    val seedingPolicy: SeedingPolicy? = null,

    val scoringRules: ScoringRules? = null,
    val tieBreakRules: TieBreakRules? = null,
    val matchDurationPolicy: MatchDurationPolicy? = null,

    val phases: List<Phase>? = null,
    val schedulingPolicy: SchedulingPolicy? = null,
    val courtAllocationPolicy: CourtAllocationPolicy? = null,

    val participants: ParticipantsRoster? = null,
    val bracketRosters: Map<BracketId, ParticipantsRoster?>? = null,

    val status: TournamentStatus? = null,

    val createdAt: Instant? = null,
    val createdByUserId: Long = 0,
    val lastModifiedAt: Instant? = null,
    val lastModifiedByUserId: Long = 0
)

fun createDraft(organizerId: Long, name: String?, visibility: Visibility?, actor: Long, now: Instant): Tournament {
    if (name == null) {
        throw IllegalArgumentException("Tournament name must not be null")
    }
    require(name.isNotBlank()) { "Tournament name must not be blank" }
    require(name.length <= 200) { "Tournament name must be <= 200 characters" }
    val requiredVisibility = java.util.Objects.requireNonNull(visibility, "Visibility must not be null")
    return Tournament(organizerId = organizerId, name = name, visibility = requiredVisibility,
        status = TournamentStatus.DRAFT, version = 0, createdAt = now, createdByUserId = actor,
        lastModifiedAt = now, lastModifiedByUserId = actor)
}
