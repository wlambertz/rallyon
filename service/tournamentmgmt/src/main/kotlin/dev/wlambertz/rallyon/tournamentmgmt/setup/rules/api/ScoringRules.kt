package dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ScoringRules(
    @field:NotNull val type: Type,
    @field:Positive val pointsPerGame: Int,
    @field:Positive val gamesPerMatch: Int,
    val winByTwo: Boolean,
    @field:Positive val capPoints: Int?
) {
    init {
        val spec = type.presetSpec
        require(spec == null || spec.matches(pointsPerGame, gamesPerMatch, winByTwo, capPoints)) {
            "Values must match preset definition for type $type"
        }
    }

    @JsonIgnore
    @AssertTrue(message = "Cap points must exceed points per game when defined")
    fun isCapPointsConsistent(): Boolean = capPoints == null || capPoints > pointsPerGame

    internal data class PresetSpec(
        val pointsPerGame: Int,
        val gamesPerMatch: Int,
        val winByTwo: Boolean,
        val capPoints: Int?
    ) {
        fun matches(pointsPerGame: Int, gamesPerMatch: Int, winByTwo: Boolean, capPoints: Int?): Boolean =
            this.pointsPerGame == pointsPerGame &&
                this.gamesPerMatch == gamesPerMatch &&
                this.winByTwo == winByTwo &&
                this.capPoints == capPoints
    }

    enum class Type(internal val presetSpec: PresetSpec?) {
        TWO_BY_TWENTY_ONE(PresetSpec(21, 3, true, 30)),
        THREE_BY_FIFTEEN(PresetSpec(15, 3, true, 21)),
        CUSTOM(null)
    }

    companion object {
        @JvmStatic
        fun twoByTwentyOne(): ScoringRules = fromPreset(Type.TWO_BY_TWENTY_ONE)

        @JvmStatic
        fun threeByFifteen(): ScoringRules = fromPreset(Type.THREE_BY_FIFTEEN)

        @JvmStatic
        fun custom(pointsPerGame: Int, gamesPerMatch: Int, winByTwo: Boolean, capPoints: Int?): ScoringRules =
            ScoringRules(Type.CUSTOM, pointsPerGame, gamesPerMatch, winByTwo, capPoints)

        private fun fromPreset(type: Type): ScoringRules {
            val spec = requireNotNull(type.presetSpec) { "Type $type is not a preset" }
            return ScoringRules(type, spec.pointsPerGame, spec.gamesPerMatch, spec.winByTwo, spec.capPoints)
        }
    }
}
