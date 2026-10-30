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
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
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
