package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.CourtEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.DisciplineEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.RegistrationWindowEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapper {

    public TournamentEntity toEntityForCreate(
            long organizerId, String name, Visibility visibility, long actingUserId, Instant now) {
        TournamentEntity entity = new TournamentEntity();
        entity.setOrganizerId(organizerId);
        entity.setVisibility(visibility);
        entity.setName(name);
        entity.setStatus(TournamentStatus.DRAFT);
        entity.setCreatedAt(now);
        entity.setCreatedByUserId(actingUserId);
        entity.setLastModifiedAt(now);
        entity.setLastModifiedByUserId(actingUserId);
        entity.setVersion(0L);
        return entity;
    }

    public void applyDraftReplacement(TournamentEntity entity, Tournament draftChanges, long actingUserId, Instant now) {
        applyScalars(entity, draftChanges);
        replaceRegistrationWindows(entity, normalizeList(draftChanges.registrationWindows()));
        replaceCourts(entity, normalizeList(draftChanges.courts()));
        replaceDisciplines(entity, normalizeList(draftChanges.disciplines()));
        replaceParticipants(entity, draftChanges.participants(), actingUserId, now);
        replaceBracketRosters(entity, normalizeMap(draftChanges.bracketRosters()), actingUserId, now);
    }

    public Tournament toApi(TournamentEntity entity) {
        return Tournament.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .organizerId(entity.getOrganizerId())
                .visibility(entity.getVisibility())
                .name(entity.getName())
                .description(entity.getDescription())
                .locale(toLocale(entity.getLocale()))
                .schedule(toSchedule(entity))
                .registrationWindows(toRegistrationWindows(entity))
                .venue(toVenue(entity))
                .courts(toCourts(entity))
                .disciplines(toDisciplines(entity))
                .capacity(toTournamentCapacity(entity))
                .registrationPolicy(entity.getRegistrationPolicy())
                .seedingPolicy(entity.getSeedingPolicy())
                .scoringRules(toScoringRules(entity))
                .tieBreakRules(toTieBreakRules(entity))
                .matchDurationPolicy(entity.getMatchDurationPolicy())
                .phases(List.of())
                .schedulingPolicy(entity.getSchedulingPolicy())
                .courtAllocationPolicy(entity.getCourtAllocationPolicy())
                .participants(toParticipantsRoster(entity.getParticipants()))
                .bracketRosters(toBracketRosters(entity))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .lastModifiedAt(entity.getLastModifiedAt())
                .lastModifiedByUserId(entity.getLastModifiedByUserId())
                .build();
    }

    private void applyScalars(TournamentEntity entity, Tournament draftChanges) {
        entity.setVisibility(draftChanges.visibility());
        entity.setName(draftChanges.name());
        entity.setDescription(draftChanges.description());
        entity.setLocale(toLocaleValue(draftChanges.locale()));

        applySchedule(entity, draftChanges.schedule());
        applyVenue(entity, draftChanges.venue());
        applyCapacity(entity, draftChanges.capacity());

        entity.setRegistrationPolicy(draftChanges.registrationPolicy());
        entity.setSchedulingPolicy(draftChanges.schedulingPolicy());
        entity.setCourtAllocationPolicy(draftChanges.courtAllocationPolicy());

        applyScoringRules(entity, draftChanges.scoringRules());
        applyTieBreakRules(entity, draftChanges.tieBreakRules());
        entity.setMatchDurationPolicy(draftChanges.matchDurationPolicy());
        entity.setSeedingPolicy(draftChanges.seedingPolicy());
    }

    private void applySchedule(TournamentEntity entity, TimeWindow schedule) {
        validateTimeWindow(schedule, "Schedule");
        if (schedule == null) {
            entity.setScheduleStart(null);
            entity.setScheduleEnd(null);
            return;
        }
        entity.setScheduleStart(schedule.start());
        entity.setScheduleEnd(schedule.end());
    }

    private void applyVenue(TournamentEntity entity, Venue venue) {
        validateVenue(venue);
        if (venue == null) {
            entity.setVenueName(null);
            entity.setVenueStreet(null);
            entity.setVenuePostalCode(null);
            entity.setVenueCity(null);
            entity.setVenueCapacityAmount(null);
            entity.setVenueCapacityUnit(null);
            return;
        }

        entity.setVenueName(venue.name());
        if (venue.address() == null) {
            entity.setVenueStreet(null);
            entity.setVenuePostalCode(null);
            entity.setVenueCity(null);
        } else {
            entity.setVenueStreet(venue.address().streetWithNumber());
            entity.setVenuePostalCode(venue.address().postalCode());
            entity.setVenueCity(venue.address().city());
        }

        Capacity peopleCapacity = venue.peopleCapacity();
        entity.setVenueCapacityAmount(peopleCapacity == null ? null : peopleCapacity.amount());
        entity.setVenueCapacityUnit(peopleCapacity == null ? null : peopleCapacity.unit());
    }

    private void applyCapacity(TournamentEntity entity, Capacity capacity) {
        validateCapacity(capacity, "Tournament capacity");
        entity.setCapacityMaxParticipants(capacity == null ? null : capacity.amount());
    }

    private void applyScoringRules(TournamentEntity entity, ScoringRules scoringRules) {
        if (scoringRules == null) {
            entity.setScoringPointsPerGame(null);
            entity.setScoringGamesPerMatch(null);
            entity.setScoringWinByTwo(null);
            entity.setScoringCapPoints(null);
            return;
        }

        entity.setScoringPointsPerGame(scoringRules.pointsPerGame());
        entity.setScoringGamesPerMatch(scoringRules.gamesPerMatch());
        entity.setScoringWinByTwo(scoringRules.winByTwo());
        entity.setScoringCapPoints(scoringRules.capPoints());
    }

    private void applyTieBreakRules(TournamentEntity entity, TieBreakRules tieBreakRules) {
        if (tieBreakRules == null) {
            entity.setTieBreakUseSetDifference(null);
            entity.setTieBreakUsePointsRatio(null);
            entity.setTieBreakUseBuchholz(null);
            return;
        }

        entity.setTieBreakUseSetDifference(tieBreakRules.useSetDifference());
        entity.setTieBreakUsePointsRatio(tieBreakRules.usePointsRatio());
        entity.setTieBreakUseBuchholz(tieBreakRules.useBuchholz());
    }

    private void replaceRegistrationWindows(TournamentEntity entity, List<TimeWindow> registrationWindows) {
        List<RegistrationWindowEntity> replacements = new ArrayList<>(registrationWindows.size());
        for (int index = 0; index < registrationWindows.size(); index++) {
            TimeWindow window = registrationWindows.get(index);
            validateTimeWindow(window, "Registration window");

            RegistrationWindowEntity registrationWindow = new RegistrationWindowEntity();
            registrationWindow.setTournament(entity);
            registrationWindow.setWindowIndex((short) index);
            registrationWindow.setRegistrationStartsAt(window.start());
            registrationWindow.setRegistrationEndsAt(window.end());
            replacements.add(registrationWindow);
        }

        entity.getRegistrationWindows().clear();
        entity.getRegistrationWindows().addAll(replacements);
    }

    private void replaceCourts(TournamentEntity entity, List<Court> courts) {
        Map<Long, CourtEntity> existingCourtsById = entity.getCourts().stream()
                .filter(court -> court.getId() != null)
                .collect(Collectors.toMap(CourtEntity::getId, court -> court, (left, right) -> left, LinkedHashMap::new));

        Set<Long> seenCourtIds = new LinkedHashSet<>();
        Set<String> seenCourtLabels = new LinkedHashSet<>();
        List<CourtEntity> replacements = new ArrayList<>(courts.size());

        for (int index = 0; index < courts.size(); index++) {
            Court court = courts.get(index);
            validateCourt(court);

            if (!seenCourtLabels.add(court.label())) {
                throw new InvalidDraftUpdateException("Duplicate court label '" + court.label() + "'");
            }

            CourtEntity courtEntity;
            if (court.id() > 0) {
                if (!seenCourtIds.add(court.id())) {
                    throw new InvalidDraftUpdateException("Duplicate court id '" + court.id() + "'");
                }
                courtEntity = existingCourtsById.get(court.id());
                if (courtEntity == null) {
                    throw new InvalidDraftUpdateException("Unknown court id '" + court.id() + "'");
                }
            } else if (court.id() < 0) {
                throw new InvalidDraftUpdateException("Court id must not be negative");
            } else {
                courtEntity = new CourtEntity();
            }

            courtEntity.setTournament(entity);
            courtEntity.setLabel(court.label());
            courtEntity.setAvailability(court.availability());
            courtEntity.setType(court.type());
            courtEntity.setSortOrder((short) index);
            replacements.add(courtEntity);
        }

        entity.getCourts().clear();
        entity.getCourts().addAll(replacements);
    }

    private void replaceDisciplines(TournamentEntity entity, List<DisciplineConfig> disciplines) {
        Map<Long, DisciplineEntity> existingDisciplinesById = entity.getDisciplines().stream()
                .collect(Collectors.toMap(
                        DisciplineEntity::getDisciplineId,
                        discipline -> discipline,
                        (left, right) -> left,
                        LinkedHashMap::new));

        Set<Long> seenDisciplineIds = new LinkedHashSet<>();
        Set<String> seenBracketIds = new LinkedHashSet<>();
        List<DisciplineEntity> replacements = new ArrayList<>(disciplines.size());

        for (DisciplineConfig discipline : disciplines) {
            validateDiscipline(discipline);

            if (!seenDisciplineIds.add(discipline.id())) {
                throw new InvalidDraftUpdateException("Duplicate discipline id '" + discipline.id() + "'");
            }

            DisciplineEntity disciplineEntity = existingDisciplinesById.get(discipline.id());
            if (disciplineEntity == null) {
                disciplineEntity = new DisciplineEntity();
            }

            disciplineEntity.setTournament(entity);
            disciplineEntity.setDisciplineId(discipline.id());
            disciplineEntity.setCategory(discipline.category());
            disciplineEntity.setDisplayName(discipline.displayName());
            disciplineEntity.setTeamSize(discipline.teamSize());

            replaceBrackets(disciplineEntity, normalizeList(discipline.brackets()), seenBracketIds);
            replacements.add(disciplineEntity);
        }

        entity.getDisciplines().clear();
        entity.getDisciplines().addAll(replacements);
    }

    private void replaceBrackets(
            DisciplineEntity disciplineEntity,
            List<BracketConfig> brackets,
            Set<String> seenBracketIds) {
        Map<String, BracketEntity> existingBracketsById = disciplineEntity.getBrackets().stream()
                .collect(Collectors.toMap(
                        BracketEntity::getBracketId,
                        bracket -> bracket,
                        (left, right) -> left,
                        LinkedHashMap::new));

        Set<String> seenLocalBracketIds = new LinkedHashSet<>();
        List<BracketEntity> replacements = new ArrayList<>(brackets.size());

        for (BracketConfig bracket : brackets) {
            validateBracket(bracket);
            String bracketId = bracket.id().value();

            if (!seenLocalBracketIds.add(bracketId)) {
                throw new InvalidDraftUpdateException(
                        "Duplicate bracket id '" + bracketId + "' within discipline " + disciplineEntity.getDisciplineId());
            }
            if (!seenBracketIds.add(bracketId)) {
                throw new InvalidDraftUpdateException(
                        "Duplicate bracket id '" + bracketId + "' across tournament disciplines");
            }

            BracketEntity bracketEntity = existingBracketsById.get(bracketId);
            if (bracketEntity == null) {
                bracketEntity = new BracketEntity();
            }

            bracketEntity.setDiscipline(disciplineEntity);
            bracketEntity.setBracketId(bracketId);
            bracketEntity.setDisplayName(bracket.displayName());
            bracketEntity.setFormat(bracket.format());
            bracketEntity.setCapacityAmount(bracket.capacity() == null ? null : bracket.capacity().amount());
            bracketEntity.setCapacityUnit(bracket.capacity() == null ? null : bracket.capacity().unit());
            replacements.add(bracketEntity);
        }

        disciplineEntity.getBrackets().clear();
        disciplineEntity.getBrackets().addAll(replacements);
    }

    private void replaceParticipants(TournamentEntity entity, ParticipantsRoster roster, long actingUserId, Instant now) {
        List<ParticipantEntity> replacements = new ArrayList<>();

        if (roster != null) {
            RosterKind rosterKind = validateRoster(roster, "Tournament participants");
            if (rosterKind == RosterKind.PLAYER) {
                for (Long playerId : roster.playerIds()) {
                    ParticipantEntity participant = new ParticipantEntity();
                    participant.setTournament(entity);
                    participant.setPlayerId(playerId);
                    participant.setAddedAt(now);
                    participant.setAddedByUserId(actingUserId);
                    replacements.add(participant);
                }
            } else {
                for (Long teamId : roster.teamIds()) {
                    ParticipantEntity participant = new ParticipantEntity();
                    participant.setTournament(entity);
                    participant.setTeamId(teamId);
                    participant.setAddedAt(now);
                    participant.setAddedByUserId(actingUserId);
                    replacements.add(participant);
                }
            }
        }

        entity.getParticipants().removeIf(participant -> participant.getCategory() == null);
        entity.getParticipants().addAll(replacements);
    }

    private void replaceBracketRosters(
            TournamentEntity entity,
            Map<BracketId, ParticipantsRoster> bracketRosters,
            long actingUserId,
            Instant now) {
        Map<String, BracketRosterTarget> bracketTargets = indexBracketTargets(entity);
        Map<String, ParticipantsRoster> incomingByBracketId = new LinkedHashMap<>();

        for (Map.Entry<BracketId, ParticipantsRoster> entry : bracketRosters.entrySet()) {
            BracketId bracketId = entry.getKey();
            ParticipantsRoster roster = entry.getValue();
            if (bracketId == null) {
                throw new InvalidDraftUpdateException("Bracket roster key must not be null");
            }
            if (roster == null) {
                throw new InvalidDraftUpdateException("Bracket roster for '" + bracketId.value() + "' must not be null");
            }

            BracketRosterTarget target = bracketTargets.get(bracketId.value());
            if (target == null) {
                throw new InvalidDraftUpdateException("Unknown bracket roster key '" + bracketId.value() + "'");
            }

            RosterKind rosterKind = validateRoster(roster, "Bracket roster '" + bracketId.value() + "'");
            if (target.teamSize() == TeamSize.SINGLES && rosterKind != RosterKind.PLAYER) {
                throw new InvalidDraftUpdateException(
                        "Bracket roster '" + bracketId.value() + "' must use playerIds for singles disciplines");
            }
            if (target.teamSize() == TeamSize.DOUBLES && rosterKind != RosterKind.TEAM) {
                throw new InvalidDraftUpdateException(
                        "Bracket roster '" + bracketId.value() + "' must use teamIds for doubles disciplines");
            }

            incomingByBracketId.put(bracketId.value(), roster);
        }

        for (Map.Entry<String, BracketRosterTarget> entry : bracketTargets.entrySet()) {
            BracketEntity bracket = entry.getValue().bracket();
            ParticipantsRoster roster = incomingByBracketId.get(entry.getKey());
            bracket.getParticipants().clear();
            if (roster == null) {
                continue;
            }

            if (entry.getValue().teamSize() == TeamSize.SINGLES) {
                for (Long playerId : roster.playerIds()) {
                    BracketParticipantEntity participant = new BracketParticipantEntity();
                    participant.setBracket(bracket);
                    participant.setPlayerId(playerId);
                    participant.setAddedAt(now);
                    participant.setAddedByUserId(actingUserId);
                    bracket.getParticipants().add(participant);
                }
            } else {
                for (Long teamId : roster.teamIds()) {
                    BracketParticipantEntity participant = new BracketParticipantEntity();
                    participant.setBracket(bracket);
                    participant.setTeamId(teamId);
                    participant.setAddedAt(now);
                    participant.setAddedByUserId(actingUserId);
                    bracket.getParticipants().add(participant);
                }
            }
        }
    }

    private Map<String, BracketRosterTarget> indexBracketTargets(TournamentEntity entity) {
        Map<String, BracketRosterTarget> bracketTargets = new LinkedHashMap<>();
        for (DisciplineEntity discipline : entity.getDisciplines()) {
            for (BracketEntity bracket : discipline.getBrackets()) {
                BracketRosterTarget existing = bracketTargets.put(
                        bracket.getBracketId(),
                        new BracketRosterTarget(bracket, discipline.getTeamSize()));
                if (existing != null) {
                    throw new InvalidDraftUpdateException(
                            "Duplicate bracket id '" + bracket.getBracketId() + "' across tournament disciplines");
                }
            }
        }
        return bracketTargets;
    }

    private RosterKind validateRoster(ParticipantsRoster roster, String fieldName) {
        List<Long> playerIds = roster.playerIds();
        List<Long> teamIds = roster.teamIds();

        boolean hasPlayers = playerIds != null;
        boolean hasTeams = teamIds != null;
        if (hasPlayers == hasTeams) {
            throw new InvalidDraftUpdateException(fieldName + " must provide exactly one of playerIds or teamIds");
        }

        if (hasPlayers) {
            validateIds(playerIds, fieldName + " playerIds");
            return RosterKind.PLAYER;
        }

        validateIds(teamIds, fieldName + " teamIds");
        return RosterKind.TEAM;
    }

    private void validateIds(List<Long> ids, String fieldName) {
        Set<Long> seenIds = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new InvalidDraftUpdateException(fieldName + " must only contain positive ids");
            }
            if (!seenIds.add(id)) {
                throw new InvalidDraftUpdateException("Duplicate id '" + id + "' in " + fieldName);
            }
        }
    }

    private void validateCourt(Court court) {
        if (court == null) {
            throw new InvalidDraftUpdateException("Court must not be null");
        }
        if (court.label() == null || court.label().isBlank()) {
            throw new InvalidDraftUpdateException("Court label must not be blank");
        }
        if (court.availability() == null) {
            throw new InvalidDraftUpdateException("Court availability must not be null");
        }
        if (court.type() == null) {
            throw new InvalidDraftUpdateException("Court type must not be null");
        }
    }

    private void validateDiscipline(DisciplineConfig discipline) {
        if (discipline == null) {
            throw new InvalidDraftUpdateException("Discipline must not be null");
        }
        if (discipline.id() <= 0) {
            throw new InvalidDraftUpdateException("Discipline id must be positive");
        }
        if (discipline.category() == null) {
            throw new InvalidDraftUpdateException("Discipline category must not be null");
        }
        if (discipline.displayName() == null || discipline.displayName().isBlank()) {
            throw new InvalidDraftUpdateException("Discipline display name must not be blank");
        }
        if (discipline.teamSize() == null) {
            throw new InvalidDraftUpdateException("Discipline team size must not be null");
        }
        if (discipline.brackets() == null) {
            throw new InvalidDraftUpdateException("Discipline brackets must not be null");
        }
    }

    private void validateBracket(BracketConfig bracket) {
        if (bracket == null) {
            throw new InvalidDraftUpdateException("Bracket must not be null");
        }
        if (bracket.id() == null || bracket.id().value().isBlank()) {
            throw new InvalidDraftUpdateException("Bracket id must not be blank");
        }
        if (bracket.displayName() == null || bracket.displayName().isBlank()) {
            throw new InvalidDraftUpdateException("Bracket display name must not be blank");
        }
        if (bracket.format() == null) {
            throw new InvalidDraftUpdateException("Bracket format must not be null");
        }
        validateCapacity(bracket.capacity(), "Bracket capacity");
    }

    private void validateVenue(Venue venue) {
        if (venue == null) {
            return;
        }
        if (venue.name() == null || venue.name().isBlank()) {
            throw new InvalidDraftUpdateException("Venue name must not be blank");
        }
        if (venue.address() != null) {
            if (venue.address().streetWithNumber() == null || venue.address().streetWithNumber().isBlank()) {
                throw new InvalidDraftUpdateException("Venue street must not be blank");
            }
            String postalCode = venue.address().postalCode();
            if (postalCode == null || postalCode.length() != 5) {
                throw new InvalidDraftUpdateException("Venue postal code must be exactly 5 characters");
            }
            if (venue.address().city() == null || venue.address().city().isBlank()) {
                throw new InvalidDraftUpdateException("Venue city must not be blank");
            }
        }

        validateCapacity(venue.peopleCapacity(), "Venue capacity");
        Capacity peopleCapacity = venue.peopleCapacity();
        if (peopleCapacity != null && peopleCapacity.amount() != null && peopleCapacity.unit() != Capacity.Unit.PEOPLE) {
            throw new InvalidDraftUpdateException("Venue capacity must use PEOPLE as unit");
        }
    }

    private void validateCapacity(Capacity capacity, String fieldName) {
        if (capacity == null) {
            return;
        }
        if (capacity.amount() != null && capacity.amount() <= 0) {
            throw new InvalidDraftUpdateException(fieldName + " amount must be positive");
        }
        if (capacity.amount() != null && capacity.unit() == null) {
            throw new InvalidDraftUpdateException(fieldName + " unit must be provided when amount is set");
        }
    }

    private void validateTimeWindow(TimeWindow window, String fieldName) {
        if (window == null) {
            return;
        }
        if (window.start() == null || window.end() == null) {
            throw new InvalidDraftUpdateException(fieldName + " must define both start and end");
        }
        if (window.end().isBefore(window.start())) {
            throw new InvalidDraftUpdateException(fieldName + " end must not be before start");
        }
    }

    private <T> List<T> normalizeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private <K, V> Map<K, V> normalizeMap(Map<K, V> values) {
        return values == null ? Map.of() : values;
    }

    private String toLocaleValue(Locale locale) {
        if (locale == null) {
            return null;
        }
        String languageTag = locale.toLanguageTag();
        return languageTag.isBlank() ? null : languageTag;
    }

    private Locale toLocale(String localeValue) {
        if (localeValue == null || localeValue.isBlank()) {
            return null;
        }
        String normalized = localeValue.replace('_', '-');
        return Locale.forLanguageTag(normalized);
    }

    private TimeWindow toSchedule(TournamentEntity entity) {
        if (entity.getScheduleStart() == null || entity.getScheduleEnd() == null) {
            return null;
        }
        return new TimeWindow(entity.getScheduleStart(), entity.getScheduleEnd());
    }

    private List<TimeWindow> toRegistrationWindows(TournamentEntity entity) {
        return entity.getRegistrationWindows().stream()
                .sorted(Comparator.comparingInt(window -> window.getWindowIndex()))
                .map(window -> new TimeWindow(window.getRegistrationStartsAt(), window.getRegistrationEndsAt()))
                .toList();
    }

    private Venue toVenue(TournamentEntity entity) {
        String name = entity.getVenueName();
        String street = entity.getVenueStreet();
        String postalCode = entity.getVenuePostalCode();
        String city = entity.getVenueCity();
        Capacity venueCapacity = toCapacity(entity.getVenueCapacityAmount(), entity.getVenueCapacityUnit());

        Venue.Address address = null;
        if (street != null || postalCode != null || city != null) {
            address = new Venue.Address(street, postalCode, city);
        }

        if (name == null && address == null && venueCapacity == null) {
            return null;
        }

        return new Venue(name, address, venueCapacity);
    }

    private List<Court> toCourts(TournamentEntity entity) {
        return entity.getCourts().stream()
                .sorted(Comparator.comparingInt(court -> court.getSortOrder()))
                .map(court -> new Court(
                        court.getId() == null ? 0L : court.getId(),
                        court.getLabel(),
                        court.getAvailability(),
                        court.getType()))
                .toList();
    }

    private List<DisciplineConfig> toDisciplines(TournamentEntity entity) {
        return entity.getDisciplines().stream()
                .map(discipline -> new DisciplineConfig(
                        discipline.getDisciplineId(),
                        discipline.getCategory(),
                        discipline.getDisplayName(),
                        discipline.getTeamSize(),
                        discipline.getBrackets().stream()
                                .map(this::toBracketConfig)
                                .toList()))
                .toList();
    }

    private BracketConfig toBracketConfig(BracketEntity bracket) {
        return new BracketConfig(
                new BracketId(bracket.getBracketId()),
                bracket.getDisplayName(),
                bracket.getFormat(),
                toCapacity(bracket.getCapacityAmount(), bracket.getCapacityUnit()));
    }

    private Capacity toTournamentCapacity(TournamentEntity entity) {
        Integer amount = entity.getCapacityMaxParticipants();
        if (amount == null) {
            return null;
        }
        return new Capacity(amount, Capacity.Unit.PARTICIPANTS);
    }

    private Capacity toCapacity(Integer amount, Capacity.Unit unit) {
        if (amount == null && unit == null) {
            return null;
        }
        Capacity.Unit resolvedUnit = unit;
        if (resolvedUnit == null && amount != null) {
            resolvedUnit = Capacity.Unit.PARTICIPANTS;
        }
        return new Capacity(amount, resolvedUnit);
    }

    private ScoringRules toScoringRules(TournamentEntity entity) {
        Integer points = entity.getScoringPointsPerGame();
        Integer games = entity.getScoringGamesPerMatch();
        Boolean winByTwo = entity.getScoringWinByTwo();
        Integer cap = entity.getScoringCapPoints();
        if (points == null || games == null || winByTwo == null) {
            return null;
        }
        boolean win = Boolean.TRUE.equals(winByTwo);
        ScoringRules candidate = ScoringRules.custom(points, games, win, cap);
        if (matches(candidate, ScoringRules.twoByTwentyOne())) {
            return ScoringRules.twoByTwentyOne();
        }
        if (matches(candidate, ScoringRules.threeByFifteen())) {
            return ScoringRules.threeByFifteen();
        }
        return candidate;
    }

    private boolean matches(ScoringRules candidate, ScoringRules preset) {
        return candidate.pointsPerGame() == preset.pointsPerGame()
                && candidate.gamesPerMatch() == preset.gamesPerMatch()
                && candidate.winByTwo() == preset.winByTwo()
                && Objects.equals(candidate.capPoints(), preset.capPoints());
    }

    private TieBreakRules toTieBreakRules(TournamentEntity entity) {
        Boolean setDifference = entity.getTieBreakUseSetDifference();
        Boolean pointsRatio = entity.getTieBreakUsePointsRatio();
        Boolean buchholz = entity.getTieBreakUseBuchholz();
        if (setDifference == null || pointsRatio == null || buchholz == null) {
            return null;
        }
        TieBreakRules candidate = TieBreakRules.custom(
                Boolean.TRUE.equals(setDifference),
                Boolean.TRUE.equals(pointsRatio),
                Boolean.TRUE.equals(buchholz));
        if (matches(candidate, TieBreakRules.headToHead())) {
            return TieBreakRules.headToHead();
        }
        if (matches(candidate, TieBreakRules.pointsRatio())) {
            return TieBreakRules.pointsRatio();
        }
        if (matches(candidate, TieBreakRules.swissStrength())) {
            return TieBreakRules.swissStrength();
        }
        return candidate;
    }

    private boolean matches(TieBreakRules candidate, TieBreakRules preset) {
        return candidate.useSetDifference() == preset.useSetDifference()
                && candidate.usePointsRatio() == preset.usePointsRatio()
                && candidate.useBuchholz() == preset.useBuchholz();
    }

    private ParticipantsRoster toParticipantsRoster(List<ParticipantEntity> participantEntities) {
        List<Long> playerIds = participantEntities.stream()
                .filter(participant -> participant.getCategory() == null && participant.getPlayerId() != null)
                .map(ParticipantEntity::getPlayerId)
                .toList();

        List<Long> teamIds = participantEntities.stream()
                .filter(participant -> participant.getCategory() == null && participant.getTeamId() != null)
                .map(ParticipantEntity::getTeamId)
                .toList();

        if (!playerIds.isEmpty()) {
            return new ParticipantsRoster(playerIds, null);
        }
        return new ParticipantsRoster(null, teamIds);
    }

    private Map<BracketId, ParticipantsRoster> toBracketRosters(TournamentEntity entity) {
        return entity.getDisciplines().stream()
                .flatMap(discipline -> discipline.getBrackets().stream())
                .collect(Collectors.toMap(
                        bracket -> new BracketId(bracket.getBracketId()),
                        this::toBracketRoster,
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new));
    }

    private ParticipantsRoster toBracketRoster(BracketEntity bracket) {
        List<Long> playerIds = bracket.getParticipants().stream()
                .map(BracketParticipantEntity::getPlayerId)
                .filter(Objects::nonNull)
                .toList();
        List<Long> teamIds = bracket.getParticipants().stream()
                .map(BracketParticipantEntity::getTeamId)
                .filter(Objects::nonNull)
                .toList();

        boolean hasPlayers = !playerIds.isEmpty();
        boolean hasTeams = !teamIds.isEmpty();

        if (hasPlayers && !hasTeams) {
            return new ParticipantsRoster(playerIds, null);
        }
        if (hasTeams && !hasPlayers) {
            return new ParticipantsRoster(null, teamIds);
        }
        if (!hasPlayers && !hasTeams) {
            boolean teamBased = bracket.getDiscipline() != null
                    && bracket.getDiscipline().getTeamSize() != null
                    && bracket.getDiscipline().getTeamSize() != TeamSize.SINGLES;
            return teamBased
                    ? new ParticipantsRoster(null, List.of())
                    : new ParticipantsRoster(List.of(), null);
        }
        boolean teamBased = bracket.getDiscipline() != null
                && bracket.getDiscipline().getTeamSize() != null
                && bracket.getDiscipline().getTeamSize() != TeamSize.SINGLES;
        return teamBased
                ? new ParticipantsRoster(null, teamIds)
                : new ParticipantsRoster(playerIds, null);
    }

    private enum RosterKind {
        PLAYER,
        TEAM
    }

    private record BracketRosterTarget(BracketEntity bracket, TeamSize teamSize) {
    }
}
