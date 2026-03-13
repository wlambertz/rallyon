package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal;

public final class TournamentNotFoundException extends RuntimeException {

    public TournamentNotFoundException(long tournamentId) {
        super("Tournament " + tournamentId + " was not found");
    }
}
