package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.CourtEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.DisciplineEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.RegistrationWindowEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TournamentMapperTest {

    private final TournamentMapper mapper = Mappers.getMapper(TournamentMapper.class);

    @Test
    void toEntityForCreateInitializesDraftDefaults() {
        Instant now = Instant.parse("2026-03-19T10:00:00Z");

        TournamentEntity entity = mapper.toEntityForCreate(77L, "RallyOn Masters", Visibility.PUBLIC, 5L, now);

        assertEquals(77L, entity.getOrganizerId());
        assertEquals(Visibility.PUBLIC, entity.getVisibility());
        assertEquals("RallyOn Masters", entity.getName());
        assertEquals(TournamentStatus.DRAFT, entity.getStatus());
        assertEquals(0L, entity.getVersion());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(5L, entity.getCreatedByUserId());
        assertEquals(now, entity.getLastModifiedAt());
        assertEquals(5L, entity.getLastModifiedByUserId());
        assertNotNull(entity.getRegistrationWindows());
        assertNotNull(entity.getCourts());
        assertNotNull(entity.getDisciplines());
        assertNotNull(entity.getParticipants());
    }

    @Test
    void toApiProjectsExtendedConfiguration() {
        Instant now = Instant.parse("2025-10-17T10:00:00Z");

        TournamentEntity entity = new TournamentEntity();
        entity.setId(99L);
        entity.setVersion(3L);
        entity.setOrganizerId(77L);
        entity.setVisibility(Visibility.PUBLIC);
        entity.setName("RallyOn Masters");
        entity.setDescription("Season highlight");
        entity.setLocale("de-DE");
        entity.setScheduleStart(now);
        entity.setScheduleEnd(now.plusSeconds(7200));
        entity.setVenueName("Olympic Arena");
        entity.setVenueStreet("Main Street 1");
        entity.setVenuePostalCode("12345");
        entity.setVenueCity("Berlin");
        entity.setVenueCapacityAmount(5000);
        entity.setVenueCapacityUnit(Capacity.Unit.PEOPLE);
        entity.setCapacityMaxParticipants(256);
        entity.setRegistrationPolicy(RegistrationPolicy.OPEN);
        entity.setSchedulingPolicy(SchedulingPolicy.MAX_PARALLEL_MATCHES);
        entity.setCourtAllocationPolicy(CourtAllocationPolicy.SEQUENTIAL);
        entity.setSeedingPolicy(SeedingPolicy.MANUAL);
        entity.setScoringPointsPerGame(21);
        entity.setScoringGamesPerMatch(3);
        entity.setScoringWinByTwo(true);
        entity.setScoringCapPoints(30);
        entity.setTieBreakUseSetDifference(true);
        entity.setTieBreakUsePointsRatio(false);
        entity.setTieBreakUseBuchholz(false);
        entity.setMatchDurationPolicy(MatchDurationPolicy.FIXED_TIMEBOX);
        entity.setStatus(TournamentStatus.DRAFT);
        entity.setCreatedAt(now);
        entity.setCreatedByUserId(5L);
        entity.setLastModifiedAt(now);
        entity.setLastModifiedByUserId(5L);

        RegistrationWindowEntity registrationWindow = new RegistrationWindowEntity();
        registrationWindow.setTournament(entity);
        registrationWindow.setWindowIndex((short) 0);
        registrationWindow.setRegistrationStartsAt(now.minusSeconds(604800));
        registrationWindow.setRegistrationEndsAt(now.minusSeconds(86400));
        entity.getRegistrationWindows().add(registrationWindow);

        CourtEntity court = new CourtEntity();
        court.setTournament(entity);
        court.setId(201L);
        court.setLabel("Court A");
        court.setAvailability(Court.Availability.AVAILABLE);
        court.setType(Court.Type.STANDARD);
        court.setSortOrder((short) 1);
        entity.getCourts().add(court);

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setTournament(entity);
        discipline.setDisciplineId(11L);
        discipline.setCategory(Category.SINGLES);
        discipline.setDisplayName("Singles");
        discipline.setTeamSize(TeamSize.SINGLES);

        BracketEntity bracket = new BracketEntity();
        bracket.setDiscipline(discipline);
        bracket.setBracketId("main");
        bracket.setDisplayName("Main Draw");
        bracket.setFormat(TournamentFormat.KO_POULE);
        bracket.setCapacityAmount(128);
        bracket.setCapacityUnit(Capacity.Unit.PARTICIPANTS);
        discipline.getBrackets().add(bracket);

        BracketParticipantEntity bracketParticipant = new BracketParticipantEntity();
        bracketParticipant.setBracket(bracket);
        bracketParticipant.setPlayerId(2001L);
        bracketParticipant.setAddedAt(now.minusSeconds(300));
        bracketParticipant.setAddedByUserId(5L);
        bracket.getParticipants().add(bracketParticipant);
        entity.getDisciplines().add(discipline);

        ParticipantEntity participant = new ParticipantEntity();
        participant.setTournament(entity);
        participant.setPlayerId(1001L);
        participant.setAddedAt(now.minusSeconds(600));
        participant.setAddedByUserId(5L);
        entity.getParticipants().add(participant);

        Tournament tournament = mapper.toApi(entity);

        assertEquals(99L, tournament.getId());
        assertEquals(3L, tournament.getVersion());
        assertEquals("RallyOn Masters", tournament.getName());
        assertEquals("Season highlight", tournament.getDescription());
        assertEquals(Locale.GERMANY.getLanguage(), tournament.getLocale().getLanguage());

        TimeWindow schedule = tournament.getSchedule();
        assertNotNull(schedule);
        assertEquals(now, schedule.getStart());
        assertEquals(now.plusSeconds(7200), schedule.getEnd());

        Venue venue = tournament.getVenue();
        assertNotNull(venue);
        assertEquals("Olympic Arena", venue.getName());
        assertNotNull(venue.getAddress());
        assertEquals("Berlin", venue.getAddress().getCity());
        assertEquals(5000, venue.getPeopleCapacity().getAmount());

        assertEquals(1, tournament.getRegistrationWindows().size());
        assertEquals(1, tournament.getCourts().size());
        Court mappedCourt = tournament.getCourts().get(0);
        assertEquals("Court A", mappedCourt.getLabel());
        assertEquals(Court.Type.STANDARD, mappedCourt.getType());

        List<DisciplineConfig> mappedDisciplines = tournament.getDisciplines();
        assertEquals(1, mappedDisciplines.size());
        DisciplineConfig mappedDiscipline = mappedDisciplines.get(0);
        assertEquals(11L, mappedDiscipline.getId());
        assertEquals(1, mappedDiscipline.getBrackets().size());
        assertEquals("Main Draw", mappedDiscipline.getBrackets().get(0).getDisplayName());

        assertEquals(256, tournament.getCapacity().getAmount());
        assertEquals(RegistrationPolicy.OPEN, tournament.getRegistrationPolicy());
        assertEquals(SeedingPolicy.MANUAL, tournament.getSeedingPolicy());
        assertEquals(MatchDurationPolicy.FIXED_TIMEBOX, tournament.getMatchDurationPolicy());

        ScoringRules scoringRules = tournament.getScoringRules();
        assertNotNull(scoringRules);
        assertEquals(21, scoringRules.getPointsPerGame());
        assertEquals(ScoringRules.Type.TWO_BY_TWENTY_ONE, scoringRules.getType());

        TieBreakRules tieBreakRules = tournament.getTieBreakRules();
        assertNotNull(tieBreakRules);
        assertEquals(TieBreakRules.Type.HEAD_TO_HEAD, tieBreakRules.getType());

        assertTrue(tournament.getParticipants().getPlayerIds().contains(1001L));

        Map<BracketId, ParticipantsRoster> bracketRosters = tournament.getBracketRosters();
        assertEquals(1, bracketRosters.size());
        ParticipantsRoster roster = bracketRosters.get(new BracketId("main"));
        assertNotNull(roster);
        assertTrue(roster.getPlayerIds().contains(2001L));
    }

    @Test
    void applyDraftReplacementUpdatesScalarsAndOwnedCollections() {
        Instant now = Instant.parse("2026-03-13T12:00:00Z");

        TournamentEntity entity = new TournamentEntity();
        entity.setVisibility(Visibility.PRIVATE);
        entity.setName("Legacy Cup");

        CourtEntity existingCourt = new CourtEntity();
        existingCourt.setId(201L);
        existingCourt.setTournament(entity);
        existingCourt.setLabel("Legacy Court");
        existingCourt.setAvailability(Court.Availability.AVAILABLE);
        existingCourt.setType(Court.Type.STANDARD);
        existingCourt.setSortOrder((short) 0);
        entity.getCourts().add(existingCourt);

        DisciplineEntity existingDiscipline = new DisciplineEntity();
        existingDiscipline.setTournament(entity);
        existingDiscipline.setDisciplineId(11L);
        existingDiscipline.setCategory(Category.SINGLES);
        existingDiscipline.setDisplayName("Legacy Singles");
        existingDiscipline.setTeamSize(TeamSize.SINGLES);

        BracketEntity existingBracket = new BracketEntity();
        existingBracket.setDiscipline(existingDiscipline);
        existingBracket.setBracketId("main");
        existingBracket.setDisplayName("Legacy Main");
        existingBracket.setFormat(TournamentFormat.SWISS);
        existingDiscipline.getBrackets().add(existingBracket);
        entity.getDisciplines().add(existingDiscipline);

        ParticipantEntity preservedCategoryParticipant = new ParticipantEntity();
        preservedCategoryParticipant.setTournament(entity);
        preservedCategoryParticipant.setCategory(Category.MIXED);
        preservedCategoryParticipant.setPlayerId(55L);
        preservedCategoryParticipant.setAddedAt(now.minusSeconds(60));
        preservedCategoryParticipant.setAddedByUserId(1L);
        entity.getParticipants().add(preservedCategoryParticipant);

        ParticipantEntity oldGeneralParticipant = new ParticipantEntity();
        oldGeneralParticipant.setTournament(entity);
        oldGeneralParticipant.setPlayerId(99L);
        oldGeneralParticipant.setAddedAt(now.minusSeconds(60));
        oldGeneralParticipant.setAddedByUserId(1L);
        entity.getParticipants().add(oldGeneralParticipant);

        BracketParticipantEntity oldBracketParticipant = new BracketParticipantEntity();
        oldBracketParticipant.setBracket(existingBracket);
        oldBracketParticipant.setPlayerId(77L);
        oldBracketParticipant.setAddedAt(now.minusSeconds(60));
        oldBracketParticipant.setAddedByUserId(1L);
        existingBracket.getParticipants().add(oldBracketParticipant);

        Tournament draftChanges = Tournament.builder()
                .name("Updated Cup")
                .visibility(Visibility.PUBLIC)
                .description("Fresh description")
                .locale(Locale.GERMANY)
                .schedule(new TimeWindow(now.plusSeconds(3600), now.plusSeconds(7200)))
                .registrationWindows(List.of(new TimeWindow(now, now.plusSeconds(1800))))
                .venue(new Venue(
                        "Olympic Arena",
                        new Venue.Address("Main Street 1", "12345", "Berlin"),
                        new Capacity(5000, Capacity.Unit.PEOPLE)))
                .courts(List.of(
                        new Court(201L, "Court A", Court.Availability.UNAVAILABLE, Court.Type.STANDARD),
                        new Court(0L, "Court B", Court.Availability.AVAILABLE, Court.Type.SINGLES_ONLY)))
                .disciplines(List.of(new DisciplineConfig(
                        11L,
                        Category.SINGLES,
                        "Singles",
                        TeamSize.SINGLES,
                        List.of(new dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig(
                                new BracketId("main"),
                                "Main Draw",
                                TournamentFormat.KO_POULE,
                                new Capacity(32, Capacity.Unit.PARTICIPANTS))))))
                .capacity(new Capacity(64, Capacity.Unit.PARTICIPANTS))
                .registrationPolicy(RegistrationPolicy.OPEN)
                .schedulingPolicy(SchedulingPolicy.MAX_PARALLEL_MATCHES)
                .courtAllocationPolicy(CourtAllocationPolicy.SEQUENTIAL)
                .seedingPolicy(SeedingPolicy.MANUAL)
                .scoringRules(ScoringRules.twoByTwentyOne())
                .tieBreakRules(TieBreakRules.headToHead())
                .matchDurationPolicy(MatchDurationPolicy.FIXED_TIMEBOX)
                .participants(new ParticipantsRoster(List.of(1001L, 1002L), null))
                .bracketRosters(Map.of(new BracketId("main"), new ParticipantsRoster(List.of(3001L), null)))
                .build();

        mapper.applyDraftReplacement(entity, draftChanges, 42L, now);

        assertEquals("Updated Cup", entity.getName());
        assertEquals(Visibility.PUBLIC, entity.getVisibility());
        assertEquals("de-DE", entity.getLocale());
        assertEquals(now.plusSeconds(3600), entity.getScheduleStart());
        assertEquals(1, entity.getRegistrationWindows().size());
        assertEquals(2, entity.getCourts().size());
        assertEquals(201L, entity.getCourts().get(0).getId());
        assertEquals("Court A", entity.getCourts().get(0).getLabel());
        assertEquals(1, entity.getDisciplines().size());
        assertEquals("Singles", entity.getDisciplines().get(0).getDisplayName());
        assertEquals(3, entity.getParticipants().size());
        assertEquals(2, entity.getParticipants().stream().filter(participant -> participant.getCategory() == null).count());
        assertEquals(1, entity.getDisciplines().get(0).getBrackets().get(0).getParticipants().size());
        assertEquals(3001L, entity.getDisciplines().get(0).getBrackets().get(0).getParticipants().get(0).getPlayerId());
    }

    @Test
    void applyDraftReplacementRejectsUnknownCourtId() {
        TournamentEntity entity = new TournamentEntity();

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> mapper.applyDraftReplacement(
                        entity,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .courts(List.of(new Court(999L, "Court X", Court.Availability.AVAILABLE, Court.Type.STANDARD)))
                                .build(),
                        42L,
                        Instant.parse("2026-03-13T12:00:00Z"))
        );

        assertEquals("Unknown court id '999'", exception.getMessage());
    }

    @Test
    void applyDraftReplacementRejectsRosterModeMismatch() {
        TournamentEntity entity = new TournamentEntity();

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> mapper.applyDraftReplacement(
                        entity,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .disciplines(List.of(new DisciplineConfig(
                                        11L,
                                        Category.DOUBLES,
                                        "Doubles",
                                        TeamSize.DOUBLES,
                                        List.of(new dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig(
                                                new BracketId("main"),
                                                "Main Draw",
                                                TournamentFormat.SWISS,
                                                null)))))
                                .bracketRosters(Map.of(
                                        new BracketId("main"),
                                        new ParticipantsRoster(List.of(3001L), null)))
                                .build(),
                        42L,
                        Instant.parse("2026-03-13T12:00:00Z"))
        );

        assertEquals("Bracket roster 'main' must use teamIds for doubles disciplines", exception.getMessage());
    }

    @Test
    void applyDraftReplacementRejectsNullParticipantId() {
        TournamentEntity entity = new TournamentEntity();
        // Arrays.asList permits a null element; List.of() would throw on construction instead
        // of exercising the mapper's own validation.
        List<Long> playerIdsWithNull = Arrays.asList(1001L, null);

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> mapper.applyDraftReplacement(
                        entity,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .participants(new ParticipantsRoster(playerIdsWithNull, null))
                                .build(),
                        42L,
                        Instant.parse("2026-03-13T12:00:00Z"))
        );

        assertEquals("Tournament participants playerIds must only contain positive ids", exception.getMessage());
    }

    @Test
    void applyDraftReplacementRejectsNullBracketRoster() {
        TournamentEntity entity = new TournamentEntity();

        InvalidDraftUpdateException exception = assertThrows(
                InvalidDraftUpdateException.class,
                () -> mapper.applyDraftReplacement(
                        entity,
                        Tournament.builder()
                                .name("Updated Cup")
                                .visibility(Visibility.PUBLIC)
                                .disciplines(List.of(new DisciplineConfig(
                                        11L,
                                        Category.DOUBLES,
                                        "Doubles",
                                        TeamSize.DOUBLES,
                                        List.of(new dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig(
                                                new BracketId("main"),
                                                "Main Draw",
                                                TournamentFormat.SWISS,
                                                null)))))
                                // Collections.singletonMap permits a null value; Map.of() would
                                // throw on construction instead of exercising the mapper.
                                .bracketRosters(Collections.singletonMap(new BracketId("main"), null))
                                .build(),
                        42L,
                        Instant.parse("2026-03-13T12:00:00Z"))
        );

        assertEquals("Bracket roster for 'main' must not be null", exception.getMessage());
    }
}
