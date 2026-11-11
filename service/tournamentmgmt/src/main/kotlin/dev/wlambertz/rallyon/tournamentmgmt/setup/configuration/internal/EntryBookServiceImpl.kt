package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryAudit
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryBookService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryBookView
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryInput
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryNotFound
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryPlayer
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryRecord
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntrySelection
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryState
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryBook
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryChoice
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryHistory
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntrySnapshot
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryDisposition
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMember
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.EntryVersionConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.EntryBookStore
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.EntryBookUseCase
import java.sql.SQLException
import java.time.Clock
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@Service
class EntryBookServiceImpl(
    store: EntryBookStore,
    tournamentClock: Clock,
    transactionManager: PlatformTransactionManager
) : EntryBookService {
    private val useCase = EntryBookUseCase(store, tournamentClock)
    private val write = TransactionTemplate(transactionManager).apply {
        // Joining a caller's weaker transaction would silently discard the required snapshot isolation.
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ
    }
    private val read = TransactionTemplate(transactionManager).apply {
        propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
        isolationLevel = TransactionDefinition.ISOLATION_REPEATABLE_READ
        isReadOnly = true
    }

    override fun get(id: Long, actor: Long): EntryBookView = execute(read) { useCase.get(id, actor) }

    override fun intake(id: Long, open: Boolean, version: Long, actor: Long): EntryBookView = execute(write) {
        useCase.intake(id, open, version, actor)
    }

    override fun create(id: Long, input: EntryInput, version: Long, actor: Long): EntryBookView = execute(write) {
        useCase.record(id, input.disciplineId, input.bracketId, input.members(), version, actor)
    }

    override fun correct(
        id: Long, entryId: Long, input: EntryInput, version: Long, actor: Long
    ): EntryBookView = execute(write) {
        useCase.correct(id, entryId, input.disciplineId, input.bracketId, input.members(), version, actor)
    }

    override fun decide(
        id: Long, entryId: Long, state: EntryState, version: Long, actor: Long
    ): EntryBookView = execute(write) {
        useCase.decide(id, entryId, EntryDisposition.valueOf(state.name), version, actor)
    }

    override fun withdraw(id: Long, entryId: Long, version: Long, actor: Long): EntryBookView = execute(write) {
        useCase.withdraw(id, entryId, version, actor)
    }

    private fun EntryInput.members(): List<EntryMember>? = players?.map { member ->
        val required = requireNotNull(member) { "Player must not be null" }
        EntryMember(required.key, required.displayName)
    }

    private fun view(book: EntryBook): EntryBookView = EntryBookView(
        book.tournamentId, book.intakeOpen, book.version,
        book.records.map(::recordView), book.audit.map(::auditView)
    )

    private fun recordView(record: EntrySnapshot): EntryRecord = EntryRecord(
        record.id, selectionView(record.selection),
        record.players.map { EntryPlayer(it.key, it.displayName) }, EntryState.valueOf(record.state.name)
    )

    private fun selectionView(selection: EntryChoice): EntrySelection = EntrySelection(
        selection.disciplineId, selection.disciplineName, selection.bracketId, selection.bracketName,
        TeamSize.valueOf(selection.teamSize.name)
    )

    private fun auditView(event: EntryHistory): EntryAudit = EntryAudit(
        event.entryId, event.operation, event.previousState?.let { EntryState.valueOf(it.name) },
        event.resultingState?.let { EntryState.valueOf(it.name) }, event.actorId, event.at
    )

    private fun execute(template: TransactionTemplate, operation: () -> EntryBook): EntryBookView = try {
        view(requireNotNull(template.execute { operation() }))
    } catch (_: EntryDenied) {
        throw EntryForbidden("Entry-book access denied")
    } catch (_: EntryMissing) {
        throw EntryNotFound("Entry or tournament not found")
    } catch (error: EntryVersionConflict) {
        throw EntryConflict(error.message ?: "Entry-book conflict")
    } catch (error: IllegalArgumentException) {
        throw EntryInvalid(error.message ?: "Invalid entry")
    } catch (_: ConcurrencyFailureException) {
        throw EntryConflict("Concurrent entry-book mutation")
    } catch (_: DataIntegrityViolationException) {
        throw EntryConflict("Conflicting entry-book mutation")
    } catch (error: RuntimeException) {
        if (isDatabaseConflict(error)) {
            throw EntryConflict("Concurrent entry-book mutation")
        }
        throw error
    }

    private fun isDatabaseConflict(error: RuntimeException): Boolean =
        generateSequence<Throwable>(error) { it.cause }.any {
            it is SQLException && it.sqlState in setOf("40001", "40P01", "23505")
        }
}
