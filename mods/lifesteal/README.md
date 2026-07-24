# Horsti Lifesteal

**`horsti-lifesteal` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Spieler-Kill klaut ein Herz. Wer auf null fällt, ist raus — bis ihn jemand wiederbelebt.

## Was macht der Mod?

Das bekannte Lifesteal-SMP-Format, aber fair und reversibel: Tötet Spieler A Spieler B, wandert ein
Maximal-Herz von B zu A. Fällt jemand auf die Mindest-Herzenzahl, wird er **Spectator** (kein Ban!) und
hinterlässt am Todesort ein **Herz-Item**. Jeder kann Herzen craften (teures Vanilla-Rezept, konfigurierbar)
und überschüssige Herzen als Item auswerfen — damit lassen sich Ausgeschiedene **wiederbeleben**
(`/lifesteal revive <Name>` gegen ein Herz oder Herz-Item ans „Grab“ bringen). Natürliche Tode (Mobs, Lava)
kosten wahlweise ebenfalls ein Herz oder sind frei.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/lifesteal` | Status + aktuelle Werte |
| `/lifesteal on \| off` | Mod an/aus (Herzen werden eingefroren, nicht gelöscht) |
| `/lifesteal set startHerzen <5–30>` | Start-Maximum (Default: **10**) |
| `/lifesteal set maxHerzen <10–40>` | Obergrenze (Default: **20**) |
| `/lifesteal set minHerzen <0–5>` | Ausscheide-Schwelle (Default: **0**) |
| `/lifesteal set natTod herz\|frei` | Kostet ein Nicht-PvP-Tod ein Herz? (Default: **frei**) |
| `/lifesteal set craften on\|off` | Herz-Rezept aktiv (Default: **on**) |
| `/lifesteal revive <Spieler>` | Admin-Revive mit einem Herz aus dem eigenen Vorrat |

Für alle Spieler: `/herzen` zeigt den eigenen Stand; Rezeptbuch zeigt das Herz-Rezept.

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/lifesteal.json`, live per `/lifesteal reload` (inkl. Herz-Rezept-Zutaten).

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | **Sehr hoch** (eines der populärsten SMP-Formate) |
| Gibt’s das schon? / Mehrwert | Viele Varianten, teils Client-Pflicht, teils Ban-basiert. Mehrwert: Spectator statt Ban, Revive-Ökonomie, alles live tunebar. Öffentlich nur bei echtem Mehrwert nach Playtest |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
