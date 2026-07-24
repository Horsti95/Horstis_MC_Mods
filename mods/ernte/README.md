# Horsti Ernte

**`horsti-ernte` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Rechtsklick auf reifes Getreide: ernten und automatisch neu pflanzen. Der QoL-Klassiker.

## Was macht der Mod?

Rechtsklick auf eine ausgewachsene Pflanze erntet sie und pflanzt sofort neu — ein Samen wird dabei aus dem
Drop abgezogen. Funktioniert generisch für alle Standard-Crops (Weizen, Kartoffeln, Karotten, Rüben),
Netherwarze und Kakao; unreife Pflanzen bleiben unberührt. Fortune auf dem gehaltenen Werkzeug wirkt wie
beim Abbau. Kein Client nötig, kein neues Item — nur ein Klick weniger Frust.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/ernte` | Status + aktuelle Werte |
| `/ernte on \| off` | Mod an/aus |
| `/ernte set nether on\|off` | Netherwarze einschließen (Default: **on**) |
| `/ernte set kakao on\|off` | Kakao einschließen (Default: **on**) |
| `/ernte set fortune on\|off` | Fortune wirkt bei Klick-Ernte (Default: **on**) |
| `/ernte set sound on\|off` | Ernte-Sound (Default: **on**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/ernte.json`, live per `/ernte reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | **Sehr hoch** (einer der meistinstallierten QoL-Wünsche überhaupt) |
| Gibt’s das schon? / Mehrwert | „Right Click Harvest“ u. a. existieren. Mehrwert: bewusst minimal (keine Hoe-Pflicht, keine Extra-Mechanik), rein server-seitig, Horsti-Command-Schema. Öffentlich nur bei echtem Mehrwert nach Playtest |
| Geschätzter Aufwand | Leicht–mittel (generische Crop-Erkennung über Block-Properties) |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
