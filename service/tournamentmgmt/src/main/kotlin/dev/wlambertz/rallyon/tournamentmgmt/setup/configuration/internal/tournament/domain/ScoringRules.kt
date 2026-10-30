package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class ScoringRules(
    val type: Type,
    val pointsPerGame: Int,
    val gamesPerMatch: Int,
    val winByTwo: Boolean,
    val capPoints: Int?
) {
    init {
        val matchesPreset = when (type) {
            Type.TWO_BY_TWENTY_ONE -> pointsPerGame == 21 && gamesPerMatch == 3 && winByTwo && capPoints == 30
            Type.THREE_BY_FIFTEEN -> pointsPerGame == 15 && gamesPerMatch == 3 && winByTwo && capPoints == 21
            Type.CUSTOM -> true
        }
        require(matchesPreset) { "Values must match preset definition for type $type" }
    }

    enum class Type {
        TWO_BY_TWENTY_ONE,
        THREE_BY_FIFTEEN,
        CUSTOM
    }
}
