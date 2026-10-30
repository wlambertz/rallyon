package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.*
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class DraftUseCaseTest {
    private val now = Instant.parse("2026-10-30T10:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val original = createDraft(5, "Original", Visibility.PRIVATE, 12, now.minusSeconds(60)).copy(id = 10, version = 3)
    private val changes = Tournament(name = "Updated", visibility = Visibility.PUBLIC)
    private val store = MemoryStore(original)
    private val update = UpdateDraftService(store, clock)

    @Test
    fun `create initializes draft audit data with injected clock`() {
        val result = CreateDraftService(store, clock).execute(5, "New", Visibility.PRIVATE, 42)
        assertThat(result.status).isEqualTo(TournamentStatus.DRAFT)
        assertThat(result.version).isZero()
        assertThat(result.createdAt).isEqualTo(now)
        assertThat(result.lastModifiedAt).isEqualTo(now)
        assertThat(result.createdByUserId).isEqualTo(42)
        assertThat(store.expectedVersion).isNull()
    }

    @Test
    fun `invalid creation never saves`() {
        val create = CreateDraftService(store, clock)
        listOf(null, " ", "x".repeat(201)).forEach { name ->
            assertThrows(IllegalArgumentException::class.java) { create.execute(5, name, Visibility.PRIVATE, 42) }
        }
        assertThrows(NullPointerException::class.java) { create.execute(5, "Valid", null, 42) }
        assertThat(store.saves).isZero()
    }

    @Test
    fun `replacement preserves identity and creation metadata`() {
        val result = update.execute(10, changes.copy(id = 99, organizerId = 99), 3, 42)
        assertThat(result.id).isEqualTo(10)
        assertThat(result.organizerId).isEqualTo(5)
        assertThat(result.createdAt).isEqualTo(original.createdAt)
        assertThat(result.createdByUserId).isEqualTo(12)
        assertThat(result.lastModifiedAt).isEqualTo(now)
        assertThat(result.lastModifiedByUserId).isEqualTo(42)
        assertThat(store.expectedVersion).isEqualTo(3)
    }

    @Test
    fun `invalid basics are rejected before lookup`() {
        listOf(changes.copy(name = null), changes.copy(name = " "), changes.copy(name = "x".repeat(201)),
            changes.copy(visibility = null), changes.copy(phases = listOf(object : Phase {}))).forEach {
            assertThrows(InvalidDraftUpdateException::class.java) { update.execute(10, it, 3, 42) }
        }
        assertThat(store.lookups).isZero()
        assertThat(store.saves).isZero()
    }

    @Test
    fun `missing non draft and stale versions never save`() {
        store.current = null
        assertThrows(TournamentNotFoundException::class.java) { update.execute(10, changes, 3, 42) }
        store.current = original.copy(status = TournamentStatus.PUBLISHED)
        assertThrows(DraftUpdateConflictException::class.java) { update.execute(10, changes, 3, 42) }
        store.current = original
        assertThrows(DraftUpdateConflictException::class.java) { update.execute(10, changes, 2, 42) }
        assertThat(store.saves).isZero()
    }

    @Test
    fun `domain rejects invalid replacement values without saving`() {
        val court = Court(0, "A", Court.Availability.AVAILABLE, Court.Type.STANDARD)
        val bracket = BracketConfig(BracketId("main"), "Main", TournamentFormat.SWISS, null)
        val discipline = DisciplineConfig(11, Category.SINGLES, "Singles", TeamSize.SINGLES, listOf(bracket))
        listOf(
            changes.copy(courts = listOf(court, court)),
            changes.copy(courts = listOf(court.copy(id = 99))),
            changes.copy(courts = listOf(null)),
            changes.copy(courts = listOf(court.copy(id = -1))),
            changes.copy(courts = listOf(court.copy(label = " "))),
            changes.copy(courts = listOf(court.copy(availability = null))),
            changes.copy(courts = listOf(court.copy(type = null))),
            changes.copy(disciplines = listOf(discipline, discipline)),
            changes.copy(disciplines = listOf(null)),
            changes.copy(disciplines = listOf(discipline.copy(displayName = " "))),
            changes.copy(disciplines = listOf(discipline.copy(brackets = listOf(null)))),
            changes.copy(disciplines = listOf(discipline.copy(brackets = listOf(bracket.copy(displayName = " "))))),
            changes.copy(disciplines = listOf(discipline, discipline.copy(id = 12))),
            changes.copy(disciplines = listOf(discipline.copy(brackets = listOf(bracket, bracket)))),
            changes.copy(participants = ParticipantsRoster(listOf(1, 1), null)),
            changes.copy(participants = ParticipantsRoster(listOf(null), null)),
            changes.copy(participants = ParticipantsRoster(null, null)),
            changes.copy(participants = ParticipantsRoster(listOf(1), listOf(2))),
            changes.copy(participants = ParticipantsRoster(null, listOf(0))),
            changes.copy(participants = ParticipantsRoster(null, listOf(2, 2))),
            changes.copy(bracketRosters = mapOf(BracketId("unknown") to ParticipantsRoster(listOf(1), null))),
            changes.copy(bracketRosters = mapOf(BracketId("main") to null)),
            changes.copy(disciplines = listOf(discipline), bracketRosters = mapOf(BracketId("main") to ParticipantsRoster(null, listOf(1)))),
            changes.copy(schedule = TimeWindow(now, now.minusSeconds(1))),
            changes.copy(schedule = TimeWindow(null, now)),
            changes.copy(registrationWindows = listOf(TimeWindow(now, null))),
            changes.copy(capacity = Capacity(5, null)),
            changes.copy(venue = Venue(" ", null, null)),
            changes.copy(venue = Venue("Hall", Venue.Address(null, "12345", "City"), null)),
            changes.copy(venue = Venue("Hall", Venue.Address("Street", "1234", "City"), null)),
            changes.copy(venue = Venue("Hall", Venue.Address("Street", "12345", " "), null)),
            changes.copy(capacity = Capacity(0, Capacity.Unit.PARTICIPANTS)),
            changes.copy(venue = Venue("Hall", null, Capacity(5, Capacity.Unit.PARTICIPANTS)))
        ).forEach { invalid ->
            assertThrows(InvalidDraftUpdateException::class.java) { update.execute(10, invalid, 3, 42) }
        }
        assertThat(store.saves).isZero()
        assertThat(store.current).isEqualTo(original)
    }

    @Test
    fun `partial draft values and empty team rosters retain existing acceptance`() {
        val bracket = BracketConfig(BracketId("main"), "Main", TournamentFormat.SWISS, Capacity(null, null))
        val doubles = DisciplineConfig(11, Category.DOUBLES, "Doubles", TeamSize.DOUBLES, listOf(bracket))
        val updated = update.execute(10, changes.copy(
            schedule = TimeWindow(now, now), capacity = Capacity(null, null),
            disciplines = listOf(doubles), participants = ParticipantsRoster(null, emptyList()),
            bracketRosters = mapOf(bracket.id to ParticipantsRoster(null, emptyList()))
        ), 3, 42)
        assertThat(updated.participants!!.teamIds).isEmpty()
        assertThat(updated.schedule!!.start).isEqualTo(updated.schedule.end)
    }

    private class MemoryStore(var current: Tournament?) : TournamentStore {
        var lookups = 0
        var saves = 0
        var expectedVersion: Long? = null
        override fun findById(id: Long): Tournament? { lookups++; return current }
        override fun save(tournament: Tournament, expectedVersion: Long?): Tournament {
            saves++
            this.expectedVersion = expectedVersion
            current = tournament
            return tournament
        }
    }
}
