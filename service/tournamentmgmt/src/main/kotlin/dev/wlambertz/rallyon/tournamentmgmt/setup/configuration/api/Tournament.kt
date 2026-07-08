package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
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
    val courts: List<Court>? = null,

    val disciplines: List<DisciplineConfig>? = null,
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
    val bracketRosters: Map<BracketId, ParticipantsRoster>? = null,

    val status: TournamentStatus? = null,

    val createdAt: Instant? = null,
    val createdByUserId: Long = 0,
    val lastModifiedAt: Instant? = null,
    val lastModifiedByUserId: Long = 0
) {
    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }

    // Hand-written replacement for the former Lombok @Builder so Java call
    // sites and MapStruct's builder detection keep working on the Kotlin data class.
    class Builder internal constructor() {
        private var id: Long = 0
        private var version: Long? = null
        private var organizerId: Long = 0
        private var visibility: Visibility? = null
        private var name: String? = null
        private var description: String? = null
        private var locale: Locale? = null
        private var schedule: TimeWindow? = null
        private var registrationWindows: List<TimeWindow>? = null
        private var venue: Venue? = null
        private var courts: List<Court>? = null
        private var disciplines: List<DisciplineConfig>? = null
        private var capacity: Capacity? = null
        private var registrationPolicy: RegistrationPolicy? = null
        private var seedingPolicy: SeedingPolicy? = null
        private var scoringRules: ScoringRules? = null
        private var tieBreakRules: TieBreakRules? = null
        private var matchDurationPolicy: MatchDurationPolicy? = null
        private var phases: List<Phase>? = null
        private var schedulingPolicy: SchedulingPolicy? = null
        private var courtAllocationPolicy: CourtAllocationPolicy? = null
        private var participants: ParticipantsRoster? = null
        private var bracketRosters: Map<BracketId, ParticipantsRoster>? = null
        private var status: TournamentStatus? = null
        private var createdAt: Instant? = null
        private var createdByUserId: Long = 0
        private var lastModifiedAt: Instant? = null
        private var lastModifiedByUserId: Long = 0

        fun id(id: Long): Builder = apply { this.id = id }

        fun version(version: Long?): Builder = apply { this.version = version }

        fun organizerId(organizerId: Long): Builder = apply { this.organizerId = organizerId }

        fun visibility(visibility: Visibility?): Builder = apply { this.visibility = visibility }

        fun name(name: String?): Builder = apply { this.name = name }

        fun description(description: String?): Builder = apply { this.description = description }

        fun locale(locale: Locale?): Builder = apply { this.locale = locale }

        fun schedule(schedule: TimeWindow?): Builder = apply { this.schedule = schedule }

        fun registrationWindows(registrationWindows: List<TimeWindow>?): Builder =
            apply { this.registrationWindows = registrationWindows }

        fun venue(venue: Venue?): Builder = apply { this.venue = venue }

        fun courts(courts: List<Court>?): Builder = apply { this.courts = courts }

        fun disciplines(disciplines: List<DisciplineConfig>?): Builder = apply { this.disciplines = disciplines }

        fun capacity(capacity: Capacity?): Builder = apply { this.capacity = capacity }

        fun registrationPolicy(registrationPolicy: RegistrationPolicy?): Builder =
            apply { this.registrationPolicy = registrationPolicy }

        fun seedingPolicy(seedingPolicy: SeedingPolicy?): Builder = apply { this.seedingPolicy = seedingPolicy }

        fun scoringRules(scoringRules: ScoringRules?): Builder = apply { this.scoringRules = scoringRules }

        fun tieBreakRules(tieBreakRules: TieBreakRules?): Builder = apply { this.tieBreakRules = tieBreakRules }

        fun matchDurationPolicy(matchDurationPolicy: MatchDurationPolicy?): Builder =
            apply { this.matchDurationPolicy = matchDurationPolicy }

        fun phases(phases: List<Phase>?): Builder = apply { this.phases = phases }

        fun schedulingPolicy(schedulingPolicy: SchedulingPolicy?): Builder =
            apply { this.schedulingPolicy = schedulingPolicy }

        fun courtAllocationPolicy(courtAllocationPolicy: CourtAllocationPolicy?): Builder =
            apply { this.courtAllocationPolicy = courtAllocationPolicy }

        fun participants(participants: ParticipantsRoster?): Builder = apply { this.participants = participants }

        fun bracketRosters(bracketRosters: Map<BracketId, ParticipantsRoster>?): Builder =
            apply { this.bracketRosters = bracketRosters }

        fun status(status: TournamentStatus?): Builder = apply { this.status = status }

        fun createdAt(createdAt: Instant?): Builder = apply { this.createdAt = createdAt }

        fun createdByUserId(createdByUserId: Long): Builder = apply { this.createdByUserId = createdByUserId }

        fun lastModifiedAt(lastModifiedAt: Instant?): Builder = apply { this.lastModifiedAt = lastModifiedAt }

        fun lastModifiedByUserId(lastModifiedByUserId: Long): Builder =
            apply { this.lastModifiedByUserId = lastModifiedByUserId }

        fun build(): Tournament = Tournament(
            id,
            version,
            organizerId,
            visibility,
            name,
            description,
            locale,
            schedule,
            registrationWindows,
            venue,
            courts,
            disciplines,
            capacity,
            registrationPolicy,
            seedingPolicy,
            scoringRules,
            tieBreakRules,
            matchDurationPolicy,
            phases,
            schedulingPolicy,
            courtAllocationPolicy,
            participants,
            bracketRosters,
            status,
            createdAt,
            createdByUserId,
            lastModifiedAt,
            lastModifiedByUserId
        )
    }
}
