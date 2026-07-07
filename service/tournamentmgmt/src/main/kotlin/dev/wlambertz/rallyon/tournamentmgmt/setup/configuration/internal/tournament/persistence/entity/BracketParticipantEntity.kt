package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "tournament_bracket_participants", schema = "tournamentmgmt")
class BracketParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_bracket_id", nullable = false)
    var bracket: BracketEntity? = null

    @Column(name = "player_id")
    var playerId: Long? = null

    @Column(name = "team_id")
    var teamId: Long? = null

    @Column(name = "added_at", nullable = false)
    var addedAt: Instant? = null

    @Column(name = "added_by_user_id", nullable = false)
    var addedByUserId: Long = 0
}
