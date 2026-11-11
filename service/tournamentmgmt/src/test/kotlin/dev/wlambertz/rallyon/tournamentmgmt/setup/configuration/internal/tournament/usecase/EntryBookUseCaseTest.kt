package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDisposition
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMember
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryVersionConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TeamSize
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class EntryBookUseCaseTest {
    private val store = MemoryStore()
    private val clock = Clock.fixed(Instant.parse("2026-11-11T12:00:00Z"), ZoneOffset.UTC)
    private val service = EntryBookUseCase(store, clock)
    private val alice = EntryMember("Alice", "Alice")
    private val bob = EntryMember("Bob", "Bob")

    @Test
    fun `intake and identity authority protect every operation`() {
        assertFalse(service.get(1, 7).intakeOpen)
        assertThrows(EntryDenied::class.java) { service.intake(1, true, 0, 9) }
        assertThrows(EntryVersionConflict::class.java) { service.record(1, 1, "A", listOf(alice), 0, 7) }
        assertEquals(0L, store.book.version)
        service.intake(1, true, 0, 7)
        assertThrows(EntryVersionConflict::class.java) { service.intake(1, true, 1, 7) }
        assertThrows(EntryVersionConflict::class.java) { service.record(1, 1, "A", listOf(alice), 0, 7) }
        assertEquals(1, store.book.audit.size)
    }

    @Test
    fun `accepted corrections reset state and withdrawal is terminal even after closure`() {
        service.intake(1, true, 0, 7)
        service.record(1, 1, "A", listOf(alice), 1, 7)
        service.decide(1, 1, EntryDisposition.ACCEPTED, 2, 7)
        val corrected = service.correct(1, 1, 1, "A", listOf(alice.copy(displayName = "Alice corrected")), 3, 7)
        assertEquals(EntryDisposition.RECEIVED, corrected.records.single().state)
        service.intake(1, false, 4, 7)
        assertThrows(EntryVersionConflict::class.java) { service.correct(1, 1, 1, "A", listOf(alice), 5, 7) }
        val withdrawn = service.withdraw(1, 1, 5, 7)
        assertEquals(EntryDisposition.WITHDRAWN, withdrawn.records.single().state)
        assertThrows(EntryVersionConflict::class.java) { service.decide(1, 1, EntryDisposition.ACCEPTED, 6, 7) }
        assertEquals(withdrawn, store.book)
    }

    @Test
    fun `active membership detects reversed pairs across brackets but releases rejected records`() {
        service.intake(1, true, 0, 7)
        service.record(1, 2, "A", listOf(alice, bob), 1, 7)
        assertThrows(EntryVersionConflict::class.java) { service.record(1, 2, "B", listOf(bob, alice), 2, 7) }
        service.decide(1, 1, EntryDisposition.REJECTED, 2, 7)
        service.record(1, 2, "B", listOf(bob, alice), 3, 7)
        assertThrows(EntryVersionConflict::class.java) { service.decide(1, 1, EntryDisposition.ACCEPTED, 4, 7) }
        assertEquals(4L, store.book.version)
    }

    @Test
    fun `local keys are case sensitive and may be reused in other disciplines`() {
        service.intake(1, true, 0, 7)
        service.record(1, 1, "A", listOf(alice), 1, 7)
        service.record(1, 1, "A", listOf(alice.copy(key = "alice")), 2, 7)
        service.record(1, 3, "A", listOf(alice), 3, 7)
        assertEquals(3, store.book.records.size)
    }

    @Test
    fun `input validation preserves previous book and snapshots survive configuration removal`() {
        service.intake(1, true, 0, 7)
        val invalid = listOf(alice.copy(key = ""), alice.copy(key = "ä"), alice.copy(key = "x".repeat(65)), alice.copy(displayName = null), alice.copy(displayName = " "), alice.copy(displayName = "x".repeat(201)), alice.copy(displayName = "a\u0000b"))
        invalid.forEach { member -> assertThrows(IllegalArgumentException::class.java) { service.record(1, 1, "A", listOf(member), 1, 7) } }
        assertEquals(1L, store.book.version)
        val original = service.record(1, 1, "A", listOf(alice.copy(key = "x".repeat(64), displayName = "x".repeat(200))), 1, 7)
        store.selectionExists = false
        assertEquals(original, service.get(1, 7))
        assertThrows(IllegalArgumentException::class.java) { service.correct(1, 1, 1, "A", listOf(alice), 2, 7) }
        assertEquals(EntryDisposition.WITHDRAWN, service.withdraw(1, 1, 2, 7).records.single().state)
    }

    @Test
    fun `foreign actors and unusable identity cannot inspect or mutate the book`() {
        service.intake(1, true, 0, 7)
        service.record(1, 1, "A", listOf(alice), 1, 7)
        listOf(9L, 0L).forEach { actor ->
            rejectsUnchanged(EntryDenied::class.java) { service.get(1, actor) }
            rejectsUnchanged(EntryDenied::class.java) { service.record(1, 1, "A", listOf(bob), 2, actor) }
            rejectsUnchanged(EntryDenied::class.java) { service.correct(1, 1, 1, "A", listOf(bob), 2, actor) }
            rejectsUnchanged(EntryDenied::class.java) { service.decide(1, 1, EntryDisposition.ACCEPTED, 2, actor) }
            rejectsUnchanged(EntryDenied::class.java) { service.withdraw(1, 1, 2, actor) }
        }
        rejectsUnchanged(EntryMissing::class.java) { service.get(99, 7) }
    }

    @Test
    fun `invalid selections and members do not reach persistence or change the book`() {
        service.intake(1, true, 0, 7)
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, null, "A", listOf(alice), 1, 7) }
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 1, null, listOf(alice), 1, 7) }
        val calls = store.selectionCalls
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 1, "A\u0000B", listOf(alice), 1, 7) }
        assertEquals(calls, store.selectionCalls)
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 1, "A", null, 1, 7) }
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 1, "A", emptyList(), 1, 7) }
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 1, "A", listOf(alice, bob), 1, 7) }
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 2, "A", listOf(alice, alice), 1, 7) }
        store.selectionExists = false
        rejectsUnchanged(IllegalArgumentException::class.java) { service.record(1, 1, "A", listOf(alice), 1, 7) }
    }

    @Test
    fun `illegal decisions and terminal corrections preserve the complete book`() {
        service.intake(1, true, 0, 7)
        service.record(1, 1, "A", listOf(alice), 1, 7)
        listOf(EntryDisposition.RECEIVED, EntryDisposition.WITHDRAWN).forEach { state ->
            rejectsUnchanged(IllegalArgumentException::class.java) { service.decide(1, 1, state, 2, 7) }
        }
        service.decide(1, 1, EntryDisposition.ACCEPTED, 2, 7)
        rejectsUnchanged(EntryVersionConflict::class.java) { service.decide(1, 1, EntryDisposition.ACCEPTED, 3, 7) }
        service.withdraw(1, 1, 3, 7)
        rejectsUnchanged(EntryVersionConflict::class.java) { service.correct(1, 1, 1, "A", listOf(bob), 4, 7) }
        rejectsUnchanged(EntryVersionConflict::class.java) { service.withdraw(1, 1, 4, 7) }
        rejectsUnchanged(EntryVersionConflict::class.java) { service.decide(1, 1, EntryDisposition.REJECTED, 4, 7) }
    }

    private fun rejectsUnchanged(type: Class<out Throwable>, action: () -> Unit) {
        val before = store.book
        assertThrows(type) { action() }
        assertEquals(before, store.book)
    }

    private class MemoryStore : EntryBookStore {
        var book = EntryBook(1)
        var selectionExists = true
        var selectionCalls = 0
        override fun creator(id: Long): Long? = if (id == 1L) 7 else null
        override fun load(id: Long, lock: Boolean): EntryBook = book
        override fun selection(id: Long, disciplineId: Long, bracketId: String): EntryChoice? {
            selectionCalls++
            return if (selectionExists) EntryChoice(
                disciplineId, "Discipline", bracketId, "Bracket",
                if (disciplineId == 2L) TeamSize.DOUBLES else TeamSize.SINGLES
            ) else null
        }
        override fun save(book: EntryBook) { this.book = book }
    }
}
