package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

/**
 * Identifier for a tournament bracket (Teilnehmerfeld).
 */
@JvmRecord
data class BracketId(val value: String) {

    init {
        require(value.isNotBlank()) { "BracketId must not be blank" }
    }

    // Map keys serialize through toString; keep the raw value representation.
    override fun toString(): String = value

    companion object {
        @JvmStatic
        fun of(value: String): BracketId = BracketId(value)
    }
}
