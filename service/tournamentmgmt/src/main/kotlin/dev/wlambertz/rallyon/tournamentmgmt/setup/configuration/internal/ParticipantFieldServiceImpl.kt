package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryPlayer
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntrySelection
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.FieldFreshness
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldAudit
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldNotFound
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldRequest
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldUnit
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldView
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldObservation
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.FieldVersionConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.FieldPreparation
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.ParticipantFieldStore
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.ParticipantFieldUseCase
import java.sql.SQLException
import java.time.Clock
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Service
class ParticipantFieldServiceImpl(
    store: ParticipantFieldStore,
    tournamentClock: Clock,
    transactionManager: PlatformTransactionManager
) : ParticipantFieldService {
    private val useCase = ParticipantFieldUseCase(store, tournamentClock)
    private val write = TransactionTemplate(transactionManager).apply {
        // Caller transactions must not silently weaken the field/source snapshot guarantee.
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ
    }
    private val read = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ
        isReadOnly = true
    }

    override fun get(id: Long, disciplineId: Long, bracketId: String, actor: Long): ParticipantFieldView = execute(read) {
        useCase.get(id, disciplineId, bracketId, actor)
    }

    override fun prepare(
        id: Long, input: ParticipantFieldRequest, version: Long, actor: Long
    ): ParticipantFieldView = execute(write) {
        useCase.prepare(
            id, FieldPreparation(
                input.disciplineId, input.bracketId, input.entryIds,
                input.sourceTournamentVersion, input.sourceEntryBookVersion
            ), version, actor
        )
    }

    private fun view(observation: FieldObservation): ParticipantFieldView {
        val field = observation.field
        return ParticipantFieldView(
            field.key.tournamentId, field.key.disciplineId, field.key.bracketId, field.version > 0, field.version,
            field.selection?.let(::selectionView), field.sourceTournamentVersion, field.sourceEntryBookVersion,
            field.units.map { unit ->
                ParticipantFieldUnit(
                    unit.entryId, selectionView(unit.selection),
                    unit.players.map { EntryPlayer(it.key, it.displayName) }
                )
            },
            field.audit.map { event ->
                ParticipantFieldAudit(
                    event.version, event.actorId, event.at, "PREPARE", event.unitCount,
                    event.sourceTournamentVersion, event.sourceEntryBookVersion
                )
            },
            observation.observedTournamentVersion, observation.observedEntryBookVersion,
            FieldFreshness.valueOf(observation.currency.name)
        )
    }

    private fun selectionView(selection: EntryChoice): EntrySelection = EntrySelection(
        selection.disciplineId, selection.disciplineName, selection.bracketId, selection.bracketName,
        TeamSize.valueOf(selection.teamSize.name)
    )

    private fun execute(template: TransactionTemplate, operation: () -> FieldObservation): ParticipantFieldView = try {
        view(requireNotNull(template.execute { operation() }))
    } catch (_: FieldDenied) {
        throw ParticipantFieldForbidden("Participant-field access denied")
    } catch (_: FieldMissing) {
        throw ParticipantFieldNotFound("Participant field or tournament not found")
    } catch (error: FieldVersionConflict) {
        throw ParticipantFieldConflict(error.message ?: "Participant-field conflict")
    } catch (error: IllegalArgumentException) {
        throw ParticipantFieldInvalid(error.message ?: "Invalid participant field")
    } catch (_: ConcurrencyFailureException) {
        throw ParticipantFieldConflict("Concurrent participant-field preparation")
    } catch (_: DataIntegrityViolationException) {
        throw ParticipantFieldConflict("Conflicting participant-field preparation")
    } catch (error: RuntimeException) {
        if (isDatabaseConflict(error)) throw ParticipantFieldConflict("Concurrent participant-field preparation")
        throw error
    }

    private fun isDatabaseConflict(error: RuntimeException): Boolean =
        generateSequence<Throwable>(error) { it.cause }.any {
            it is SQLException && it.sqlState in setOf("40001", "40P01", "23505")
        }
}
