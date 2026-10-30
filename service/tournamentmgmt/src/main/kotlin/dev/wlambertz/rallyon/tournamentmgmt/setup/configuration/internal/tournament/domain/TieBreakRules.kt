package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class TieBreakRules(
    val type: Type,
    val useSetDifference: Boolean,
    val usePointsRatio: Boolean,
    val useBuchholz: Boolean
) {
    init {
        val matchesPreset = when (type) {
            Type.HEAD_TO_HEAD -> useSetDifference && !usePointsRatio && !useBuchholz
            Type.POINTS_RATIO -> !useSetDifference && usePointsRatio && !useBuchholz
            Type.SWISS_STRENGTH -> useSetDifference && usePointsRatio && useBuchholz
            Type.CUSTOM -> true
        }
        require(matchesPreset) { "Values must match preset definition for type $type" }
    }

    enum class Type {
        HEAD_TO_HEAD,
        POINTS_RATIO,
        SWISS_STRENGTH,
        CUSTOM
    }
}
