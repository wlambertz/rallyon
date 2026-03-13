@org.springframework.modulith.ApplicationModule(
    id = "setup.configuration",
    type = org.springframework.modulith.ApplicationModule.Type.CLOSED,
    allowedDependencies = {"setup.rules::api", "setup.phases::api"}
)
package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration;

/**
 * Tournament Configuration Submodule
 *
 * Manages tournament-specific configuration settings including:
 * - Tournament types and formats
 * - Scoring systems and tiebreakers
 * - Time limits and match settings
 * - Court and venue configurations
 *
 * This submodule provides the API for configuring tournament parameters
 * and ensures configuration consistency across the system.
 */
