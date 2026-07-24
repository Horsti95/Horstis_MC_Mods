# Horsti Graves

**`horsti-graves` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Kein Item-Despawn mehr: Beim Tod entsteht ein Grab mit deinem Inventar — geschützt, auffindbar, fair.

## Was macht der Mod?

Stirbst du, entsteht am Todesort ein Grab (Spielerkopf auf einem Sockel, nur Vanilla-Blöcke). Rechtsklick
aufs eigene Grab gibt alles zurück (direkt ins Inventar, Rest droppt); XP wird zu einem konfigurierbaren
Anteil aufbewahrt. Gräber sind eine Schutzzeit lang nur für den Besitzer öffnbar, danach für alle
(Loot-Anreiz!). Randfälle abgedeckt: Void-Tod → Grab am letzten sicheren Ort; Lava → Grab auf nächstem
festen Block; Explosionen zerstören Gräber nicht. Der Vanilla-Recovery-Kompass zeigt wie gewohnt zum
Todesort — das Grab liegt genau dort.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/graves` | Status + aktuelle Werte |
| `/graves on \| off` | Mod an/aus (off = Vanilla-Drop) |
| `/graves set schutzMin <0–120>` | Nur-Besitzer-Schutzzeit, 0 = sofort offen (Default: **15**) |
| `/graves set verfallMin <0–1440>` | Grab droppt danach alles, 0 = nie (Default: **0**) |
| `/graves set xpErhalt <0–100>` | Prozent der XP im Grab (Default: **100**) |
| `/graves set ansage on\|off` | Todesort-Koordinaten beim Tod anzeigen (Default: **on**) |
| `/graves liste [<Spieler>]` | Aktive Gräber mit Koordinaten |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/graves.json`, live per `/graves reload`.

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
