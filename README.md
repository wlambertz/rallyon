# BadTourn

BadTourn – Das smarte System für Badmintonturniere

## Projektstruktur

```text
badtourn/
  application/                 App-Bootstrap, Zusammensetzung der Module (Composition Root)
    organizer/                 Platzhalter: Oberfläche/Workflows für Organisator:innen (Frontend-Stack noch offen)
    audience/                  Öffentliche Ansichten für Zuschauer:innen ("Turnier-TV")
  service/
    player_mgmt/               Spielerverwaltung (PlayerManagement)
    scoring/                   Ergebnisdienst (ScoringService)
    tournament_mgmt/           Turnierverwaltung (TournamentManagement)
  3rd_party/
    authentication/            Authentifizierung/Identität (Integration)
    event_bus/                 Event-Bus/Integration
    search_engine/             Suche/Indexierung
  docs/wiki/                    Wiki content (architecture, CLI workflows, personas, design context)
  build.gradle                 Gradle Buildskript (Root)
  settings.gradle              Gradle Settings
  gradlew, gradlew.bat         Gradle Wrapper (Unix/Windows)
  gradle/wrapper/              Wrapper-Konfiguration
```

- Die fachlichen Module und ihre Verantwortlichkeiten sind im Wiki beschrieben: `docs/wiki/Architecture/Modules.md`.
- Hohe Ebene der Module (Auszug):
  - Authentifizierung & Autorisierung: Benutzer, Rollen, Rechte.
  - Turnierverwaltung: Planung/Konfiguration/Spielpläne.
  - Spielerverwaltung: Registrierung und Pflege von Spielern/Teams.
  - Ergebnisdienst: Erfassung/Berechnung von Ergebnissen und Ranglisten.
- Öffentliche Informationen: Lesemodelle/Ansichten für Spieler/Zuschauer.

## Organizer-Portal (Platzhalter)

`application/organizer/` ist aktuell ein leerer Platzhalter. Die vorherige Angular-Implementierung wurde entfernt, während der Frontend-Stack neu bewertet wird. Es gibt derzeit keine Root-NPM-Skripte oder `ro`-CLI-Wrapper für diesen Bereich. Details stehen in `application/organizer/AGENTS.md`.

## Dev Container

Für eine konsistente lokale Umgebung gibt es eine Dev-Container-Konfiguration in `.devcontainer/`. Der Container enthält Java 25, Node.js 20, Go 1.25, Docker CLI mit Host-Docker-Zugriff, GitHub CLI (`gh`), Google Chrome für `ChromeHeadless`/Playwright, den PostgreSQL-Client sowie `@openai/codex` als globales npm-Paket.

- Voraussetzung auf dem Host: Docker bzw. Docker Desktop mit verfügbarem `/var/run/docker.sock`.
- Beim ersten Erstellen des Containers werden Root-NPM-Abhängigkeiten installiert, die lokalen IAM- Maven-Module gebaut und die Go-CLI `ro` nach `bin/ro` kompiliert.
- Infrastruktur bleibt explizit manuell startbar, damit der Container schnell und reproduzierbar hochfährt.

Typische Befehle im Container:

```bash
ro doctor
ro run service tournamentmgmt --env local --port 8080
docker compose -f infrastructure/local/docker-compose.yml up -d keycloak tournamentmgmt-db
```

## API-Dokumentation

- **Swagger UI**: [Swagger UI (localhost)](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)
- **OpenAPI YAML**: [`http://localhost:8080/v3/api-docs.yaml`](http://localhost:8080/v3/api-docs.yaml)

Hinweis: Wenn ein `server.servlet.context-path` konfiguriert ist, wird dieser den Pfaden vorangestellt.

## Wiki-Inhalt

Das Wiki wird im Ordner `docs/wiki/` im Haupt-Repo vorgehalten (kein separates Submodul mehr).

- Initiales Klonen: `git clone https://github.com/wlambertz/rallyon.git` (enthält bereits `docs/wiki/`)
- Falls bereits geklont und das Wiki fehlt: `docs/wiki/` ist Teil des Haupt-Repos, kein `git submodule update` nötig.
- Änderungen am Wiki: Bearbeiten Sie die Dateien unter `docs/wiki/` und committen Sie sie zusammen mit dem Code.

### Hinweis: Früheres Wiki-Submodul

Früher wurde das GitHub-Wiki als Submodul im Ordner `wiki/` eingebunden. Das Submodul wurde entfernt und der Inhalt in `docs/wiki/` überführt.

## Windows: Zeilenenden (CRLF/LF)

Wenn auf Windows gearbeitet wird, kann Git beim Stagen/Committen Zeilenenden konvertieren. Um Warnungen zu vermeiden und konsistent zu bleiben:

- Empfohlene Git-Einstellung (global):

  ```bash
  git config --global core.autocrlf true
  ```

- Falls es nachträglich zu Mischungen kam, Inhalte einmalig normalisieren:

  ```bash
  git add --renormalize .
  git commit -m "Normalize line endings"
  ```

Optional kann eine `.gitattributes` mit Standard-Textbehandlung helfen:

```gitattributes
* text=auto
```
