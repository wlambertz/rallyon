package dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api

import jakarta.validation.constraints.NotNull

data class TieBreakRules(
    @field:NotNull val type: Type,
    val useSetDifference: Boolean,
    val usePointsRatio: Boolean,
    val useBuchholz: Boolean
) {
    init {
        val spec = type.presetSpec
        require(spec == null || spec.matches(useSetDifference, usePointsRatio, useBuchholz)) {
            "Values must match preset definition for type $type"
        }
    }

    internal data class PresetSpec(
        val useSetDifference: Boolean,
        val usePointsRatio: Boolean,
        val useBuchholz: Boolean
    ) {
        fun matches(useSetDifference: Boolean, usePointsRatio: Boolean, useBuchholz: Boolean): Boolean =
            this.useSetDifference == useSetDifference &&
                this.usePointsRatio == usePointsRatio &&
                this.useBuchholz == useBuchholz
    }

    enum class Type(internal val presetSpec: PresetSpec?) {
        HEAD_TO_HEAD(PresetSpec(true, false, false)),
        POINTS_RATIO(PresetSpec(false, true, false)),
        SWISS_STRENGTH(PresetSpec(true, true, true)),
        CUSTOM(null)
    }

    companion object {
        @JvmStatic
        fun headToHead(): TieBreakRules = fromPreset(Type.HEAD_TO_HEAD)

        @JvmStatic
        fun pointsRatio(): TieBreakRules = fromPreset(Type.POINTS_RATIO)

        @JvmStatic
        fun swissStrength(): TieBreakRules = fromPreset(Type.SWISS_STRENGTH)

        @JvmStatic
        fun custom(useSetDifference: Boolean, usePointsRatio: Boolean, useBuchholz: Boolean): TieBreakRules =
            TieBreakRules(Type.CUSTOM, useSetDifference, usePointsRatio, useBuchholz)

        private fun fromPreset(type: Type): TieBreakRules {
            val spec = requireNotNull(type.presetSpec) { "Type $type is not a preset" }
            return TieBreakRules(type, spec.useSetDifference, spec.usePointsRatio, spec.useBuchholz)
        }
    }
}
