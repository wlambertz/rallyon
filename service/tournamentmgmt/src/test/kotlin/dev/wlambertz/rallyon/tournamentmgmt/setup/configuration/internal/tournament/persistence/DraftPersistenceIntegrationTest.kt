package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.RollbackException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest(properties = ["spring.jpa.open-in-view=false"])
@Import(DraftPersistenceIntegrationTest.FixedClockConfiguration::class)
class DraftPersistenceIntegrationTest {
    @Autowired private lateinit var service: ConfigurationService
    @Autowired private lateinit var repository: TournamentRepository
    @Autowired private lateinit var mapper: TournamentMapper
    @Autowired private lateinit var transactions: PlatformTransactionManager
    @Autowired private lateinit var entityManagers: EntityManagerFactory

    private fun read(id: Long): Tournament = TransactionTemplate(transactions).execute {
        mapper.toApi(repository.findById(id).orElseThrow())
    }!!

    @Test
    fun `retrieves nullable draft without changing its stored snapshot`() {
        val created = service.createDraft(5, "Partial draft", Visibility.PRIVATE, 12)
        val stored = read(created.id)

        val retrieved = service.get(created.id)

        assertThat(retrieved).isEqualTo(stored)
        assertThat(retrieved.id).isEqualTo(created.id)
        assertThat(retrieved.name).isEqualTo("Partial draft")
        assertThat(retrieved.visibility).isEqualTo(Visibility.PRIVATE)
        assertThat(retrieved.status).isEqualTo(TournamentStatus.DRAFT)
        assertThat(retrieved.description).isNull()
        assertThat(retrieved.schedule).isNull()
        assertThat(retrieved.venue).isNull()
        assertThat(retrieved.capacity).isNull()
        assertThat(retrieved.scoringRules).isNull()
        assertThat(retrieved.courts).isEmpty()
        assertThat(retrieved.disciplines).isEmpty()
        assertThat(service.get(created.id)).isEqualTo(stored)
        assertThat(read(created.id)).isEqualTo(stored)
    }

    @Test
    fun `retrieves nested published configuration after transaction closes without changing child identities`() {
        val created = service.createDraft(5, "Nested read", Visibility.PRIVATE, 12)
        val bracket = BracketConfig(BracketId("main"), "Main", TournamentFormat.SWISS, Capacity(16, Capacity.Unit.PARTICIPANTS))
        val discipline = DisciplineConfig(11, Category.SINGLES, "Singles", TeamSize.SINGLES, listOf(bracket))
        val window = TimeWindow(Instant.parse("2026-11-01T09:00:00Z"), Instant.parse("2026-11-01T17:00:00Z"))
        val replacement = changes().copy(
            schedule = window, registrationWindows = listOf(window),
            venue = Venue("Sports hall", Venue.Address("Court Street 1", "12345", "Bonn"), Capacity(100, Capacity.Unit.PEOPLE)),
            courts = listOf(court(0, "A")), disciplines = listOf(discipline),
            participants = ParticipantsRoster(listOf(1001, 1002), null),
            bracketRosters = mapOf(bracket.id to ParticipantsRoster(listOf(1001), null))
        )
        service.updateDraft(created.id, replacement, created.version!!, 42)
        TransactionTemplate(transactions).executeWithoutResult {
            repository.findById(created.id).orElseThrow().status = TournamentStatus.PUBLISHED
        }
        val stored = read(created.id)
        val identifiers = persistedChildIds(created.id)
        assertThat(identifiers.values.flatten()).doesNotContainNull()
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse()

        repeat(2) {
            val retrieved = service.get(created.id)

            assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse()
            assertThat(TransactionSynchronizationManager.hasResource(entityManagers)).isFalse()
            assertThat(retrieved).isEqualTo(stored)
            assertThat(retrieved.status).isEqualTo(TournamentStatus.PUBLISHED)
            assertThat(retrieved.schedule).isEqualTo(window)
            assertThat(retrieved.registrationWindows).containsExactly(window)
            assertThat(retrieved.venue).isEqualTo(replacement.venue)
            assertThat(retrieved.courts!!.single().id).isPositive()
            assertThat(retrieved.courts!!.single().label).isEqualTo("A")
            assertThat(retrieved.disciplines).containsExactly(discipline)
            assertThat(retrieved.participants!!.playerIds).containsExactly(1001, 1002)
            assertThat(retrieved.bracketRosters!![bracket.id]!!.playerIds).containsExactly(1001)
            assertThat(persistedChildIds(created.id)).isEqualTo(identifiers)
            assertThat(read(created.id)).isEqualTo(stored)
        }
    }

    @Test
    fun `retrieves committed replacement with persisted version and audit values`() {
        val created = service.createDraft(5, "Before replacement", Visibility.PRIVATE, 12)
        val before = service.get(created.id)
        val response = service.updateDraft(created.id, changes().copy(courts = listOf(court(0, "Replacement"))), before.version!!, 43)
        val committed = read(created.id)

        val retrieved = service.get(created.id)

        // The update response precedes flush; GET must expose the committed version and timestamp precision.
        assertThat(response.version).isEqualTo(before.version)
        assertThat(retrieved.version).isEqualTo(before.version!! + 1)
        assertThat(retrieved).isEqualTo(committed)
        assertThat(retrieved.name).isEqualTo("Updated")
        assertThat(retrieved.visibility).isEqualTo(Visibility.PUBLIC)
        assertThat(retrieved.courts!!.single().label).isEqualTo("Replacement")
        assertThat(retrieved.createdAt).isEqualTo(before.createdAt)
        assertThat(retrieved.createdByUserId).isEqualTo(12)
        assertThat(retrieved.lastModifiedAt).isEqualTo(committed.lastModifiedAt)
        assertThat(retrieved.lastModifiedByUserId).isEqualTo(43)
        assertThat(service.get(created.id)).isEqualTo(committed)
        assertThat(read(created.id)).isEqualTo(committed)
    }

    private fun persistedChildIds(id: Long): Map<String, List<Long?>> = TransactionTemplate(transactions).execute {
        val entity = repository.findById(id).orElseThrow()
        val brackets = entity.disciplines.flatMap { it.brackets }
        mapOf(
            "registrationWindows" to entity.registrationWindows.map { it.id },
            "courts" to entity.courts.map { it.id },
            "disciplines" to entity.disciplines.map { it.id },
            "brackets" to brackets.map { it.id },
            "participants" to entity.participants.map { it.id },
            "bracketParticipants" to brackets.flatMap { it.participants }.map { it.id }
        )
    }!!

    @Test
    fun `round trips replacement retaining child ids and response version timing`() {
        val created = service.createDraft(5, "Persistence Cup", Visibility.PRIVATE, 12)
        assertThat(created.createdAt).isEqualTo(FIXED_INSTANT)
        val storedCreation = read(created.id)
        val first = service.updateDraft(created.id, changes().copy(courts = listOf(court(0, "A"), court(0, "B"))), created.version!!, 42)
        assertThat(first.version).isEqualTo(created.version)
        val persisted = read(created.id)
        assertThat(persisted.version).isEqualTo(created.version!! + 1)
        assertThat(persisted.courts!!.map { it.label }).containsExactly("A", "B")
        val retainedId = persisted.courts!![1].id
        service.updateDraft(created.id, changes().copy(courts = listOf(court(retainedId, "B retained"))), persisted.version!!, 43)
        val replaced = read(created.id)
        assertThat(replaced.courts!!.map { it.id }).containsExactly(retainedId)
        assertThat(replaced.courts!![0].label).isEqualTo("B retained")
        // Persistence precision differs from the response's nanoseconds; compare stored values.
        assertThat(replaced.createdAt).isEqualTo(storedCreation.createdAt)
        assertThat(replaced.createdByUserId).isEqualTo(12)
        assertThat(replaced.lastModifiedByUserId).isEqualTo(43)
    }

    @Test
    fun `reconciles nested disciplines brackets and rosters without changing retained identities`() {
        val created = service.createDraft(5, "Nested", Visibility.PRIVATE, 12)
        val bracket = BracketConfig(BracketId("main"), "Main", TournamentFormat.SWISS, null)
        val discipline = DisciplineConfig(11, Category.SINGLES, "Singles", TeamSize.SINGLES, listOf(bracket))
        val replacement = changes().copy(
            disciplines = listOf(discipline), participants = ParticipantsRoster(listOf(1001, 1002), null),
            bracketRosters = mapOf(bracket.id to ParticipantsRoster(listOf(1001), null))
        )
        service.updateDraft(created.id, replacement, created.version!!, 42)
        val before = childIds(created.id)
        assertThat(before.first).isNotNull()
        assertThat(before.second).isNotNull()
        val persisted = read(created.id)
        assertThat(persisted.participants!!.playerIds).containsExactly(1001, 1002)
        assertThat(persisted.bracketRosters!![bracket.id]!!.playerIds).containsExactly(1001)
        service.updateDraft(created.id, replacement.copy(
            disciplines = listOf(discipline.copy(brackets = listOf(bracket.copy(displayName = "Renamed"))))
        ), persisted.version!!, 43)
        assertThat(childIds(created.id)).isEqualTo(before)
        assertThat(read(created.id).disciplines!![0].brackets[0].displayName).isEqualTo("Renamed")
        val version = read(created.id).version!!
        service.updateDraft(created.id, changes(), version, 43)
        val cleared = read(created.id)
        assertThat(cleared.disciplines).isEmpty()
        assertThat(cleared.bracketRosters).isEmpty()
        assertThat(cleared.participants!!.teamIds).isEmpty()
    }

    private fun childIds(id: Long): Pair<Long?, Long?> = TransactionTemplate(transactions).execute {
        val discipline = repository.findById(id).orElseThrow().disciplines.single()
        discipline.id to discipline.brackets.single().id
    }!!

    @Test
    fun `rejected replacement rolls back and stale update leaves persisted data unchanged`() {
        val created = service.createDraft(5, "Original", Visibility.PRIVATE, 12)
        assertThrows(InvalidDraftUpdateException::class.java) {
            service.updateDraft(created.id, changes().copy(courts = listOf(court(999999, "Unknown"))), created.version!!, 42)
        }
        val afterFailure = read(created.id)
        assertThat(afterFailure.name).isEqualTo("Original")
        assertThat(afterFailure.version).isEqualTo(created.version)
        assertThat(afterFailure.lastModifiedByUserId).isEqualTo(12)
        service.updateDraft(created.id, changes(), created.version!!, 42)
        assertThrows(DraftUpdateConflictException::class.java) {
            service.updateDraft(created.id, changes().copy(name = "Stale"), created.version!!, 43)
        }
        assertThat(read(created.id).name).isEqualTo("Updated")
    }

    @Test
    fun `hibernate detects a concurrent write after both transactions read the same version`() {
        val created = service.createDraft(5, "Concurrent", Visibility.PRIVATE, 12)
        val first = entityManagers.createEntityManager()
        val second = entityManagers.createEntityManager()
        try {
            first.transaction.begin()
            second.transaction.begin()
            val a = first.find(TournamentEntity::class.java, created.id)
            val b = second.find(TournamentEntity::class.java, created.id)
            a.name = "Winner"
            first.transaction.commit()
            b.name = "Loser"
            assertThrows(RollbackException::class.java) { second.transaction.commit() }
            assertThat(read(created.id).name).isEqualTo("Winner")
        } finally {
            if (first.transaction.isActive) first.transaction.rollback()
            if (second.transaction.isActive) second.transaction.rollback()
            first.close()
            second.close()
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    class FixedClockConfiguration {
        @Bean
        @Primary
        fun fixedClock(): Clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC)
    }

    companion object {
        private val FIXED_INSTANT = Instant.parse("2026-10-30T10:00:00.123456789Z")
    }

    private fun changes() = Tournament(name = "Updated", visibility = Visibility.PUBLIC)
    private fun court(id: Long, name: String) = Court(id, name, Court.Availability.AVAILABLE, Court.Type.STANDARD)
}
