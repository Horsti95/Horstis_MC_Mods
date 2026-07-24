# Horsti Anvilfix

**`horsti-anvilfix` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Schluss mit „Zu teuer!“ — der Amboss-Klassiker unter den Community-Wünschen, endlich regelbar.

## Was macht der Mod?

Entfernt das „Too Expensive“-Limit (Vanilla: ab 40 Leveln blockiert) und macht die Amboss-Kosten
konfigurierbar. Die „Prior Work Penalty“ (jede Reparatur verdoppelt künftige Kosten) lässt sich dämpfen
oder abschalten. Alles reine Server-Logik — das Amboss-Menü wird server-seitig berechnet, Vanilla-Clients
sehen einfach die neuen Kosten.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/anvilfix` | Status + aktuelle Werte |
| `/anvilfix on \| off` | Mod an/aus (off = Vanilla-Verhalten) |
| `/anvilfix set levelCap <0–1000>` | Kostenobergrenze, 0 = kein Limit (Default: **0**) |
| `/anvilfix set priorWork vanilla\|halb\|aus` | Verdopplungs-Strafe (Default: **halb**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/anvilfix.json`, live per `/anvilfix reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | **Sehr hoch** — einer der langlebigsten Vanilla-Beschwerdepunkte überhaupt |
| Gibt’s das schon? / Mehrwert | Meist Teil größerer Anvil-Rework-Mods (oft mit Client-Teil). Mehrwert: Mini-Standalone, rein server-seitig, live regelbar |
| Geschätzter Aufwand | Leicht |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
