#!/usr/bin/env bash
set -euo pipefail

repo_root="/workspaces/rallyon"

cd "$repo_root"

npm ci
npm ci --prefix application/organizer

sh ./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install

mkdir -p bin
(
  cd tools/cli/ro
  go build -o "$repo_root/bin/ro" .
)
