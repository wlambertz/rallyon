package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import java.time.Instant

data class ParticipantFieldRequest(
    val disciplineId: Long?,
    val bracketId: String?,
    val entryIds: List<Long?>?,
    val sourceTournamentVersion: Long?,
    val sourceEntryBookVersion: Long?
)

enum class FieldFreshness { UNPREPARED, CURRENT, OUTDATED }
enum class CapacityReview { MANUAL }

data class ParticipantFieldUnit(
    val entryId: Long,
    val selection: EntrySelection,
    val players: List<EntryPlayer>
)

data class ParticipantFieldAudit(
    val version: Long,
    val actorId: Long,
    val at: Instant,
    val operation: String,
    val unitCount: Int,
    val sourceTournamentVersion: Long,
    val sourceEntryBookVersion: Long
)

data class ParticipantFieldView(
    val tournamentId: Long,
    val disciplineId: Long,
    val bracketId: String,
    val prepared: Boolean,
    val version: Long,
    val selection: EntrySelection?,
    val sourceTournamentVersion: Long?,
    val sourceEntryBookVersion: Long?,
    val units: List<ParticipantFieldUnit>,
    val audit: List<ParticipantFieldAudit>,
    val observedTournamentVersion: Long,
    val observedEntryBookVersion: Long,
    val freshness: FieldFreshness,
    val capacityReview: CapacityReview = CapacityReview.MANUAL
)

interface ParticipantFieldService {
    fun get(id: Long, disciplineId: Long, bracketId: String, actor: Long): ParticipantFieldView
    fun prepare(id: Long, input: ParticipantFieldRequest, version: Long, actor: Long): ParticipantFieldView
}

class ParticipantFieldInvalid(message: String) : RuntimeException(message)
class ParticipantFieldForbidden(message: String) : RuntimeException(message)
class ParticipantFieldNotFound(message: String) : RuntimeException(message)
class ParticipantFieldConflict(message: String) : RuntimeException(message)
