package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal

class TournamentNotFoundException(tournamentId: Long) : RuntimeException("Tournament $tournamentId was not found")
