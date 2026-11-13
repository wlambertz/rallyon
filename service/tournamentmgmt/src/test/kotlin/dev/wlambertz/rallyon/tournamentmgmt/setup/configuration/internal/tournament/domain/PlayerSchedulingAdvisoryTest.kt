package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Duration
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayerSchedulingAdvisoryTest {
    private fun time(minute: Int, kind: AdvisoryTimeKind = AdvisoryTimeKind.ACTUAL) =
        AdvisoryTime(Instant.parse("2026-10-17T10:00:00Z").plusSeconds(minute * 60L), kind)

    private fun match(id: String, start: AdvisoryTime?, end: AdvisoryTime?, players: Set<String> = setOf("p"), complete: Boolean = true) =
        AdvisoryMatch(id, players, complete, start, end)

    private val rule = AdvisoryRule(Duration.ofMinutes(30), "DBV", "2026/27", "event confirmation")

    @Test
    fun `doubles member overlapping singles produces actual or potential findings`() {
        val doubles = match("doubles", time(0), time(30), setOf("p", "partner"))
        val singles = match("singles", time(20), time(50))
        val actual = PlayerSchedulingAdvisory.assess(listOf(doubles, singles), rule)
        assertTrue(actual.any { it.kind == AdvisoryKind.ACTUAL_OVERLAP && it.player == "p" && it.times.size == 4 })
        val provisional = singles.copy(start = time(20, AdvisoryTimeKind.PROPOSED))
        assertTrue(PlayerSchedulingAdvisory.assess(listOf(doubles, provisional), rule).any { it.kind == AdvisoryKind.POTENTIAL_OVERLAP })
    }

    @Test
    fun `adjacent intervals and exact recovery boundary are not conflicts`() {
        val first = match("a", time(0), time(30))
        val adjacent = match("b", time(30), time(40))
        assertFalse(PlayerSchedulingAdvisory.assess(listOf(first, adjacent), rule).any { it.kind == AdvisoryKind.ACTUAL_OVERLAP })
        val later = match("b", time(60), time(70))
        assertFalse(PlayerSchedulingAdvisory.assess(listOf(first, later), rule).any { it.kind == AdvisoryKind.RECOVERY_CONCERN })
    }

    @Test
    fun `uncertain membership missing rule and call without start remain unresolved`() {
        val first = match("a", time(0), time(30), complete = false)
        val second = match("b", null, time(60)).copy(call = time(40).value)
        val findings = PlayerSchedulingAdvisory.assess(listOf(first, second), null)
        assertTrue(findings.any { it.kind == AdvisoryKind.UNRESOLVED && it.player == null })
        assertTrue(findings.any { it.kind == AdvisoryKind.UNRESOLVED && it.player == "p" })
        val known = second.copy(start = time(45))
        assertTrue(PlayerSchedulingAdvisory.assess(listOf(first, known), null).any { it.reason.contains("rule") })
    }

    @Test
    fun `delay and correction are reassessed with provenance`() {
        val first = match("a", time(0), time(30))
        val next = match("b", time(60, AdvisoryTimeKind.PROPOSED), time(90, AdvisoryTimeKind.ESTIMATED))
        assertFalse(PlayerSchedulingAdvisory.assess(listOf(first, next), rule).any { it.kind == AdvisoryKind.RECOVERY_CONCERN })
        val delayed = first.copy(end = time(50))
        val concern = PlayerSchedulingAdvisory.assess(listOf(delayed, next), rule).single { it.kind == AdvisoryKind.RECOVERY_CONCERN }
        assertEquals(rule, concern.rule)
        assertEquals(AdvisoryTimeKind.PROPOSED, concern.times.last().kind)
        assertFalse(PlayerSchedulingAdvisory.assess(listOf(first, next), rule).any { it.kind == AdvisoryKind.RECOVERY_CONCERN })
    }

    @Test
    fun `known end permits recovery assessment without earlier start`() {
        val first = match("a", null, time(30))
        val next = match("b", time(45, AdvisoryTimeKind.PROPOSED), time(70))
        val findings = PlayerSchedulingAdvisory.assess(listOf(first, next), rule)
        assertTrue(findings.any { it.kind == AdvisoryKind.UNRESOLVED && it.reason == "Occupied interval incomplete" })
        val concern = findings.single { it.kind == AdvisoryKind.RECOVERY_CONCERN }
        assertEquals(listOf(time(30), time(45, AdvisoryTimeKind.PROPOSED)), concern.times)
        assertEquals(rule, concern.rule)
        val reversed = PlayerSchedulingAdvisory.assess(listOf(next, first), rule)
        assertTrue(reversed.any { it.kind == AdvisoryKind.RECOVERY_CONCERN })
    }

    @Test
    fun `call does not provide a recovery start even with a selected rule`() {
        val first = match("a", time(0), time(30))
        val next = match("b", null, time(70)).copy(call = time(45).value)
        val findings = PlayerSchedulingAdvisory.assess(listOf(first, next), rule)
        assertTrue(findings.any { it.kind == AdvisoryKind.UNRESOLVED && it.reason == "Match order or start unknown" })
        assertFalse(findings.any { it.kind == AdvisoryKind.RECOVERY_CONCERN })
    }

    @Test
    fun `estimated end is unresolved and invalid intervals are rejected`() {
        val first = match("a", time(0), time(30, AdvisoryTimeKind.ESTIMATED))
        val next = match("b", time(45), time(60))
        assertTrue(PlayerSchedulingAdvisory.assess(listOf(first, next), rule).any { it.kind == AdvisoryKind.UNRESOLVED })
        assertThrows(IllegalArgumentException::class.java) { match("x", time(30), time(20)) }
        assertThrows(IllegalArgumentException::class.java) { PlayerSchedulingAdvisory.assess(listOf(first, first), rule) }
    }
}
