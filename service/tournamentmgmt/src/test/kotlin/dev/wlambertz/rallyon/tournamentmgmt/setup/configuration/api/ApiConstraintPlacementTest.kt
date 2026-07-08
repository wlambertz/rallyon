package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * Locks in that Bean Validation constraints on the Kotlin-migrated API data
 * classes stay visible to Hibernate Validator. Kotlin does not replicate
 * javac's record-component annotation propagation (the mechanism real Java
 * records rely on), so a missing or mis-targeted use-site target (@field:)
 * would silently disable a constraint — no runtime path validates these
 * types today.
 */
class ApiConstraintPlacementTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().use { it.validator }

    private fun <T> violatedPaths(bean: T): Set<String> =
        validator.validate(bean).map { it.propertyPath.toString() }.toSet()

    @Test
    fun `field constraint on data class property is enforced`() {
        val paths = violatedPaths(Court(1L, " ", Court.Availability.AVAILABLE, Court.Type.STANDARD))

        assertTrue("label" in paths) { "expected @NotBlank violation on Court.label, got $paths" }
    }

    @Test
    fun `assert-true method constraint is enforced`() {
        val paths = violatedPaths(Capacity(32, null))

        assertTrue("unitConsistent" in paths) { "expected @AssertTrue violation on Capacity.isUnitConsistent, got $paths" }
    }

    @Test
    fun `class-level time window range constraint is enforced`() {
        val start = Instant.parse("2026-07-06T12:00:00Z")
        val paths = violatedPaths(TimeWindow(start, start.minusSeconds(60)))

        assertTrue("" in paths) { "expected class-level @TimeWindowRange violation, got $paths" }
    }

    @Test
    fun `size constraint on nested address data class is enforced`() {
        val paths = violatedPaths(Venue.Address("Hauptstr. 1", "123", "Bonn"))

        assertTrue("postalCode" in paths) { "expected @Size violation on Address.postalCode, got $paths" }
    }

    @Test
    fun `valid annotation cascades into nested address`() {
        val venue = Venue("Stadthalle", Venue.Address("Hauptstr. 1", "123", "Bonn"), null)

        val paths = violatedPaths(venue)

        assertTrue("address.postalCode" in paths) {
            "expected cascaded violation under address.postalCode, got $paths"
        }
    }
}
