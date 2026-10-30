package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.mapping.TournamentApiMapping
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.validateReplacementAgainst
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.CourtEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.DisciplineEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.RegistrationWindowEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
import java.time.Instant
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.Locale
import java.util.Objects
import java.util.stream.Collectors
import org.mapstruct.BeanMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(config = TournamentMapperConfig::class)
abstract class TournamentMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "organizerId", source = "organizerId")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", expression = "java(defaultDraftStatus())")
    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "createdByUserId", source = "actingUserId")
    @Mapping(target = "lastModifiedAt", source = "now")
    @Mapping(target = "lastModifiedByUserId", source = "actingUserId")
    @Mapping(target = "version", expression = "java(initialVersion())")
    abstract fun toEntityForCreate(
        organizerId: Long,
        name: String?,
        visibility: Visibility?,
        actingUserId: Long,
        now: Instant?
    ): TournamentEntity

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "organizerId", source = "organizerId")
    @Mapping(target = "visibility", source = "visibility")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "locale", source = "locale", qualifiedByName = ["localeValueToLocale"])
    @Mapping(target = "schedule", source = ".", qualifiedByName = ["entityToSchedule"])
    @Mapping(target = "registrationWindows", source = "registrationWindows", qualifiedByName = ["mapRegistrationWindows"])
    @Mapping(target = "venue", source = ".", qualifiedByName = ["entityToVenue"])
    @Mapping(target = "courts", source = "courts", qualifiedByName = ["mapCourts"])
    @Mapping(target = "disciplines", source = "disciplines")
    @Mapping(target = "capacity", source = ".", qualifiedByName = ["entityToTournamentCapacity"])
    @Mapping(target = "registrationPolicy", source = "registrationPolicy")
    @Mapping(target = "seedingPolicy", source = "seedingPolicy")
    @Mapping(target = "scoringRules", source = ".", qualifiedByName = ["entityToScoringRules"])
    @Mapping(target = "tieBreakRules", source = ".", qualifiedByName = ["entityToTieBreakRules"])
    @Mapping(target = "matchDurationPolicy", source = "matchDurationPolicy")
    @Mapping(target = "phases", expression = "java(emptyPhases())")
    @Mapping(target = "schedulingPolicy", source = "schedulingPolicy")
    @Mapping(target = "courtAllocationPolicy", source = "courtAllocationPolicy")
    @Mapping(target = "participants", source = "participants", qualifiedByName = ["mapParticipantsRoster"])
    @Mapping(target = "bracketRosters", source = ".", qualifiedByName = ["mapBracketRosters"])
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "createdByUserId", source = "createdByUserId")
    @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    @Mapping(target = "lastModifiedByUserId", source = "lastModifiedByUserId")
    abstract fun toApi(entity: TournamentEntity): Tournament

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "start", source = "registrationStartsAt")
    @Mapping(target = "end", source = "registrationEndsAt")
    protected abstract fun toTimeWindow(window: RegistrationWindowEntity): TimeWindow

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "label")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "type", source = "type")
    protected abstract fun toCourt(court: CourtEntity): Court

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "disciplineId")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "teamSize", source = "teamSize")
    @Mapping(target = "brackets", source = "brackets")
    protected abstract fun toDisciplineConfig(discipline: DisciplineEntity): DisciplineConfig

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "bracketId")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "format", source = "format")
    @Mapping(target = "capacity", source = ".", qualifiedByName = ["entityToBracketCapacity"])
    protected abstract fun toBracketConfig(bracket: BracketEntity): BracketConfig

    fun applyDraftReplacement(entity: TournamentEntity, draftChanges: Tournament, actingUserId: Long, now: Instant) {
        TournamentApiMapping.toDomain(draftChanges).validateReplacementAgainst(
            TournamentApiMapping.toDomain(toApi(entity))
        )
        applyScalars(entity, draftChanges)
        replaceRegistrationWindows(entity, normalizeList(draftChanges.registrationWindows))
        replaceCourts(entity, normalizeList(draftChanges.courts))
        replaceDisciplines(entity, normalizeList(draftChanges.disciplines))
        replaceParticipants(entity, draftChanges.participants, actingUserId, now)
        replaceBracketRosters(entity, normalizeMap(draftChanges.bracketRosters), actingUserId, now)
    }

    @Named("localeValueToLocale")
    protected fun toLocale(localeValue: String?): Locale? {
        if (localeValue == null || localeValue.isBlank()) {
            return null
        }
        val normalized = localeValue.replace('_', '-')
        return Locale.forLanguageTag(normalized)
    }

    protected fun toLocaleValue(locale: Locale?): String? {
        if (locale == null) {
            return null
        }
        val languageTag = locale.toLanguageTag()
        return if (languageTag.isBlank()) null else languageTag
    }

    protected fun toBracketId(bracketId: String?): BracketId? = if (bracketId == null) null else BracketId(bracketId)

    @Named("entityToSchedule")
    protected fun toSchedule(entity: TournamentEntity): TimeWindow? {
        if (entity.scheduleStart == null || entity.scheduleEnd == null) {
            return null
        }
        return TimeWindow(entity.scheduleStart, entity.scheduleEnd)
    }

    @Named("mapRegistrationWindows")
    protected fun toRegistrationWindows(registrationWindows: List<RegistrationWindowEntity>?): List<TimeWindow> {
        if (registrationWindows == null || registrationWindows.isEmpty()) {
            return emptyList()
        }
        return registrationWindows.stream()
            .sorted(Comparator.comparingInt { window -> window.windowIndex.toInt() })
            .map { window -> toTimeWindow(window) }
            .toList()
    }

    @Named("entityToVenue")
    protected fun toVenue(entity: TournamentEntity): Venue? {
        val name = entity.venueName
        val street = entity.venueStreet
        val postalCode = entity.venuePostalCode
        val city = entity.venueCity
        val venueCapacity = toCapacity(entity.venueCapacityAmount, entity.venueCapacityUnit)

        var address: Venue.Address? = null
        if (street != null || postalCode != null || city != null) {
            address = Venue.Address(street, postalCode, city)
        }

        if (name == null && address == null && venueCapacity == null) {
            return null
        }

        return Venue(name, address, venueCapacity)
    }

    @Named("mapCourts")
    protected fun toCourts(courts: List<CourtEntity>?): List<Court> {
        if (courts == null || courts.isEmpty()) {
            return emptyList()
        }
        return courts.stream()
            .sorted(Comparator.comparingInt { court -> court.sortOrder.toInt() })
            .map { court -> toCourt(court) }
            .toList()
    }

    @Named("entityToTournamentCapacity")
    protected fun toTournamentCapacity(entity: TournamentEntity): Capacity? {
        val amount = entity.capacityMaxParticipants
        if (amount == null) {
            return null
        }
        return Capacity(amount, Capacity.Unit.PARTICIPANTS)
    }

    @Named("entityToBracketCapacity")
    protected fun toBracketCapacity(bracket: BracketEntity): Capacity? = toCapacity(bracket.capacityAmount, bracket.capacityUnit)

    protected fun toCapacity(amount: Int?, unit: Capacity.Unit?): Capacity? {
        if (amount == null && unit == null) {
            return null
        }
        var resolvedUnit = unit
        if (resolvedUnit == null && amount != null) {
            resolvedUnit = Capacity.Unit.PARTICIPANTS
        }
        return Capacity(amount, resolvedUnit)
    }

    @Named("entityToScoringRules")
    protected fun toScoringRules(entity: TournamentEntity): ScoringRules? {
        val points = entity.scoringPointsPerGame
        val games = entity.scoringGamesPerMatch
        val winByTwo = entity.scoringWinByTwo
        val cap = entity.scoringCapPoints
        if (points == null || games == null || winByTwo == null) {
            return null
        }
        val candidate = ScoringRules.custom(points, games, winByTwo, cap)
        if (matches(candidate, ScoringRules.twoByTwentyOne())) {
            return ScoringRules.twoByTwentyOne()
        }
        if (matches(candidate, ScoringRules.threeByFifteen())) {
            return ScoringRules.threeByFifteen()
        }
        return candidate
    }

    @Named("entityToTieBreakRules")
    protected fun toTieBreakRules(entity: TournamentEntity): TieBreakRules? {
        val setDifference = entity.tieBreakUseSetDifference
        val pointsRatio = entity.tieBreakUsePointsRatio
        val buchholz = entity.tieBreakUseBuchholz
        if (setDifference == null || pointsRatio == null || buchholz == null) {
            return null
        }
        val candidate = TieBreakRules.custom(setDifference, pointsRatio, buchholz)
        if (matches(candidate, TieBreakRules.headToHead())) {
            return TieBreakRules.headToHead()
        }
        if (matches(candidate, TieBreakRules.pointsRatio())) {
            return TieBreakRules.pointsRatio()
        }
        if (matches(candidate, TieBreakRules.swissStrength())) {
            return TieBreakRules.swissStrength()
        }
        return candidate
    }

    @Named("mapParticipantsRoster")
    protected fun toParticipantsRoster(participantEntities: List<ParticipantEntity>): ParticipantsRoster {
        val playerIds: List<Long> = participantEntities.stream()
            .filter { participant -> participant.category == null && participant.playerId != null }
            .map { participant -> participant.playerId!! }
            .toList()

        val teamIds: List<Long> = participantEntities.stream()
            .filter { participant -> participant.category == null && participant.teamId != null }
            .map { participant -> participant.teamId!! }
            .toList()

        if (playerIds.isNotEmpty()) {
            return ParticipantsRoster(playerIds, null)
        }
        return ParticipantsRoster(null, teamIds)
    }

    @Named("mapBracketRosters")
    protected fun toBracketRosters(entity: TournamentEntity): Map<BracketId, ParticipantsRoster> =
        entity.disciplines.stream()
            .flatMap { discipline -> discipline.brackets.stream() }
            .collect(
                Collectors.toMap(
                    { bracket -> BracketId(bracket.bracketId!!) },
                    { bracket -> toBracketRoster(bracket) },
                    { _: ParticipantsRoster, replacement: ParticipantsRoster -> replacement },
                    { LinkedHashMap<BracketId, ParticipantsRoster>() }
                )
            )

    protected fun toBracketRoster(bracket: BracketEntity): ParticipantsRoster {
        val playerIds: List<Long> = bracket.participants.stream()
            .map { participant -> participant.playerId }
            .filter { playerId -> playerId != null }
            .map { playerId -> playerId!! }
            .toList()
        val teamIds: List<Long> = bracket.participants.stream()
            .map { participant -> participant.teamId }
            .filter { teamId -> teamId != null }
            .map { teamId -> teamId!! }
            .toList()

        val hasPlayers = playerIds.isNotEmpty()
        val hasTeams = teamIds.isNotEmpty()

        if (hasPlayers && !hasTeams) {
            return ParticipantsRoster(playerIds, null)
        }
        if (hasTeams && !hasPlayers) {
            return ParticipantsRoster(null, teamIds)
        }
        if (!hasPlayers && !hasTeams) {
            val teamBased = bracket.discipline != null &&
                bracket.discipline!!.teamSize != null &&
                bracket.discipline!!.teamSize != TeamSize.SINGLES
            return if (teamBased) {
                ParticipantsRoster(null, emptyList())
            } else {
                ParticipantsRoster(emptyList(), null)
            }
        }
        val teamBased = bracket.discipline != null &&
            bracket.discipline!!.teamSize != null &&
            bracket.discipline!!.teamSize != TeamSize.SINGLES
        return if (teamBased) {
            ParticipantsRoster(null, teamIds)
        } else {
            ParticipantsRoster(playerIds, null)
        }
    }

    protected fun defaultDraftStatus(): TournamentStatus = TournamentStatus.DRAFT

    protected fun initialVersion(): Long? = 0L

    protected fun emptyPhases(): List<Phase> = emptyList()

    private fun applyScalars(entity: TournamentEntity, draftChanges: Tournament) {
        entity.visibility = draftChanges.visibility
        entity.name = draftChanges.name
        entity.description = draftChanges.description
        entity.locale = toLocaleValue(draftChanges.locale)

        applySchedule(entity, draftChanges.schedule)
        applyVenue(entity, draftChanges.venue)
        applyCapacity(entity, draftChanges.capacity)

        entity.registrationPolicy = draftChanges.registrationPolicy
        entity.schedulingPolicy = draftChanges.schedulingPolicy
        entity.courtAllocationPolicy = draftChanges.courtAllocationPolicy

        applyScoringRules(entity, draftChanges.scoringRules)
        applyTieBreakRules(entity, draftChanges.tieBreakRules)
        entity.matchDurationPolicy = draftChanges.matchDurationPolicy
        entity.seedingPolicy = draftChanges.seedingPolicy
    }

    private fun applySchedule(entity: TournamentEntity, schedule: TimeWindow?) {
        if (schedule == null) {
            entity.scheduleStart = null
            entity.scheduleEnd = null
            return
        }
        entity.scheduleStart = schedule.start
        entity.scheduleEnd = schedule.end
    }

    private fun applyVenue(entity: TournamentEntity, venue: Venue?) {
        if (venue == null) {
            entity.venueName = null
            entity.venueStreet = null
            entity.venuePostalCode = null
            entity.venueCity = null
            entity.venueCapacityAmount = null
            entity.venueCapacityUnit = null
            return
        }

        entity.venueName = venue.name
        if (venue.address == null) {
            entity.venueStreet = null
            entity.venuePostalCode = null
            entity.venueCity = null
        } else {
            entity.venueStreet = venue.address.streetWithNumber
            entity.venuePostalCode = venue.address.postalCode
            entity.venueCity = venue.address.city
        }

        val peopleCapacity = venue.peopleCapacity
        entity.venueCapacityAmount = peopleCapacity?.amount
        entity.venueCapacityUnit = peopleCapacity?.unit
    }

    private fun applyCapacity(entity: TournamentEntity, capacity: Capacity?) {
        entity.capacityMaxParticipants = capacity?.amount
    }

    private fun applyScoringRules(entity: TournamentEntity, scoringRules: ScoringRules?) {
        if (scoringRules == null) {
            entity.scoringPointsPerGame = null
            entity.scoringGamesPerMatch = null
            entity.scoringWinByTwo = null
            entity.scoringCapPoints = null
            return
        }

        entity.scoringPointsPerGame = scoringRules.pointsPerGame
        entity.scoringGamesPerMatch = scoringRules.gamesPerMatch
        entity.scoringWinByTwo = scoringRules.winByTwo
        entity.scoringCapPoints = scoringRules.capPoints
    }

    private fun applyTieBreakRules(entity: TournamentEntity, tieBreakRules: TieBreakRules?) {
        if (tieBreakRules == null) {
            entity.tieBreakUseSetDifference = null
            entity.tieBreakUsePointsRatio = null
            entity.tieBreakUseBuchholz = null
            return
        }

        entity.tieBreakUseSetDifference = tieBreakRules.useSetDifference
        entity.tieBreakUsePointsRatio = tieBreakRules.usePointsRatio
        entity.tieBreakUseBuchholz = tieBreakRules.useBuchholz
    }

    private fun replaceRegistrationWindows(entity: TournamentEntity, registrationWindows: List<TimeWindow>) {
        val replacements = ArrayList<RegistrationWindowEntity>(registrationWindows.size)
        for (index in registrationWindows.indices) {
            val window = registrationWindows[index]

            val registrationWindow = RegistrationWindowEntity()
            registrationWindow.tournament = entity
            registrationWindow.windowIndex = index.toShort()
            registrationWindow.registrationStartsAt = window.start
            registrationWindow.registrationEndsAt = window.end
            replacements.add(registrationWindow)
        }

        entity.registrationWindows.clear()
        entity.registrationWindows.addAll(replacements)
    }

    private fun replaceCourts(entity: TournamentEntity, courts: List<Court>) {
        val existingCourtsById: Map<Long, CourtEntity> = entity.courts.stream()
            .filter { court -> court.id != null }
            .collect(
                Collectors.toMap(
                    { court -> court.id!! },
                    { court -> court },
                    { left: CourtEntity, _: CourtEntity -> left },
                    { LinkedHashMap<Long, CourtEntity>() }
                )
            )

        val replacements = ArrayList<CourtEntity>(courts.size)

        for (index in courts.indices) {
            val court = courts[index]

            val courtEntity: CourtEntity
            if (court.id > 0) {
                courtEntity = existingCourtsById[court.id]
                    ?: error("Validated court is missing")
            } else {
                courtEntity = CourtEntity()
            }

            courtEntity.tournament = entity
            courtEntity.label = court.label
            courtEntity.availability = court.availability
            courtEntity.type = court.type
            courtEntity.sortOrder = index.toShort()
            replacements.add(courtEntity)
        }

        entity.courts.clear()
        entity.courts.addAll(replacements)
    }

    private fun replaceDisciplines(entity: TournamentEntity, disciplines: List<DisciplineConfig>) {
        val existingDisciplinesById: Map<Long, DisciplineEntity> = entity.disciplines.stream()
            .collect(
                Collectors.toMap(
                    { discipline -> discipline.disciplineId },
                    { discipline -> discipline },
                    { left: DisciplineEntity, _: DisciplineEntity -> left },
                    { LinkedHashMap<Long, DisciplineEntity>() }
                )
            )

        val replacements = ArrayList<DisciplineEntity>(disciplines.size)

        for (discipline in disciplines) {
            var disciplineEntity = existingDisciplinesById[discipline.id]
            if (disciplineEntity == null) {
                disciplineEntity = DisciplineEntity()
            }

            disciplineEntity.tournament = entity
            disciplineEntity.disciplineId = discipline.id
            disciplineEntity.category = discipline.category
            disciplineEntity.displayName = discipline.displayName
            disciplineEntity.teamSize = discipline.teamSize

            replaceBrackets(disciplineEntity, normalizeList(discipline.brackets))
            replacements.add(disciplineEntity)
        }

        entity.disciplines.clear()
        entity.disciplines.addAll(replacements)
    }

    private fun replaceBrackets(
        disciplineEntity: DisciplineEntity,
        brackets: List<BracketConfig>
    ) {
        val existingBracketsById: Map<String, BracketEntity> = disciplineEntity.brackets.stream()
            .collect(
                Collectors.toMap(
                    { bracket -> bracket.bracketId!! },
                    { bracket -> bracket },
                    { left: BracketEntity, _: BracketEntity -> left },
                    { LinkedHashMap<String, BracketEntity>() }
                )
            )

        val replacements = ArrayList<BracketEntity>(brackets.size)

        for (bracket in brackets) {
            val bracketId = bracket.id.value

            var bracketEntity = existingBracketsById[bracketId]
            if (bracketEntity == null) {
                bracketEntity = BracketEntity()
            }

            bracketEntity.discipline = disciplineEntity
            bracketEntity.bracketId = bracketId
            bracketEntity.displayName = bracket.displayName
            bracketEntity.format = bracket.format
            bracketEntity.capacityAmount = bracket.capacity?.amount
            bracketEntity.capacityUnit = bracket.capacity?.unit
            replacements.add(bracketEntity)
        }

        disciplineEntity.brackets.clear()
        disciplineEntity.brackets.addAll(replacements)
    }

    private fun replaceParticipants(
        entity: TournamentEntity,
        roster: ParticipantsRoster?,
        actingUserId: Long,
        now: Instant
    ) {
        val replacements = ArrayList<ParticipantEntity>()

        if (roster != null) {
            val playerRoster = roster.playerIds != null
            if (playerRoster) {
                for (playerId in roster.playerIds!!) {
                    val participant = ParticipantEntity()
                    participant.tournament = entity
                    participant.playerId = playerId
                    participant.addedAt = now
                    participant.addedByUserId = actingUserId
                    replacements.add(participant)
                }
            } else {
                for (teamId in roster.teamIds!!) {
                    val participant = ParticipantEntity()
                    participant.tournament = entity
                    participant.teamId = teamId
                    participant.addedAt = now
                    participant.addedByUserId = actingUserId
                    replacements.add(participant)
                }
            }
        }

        entity.participants.removeIf { participant -> participant.category == null }
        entity.participants.addAll(replacements)
    }

    private fun replaceBracketRosters(
        entity: TournamentEntity,
        bracketRosters: Map<BracketId, ParticipantsRoster>,
        actingUserId: Long,
        now: Instant
    ) {
        val bracketTargets = indexBracketTargets(entity)
        val incomingByBracketId = LinkedHashMap<String, ParticipantsRoster>()

        for ((bracketId, roster) in bracketRosters) {
            incomingByBracketId.put(bracketId.value, roster)
        }

        for ((bracketId, target) in bracketTargets) {
            val bracket = target.bracket
            val roster = incomingByBracketId[bracketId]
            bracket.participants.clear()
            if (roster == null) {
                continue
            }

            if (target.teamSize == TeamSize.SINGLES) {
                for (playerId in roster.playerIds!!) {
                    val participant = BracketParticipantEntity()
                    participant.bracket = bracket
                    participant.playerId = playerId
                    participant.addedAt = now
                    participant.addedByUserId = actingUserId
                    bracket.participants.add(participant)
                }
            } else {
                for (teamId in roster.teamIds!!) {
                    val participant = BracketParticipantEntity()
                    participant.bracket = bracket
                    participant.teamId = teamId
                    participant.addedAt = now
                    participant.addedByUserId = actingUserId
                    bracket.participants.add(participant)
                }
            }
        }
    }

    private fun indexBracketTargets(entity: TournamentEntity): Map<String, BracketRosterTarget> {
        val bracketTargets = LinkedHashMap<String, BracketRosterTarget>()
        for (discipline in entity.disciplines) {
            for (bracket in discipline.brackets) {
                val bracketId = bracket.bracketId!!
                bracketTargets.put(
                    bracketId,
                    BracketRosterTarget(bracket, discipline.teamSize)
                )
            }
        }
        return bracketTargets
    }

    private fun matches(candidate: ScoringRules, preset: ScoringRules): Boolean =
        candidate.pointsPerGame == preset.pointsPerGame &&
            candidate.gamesPerMatch == preset.gamesPerMatch &&
            candidate.winByTwo == preset.winByTwo &&
            Objects.equals(candidate.capPoints, preset.capPoints)

    private fun matches(candidate: TieBreakRules, preset: TieBreakRules): Boolean =
        candidate.useSetDifference == preset.useSetDifference &&
            candidate.usePointsRatio == preset.usePointsRatio &&
            candidate.useBuchholz == preset.useBuchholz

    private fun <T> normalizeList(values: List<T>?): List<T> = values ?: emptyList()

    private fun <K, V> normalizeMap(values: Map<K, V>?): Map<K, V> = values ?: emptyMap()

    private data class BracketRosterTarget(val bracket: BracketEntity, val teamSize: TeamSize?)
}
