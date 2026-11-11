package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.iam.keycloak.spring.AuthenticatedPrincipalProvider
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakValidationException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementContent
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementNotFound
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementView
import java.net.URI
import java.nio.charset.StandardCharsets
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
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
@RequestMapping("/api/tournamentmgmt/announcements")
class AnnouncementController(
    private val service: AnnouncementService,
    private val principalProvider: AuthenticatedPrincipalProvider
) {
    private fun actor(): Long {
        val userId = try {
            principalProvider.currentPrincipal().orElseThrow {
                AnnouncementForbidden("Authenticated user identity is required")
            }.userId().orElseThrow { AnnouncementForbidden("Numeric user identity is required") }
        } catch (_: KeycloakValidationException) {
            throw AnnouncementForbidden("Valid user identity is required")
        }
        if (userId <= 0) throw AnnouncementForbidden("Positive user identity is required")
        return userId
    }

    @PostMapping("/{tournamentId}")
    fun create(@PathVariable tournamentId: Long, @RequestBody content: AnnouncementContent): ResponseEntity<AnnouncementView> {
        val saved = service.create(tournamentId, content, actor())
        return ResponseEntity.created(URI.create("/api/tournamentmgmt/announcements/$tournamentId")).body(saved)
    }

    @PutMapping("/{tournamentId}")
    fun replace(
        @PathVariable tournamentId: Long,
        @RequestBody content: AnnouncementContent,
        @RequestHeader("If-Match") version: Long
    ): AnnouncementView = service.replace(tournamentId, content, version, actor())

    @DeleteMapping("/{tournamentId}")
    fun withdraw(@PathVariable tournamentId: Long, @RequestHeader("If-Match") version: Long): AnnouncementView =
        service.withdraw(tournamentId, version, actor())

    @GetMapping("/{tournamentId}/management")
    fun management(@PathVariable tournamentId: Long): ResponseEntity<AnnouncementView> =
        ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.management(tournamentId, actor()))

    @GetMapping("/{tournamentId}", produces = ["text/plain;charset=UTF-8"])
    fun publicRead(@PathVariable tournamentId: Long): ResponseEntity<String> {
        val announcement = service.publicRead(tournamentId)
        if (!announcement.published) throw AnnouncementNotFound("Announcement not found")
        val content = announcement.content
        val text = "${content.title}\n${content.eventStart} – ${content.eventEnd}\n${content.venue}\n\n" +
            "${content.body}\n\nVersion: ${announcement.version}\nPublished: ${announcement.publishedAt}\n"
        return ResponseEntity.ok()
            .contentType(MediaType("text", "plain", StandardCharsets.UTF_8))
            .cacheControl(CacheControl.noStore())
            .header("X-Content-Type-Options", "nosniff")
            .header("Content-Security-Policy", "default-src 'none'; sandbox")
            .body(text)
    }
}
