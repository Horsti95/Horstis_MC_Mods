# Horsti Keepmoving

**`horsti-keepmoving` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Wer rastet, der rostet: Stillstand tut nach einer Karenzzeit weh. Für Nervenkitzel-Runden.

## Was macht der Mod?

Bewegst du dich X Sekunden lang nicht (Position ± kleine Toleranz), beginnt ein wählbarer Malus zu ticken:
direkter Schaden, Hunger oder Wither-Effekt. Actionbar-Warnung während der Karenz („Beweg dich! 3…2…1“).
Gedacht als Runden-Twist („Keep-Moving-SMP“), nicht als Dauerzustand — daher startet der Mod **aus** und
wird pro Session per Command scharf geschaltet.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/keepmoving` | Status + aktuelle Werte |
| `/keepmoving on \| off` | scharf/entschärft (Default: **off**) |
| `/keepmoving set karenzSek <3–120>` | Stillstand bis zum Malus (Default: **10**) |
| `/keepmoving set modus schaden\|hunger\|wither` | Malus-Art (Default: **schaden**) |
| `/keepmoving set staerke <1–5>` | Malus-Stärke pro Sekunde (Default: **1**, = ½ Herz) |
| `/keepmoving set warnung on\|off` | Actionbar-Countdown (Default: **on**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/keepmoving.json`, live per `/keepmoving reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Niedrig-mittel (Nischen-Twist, aber origineller Party-Effekt) |
| Gibt’s das schon? / Mehrwert | Kaum vorhanden — praktisch eine Lücke. Mehrwert: fertiges, tunebares Runden-Format |
| Geschätzter Aufwand | Leicht |
| Zielgruppe | MP (SP für Selbst-Challenges) |
| Horsti-Priorität | TBD |
