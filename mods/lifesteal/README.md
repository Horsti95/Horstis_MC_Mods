# Horsti Lifesteal

**`horsti-lifesteal` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> A player kill steals a heart. Drop to zero and you are out — until somebody revives you.

## What it does

The well-known lifesteal SMP format, but fair and reversible: if player A kills player B, one maximum
heart moves from B to A (announced to the server with both standings). Fall to the minimum heart count and
you become a **spectator instead of getting banned** — and stay one across restarts.

**Reviving:** any player can use `/revive <name>` and pays with a **heart item** from their inventory
(default: a nether star, changeable to any item via `heartItem` — a diamond block, say, if it should be
cheaper). Admins use `/lifesteal revive <name>` at no cost.

Natural deaths (mobs, lava) either cost a heart too or are free, your choice.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/lifesteal` | Status and current values |
| `/lifesteal on \| off` | Enable/disable (hearts are frozen, not deleted) |
| `/lifesteal set startHearts <5–30>` | Starting maximum (default: **10**) |
| `/lifesteal set maxHearts <10–40>` | Upper limit (default: **20**) |
| `/lifesteal set minHearts <0–5>` | Elimination threshold (default: **0**) |
| `/lifesteal set naturalDeath heart\|free` | Does a non-PvP death cost a heart? (default: **free**) |
| `/lifesteal set heartItem <item-id>` | Item that counts as a heart (default: **minecraft:nether_star**) |
| `/lifesteal revive <player>` | Admin revive at no cost |
| `/lifesteal hearts <player> <amount>` | Set someone's hearts directly |

For every player: **`/hearts`** (your own standing) and **`/revive <player>`** (costs a heart item).

*Why `/lifesteal hearts` and not `set`? `core` already generates a `/lifesteal set <parameter>` branch,
so the sub-command needs its own name.*

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder. Other players need nothing.

## Configuration

`config/horsti/lifesteal.json`, reloadable with `/lifesteal reload`.
Heart standings live in `config/horsti/daten/lifesteal.json`.

## Fact sheet

| | |
|---|---|
| Community demand | **Very high** — one of the most popular SMP formats |
| Does this exist? / our edge | Many variants, some requiring a client, some ban-based. Our edge: spectator instead of ban, revive against a freely chosen item, everything tunable live. Only worth publishing if the playtest shows real added value |
| Estimated effort | Medium |
| Target | MP |
| Horsti priority | TBD |
