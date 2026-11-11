package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDisposition
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryHistory
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMember
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntrySnapshot
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.active
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.conflictIf
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.EntryBookStore
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import java.sql.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import org.springframework.stereotype.Repository

@Repository
class JpaEntryBookStore(private val em: EntityManager) : EntryBookStore {
    override fun creator(id: Long): Long? = query(
        "SELECT created_by_user_id FROM tournamentmgmt.tournaments WHERE id=:id", id
    ).resultList.firstOrNull().let { (it as? Number)?.toLong() }

    override fun selection(id: Long, disciplineId: Long, bracketId: String): EntryChoice? {
        val row = query(
            """
            SELECT d.discipline_id, d.display_name, b.bracket_id, b.display_name, CAST(d.team_size AS varchar)
            FROM tournamentmgmt.tournament_disciplines d
            JOIN tournamentmgmt.tournament_brackets b ON b.tournament_discipline_id=d.id
            WHERE d.tournament_id=:id AND d.discipline_id=:discipline AND b.bracket_id=:bracket
            """.trimIndent(), id
        ).setParameter("discipline", disciplineId).setParameter("bracket", bracketId)
            .resultList.firstOrNull() as? Array<*> ?: return null
        return EntryChoice(
            number(row[0]), row[1] as String, row[2] as String, row[3] as String,
            TeamSize.valueOf(row[4] as String)
        )
    }

    override fun load(id: Long, lock: Boolean): EntryBook {
        if (lock) ensureBook(id)
        val lockClause = if (lock) " FOR UPDATE" else ""
        val book = query(
            "SELECT intake_open,version FROM tournamentmgmt.entry_books WHERE tournament_id=:id$lockClause", id
        ).resultList.firstOrNull() as? Array<*> ?: return EntryBook(id)
        return EntryBook(id, book[0] as Boolean, number(book[1]), loadRecords(id), loadHistory(id))
    }

    private fun ensureBook(id: Long) {
        query(
            """
            INSERT INTO tournamentmgmt.entry_books(tournament_id,intake_open,version)
            VALUES (:id,false,0) ON CONFLICT (tournament_id) DO NOTHING
            """.trimIndent(), id
        ).executeUpdate()
    }

    private fun loadRecords(id: Long): List<EntrySnapshot> = rows(
        """
        SELECT entry_id,discipline_id,discipline_name,bracket_id,bracket_name,team_size,state
        FROM tournamentmgmt.entry_records WHERE tournament_id=:id ORDER BY entry_id
        """.trimIndent(), id
    ).map { row ->
        val entryId = number(row[0])
        val selection = EntryChoice(
            number(row[1]), row[2] as String, row[3] as String, row[4] as String,
            TeamSize.valueOf(row[5] as String)
        )
        EntrySnapshot(entryId, selection, loadMembers(id, entryId), EntryDisposition.valueOf(row[6] as String))
    }

    private fun loadMembers(id: Long, entryId: Long): List<EntryMember> = query(
        """
        SELECT player_key,display_name FROM tournamentmgmt.entry_members
        WHERE tournament_id=:id AND entry_id=:entry ORDER BY ordinal
        """.trimIndent(), id
    ).setParameter("entry", entryId).resultList.map {
        val row = it as Array<*>
        EntryMember(row[0] as String, row[1] as String)
    }

    private fun loadHistory(id: Long): List<EntryHistory> = rows(
        """
        SELECT entry_id,operation,previous_state,resulting_state,actor_id,occurred_at
        FROM tournamentmgmt.entry_audit WHERE tournament_id=:id ORDER BY book_version
        """.trimIndent(), id
    ).map { row ->
        EntryHistory(
            (row[0] as? Number)?.toLong(), row[1] as String,
            (row[2] as? String)?.let(EntryDisposition::valueOf),
            (row[3] as? String)?.let(EntryDisposition::valueOf), number(row[4]), instant(row[5])
        )
    }

    override fun save(book: EntryBook) {
        clearMembership(book.tournamentId)
        book.records.forEach { entry ->
            saveRecord(book.tournamentId, entry)
            saveMembers(book.tournamentId, entry)
        }
        appendAudit(book)
        advanceVersion(book)
    }

    private fun clearMembership(id: Long) {
        query("DELETE FROM tournamentmgmt.entry_active_members WHERE tournament_id=:id", id).executeUpdate()
        query("DELETE FROM tournamentmgmt.entry_members WHERE tournament_id=:id", id).executeUpdate()
    }

    private fun saveRecord(id: Long, entry: EntrySnapshot) {
        query(
            """
            INSERT INTO tournamentmgmt.entry_records
                (tournament_id,entry_id,discipline_id,discipline_name,bracket_id,bracket_name,team_size,state)
            VALUES (:id,:entry,:discipline,:disciplineName,:bracket,:bracketName,:size,:state)
            ON CONFLICT(tournament_id,entry_id) DO UPDATE SET
                discipline_id=excluded.discipline_id,discipline_name=excluded.discipline_name,
                bracket_id=excluded.bracket_id,bracket_name=excluded.bracket_name,
                team_size=excluded.team_size,state=excluded.state
            """.trimIndent(), id
        ).setParameter("entry", entry.id)
            .setParameter("discipline", entry.selection.disciplineId)
            .setParameter("disciplineName", entry.selection.disciplineName)
            .setParameter("bracket", entry.selection.bracketId)
            .setParameter("bracketName", entry.selection.bracketName)
            .setParameter("size", entry.selection.teamSize.name)
            .setParameter("state", entry.state.name).executeUpdate()
    }

    private fun saveMembers(id: Long, entry: EntrySnapshot) {
        entry.players.forEachIndexed { ordinal, member ->
            query(
                """
                INSERT INTO tournamentmgmt.entry_members(tournament_id,entry_id,ordinal,player_key,display_name)
                VALUES (:id,:entry,:ordinal,:key,:name)
                """.trimIndent(), id
            ).setParameter("entry", entry.id).setParameter("ordinal", ordinal)
                .setParameter("key", member.key).setParameter("name", member.displayName).executeUpdate()
            if (entry.active()) saveActiveClaim(id, entry, member)
        }
    }

    private fun saveActiveClaim(id: Long, entry: EntrySnapshot, member: EntryMember) {
        query(
            """
            INSERT INTO tournamentmgmt.entry_active_members(tournament_id,discipline_id,player_key,entry_id)
            VALUES (:id,:discipline,:key,:entry)
            """.trimIndent(), id
        ).setParameter("discipline", entry.selection.disciplineId)
            .setParameter("key", member.key).setParameter("entry", entry.id).executeUpdate()
    }

    private fun appendAudit(book: EntryBook) {
        val event = book.audit.last()
        query(
            """
            INSERT INTO tournamentmgmt.entry_audit
                (tournament_id,book_version,entry_id,operation,previous_state,resulting_state,actor_id,occurred_at)
            VALUES (:id,:version,:entry,:operation,:previous,:result,:actor,:at)
            """.trimIndent(), book.tournamentId
        ).setParameter("version", book.version).setParameter("entry", event.entryId)
            .setParameter("operation", event.operation).setParameter("previous", event.previousState?.name)
            .setParameter("result", event.resultingState?.name).setParameter("actor", event.actorId)
            .setParameter("at", event.at).executeUpdate()
    }

    private fun advanceVersion(book: EntryBook) {
        val count = query(
            """
            UPDATE tournamentmgmt.entry_books SET intake_open=:open,version=:version
            WHERE tournament_id=:id AND version=:expected
            """.trimIndent(), book.tournamentId
        ).setParameter("open", book.intakeOpen).setParameter("version", book.version)
            .setParameter("expected", book.version - 1).executeUpdate()
        conflictIf(count != 1, "Entry-book version mismatch")
    }

    private fun query(sql: String, id: Long): Query = em.createNativeQuery(sql).setParameter("id", id)
    private fun rows(sql: String, id: Long): List<Array<*>> = query(sql, id).resultList.map { it as Array<*> }
    private fun number(value: Any?): Long = (value as Number).toLong()
    private fun instant(value: Any?): Instant = when (value) {
        is Instant -> value
        is Timestamp -> value.toInstant()
        is OffsetDateTime -> value.toInstant()
        else -> error("Unexpected audit timestamp type")
    }
}
