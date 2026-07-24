# Horsti Nemesis ⭐

**`horsti-nemesis` · SMP-Twist · server-seitig · Vanilla-Clients kompatibel · Flaggschiff**

> Der Mob, der dich getötet hat, vergisst dich nicht. Er bekommt einen Namen — und kommt wieder.

## Was macht der Mod?

Tötet dich ein Mob, wird er zu deinem **Nemesis**: Er bekommt einen generierten Namen („Klaus der
Knochenbrecher“), despawnt nie und wird gespeichert. Nach deinem Respawn taucht er nach einer Weile in
deiner Nähe wieder auf — Level 1 gebufft (mehr HP, mehr Schaden, sichtbar am Namen + Glow-Moment bei
Ankunft) und **er jagt bevorzugt dich**. Tötet er dich erneut, steigt er ein Level (bis Maximum) und
bekommt pro Level einen Zusatz-Trick aus Vanilla-Bausteinen (Speed, Feuerresistenz, Rüstung anlegen,
Trank-Wurf bei Hexen …). Besiegst du ihn, gibt es Ruhm: Server-Ansage, Bonus-XP und seine Trophäe
(Mob-Kopf, wo Vanilla einen hat — sonst sein Namensschild). Pro Spieler existiert höchstens ein Nemesis
(konfigurierbar); Nemeses überleben Server-Restarts.

Nur Vanilla-Mobs, -Effekte und -Items — der ganze Reiz entsteht aus Logik: Persistenz, Naming, Targeting,
Eskalation.

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/nemesis` | Status + aktuelle Werte |
| `/nemesis on \| off` | Mod an/aus (bestehende Nemeses schlafen ein) |
| `/nemesis liste` | Alle aktiven Nemeses (Besitzer, Mob, Level, zuletzt gesehen) |
| `/nemesis begnadige <Spieler>` | Nemesis eines Spielers endgültig entfernen |
| `/nemesis set maxLevel <1–10>` | Eskalations-Deckel (Default: **5**) |
| `/nemesis set hpProLevel <10–50>` | % Bonus-HP je Level (Default: **25**) |
| `/nemesis set schadenProLevel <10–50>` | % Bonus-Schaden je Level (Default: **20**) |
| `/nemesis set rueckkehrMin <1–60>` | Minuten bis zur Rückkehr (Default: **10**) |
| `/nemesis set proSpieler <1–3>` | Max. Nemeses je Spieler (Default: **1**) |
| `/nemesis set ansagen on\|off` | Server-Ansagen (Rückkehr/Tod) (Default: **on**) |

Für alle Spieler: `/meinnemesis` zeigt den eigenen Nemesis (Name, Level, zuletzt gesehen).

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers bzw. der SP-Instanz. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/nemesis.json`, live per `/nemesis reload` (inkl. Namens-Silbenlisten fürs Naming).

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Nische, aber laut und begeistert („Nemesis-System in MC“ ist ein wiederkehrender Traum) |
| Gibt’s das schon? / Mehrwert | Nur als großes Custom-Mob-Mod („Nemesis System“, Client-lastig). **Als leichtes Vanilla-Mob-Server-Mod: quasi einzigartig — unser größter Mehrwert im ganzen Projekt** |
| Geschätzter Aufwand | Mittel–schwer (Persistenz, Respawn-Logik, Targeting, Eskalation) |
| Zielgruppe | SP + MP |
| Horsti-Priorität | TBD |
