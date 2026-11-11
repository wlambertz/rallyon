package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryNotFound
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice(assignableTypes = [EntryBookController::class])
internal class EntryBookControllerAdvice {
    @ExceptionHandler(EntryInvalid::class)
    fun invalid(exception: EntryInvalid) = problem(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(EntryForbidden::class)
    fun forbidden() = problem(HttpStatus.FORBIDDEN, "Entry book access denied")

    @ExceptionHandler(EntryNotFound::class)
    fun missing() = problem(HttpStatus.NOT_FOUND, "Tournament or entry not found")

    @ExceptionHandler(EntryConflict::class)
    fun conflict(exception: EntryConflict) = problem(HttpStatus.CONFLICT, exception.message)

    @ExceptionHandler(MissingRequestHeaderException::class, MethodArgumentTypeMismatchException::class,
        HttpMessageNotReadableException::class)
    fun malformed() = problem(HttpStatus.BAD_REQUEST, "Malformed entry request or version")

    private fun problem(status: HttpStatus, detail: String?): ResponseEntity<ProblemDetail> =
        ResponseEntity.status(status).cacheControl(CacheControl.noStore())
            .body(ProblemDetail.forStatusAndDetail(status, detail ?: status.reasonPhrase))
}
