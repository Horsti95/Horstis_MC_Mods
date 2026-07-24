# Horsti Tag

**`horsti-tag` · Minigame · server-seitig · Vanilla-Clients kompatibel**

> Fangen in Minecraft: „Es“ leuchtet, rennt schneller — und will dich unbedingt berühren.

## Was macht der Mod?

Ein Spieler wird zufällig „Es“ (Glowing + Speed, für alle sichtbar angesagt). Schlägt „Es“ einen anderen
Spieler, wandert die Rolle weiter (kurzer Rückgabe-Schutz verhindert Ping-Pong). Wer beim Rundenende „Es“
ist, verliert; alle anderen bekommen Punkte für ihre „Nicht-Es“-Zeit (Scoreboard). Boss-Bar zeigt die
Restzeit. Kein PvP-Schaden nötig — der Treffer zählt, der Schaden wird auf Wunsch neutralisiert.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/tag` | Status + aktuelle Werte |
| `/tag start [alle\|<Spieler…>]` | Runde starten, zufälliges „Es“ |
| `/tag stop` | Runde sauber beenden (Effekte weg, Wertung) |
| `/tag set rundenMin <1–60>` | Rundenlänge (Default: **10**) |
| `/tag set esSpeed <0–2>` | Speed-Stufe für „Es“ (Default: **1**) |
| `/tag set esGlow on\|off` | „Es“ leuchtet (Default: **on**) |
| `/tag set schutzSek <0–30>` | Rückgabe-Schutz nach Übergabe (Default: **5**) |
| `/tag set schaden on\|off` | Schlag macht echten Schaden (Default: **off**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/tag.json`, live per `/tag reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (Party-Klassiker, jeder versteht es sofort) |
| Gibt’s das schon? / Mehrwert | Als leichte Fabric-Server-Mod **kaum vorhanden** (meist Minigame-Server-Feature) — echte Lücke. Mehrwert: 1-Command-Start, überall spielbar |
| Geschätzter Aufwand | Leicht–mittel |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
