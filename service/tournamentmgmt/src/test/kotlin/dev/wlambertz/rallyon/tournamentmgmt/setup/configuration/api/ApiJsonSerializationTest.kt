package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.databind.json.JsonMapper

/**
 * Locks in that @AssertTrue validation-helper methods (isXxx()) on the
 * Kotlin-migrated API data classes don't leak into JSON responses. Jackson's
 * java.lang.Record introspection (used while these types were @JvmRecord)
 * only serializes record components; plain-data-class introspection treats
 * any isXxx(): Boolean method as an implicit getter unless told otherwise.
 */
class ApiJsonSerializationTest {

    private val mapper = JsonMapper.builder().findAndAddModules().build()

    @Test
    fun `capacity serializes only its declared properties`() {
        val json = mapper.writeValueAsString(Capacity(10, Capacity.Unit.PEOPLE))

        assertEquals(setOf("amount", "unit"), mapper.readTree(json).propertyNames().toSet())
    }

    @Test
    fun `venue serializes only its declared properties`() {
        val json = mapper.writeValueAsString(Venue("Stadthalle", null, null))

        assertEquals(setOf("name", "address", "peopleCapacity"), mapper.readTree(json).propertyNames().toSet())
    }

    @Test
    fun `participants roster serializes only its declared properties`() {
        val json = mapper.writeValueAsString(ParticipantsRoster(listOf(1L), null))

        assertEquals(setOf("playerIds", "teamIds"), mapper.readTree(json).propertyNames().toSet())
    }

    @Test
    fun `scoring rules serializes only its declared properties`() {
        val json = mapper.writeValueAsString(ScoringRules.twoByTwentyOne())

        assertEquals(
            setOf("type", "pointsPerGame", "gamesPerMatch", "winByTwo", "capPoints"),
            mapper.readTree(json).propertyNames().toSet()
        )
    }
}
