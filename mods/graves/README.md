# Horsti Graves

**`horsti-graves` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Kein Item-Despawn mehr: Beim Tod entsteht ein Grab mit deinem Inventar — geschützt, auffindbar, fair.

## Was macht der Mod?

Stirbst du, entsteht am Todesort ein Grab aus **zwei gestapelten Vanilla-Kisten** (54 Slots — mehr als dein
Inventar fassen kann, es geht also nichts verloren). Weil es echte Kisten sind, speichert Minecraft den
Inhalt selbst: Verzauberungen, Haltbarkeit und Namen bleiben unangetastet, auch über Server-Neustarts.

Öffnen geht ganz normal per Rechtsklick. Eine Schutzzeit lang kann **nur der Besitzer** das Grab öffnen —
danach ist es für alle offen (Loot-Anreiz). Beim ersten eigenen Öffnen bekommst du deine XP zurück
(Anteil konfigurierbar). Stirbst du im Void oder in Lava, wandert das Grab an die nächste sichere Stelle
darüber.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/graves` | Status + aktuelle Werte |
| `/graves on \| off` | Mod an/aus (off = Vanilla-Drop) |
| `/graves set schutzMin <0–120>` | Nur-Besitzer-Schutzzeit, 0 = sofort offen (Default: **15**) |
| `/graves set xpErhalt <0–100>` | Prozent der XP im Grab (Default: **100**) |
| `/graves set ansage on\|off` | Todesort-Koordinaten beim Tod anzeigen (Default: **on**) |
| `/graves liste [<Spieler>]` | Aktive Gräber mit Koordinaten |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/graves.json`, live per `/graves reload`. Grab-Metadaten: `config/horsti/daten/graves.json` (die Items selbst liegen in den Kisten, also in der Welt).

## Hinweis Modularität

Die „Todesort-Ansage“ überschneidet sich bewusst mit `horsti-todesort` — wer Graves nutzt, braucht
Todesort nicht (und umgekehrt). Beide erkennen sich gegenseitig und deaktivieren die Doppel-Ansage.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | **Sehr hoch** (Item-Verlust ist Frustquelle Nr. 1) |
| Gibt’s das schon? / Mehrwert | „Universal Graves“ (Polymer) ist stark. Mehrwert: dependency-frei, bewusst schlank, Loot-Anreiz nach Schutzzeit als eigener Dreh. Öffentlich nur bei echtem Mehrwert nach Playtest |
| Geschätzter Aufwand | Mittel (Randfälle: Void, Lava, Grief-Schutz, Mehrfach-Tode) |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
