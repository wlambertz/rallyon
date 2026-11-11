package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementNotFound
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [AnnouncementController::class])
internal class AnnouncementControllerAdvice {
    @ExceptionHandler(AnnouncementInvalid::class)
    fun invalid(exception: AnnouncementInvalid) = problem(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(AnnouncementForbidden::class)
    fun forbidden(exception: AnnouncementForbidden) = problem(HttpStatus.FORBIDDEN, exception.message)

    @ExceptionHandler(AnnouncementNotFound::class)
    fun missing(): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN)
            .cacheControl(CacheControl.noStore()).body("Announcement not found")

    @ExceptionHandler(AnnouncementConflict::class)
    fun conflict(exception: AnnouncementConflict) = problem(HttpStatus.CONFLICT, exception.message)

    private fun problem(status: HttpStatus, detail: String?): ResponseEntity<ProblemDetail> =
        ResponseEntity.status(status).cacheControl(CacheControl.noStore())
            .body(ProblemDetail.forStatusAndDetail(status, detail ?: status.reasonPhrase))
}
