# Horsti AFK

**`horsti-afk` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Wer eine Weile nichts tut, wird in der Tab-Liste als AFK markiert.

## Was macht der Mod?

Keine Bewegung, kein Chat, kein Klick für X Minuten → der Spielername in der Tab-Liste wird grau und
bekommt das Suffix `[AFK]`; optional eine dezente Chat-Ansage. Jede Aktivität hebt den Status sofort auf.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/afk` | Status + aktuelle Werte |
| `/afk on \| off` | Mod an/aus |
| `/afk set minuten <1–60>` | Inaktivität bis AFK (Default: **5**) |
| `/afk set ansage on\|off` | Chat-Ansage bei AFK/Rückkehr (Default: **on**) |
| `/afk set kickMinuten <0–120>` | Nach weiteren X Min. kicken, 0 = nie (Default: **0**) |

Spieler ohne OP können sich mit `/afk!` manuell als AFK markieren.

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/afk.json`, live per `/afk reload`. Alle Werte auch per Command.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (SMP-Standardwunsch) |
| Gibt’s das schon? / Mehrwert | Existiert in Essentials-artigen Paketen; Mehrwert: Mini-Standalone ohne Beiwerk, rein Tab-Listen-basiert |
| Geschätzter Aufwand | Trivial |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
