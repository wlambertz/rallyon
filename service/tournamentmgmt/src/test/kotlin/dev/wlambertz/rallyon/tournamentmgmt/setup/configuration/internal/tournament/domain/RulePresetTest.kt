package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class RulePresetTest {
    private val scoringPresets = listOf(
        ScoringRules(ScoringRules.Type.TWO_BY_TWENTY_ONE, 21, 3, true, 30),
        ScoringRules(ScoringRules.Type.THREE_BY_FIFTEEN, 15, 3, true, 21)
    )
    private val tieBreakPresets = listOf(
        TieBreakRules(TieBreakRules.Type.HEAD_TO_HEAD, true, false, false),
        TieBreakRules(TieBreakRules.Type.POINTS_RATIO, false, true, false),
        TieBreakRules(TieBreakRules.Type.SWISS_STRENGTH, true, true, true)
    )

    @TestFactory
    fun `scoring presets reject each mismatched value`() = scoringPresets.flatMap { preset ->
        listOf<Pair<String, () -> ScoringRules>>(
            "points" to { preset.copy(pointsPerGame = -1) },
            "games" to { preset.copy(gamesPerMatch = -1) },
            "win by two" to { preset.copy(winByTwo = false) },
            "cap" to { preset.copy(capPoints = null) }
        ).map { (field, change) ->
            dynamicTest("${preset.type} rejects mismatched $field") {
                val error = assertThrows(IllegalArgumentException::class.java) { change() }
                assertThat(error.message).isEqualTo("Values must match preset definition for type ${preset.type}")
            }
        }
    }

    @TestFactory
    fun `tie break presets reject each mismatched flag`() = tieBreakPresets.flatMap { preset ->
        listOf<Pair<String, () -> TieBreakRules>>(
            "set difference" to { preset.copy(useSetDifference = !preset.useSetDifference) },
            "points ratio" to { preset.copy(usePointsRatio = !preset.usePointsRatio) },
            "buchholz" to { preset.copy(useBuchholz = !preset.useBuchholz) }
        ).map { (field, change) ->
            dynamicTest("${preset.type} rejects mismatched $field") {
                val error = assertThrows(IllegalArgumentException::class.java) { change() }
                assertThat(error.message).isEqualTo("Values must match preset definition for type ${preset.type}")
            }
        }
    }

    @Test
    fun `custom values remain unconstrained by preset definitions`() {
        val scoring = ScoringRules(ScoringRules.Type.CUSTOM, -1, 0, false, -5)
        assertThat(scoring.pointsPerGame).isEqualTo(-1)
        val combinations = (0..7).map { flags ->
            TieBreakRules(TieBreakRules.Type.CUSTOM, flags and 1 != 0, flags and 2 != 0, flags and 4 != 0)
        }
        assertThat(combinations).hasSize(8)
    }
}
