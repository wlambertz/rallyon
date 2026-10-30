package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

data class Venue(
    val name: String?,
    val address: Address?,
    val peopleCapacity: Capacity?
) {
    fun isPeopleCapacityConsistent(): Boolean =
        peopleCapacity == null || peopleCapacity.amount == null || peopleCapacity.unit == Capacity.Unit.PEOPLE

    data class Address(
        val streetWithNumber: String?,
        val postalCode: String?,
        val city: String?
    )
}
