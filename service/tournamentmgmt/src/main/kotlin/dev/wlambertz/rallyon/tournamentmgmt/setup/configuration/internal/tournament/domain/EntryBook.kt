package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Instant

enum class EntryDisposition { RECEIVED, ACCEPTED, REJECTED, WITHDRAWN }

data class EntryMember(val key: String?, val displayName: String?)

data class EntryChoice(
    val disciplineId: Long,
    val disciplineName: String,
    val bracketId: String,
    val bracketName: String,
    val teamSize: TeamSize
)

data class EntrySnapshot(
    val id: Long,
    val selection: EntryChoice,
    val players: List<EntryMember>,
    val state: EntryDisposition
)

data class EntryHistory(
    val entryId: Long?,
    val operation: String,
    val previousState: EntryDisposition?,
    val resultingState: EntryDisposition?,
    val actorId: Long,
    val at: Instant
)

data class EntryBook(
    val tournamentId: Long,
    val intakeOpen: Boolean = false,
    val version: Long = 0,
    val records: List<EntrySnapshot> = emptyList(),
    val audit: List<EntryHistory> = emptyList()
) {
    fun changeIntake(open: Boolean, actor: Long, now: Instant): EntryBook {
        conflictIf(open == intakeOpen, "Intake already has that state")
        val event = EntryHistory(null, if (open) "OPEN" else "CLOSE", null, null, actor, now)
        return changed(records, open, event)
    }

    fun record(selection: EntryChoice, players: List<EntryMember>, actor: Long, now: Instant): EntryBook {
        conflictIf(!intakeOpen, "Intake is closed")
        validateMembers(selection, players)
        val id = (records.maxOfOrNull { it.id } ?: 0) + 1
        val entry = EntrySnapshot(id, selection, players.toList(), EntryDisposition.RECEIVED)
        checkMembership(entry)
        return changed(records + entry, intakeOpen, EntryHistory(id, "CREATE", null, entry.state, actor, now))
    }

    fun correct(
        id: Long, selection: EntryChoice, players: List<EntryMember>, actor: Long, now: Instant
    ): EntryBook {
        val previous = entry(id)
        conflictIf(!intakeOpen || previous.state == EntryDisposition.WITHDRAWN, "Entry cannot be corrected")
        validateMembers(selection, players)
        val replacement = previous.copy(
            selection = selection, players = players.toList(), state = EntryDisposition.RECEIVED
        )
        checkMembership(replacement)
        val event = EntryHistory(id, "CORRECT", previous.state, replacement.state, actor, now)
        return changed(replaced(replacement), intakeOpen, event)
    }

    fun decide(id: Long, state: EntryDisposition, actor: Long, now: Instant): EntryBook {
        require(state == EntryDisposition.ACCEPTED || state == EntryDisposition.REJECTED) {
            "Decision must be ACCEPTED or REJECTED"
        }
        return transition(id, state, "DECIDE", actor, now)
    }

    fun withdraw(id: Long, actor: Long, now: Instant): EntryBook =
        transition(id, EntryDisposition.WITHDRAWN, "WITHDRAW", actor, now)

    private fun transition(
        id: Long, state: EntryDisposition, operation: String, actor: Long, now: Instant
    ): EntryBook {
        val previous = entry(id)
        conflictIf(previous.state == EntryDisposition.WITHDRAWN || previous.state == state, "Illegal entry transition")
        val replacement = previous.copy(state = state)
        checkMembership(replacement)
        val event = EntryHistory(id, operation, previous.state, state, actor, now)
        return changed(replaced(replacement), intakeOpen, event)
    }

    private fun entry(id: Long): EntrySnapshot = records.find { it.id == id } ?: throw EntryMissing()

    private fun replaced(replacement: EntrySnapshot): List<EntrySnapshot> =
        records.map { if (it.id == replacement.id) replacement else it }

    private fun checkMembership(candidate: EntrySnapshot) {
        if (!candidate.active()) return
        val keys = candidate.players.map { it.key }.toSet()
        val duplicate = records.any { existing ->
            existing.id != candidate.id && existing.active() &&
                existing.selection.disciplineId == candidate.selection.disciplineId &&
                existing.players.any { it.key in keys }
        }
        conflictIf(duplicate, "Player already has an active entry in this discipline")
    }

    private fun changed(next: List<EntrySnapshot>, open: Boolean, event: EntryHistory): EntryBook =
        copy(intakeOpen = open, version = Math.addExact(version, 1), records = next, audit = audit + event)
}

fun EntrySnapshot.active(): Boolean = state == EntryDisposition.RECEIVED || state == EntryDisposition.ACCEPTED

private fun validateMembers(selection: EntryChoice, players: List<EntryMember>) {
    require(players.size == selection.teamSize.size) { "Player count must match the discipline" }
    require(players.map { it.key }.toSet().size == players.size) { "Players must be distinct" }
    players.forEach { player ->
        require(player.key != null && Regex("[A-Za-z0-9][A-Za-z0-9_-]{0,63}").matches(player.key)) {
            "Invalid local player key"
        }
        require(
            !player.displayName.isNullOrBlank() && player.displayName.length <= 200 &&
                !player.displayName.contains('\u0000')
        ) { "Invalid player display name" }
    }
}

class EntryMissing : RuntimeException()
class EntryDenied : RuntimeException()
class EntryVersionConflict(message: String) : RuntimeException(message)

fun conflictIf(condition: Boolean, message: String) {
    if (condition) throw EntryVersionConflict(message)
}
