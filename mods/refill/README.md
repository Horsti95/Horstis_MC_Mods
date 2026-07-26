# Horsti Refill

**`horsti-refill` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Placed your last torch? The next stack slides into your hand automatically.

## What it does

When the stack in your main hand runs out while placing blocks or using items, the mod moves an
identical stack from your inventory into the same slot. Building a long wall no longer means opening
the inventory every 64 blocks.

Works for blocks, torches, food and any other stackable item. If no matching stack is left, nothing
happens — exactly like vanilla.

Server-side, so it works for every player on the server without anyone installing anything.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/refill` | Status and current values |
| `/refill on \| off` | Enable/disable |
| `/refill set bloecke on\|off` | Refill placeable blocks (default: **on**) |
| `/refill set essen on\|off` | Refill food (default: **on**) |
| `/refill set werkzeug on\|off` | Swap in an identical tool when one breaks (default: **off**) |
| `/refill set hotbarNur on\|off` | Only pull from the hotbar (default: **off**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/refill.json`, reloadable with `/refill reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium–high (a building staple) |
| Does this exist? / our edge | Mostly client-side (Inventory Profiles Next and friends). Our edge: server-side, so it applies to everyone, and it is a single toggle |
| Estimated effort | Low–medium |
| Target | SP + MP |
| Horsti priority | TBD |
