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
| `/anvilfix set maxKosten <1–39>` | Kostendeckel in Leveln; 39 = nie „Zu teuer“ (Default: **39**) |
| `/anvilfix set priorWork vanilla\|halb\|aus` | Verdopplungs-Strafe (Default: **halb**) |

*Warum max. 39? Vanilla-Clients verweigern ab 40 client-seitig die Entnahme — der Deckel bleibt darunter,
damit auch ungemoddete Clients das Ergebnis nehmen können. Effekt: „Zu teuer“ existiert nicht mehr,
teure Aktionen kosten höchstens `maxKosten` Level.*

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
