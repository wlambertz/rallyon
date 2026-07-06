package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.iam.keycloak.spring.AuthenticatedPrincipalProvider
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Locale

@RestController
@RequestMapping("/api/tournamentmgmt/config")
class ConfigurationController(
    private val configurationService: ConfigurationService,
    private val principalProvider: AuthenticatedPrincipalProvider
) {

    private fun actingUserId(): Long =
        principalProvider.requirePrincipal()
            .userId()
            .orElseThrow { IllegalStateException("Token missing numeric rallyon_user_id claim.") }

    // Lifecycle
    @PostMapping("/drafts")
    fun createDraft(
        @RequestParam("organizerId") organizerId: Long,
        @RequestBody request: CreateDraftRequest
    ): ResponseEntity<Tournament> {
        val created = configurationService.createDraft(
            organizerId, request.name, request.visibility, actingUserId()
        )
        return ResponseEntity(created, HttpStatus.CREATED)
    }

    @PutMapping("/{tournamentId}/draft")
    fun updateDraft(
        @PathVariable tournamentId: Long,
        @RequestBody draftChanges: Tournament,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.updateDraft(tournamentId, draftChanges, version, actingUserId())

    @PostMapping("/{tournamentId}/publish")
    fun publish(
        @PathVariable tournamentId: Long,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.publish(tournamentId, version, actingUserId())

    @PostMapping("/{tournamentId}/registration/open")
    fun openRegistration(
        @PathVariable tournamentId: Long,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.openRegistration(tournamentId, version, actingUserId())

    @PostMapping("/{tournamentId}/registration/close")
    fun closeRegistration(
        @PathVariable tournamentId: Long,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.closeRegistration(tournamentId, version, actingUserId())

    @PostMapping("/{tournamentId}/lock")
    fun lockConfiguration(
        @PathVariable tournamentId: Long,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.lockConfiguration(tournamentId, version, actingUserId())

    @PostMapping("/{tournamentId}/start")
    fun start(
        @PathVariable tournamentId: Long,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.start(tournamentId, version, actingUserId())

    @PostMapping("/{tournamentId}/complete")
    fun complete(
        @PathVariable tournamentId: Long,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.complete(tournamentId, version, actingUserId())

    @PostMapping("/{tournamentId}/cancel")
    fun cancel(
        @PathVariable tournamentId: Long,
        @RequestParam("reason") reason: String,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.cancel(tournamentId, version, reason, actingUserId())

    // Core configuration setters
    @PutMapping("/{tournamentId}/basics")
    fun setBasics(
        @PathVariable tournamentId: Long,
        @RequestBody request: SetBasicsRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setBasics(
        tournamentId,
        request.name,
        request.description,
        request.locale,
        request.visibility,
        version,
        actingUserId()
    )

    @PutMapping("/{tournamentId}/schedule")
    fun setSchedule(
        @PathVariable tournamentId: Long,
        @RequestBody request: SetScheduleRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setSchedule(
        tournamentId,
        request.schedule,
        request.registrationWindows,
        version,
        actingUserId()
    )

    @PutMapping("/{tournamentId}/venue")
    fun setVenueAndCourts(
        @PathVariable tournamentId: Long,
        @RequestBody request: SetVenueAndCourtsRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setVenueAndCourts(
        tournamentId,
        request.venue,
        request.courts,
        version,
        actingUserId()
    )

    @PutMapping("/{tournamentId}/disciplines")
    fun setDisciplines(
        @PathVariable tournamentId: Long,
        @RequestBody request: SetDisciplinesRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setDisciplines(
        tournamentId,
        request.disciplines,
        version,
        actingUserId()
    )

    @PutMapping("/{tournamentId}/capacity")
    fun setCapacity(
        @PathVariable tournamentId: Long,
        @RequestBody capacity: Capacity,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setCapacity(tournamentId, capacity, version, actingUserId())

    @PutMapping("/{tournamentId}/policies")
    fun setPolicies(
        @PathVariable tournamentId: Long,
        @RequestBody request: SetPoliciesRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setPolicies(
        tournamentId,
        request.registrationPolicy,
        request.schedulingPolicy,
        request.courtAllocationPolicy,
        version,
        actingUserId()
    )

    @PutMapping("/{tournamentId}/rules")
    fun setRules(
        @PathVariable tournamentId: Long,
        @RequestBody request: SetRulesRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setRules(
        tournamentId,
        request.scoringRules,
        request.tieBreakRules,
        request.matchDurationPolicy,
        request.seedingPolicy,
        version,
        actingUserId()
    )

    // Roster
    @PutMapping("/{tournamentId}/participants")
    fun setParticipantsRoster(
        @PathVariable tournamentId: Long,
        @RequestBody roster: ParticipantsRoster,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setParticipantsRoster(tournamentId, roster, version, actingUserId())

    @PutMapping("/{tournamentId}/participants/brackets/{bracketId}")
    fun setBracketRoster(
        @PathVariable tournamentId: Long,
        @PathVariable bracketId: String,
        @RequestBody roster: ParticipantsRoster,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.setBracketRoster(
        tournamentId,
        BracketId.of(bracketId),
        roster,
        version,
        actingUserId()
    )

    @PostMapping("/{tournamentId}/participants")
    fun addParticipant(
        @PathVariable tournamentId: Long,
        @RequestBody request: AddParticipantRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.addParticipant(
        tournamentId,
        request.playerId,
        request.teamId,
        // !! reproduces the Java record's implicit unboxing/argument NPE on null input
        request.disciplineId!!,
        BracketId.of(request.bracketId!!),
        version,
        actingUserId()
    )

    @DeleteMapping("/{tournamentId}/participants")
    fun removeParticipant(
        @PathVariable tournamentId: Long,
        @RequestBody request: RemoveParticipantRequest,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.removeParticipant(
        tournamentId,
        request.playerId,
        request.teamId,
        // !! reproduces the Java record's implicit unboxing/argument NPE on null input
        request.disciplineId!!,
        BracketId.of(request.bracketId!!),
        version,
        actingUserId()
    )

    // Phases & validation
    @PutMapping("/{tournamentId}/phases")
    fun definePhases(
        @PathVariable tournamentId: Long,
        @RequestBody phases: List<Phase>,
        @RequestHeader("If-Match") version: Long
    ): Tournament = configurationService.definePhases(tournamentId, phases, version, actingUserId())

    @PostMapping("/{tournamentId}/validate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validateConfiguration(@PathVariable tournamentId: Long) {
        configurationService.validateConfiguration(tournamentId)
    }

    // Queries
    @GetMapping("/{tournamentId}")
    fun get(@PathVariable tournamentId: Long): Tournament = configurationService.get(tournamentId)

    @GetMapping("/organizers/{organizerId}/tournaments")
    fun listByOrganizer(
        @PathVariable organizerId: Long,
        @RequestParam(value = "statuses", required = false) statuses: Set<TournamentStatus>?,
        @RequestParam(value = "visibility", required = false) visibilityFilter: Visibility?
    ): List<Tournament> = configurationService.listByOrganizer(organizerId, statuses, visibilityFilter)

    @GetMapping("/public")
    fun findPublic(
        @RequestParam(value = "q", required = false) search: String?,
        @RequestParam(value = "locale", required = false) locale: Locale?,
        @RequestParam(value = "from", required = false) from: Instant?,
        @RequestParam(value = "to", required = false) to: Instant?
    ): List<Tournament> = configurationService.findPublic(search, locale, from, to)

    // Simple request DTOs to keep the controller lean. Fields are nullable to
    // preserve the former Java records' pass-through of absent JSON fields.
    data class CreateDraftRequest(val name: String?, val visibility: Visibility?)

    data class SetBasicsRequest(
        val name: String?,
        val description: String?,
        val locale: Locale?,
        val visibility: Visibility?
    )

    data class SetScheduleRequest(
        val schedule: TimeWindow?,
        val registrationWindows: List<TimeWindow>?
    )

    data class SetVenueAndCourtsRequest(
        val venue: Venue?,
        val courts: List<Court>?
    )

    data class SetDisciplinesRequest(
        val disciplines: List<DisciplineConfig>?
    )

    data class SetPoliciesRequest(
        val registrationPolicy: RegistrationPolicy?,
        val schedulingPolicy: SchedulingPolicy?,
        val courtAllocationPolicy: CourtAllocationPolicy?
    )

    data class SetRulesRequest(
        val scoringRules: ScoringRules?,
        val tieBreakRules: TieBreakRules?,
        val matchDurationPolicy: MatchDurationPolicy?,
        val seedingPolicy: SeedingPolicy?
    )

    data class AddParticipantRequest(
        val playerId: Long?,
        val teamId: Long?,
        val disciplineId: Long?,
        val bracketId: String?
    )

    data class RemoveParticipantRequest(
        val playerId: Long?,
        val teamId: Long?,
        val disciplineId: Long?,
        val bracketId: String?
    )
}
