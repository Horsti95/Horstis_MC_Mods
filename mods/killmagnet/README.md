# Horsti Killmagnet

**`horsti-killmagnet` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Was du tötest, gehört dir: Drops deiner Kills fliegen direkt zu dir (oder ins Inventar).

## Was macht der Mod?

Tötest du einen Mob, landen dessen Drops und XP nicht auf dem Boden, sondern kommen zu dir — wahlweise
als „Magnet“ (Items fliegen sichtbar zu dir) oder direkt ins Inventar (voll = fällt normal). Gilt nur für
**deine** Kills; Umgebungs-Drops (Abbau, andere Todesursachen) bleiben vanilla. Kein „Staubsauger“-Mod.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/killmagnet` | Status + aktuelle Werte |
| `/killmagnet on \| off` | Mod an/aus |
| `/killmagnet set modus magnet\|inventar` | Flug-Animation oder Direkteinzug (Default: **magnet**) |
| `/killmagnet set xp on\|off` | XP ebenfalls einziehen (Default: **on**) |
| `/killmagnet set spielerKills on\|off` | Auch bei PvP-Kills (Default: **off**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/killmagnet.json`, live per `/killmagnet reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel-hoch (Auto-Pickup ist beliebtes QoL) |
| Gibt’s das schon? / Mehrwert | Item-Magnete existieren als Dauer-Sauger; Mehrwert: **kill-gebunden** (originell, balance-schonend), togglebar |
| Geschätzter Aufwand | Trivial |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
