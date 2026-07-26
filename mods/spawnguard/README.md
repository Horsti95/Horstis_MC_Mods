# Horsti Spawnguard

**`horsti-spawnguard` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Mark a zone, and hostile mobs stop spawning inside it — no carpet of torches required.

## What it does

`/spawnguard hier <radius>` marks a protected zone around your position. Inside it, hostile mobs no
longer spawn naturally. Everything else stays vanilla: spawners still work, mobs can still walk in,
and you can still fight them.

Useful for base interiors, farms and event arenas — and it lets builds stay dark without turning into
a zombie factory.

Zones survive restarts and can be listed and removed. A configurable limit prevents someone from
protecting the whole world.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/spawnguard` | Status and current values |
| `/spawnguard on \| off` | Enable/disable |
| `/spawnguard hier <radius>` | Create a zone at your position |
| `/spawnguard liste` | List all zones with coordinates |
| `/spawnguard entferne <nr>` | Remove a zone |
| `/spawnguard set maxRadius <8–128>` | Maximum radius per zone (default: **32**) |
| `/spawnguard set maxZonen <1–50>` | Maximum number of zones (default: **10**) |
| `/spawnguard set spawner on\|off` | Also suppress monster spawners (default: **off**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/spawnguard.json`, zones in `config/horsti/daten/spawnguard.json`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium |
| Does this exist? / our edge | Partly (claim plugins bundle it). Our edge: standalone, no claim system, one command |
| Estimated effort | Low–medium |
| Target | SP + MP |
| Horsti priority | TBD |
