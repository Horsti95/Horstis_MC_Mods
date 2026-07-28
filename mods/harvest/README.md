# Horsti Harvest

**`horsti-harvest` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Right-click a ripe crop: harvest and replant in one motion. The QoL classic.

## What it does

Right-clicking a fully grown plant harvests it and replants it immediately — one seed is taken out of the
drop for that. It works generically for all standard crops (wheat, potatoes, carrots, beetroot), nether
wart and cocoa; unripe plants are left alone. Fortune on the held tool applies just like it does when
breaking. No client needed, no new item — just one less click of frustration.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/harvest` | Status and current values |
| `/harvest on \| off` | Enable/disable |
| `/harvest set netherWart on\|off` | Include nether wart (default: **on**) |
| `/harvest set cocoa on\|off` | Include cocoa beans (default: **on**) |
| `/harvest set fortune on\|off` | Fortune applies to click harvesting (default: **on**) |
| `/harvest set sound on\|off` | Play a harvest sound (default: **on**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/harvest.json`, reloadable with `/harvest reload`.

## Fact sheet

| | |
|---|---|
| Community demand | **Very high** — one of the most installed QoL wishes there is |
| Does this exist? / our edge | "Right Click Harvest" and others exist. Our edge: deliberately minimal (no hoe requirement, no extra mechanics), purely server-side, in the Horsti command scheme. Only worth publishing if the playtest shows real added value |
| Estimated effort | Low–medium (generic crop detection via block properties) |
| Target | SP + MP |
| Horsti priority | TBD |
