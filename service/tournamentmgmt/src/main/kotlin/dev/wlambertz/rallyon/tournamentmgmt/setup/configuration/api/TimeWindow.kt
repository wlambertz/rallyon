package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.constraints.NotNull
import java.time.Instant

@TimeWindowRange
data class TimeWindow(
    @field:NotNull val start: Instant?,
    @field:NotNull val end: Instant?
)
