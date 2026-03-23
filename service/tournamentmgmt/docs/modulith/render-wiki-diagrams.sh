#!/usr/bin/env bash
set -euo pipefail

repo_root=$(git rev-parse --show-toplevel)
source_dir="$repo_root/service/tournamentmgmt/docs/modulith/generated"
output_dir="$repo_root/wiki/Architecture/generated/tournamentmgmt-png"
host_repo_root="$repo_root"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required to render PlantUML diagrams for the wiki" >&2
  exit 1
fi

if [[ ! -d "$source_dir" ]]; then
  echo "missing source directory: $source_dir" >&2
  echo "generate the Modulith source files first:" >&2
  echo "  cd service/tournamentmgmt && ./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests" >&2
  exit 1
fi

if [[ -f /.dockerenv ]]; then
  mount_source=$(
    docker inspect "$(hostname)" \
      --format "{{range .Mounts}}{{if eq .Destination \"$repo_root\"}}{{.Source}}{{end}}{{end}}" \
      2>/dev/null || true
  )

  if [[ -n "$mount_source" ]]; then
    host_repo_root="$mount_source"
  fi
fi

mkdir -p "$output_dir"
find "$output_dir" -maxdepth 1 -type f -name '*.png' -delete

docker run --rm \
  --user "$(id -u):$(id -g)" \
  --entrypoint /bin/sh \
  -v "$host_repo_root:/workspace" \
  -w /workspace \
  plantuml/plantuml \
  -lc '
    set -eu
    mkdir -p wiki/Architecture/generated/tournamentmgmt-png
    for file in service/tournamentmgmt/docs/modulith/generated/*.puml; do
      java -jar /opt/plantuml.jar -tpng "$file"
      png="${file%.puml}.png"
      mv "$png" "wiki/Architecture/generated/tournamentmgmt-png/$(basename "${file%.puml}").png"
    done
  '

echo "Rendered PNG diagrams to wiki/Architecture/generated/tournamentmgmt-png/" >&2
