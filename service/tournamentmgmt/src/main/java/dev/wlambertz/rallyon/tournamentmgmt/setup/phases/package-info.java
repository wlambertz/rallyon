@org.springframework.modulith.ApplicationModule(
    id = "setup.phases",
    type = org.springframework.modulith.ApplicationModule.Type.CLOSED
)
package dev.wlambertz.rallyon.tournamentmgmt.setup.phases;

/**
 * Tournament Phases Submodule
 *
 * Manages tournament phases and their transitions including:
 * - Phase definitions (registration, group stage, knockout, etc.)
 * - Phase progression logic
 * - Automatic and manual phase transitions
 * - Phase-specific business rules
 *
 * This submodule handles the lifecycle management of tournament phases
 * and ensures proper sequencing of tournament events.
 */
