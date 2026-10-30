package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Instant

fun Tournament.validateDraftBasics() {
    if (name == null) {
        throw InvalidDraftUpdateException("Tournament name must not be null")
    }
    if (name.isBlank()) {
        throw InvalidDraftUpdateException("Tournament name must not be blank")
    }
    if (name.length > 200) {
        throw InvalidDraftUpdateException("Tournament name must be <= 200 characters")
    }
    if (visibility == null) {
        throw InvalidDraftUpdateException("Visibility must not be null")
    }
    if (!phases.isNullOrEmpty()) {
        throw InvalidDraftUpdateException("Phases are not supported by draft updates yet")
    }
}

fun Tournament.replaceDraft(changes: Tournament, expectedVersion: Long, actor: Long, now: Instant): Tournament {
    changes.validateDraftBasics()
    if (status != TournamentStatus.DRAFT) {
        throw DraftUpdateConflictException("Only tournaments in DRAFT status can be updated")
    }
    if (version == null || version != expectedVersion) {
        throw DraftUpdateConflictException("Draft version mismatch: expected $version but got $expectedVersion")
    }
    changes.validateReplacementAgainst(this)
    return changes.copy(
        id = id, version = version, organizerId = organizerId, status = status,
        createdAt = createdAt, createdByUserId = createdByUserId,
        lastModifiedAt = now, lastModifiedByUserId = actor
    )
}

fun Tournament.validateReplacementAgainst(existing: Tournament) {
    validateTimeWindow(schedule, "Schedule")
    validateVenue(venue)
    validateCapacity(capacity, "Tournament capacity")
    registrationWindows.orEmpty().forEach { validateTimeWindow(it, "Registration window") }
    validateCourts(courts.orEmpty(), existing.courts.orEmpty())
    val bracketTeams = validateDisciplines(disciplines.orEmpty())
    participants?.let { validateRoster(it, "Tournament participants") }
    validateBracketRosters(bracketRosters.orEmpty(), bracketTeams)
}

private fun validateCourts(courts: List<Court?>, existingCourts: List<Court?>) {
    val courtIds = mutableSetOf<Long>()
    val labels = mutableSetOf<String?>()
    for (court in courts) {
        validateCourt(court)
        court!!
        if (!labels.add(court.label)) {
            throw InvalidDraftUpdateException("Duplicate court label '${court.label}'")
        }
        if (court.id > 0) {
            if (!courtIds.add(court.id)) {
                throw InvalidDraftUpdateException("Duplicate court id '${court.id}'")
            }
            if (existingCourts.none { it?.id == court.id }) {
                throw InvalidDraftUpdateException("Unknown court id '${court.id}'")
            }
        } else if (court.id < 0) {
            throw InvalidDraftUpdateException("Court id must not be negative")
        }
    }
}

private fun validateDisciplines(disciplines: List<DisciplineConfig?>): Map<String, TeamSize> {
    val disciplinesSeen = mutableSetOf<Long>()
    val bracketsSeen = mutableSetOf<String>()
    val bracketTeams = linkedMapOf<String, TeamSize>()
    for (discipline in disciplines) {
        validateDiscipline(discipline)
        discipline!!
        if (!disciplinesSeen.add(discipline.id)) {
            throw InvalidDraftUpdateException("Duplicate discipline id '${discipline.id}'")
        }
        val localBrackets = mutableSetOf<String>()
        for (bracket in discipline.brackets) {
            validateBracket(bracket)
            bracket!!
            val id = bracket.id.value
            if (!localBrackets.add(id)) {
                throw InvalidDraftUpdateException("Duplicate bracket id '$id' within discipline ${discipline.id}")
            }
            if (!bracketsSeen.add(id)) {
                throw InvalidDraftUpdateException("Duplicate bracket id '$id' across tournament disciplines")
            }
            bracketTeams[id] = discipline.teamSize
        }
    }
    return bracketTeams
}

private fun validateBracketRosters(
    bracketRosters: Map<BracketId, ParticipantsRoster?>,
    bracketTeams: Map<String, TeamSize>
) {
    for ((id, roster) in bracketRosters) {
        if (roster == null) {
            throw InvalidDraftUpdateException("Bracket roster for '${id.value}' must not be null")
        }
        val teamSize = bracketTeams[id.value] ?: throw InvalidDraftUpdateException("Unknown bracket roster key '${id.value}'")
        val kind = validateRoster(roster, "Bracket roster '${id.value}'")
        if (teamSize == TeamSize.SINGLES && kind != RosterKind.PLAYER) {
            throw InvalidDraftUpdateException("Bracket roster '${id.value}' must use playerIds for singles disciplines")
        }
        if (teamSize == TeamSize.DOUBLES && kind != RosterKind.TEAM) {
            throw InvalidDraftUpdateException("Bracket roster '${id.value}' must use teamIds for doubles disciplines")
        }
    }
}

private fun validateRoster(roster: ParticipantsRoster, fieldName: String): RosterKind {
    val playerIds = roster.playerIds
    val teamIds = roster.teamIds

    val hasPlayers = playerIds != null
    val hasTeams = teamIds != null
    if (hasPlayers == hasTeams) {
        throw InvalidDraftUpdateException("$fieldName must provide exactly one of playerIds or teamIds")
    }

    if (hasPlayers) {
        validateIds(playerIds, "$fieldName playerIds")
        return RosterKind.PLAYER
    }

    validateIds(teamIds!!, "$fieldName teamIds")
    return RosterKind.TEAM
}

private fun validateIds(ids: List<Long?>, fieldName: String) {
    val seenIds = LinkedHashSet<Long>()
    for (id in ids) {
        if (id == null || id <= 0) {
            throw InvalidDraftUpdateException("$fieldName must only contain positive ids")
        }
        if (!seenIds.add(id)) {
            throw InvalidDraftUpdateException("Duplicate id '$id' in $fieldName")
        }
    }
}

private fun validateCourt(court: Court?) {
    if (court == null) {
        throw InvalidDraftUpdateException("Court must not be null")
    }
    if (court.label == null || court.label.isBlank()) {
        throw InvalidDraftUpdateException("Court label must not be blank")
    }
    if (court.availability == null) {
        throw InvalidDraftUpdateException("Court availability must not be null")
    }
    if (court.type == null) {
        throw InvalidDraftUpdateException("Court type must not be null")
    }
}

private fun validateDiscipline(discipline: DisciplineConfig?) {
    if (discipline == null) {
        throw InvalidDraftUpdateException("Discipline must not be null")
    }
    if (discipline.id <= 0) {
        throw InvalidDraftUpdateException("Discipline id must be positive")
    }
    if (discipline.displayName.isBlank()) {
        throw InvalidDraftUpdateException("Discipline display name must not be blank")
    }
}

private fun validateBracket(bracket: BracketConfig?) {
    if (bracket == null) {
        throw InvalidDraftUpdateException("Bracket must not be null")
    }
    if (bracket.id.value.isBlank()) {
        throw InvalidDraftUpdateException("Bracket id must not be blank")
    }
    if (bracket.displayName.isBlank()) {
        throw InvalidDraftUpdateException("Bracket display name must not be blank")
    }
    validateCapacity(bracket.capacity, "Bracket capacity")
}

private fun validateVenue(venue: Venue?) {
    if (venue == null) {
        return
    }
    if (venue.name == null || venue.name.isBlank()) {
        throw InvalidDraftUpdateException("Venue name must not be blank")
    }
    if (venue.address != null) {
        if (venue.address.streetWithNumber == null || venue.address.streetWithNumber.isBlank()) {
            throw InvalidDraftUpdateException("Venue street must not be blank")
        }
        val postalCode = venue.address.postalCode
        if (postalCode == null || postalCode.length != 5) {
            throw InvalidDraftUpdateException("Venue postal code must be exactly 5 characters")
        }
        if (venue.address.city == null || venue.address.city.isBlank()) {
            throw InvalidDraftUpdateException("Venue city must not be blank")
        }
    }

    validateCapacity(venue.peopleCapacity, "Venue capacity")
    val peopleCapacity = venue.peopleCapacity
    if (peopleCapacity != null && peopleCapacity.amount != null && peopleCapacity.unit != Capacity.Unit.PEOPLE) {
        throw InvalidDraftUpdateException("Venue capacity must use PEOPLE as unit")
    }
}

private fun validateCapacity(capacity: Capacity?, fieldName: String) {
    if (capacity == null) {
        return
    }
    if (capacity.amount != null && capacity.amount <= 0) {
        throw InvalidDraftUpdateException("$fieldName amount must be positive")
    }
    if (capacity.amount != null && capacity.unit == null) {
        throw InvalidDraftUpdateException("$fieldName unit must be provided when amount is set")
    }
}

private fun validateTimeWindow(window: TimeWindow?, fieldName: String) {
    if (window == null) {
        return
    }
    if (window.start == null || window.end == null) {
        throw InvalidDraftUpdateException("$fieldName must define both start and end")
    }
    if (window.end.isBefore(window.start)) {
        throw InvalidDraftUpdateException("$fieldName end must not be before start")
    }
}

private enum class RosterKind { PLAYER, TEAM }
