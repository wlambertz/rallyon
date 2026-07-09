package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Positive

data class Capacity(
    @field:Positive(message = "Capacity amount must be positive") val amount: Int?,
    val unit: Unit?
) {
    @JsonIgnore
    fun isUnbounded(): Boolean = amount == null

    @JsonIgnore
    @AssertTrue(message = "Capacity unit must be provided when amount is set")
    fun isUnitConsistent(): Boolean = amount == null || unit != null

    enum class Unit {
        PARTICIPANTS,
        PEOPLE
    }
}
