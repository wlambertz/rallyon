package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize
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
@Table(name = "tournament_disciplines", schema = "tournamentmgmt")
class DisciplineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    var tournament: TournamentEntity? = null

    @Column(name = "discipline_id", nullable = false)
    var disciplineId: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: Category? = null

    @Column(name = "display_name", nullable = false)
    var displayName: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "team_size", nullable = false)
    var teamSize: TeamSize? = null

    @OneToMany(mappedBy = "discipline", cascade = [CascadeType.ALL], orphanRemoval = true)
    var brackets: MutableList<BracketEntity> = ArrayList()
}
