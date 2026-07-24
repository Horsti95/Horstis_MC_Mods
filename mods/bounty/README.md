# Horsti Bounty

**`horsti-bounty` · Minigame/SMP · server-seitig · Vanilla-Clients kompatibel**

> Alle Stunde ein Kopfgeld auf einen Zufallsspieler: Er leuchtet, alle jagen, der Killer kassiert.

## Was macht der Mod?

In konfigurierbaren Abständen wird ein zufälliger Online-Spieler zur Zielscheibe: Server-Ansage, das Ziel
bekommt Glowing (optional erst nach Schonfrist) und einen Boss-Bar-Timer. Wer das Ziel innerhalb der Frist
tötet, bekommt die Belohnung (konfigurierbare Item-Liste). Überlebt das Ziel, kassiert es selbst die
Belohnung („Überlebensprämie“). Faire Ziele: niemand wird zweimal hintereinander gezogen, frisch gejoinete
Spieler haben Karenz.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/bounty` | Status + aktuelle Werte |
| `/bounty on \| off` | Automatik an/aus |
| `/bounty jetzt [<Spieler>]` | Sofort ein Kopfgeld (zufällig oder gezielt) |
| `/bounty belohnung <item> <anzahl>` | Belohnung setzen, mit Item-Autovervollständigung (Default: **3× Diamant**) |
| `/bounty set intervallMin <10–240>` | Abstand zwischen Kopfgeldern (Default: **45**) |
| `/bounty set dauerMin <5–60>` | Jagd-Frist (Default: **15**) |
| `/bounty set glow on\|off` | Ziel leuchtet (Default: **on**) |
| `/bounty set minSpieler <2–16>` | Automatik erst ab X Online-Spielern (Default: **3**) |
| `/bounty set ueberlebensPraemie on\|off` | Ziel kassiert bei Überleben (Default: **on**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/bounty.json`, live per `/bounty reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (beliebtes SMP-Würzmittel) |
| Gibt’s das schon? / Mehrwert | Fast nur als Paper-Plugin — im Fabric-Bereich Lücke. Mehrwert: Standalone-Modul, Überlebensprämie als eigener Dreh |
| Geschätzter Aufwand | Leicht–mittel |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
