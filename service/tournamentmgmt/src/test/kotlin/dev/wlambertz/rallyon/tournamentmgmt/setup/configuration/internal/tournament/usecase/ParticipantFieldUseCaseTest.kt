package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDisposition
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMember
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntrySnapshot
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldCurrency
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldKey
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldTournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldVersionConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.ParticipantField
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TeamSize
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParticipantFieldUseCaseTest {
    private val store = MemoryStore()
    private val clock = Clock.fixed(Instant.parse("2026-11-11T12:00:00.123456789Z"), ZoneOffset.UTC)
    private val service = ParticipantFieldUseCase(store, clock)
    private val key = FieldKey(1, 1, "A")
    private val alice = EntryMember("Alice", "Alice")
    private val bob = EntryMember("Bob", "Bob")

    @Test
    fun `unprepared reads require an existing target and create no storage`() {
        val read = service.get(1, 1, "A", 7)
        assertEquals(FieldCurrency.UNPREPARED, read.currency)
        assertEquals(ParticipantField(key), read.field)
        assertEquals(3L, read.observedTournamentVersion)
        assertEquals(0L, read.observedEntryBookVersion)
        assertTrue(store.fields.isEmpty())
        assertEquals(0, store.lockCalls)
        store.selectionExists = false
        rejectsUnchanged(FieldMissing::class.java) { service.get(1, 1, "A", 7) }
        rejectsUnchanged(FieldMissing::class.java) { service.get(99, 1, "A", 7) }
    }

    @Test
    fun `foreign and unusable identity cannot load private sources or fields`() {
        listOf(9L, 0L, -1L).forEach { actor ->
            rejectsUnchanged(FieldDenied::class.java) { service.get(1, 1, "A", actor) }
            rejectsUnchanged(FieldDenied::class.java) { prepare(emptyList(), actor = actor) }
        }
        assertEquals(0, store.bookCalls)
        assertEquals(0, store.loadCalls)
    }

    @Test
    fun `explicit subset preserves original labels and members with canonical unit order`() {
        val old = choice().copy(disciplineName = "Original discipline", bracketName = "Original bracket")
        store.book = store.book.copy(version = 8, records = listOf(
            entry(8, bob, selection = old), entry(3, alice, selection = old), entry(9, EntryMember("other", "Other"))
        ))
        val result = prepare(listOf(8, 3))
        assertEquals(listOf(3L, 8L), result.field.units.map { it.entryId })
        assertEquals(old, result.field.units.first().selection)
        assertEquals(choice(), result.field.selection)
        assertEquals(listOf(alice), result.field.units.first().players)
        assertEquals(FieldCurrency.CURRENT, result.currency)
        assertEquals(3L, result.field.sourceTournamentVersion)
        assertEquals(8L, result.field.sourceEntryBookVersion)
        val audit = result.field.audit.single()
        assertEquals(7L, audit.actorId)
        assertEquals(2, audit.unitCount)
        assertEquals(Instant.parse("2026-11-11T12:00:00.123456Z"), audit.at)
        assertEquals(3, store.book.records.size)
    }

    @Test
    fun `double unit retains both members and does not interpret their ordering`() {
        val doubleChoice = choice(2)
        store.book = store.book.copy(records = listOf(
            EntrySnapshot(1, doubleChoice, listOf(bob, alice), EntryDisposition.ACCEPTED)
        ))
        val result = prepare(listOf(1), disciplineId = 2)
        assertEquals(listOf(bob, alice), result.field.units.single().players)
        assertEquals(TeamSize.DOUBLES, result.field.selection?.teamSize)
    }

    @Test
    fun `empty and repeated preparations advance versions without mutating sources`() {
        val source = store.book
        val first = prepare(emptyList())
        val second = prepare(emptyList())
        assertEquals(1L, first.field.version)
        assertEquals(2L, second.field.version)
        assertTrue(second.field.units.isEmpty())
        assertEquals(listOf(1L, 2L), second.field.audit.map { it.version })
        assertEquals(FieldCurrency.CURRENT, second.currency)
        assertEquals(source, store.book)
        assertFalse(store.book.intakeOpen)
        assertEquals(0L, second.field.sourceEntryBookVersion)
    }

    @Test
    fun `source version changes only affect observed freshness`() {
        val saved = prepare(emptyList()).field
        store.tournamentVersion++
        assertEquals(FieldCurrency.OUTDATED, service.get(1, 1, "A", 7).currency)
        store.book = store.book.copy(version = 1, intakeOpen = true)
        val read = service.get(1, 1, "A", 7)
        assertEquals(saved, read.field)
        assertEquals(FieldCurrency.OUTDATED, read.currency)
        assertEquals(saved, store.fields[key])
        val reconciled = prepare(emptyList())
        assertEquals(2L, reconciled.field.version)
        assertEquals(FieldCurrency.CURRENT, reconciled.currency)
    }

    @Test
    fun `retained field survives removed target and may reconcile after restoration`() {
        store.book = store.book.copy(records = listOf(entry(1, alice)))
        val saved = prepare(listOf(1)).field
        store.selectionExists = false
        store.tournamentVersion++
        val read = service.get(1, 1, "A", 7)
        assertEquals(saved, read.field)
        assertEquals(FieldCurrency.OUTDATED, read.currency)
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(listOf(1)) }
        store.selectionExists = true
        assertEquals(FieldCurrency.CURRENT, prepare(listOf(1)).currency)
    }

    @Test
    fun `withdrawal does not rewrite a field and explicit clearing retains only minimal audit`() {
        store.book = store.book.copy(records = listOf(entry(1, alice)))
        val saved = prepare(listOf(1)).field
        store.book = store.book.copy(version = 1, records = listOf(entry(1, alice, state = EntryDisposition.WITHDRAWN)))
        assertEquals(saved, service.get(1, 1, "A", 7).field)
        rejectsUnchanged(FieldVersionConflict::class.java) { prepare(listOf(1)) }
        val cleared = prepare(emptyList()).field
        assertTrue(cleared.units.isEmpty())
        assertEquals(2L, cleared.version)
        assertEquals(listOf(1, 0), cleared.audit.map { it.unitCount })
        assertEquals(saved.audit.single(), cleared.audit.first())
    }

    @Test
    fun `nonaccepted and incompatible sources reject the complete replacement`() {
        store.book = store.book.copy(records = listOf(entry(1, alice)))
        prepare(listOf(1))
        EntryDisposition.entries.filter { it != EntryDisposition.ACCEPTED }.forEach { state ->
            store.book = store.book.copy(records = listOf(entry(1, alice, state = state)))
            rejectsUnchanged(FieldVersionConflict::class.java) { prepare(listOf(1)) }
        }
        listOf(choice(2), choice().copy(bracketId = "B"), choice().copy(teamSize = TeamSize.DOUBLES)).forEach { selection ->
            store.book = store.book.copy(records = listOf(entry(1, alice, selection = selection)))
            rejectsUnchanged(FieldVersionConflict::class.java) { prepare(listOf(1)) }
        }
    }

    @Test
    fun `invalid input and stale versions do not overwrite complete saved state`() {
        prepare(emptyList())
        listOf<List<Long?>?>(null, listOf(null), listOf(0), listOf(-1), listOf(1, 1), listOf(99)).forEach { ids ->
            rejectsUnchanged(IllegalArgumentException::class.java) { prepare(ids) }
        }
        listOf<String?>(null, "", " ", "a\u0000b", "x".repeat(101)).forEach { bracket ->
            rejectsUnchanged(IllegalArgumentException::class.java) { prepare(emptyList(), bracketId = bracket) }
        }
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(emptyList(), disciplineId = null) }
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(emptyList(), disciplineId = 0) }
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(emptyList(), version = -1) }
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(emptyList(), draft = null) }
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(emptyList(), book = -1) }
        rejectsUnchanged(FieldVersionConflict::class.java) { prepare(emptyList(), version = 0) }
        rejectsUnchanged(FieldVersionConflict::class.java) { prepare(emptyList(), draft = 2) }
        rejectsUnchanged(FieldVersionConflict::class.java) { prepare(emptyList(), book = 3) }
    }

    @Test
    fun `member validation enforces boundaries and case sensitive uniqueness`() {
        val invalid = listOf(
            alice.copy(key = null), alice.copy(key = ""), alice.copy(key = "ä"), alice.copy(key = "x".repeat(65)),
            alice.copy(displayName = null), alice.copy(displayName = " "),
            alice.copy(displayName = "x".repeat(201)), alice.copy(displayName = "x\u0000y")
        )
        invalid.forEach { member ->
            store.book = store.book.copy(records = listOf(entry(1, member)))
            rejectsUnchanged(IllegalArgumentException::class.java) { prepare(listOf(1)) }
        }
        store.book = store.book.copy(records = listOf(
            entry(1, alice.copy(key = "x".repeat(64), displayName = "x".repeat(200))),
            entry(2, alice), entry(3, alice.copy(key = "alice"))
        ))
        assertEquals(3, prepare(listOf(3, 2, 1)).field.units.size)
        store.book = store.book.copy(records = listOf(entry(1, alice), entry(2, alice)))
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(listOf(1, 2)) }
        store.book = store.book.copy(records = listOf(
            EntrySnapshot(1, choice(2), listOf(alice, alice), EntryDisposition.ACCEPTED)
        ))
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(listOf(1), disciplineId = 2) }
        store.book = store.book.copy(records = listOf(
            EntrySnapshot(1, choice(), listOf(alice, bob), EntryDisposition.ACCEPTED)
        ))
        rejectsUnchanged(IllegalArgumentException::class.java) { prepare(listOf(1)) }
    }

    @Test
    fun `different fields retain independent content versions and audit`() {
        store.book = store.book.copy(records = listOf(entry(1, alice)))
        val a = prepare(listOf(1)).field
        val b = prepare(emptyList(), bracketId = "B").field
        assertEquals(1L, a.version)
        assertEquals(1L, b.version)
        prepare(emptyList(), bracketId = "B")
        assertEquals(a, store.fields[key])
        assertEquals(2L, store.fields[FieldKey(1, 1, "B")]?.version)
    }

    private fun prepare(
        ids: List<Long?>?, disciplineId: Long? = 1, bracketId: String? = "A",
        version: Long = store.fields[FieldKey(1, disciplineId ?: 0, bracketId ?: "")]?.version ?: 0,
        draft: Long? = store.tournamentVersion, book: Long? = store.book.version, actor: Long = 7
    ) = service.prepare(1, FieldPreparation(disciplineId, bracketId, ids, draft, book), version, actor)

    private fun entry(
        id: Long, player: EntryMember, selection: EntryChoice = choice(), state: EntryDisposition = EntryDisposition.ACCEPTED
    ) = EntrySnapshot(id, selection, listOf(player), state)

    private fun choice(discipline: Long = 1) = EntryChoice(
        discipline, "Discipline", "A", "Bracket", if (discipline == 2L) TeamSize.DOUBLES else TeamSize.SINGLES
    )

    private fun rejectsUnchanged(type: Class<out Throwable>, action: () -> Unit) {
        val before = store.fields.toMap()
        val source = store.book
        assertThrows(type) { action() }
        assertEquals(before, store.fields)
        assertEquals(source, store.book)
    }

    private class MemoryStore : ParticipantFieldStore {
        val fields = mutableMapOf<FieldKey, ParticipantField>()
        var book = EntryBook(1)
        var tournamentVersion = 3L
        var selectionExists = true
        var bookCalls = 0
        var loadCalls = 0
        var lockCalls = 0
        override fun tournament(id: Long): FieldTournament? = if (id == 1L) FieldTournament(7, tournamentVersion) else null
        override fun book(id: Long): EntryBook { bookCalls++; return book }
        override fun selection(key: FieldKey): EntryChoice? = if (selectionExists) EntryChoice(
            key.disciplineId, "Discipline", key.bracketId, "Bracket",
            if (key.disciplineId == 2L) TeamSize.DOUBLES else TeamSize.SINGLES
        ) else null
        override fun load(key: FieldKey, lock: Boolean): ParticipantField {
            loadCalls++
            if (lock) lockCalls++
            return fields[key] ?: ParticipantField(key)
        }
        override fun save(field: ParticipantField) { fields[field.key] = field }
    }
}
