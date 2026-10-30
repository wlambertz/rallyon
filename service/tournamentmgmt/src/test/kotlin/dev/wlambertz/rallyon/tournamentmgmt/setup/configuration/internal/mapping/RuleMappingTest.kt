package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.mapping

import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RuleMappingTest {
    @Test
    fun `all scoring presets and custom values survive the domain boundary`() {
        listOf(
            ScoringRules.twoByTwentyOne(), ScoringRules.threeByFifteen(),
            ScoringRules.custom(11, 1, false, null), ScoringRules.custom(-1, 0, false, -5)
        ).forEach { api ->
            assertThat(TournamentApiMapping.toApi(TournamentApiMapping.toDomain(api))).isEqualTo(api)
        }
    }

    @Test
    fun `all tie break presets and custom flag combinations survive the domain boundary`() {
        val custom = (0..7).map { flags ->
            TieBreakRules.custom(flags and 1 != 0, flags and 2 != 0, flags and 4 != 0)
        }
        (listOf(TieBreakRules.headToHead(), TieBreakRules.pointsRatio(), TieBreakRules.swissStrength()) + custom)
            .forEach { api ->
                assertThat(TournamentApiMapping.toApi(TournamentApiMapping.toDomain(api))).isEqualTo(api)
            }
    }
}
