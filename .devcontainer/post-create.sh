#!/usr/bin/env bash
set -euo pipefail

repo_root="/workspaces/rallyon"

cd "$repo_root"

npm ci

sh ./service/tournamentmgmt/mvnw -B -f 3rd_party/iam/pom.xml install

mkdir -p bin
(
  cd tools/cli/ro
  go build -o "$repo_root/bin/ro" .
)
