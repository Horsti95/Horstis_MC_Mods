# Horsti Gabe & Bürde

**`horsti-gabe-buerde` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel**

> Jeder Spieler bekommt ein festes, zufälliges Paar: eine Gabe und eine Bürde. Wer bist du?

## Was macht der Mod?

Beim ersten Join zieht jeder Spieler (seed-fest, also reproduzierbar pro Welt+Name) eine **Gabe** und eine
**Bürde** aus getrennten Pools — nur Vanilla-Mechanik, z. B.:

- **Gaben:** +2 Herzen · +10 % Speed · stärkerer Sprung · schnellerer Abbau · Wasseratmung · zäher Magen
  (Essen sättigt mehr) · Feder-Fall
- **Bürden:** −2 Herzen · −10 % Speed · mehr Hunger · Skelette zielen besser auf dich · kein Sprint-Sprung-
  Bonus · Angst im Dunkeln (kurze Slowness bei Licht < 4)

Die Kombination wird beim Join per Titel angezeigt („Du bist: Flink, aber verfressen“). Ein **Reroll** ist
über ein konfigurierbares Opfer möglich (Default: 30 XP-Level an einem Verzauberungstisch entwerten —
alternativ Nether-Stern, einstellbar). Origins-artig, aber ohne Origins, ohne Client, ohne neue Assets.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/gabe` | Status + aktuelle Werte |
| `/gabe on \| off` | Mod an/aus (Attribute werden sauber entfernt/wiederhergestellt) |
| `/gabe reroll <Spieler>` | Admin-Reroll ohne Kosten |
| `/gabe set staerke <1–3>` | Wirkungsgrad aller Gaben/Bürden (Default: **1**) |
| `/gabe set rerollKosten xp30\|netherstern\|aus` | Spieler-Reroll-Preis (Default: **xp30**) |
| `/gabe set ansage on\|off` | Titel-Ansage beim Join (Default: **on**) |

Für alle Spieler: **`/meinegabe`** (eigene Kombination) und **`/gabereroll`** (neu würfeln gegen den
eingestellten Preis).

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/gabe-buerde.json`, live per `/gabe reload`. Pools sind in der Config erweiter-/kürzbar.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel (Origins ist riesig; „Random-Light“-Varianten gefragt für SMP-Starts) |
| Gibt’s das schon? / Mehrwert | „Origins Randomiser“ braucht Origins + Client. Mehrwert: dependency-frei, Vanilla-Clients, seed-fest, Reroll als Spielmechanik |
| Geschätzter Aufwand | Mittel |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
