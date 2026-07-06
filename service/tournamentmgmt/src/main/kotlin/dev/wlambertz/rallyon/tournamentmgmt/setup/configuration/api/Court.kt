package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@JvmRecord
data class Court(
    val id: Long,
    @field:NotBlank val label: String?,
    @field:NotNull val availability: Availability?,
    @field:NotNull val type: Type?
) {
    enum class Availability {
        AVAILABLE,
        IN_USE,
        UNAVAILABLE
    }

    enum class Type {
        STANDARD,
        SINGLES_ONLY
    }
}
