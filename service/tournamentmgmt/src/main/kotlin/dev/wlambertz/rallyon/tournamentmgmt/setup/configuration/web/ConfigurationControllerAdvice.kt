package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [ConfigurationController::class])
internal class ConfigurationControllerAdvice {

    @ExceptionHandler(InvalidDraftUpdateException::class)
    fun handleInvalidDraftUpdate(exception: InvalidDraftUpdateException): ProblemDetail =
        problem(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(TournamentNotFoundException::class)
    fun handleTournamentNotFound(exception: TournamentNotFoundException): ProblemDetail =
        problem(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(DraftUpdateConflictException::class)
    fun handleDraftUpdateConflict(exception: DraftUpdateConflictException): ProblemDetail =
        problem(HttpStatus.CONFLICT, exception.message)

    private fun problem(status: HttpStatus, detail: String?): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
        problemDetail.title = status.reasonPhrase
        return problemDetail
    }
}
