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

Die Java- und Kotlin-Buildziele sowie die gemeinsamen IAM-Module verwenden Java 25. Das reduziert Abweichungen zwischen Entwicklung, CI und der bestehenden Java-25-Service-Laufzeit; Verbraucher der IAM-Artefakte benötigen dadurch mindestens Java 25.

- Voraussetzung auf dem Host: Docker bzw. Docker Desktop mit verfügbarem `/var/run/docker.sock`.
- Beim ersten Erstellen des Containers werden Root-NPM-Abhängigkeiten installiert, die lokalen IAM- Maven-Module gebaut und die Go-CLI `ro` nach `bin/ro` kompiliert.
- Infrastruktur bleibt explizit manuell startbar, damit der Container schnell und reproduzierbar hochfährt.

Typische Befehle im Container:

```bash
ro doctor
ro run service tournamentmgmt --env local --port 8080
docker compose -f infrastructure/local/docker-compose.yml up -d keycloak tournamentmgmt-db
```

Die lokale PostgreSQL-18-Konfiguration behält das bestehende Daten-Volume bei. Daten aus einer älteren PostgreSQL-Hauptversion müssen vor dem Start ausdrücklich migriert werden; ein Image-Update allein führt kein Datenbank-Upgrade durch.

Der Turnierverwaltungsservice führt beim Start seine vorhandenen Flyway-Migrationen aus. Für eine bereits befüllte Datenbank ohne passende Flyway-Historie muss der Schema- und Migrationsstand vor dem Upgrade geprüft werden; Flyway bricht den Start bei Konflikten ab. Vor Änderungen an bestehenden Datenbanken ein Backup erstellen und keine automatische Baseline ohne Prüfung setzen.

Der PostgreSQL-Regressionstest läuft in der Java-CI gegen eine eigene leere Datenbank. Lokal wird er ausgeführt, wenn `RALLYON_FLYWAY_TEST_URL`, `RALLYON_FLYWAY_TEST_USER`, `RALLYON_FLYWAY_TEST_PASSWORD` und `RALLYON_FLYWAY_TEST_ALLOW_MUTATION=yes` gesetzt sind. Dafür ausschließlich eine dedizierte leere Testdatenbank verwenden; der Test legt dort Schemas und Tabellen an.

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

## Lizenz

Der eigene RallyOn-Code einschließlich der `ro`-CLI steht unter der [MIT-Lizenz](LICENSE). Sie erlaubt die Nutzung, Änderung und Weitergabe auch in kommerziellen und geschlossenen Produkten, sofern der Copyright- und Lizenzhinweis erhalten bleibt. Das unterstützt den Lern- und Austauschgedanken des Projekts.

Fremde Bestandteile behalten ihre jeweiligen Lizenzen; vorhandene Lizenz- und Copyright-Hinweise gelten weiterhin.
