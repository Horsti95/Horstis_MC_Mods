# Horsti Anvilfix

**`horsti-anvilfix` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> No more "Too Expensive!" — the longest-running anvil complaint in Minecraft, finally adjustable.

## What it does

Removes the "Too Expensive" wall (vanilla blocks anything above 40 levels) and makes anvil costs
configurable. The prior-work penalty — every repair doubling future costs — can be softened to linear
growth or frozen entirely.

This is pure server-side logic: the anvil menu is calculated on the server, so vanilla clients simply
see the new prices. Nobody needs to install anything.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/anvilfix` | Status and current values |
| `/anvilfix on \| off` | Enable/disable (off = vanilla behaviour) |
| `/anvilfix set maxCost <1–39>` | Cost cap in levels; 39 means "never too expensive" (default: **39**) |
| `/anvilfix set priorWork vanilla\|linear\|frozen` | Prior-work penalty (default: **linear**) |

*Why cap at 39? Vanilla clients refuse to take the result at 40 levels or more, client-side. Staying
below that means the fix works for everyone, including unmodded players. The effect: "Too Expensive"
no longer exists, and expensive operations cost at most `maxCost` levels.*

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder, or
into a single-player instance. Other players need nothing.

## Configuration

`config/horsti/anvilfix.json`, reloadable with `/anvilfix reload`.

## Fact sheet

| | |
|---|---|
| Community demand | **Very high** — one of the most persistent complaints about vanilla |
| Does this exist? / our edge | Usually part of larger anvil-rework mods, often with a client component. Our edge: a single-purpose mod, purely server-side, adjustable live without a restart |
| Estimated effort | Low |
| Target | SP + MP |
| Horsti priority | TBD |
