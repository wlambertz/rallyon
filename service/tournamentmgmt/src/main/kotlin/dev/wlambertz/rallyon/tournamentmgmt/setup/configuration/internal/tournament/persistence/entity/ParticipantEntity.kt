package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "tournament_participants", schema = "tournamentmgmt")
class ParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    var tournament: TournamentEntity? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    var category: Category? = null

    @Column(name = "player_id")
    var playerId: Long? = null

    @Column(name = "team_id")
    var teamId: Long? = null

    @Column(name = "added_at", nullable = false)
    var addedAt: Instant? = null

    @Column(name = "added_by_user_id", nullable = false)
    var addedByUserId: Long = 0
}
