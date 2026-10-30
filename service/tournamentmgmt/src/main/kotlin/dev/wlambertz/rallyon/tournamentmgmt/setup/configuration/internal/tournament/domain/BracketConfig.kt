package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class BracketConfig(
    val id: BracketId,
    val displayName: String,
    val format: TournamentFormat,
    val capacity: Capacity?
)
