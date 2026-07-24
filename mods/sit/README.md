# Horsti Sit

**`horsti-sit` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Rechtsklick auf Treppe oder Stufe — und du sitzt. Endlich.

## Was macht der Mod?

Rechtsklick mit leerer Hand auf eine Treppe oder Stufe setzt dich darauf (unsichtbares Sitz-Entity,
funktioniert für Vanilla-Clients). Aufstehen: Shift, Bewegung oder erneuter Klick — du landest sicher auf
dem Ausgangsblock. Bei Schaden stehst du automatisch auf. Optional gibt es `/sitz` zum Hinsetzen an Ort und
Stelle (für alle Spieler, togglebar).

## In-Game-Steuerung

Für alle Spieler: Rechtsklick auf Treppe/Stufe · `/sitz` (wenn aktiviert).

| Command (OP-Level 2) | Wirkung |
|---|---|
| `/sit` | Status + aktuelle Werte |
| `/sit on \| off` | Mod an/aus (Sitzende stehen sauber auf) |
| `/sit set treppen on\|off` | Treppen klickbar (Default: **on**) |
| `/sit set stufen on\|off` | Stufen klickbar (Default: **on**) |
| `/sit set command on\|off` | `/sitz` für alle erlauben (Default: **off**) |
| `/sit set nurLeereHand on\|off` | Nur mit leerer Hand (Default: **on** — verhindert Fehlklicks beim Bauen) |
| `/sit set aufstehenBeiSchaden on\|off` | Bei Schaden automatisch aufstehen (Default: **on**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/sit.json`, live per `/sit reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Hoch (Evergreen seit über einem Jahrzehnt) |
| Gibt’s das schon? / Mehrwert | „Polysit“ (server-seitig, braucht Polymer). Mehrwert: dependency-frei, feiner togglebar, Fehlklick-Schutz. Öffentlich nur bei echtem Mehrwert nach Playtest |
| Geschätzter Aufwand | Leicht–mittel (Randfälle: Aufstehen, Block weggebaut, Portal) |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
