package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class BracketId(val value: String) {

    init {
        require(value.isNotBlank()) { "BracketId must not be blank" }
    }

    override fun toString(): String = value

    companion object {
        fun of(value: String): BracketId = BracketId(value)
    }
}
