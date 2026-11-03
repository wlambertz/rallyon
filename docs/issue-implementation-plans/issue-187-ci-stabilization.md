# Issue #187: NVD als Hauptquelle behalten

## Zusammenfassung und Freigabegrundlage

Diese vom Maintainer ausdrücklich zur Umsetzung beauftragte Revision ersetzt den BSI-Ersatzplan aus Commit `0d22186`. NVD bleibt die Hauptquelle des automatischen Dependency-Scans. BSI-/CERT-Bund-Meldungen dienen ergänzend zur manuellen Bewertung; es entstehen keine BSI-Scan-Engine und kein zusätzlicher CI-Job. Hersteller-Advisories bleiben Belege für betroffene und korrigierte Versionen.

Die Lieferung bleibt bei zwei aufeinanderfolgenden PRs: zuerst Dependencies/Security/SBOM in PR #201, anschließend die übrigen Workflows. #187 bleibt bis zum Abschluss beider Lieferungen offen. Die tatsächlichen Fehlernachweise und bisherigen Genehmigungen bleiben historisch erhalten; die betroffenen Lifecycle-Nachweise werden erneuert. G8-Abnahme und Merge bleiben getrennte menschliche Entscheidungen.

## PR 1: gezielte Korrektur des Datenabrufs

- Die bereits implementierten Upgrades auf Boot 4.1.2 im Service/IAM, Kotlin 2.4.21 und Swagger UI 5.34.1, JWT-Regressionstests, eng befristeten Fehlzuordnungsregeln und unabhängigen Scan-/SBOM-Jobs erhalten.
- Dependency-Check 13.0.0 mit dem bereits verifizierten OCI-Index-Digest `sha256:0e8f69ad5b00d62000937f4e57189812343f6903d9c83ee712012d51542a020a` beibehalten.
- Ohne vertrauenswürdig bereitgestellten NVD-Schlüssel den offiziell unterstützten direkten NIST-Datenfeed verwenden: `--nvdDatafeed https://nvd.nist.gov/feeds/json/cve/2.0/nvdcve-2.0-{0}.json.gz`.
- Mit optionalem Schlüssel die bisherige NVD-API verwenden. Schlüssel und gemeinsame Cache-Schreibzugriffe bleiben ausschließlich Default-Branch-Push-/Schedule-Läufen vorbehalten. PRs nutzen den schlüssellosen Feed.
- Vor jedem schlüssellosen Lauf die offiziellen `https://nvd.nist.gov/feeds/json/cve/2.0/nvdcve-2.0-modified.meta`-Daten abrufen. Fehlende oder ungültige Zeitstempel, ein Alter über 48 Stunden oder eine Datierung mehr als fünf Minuten in die Zukunft blockieren. Abrufe erhalten drei Versuche mit jeweils höchstens 20 Sekunden Timeout.
- Aktive Scanner-Updates und das 60-Minuten-Joblimit erhalten. Fehler bleiben sichtbar und blockierend; kein automatischer Wechsel zwischen API und Feed nach einem Fehler und kein Offline-Erfolgsfallback.
- Datenquellenmodus, Metadaten-Zeitstempel und Quellenhash in den Ausführungsnachweis aufnehmen. Bestehende Helper-Befehle sowie REST-, JWT-, Datenbank- und `ro`-Schnittstellen bleiben unverändert.
- CVSS ≥ 7 bleibt blockierend. Runtime-JARs und eingebettetes JavaScript bleiben geprüft. HTML-/JSON-Diagnosen und die unabhängig erzeugte CycloneDX-SBOM werden auch bei roten Befunden bereitgestellt.

## Dokumentation, Lifecycle und weitere Lieferung

- Diesen Plan auf #187 veröffentlichen und separat committen. OpenSpec-Design, betroffene Szenarien und Aufgaben konkret aktualisieren. Die ursprünglichen Fehlernachweise und bisherigen Genehmigungen historisch erhalten.
- Die betroffenen G2–G4-Nachweise anhand des ausdrücklich beauftragten Plans erneuern; bestehendes Umsetzungsticket #200 mit unverändertem genehmigtem Issue-Payload erhalten und keine doppelten Issues erstellen. Danach Implementierung, unabhängige G5/G6-Prüfung und drei G7-Review-Linsen abschließen.
- G8 umfasst einen nachvollziehbaren Bericht-/CI-Walkthrough. Abnahme und Merge bleiben getrennte menschliche Entscheidungen. Ein Planauftrag ersetzt diese Entscheidungen nicht.
- PR 2 behält den bisherigen Umfang und die unten festgelegten Toolchain-Versionen/Plattformziele. Nach beiden genehmigten Merges den tatsächlichen `main` einschließlich Docker beobachten, Checklisten abschließen und erst dann #187 schließen.

## Zusätzliche Validierung für den Datenabruf

- Regressionen für Feed-/API-Auswahl, PR-Vertrauensgrenzen, fehlenden Schlüssel und unveränderte Scanner-Argumente ergänzen.
- Metadatenfälle testen: gültig, exakt 48 Stunden alt, zu alt, ungültig, fehlend, zukünftiger Zeitstempel sowie HTTP-/Timeout-Fehler. Uhr und Netzwerk in Tests faken.
- Echte schlüssellose Kalt- und Warmläufe durchführen. Quellenfrische, Input-Hashes, Scanner-Version, Laufzeit, Reports und Exitcodes dokumentieren. Ein unvollständig initialisierter Cache zählt nicht als erfolgreicher Warmlauf.
- Blockierende Befunde und Datenfehler simulieren: Scan bleibt rot, verfügbare Berichte und SBOM bleiben erhalten. Schlüsselbereinigung, Ausnahmeablauf und bisherige Security-Regressionen erneut prüfen.
- Keine neuen Pflicht-Secrets, keine zusätzliche Produktionsinfrastruktur und keine neuen Bibliotheken für den Metadatenabruf. Keine Live-Releases, Test-Tags, Registry-Pushes oder Deployments zur Validierung.

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
