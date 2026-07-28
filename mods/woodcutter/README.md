# Horsti Woodcutter

**`horsti-woodcutter` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> The stonecutter finally cuts wood: log in, planks, stairs, slabs and fences out.

## What it does

Adds stonecutter recipes for wood: log → planks, planks → slabs, stairs, fences, fence gates, doors,
trapdoors, buttons, pressure plates and signs. The recipes ship as **three built-in datapacks**
(base / doors / redstone) and are synced to clients automatically — vanilla clients simply see them in the
stonecutter. No new blocks, no new assets.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/woodcutter` | Status and current values |
| `/woodcutter on \| off` | Recipes active/inactive (reloads the recipe list live) |
| `/woodcutter set doors on\|off` | Also doors and trapdoors (default: **on**) |
| `/woodcutter set redstone on\|off` | Also buttons and pressure plates (default: **on**) |

*How it works: the toggles switch the built-in packs on and off and reload the recipes live, so the change
reaches every player without a restart. Yields follow vanilla (log → 4 planks).*

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/woodcutter.json`, reloadable with `/woodcutter reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium–high — the evergreen "why can't the stonecutter do wood?" |
| Does this exist? / our edge | Widespread as a datapack. Our edge: toggleable live in-game, split into three parts you can enable separately |
| Estimated effort | Low |
| Target | SP + MP |
| Horsti priority | TBD |
