package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class DisciplineConfig(
    val id: Long,
    val category: Category,
    val displayName: String,
    val teamSize: TeamSize,
    val brackets: List<BracketConfig?>
) {
    init {
        require(id > 0) { "Discipline id must be positive" }
    }

    companion object {
        fun of(
            id: Long,
            category: Category,
            displayName: String,
            teamSize: TeamSize,
            brackets: List<BracketConfig?>
        ): DisciplineConfig =

            DisciplineConfig(id, category, displayName, teamSize, java.util.List.copyOf(brackets))
    }
}
