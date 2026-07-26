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
- 🐟 **Angler**, 🌾 **Farmer**, 🧗 **Climber** … (categories are individually toggleable)

Every category names a winner and their number. Ties are listed together. Players who joined this week
are included from their first day, so nobody looks bad for being new.

Nothing is rendered, nothing is drawn — it is chat text built from data the game already collects.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/wrapped` | Show the current standings right now |
| `/wrapped on \| off` | Enable/disable the automatic weekly announcement |
| `/wrapped now` | Trigger the weekly announcement immediately (and reset the period) |
| `/wrapped set intervallTage <1–30>` | Days between announcements (default: **7**) |
| `/wrapped set kategorien <n>` | How many categories to announce (default: **6**) |
| `/wrapped set minSpieler <1–16>` | Minimum players before announcing (default: **2**) |
| `/wrapped set kategorie <name> on\|off` | Toggle a single category |

Any player can use `/wrapped` to see the standings.

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/wrapped.json`, reloadable with `/wrapped reload`.
Period data lives in `config/horsti/daten/wrapped.json` (snapshot of each player's stats at period start).

## Fact sheet

| | |
|---|---|
| Community demand | Medium — but a strong "wow" moment on any SMP |
| Does this exist? / our edge | Only as Bukkit plugins (PlayerStats) and external web dashboards; **no Fabric server mod found**. Our edge: zero setup, announces itself, runs anywhere including Aternos |
| Estimated effort | Medium (statistics API + period snapshots) |
| Target | SP + MP |
| Horsti priority | TBD |
