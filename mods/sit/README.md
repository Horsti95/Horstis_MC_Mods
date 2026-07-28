# Horsti Sit

**`horsti-sit` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Right-click a stair or a slab — and you sit down. Finally.

## What it does

Right-click a stair or slab with an empty hand and you sit on it (an invisible seat entity, so it works
for vanilla clients). Stand up by sneaking, moving or clicking again — you land safely on the block you
started from. Taking damage stands you up automatically. Optionally `/sitdown` lets you sit down on the
spot (available to all players, toggleable).

## In-game control

For every player: right-click a stair or slab · `/sitdown` (when enabled).

| Command (OP level 2) | Effect |
|---|---|
| `/sit` | Status and current values |
| `/sit on \| off` | Enable/disable (anyone sitting stands up cleanly) |
| `/sit set stairs on\|off` | Stairs are clickable (default: **on**) |
| `/sit set slabs on\|off` | Slabs are clickable (default: **on**) |
| `/sit set command on\|off` | Allow `/sitdown` for everyone (default: **off**) |
| `/sit set emptyHandOnly on\|off` | Only with an empty hand (default: **on** — stops misclicks while building) |
| `/sit set standUpOnDamage on\|off` | Stand up automatically when damaged (default: **on**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/sit.json`, reloadable with `/sit reload`.

## Fact sheet

| | |
|---|---|
| Community demand | High — an evergreen for over a decade |
| Does this exist? / our edge | "Polysit" (server-side, needs Polymer). Our edge: dependency-free, finer toggles, misclick protection. Only worth publishing if the playtest shows real added value |
| Estimated effort | Low–medium (edge cases: standing up, block removed, portals) |
| Target | SP + MP |
| Horsti priority | TBD |
