#!/usr/bin/env bash
set -euo pipefail

cat <<'EOF'
Dev container ready.

Common commands:
  ro doctor
  ro run service tournamentmgmt --env local --port 8080
  docker compose -f infrastructure/local/docker-compose.yml up -d keycloak tournamentmgmt-db
EOF
