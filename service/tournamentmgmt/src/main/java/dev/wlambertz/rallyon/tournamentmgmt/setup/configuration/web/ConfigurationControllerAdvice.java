package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = ConfigurationController.class)
class ConfigurationControllerAdvice {

    @ExceptionHandler(InvalidDraftUpdateException.class)
    ProblemDetail handleInvalidDraftUpdate(InvalidDraftUpdateException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(TournamentNotFoundException.class)
    ProblemDetail handleTournamentNotFound(TournamentNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DraftUpdateConflictException.class)
    ProblemDetail handleDraftUpdateConflict(DraftUpdateConflictException exception) {
        return problem(HttpStatus.CONFLICT, exception.getMessage());
    }

    private static ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }
}
