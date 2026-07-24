# Horsti Juggernaut

**`horsti-juggernaut` · Minigame · server-seitig · Vanilla-Clients kompatibel**

> Einer gegen alle — und der Eine ist ein Panzer. Automatisch fair skaliert nach Spielerzahl.

## Was macht der Mod?

Ein Spieler (gewählt oder zufällig) wird Juggernaut: mehr Herzen, Resistenz und Stärke — automatisch
skaliert mit der Zahl der Gegner, damit es bei 3 wie bei 10 Jägern spannend bleibt. Der Juggernaut leuchtet
optional (damit die Meute ihn findet), Jäger respawnen normal, der Juggernaut nicht. Sieg: Jäger töten den
Juggernaut / der Juggernaut tötet alle Jäger je einmal (konfigurierbare Kill-Quote). Boss-Bar zeigt
Juggernaut-HP für alle.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/juggernaut` | Status + aktuelle Werte |
| `/juggernaut start random\|<Spieler>` | Runde starten |
| `/juggernaut stop` | Runde sauber beenden (Buffs weg) |
| `/juggernaut set herzenProGegner <1–10>` | Extra-Herzen je Jäger (Default: **4**) |
| `/juggernaut set staerkeAb <2–10>` | Stärke I ab X Jägern, II ab 2X … (Default: **4**) |
| `/juggernaut set glow on\|off` | Juggernaut leuchtet (Default: **on**) |
| `/juggernaut set killQuote <1–5>` | Kills pro Jäger, die der Juggernaut zum Sieg braucht (Default: **1**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/juggernaut.json`, live per `/juggernaut reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (bekanntes Format aus Shootern/Minigame-Servern) |
| Gibt’s das schon? / Mehrwert | Als leichte Fabric-Server-Mod kaum vorhanden — Lücke. Mehrwert: Auto-Balancing nach Spielerzahl statt fester Werte |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
