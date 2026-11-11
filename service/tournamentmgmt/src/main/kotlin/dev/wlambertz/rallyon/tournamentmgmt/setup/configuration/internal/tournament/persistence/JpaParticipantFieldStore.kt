package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMember
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldHistory
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldKey
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldTournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldUnit
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.ParticipantField
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.fieldConflictIf
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.EntryBookStore
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.ParticipantFieldStore
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import java.sql.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import org.springframework.stereotype.Repository

@Repository
class JpaParticipantFieldStore(private val em: EntityManager, private val entries: EntryBookStore) : ParticipantFieldStore {
    override fun tournament(id: Long): FieldTournament? {
        val row = em.createNativeQuery(
            "SELECT created_by_user_id,version FROM tournamentmgmt.tournaments WHERE id=:id"
        ).setParameter("id", id).resultList.firstOrNull() as? Array<*> ?: return null
        return FieldTournament(number(row[0]), number(row[1]))
    }

    override fun book(id: Long): EntryBook = entries.load(id, false)

    override fun selection(key: FieldKey): EntryChoice? =
        entries.selection(key.tournamentId, key.disciplineId, key.bracketId)

    override fun load(key: FieldKey, lock: Boolean): ParticipantField {
        if (lock) ensureField(key)
        val lockClause = if (lock) " FOR UPDATE" else ""
        val row = query(
            """
            SELECT version,discipline_name,bracket_name,team_size,source_tournament_version,source_entry_book_version
            FROM tournamentmgmt.participant_fields
            WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket$lockClause
            """.trimIndent(), key
        ).resultList.firstOrNull() as? Array<*> ?: return ParticipantField(key)
        val version = number(row[0])
        if (version == 0L) return ParticipantField(key)
        return ParticipantField(
            key, version, choice(key, row[1], row[2], row[3]), number(row[4]), number(row[5]),
            loadUnits(key), loadAudit(key)
        )
    }

    private fun ensureField(key: FieldKey) {
        query(
            """
            INSERT INTO tournamentmgmt.participant_fields(tournament_id,discipline_id,bracket_id,version)
            VALUES (:id,:discipline,:bracket,0)
            ON CONFLICT (tournament_id,discipline_id,bracket_id) DO NOTHING
            """.trimIndent(), key
        ).executeUpdate()
    }

    private fun loadUnits(key: FieldKey): List<FieldUnit> = query(
        """
        SELECT entry_id,discipline_name,bracket_name,team_size FROM tournamentmgmt.participant_field_units
        WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket ORDER BY entry_id
        """.trimIndent(), key
    ).resultList.map {
        val row = it as Array<*>
        val entryId = number(row[0])
        FieldUnit(entryId, choice(key, row[1], row[2], row[3]), loadMembers(key, entryId))
    }

    private fun loadMembers(key: FieldKey, entryId: Long): List<EntryMember> = query(
        """
        SELECT player_key,display_name FROM tournamentmgmt.participant_field_members
        WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket AND entry_id=:entry
        ORDER BY ordinal
        """.trimIndent(), key
    ).setParameter("entry", entryId).resultList.map {
        val row = it as Array<*>
        EntryMember(row[0] as String, row[1] as String)
    }

    private fun loadAudit(key: FieldKey): List<FieldHistory> = query(
        """
        SELECT field_version,actor_id,occurred_at,unit_count,source_tournament_version,source_entry_book_version
        FROM tournamentmgmt.participant_field_audit
        WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket ORDER BY field_version
        """.trimIndent(), key
    ).resultList.map {
        val row = it as Array<*>
        FieldHistory(number(row[0]), number(row[1]), instant(row[2]), (row[3] as Number).toInt(), number(row[4]), number(row[5]))
    }

    override fun save(field: ParticipantField) {
        advanceRoot(field)
        query(
            "DELETE FROM tournamentmgmt.participant_field_members WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket",
            field.key
        ).executeUpdate()
        query(
            "DELETE FROM tournamentmgmt.participant_field_units WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket",
            field.key
        ).executeUpdate()
        field.units.forEach { saveUnit(field.key, it) }
        appendAudit(field)
    }

    private fun advanceRoot(field: ParticipantField) {
        val target = requireNotNull(field.selection)
        val count = query(
            """
            UPDATE tournamentmgmt.participant_fields SET version=:version,discipline_name=:disciplineName,
                bracket_name=:bracketName,team_size=:size,source_tournament_version=:tournamentVersion,
                source_entry_book_version=:bookVersion
            WHERE tournament_id=:id AND discipline_id=:discipline AND bracket_id=:bracket AND version=:expected
            """.trimIndent(), field.key
        ).setParameter("version", field.version).setParameter("expected", field.version - 1)
            .setParameter("disciplineName", target.disciplineName).setParameter("bracketName", target.bracketName)
            .setParameter("size", target.teamSize.name)
            .setParameter("tournamentVersion", field.sourceTournamentVersion)
            .setParameter("bookVersion", field.sourceEntryBookVersion).executeUpdate()
        fieldConflictIf(count != 1, "Participant-field version mismatch")
    }

    private fun saveUnit(key: FieldKey, unit: FieldUnit) {
        query(
            """
            INSERT INTO tournamentmgmt.participant_field_units
                (tournament_id,discipline_id,bracket_id,entry_id,discipline_name,bracket_name,team_size)
            VALUES (:id,:discipline,:bracket,:entry,:disciplineName,:bracketName,:size)
            """.trimIndent(), key
        ).setParameter("entry", unit.entryId).setParameter("disciplineName", unit.selection.disciplineName)
            .setParameter("bracketName", unit.selection.bracketName).setParameter("size", unit.selection.teamSize.name)
            .executeUpdate()
        unit.players.forEachIndexed { ordinal, player ->
            query(
                """
                INSERT INTO tournamentmgmt.participant_field_members
                    (tournament_id,discipline_id,bracket_id,entry_id,ordinal,player_key,display_name)
                VALUES (:id,:discipline,:bracket,:entry,:ordinal,:key,:name)
                """.trimIndent(), key
            ).setParameter("entry", unit.entryId).setParameter("ordinal", ordinal)
                .setParameter("key", player.key).setParameter("name", player.displayName).executeUpdate()
        }
    }

    private fun appendAudit(field: ParticipantField) {
        val event = field.audit.last()
        query(
            """
            INSERT INTO tournamentmgmt.participant_field_audit
                (tournament_id,discipline_id,bracket_id,field_version,actor_id,occurred_at,operation,
                 unit_count,source_tournament_version,source_entry_book_version)
            VALUES (:id,:discipline,:bracket,:version,:actor,:at,'PREPARE',:count,:tournamentVersion,:bookVersion)
            """.trimIndent(), field.key
        ).setParameter("version", event.version).setParameter("actor", event.actorId).setParameter("at", event.at)
            .setParameter("count", event.unitCount).setParameter("tournamentVersion", event.sourceTournamentVersion)
            .setParameter("bookVersion", event.sourceEntryBookVersion).executeUpdate()
    }

    private fun query(sql: String, key: FieldKey): Query = em.createNativeQuery(sql)
        .setParameter("id", key.tournamentId).setParameter("discipline", key.disciplineId).setParameter("bracket", key.bracketId)

    private fun choice(key: FieldKey, disciplineName: Any?, bracketName: Any?, size: Any?): EntryChoice =
        EntryChoice(key.disciplineId, disciplineName as String, key.bracketId, bracketName as String, TeamSize.valueOf(size as String))

    private fun number(value: Any?): Long = (value as Number).toLong()

    private fun instant(value: Any?): Instant = when (value) {
        is Instant -> value
        is Timestamp -> value.toInstant()
        is OffsetDateTime -> value.toInstant()
        else -> error("Unexpected participant-field timestamp type")
    }
}
