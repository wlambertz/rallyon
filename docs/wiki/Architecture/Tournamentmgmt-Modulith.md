# Tournament Management Modulith

Die PlantUML- und AsciiDoc-Exporte fuer `service/tournamentmgmt` bleiben die Quelle der Wahrheit im Haupt-Repository unter `service/tournamentmgmt/docs/modulith/generated/`. Dieses Wiki bindet daraus erzeugte PNG-Kopien ein, damit die Diagramme direkt lesbar sind.

## Aktualisierung

Zuerst die Modulith-Quellen im Haupt-Repository regenerieren:

```bash
cd service/tournamentmgmt
./mvnw test -Dmodulith.docs=true -Dtest=TournamentmgmtDocumentationTests
```

Danach die Wiki-Bilder aus den `.puml`-Dateien rendern:

```bash
./service/tournamentmgmt/docs/modulith/render-wiki-diagrams.sh
```

Die gerenderten Wiki-Dateien liegen unter `wiki/Architecture/generated/tournamentmgmt-png/`.

## Overview

![Tournamentmgmt overview](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/components.png)

## Administration

![Administration module](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/module-administration.png)

## Planning

![Planning module](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/module-planning.png)

## Setup

![Setup module](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/module-setup.png)

## Setup / Phases

![Setup phases module](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/module-setup.phases.png)

## Setup / Rules

![Setup rules module](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/module-setup.rules.png)

## Setup / Configuration

![Setup configuration module](https://raw.githubusercontent.com/wiki/wlambertz/rallyon/Architecture/generated/tournamentmgmt-png/module-setup.configuration.png)
