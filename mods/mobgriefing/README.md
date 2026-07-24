# Horsti Mobgriefing

**`horsti-mobgriefing` · QoL · server-seitig · Vanilla-Clients kompatibel**

> `mobGriefing` ist alles-oder-nichts. Hier stellst du es **pro Mob-Typ** ein: Creeper nein, Villager ja.

## Was macht der Mod?

Vanilla kennt nur eine globale Gamerule: `mobGriefing` aus heißt auch keine Villager-Farmen, keine
Schaf-Grasfresser. Dieser Mod entkoppelt das — jede Mob-Kategorie einzeln schaltbar:

| Schalter | Steuert | Default |
|---|---|---|
| `creeper` | Creeper-Explosionen zerstören Blöcke | **off** ✋ |
| `enderman` | Endermen tragen Blöcke weg | **off** ✋ |
| `ghast` | Ghast-Feuerbälle zerstören Blöcke | on |
| `wither` | Wither zerstört Blöcke | on |
| `villager` | Villager ernten/säen (Farmen!) | on |
| `schaf` | Schafe fressen Gras | on |
| `sonstige` | Alles Übrige (Ravager, Silverfish, Fuchs …) | on |

Die Vanilla-Gamerule bleibt unberührt auf `true`; der Mod fängt gezielt die Einzelfälle ab.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/mobgriefing` | Status: Tabelle aller Schalter |
| `/mobgriefing on \| off` | Mod an/aus (off = pures Vanilla) |
| `/mobgriefing set <schalter> on\|off` | Einzelnen Mob-Typ schalten (siehe Tabelle) |
| `/mobgriefing reset` | Defaults wiederherstellen |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/mobgriefing.json`, live per `/mobgriefing reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel-hoch (klassischer Feedback-Portal-Wunsch: „per-mob gamerule“) |
| Gibt’s das schon? / Mehrwert | Als Datapack/Plugin verstreut. Mehrwert: ein Command, klare Tabelle, Fabric-Mini-Mod |
| Geschätzter Aufwand | Leicht–mittel (mehrere saubere Eingriffspunkte) |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
