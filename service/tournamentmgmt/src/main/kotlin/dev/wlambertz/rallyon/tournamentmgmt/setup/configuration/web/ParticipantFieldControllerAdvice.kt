package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldNotFound
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice(assignableTypes = [ParticipantFieldController::class])
internal class ParticipantFieldControllerAdvice {
    @ExceptionHandler(ParticipantFieldInvalid::class)
    fun invalid() = problem(HttpStatus.BAD_REQUEST, "Invalid participant-field selection or request")

    @ExceptionHandler(ParticipantFieldForbidden::class)
    fun forbidden() = problem(HttpStatus.FORBIDDEN, "Participant-field access denied")

    @ExceptionHandler(ParticipantFieldNotFound::class)
    fun missing() = problem(HttpStatus.NOT_FOUND, "Tournament or participant-field target not found")

    @ExceptionHandler(ParticipantFieldConflict::class)
    fun conflict() = problem(HttpStatus.CONFLICT, "Participant-field source, version or selection conflict")

    @ExceptionHandler(
        MissingRequestHeaderException::class,
        MissingServletRequestParameterException::class,
        MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class
    )
    fun malformed() = problem(HttpStatus.BAD_REQUEST, "Malformed participant-field request or version")

    private fun problem(status: HttpStatus, detail: String): ResponseEntity<ProblemDetail> =
        ResponseEntity.status(status).cacheControl(CacheControl.noStore())
            .body(ProblemDetail.forStatusAndDetail(status, detail))
}
