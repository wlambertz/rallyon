package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldKey
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldObservation
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldTournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.ParticipantField
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.fieldConflictIf
import java.time.Clock
import java.time.temporal.ChronoUnit

interface ParticipantFieldStore {
    fun tournament(id: Long): FieldTournament?
    fun book(id: Long): EntryBook
    fun selection(key: FieldKey): EntryChoice?
    fun load(key: FieldKey, lock: Boolean): ParticipantField
    fun save(field: ParticipantField)
}

class ParticipantFieldUseCase(private val store: ParticipantFieldStore, private val clock: Clock) {
    fun get(id: Long, disciplineId: Long, bracketId: String, actor: Long): FieldObservation {
        val tournament = authorize(id, actor)
        val key = key(id, disciplineId, bracketId)
        val book = store.book(id)
        val field = store.load(key, false)
        if (field.version == 0L && store.selection(key) == null) throw FieldMissing()
        return FieldObservation(field, tournament.version, book.version)
    }

    fun prepare(
        id: Long,
        disciplineId: Long?,
        bracketId: String?,
        entryIds: List<Long?>?,
        sourceTournamentVersion: Long?,
        sourceEntryBookVersion: Long?,
        expectedFieldVersion: Long,
        actor: Long
    ): FieldObservation {
        val tournament = authorize(id, actor)
        val key = key(id, disciplineId, bracketId)
        require(
            expectedFieldVersion >= 0 && sourceTournamentVersion != null && sourceTournamentVersion >= 0 &&
                sourceEntryBookVersion != null && sourceEntryBookVersion >= 0
        ) { "Valid field and source versions are required" }
        val requested = requireNotNull(entryIds) { "Entry selection is required" }
        require(requested.all { it != null && it > 0 } && requested.distinct().size == requested.size) {
            "Entry IDs must be positive and distinct"
        }
        val book = store.book(id)
        fieldConflictIf(
            sourceTournamentVersion != tournament.version || sourceEntryBookVersion != book.version,
            "Participant-field source version mismatch"
        )
        val target = requireNotNull(store.selection(key)) { "Target discipline/bracket does not exist" }
        val entriesById = book.records.associateBy { it.id }
        val entries = requested.map { requireNotNull(entriesById[it]) { "Selected entry does not exist" } }
        val current = store.load(key, true)
        fieldConflictIf(expectedFieldVersion != current.version, "Participant-field version mismatch")
        val next = current.prepare(
            target, entries, tournament.version, book.version, actor,
            clock.instant().truncatedTo(ChronoUnit.MICROS)
        )
        store.save(next)
        return FieldObservation(next, tournament.version, book.version)
    }

    private fun authorize(id: Long, actor: Long): FieldTournament {
        val tournament = store.tournament(id) ?: throw FieldMissing()
        if (actor <= 0 || actor != tournament.creatorId) throw FieldDenied()
        return tournament
    }

    private fun key(id: Long, disciplineId: Long?, bracketId: String?): FieldKey {
        require(
            disciplineId != null && disciplineId > 0 && !bracketId.isNullOrBlank() &&
                bracketId.length <= 100 && !bracketId.contains('\u0000')
        ) { "Invalid discipline/bracket selection" }
        return FieldKey(id, disciplineId, bracketId)
    }
}
