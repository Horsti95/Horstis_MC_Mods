# Horsti Bounty

**`horsti-bounty` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> A bounty on a random player: they glow, everyone hunts, the killer collects.

## What it does

At configurable intervals a random online player becomes the target: a server announcement, glowing on the
target, and a boss bar timer. Kill the target inside the window and you get the reward (a configurable
item). If the target survives, it collects the reward itself — the survival bonus. Targets stay fair:
nobody is drawn twice in a row, and freshly joined players get a grace period.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/bounty` | Status and current values |
| `/bounty on \| off` | Enable/disable the automation |
| `/bounty now [<player>]` | Start a bounty right away (random or specific) |
| `/bounty reward <item> <amount>` | Set the reward, with item autocompletion (default: **3× diamond**) |
| `/bounty set intervalMinutes <10–240>` | Gap between bounties (default: **45**) |
| `/bounty set durationMinutes <5–60>` | Hunting window (default: **15**) |
| `/bounty set glow on\|off` | The target glows (default: **on**) |
| `/bounty set minPlayers <2–16>` | Automatic start needs this many online players (default: **3**) |
| `/bounty set survivalReward on\|off` | The target collects if it survives (default: **on**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder. Other players need nothing.

## Configuration

`config/horsti/bounty.json`, reloadable with `/bounty reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a popular SMP seasoning |
| Does this exist? / our edge | Covered by "Bounty Hunt" and "Spoorn Bounty Mobs", so we keep this one private |
| Estimated effort | Low–medium |
| Target | MP |
| Horsti priority | TBD |
