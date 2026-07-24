# Horsti Manhunt

**`horsti-manhunt` · Minigame · server-seitig · Vanilla-Clients kompatibel**

> Runner wollen den Drachen legen. Jäger haben einen Kompass, der immer auf die Beute zeigt.

## Was macht der Mod?

Das Dream-Format für die eigene Runde: Rollen per Command verteilen, Jäger bekommen einen **Tracking-
Kompass** (Rechtsklick wechselt das Ziel durch alle Runner). Funktioniert dimensionsübergreifend: Ist der
Runner in einer anderen Dimension, zeigt der Kompass auf sein Eintritts-Portal. Schonfrist beim Start
(Jäger eingefroren + blind, konfigurierbar). Sieg-Erkennung: Enderdrache tot → Runner gewinnen; alle Runner
final tot → Jäger gewinnen (Runner-Respawn togglebar: Hardcore-Modus vs. Respawn-erlaubt). Jäger behalten
den Kompass beim Respawn automatisch.

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
| `/manhunt set kompassSlot on\|off` | Kompass belegt festen Slot 9 (Default: **on**) |

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
