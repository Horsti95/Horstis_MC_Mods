# Horsti Pets

**`horsti-pets` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Nie wieder das eigene Haustier verprügeln oder verlieren: Schutz + Suchbefehl + Sammel-Kommandos.

## Was macht der Mod?

Drei Dinge, die Haustier-Besitzer seit Jahren wollen:

1. **Friendly-Fire-Schutz:** Du kannst deine eigenen gezähmten Tiere (Hund, Katze, Papagei, Pferd …) nicht
   mehr versehentlich schlagen — auch nicht mit Pfeil oder Trident (togglebar, z. B. fürs Schlachten).
2. **Wiederfinden:** `/pets find` lässt alle deine Tiere in der Nähe kurz glühen; `/pets liste` zeigt Art,
   Name und Entfernung deiner registrierten Tiere.
3. **Sammel-Kommandos:** `/pets stay` / `/pets follow` setzt alle deine Hunde/Katzen in der Nähe auf
   Sitzen/Folgen — ohne jedem einzeln hinterherzuklicken.

Alle Spieler dürfen die `/pets`-Befehle für ihre eigenen Tiere nutzen; die Einstellungen sind OP-Sache.

## In-Game-Steuerung

Für alle Spieler: `/pets liste` · `/pets find` · `/pets stay` · `/pets follow`

| Command (OP-Level 2) | Wirkung |
|---|---|
| `/pets` | Status + aktuelle Werte |
| `/pets on \| off` | Mod an/aus |
| `/pets set schutz on\|off` | Friendly-Fire-Schutz (Default: **on**) |
| `/pets set schutzSneak on\|off` | Sneak + Schlag umgeht den Schutz (Default: **on**) |
| `/pets set findGlowSek <5–60>` | Glow-Dauer bei `/pets find` (Default: **30**) |
| `/pets set radius <16–128>` | Wirkradius für find/stay/follow (Default: **48**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/pets.json`, live per `/pets reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (Haustier-QoL ist Dauerthema im Feedback-Portal) |
| Gibt’s das schon? / Mehrwert | Verstreut über große Pet-Mods (meist mit Client-Teil). Mehrwert: die drei wichtigsten Funktionen als Mini-Mod, rein server-seitig |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
