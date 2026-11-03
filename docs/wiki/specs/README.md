# Legacy specification directory

The nine former project YAML specifications were migrated to [current OpenSpec specifications](../../../openspec/specs/). See the [migration inventory](../../legacy-spec-migration.md) for the source-to-requirement mapping and corrected assumptions.

This directory remains only because deprecated `ro create` and `ro spec create` use it as their historical default output directory. Both commands still accept caller-owned legacy files and emit a migration notice; this README preserves that directory in a fresh checkout. Do not add new project specifications here. Use OpenSpec 1.14.2 and the [shared instructions](../../../.agents/skills/openspec/SKILL.md).
