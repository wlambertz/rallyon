package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class ParticipantsRoster(
    val playerIds: List<Long?>?,
    val teamIds: List<Long?>?
) {
    fun isExclusiveRoster(): Boolean = (playerIds == null) != (teamIds == null)
}
