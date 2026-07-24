# Horsti Todesort

**`horsti-todesort` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Beim Tod bekommst du deine Todeskoordinaten privat in den Chat — klickbar zum Kopieren.

## Was macht der Mod?

Stirbst du, erscheint (nur für dich) eine Chatzeile wie
`☠ Du starbst bei X 120 / Y 64 / Z -338 (Overworld)` — ein Klick kopiert die Koordinaten in die
Zwischenablage. Ergänzt den Vanilla-Recovery-Kompass, ersetzt ihn nicht: Die Zeile bleibt im Chatverlauf,
auch nach mehrmaligem Sterben.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/todesort` | Status + aktuelle Werte |
| `/todesort on \| off` | Mod an/aus |
| `/todesort set oeffentlich on\|off` | Koordinaten für alle sichtbar statt privat (Default: **off**) |
| `/todesort set dimension on\|off` | Dimension mit anzeigen (Default: **on**) |
| `/todesort reset` | Defaults wiederherstellen |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der Singleplayer-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/todesort.json` — wird automatisch erzeugt; Datei-Änderungen greifen per `/todesort reload`.
Jeder Wert ist auch per Command erreichbar (siehe oben).

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (Dauer-QoL-Wunsch; „wo bin ich gestorben?“) |
| Gibt’s das schon? / Mehrwert | Teil größerer Mods; als Mini-Standalone selten. Mehrwert: 5-KB-Mod, togglebar, klickbare Koordinaten |
| Geschätzter Aufwand | Trivial |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
