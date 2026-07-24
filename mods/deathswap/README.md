# Horsti Deathswap

**`horsti-deathswap` · Minigame · server-seitig · Vanilla-Clients kompatibel**

> Alle paar Minuten tauschen alle Spieler die Positionen. Bau die Falle, bevor du selbst drinsteckst.

## Was macht der Mod?

Klassisches Death-Swap-Format: Nach einem (leicht zufälligen) Intervall tauschen alle teilnehmenden Spieler
gleichzeitig und ringförmig die Positionen (A→B→C→A, zufällig gemischt — nie zurück auf sich selbst).
Wer stirbt, scheidet aus (Spectator); Sieger ist, wer übrig bleibt. Countdown optional sichtbar oder
versteckt (Überraschungs-Modus). `/deathswap off` mitten in der Runde beendet sauber (Ansage, Spectator zurück).

## In-Game-Steuerung (OP-Level 2)

| Command | Wirkung |
|---|---|
| `/deathswap` | Status + aktuelle Werte |
| `/deathswap start [alle\|<Spieler…>]` | Runde starten (Default: alle Überlebensmodus-Spieler) |
| `/deathswap stop` | Runde sauber beenden |
| `/deathswap set intervallMin <1–30>` | Basis-Intervall (Default: **5**) |
| `/deathswap set zufallSek <0–120>` | ± Zufallsfenster aufs Intervall (Default: **60**) |
| `/deathswap set countdown on\|off` | Tausch-Countdown sichtbar (Default: **off** — Überraschung!) |
| `/deathswap set minSpieler <2–16>` | Mindestteilnehmer (Default: **2**) |

## Installation

Jar (+ Fabric API) in den `mods/`-Ordner des Servers. Mitspieler brauchen nichts.

## Konfiguration

`config/horsti/deathswap.json`, live per `/deathswap reload`.

## Steckbrief

| | |
|---|---|
| Community-Nachfrage | Mittel-hoch (YouTube-Klassiker) |
| Gibt’s das schon? / Mehrwert | Existiert als Datapack/Mod-Hybrid. Mehrwert: integrierte, live tunebare Version im Horsti-Command-Schema; Ring-Tausch für >2 Spieler |
| Geschätzter Aufwand | Leicht |
| Zielgruppe | MP |
| Horsti-Priorität | TBD |
