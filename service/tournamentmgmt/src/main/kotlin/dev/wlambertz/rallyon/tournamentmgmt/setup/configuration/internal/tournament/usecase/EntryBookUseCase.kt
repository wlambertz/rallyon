package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDisposition
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMember
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.conflictIf
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

interface EntryBookStore {
    fun creator(id: Long): Long?
    fun load(id: Long, lock: Boolean): EntryBook
    fun selection(id: Long, disciplineId: Long, bracketId: String): EntryChoice?
    fun save(book: EntryBook)
}

class EntryBookUseCase(private val store: EntryBookStore, private val clock: Clock) {
    fun get(id: Long, actor: Long): EntryBook {
        authorize(id, actor)
        return store.load(id, false)
    }

    fun intake(id: Long, open: Boolean, version: Long, actor: Long): EntryBook =
        mutate(id, version, actor) { it.changeIntake(open, actor, now()) }

    fun record(
        id: Long, discipline: Long?, bracket: String?, players: List<EntryMember>?, version: Long, actor: Long
    ): EntryBook = mutate(id, version, actor) {
        it.record(choice(id, discipline, bracket), requireNotNull(players) { "Players are required" }, actor, now())
    }

    fun correct(
        id: Long, entry: Long, discipline: Long?, bracket: String?, players: List<EntryMember>?,
        version: Long, actor: Long
    ): EntryBook = mutate(id, version, actor) {
        it.correct(entry, choice(id, discipline, bracket), requireNotNull(players) { "Players are required" }, actor, now())
    }

    fun decide(id: Long, entry: Long, state: EntryDisposition, version: Long, actor: Long): EntryBook =
        mutate(id, version, actor) { it.decide(entry, state, actor, now()) }

    fun withdraw(id: Long, entry: Long, version: Long, actor: Long): EntryBook =
        mutate(id, version, actor) { it.withdraw(entry, actor, now()) }

    private fun mutate(id: Long, version: Long, actor: Long, operation: (EntryBook) -> EntryBook): EntryBook {
        authorize(id, actor)
        val current = store.load(id, true)
        conflictIf(version != current.version, "Entry-book version mismatch")
        val result = operation(current)
        store.save(result)
        return result
    }

    private fun authorize(id: Long, actor: Long) {
        val creator = store.creator(id) ?: throw EntryMissing()
        if (actor <= 0 || actor != creator) throw EntryDenied()
    }

    private fun choice(id: Long, discipline: Long?, bracket: String?): EntryChoice {
        require(
            discipline != null && discipline > 0 && !bracket.isNullOrBlank() &&
                bracket.length <= 100 && !bracket.contains('\u0000')
        ) { "Invalid discipline/bracket selection" }
        return requireNotNull(store.selection(id, discipline, bracket)) { "Discipline/bracket does not exist" }
    }

    private fun now(): Instant = clock.instant().truncatedTo(ChronoUnit.MICROS)
}
