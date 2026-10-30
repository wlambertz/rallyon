package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Instant

data class TimeWindow(
    val start: Instant?,
    val end: Instant?
)
