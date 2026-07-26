# Horsti Wrapped

**`horsti-wrapped` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Your server's week in numbers: who walked the farthest, who died the most, who mined a mountain.
> Announced every week — or on demand with `/wrapped`.

## What it does

Minecraft already tracks hundreds of statistics per player and never shows them to anyone. This mod
turns them into a weekly highlight reel, posted to chat with a headline per category:

- 🥾 **Wanderer** — most blocks walked
- ⛏️ **Miner** — most blocks mined
- ☠️ **Unlucky One** — most deaths
- ⚔️ **Hunter** — most mobs killed
- 🕐 **Night Owl** — most time played
- 🐟 **Angler** — most fish caught
- 🦘 **Jumping Bean** — most jumps
- 💔 **Punching Bag** — most damage taken

Every category names a winner and their number, ties are listed together, and the period resets after
each announcement. Players who joined mid-period are measured from their first day, so nobody looks
bad for being new.

Nothing is rendered and nothing is drawn — it is chat text built from data the game already collects.

## Categories are modules

Each category is a small self-contained module (`StatKategorie`): a name, the statistic to read, and
how to format the number. Adding one is a single line; turning one off is a single command. That also
means **other mods can register their own categories** — see below.

### Cross-mod: a Cobblemon season recap

The same engine can announce a **Cobblemon week**: most Pokémon caught, most shinies, most battles
won, biggest catch streak. Two things are worth knowing before that happens:

- Cobblemon runs on **Minecraft 1.21.1**, our mods on **26.2** — so the Cobblemon categories cannot
  live in this jar. They belong to [`cobble-league`](../../cobblemon/cobble-league), which brings its
  own copy of the recap engine for the 1.21.1 branch.
- The **format stays identical** (same categories concept, same weekly announcement), so a server
  running both feels like one system, and a league season and a server week can share one rhythm.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/wrapped` | Show the current standings right now |
| `/wrapped on \| off` | Enable/disable the automatic announcement |
| `/wrapped jetzt` | Announce immediately and start a new period |
| `/wrapped set intervallTage <1–30>` | Days between announcements (default: **7**) |
| `/wrapped set kategorien <3–8>` | How many categories to announce (default: **6**) |
| `/wrapped set minSpieler <1–16>` | Minimum players before announcing (default: **2**) |
| `/wrapped set nullwerte on\|off` | Include categories where everyone scored zero (default: **off**) |

Any player can use `/wrapped` to see the standings.

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/wrapped.json`, reloadable with `/wrapped reload`.
Period baselines live in `config/horsti/daten/wrapped.json` — a snapshot of every player's counters at
the start of the period, so the announcement always reports *this week*, not lifetime totals.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — but a strong "wow" moment on any SMP |
| Does this exist? / our edge | Only as Bukkit plugins (PlayerStats) and external web dashboards; **no Fabric server mod found**. Our edge: zero setup, announces itself, runs anywhere including Aternos |
| Estimated effort | Medium (statistics API + period snapshots) |
| Target | SP + MP |
| Horsti priority | TBD |
