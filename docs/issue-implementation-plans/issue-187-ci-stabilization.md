# Issue #187: CI vollständig stabilisieren und härten

## Ziel und Lieferung

Alle bestehenden Workflows überarbeiten und echte Dependency-Probleme beheben, ohne Sicherheitsprüfungen abzuschwächen. Lieferung in **zwei aufeinanderfolgenden PRs**:

1. Dependencies, Security-Scan und SBOM.
2. Java-/Go-CI, Formatierung/Sonar, Commit-Lint, Lifecycle, Docker, CodeQL und Release.

#187 bleibt bis zum Abschluss beider PRs offen. Zu Beginn den Plan unter `docs/issue-implementation-plans/issue-187-ci-stabilization.md` speichern, auf #187 kommentieren und separat committen. Danach auf isolierten Branches von aktuellem `main` arbeiten.

## PR 1: Dependencies und Security/SBOM

- Den aktuellen Report als Ausgangspunkt dokumentieren: Paketidentität, erkannte CVEs, Herstellerquelle, betroffene Versionen und Maßnahme pro Befund.
- Spring Boot im Service und IAM-Starter gemeinsam auf **4.1.2** aktualisieren. Dessen BOM liefert unter anderem Spring Framework 7.0.10, Tomcat 11.0.27 und PostgreSQL JDBC 42.7.14. [Offizielle Versionsverwaltung](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html).
- Kotlin-Compiler, Laufzeit und Compiler-Plugins gemeinsam auf **2.4.21** aktualisieren. Swagger UI gezielt auf **5.34.1** verwalten, einschließlich des korrigierten eingebetteten DOMPurify. Keine Spring-Boot-Hauptversion oder Auth-Policy ändern.
- In `.github/workflows/security-scan.yaml` den intern verwendeten `latest`-Container durch Dependency-Check **13.0.0 mit verifiziertem Image-Digest** ersetzen. Aufgelöste Runtime-JARs einschließlich eingebetteter JavaScript-Ressourcen weiter scannen.
- **CVSS ≥ 7 bleibt blockierend.** Nur belegte Fehlzuordnungen unterdrücken, jeweils eng auf Paket und falschen CPE/CVE begrenzt, mit Begründung, Herstellerbeleg und maximal 90 Tagen Laufzeit. Keine pauschalen Bibliotheks-, Analyzer- oder Schwellenwert-Ausnahmen.
- Scanner-Daten zwischen Läufen cachen; Cache-Schreibzugriffe nur aus vertrauenswürdigen Läufen. Updates bleiben aktiviert. Optionales `NVD_API_KEY` unterstützen, aber keinen neuen Pflicht-Secret voraussetzen. Netzwerk-, Feed- und Scannerfehler dürfen keinen grünen Scan erzeugen.
- SBOM unabhängig vom Scan-Ergebnis erzeugen. Beide Jobs benötigen eine erfolgreiche Dependency-Auflösung; Scanfehler blockieren weiterhin die Gesamtprüfung. HTML-/JSON-Berichte und CycloneDX-SBOM auch bei roten Befunden bereitstellen.

## PR 2: Alle übrigen Workflows

- Bestehende Workflow-Dateinamen und verwendete Check-Namen erhalten. Actions per vollständigem Commit-SHA pinnen; explizite Timeouts, minimale Job-Berechtigungen und passende Parallelitätsregeln ergänzen. Veröffentlichungen nicht während eines laufenden Uploads abbrechen.
- Toolchains vereinheitlichen: Temurin Java 21, Node **24.22.2**, Go **1.26.10**, Python 3.12 und OpenSpec **1.14.2**. Das bestehende Go-Modul-Minimum und die Java-25-Containerlaufzeit bleiben unverändert.
- Java-CI installiert zuerst IAM und führt `clean verify` aus. Go-Tests zusätzlich auf PRs und `main` ausführen. Formatierungs-/Lifecycle-Prüfungen ohne Secrets betreiben.
- Sonar nur in vertrauenswürdigen Läufen mit vorhandenem Token ausführen. Fork-PRs behalten Build-, Test- und Formatierungsprüfungen; übersprungene Analyse ausdrücklich anzeigen. Kein `pull_request_target` zum Ausführen fremden Codes mit Secrets verwenden.
- CodeQL-Matrix auf **Actions, Java/Kotlin und Go** erweitern. Java/Kotlin erhält einen expliziten IAM-/Service-Build; Go wird aus seinem Modul gebaut.
- Docker auf PRs bauen, jedoch ohne Registry-Login oder Push. Bestehende Veröffentlichungen auf `main` und manuelle Ausführung erhalten.
- GoReleaser auf **2.19.0** pinnen, Konfiguration und Archivpfade korrigieren. Die sechs bisherigen Plattform-/Architektur-Kombinationen sowie README, Lizenz, CLI-Referenz, Konfiguration und Checksummen erhalten.
- `ro/v*`-Tags und Draft-Releases erhalten, ohne kostenpflichtige Monorepo-Funktionen vorauszusetzen: GoReleaser erstellt die Pakete ohne Veröffentlichung; ein getesteter Wrapper leitet die Version aus dem geprüften Tag ab und liefert die Artefakte per GitHub CLI an dessen Draft-Release. Veröffentlichte Releases niemals überschreiben.

## Validierung und Abnahme

- IAM installieren und testen; Backend `clean verify`, vollständige Go-Tests, Lifecycle-/Hook-/Readiness-Suites, OpenSpec-Validierung, Formatierung und `git diff --check` ausführen.
- Alle Workflows mit gepinntem Actionlint prüfen. Regressionen für Trigger, Berechtigungen, Fork-/Secret-Behandlung, Publikationsbedingungen und Fehlerweitergabe abdecken.
- Security-Scan mit kaltem und warmem Cache prüfen. Abgelaufene Suppressions müssen fehlschlagen; ein simuliertes blockierendes Scan-Ergebnis muss rot bleiben, während Berichte und SBOM verfügbar werden.
- Release-Pakete ohne Veröffentlichung bauen und Archivinhalt, Version und Checksummen prüfen. GitHub-Aufrufe mit Fakes testen, einschließlich ungültiger Tags und bereits vorhandener Releases. Keine Test-Tags, echten Releases oder Registry-Pushes zur Validierung erzeugen.
- Jeden PR unabhängig prüfen und seine tatsächlichen CI-Ergebnisse beobachten. Echte verbleibende blockierende Schwachstellen verhindern die Abnahme; sie werden nicht als „CI-Flake“ ausgeblendet.

## Abschluss und Grenzen

REST-, JWT-, Datenbank- und `ro`-Verträge erhalten. Lokale Bootstrap-/Flyway-Probleme und Produktfeatures bleiben außerhalb dieses Issues.

Wegen des breiten Umfangs und der IAM-/Security-Dependencies den vollständigen Lifecycle verwenden; menschliche Abnahme und Merge bleiben explizite Entscheidungen. Nach beiden genehmigten Merges den grünen `main` einschließlich Docker-Workflow beobachten, Dokumentation und Issue-Checkliste abschließen, #187 schließen und den lokalen `main` aktualisieren.
