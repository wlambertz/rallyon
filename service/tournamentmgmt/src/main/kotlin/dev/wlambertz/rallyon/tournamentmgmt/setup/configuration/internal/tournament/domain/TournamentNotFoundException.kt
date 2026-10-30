package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

class TournamentNotFoundException(tournamentId: Long) : RuntimeException("Tournament $tournamentId was not found")
