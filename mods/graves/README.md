# Horsti Graves

**`horsti-graves` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> No more despawning items: dying leaves a grave holding your inventory — protected, findable, fair.

## What it does

When you die, a grave appears at the spot, made of **two stacked vanilla chests** (54 slots — more than
your inventory can hold, so nothing is lost). Because they are real chests, Minecraft stores the contents
itself: enchantments, durability and names survive untouched, server restarts included.

You open it by right-clicking as normal. For a protection window **only the owner** can open the grave —
after that it is open to everyone, which is the loot incentive. The first time you open your own grave you
get your XP back (the share is configurable). Die in the void or in lava and the grave moves up to the
nearest safe spot.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/graves` | Status and current values |
| `/graves on \| off` | Enable/disable (off = vanilla drop) |
| `/graves set protectMinutes <0–120>` | Owner-only protection, 0 = open immediately (default: **15**) |
| `/graves set xpKept <0–100>` | Percent of your XP stored in the grave (default: **100**) |
| `/graves set announce on\|off` | Show the grave coordinates on death (default: **on**) |
| `/graves list` | Active graves with coordinates |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/graves.json`, reloadable with `/graves reload`. Grave metadata lives in
`config/horsti/daten/graves.json` — the items themselves sit in the chests, i.e. in the world.

## Note on overlap

The death-coordinates announcement deliberately overlaps with
[`horsti-deathpoint`](../deathpoint) — if you run Graves you do not need Deathpoint, and the other way
round. The two do **not** detect each other; running both simply means the message appears twice, so turn
one of them off with `/graves set announce off` or `/deathpoint off`.

## Fact sheet

| | |
|---|---|
| Community demand | **Very high** — losing items is frustration source number one |
| Does this exist? / our edge | "Universal Graves" (Polymer) is strong. Our edge: dependency-free, deliberately lean, and the loot incentive after the protection window as our own twist. Only worth publishing if the playtest shows real added value |
| Estimated effort | Medium (edge cases: void, lava, grief protection, repeated deaths) |
| Target | SP + MP |
| Horsti priority | TBD |
