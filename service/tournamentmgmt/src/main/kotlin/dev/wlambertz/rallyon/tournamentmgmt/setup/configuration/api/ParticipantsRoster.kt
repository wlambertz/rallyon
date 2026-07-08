package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.constraints.AssertTrue

data class ParticipantsRoster(
    val playerIds: List<Long>?,
    val teamIds: List<Long>?
) {
    @AssertTrue(message = "Exactly one of playerIds or teamIds must be set")
    fun isExclusiveRoster(): Boolean = (playerIds == null) != (teamIds == null)
}
