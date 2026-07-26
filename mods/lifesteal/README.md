# Horsti Lifesteal

**`horsti-lifesteal` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Spieler-Kill klaut ein Herz. Wer auf null fällt, ist raus — bis ihn jemand wiederbelebt.

## Was macht der Mod?

Das bekannte Lifesteal-SMP-Format, aber fair und reversibel: Tötet Spieler A Spieler B, wandert ein
Maximal-Herz von B zu A (Server-Ansage mit beiden Ständen). Fällt jemand auf die Mindest-Herzenzahl, wird er
**Spectator statt gebannt** — und bleibt es auch über Neustarts hinweg.

**Wiederbelebung:** Jeder Spieler kann `/revive <Name>` benutzen und zahlt dafür ein **Herz-Item** aus seinem
Inventar (Default: Netherstern, per `herzItem` auf jedes beliebige Item umstellbar — z. B. auf einen
Diamantblock, wenn es günstiger sein soll). Admins nutzen `/lifesteal revive <Name>` ohne Kosten.

Natürliche Tode (Mobs, Lava) kosten wahlweise ebenfalls ein Herz oder sind frei.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/lifesteal` | Status + aktuelle Werte |
| `/lifesteal on \| off` | Mod an/aus (Herzen werden eingefroren, nicht gelöscht) |
| `/lifesteal set startHerzen <5–30>` | Start-Maximum (Default: **10**) |
| `/lifesteal set maxHerzen <10–40>` | Obergrenze (Default: **20**) |
| `/lifesteal set minHerzen <0–5>` | Ausscheide-Schwelle (Default: **0**) |
| `/lifesteal set natTod herz\|frei` | Kostet ein Nicht-PvP-Tod ein Herz? (Default: **frei**) |
| `/lifesteal set herzItem <item-id>` | Item, das als Herz zählt (Default: **minecraft:nether_star**) |
| `/lifesteal revive <Spieler>` | Admin-Revive ohne Kosten |
| `/lifesteal setze <Spieler> <herzen>` | Herzen direkt setzen |

Für alle Spieler: **`/herzen`** (eigener Stand) und **`/revive <Spieler>`** (kostet ein Herz-Item).

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/lifesteal.json`, live per `/lifesteal reload`. Herzen-Stände: `config/horsti/daten/lifesteal.json`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | **Sehr hoch** (eines der populärsten SMP-Formate) |
| Gibt’s das schon? / Mehrwert | Viele Varianten, teils Client-Pflicht, teils Ban-basiert. Mehrwert: Spectator statt Ban, Revive gegen frei wählbares Item, alles live tunebar. Öffentlich nur bei echtem Mehrwert nach Playtest |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
