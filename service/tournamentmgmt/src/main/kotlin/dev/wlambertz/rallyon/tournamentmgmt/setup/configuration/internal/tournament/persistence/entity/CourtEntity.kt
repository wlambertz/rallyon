package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court
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

@Entity
@Table(name = "tournament_courts", schema = "tournamentmgmt")
class CourtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    var tournament: TournamentEntity? = null

    @Column(name = "source_court_id")
    var sourceCourtId: Long? = null

    @Column(name = "label", nullable = false)
    var label: String? = null

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Short = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    var availability: Court.Availability? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: Court.Type? = null
}
