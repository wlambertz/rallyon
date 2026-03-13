@org.springframework.modulith.ApplicationModule(
    id = "setup.rules",
    type = org.springframework.modulith.ApplicationModule.Type.CLOSED
)
package dev.wlambertz.rallyon.tournamentmgmt.setup.rules;

/**
 * Tournament Rules Submodule
 *
 * Defines and manages tournament rules and regulations including:
 * - Tournament format rules (Swiss system, round-robin, etc.)
 * - Scoring rules and point calculations
 * - Eligibility and participation rules
 * - Custom rule definitions per tournament type
 *
 * This submodule provides a flexible rule engine for different
 * tournament formats and ensures fair play enforcement.
 */
