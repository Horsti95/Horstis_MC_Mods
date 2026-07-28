# Horsti Holzsäge

**`horsti-holzsaege` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Die Steinsäge kann endlich Holz: Stamm rein, Bretter/Treppen/Stufen/Zäune raus.

## Was macht der Mod?

Fügt der Vanilla-Steinsäge (Stonecutter) Rezepte für alle Holzarten hinzu: Stamm → Bretter, Bretter →
Stufen/Treppen/Zäune/Zauntore/Türen/Falltüren/Knöpfe/Druckplatten/Schilder. Rezepte werden zur Laufzeit für
**alle** Holzarten generiert (inkl. Bambus, Kirsche, Mangrove, Karmesin/Wirr) und automatisch an die Clients
gesynct — Vanilla-Clients sehen sie ganz normal in der Säge. Keine neuen Blöcke, keine neuen Assets.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/holzsaege` | Status + aktuelle Werte |
| `/holzsaege on \| off` | Rezepte aktiv/inaktiv (lädt die Rezeptliste live neu) |
| `/holzsaege set tueren on\|off` | Auch Türen/Falltüren (Default: **on**) |
| `/holzsaege set redstone on\|off` | Auch Knöpfe/Druckplatten (Default: **on**) |

*Technik: Die Rezepte stecken in drei eingebauten Datapacks (Basis/Türen/Redstone); Toggles schalten die
Packs um und laden live neu. Erträge fest an Vanilla angelehnt (Stamm→4 Bretter, Bambus-Block→2).*

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/holzsaege.json`, live per `/holzsaege reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel-hoch (Evergreen: „Warum kann die Steinsäge kein Holz?“) |
| Gibt’s das schon? / Mehrwert | Als Datapack verbreitet. Mehrwert: automatische Abdeckung **aller** Holzarten (auch künftiger), Ertrag regelbar, togglebar |
| Geschätzter Aufwand | Leicht |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
