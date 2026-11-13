package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Duration
import java.time.Instant

internal enum class AdvisoryTimeKind { ACTUAL, ESTIMATED, PROPOSED }

internal data class AdvisoryTime(val value: Instant, val kind: AdvisoryTimeKind)

internal data class AdvisoryMatch(
    val id: String,
    val confirmedPlayers: Set<String>,
    val membershipComplete: Boolean,
    val start: AdvisoryTime?,
    val end: AdvisoryTime?,
    val call: Instant? = null
) {
    init {
        require(id.isNotBlank())
        require(confirmedPlayers.none { it.isBlank() })
        require(start == null || end == null || start.value < end.value)
    }
}

internal data class AdvisoryRule(
    val recovery: Duration,
    val authority: String,
    val edition: String,
    val applicabilityReference: String
) {
    init {
        require(!recovery.isNegative)
        require(authority.isNotBlank() && edition.isNotBlank() && applicabilityReference.isNotBlank())
    }
}

internal enum class AdvisoryKind { ACTUAL_OVERLAP, POTENTIAL_OVERLAP, RECOVERY_CONCERN, UNRESOLVED }

internal data class AdvisoryFinding(
    val kind: AdvisoryKind,
    val matches: Pair<String, String>,
    val player: String?,
    val reason: String,
    val times: List<AdvisoryTime> = emptyList(),
    val rule: AdvisoryRule? = null
)

internal object PlayerSchedulingAdvisory {
    fun assess(matches: List<AdvisoryMatch>, rule: AdvisoryRule?): List<AdvisoryFinding> {
        require(matches.map { it.id }.distinct().size == matches.size)
        return matches.indices.flatMap { left ->
            (left + 1 until matches.size).flatMap { right -> assessPair(matches[left], matches[right], rule) }
        }
    }

    private fun assessPair(a: AdvisoryMatch, b: AdvisoryMatch, rule: AdvisoryRule?): List<AdvisoryFinding> {
        val pair = a.id to b.id
        val findings = mutableListOf<AdvisoryFinding>()
        if (!a.membershipComplete || !b.membershipComplete) {
            findings += AdvisoryFinding(AdvisoryKind.UNRESOLVED, pair, null, "Participant identity or membership incomplete")
        }
        for (player in a.confirmedPlayers intersect b.confirmedPlayers) {
            val times = listOfNotNull(a.start, a.end, b.start, b.end)
            if (times.size < 4) {
                findings += AdvisoryFinding(AdvisoryKind.UNRESOLVED, pair, player, "Occupied interval incomplete", times)
            } else if (a.start!!.value < b.end!!.value && b.start!!.value < a.end!!.value) {
                val kind = if (times.all { it.kind == AdvisoryTimeKind.ACTUAL }) AdvisoryKind.ACTUAL_OVERLAP else AdvisoryKind.POTENTIAL_OVERLAP
                findings += AdvisoryFinding(kind, pair, player, "Occupied intervals overlap", times)
            }
            val starts = listOfNotNull(a.start, b.start)
            val ordered = when {
                a.start != null && b.start != null && a.start.value != b.start.value ->
                    if (a.start.value < b.start.value) a to b else b to a
                a.start == null && a.end?.kind == AdvisoryTimeKind.ACTUAL && b.start != null && a.end.value <= b.start.value -> a to b
                b.start == null && b.end?.kind == AdvisoryTimeKind.ACTUAL && a.start != null && b.end.value <= a.start.value -> b to a
                else -> null
            }
            if (ordered == null) {
                findings += AdvisoryFinding(AdvisoryKind.UNRESOLVED, pair, player, "Match order or start unknown", starts)
                continue
            }
            val (first, next) = ordered
            val end = first.end
            val start = next.start!!
            if (rule == null || end == null || end.kind != AdvisoryTimeKind.ACTUAL || start.kind == AdvisoryTimeKind.ESTIMATED) {
                findings += AdvisoryFinding(AdvisoryKind.UNRESOLVED, pair, player, "Recovery rule, actual end or start uncertain", listOfNotNull(end, start), rule)
            } else if (Duration.between(end.value, start.value) < rule.recovery) {
                findings += AdvisoryFinding(AdvisoryKind.RECOVERY_CONCERN, pair, player, "Recovery shorter than supplied interval", listOf(end, start), rule)
            }
        }
        return findings
    }
}
