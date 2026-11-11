package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakValidationException
import dev.wlambertz.rallyon.iam.keycloak.spring.AuthenticatedPrincipalProvider
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryBookService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryBookView
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryInput
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryState
import org.springframework.http.CacheControl
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tournamentmgmt/entries/{tournamentId}")
class EntryBookController(
    private val service: EntryBookService,
    private val principalProvider: AuthenticatedPrincipalProvider
) {
    @GetMapping
    fun get(@PathVariable tournamentId: Long): ResponseEntity<EntryBookView> =
        response(service.get(tournamentId, actor()))

    @PutMapping("/intake")
    fun intake(
        @PathVariable tournamentId: Long,
        @RequestBody request: IntakeRequest,
        @RequestHeader("If-Match") version: Long
    ): ResponseEntity<EntryBookView> = response(
        service.intake(tournamentId, request.open ?: throw EntryInvalid("Intake state is required"), version, actor())
    )

    @PostMapping("/records")
    fun create(
        @PathVariable tournamentId: Long,
        @RequestBody input: EntryInput,
        @RequestHeader("If-Match") version: Long
    ): ResponseEntity<EntryBookView> = response(service.create(tournamentId, input, version, actor()), HttpStatus.CREATED)

    @PutMapping("/records/{entryId}")
    fun correct(
        @PathVariable tournamentId: Long,
        @PathVariable entryId: Long,
        @RequestBody input: EntryInput,
        @RequestHeader("If-Match") version: Long
    ): ResponseEntity<EntryBookView> = response(service.correct(tournamentId, entryId, input, version, actor()))

    @PutMapping("/records/{entryId}/decision")
    fun decide(
        @PathVariable tournamentId: Long,
        @PathVariable entryId: Long,
        @RequestBody request: DecisionRequest,
        @RequestHeader("If-Match") version: Long
    ): ResponseEntity<EntryBookView> = response(
        service.decide(tournamentId, entryId, request.state ?: throw EntryInvalid("Entry decision is required"), version, actor())
    )

    @DeleteMapping("/records/{entryId}")
    fun withdraw(
        @PathVariable tournamentId: Long,
        @PathVariable entryId: Long,
        @RequestHeader("If-Match") version: Long
    ): ResponseEntity<EntryBookView> = response(service.withdraw(tournamentId, entryId, version, actor()))

    private fun actor(): Long {
        val id = try {
            principalProvider.currentPrincipal().orElseThrow {
                EntryForbidden("Authenticated user identity is required")
            }.userId().orElseThrow { EntryForbidden("Numeric user identity is required") }
        } catch (_: KeycloakValidationException) {
            throw EntryForbidden("Valid user identity is required")
        }
        if (id <= 0) throw EntryForbidden("Positive user identity is required")
        return id
    }

    private fun response(book: EntryBookView, status: HttpStatus = HttpStatus.OK): ResponseEntity<EntryBookView> =
        ResponseEntity.status(status).cacheControl(CacheControl.noStore()).body(book)

    data class IntakeRequest(val open: Boolean?)
    data class DecisionRequest(val state: EntryState?)
}
