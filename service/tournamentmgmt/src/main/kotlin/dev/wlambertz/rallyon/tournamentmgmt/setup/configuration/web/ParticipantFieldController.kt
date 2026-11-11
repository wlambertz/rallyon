package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakValidationException
import dev.wlambertz.rallyon.iam.keycloak.spring.AuthenticatedPrincipalProvider
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldRequest
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldView
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.JsonNode

@RestController
@RequestMapping("/api/tournamentmgmt/participant-fields/{tournamentId}")
class ParticipantFieldController(
    private val service: ParticipantFieldService,
    private val principalProvider: AuthenticatedPrincipalProvider
) {
    @GetMapping
    fun get(
        @PathVariable tournamentId: Long,
        @RequestParam disciplineId: Long,
        @RequestParam bracketId: String
    ): ResponseEntity<ParticipantFieldView> = response(service.get(tournamentId, disciplineId, bracketId, actor()))

    @PutMapping
    fun prepare(
        @PathVariable tournamentId: Long,
        @RequestBody input: JsonNode,
        @RequestHeader("If-Match") version: Long
    ): ResponseEntity<ParticipantFieldView> = response(service.prepare(tournamentId, request(input), version, actor()))

    private fun request(input: JsonNode): ParticipantFieldRequest {
        // The shared mapper coerces fractions and strings to Long; this endpoint's version contract forbids that.
        if (!input.isObject) throw ParticipantFieldInvalid("Request must be an object")
        val bracket = input.path("bracketId")
        val entries = input.path("entryIds")
        if (!bracket.isString || !entries.isArray) throw ParticipantFieldInvalid("Invalid selection shape")
        return ParticipantFieldRequest(
            integer(input.path("disciplineId")),
            bracket.stringValue(),
            entries.values().map(::integer),
            integer(input.path("sourceTournamentVersion")),
            integer(input.path("sourceEntryBookVersion"))
        )
    }

    private fun integer(value: JsonNode): Long {
        if (!value.isIntegralNumber || !value.canConvertToLong()) {
            throw ParticipantFieldInvalid("Expected an integer within the supported range")
        }
        return value.longValue()
    }

    private fun actor(): Long {
        val id = try {
            principalProvider.currentPrincipal().orElseThrow {
                ParticipantFieldForbidden("Authenticated user identity is required")
            }.userId().orElseThrow { ParticipantFieldForbidden("Numeric user identity is required") }
        } catch (_: KeycloakValidationException) {
            throw ParticipantFieldForbidden("Valid user identity is required")
        }
        if (id <= 0) throw ParticipantFieldForbidden("Positive user identity is required")
        return id
    }

    private fun response(field: ParticipantFieldView): ResponseEntity<ParticipantFieldView> =
        ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(field)
}
