package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant

@Entity
@Table(name = "tournaments", schema = "tournamentmgmt")
class TournamentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "organizer_id", nullable = false)
    var organizerId: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    var visibility: Visibility? = null

    @Column(name = "name", nullable = false, length = 200)
    var name: String? = null

    @Column(name = "description")
    var description: String? = null

    @Column(name = "locale")
    var locale: String? = null

    @Column(name = "schedule_start")
    var scheduleStart: Instant? = null

    @Column(name = "schedule_end")
    var scheduleEnd: Instant? = null

    @Column(name = "venue_name")
    var venueName: String? = null

    @Column(name = "venue_street")
    var venueStreet: String? = null

    @Column(name = "venue_postal_code")
    var venuePostalCode: String? = null

    @Column(name = "venue_city")
    var venueCity: String? = null

    @Column(name = "venue_capacity_amount")
    var venueCapacityAmount: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "venue_capacity_unit")
    var venueCapacityUnit: Capacity.Unit? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "format")
    var format: TournamentFormat? = null

    @Column(name = "capacity_max_participants")
    var capacityMaxParticipants: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "team_size")
    var teamSize: TeamSize? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_policy")
    var registrationPolicy: RegistrationPolicy? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "scheduling_policy")
    var schedulingPolicy: SchedulingPolicy? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "court_allocation_policy")
    var courtAllocationPolicy: CourtAllocationPolicy? = null

    @Column(name = "scoring_points_per_game")
    var scoringPointsPerGame: Int? = null

    @Column(name = "scoring_games_per_match")
    var scoringGamesPerMatch: Int? = null

    @Column(name = "scoring_win_by_two")
    var scoringWinByTwo: Boolean? = null

    @Column(name = "scoring_cap_points")
    var scoringCapPoints: Int? = null

    @Column(name = "tie_break_use_set_difference")
    var tieBreakUseSetDifference: Boolean? = null

    @Column(name = "tie_break_use_points_ratio")
    var tieBreakUsePointsRatio: Boolean? = null

    @Column(name = "tie_break_use_buchholz")
    var tieBreakUseBuchholz: Boolean? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "match_duration_policy")
    var matchDurationPolicy: MatchDurationPolicy? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "seeding_policy")
    var seedingPolicy: SeedingPolicy? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TournamentStatus? = null

    @Column(name = "cancel_reason")
    var cancelReason: String? = null

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null

    @Column(name = "created_by_user_id", nullable = false)
    var createdByUserId: Long = 0

    @Column(name = "last_modified_at", nullable = false)
    var lastModifiedAt: Instant? = null

    @Column(name = "last_modified_by_user_id", nullable = false)
    var lastModifiedByUserId: Long = 0

    @OneToMany(mappedBy = "tournament", cascade = [CascadeType.ALL], orphanRemoval = true)
    var registrationWindows: MutableList<RegistrationWindowEntity> = ArrayList()

    @OneToMany(mappedBy = "tournament", cascade = [CascadeType.ALL], orphanRemoval = true)
    var courts: MutableList<CourtEntity> = ArrayList()

    @OneToMany(mappedBy = "tournament", cascade = [CascadeType.ALL], orphanRemoval = true)
    var disciplines: MutableList<DisciplineEntity> = ArrayList()

    @OneToMany(mappedBy = "tournament", cascade = [CascadeType.ALL], orphanRemoval = true)
    var participants: MutableList<ParticipantEntity> = ArrayList()
}
