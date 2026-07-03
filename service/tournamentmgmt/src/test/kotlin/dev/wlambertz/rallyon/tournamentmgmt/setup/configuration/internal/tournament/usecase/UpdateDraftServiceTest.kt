package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.CourtEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.DisciplineEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
import java.time.Instant
import java.util.Locale
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mapstruct.factory.Mappers
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension

/**
 * Kotlin interop test for the Java draft update use case, repository contract, records, and MapStruct mapper.
 */
@ExtendWith(MockitoExtension::class)
class UpdateDraftServiceTest {

    @Mock
    private lateinit var tournamentRepository: TournamentRepository

    private lateinit var updateDraftService: UpdateDraftService

    @BeforeEach
    fun setUp() {
        updateDraftService = UpdateDraftService(
            tournamentRepository,
            Mappers.getMapper(TournamentMapper::class.java)
        )
    }

    @Test
    fun updatesDraftAndReplacesOwnedFields() {
        val entity = givenExistingDraft()
        Mockito.`when`(tournamentRepository.save(ArgumentMatchers.any(TournamentEntity::class.java)))
            .thenAnswer { invocation -> invocation.getArgument(0, TournamentEntity::class.java) }

        val draftChanges = baseDraftBuilder()
            .description("Fresh description")
            .locale(Locale.GERMANY)
            .schedule(timeWindow("2026-03-20T10:00:00Z", "2026-03-20T18:00:00Z"))
            .registrationWindows(listOf(timeWindow("2026-03-01T10:00:00Z", "2026-03-10T10:00:00Z")))
            .venue(olympicArena())
            .courts(replacementCourts())
            .disciplines(listOf(singlesDiscipline(TournamentFormat.KO_POULE, Capacity(32, Capacity.Unit.PARTICIPANTS))))
            .capacity(Capacity(64, Capacity.Unit.PARTICIPANTS))
            .registrationPolicy(RegistrationPolicy.OPEN)
            .schedulingPolicy(SchedulingPolicy.MAX_PARALLEL_MATCHES)
            .courtAllocationPolicy(CourtAllocationPolicy.SEQUENTIAL)
            .scoringRules(ScoringRules.twoByTwentyOne())
            .tieBreakRules(TieBreakRules.headToHead())
            .matchDurationPolicy(MatchDurationPolicy.FIXED_TIMEBOX)
            .seedingPolicy(SeedingPolicy.MANUAL)
            .participants(ParticipantsRoster(listOf(1001L, 1002L), null))
            .bracketRosters(mapOf(BracketId("main") to ParticipantsRoster(listOf(3001L), null)))
            .phases(listOf())
            .build()

        val updated = executeUpdate(draftChanges)

        assertEquals(UPDATED_CUP_NAME, updated.name())
        assertEquals(Visibility.PUBLIC, updated.visibility())
        assertEquals(Locale.GERMANY.language, updated.locale().language)
        assertEquals(2, updated.courts().size)
        assertEquals(1, updated.registrationWindows().size)
        assertEquals(1, updated.disciplines().size)
        assertEquals(listOf(1001L, 1002L), updated.participants().playerIds())
        assertEquals(listOf(3001L), updated.bracketRosters()[BracketId("main")]!!.playerIds())
        assertEquals(ACTING_USER_ID, updated.lastModifiedByUserId())
        assertNotNull(updated.lastModifiedAt())

        assertEquals(UPDATED_CUP_NAME, entity.name)
        assertEquals(2, entity.courts.size)
        assertEquals(1, entity.disciplines.size)
        assertSame(entity.courts.first(), entity.courts.stream()
            .filter { court -> court.id != null && court.id == 200L }
            .findFirst()
            .orElseThrow())
        assertEquals(2, entity.participants.stream().filter { participant -> participant.category == null }.count())
        assertEquals(1, entity.disciplines.first().brackets.first().participants.size)

        verify(tournamentRepository).findById(TOURNAMENT_ID)
        verify(tournamentRepository).save(entity)
    }

    @Test
    fun rejectsMissingTournament() {
        Mockito.`when`(tournamentRepository.findById(TOURNAMENT_ID)).thenReturn(Optional.empty())

        val exception = assertThrows(TournamentNotFoundException::class.java) {
            executeUpdate(minimalDraft())
        }

        assertEquals("Tournament 10 was not found", exception.message)
        verifyOnlyDraftLookup()
    }

    @Test
    fun rejectsStaleVersion() {
        val entity = givenExistingDraft()
        entity.version = 4L

        val exception = assertThrows(DraftUpdateConflictException::class.java) {
            executeUpdate(minimalDraft())
        }

        assertEquals("Draft version mismatch: expected 4 but got 3", exception.message)
        verifyOnlyDraftLookup()
    }

    @Test
    fun rejectsNonDraftTournament() {
        val entity = givenExistingDraft()
        entity.status = TournamentStatus.PUBLISHED

        val exception = assertThrows(DraftUpdateConflictException::class.java) {
            executeUpdate(minimalDraft())
        }

        assertEquals("Only tournaments in DRAFT status can be updated", exception.message)
        verifyOnlyDraftLookup()
    }

    @Test
    fun rejectsNonEmptyPhases() {
        val exception = assertThrows(InvalidDraftUpdateException::class.java) {
            executeUpdate(
                baseDraftBuilder()
                    .phases(listOf(object : Phase {}))
                    .build()
            )
        }

        assertEquals("Phases are not supported by draft updates yet", exception.message)
        verifyNoMoreInteractions(tournamentRepository)
    }

    @Test
    fun rejectsUnknownCourtIds() {
        givenExistingDraft()

        val exception = assertThrows(InvalidDraftUpdateException::class.java) {
            executeUpdate(
                baseDraftBuilder()
                    .courts(listOf(Court(999L, "Court X", Court.Availability.AVAILABLE, Court.Type.STANDARD)))
                    .build()
            )
        }

        assertEquals("Unknown court id '999'", exception.message)
    }

    @Test
    fun rejectsUnknownBracketRosterKeys() {
        givenExistingDraft()

        val exception = assertThrows(InvalidDraftUpdateException::class.java) {
            executeUpdate(draftWithBracketRoster(BracketId("unknown"), ParticipantsRoster(listOf(3001L), null)))
        }

        assertEquals("Unknown bracket roster key 'unknown'", exception.message)
    }

    @Test
    fun rejectsRosterModeMismatchAgainstSinglesBracket() {
        givenExistingDraft()

        val exception = assertThrows(InvalidDraftUpdateException::class.java) {
            executeUpdate(draftWithBracketRoster(BracketId("main"), ParticipantsRoster(null, listOf(4001L))))
        }

        assertEquals(
            "Bracket roster 'main' must use playerIds for singles disciplines",
            exception.message
        )
    }

    private fun executeUpdate(draftChanges: Tournament): Tournament =
        updateDraftService.execute(TOURNAMENT_ID, draftChanges, EXPECTED_VERSION, ACTING_USER_ID)

    private fun givenExistingDraft(): TournamentEntity = existingDraftEntity().also { entity ->
        Mockito.`when`(tournamentRepository.findById(TOURNAMENT_ID)).thenReturn(Optional.of(entity))
    }

    private fun verifyOnlyDraftLookup() {
        verify(tournamentRepository).findById(TOURNAMENT_ID)
        verifyNoMoreInteractions(tournamentRepository)
    }

    private fun minimalDraft(): Tournament = baseDraftBuilder().build()

    private fun baseDraftBuilder() = Tournament.builder()
        .name(UPDATED_CUP_NAME)
        .visibility(Visibility.PUBLIC)

    private fun draftWithBracketRoster(bracketId: BracketId, roster: ParticipantsRoster): Tournament = baseDraftBuilder()
        .disciplines(listOf(singlesDiscipline(TournamentFormat.SWISS, null)))
        .bracketRosters(mapOf(bracketId to roster))
        .build()

    private fun timeWindow(start: String, end: String): TimeWindow = TimeWindow(Instant.parse(start), Instant.parse(end))

    private fun olympicArena(): Venue = Venue(
        "Olympic Arena",
        Venue.Address("Main Street 1", "12345", "Berlin"),
        Capacity(5000, Capacity.Unit.PEOPLE)
    )

    private fun replacementCourts(): List<Court> = listOf(
        Court(200L, "Court A", Court.Availability.AVAILABLE, Court.Type.STANDARD),
        Court(0L, "Court B", Court.Availability.UNAVAILABLE, Court.Type.SINGLES_ONLY)
    )

    private fun singlesDiscipline(format: TournamentFormat, capacity: Capacity?): DisciplineConfig = DisciplineConfig(
        11L,
        Category.SINGLES,
        "Singles",
        TeamSize.SINGLES,
        listOf(BracketConfig(BracketId("main"), "Main Draw", format, capacity))
    )

    private fun existingDraftEntity(): TournamentEntity {
        val createdAt = Instant.parse("2026-01-01T10:00:00Z")

        val entity = TournamentEntity()
        entity.id = 10L
        entity.organizerId = 5L
        entity.visibility = Visibility.PRIVATE
        entity.name = "Original Cup"
        entity.status = TournamentStatus.DRAFT
        entity.version = 3L
        entity.createdAt = createdAt
        entity.createdByUserId = 12L
        entity.lastModifiedAt = createdAt
        entity.lastModifiedByUserId = 12L

        val court = CourtEntity()
        court.id = 200L
        court.tournament = entity
        court.label = "Legacy Court"
        court.availability = Court.Availability.AVAILABLE
        court.type = Court.Type.STANDARD
        court.sortOrder = 0.toShort()
        entity.courts.add(court)

        val discipline = DisciplineEntity()
        discipline.tournament = entity
        discipline.disciplineId = 11L
        discipline.category = Category.SINGLES
        discipline.displayName = "Legacy Singles"
        discipline.teamSize = TeamSize.SINGLES

        val bracket = BracketEntity()
        bracket.discipline = discipline
        bracket.bracketId = "main"
        bracket.displayName = "Legacy Draw"
        bracket.format = TournamentFormat.SWISS
        discipline.brackets.add(bracket)
        entity.disciplines.add(discipline)

        val participant = ParticipantEntity()
        participant.tournament = entity
        participant.playerId = 999L
        participant.addedAt = createdAt
        participant.addedByUserId = 12L
        entity.participants.add(participant)

        val bracketParticipant = BracketParticipantEntity()
        bracketParticipant.bracket = bracket
        bracketParticipant.playerId = 888L
        bracketParticipant.addedAt = createdAt
        bracketParticipant.addedByUserId = 12L
        bracket.participants.add(bracketParticipant)

        return entity
    }

    private companion object {
        const val TOURNAMENT_ID = 10L
        const val EXPECTED_VERSION = 3L
        const val ACTING_USER_ID = 55L
        const val UPDATED_CUP_NAME = "Updated Cup"
    }
}
