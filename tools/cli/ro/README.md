# RallyOn Developer CLI (ro)

## Build

- Windows PowerShell

```PowerShell
cd tools\cli\ro
New-Item -ItemType Directory -Force ..\..\bin | Out-Null
go mod tidy
go build -o ..\..\bin\ro.exe .  # -o sets output path, trailing dot builds current module
```

- WSL (Ubuntu/Debian)

```bash
cd /mnt/c/Users/wla_edu/Documents/GitHub/rallyon/tools/cli/ro
mkdir -p ../../bin
go mod tidy
go build -o ../../bin/ro .  # -o sets output path, trailing dot builds current module
```

- WSL Java setup

```bash
# install or point to a JDK that Maven can see
# example: use Windows JDK from WSL (quotes handle spaces)
export JAVA_HOME="/mnt/c/Users/wla_edu/tools/jdk-25"
export PATH="$JAVA_HOME/bin:$PATH"

# or, if you installed a Linux JDK in WSL (Temurin via APT)
# sudo apt update && sudo apt install -y temurin-17-jdk temurin-25-jdk
export JAVA_HOME="/usr/lib/jvm/temurin-25-jdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"

# add the export once to ~/.bashrc to persist (idempotent append)
if ! grep -q 'temurin-25-jdk-amd64' ~/.bashrc; then
  cat >> ~/.bashrc <<'EOF'
export JAVA_HOME="/usr/lib/jvm/temurin-25-jdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"
EOF
fi

# reload shell after editing ~/.bashrc
source ~/.bashrc
```

## Run from any folder

- Windows

```PowerShell
# run once per shell to try it out
$env:Path = "C:\Users\wla_edu\Documents\GitHub\rallyon\tools\bin;" + $env:Path

# to persist across sessions add the path to your profile (PowerShell 7+)
Add-Content -Path $PROFILE -Value '$env:Path = "C:\Users\wla_edu\Documents\GitHub\rallyon\tools\bin;" + $env:Path'
```

- WSL / Linux

```bash
# ensure a per-user bin dir exists and is ahead of Windows paths
mkdir -p "$HOME/bin"
ln -sf /mnt/c/Users/wla_edu/Documents/GitHub/rallyon/tools/bin/ro "$HOME/bin/ro"
grep -q 'export PATH="$HOME/bin:$PATH"' ~/.bashrc || echo 'export PATH="$HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

# verify WSL resolves the Linux binary (ELF) not ro.exe
which ro
file "$(which ro)"
```

## Usage

- Show help

```sh
ro --help
```

- Config

```sh
ro config show --json
```

- Local auth token for Swagger UI

```sh
RALLYON_CLIENT_SECRET=super-secret ro auth token --format bearer
RALLYON_CLIENT_SECRET=super-secret ro auth token
```

If Keycloak is not reachable at `http://localhost:8081` from your shell environment, pass `--server` or set `KEYCLOAK_SERVER`.

- Build / Test / Run

```sh
ro build [--fast|--ci]
ro test
ro run service tournamentmgmt --env dev --port 8080
```

- Docker

```sh
ro docker build --branch-tag --sha-tag --push
ro docker build --tag release --latest
ro docker compose up --profile dev
ro docker compose down --profile dev
ro docker compose logs api
```

- Git helpers

```shell
ro git status
ro git branch --verbose
ro git rebase --onto origin/main
ro git commit --type feat --summary "add feature"
ro git push --force
ro git sync --remote origin --branch main
```

- Deploy

```shell
# requires GITHUB_TOKEN in env
ro deploy --env dev --dry-run   # safe preview
ro deploy --env prod --yes --wait=false --input release=1.2.3
ro deploy --env dev --check-only --json  # CI preflight
```

- Deploy defaults live under `deploy.*` in `ro.yaml` (repo, workflow slug, ref mapping, safety gates). Override with `RO_DEPLOY_*` env vars when needed.
- Docs

```shell
ro docs generate --wiki                     # writes docs/cli-reference.md (+ wiki/CLI.md)
ro docs generate --wiki --commit-wiki       # also commits wiki (docs.wikiCommitMessage)
ro docs generate --wiki --commit-wiki --commit-message "docs: refresh CLI"
```

- Scaffold

```shell
ro scaffold module registration
ro scaffold module scheduling --package com.rallyon.tournament.scheduling
ro scaffold module scoring --dry-run
ro scaffold module messaging --template-set adapter --force
ro scaffold module catalog --template-set module --base service/tournamentmgmt/src/main/java
```

- Version

```shell
ro version
```

- Telemetry / Diagnostics

```shell
ro telemetry status
RO_TELEMETRY_ENABLED=true ro deploy --env dev  # one-off opt-in
ro doctor   # checks Go/Java/Docker/Git state
```

- The `ro auth token` helper is intended for local development only. It wraps the existing Keycloak dev realm/client/user flow and prints a bearer token for Swagger UI or manual API testing without storing credentials.

- Packaging / Release

```shell
cd tools/cli/ro
goreleaser build --snapshot --clean  # local multi-platform artifacts (dist/)
goreleaser release --clean --skip=publish  # dry-run release
```

CI publishes tagged releases via `.github/workflows/ro-release.yml` (tags `ro/v*`).

## Shell completion

Cobra provides the `ro completion` command. See `ro completion --help` and follow your shell instructions.
