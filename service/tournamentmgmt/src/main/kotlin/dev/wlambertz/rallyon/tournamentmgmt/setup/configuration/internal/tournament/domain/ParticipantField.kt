package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Instant

data class FieldKey(val tournamentId: Long, val disciplineId: Long, val bracketId: String)
data class FieldTournament(val creatorId: Long, val version: Long)
data class FieldUnit(val entryId: Long, val selection: EntryChoice, val players: List<EntryMember>)
data class FieldHistory(
    val version: Long,
    val actorId: Long,
    val at: Instant,
    val unitCount: Int,
    val sourceTournamentVersion: Long,
    val sourceEntryBookVersion: Long
)

enum class FieldCurrency { UNPREPARED, CURRENT, OUTDATED }

data class ParticipantField(
    val key: FieldKey,
    val version: Long = 0,
    val selection: EntryChoice? = null,
    val sourceTournamentVersion: Long? = null,
    val sourceEntryBookVersion: Long? = null,
    val units: List<FieldUnit> = emptyList(),
    val audit: List<FieldHistory> = emptyList()
) {
    fun prepare(
        target: EntryChoice,
        entries: List<EntrySnapshot>,
        tournamentVersion: Long,
        bookVersion: Long,
        actorId: Long,
        at: Instant
    ): ParticipantField {
        val selected = entries.sortedBy { it.id }.map { entry ->
            fieldConflictIf(entry.state != EntryDisposition.ACCEPTED, "Selected entry is not accepted")
            fieldConflictIf(
                entry.selection.disciplineId != key.disciplineId || entry.selection.bracketId != key.bracketId ||
                    entry.selection.teamSize != target.teamSize,
                "Selected entry is incompatible with the target"
            )
            validateMembers(target.teamSize, entry.players)
            FieldUnit(entry.id, entry.selection, entry.players.map { it.copy() })
        }
        val keys = selected.flatMap { it.players }.map { it.key }
        require(keys.distinct().size == keys.size) { "Selected entries contain duplicate players" }
        val next = Math.addExact(version, 1)
        return copy(
            version = next,
            selection = target,
            sourceTournamentVersion = tournamentVersion,
            sourceEntryBookVersion = bookVersion,
            units = selected,
            audit = audit + FieldHistory(next, actorId, at, selected.size, tournamentVersion, bookVersion)
        )
    }

    private fun validateMembers(size: TeamSize, players: List<EntryMember>) {
        require(players.size == size.size) { "Selected entry member count is invalid" }
        players.forEach { player ->
            require(player.key != null && Regex("[A-Za-z0-9][A-Za-z0-9_-]{0,63}").matches(player.key)) {
                "Selected entry player key is invalid"
            }
            require(
                !player.displayName.isNullOrBlank() && player.displayName.length <= 200 &&
                    !player.displayName.contains('\u0000')
            ) { "Selected entry player name is invalid" }
        }
    }
}

data class FieldObservation(
    val field: ParticipantField,
    val observedTournamentVersion: Long,
    val observedEntryBookVersion: Long
) {
    val currency: FieldCurrency
        get() = when {
            this.field.version == 0L -> FieldCurrency.UNPREPARED
            this.field.sourceTournamentVersion == observedTournamentVersion &&
                this.field.sourceEntryBookVersion == observedEntryBookVersion -> FieldCurrency.CURRENT
            else -> FieldCurrency.OUTDATED
        }
}

class FieldDenied : RuntimeException()
class FieldMissing : RuntimeException()
class FieldVersionConflict(message: String) : RuntimeException(message)

fun fieldConflictIf(condition: Boolean, message: String) {
    if (condition) throw FieldVersionConflict(message)
}
