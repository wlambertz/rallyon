--- 
name: openspec 
description: OpenSpec spec framework integration for RallyOn - spec creation, validation, and AI agent orchestration. Use for adding OpenSpec spec file support, ro CLI commands for spec management, and integration with AI agent workflows. Preserve compatibility with existing sdlc-skills, ro-cli, and keycloak-auth workflows. 
--- 

# OpenSpec Skill

Use this skill for OpenSpec spec framework integration in the RallyOn monorepo. OpenSpec provides a lightweight, configurable format for defining software specifications as hierarchical tickets (Specification → Epic → Ticket) with embedded acceptance criteria, test specifications, implementation steps, and code references.

## Read before editing

1. [ openspec.dev ](https://openspec.dev/) - Official OpenSpec website and npm package
2. [ openspec.tech ](https://openspec.tech/) - Open Standard for AI Agent Orchestration v1.1
3. [ Schema URL ](https://openspec.tech/schema/v1.1/openspec-schema.json) - Permanent schema for validation
4. [ RallyOn AGENTS.md ](./AGENTS.md) - General project conventions and boundaries
5. [ sdlc-skills SKILL.md](../sdlc-skills/SKILL.md) - SDLC practices that OpenSpec complements
6. [ ro-cli SKILL.md](../ro-cli/SKILL.md) - CLI command conventions to follow
7. [ keycloak-auth SKILL.md](../keycloak-auth/SKILL.md) - Auth-related specs can define JWT requirements, role mappings, etc.

## Local bootstrap flow

For local OpenSpec workflow:

1. Install OpenSpec CLI: `npm install -g @fission-ai/openspec@latest`
2. Validate a spec: `openspec validate ./my-spec.yaml --schema https://openspec.tech/schema/v1.1/openspec-schema.json`
3. Create a spec from template: `ro spec create --template basic --output ./specs/my-feature.yaml`
4. Check integration with Keycloak: specs can define auth requirements referenced by keycloak-auth skill

## Safety rules

Follow root `AGENTS.md` Safety Boundaries and `3rd_party/iam/AGENTS.md` Safety Boundaries:

- Do not relax spec validation requirements; OpenSpec schema validation is strict by design
- Preserve `openspec.*` property names where they intersect with existing `rallyon.*` property names
- Do not silently change failures for missing fields or invalid specs; keep validation errors visible
- Keep OpenSpec format reusable across services; avoid service-specific extensions unless explicitly needed
- When OpenSpec tickets reference Keycloak auth, preserve `rallyon.security.keycloak.*` property names and `rallyon_user_id` claim handling
- Treat OpenSpec specs as versioned artifacts - do not assume backward compatibility without explicit migration plan

## Local development

```
# Install OpenSpec CLI
npm install -g @fission-ai/openspec@latest

# Validate all specs in the repo
openspec validate ./docs/wiki/ --schema https://openspec.tech/schema/v1.1/openspec-schema.json

# Create a new spec from template
ro spec create --template basic --output ./docs/wiki/my-feature-spec.yaml

# View spec contents
ro spec view ./docs/wiki/my-feature-spec.yaml
```

## Validation

- OpenSpec schema validation: `openspec validate` against `https://openspec.tech/schema/v1.1/openspec-schema.json`
- Ensure specs are well-formed YAML/JSON/TOON
- Verify acceptance criteria are present and testable
- Check that code references and type references are accurate