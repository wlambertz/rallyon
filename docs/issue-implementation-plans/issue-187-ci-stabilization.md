# Issue #187: CI vollständig stabilisieren und härten

## Planrevision: BSI als Ersatz für die NVD-basierte Prüfung

Stand 2026-11-03, auf ausdrücklichen Wunsch des Maintainers. Diese Revision plant den Ersatz der NVD-basierten Schwachstellenprüfung durch öffentliche BSI-/CERT-Bund-CSAF-Daten. Sie ist **keine Umsetzungs-, G8- oder Merge-Freigabe**. Die zuvor vorgeschlagene direkte NIST-Feed-Anpassung wurde nicht bestätigt und wird nicht umgesetzt. Bestehende Genehmigungen betreffen den alten Entwurf; vor einer veränderten Umsetzung sind die betroffenen Lifecycle-Gates erneut vorzubereiten.

Der Zwischenstand liegt in [PR #201](https://github.com/wlambertz/rallyon/pull/201); dessen tatsächlicher Dependency-Check-13.0.0-Kaltstart scheitert am bekannten schlüssellosen NVD-Abruf. Dieser Fehler bleibt sichtbar. Dependency-Upgrades, unabhängige SBOM-Erzeugung und die Arbeit an PR 2 bleiben Bestandteile von #187. Der lokale `main` bleibt unverändert.

## Ziel und Lieferung

Alle bestehenden Workflows überarbeiten und echte Dependency-Probleme beheben, ohne Sicherheitsprüfungen abzuschwächen. Lieferung in **zwei aufeinanderfolgenden PRs**:

1. Dependencies, Security-Scan und SBOM.
2. Java-/Go-CI, Formatierung/Sonar, Commit-Lint, Lifecycle, Docker, CodeQL und Release.

#187 bleibt bis zum Abschluss beider PRs offen. Zu Beginn den Plan unter `docs/issue-implementation-plans/issue-187-ci-stabilization.md` speichern, auf #187 kommentieren und separat committen. Danach auf isolierten Branches von aktuellem `main` arbeiten.

## PR 1: Dependencies und Security/SBOM

- Den aktuellen Report als Ausgangspunkt dokumentieren: Paketidentität, erkannte CVEs, Herstellerquelle, betroffene Versionen und Maßnahme pro Befund.
- Spring Boot im Service und IAM-Starter gemeinsam auf **4.1.2** aktualisieren. Dessen BOM liefert unter anderem Spring Framework 7.0.10, Tomcat 11.0.27 und PostgreSQL JDBC 42.7.14. [Offizielle Versionsverwaltung](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html).
- Kotlin-Compiler, Laufzeit und Compiler-Plugins gemeinsam auf **2.4.21** aktualisieren. Swagger UI gezielt auf **5.34.1** verwalten, einschließlich des korrigierten eingebetteten DOMPurify. Keine Spring-Boot-Hauptversion oder Auth-Policy ändern.
- In `.github/workflows/security-scan.yaml` die bisherige NVD-basierte Vulnerability-Auswertung nach dem unten beschriebenen Nachweis durch eine BSI-CSAF-Auswertung ersetzen. Der bereits gepinnte Dependency-Check 13.0.0 ist der dokumentierte Zwischenstand, nicht die vorausgesetzte BSI-Engine. Eine BSI-URL wird nicht als NVD-Feed ausgegeben. Aufgelöste Runtime-JARs einschließlich eingebetteter JavaScript-Ressourcen bleiben im Inventar und in der Prüfung.
- **CVSS ≥ 7 bleibt blockierend.** Nur belegte Fehlzuordnungen unterdrücken, jeweils eng auf Paket und falschen CPE/CVE begrenzt, mit Begründung, Herstellerbeleg und maximal 90 Tagen Laufzeit. Keine pauschalen Bibliotheks-, Analyzer- oder Schwellenwert-Ausnahmen.
- Validierte BSI-Quelldaten und normalisierte Daten getrennt versioniert cachen; Cache-Schreibzugriffe nur aus vertrauenswürdigen Läufen. Updates bleiben aktiviert. Der vollständige BSI-Ersatz benötigt keinen NVD-Schlüssel und keinen neuen Pflicht-Secret. Netzwerk-, Feed-, Signatur-, Zuordnungs- und Auswertungsfehler dürfen keinen grünen Scan erzeugen. Kein stiller Rückfall auf NVD oder andere Datenanbieter.
- SBOM unabhängig vom Scan-Ergebnis erzeugen. Beide Jobs benötigen eine erfolgreiche Dependency-Auflösung; Scanfehler blockieren weiterhin die Gesamtprüfung. HTML-/JSON-Berichte und CycloneDX-SBOM auch bei roten Befunden bereitstellen.

## PR 1: Umsetzungsplan für den BSI-Ersatz

### 1. Machbarkeit und Abdeckung vor dem Wechsel belegen

- Die offiziellen [BSI-Provider-Metadaten](https://wid.cert-bund.de/.well-known/csaf/provider-metadata.json) als Einstieg verwenden. Daraus öffentliche WHITE/CLEAR-Distributionen entdecken, zunächst WID und anschließend die übrigen öffentlichen BSI-/CVD-Verteilungen abgleichen und Duplikate nach Herausgeber, Dokument-ID und Revision zusammenführen. GREEN- oder andere zugriffsbeschränkte Dokumente nicht abrufen oder in öffentliche CI-Artefakte übernehmen.
- Ein reproduzierbares, mit Zeitstempel und SHA-256 versehenes Quelleninventar erstellen: Metadaten, ROLIE-Index, Advisory-Revisionen, Quellen-URLs, Produktkennungen, Versionsangaben, Status und verfügbare Scores. Anbieter-Updatezeit und tatsächlichen Abrufzeitpunkt getrennt dokumentieren; unveränderte ältere Advisories sind nicht allein wegen ihres Veröffentlichungsdatums veraltet.
- Für alle **145 aktuellen Runtime-Koordinaten**, die eingebetteten JavaScript-Komponenten sowie alle **13 Baseline-Einträge / 82 Instanzen / 64 IDs** eine Abdeckungsmatrix liefern. Eine im Index gefundene CVE ist noch kein Nachweis korrekter Paket-/Versionszuordnung. Echte betroffene Fälle und die sieben belegten Fehlzuordnungen getrennt prüfen.
- Vor dem Umsetzen eines eigenen Matchers vorhandene BSI-/CSAF-Werkzeuge anhand dieser Matrix erproben. Toolauswahl, erforderliche neue CI-Abhängigkeiten, Lizenz, Version/Digest und Wartungsaufwand als konkrete Designentscheidung vorlegen. Ein ständig laufender zusätzlicher Dienst ist nicht vorausgesetzt. Ein umfangreicher eigener allgemeiner SCA-Scanner wäre eine gesonderte Architekturentscheidung.
- Ergebnis ist ein überprüfbares Go/No-Go für einen **vollständigen** BSI-Ersatz. Nicht abgedeckte kritische Komponenten, fehlende notwendige Versionsangaben oder Scores verhindern den Wechsel. Falls BSI allein nicht reicht, diese Grenzen präsentieren; Herstellerdaten oder weitere Quellen werden nur nach einer neuen ausdrücklichen Entscheidung ergänzt. Kein automatischer NVD-Fallback und keine vorgetäuschte Gleichwertigkeit.

### 2. BSI-Adapter und fachliche Auswertung klar trennen

Vorgesehene Ablage bei Freigabe: `tools/scripts/` für ausführbare CI-Werkzeuge, `tools/scripts/tests/` für isolierte Regressionen, `docs/security/` für Betrieb und Abdeckungsmatrix. Die konkrete Engine wird nach Schritt 1 benannt; diese Planung installiert keine neue Bibliothek.

- Der Quelladapter liest CSAF 2.0 entsprechend den tatsächlich angebotenen Metadaten, prüft Schema, Herausgeber, Verteilungslabel, Integrität und angebotene Signaturen. Vertrauensanker explizit festhalten; Schlüsselwechsel nicht allein aus unbeglaubigten Metadaten automatisch übernehmen. HTTPS, begrenzte Laufzeiten, Retries, Antwortgrößen und zulässige Weiterleitungsziele absichern. Cache nur nach erfolgreicher Validierung aktualisieren und atomar schreiben.
- ROLIE-Inhalte nicht als ausführbaren Code oder beliebige Download-Ziele behandeln. Nur geprüfte öffentliche Anbieterpfade abrufen; externe Herstellerlinks bleiben Belege. Auch kalte Initialisierung muss innerhalb des CI-Zeitlimits funktionieren; Indexgröße, Downloadanzahl und Rate-Limits im Machbarkeitslauf messen. Kandidatenauswahl über nachvollziehbare Produkt-/Aliaszuordnung und Indexkategorien spezifizieren; kein bloßer Titel-Substring-Filter. ETags, Revisionen und begrenzte Parallelität vermeiden einen ungeprüften Komplettdownload aller Advisories in jedem PR.
- Aktualität anhand eines erfolgreich aktualisierten Index-/Provider-Snapshots prüfen, maximale Zeit seit erfolgreicher Quellenaktualisierung **48 Stunden**. Rückdatierte Revisionen, widersprüchliche Metadaten und Fehler beim notwendigen Update blockieren. Ein fehlgeschlagener Abruf darf nicht durch einen alten Cache grün werden.
- Ein normalisiertes internes Ergebnisformat speichert Advisory-ID/Revision, CVE, Paket-/Komponenten-ID, Quelldatenhash, Produktbezug, betroffene/fixe Versionen, Score und Zuordnungsevidenz. CSAF-Produkt-IDs sind dokumentlokal; `product_status`, Produktbaum und etwaige Beziehungen müssen gemeinsam aufgelöst werden.
- Exakte PURLs bevorzugen; CPE und explizit geprüfte Maven-/Hersteller-Aliase nur mit dokumentierter Identität verwenden. Namensähnlichkeit ist kein belastbarer Treffer: RallyOns eigenes `keycloak-core` bleibt vom Keycloak-Server getrennt, die Micrometer-Registry vom Prometheus-Server. Betriebssystem-/Distributionspakete nicht mit eigenständigen Maven-JARs gleichsetzen.
- Maven-Versionen, Release-Kandidaten und Versionsbereiche nicht lexikografisch vergleichen. Unklare Angaben werden `unknown`; `fixed` und `known_not_affected` gelten nur für das konkret referenzierte Produkt und die konkrete Version. Neuere Dokumentrevisionen und Rücknahmen nachvollziehbar behandeln; widersprüchliche Aussagen nicht still überschreiben.
- Eingebettetes DOMPurify aus den tatsächlich ausgelieferten Swagger-UI-JARs und beiden Bundles weiterhin erkennen, dessen Version/Hash belegen und als eigene Komponente prüfen. Die SBOM allein ersetzt diese Erkennung nicht. Versionserkennung und Produktzuordnung müssen auch für unbekannte zukünftige Bundle-Layouts sichtbar fehlschlagen.

### 3. Unverändert blockierendes Sicherheits-Gate

- Numerische, zum betroffenen Produkt gehörige CVSS-Basisscores aus dem gewählten BSI-Dokument verwenden und Score-Version/Quelle ausweisen. Unterstützte CVSS-Versionen und den Umgang mit mehreren Scores spezifizieren und testen; ein vorhandener anwendbarer Basisscore ≥ 7 darf nicht durch einen niedrigeren Wert verschwinden. CSAF `aggregate_severity: hoch` nicht in eine erfundene Zahl umrechnen.
- Bei einer relevanten Meldung ohne belastbaren numerischen Score, bei unklarer Paket-/Versionszuordnung oder bei widersprüchlichem Status lautet das Ergebnis `unknown` bzw. `conflict` und blockiert. Fehlender Advisory-Treffer wird ausdrücklich `not_listed`, nicht `known_not_affected`. Komponenten ohne nachgewiesene Quellen-/Identitätsabdeckung verhindern die positive Cutover-Abnahme.
- Die historischen Baseline-Scores bleiben Vergleichsbelege; sie werden nicht als angeblich vom BSI gelieferte aktuelle Werte übernommen. Eine manuelle Ergänzung oder alternative Datenquelle braucht eine konkrete genehmigte Regel und nachvollziehbare Herkunft.
- Vorhandene Fehlzuordnungsregeln nicht blind in die neue Engine übernehmen. Nur weiterhin erforderliche, eng auf Komponente und Advisory/CVE begrenzte Ausnahmen mit Herstellerbeleg und maximal 90 Tagen Laufzeit zulassen. Abgelaufene, zu breite oder unvollständige Regeln scheitern. Fehlende BSI-Abdeckung ist keine zulässige Ausnahme.
- Getrennte Ergebnisgründe für blockierenden Befund, unzureichende Abdeckung und Infrastruktur-/Quellfehler liefern. Jeder bleibt im CI rot. Deterministische HTML-/JSON-Berichte enthalten auch fehlende/unklare Daten und Quellenbelege; keine Ausgabe „0 vulnerabilities“ bei fehlgeschlagener Prüfung.

### 4. Workflow-Integration und sichere Umstellung

- Bestehenden Workflow-Dateinamen und Check-Namen `Dependency vulnerability scan` / `Generate SBOM` erhalten. Erfolgreiche Dependency-Vorbereitung liefert unveränderliche Runtime-Inputs und deren Hashes an beide unabhängigen Jobs. SBOM bleibt auch bei rotem BSI-Gate verfügbar.
- Ausschließlich geprüfte öffentliche BSI-Daten verwenden; keine CI-Schlüssel für BSI voraussetzen. PRs lesen kompatible vertrauenswürdige Caches, schreiben sie aber nicht gemeinsam. Speichern nur bei erfolgreicher Validierung in vertrauenswürdigen Default-Branch-Push-/Schedule-Läufen; Cache-Identität enthält Anbieter, Schema und Engine-Version.
- Keine neue verpflichtende Produktionsinfrastruktur, keine `pull_request_target`-Ausführung fremden Codes mit Secrets. Für neue Werkzeuge vollständige Versionen/Digests und SHA-gepinnte Actions verwenden. Die bisherige Scanner-Logik, Cache-Schlüssel und NVD-Schlüsselreferenzen erst nach nachgewiesener Gleichwertigkeit entfernen; Historie und fehlgeschlagene Läufe erhalten.
- Für den Vergleich zunächst gespeicherte Baseline-/Herstellerbelege und Offline-Fixtures verwenden. Ein erneuter NVD-Netzabruf ist keine Voraussetzung für die BSI-Auswertung. Während einer eventuellen CI-Vergleichsphase bleibt ein Fehler sichtbar; es gibt keinen deaktivierten alten Check, der als erfolgreich dargestellt wird.
- Betriebshandbuch beschreibt Datenquellen, Update-/Frische-Regeln, Fehlerdiagnose, Cache-Recovery, Vertrauensanker und Abdeckungsgrenzen. Ein Rückbau auf den dokumentierten alten Zustand bleibt nachvollziehbar; dessen bekannter NVD-Fehler wird dadurch nicht behoben oder grün.

### 5. Nachweis, Lifecycle und Auslieferung

- Wegen der veränderten Datenquelle, Engine und Sicherheitsannahmen G1/G2 anhand konkreter aktualisierter OpenSpec-Artefakte erneut vorlegen; Value-/Aufwandsannahmen neu bewerten. Danach frische unabhängige G3-Prüfung, aktualisierte Aufgaben und exakt angezeigtes Umsetzungspaket für erneutes G4. #200 wiederverwenden, sofern derselbe Ticketumfang passt; kein zusätzliches Issue ohne G4 und keinen bereits erfolgreichen Helper-Eintrag löschen oder erneut erstellen. Wenn die Änderung des bestehenden Issue-Payloads besondere Helper-Unterstützung erfordert, das offen klären statt den Schutz zu umgehen.
- Die aktuelle Planung ändert weder bestehende OpenSpec-Approvals noch Gate-Ergebnisse. Die Umsetzung der BSI-Anpassung beginnt erst nach den erneuerten Freigaben. Ein bloß erfolgreicher BSI-Download erfüllt kein Gate.
- Schritte in PR #201 nachvollziehbar committen: erst Quellen-/Abdeckungsnachweis und Tests, dann freigegebene Engine/Policy und Workflow-Umschaltung, schließlich Entfernen veralteter NVD-Konfiguration und Betriebsdokumentation. Separate PR-1-/PR-2-Lieferung erhalten; bei zusätzlichem Architekturumfang zuerst neu entscheiden.
- Native unabhängige Prüfungen für G5/G6 und alle drei G7-Linsen; gewitnesseter Walkthrough der Quellen-/Abdeckungsberichte, eines blockierenden Befunds und eines Datenfehlers für G8. Menschliche Merge-Freigabe separat. Ohne positive Abdeckung und vollständige tatsächliche CI-Evidenz bleibt #187 offen.

## BSI-Recherche und offene Machbarkeit

Am **2026-11-03** wurden die offiziellen Provider-Metadaten, der öffentliche WID-Index und drei konkrete Dokumente gelesen. Das ist eine begrenzte Untersuchung, keine vollständige BSI-Abdeckungszertifizierung.

- Der WID-Index umfasst 14.439 Einträge; sein Updatezeitpunkt war `2026-11-03T10:00:02.448+00:00`. SHA-256 des gelesenen JSON: `7decd673bbf39ad8fc2b7487adc6556712f5eeb02542f5c7c76dbc91f96a6fd1`.
- **47 von 64** bisherigen IDs stehen als CVE-Kategorie im WID-Index; **17 fehlen dort**, darunter vier bereits belegte Fehlzuordnungen und 13 andere IDs aus DOMPurify, Kotlin und Spring Data JPA. Das ist nur Indexpräsenz, keine Aussage über passende Versionen oder über alle weiteren öffentlichen BSI-Feeds. Weitere Feeds müssen vor dem Go/No-Go untersucht werden.
- Die [Tomcat-Meldung WID-SEC-W-2026-3007](https://wid.cert-bund.de/.well-known/csaf/white/2026/wid-sec-w-2026-3007.json) enthält zehn Vulnerability-Einträge, die [Spring-Meldung WID-SEC-W-2026-1828](https://wid.cert-bund.de/.well-known/csaf/white/2026/wid-sec-w-2026-1828.json) 18 und die [DOMPurify einschließende Meldung WID-SEC-W-2026-2452](https://wid.cert-bund.de/.well-known/csaf/white/2026/wid-sec-w-2026-2452.json) elf. Alle drei sind `csaf_base`, führen `aggregate_severity: hoch`, aber enthalten in den geprüften Vulnerability-Einträgen keine `scores`. Ein vollständiger Ersatz des numerischen CVSS-Gates ist damit **noch nicht belegt**.
- Die DOMPurify einschließende Meldung betrifft Red Hat Ansible; daraus folgt nicht automatisch eine Aussage über das in Swagger UI eingebettete DOMPurify. Ebenso ist eine PostgreSQL-Server-Meldung kein Beleg für JDBC. Diese Zuordnungen gehören ausdrücklich in den Machbarkeitsnachweis.
- Dependency-Check dokumentiert [NVD/CPE als Datenquelle](https://dependency-check.github.io/DependencyCheck/data/index.html), während das BSI [CSAF-/ROLIE-Verteilungen](https://wid.cert-bund.de/.well-known/csaf/provider-metadata.json) veröffentlicht. Eine bloße URL-Umstellung erfüllt daher die unterschiedlichen Formate und Auswertungsanforderungen nicht. Maßgeblich für die Adapter-Semantik sind die [CSAF-2.0-Spezifikation](https://docs.oasis-open.org/csaf/csaf/v2.0/os/csaf-v2.0-os.html) und die tatsächlich validierten BSI-Dokumente.

## Zusätzliche Validierung für den BSI-Ersatz

- Vollständiges Inventar und Baseline-Abdeckung prüfen: 145 Runtime-PURLs, eingebettete Bundles, 13 Einträge, 82 Instanzen, 64 IDs; jede fehlende/mehrdeutige Zuordnung und jede Abweichung zu Herstellerbelegen ausweisen. Fix-Versionen und benachbarte betroffene Versionen als Gegenproben verwenden. Keine verlorenen Findings beim Zusammenführen mehrerer Advisories.
- Offline-Fixtures für CSAF-/ROLIE-Schema, öffentliche Labels, Signaturen/Schlüsselwechsel, ungültige/vertauschte Hashes, URL-/Redirect-Grenzen, Größenlimits, Timeouts, leere oder abgeschnittene Antworten und atomare Cache-Recovery. Kalt-/Warmläufe, notwendige Updates, Cache-Kompatibilität und 48-Stunden-Grenze mit injizierter Uhr prüfen.
- Paket-/Versionsfälle: exakte PURL/CPE, Namenskollisionen, dokumentlokale Produkt-IDs, `known_affected`, `fixed`, `known_not_affected`, `under_investigation`, fehlender Status, Versionsbereiche und Vorabversionen, zurückgezogene/aktualisierte Dokumente sowie widersprüchliche Meldungen. Tests dürfen die gewünschte Zuordnung nicht einfach selbst erfinden.
- Policy-Fälle: CVSS 6,9 / 7,0 / 10,0, fehlender Score, mehrere anwendbare Scores, fehlender Treffer und unzureichende Abdeckung. Ein Eintrag mit nur `hoch` bleibt ohne genehmigte Ergänzungsregel ungeklärt. Zwei Bundle-Funde derselben CVE nicht still zu einem einzigen Inventarbestandteil zusammenziehen.
- Workflow-Fixtures für Fork-/Same-Repo-PR-/Main-/Schedule-Trust, fehlgeschlagene Vorbereitung, blockierende bzw. unbekannte Findings und Quellfehler. Echte GitHub-Läufe und heruntergeladene Artefakte belegen, dass der Scan rot bleibt und die unabhängige SBOM trotzdem vorhanden ist.
- Existierende Security-Regressionen mit `python3 -m unittest discover -s tools/scripts/tests -p 'test_security*.py' -v` weiter ausführen; für die freigegebene BSI-Engine dort die neuen Fixtures integrieren. Neue Engine-spezifische Befehle erst nach Toolauswahl verbindlich dokumentieren. Keine Live-Issues, Releases, Tags, Registry-Pushes, Deployments oder echten Auth-Aufrufe zur Prüfung.

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
