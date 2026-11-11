package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import java.time.Instant

data class EntryPlayer(val key: String?, val displayName: String?)

data class EntryInput(val disciplineId: Long?, val bracketId: String?, val players: List<EntryPlayer>?)

data class EntrySelection(
    val disciplineId: Long,
    val disciplineName: String,
    val bracketId: String,
    val bracketName: String,
    val teamSize: TeamSize
)

enum class EntryState { RECEIVED, ACCEPTED, REJECTED, WITHDRAWN }

data class EntryRecord(
    val id: Long,
    val selection: EntrySelection,
    val players: List<EntryPlayer>,
    val state: EntryState
)

data class EntryAudit(
    val entryId: Long?,
    val operation: String,
    val previousState: EntryState?,
    val resultingState: EntryState?,
    val actorId: Long,
    val at: Instant
)

data class EntryBookView(
    val tournamentId: Long,
    val intakeOpen: Boolean,
    val version: Long,
    val records: List<EntryRecord>,
    val audit: List<EntryAudit>
)

interface EntryBookService {
    fun get(id: Long, actor: Long): EntryBookView
    fun intake(id: Long, open: Boolean, version: Long, actor: Long): EntryBookView
    fun create(id: Long, input: EntryInput, version: Long, actor: Long): EntryBookView
    fun correct(id: Long, entryId: Long, input: EntryInput, version: Long, actor: Long): EntryBookView
    fun decide(id: Long, entryId: Long, state: EntryState, version: Long, actor: Long): EntryBookView
    fun withdraw(id: Long, entryId: Long, version: Long, actor: Long): EntryBookView
}

class EntryInvalid(message: String) : RuntimeException(message)
class EntryForbidden(message: String) : RuntimeException(message)
class EntryNotFound(message: String) : RuntimeException(message)
class EntryConflict(message: String) : RuntimeException(message)
