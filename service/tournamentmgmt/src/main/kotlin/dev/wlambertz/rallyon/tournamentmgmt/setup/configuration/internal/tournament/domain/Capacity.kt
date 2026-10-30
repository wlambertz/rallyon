package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class Capacity(
    val amount: Int?,
    val unit: Unit?
) {
    fun isUnbounded(): Boolean = amount == null

    fun isUnitConsistent(): Boolean = amount == null || unit != null

    enum class Unit {
        PARTICIPANTS,
        PEOPLE
    }
}
