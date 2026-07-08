package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class DisciplineConfig(
    @field:Positive val id: Long,
    @field:NotNull val category: Category,
    @field:NotBlank val displayName: String,
    @field:NotNull val teamSize: TeamSize,
    @field:NotNull @field:Valid val brackets: List<BracketConfig>
) {
    init {
        require(id > 0) { "Discipline id must be positive" }
    }

    companion object {
        @JvmStatic
        fun of(
            id: Long,
            category: Category,
            displayName: String,
            teamSize: TeamSize,
            brackets: List<BracketConfig>
        ): DisciplineConfig =
            // Defensive copy lives in the factory to keep the primary constructor simple.
            DisciplineConfig(id, category, displayName, teamSize, java.util.List.copyOf(brackets))
    }
}
