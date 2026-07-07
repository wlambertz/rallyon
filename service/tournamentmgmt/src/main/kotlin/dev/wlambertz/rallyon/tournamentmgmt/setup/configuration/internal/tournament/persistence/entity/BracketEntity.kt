package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat
import jakarta.persistence.CascadeType
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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "tournament_brackets", schema = "tournamentmgmt")
class BracketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_discipline_id", nullable = false)
    var discipline: DisciplineEntity? = null

    @Column(name = "bracket_id", nullable = false)
    var bracketId: String? = null

    @Column(name = "display_name", nullable = false)
    var displayName: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    var format: TournamentFormat? = null

    @Column(name = "capacity_amount")
    var capacityAmount: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "capacity_unit")
    var capacityUnit: Capacity.Unit? = null

    @OneToMany(mappedBy = "bracket", cascade = [CascadeType.ALL], orphanRemoval = true)
    var participants: MutableList<BracketParticipantEntity> = ArrayList()
}
