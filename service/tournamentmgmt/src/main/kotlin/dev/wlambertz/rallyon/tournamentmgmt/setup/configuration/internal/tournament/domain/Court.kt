package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class Court(
    val id: Long,
    val label: String?,
    val availability: Availability?,
    val type: Type?
) {
    enum class Availability {
        AVAILABLE,
        IN_USE,
        UNAVAILABLE
    }

    enum class Type {
        STANDARD,
        SINGLES_ONLY
    }
}
