# Horsti Pets

**`horsti-pets` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Never punch or lose your own pet again: protection, a find command, and bulk orders.

## What it does

Three things pet owners have wanted for years:

1. **Friendly-fire protection:** you can no longer hit your own tamed animals (dog, cat, parrot, horse …)
   by accident — arrows and tridents included. Toggleable, e.g. for deliberate slaughtering.
2. **Finding them again:** `/pets find` makes all your nearby pets glow briefly; `/pets list` shows the
   name and distance of every pet of yours in range.
3. **Bulk orders:** `/pets stay` and `/pets follow` set every nearby pet of yours to sitting or following
   — without clicking each one individually.

Any player may use the `/pets` commands for their own animals; the settings are an OP matter.

## In-game control

For every player: `/pets list` · `/pets find` · `/pets stay` · `/pets follow`

| Command (OP level 2) | Effect |
|---|---|
| `/pets` | Status and current values |
| `/pets on \| off` | Enable/disable |
| `/pets set protect on\|off` | Friendly-fire protection (default: **on**) |
| `/pets set sneakBypass on\|off` | Sneak + hit bypasses the protection (default: **on**) |
| `/pets set findGlowSeconds <5–60>` | Glow duration for `/pets find` (default: **30**) |
| `/pets set radius <16–128>` | Range for find/stay/follow (default: **48**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/pets.json`, reloadable with `/pets reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — pet QoL is a standing topic on the feedback site |
| Does this exist? / our edge | Scattered across big pet mods, usually with a client component. Our edge: the three most useful features as a mini mod, purely server-side |
| Estimated effort | Medium |
| Target | SP + MP |
| Horsti priority | TBD |
