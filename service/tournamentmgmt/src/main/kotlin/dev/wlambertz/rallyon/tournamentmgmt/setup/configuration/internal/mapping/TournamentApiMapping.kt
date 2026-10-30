package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.mapping

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig as ApiBracketConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId as ApiBracketId
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity as ApiCapacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category as ApiCategory
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court as ApiCourt
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig as ApiDisciplineConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster as ApiParticipantsRoster
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy as ApiRegistrationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy as ApiSchedulingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize as ApiTeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow as ApiTimeWindow
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament as ApiTournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat as ApiTournamentFormat
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus as ApiTournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue as ApiVenue
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility as ApiVisibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.BracketConfig as DomainBracketConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.BracketId as DomainBracketId
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Capacity as DomainCapacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Category as DomainCategory
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Court as DomainCourt
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.CourtAllocationPolicy as DomainCourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.DisciplineConfig as DomainDisciplineConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.MatchDurationPolicy as DomainMatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.ParticipantsRoster as DomainParticipantsRoster
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Phase as DomainPhase
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.RegistrationPolicy as DomainRegistrationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.SchedulingPolicy as DomainSchedulingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.ScoringRules as DomainScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.SeedingPolicy as DomainSeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TeamSize as DomainTeamSize
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TieBreakRules as DomainTieBreakRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TimeWindow as DomainTimeWindow
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament as DomainTournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentFormat as DomainTournamentFormat
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentStatus as DomainTournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Venue as DomainVenue
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Visibility as DomainVisibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase as ApiPhase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy as ApiCourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy as ApiMatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules as ApiScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy as ApiSeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules as ApiTieBreakRules

object TournamentApiMapping {
    @JvmStatic
    fun toDomain(value: ApiTimeWindow): DomainTimeWindow = DomainTimeWindow(
        start = value.start,
        end = value.end,
    )

    @JvmStatic
    fun toApi(value: DomainTimeWindow): ApiTimeWindow = ApiTimeWindow(
        start = value.start,
        end = value.end,
    )

    @JvmStatic
    fun toDomain(value: ApiDisciplineConfig): DomainDisciplineConfig = DomainDisciplineConfig(
        id = value.id,
        category = DomainCategory.valueOf(value.category.name),
        displayName = value.displayName,
        teamSize = DomainTeamSize.valueOf(value.teamSize.name),
        brackets = value.brackets.map { item -> item?.let { toDomain(it) } },
    )

    @JvmStatic
    fun toApi(value: DomainDisciplineConfig): ApiDisciplineConfig = ApiDisciplineConfig(
        id = value.id,
        category = ApiCategory.valueOf(value.category.name),
        displayName = value.displayName,
        teamSize = ApiTeamSize.valueOf(value.teamSize.name),
        brackets = value.brackets.map { item -> toApi(requireNotNull(item)) },
    )

    @JvmStatic
    fun toDomain(value: ApiVisibility): DomainVisibility = DomainVisibility.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainVisibility): ApiVisibility = ApiVisibility.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiBracketId): DomainBracketId = DomainBracketId(
        value = value.value,
    )

    @JvmStatic
    fun toApi(value: DomainBracketId): ApiBracketId = ApiBracketId(
        value = value.value,
    )

    @JvmStatic
    fun toDomain(value: ApiTournament): DomainTournament = DomainTournament(
        id = value.id,
        version = value.version,
        organizerId = value.organizerId,
        visibility = value.visibility?.let { DomainVisibility.valueOf(it.name) },
        name = value.name,
        description = value.description,
        locale = value.locale,
        schedule = value.schedule?.let { toDomain(it) },
        registrationWindows = value.registrationWindows?.let { it.map { item -> toDomain(item) } },
        venue = value.venue?.let { toDomain(it) },
        courts = value.courts?.let { it.map { item -> item?.let { toDomain(it) } } },
        disciplines = value.disciplines?.let { it.map { item -> item?.let { toDomain(it) } } },
        capacity = value.capacity?.let { toDomain(it) },
        registrationPolicy = value.registrationPolicy?.let { DomainRegistrationPolicy.valueOf(it.name) },
        seedingPolicy = value.seedingPolicy?.let { DomainSeedingPolicy.valueOf(it.name) },
        scoringRules = value.scoringRules?.let { toDomain(it) },
        tieBreakRules = value.tieBreakRules?.let { toDomain(it) },
        matchDurationPolicy = value.matchDurationPolicy?.let { DomainMatchDurationPolicy.valueOf(it.name) },
        phases = value.phases?.let { it.map { object : DomainPhase {} } },
        schedulingPolicy = value.schedulingPolicy?.let { DomainSchedulingPolicy.valueOf(it.name) },
        courtAllocationPolicy = value.courtAllocationPolicy?.let { DomainCourtAllocationPolicy.valueOf(it.name) },
        participants = value.participants?.let { toDomain(it) },
        bracketRosters = value.bracketRosters?.entries?.associate { (key, roster) ->
            toDomain(key) to roster?.let { toDomain(it) }
        },
        status = value.status?.let { DomainTournamentStatus.valueOf(it.name) },
        createdAt = value.createdAt,
        createdByUserId = value.createdByUserId,
        lastModifiedAt = value.lastModifiedAt,
        lastModifiedByUserId = value.lastModifiedByUserId,
    )

    @JvmStatic
    fun toApi(value: DomainTournament): ApiTournament = ApiTournament(
        id = value.id,
        version = value.version,
        organizerId = value.organizerId,
        visibility = value.visibility?.let { ApiVisibility.valueOf(it.name) },
        name = value.name,
        description = value.description,
        locale = value.locale,
        schedule = value.schedule?.let { toApi(it) },
        registrationWindows = value.registrationWindows?.let { it.map { item -> toApi(item) } },
        venue = value.venue?.let { toApi(it) },
        courts = value.courts?.let { it.map { item -> toApi(requireNotNull(item)) } },
        disciplines = value.disciplines?.let { it.map { item -> toApi(requireNotNull(item)) } },
        capacity = value.capacity?.let { toApi(it) },
        registrationPolicy = value.registrationPolicy?.let { ApiRegistrationPolicy.valueOf(it.name) },
        seedingPolicy = value.seedingPolicy?.let { ApiSeedingPolicy.valueOf(it.name) },
        scoringRules = value.scoringRules?.let { toApi(it) },
        tieBreakRules = value.tieBreakRules?.let { toApi(it) },
        matchDurationPolicy = value.matchDurationPolicy?.let { ApiMatchDurationPolicy.valueOf(it.name) },
        phases = value.phases?.let { it.map { object : ApiPhase {} } },
        schedulingPolicy = value.schedulingPolicy?.let { ApiSchedulingPolicy.valueOf(it.name) },
        courtAllocationPolicy = value.courtAllocationPolicy?.let { ApiCourtAllocationPolicy.valueOf(it.name) },
        participants = value.participants?.let { toApi(it) },
        bracketRosters = value.bracketRosters?.entries?.associate { (key, roster) ->
            toApi(key) to toApi(requireNotNull(roster))
        },
        status = value.status?.let { ApiTournamentStatus.valueOf(it.name) },
        createdAt = value.createdAt,
        createdByUserId = value.createdByUserId,
        lastModifiedAt = value.lastModifiedAt,
        lastModifiedByUserId = value.lastModifiedByUserId,
    )

    @JvmStatic
    fun toDomain(value: ApiTeamSize): DomainTeamSize = DomainTeamSize.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainTeamSize): ApiTeamSize = ApiTeamSize.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiCategory): DomainCategory = DomainCategory.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainCategory): ApiCategory = ApiCategory.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiTournamentStatus): DomainTournamentStatus = DomainTournamentStatus.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainTournamentStatus): ApiTournamentStatus = ApiTournamentStatus.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiCourt): DomainCourt = DomainCourt(
        id = value.id,
        label = value.label,
        availability = value.availability?.let { DomainCourt.Availability.valueOf(it.name) },
        type = value.type?.let { DomainCourt.Type.valueOf(it.name) },
    )

    @JvmStatic
    fun toApi(value: DomainCourt): ApiCourt = ApiCourt(
        id = value.id,
        label = value.label,
        availability = value.availability?.let { ApiCourt.Availability.valueOf(it.name) },
        type = value.type?.let { ApiCourt.Type.valueOf(it.name) },
    )

    @JvmStatic
    fun toDomain(value: ApiBracketConfig): DomainBracketConfig = DomainBracketConfig(
        id = toDomain(value.id),
        displayName = value.displayName,
        format = DomainTournamentFormat.valueOf(value.format.name),
        capacity = value.capacity?.let { toDomain(it) },
    )

    @JvmStatic
    fun toApi(value: DomainBracketConfig): ApiBracketConfig = ApiBracketConfig(
        id = toApi(value.id),
        displayName = value.displayName,
        format = ApiTournamentFormat.valueOf(value.format.name),
        capacity = value.capacity?.let { toApi(it) },
    )

    @JvmStatic
    fun toDomain(value: ApiVenue): DomainVenue = DomainVenue(
        name = value.name,
        address = value.address?.let { toDomain(it) },
        peopleCapacity = value.peopleCapacity?.let { toDomain(it) },
    )

    @JvmStatic
    fun toApi(value: DomainVenue): ApiVenue = ApiVenue(
        name = value.name,
        address = value.address?.let { toApi(it) },
        peopleCapacity = value.peopleCapacity?.let { toApi(it) },
    )

    @JvmStatic
    fun toDomain(value: ApiVenue.Address): DomainVenue.Address = DomainVenue.Address(
        streetWithNumber = value.streetWithNumber,
        postalCode = value.postalCode,
        city = value.city,
    )

    @JvmStatic
    fun toApi(value: DomainVenue.Address): ApiVenue.Address = ApiVenue.Address(
        streetWithNumber = value.streetWithNumber,
        postalCode = value.postalCode,
        city = value.city,
    )

    @JvmStatic
    fun toDomain(value: ApiSchedulingPolicy): DomainSchedulingPolicy = DomainSchedulingPolicy.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainSchedulingPolicy): ApiSchedulingPolicy = ApiSchedulingPolicy.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiCapacity): DomainCapacity = DomainCapacity(
        amount = value.amount,
        unit = value.unit?.let { DomainCapacity.Unit.valueOf(it.name) },
    )

    @JvmStatic
    fun toApi(value: DomainCapacity): ApiCapacity = ApiCapacity(
        amount = value.amount,
        unit = value.unit?.let { ApiCapacity.Unit.valueOf(it.name) },
    )

    @JvmStatic
    fun toDomain(value: ApiRegistrationPolicy): DomainRegistrationPolicy = DomainRegistrationPolicy.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainRegistrationPolicy): ApiRegistrationPolicy = ApiRegistrationPolicy.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiTournamentFormat): DomainTournamentFormat = DomainTournamentFormat.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainTournamentFormat): ApiTournamentFormat = ApiTournamentFormat.valueOf(value.name)

    @JvmStatic
    // JSON and Java callers can supply null IDs despite the published List<Long> type.
    // Copy without unboxing so the domain reports the established validation error.
    fun toDomain(value: ApiParticipantsRoster): DomainParticipantsRoster = DomainParticipantsRoster(
        playerIds = value.playerIds?.toList(),
        teamIds = value.teamIds?.toList(),
    )

    @JvmStatic
    fun toApi(value: DomainParticipantsRoster): ApiParticipantsRoster = ApiParticipantsRoster(
        playerIds = value.playerIds?.let { it.map { item -> requireNotNull(item) } },
        teamIds = value.teamIds?.let { it.map { item -> requireNotNull(item) } },
    )

    @JvmStatic
    fun toDomain(value: ApiScoringRules): DomainScoringRules = DomainScoringRules(
        type = DomainScoringRules.Type.valueOf(value.type.name),
        pointsPerGame = value.pointsPerGame,
        gamesPerMatch = value.gamesPerMatch,
        winByTwo = value.winByTwo,
        capPoints = value.capPoints,
    )

    @JvmStatic
    fun toApi(value: DomainScoringRules): ApiScoringRules = ApiScoringRules(
        type = ApiScoringRules.Type.valueOf(value.type.name),
        pointsPerGame = value.pointsPerGame,
        gamesPerMatch = value.gamesPerMatch,
        winByTwo = value.winByTwo,
        capPoints = value.capPoints,
    )

    @JvmStatic
    fun toDomain(value: ApiSeedingPolicy): DomainSeedingPolicy = DomainSeedingPolicy.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainSeedingPolicy): ApiSeedingPolicy = ApiSeedingPolicy.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiCourtAllocationPolicy): DomainCourtAllocationPolicy = DomainCourtAllocationPolicy.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainCourtAllocationPolicy): ApiCourtAllocationPolicy = ApiCourtAllocationPolicy.valueOf(value.name)

    @JvmStatic
    fun toDomain(value: ApiTieBreakRules): DomainTieBreakRules = DomainTieBreakRules(
        type = DomainTieBreakRules.Type.valueOf(value.type.name),
        useSetDifference = value.useSetDifference,
        usePointsRatio = value.usePointsRatio,
        useBuchholz = value.useBuchholz,
    )

    @JvmStatic
    fun toApi(value: DomainTieBreakRules): ApiTieBreakRules = ApiTieBreakRules(
        type = ApiTieBreakRules.Type.valueOf(value.type.name),
        useSetDifference = value.useSetDifference,
        usePointsRatio = value.usePointsRatio,
        useBuchholz = value.useBuchholz,
    )

    @JvmStatic
    fun toDomain(value: ApiMatchDurationPolicy): DomainMatchDurationPolicy = DomainMatchDurationPolicy.valueOf(value.name)

    @JvmStatic
    fun toApi(value: DomainMatchDurationPolicy): ApiMatchDurationPolicy = ApiMatchDurationPolicy.valueOf(value.name)

}
