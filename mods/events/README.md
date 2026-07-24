# Horsti Events

**`horsti-events` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Die Welt schlägt zurück: Blutmond-Horden, Meteoritenregen, schrumpfende Grenze — als schaltbare Module.

## Was macht der Mod?

Ein schlanker Event-Scheduler plus drei unabhängige Event-Module (je einzeln togglebar und **rauslösbar**,
siehe Modularität):

1. **Blutmond** — Eine markierte Nacht (roter Titel, Sound): deutlich mehr und aggressivere Mob-Spawns um
   die Spieler, Schlafen gesperrt, bei Morgengrauen Ende + Belohnungs-XP für Überlebende.
2. **Meteoritenregen** — Angekündigter Schauer aus Vanilla-Feuerbällen über einem Zufallsgebiet nahe eines
   Spielers: Spektakel + Flächenbrand-Gefahr; Einschlagskrater hinterlassen als Trostpreis Glowstone?
   Nein — **keine neuen Assets, kein Terrain-Umbau**: Standard-Explosionen, togglebarer Blockschaden.
3. **Schrumpfgrenze** — Weltgrenze zieht sich über eine konfigurierte Dauer zusammen (Battle-Royale-Gefühl
   für Runden-Spiele; als Dauer-SMP-Event ungeeignet und deshalb nur manuell startbar).

Events feuern zufällig (Automatik, Default **aus**) oder manuell per Command. Mehrspieler-fair: Events
kündigen sich 60 s vorher an.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/events` | Status + aktuelle Werte |
| `/events on \| off` | Zufalls-Automatik an/aus (Default: **off**) |
| `/events trigger blutmond\|meteor\|grenze` | Event sofort starten |
| `/events abbrechen` | Laufendes Event sauber beenden |
| `/events set blutmondChance <0–20>` | %-Chance pro Nacht (Default: **5**) |
| `/events set meteorChance <0–20>` | %-Chance pro Tag (Default: **3**) |
| `/events set meteorBlockschaden on\|off` | Feuerbälle beschädigen Blöcke (Default: **off**) |
| `/events set grenzeDauerMin <5–120>` + `grenzeZielRadius <16–512>` | Schrumpf-Parameter (Default: **30**, **64**) |

## Modularität / Rauslösen

Jedes Event ist ein eigenes Modul hinter einem Interface (`HorstiEvent`: announce → run → cleanup) ohne
Querbezüge — ein Modul lässt sich in unter einer Stunde in einen eigenen Standalone-Mod heben (z. B.
„horsti-blutmond“), falls es einzeln glänzen soll. Der Scheduler ist die gemeinsame Infrastruktur, deshalb
sind die drei hier gebündelt (Bündel-Regel aus PLAN.md).

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/events.json`, live per `/events reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (Blutmond-Mods beliebt; „lebendige Welt“ ist Dauerwunsch) |
| Gibt’s das schon? / Mehrwert | Blutmond existiert einzeln (oft Client-Visuals). Mehrwert: drei Events server-seitig, ankündigungs-fair, modular |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
