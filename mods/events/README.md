# Horsti Events

**`horsti-events` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Die Welt schlägt zurück: Blutmond-Horden, Meteoritenregen, schrumpfende Grenze — als schaltbare Module.

## Was macht der Mod?

Ein schlanker Event-Scheduler plus drei unabhängige Event-Module (je einzeln togglebar und **rauslösbar**,
siehe Modularität):

1. **Blutmond** — Eine markierte Phase (roter Titel, Wither-Sound): alle 10 Sekunden spawnt rund um jeden
   Spieler eine Welle aggressiver Mobs, die sofort auf ihn zielen. Nach der eingestellten Dauer endet der
   Blutmond mit Belohnungs-XP für alle Überlebenden.
2. **Meteoritenregen** — Eine Minute lang schlagen rund um jeden Spieler Einschläge ein (Vanilla-
   Explosionen, kein neues Asset). Blockschaden ist per Default **aus**, damit SMP-Basen heil bleiben —
   gefährlich ist es trotzdem, wer im Freien steht.
3. **Schrumpfgrenze** — Die Weltgrenze zieht sich gleichmäßig auf den Ziel-Radius zusammen (Battle-Royale-
   Gefühl für Runden-Spiele). Nur manuell startbar; `/events abbrechen` stellt die alte Grenze wieder her.

Events feuern zufällig (Automatik, Default **aus**) oder manuell per Command. Mehrspieler-fair: Jedes Event
kündigt sich 60 Sekunden vorher an.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/events` | Status + aktuelle Werte |
| `/events on \| off` | Zufalls-Automatik an/aus (Default: **off**) |
| `/events trigger blutmond\|meteor\|grenze` | Event sofort starten |
| `/events abbrechen` | Laufendes Event sauber beenden |
| `/events set pruefIntervallMin <5–240>` | Minuten zwischen zwei Würfen der Automatik (Default: **20**) |
| `/events set blutmondChance <0–100>` | %-Chance je Wurf (Default: **15**) |
| `/events set meteorChance <0–100>` | %-Chance je Wurf (Default: **10**) |
| `/events set blutmondDauerMin <1–30>` | Blutmond-Dauer (Default: **8**) |
| `/events set blutmondProWelle <1–10>` | Mobs je Welle und Spieler (Default: **3**) |
| `/events set meteorBlockschaden on\|off` | Einschläge beschädigen Blöcke (Default: **off**) |
| `/events set grenzeDauerMin <5–120>` + `grenzeZielRadius <16–512>` | Schrumpf-Parameter (Default: **30**, **64**) |

*Hinweis: Die Automatik würfelt in Echtzeit-Intervallen statt an Tag/Nacht-Wechseln — in 26.x gibt es keine
öffentliche Tageszeit-API mehr. Vorteil: funktioniert unabhängig von Schlafen und `/time set`.*

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
