# Horsti Manhunt

**`horsti-manhunt` · Minigame · server-seitig · Vanilla-Clients kompatibel**

> Runner wollen den Drachen legen. Jäger haben einen Kompass, der immer auf die Beute zeigt.

## Was macht der Mod?

Das Dream-Format für die eigene Runde: Rollen per Command verteilen, Jäger bekommen beim Start einen
**Kompass**. Solange ein Jäger ihn in der Hand hält, bekommt er sekündlich eine **Peilung** in die Actionbar:
Richtungspfeil relativ zur eigenen Blickrichtung, Entfernung in Blöcken — und bei einem Runner in einer
anderen Dimension die Meldung „ist im Nether/Ende“. Mit `/ziel` schaltet man durch mehrere Runner.

*Warum keine Kompass-Nadel? Eine Nadel zeigt beim Dimensionswechsel ins Leere und verrät keine Entfernung.
Die Peilung funktioniert überall, braucht keine Item-Komponenten und ist damit auch update-fester.*

Schonfrist beim Start (Jäger blind + eingefroren, konfigurierbar). Sieg-Erkennung: Enderdrache von einem
Runner erlegt → Runner gewinnen; alle Runner tot → Jäger gewinnen (Runner-Respawn togglebar).

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/manhunt` | Status: Rollen + aktuelle Werte |
| `/manhunt runner add\|remove <Spieler>` | Runner setzen |
| `/manhunt hunter add\|remove <Spieler>` | Jäger setzen |
| `/manhunt start` | Spiel starten (Schonfrist läuft) |
| `/manhunt stop` | Spiel sauber beenden |
| `/manhunt set schonfristSek <0–300>` | Jäger-Freeze am Start (Default: **30**) |
| `/manhunt set runnerRespawn on\|off` | Runner dürfen respawnen (Default: **off** = klassisch) |
| `/manhunt set peilungSek <1–10>` | Sekunden zwischen zwei Peilungen (Default: **1**) |

Für Jäger: **`/ziel`** schaltet zum nächsten Runner durch.

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/manhunt.json`, live per `/manhunt reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Hoch (YouTube-Dauerbrenner) |
| Gibt’s das schon? / Mehrwert | Mehrere gute Server-Mods (Compass Manhunt u. a.). Mehrwert: an unsere Runde angepasst — Ziel-Durchschalten, Portal-Tracking, Schonfrist, Respawn-Modi. Öffentlich nur bei echtem Mehrwert nach Playtest |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
