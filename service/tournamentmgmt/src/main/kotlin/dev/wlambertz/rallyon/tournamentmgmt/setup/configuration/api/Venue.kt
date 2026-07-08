package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class Venue(
    @field:NotBlank val name: String?,
    @field:Valid val address: Address?,
    @field:Valid val peopleCapacity: Capacity?
) {
    @AssertTrue(message = "Venue capacity must use PEOPLE unit when amount is set")
    fun isPeopleCapacityConsistent(): Boolean =
        peopleCapacity == null || peopleCapacity.amount == null || peopleCapacity.unit == Capacity.Unit.PEOPLE

    data class Address(
        @field:NotBlank val streetWithNumber: String?,
        @field:NotBlank
        @field:Size(min = 5, max = 5, message = "Postal code must be exactly 5 characters")
        val postalCode: String?,
        @field:NotBlank val city: String?
    )
}
