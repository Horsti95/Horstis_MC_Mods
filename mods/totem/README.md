# Horsti Totem

**`horsti-totem` · QoL · server-seitig · Vanilla-Clients kompatibel**

> Das Totem der Unsterblichkeit wirkt aus dem Inventar — nicht nur aus der Hand.

## Was macht der Mod?

Vanilla rettet dich ein Totem nur in Haupt- oder Nebenhand. Mit diesem Mod zählt wahlweise die Hotbar
oder das ganze Inventar; das Totem wird wie gewohnt verbraucht (erstes gefundene, Vanilla-Effekte inklusive
Partikel/Sound). Optionaler Cooldown verhindert Totem-Ketten in PvP-Runden.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/totem` | Status + aktuelle Werte |
| `/totem on \| off` | Mod an/aus |
| `/totem set bereich hotbar\|inventar` | Wo Totems zählen (Default: **inventar**) |
| `/totem set cooldownSek <0–600>` | Sperrzeit nach Auslösung, 0 = aus (Default: **0**) |
| `/totem set ansage on\|off` | Kurze Actionbar-Meldung „Totem aus dem Inventar!“ (Default: **on**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/totem.json`, live per `/totem reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (regelmäßiger Wunsch, spaltet Balance-Puristen → deshalb togglebar + Cooldown) |
| Gibt’s das schon? / Mehrwert | Als Datapack verbreitet. Mehrwert: sauber togglebar, Cooldown-Option, PvP-tauglich |
| Geschätzter Aufwand | Leicht |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
