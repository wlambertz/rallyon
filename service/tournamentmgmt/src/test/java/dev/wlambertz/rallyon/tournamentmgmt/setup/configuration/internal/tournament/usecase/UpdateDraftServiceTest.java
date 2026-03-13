package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.CourtEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.DisciplineEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository;
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateDraftServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    private UpdateDraftService updateDraftService;

    @BeforeEach
    void setUp() {
        updateDraftService = new UpdateDraftService(tournamentRepository, new TournamentMapper());
    }

    @Test
    void updatesDraftAndReplacesOwnedFields() {
        TournamentEntity entity = existingDraftEntity();
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(tournamentRepository.save(any(TournamentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, TournamentEntity.class));

        Tournament draftChanges = Tournament.builder()
                .name("Updated Cup")
                .visibility(Visibility.PUBLIC)
                .description("Fresh description")
                .locale(Locale.GERMANY)
                .schedule(new TimeWindow(
                        Instant.parse("2026-03-20T10:00:00Z"),
                        Instant.parse("2026-03-20T18:00:00Z")))
                .registrationWindows(List.of(new TimeWindow(
                        Instant.parse("2026-03-01T10:00:00Z"),
                        Instant.parse("2026-03-10T10:00:00Z"))))
                .venue(new Venue(
                        "Olympic Arena",
                        new Venue.Address("Main Street 1", "12345", "Berlin"),
                        new Capacity(5000, Capacity.Unit.PEOPLE)))
                .courts(List.of(
                        new Court(200L, "Court A", Court.Availability.AVAILABLE, Court.Type.STANDARD),
                        new Court(0L, "Court B", Court.Availability.UNAVAILABLE, Court.Type.SINGLES_ONLY)))
                .disciplines(List.of(new DisciplineConfig(
                        11L,
                        Category.SINGLES,
                        "Singles",
                        TeamSize.SINGLES,
                        List.of(new BracketConfig(
                                new BracketId("main"),
                                "Main Draw",
                                TournamentFormat.KO_POULE,
                                new Capacity(32, Capacity.Unit.PARTICIPANTS))))))
                .capacity(new Capacity(64, Capacity.Unit.PARTICIPANTS))
                .registrationPolicy(RegistrationPolicy.OPEN)
                .schedulingPolicy(SchedulingPolicy.MAX_PARALLEL_MATCHES)
                .courtAllocationPolicy(CourtAllocationPolicy.SEQUENTIAL)
                .scoringRules(ScoringRules.twoByTwentyOne())
                .tieBreakRules(TieBreakRules.headToHead())
                .matchDurationPolicy(MatchDurationPolicy.FIXED_TIMEBOX)
                .seedingPolicy(SeedingPolicy.MANUAL)
                .participants(new ParticipantsRoster(List.of(1001L, 1002L), null))
                .bracketRosters(Map.of(new BracketId("main"), new ParticipantsRoster(List.of(3001L), null)))
                .phases(List.of())
                .build();

        Tournament updated = updateDraftService.execute(10L, draftChanges, 3L, 55L);

        assertEquals("Updated Cup", updated.name());
        assertEquals(Visibility.PUBLIC, updated.visibility());
        assertEquals(Locale.GERMANY.getLanguage(), updated.locale().getLanguage());
        assertEquals(2, updated.courts().size());
        assertEquals(1, updated.registrationWindows().size());
        assertEquals(1, updated.disciplines().size());
        assertEquals(List.of(1001L, 1002L), updated.participants().playerIds());
        assertEquals(List.of(3001L), updated.bracketRosters().get(new BracketId("main")).playerIds());
        assertEquals(55L, updated.lastModifiedByUserId());
        assertNotNull(updated.lastModifiedAt());

        assertEquals("Updated Cup", entity.getName());
        assertEquals(2, entity.getCourts().size());
        assertEquals(1, entity.getDisciplines().size());
        assertSame(entity.getCourts().getFirst(), entity.getCourts().stream()
                .filter(court -> court.getId() != null && court.getId() == 200L)
                .findFirst()
                .orElseThrow());
        assertEquals(2, entity.getParticipants().stream().filter(participant -> participant.getCategory() == null).count());
        assertEquals(1, entity.getDisciplines().getFirst().getBrackets().getFirst().getParticipants().size());

        verify(tournamentRepository).findById(10L);
        verify(tournamentRepository).save(entity);
    }

    @Test
    void rejectsMissingTournament() {
        when(tournamentRepository.findById(10L)).thenReturn(Optional.empty());

        TournamentNotFoundException exception = assertThrows(
                TournamentNotFoundException.class,
                () -> updateDraftService.execute(10L, minimalDraft(), 3L, 55L)
        );

        assertEquals("Tournament 10 was not found", exception.getMessage());
        verify(tournamentRepository).findById(10L);
        verifyNoMoreInteractions(tournamentRepository);
    }

    @Test
    void rejectsStaleVersion() {
        TournamentEntity entity = existingDraftEntity();
        entity.setVersion(4L);
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(entity));

        DraftUpdateConflictException exception = assertThrows(
                DraftUpdateConflictException.class,
                () -> updateDraftService.execute(10L, minimalDraft(), 3L, 55L)
        );

        assertEquals("Draft version mismatch: expected 4 but got 3", exception.getMessage());
        verify(tournamentRepository).findById(10L);
        verifyNoMoreInteractions(tournamentRepository);
    }

    @Test
    void rejectsNonDraftTournament() {
        TournamentEntity entity = existingDraftEntity();
        entity.setStatus(TournamentStatus.PUBLISHED);
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(entity));

        DraftUpdateConflictException exception = assertThrows(
                DraftUpdateConflictException.class,
                () -> updateDraftService.execute(10L, minimalDraft(), 3L, 55L)
        );

        assertEquals("Only tournaments in DRAFT status can be updated", exception.getMessage());
        verify(tournamentRepository).findById(10L);
        verifyNoMoreInteractions(tournamentRepository);
    }

    @Test
    void rejectsNonEmptyPhases() {
        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> updateDraftService.execute(
                        10L,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .phases(List.of(new Phase() { }))
                                .build(),
                        3L,
                        55L)
        );

        assertEquals("Phases are not supported by draft updates yet", exception.getMessage());
        verifyNoMoreInteractions(tournamentRepository);
    }

    @Test
    void rejectsUnknownCourtIds() {
        TournamentEntity entity = existingDraftEntity();
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(entity));

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> updateDraftService.execute(
                        10L,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .courts(List.of(new Court(999L, "Court X", Court.Availability.AVAILABLE, Court.Type.STANDARD)))
                                .build(),
                        3L,
                        55L)
        );

        assertEquals("Unknown court id '999'", exception.getMessage());
    }

    @Test
    void rejectsUnknownBracketRosterKeys() {
        TournamentEntity entity = existingDraftEntity();
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(entity));

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> updateDraftService.execute(
                        10L,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .disciplines(List.of(new DisciplineConfig(
                                        11L,
                                        Category.SINGLES,
                                        "Singles",
                                        TeamSize.SINGLES,
                                        List.of(new BracketConfig(
                                                new BracketId("main"),
                                                "Main Draw",
                                                TournamentFormat.SWISS,
                                                null)))))
                                .bracketRosters(Map.of(
                                        new BracketId("unknown"),
                                        new ParticipantsRoster(List.of(3001L), null)))
                                .build(),
                        3L,
                        55L)
        );

        assertEquals("Unknown bracket roster key 'unknown'", exception.getMessage());
    }

    @Test
    void rejectsRosterModeMismatchAgainstSinglesBracket() {
        TournamentEntity entity = existingDraftEntity();
        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(entity));

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> updateDraftService.execute(
                        10L,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .disciplines(List.of(new DisciplineConfig(
                                        11L,
                                        Category.SINGLES,
                                        "Singles",
                                        TeamSize.SINGLES,
                                        List.of(new BracketConfig(
                                                new BracketId("main"),
                                                "Main Draw",
                                                TournamentFormat.SWISS,
                                                null)))))
                                .bracketRosters(Map.of(
                                        new BracketId("main"),
                                        new ParticipantsRoster(null, List.of(4001L))))
                                .build(),
                        3L,
                        55L)
        );

        assertEquals(
                "Bracket roster 'main' must use playerIds for singles disciplines",
                exception.getMessage());
    }

    private Tournament minimalDraft() {
        return Tournament.builder()
                .name("Updated Cup")
                .visibility(Visibility.PUBLIC)
                .build();
    }

    private TournamentEntity existingDraftEntity() {
        Instant createdAt = Instant.parse("2026-01-01T10:00:00Z");

        TournamentEntity entity = new TournamentEntity();
        entity.setId(10L);
        entity.setOrganizerId(5L);
        entity.setVisibility(Visibility.PRIVATE);
        entity.setName("Original Cup");
        entity.setStatus(TournamentStatus.DRAFT);
        entity.setVersion(3L);
        entity.setCreatedAt(createdAt);
        entity.setCreatedByUserId(12L);
        entity.setLastModifiedAt(createdAt);
        entity.setLastModifiedByUserId(12L);

        CourtEntity court = new CourtEntity();
        court.setId(200L);
        court.setTournament(entity);
        court.setLabel("Legacy Court");
        court.setAvailability(Court.Availability.AVAILABLE);
        court.setType(Court.Type.STANDARD);
        court.setSortOrder((short) 0);
        entity.getCourts().add(court);

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setTournament(entity);
        discipline.setDisciplineId(11L);
        discipline.setCategory(Category.SINGLES);
        discipline.setDisplayName("Legacy Singles");
        discipline.setTeamSize(TeamSize.SINGLES);

        BracketEntity bracket = new BracketEntity();
        bracket.setDiscipline(discipline);
        bracket.setBracketId("main");
        bracket.setDisplayName("Legacy Draw");
        bracket.setFormat(TournamentFormat.SWISS);
        discipline.getBrackets().add(bracket);
        entity.getDisciplines().add(discipline);

        ParticipantEntity participant = new ParticipantEntity();
        participant.setTournament(entity);
        participant.setPlayerId(999L);
        participant.setAddedAt(createdAt);
        participant.setAddedByUserId(12L);
        entity.getParticipants().add(participant);

        BracketParticipantEntity bracketParticipant = new BracketParticipantEntity();
        bracketParticipant.setBracket(bracket);
        bracketParticipant.setPlayerId(888L);
        bracketParticipant.setAddedAt(createdAt);
        bracketParticipant.setAddedByUserId(12L);
        bracket.getParticipants().add(bracketParticipant);

        return entity;
    }
}
