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
@Table(name = "tournament_registration_windows", schema = "tournamentmgmt")
class RegistrationWindowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    var tournament: TournamentEntity? = null

    @Column(name = "window_index", nullable = false)
    var windowIndex: Short = 0

    @Column(name = "registration_starts_at", nullable = false)
    var registrationStartsAt: Instant? = null

    @Column(name = "registration_ends_at", nullable = false)
    var registrationEndsAt: Instant? = null
}
