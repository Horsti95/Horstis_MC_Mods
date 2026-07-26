# Horsti Mobgriefing

**`horsti-mobgriefing` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> `mobGriefing` is all-or-nothing. Here you set it **per mob type**: creepers no, villagers yes.

## What it does

Vanilla gives you one game rule: turn `mobGriefing` off and you also lose villager farms and
grass-eating sheep. This mod decouples them — each mob category has its own switch:

| Switch | Controls | Default |
|---|---|---|
| `creeper` | Creeper explosions destroy blocks | **off** ✋ |
| `enderman` | Endermen pick up blocks | **off** ✋ |
| `ghast` | Ghast fireballs destroy blocks | on |
| `wither` | Wither explosions destroy blocks | on |
| `villager` | Villagers harvest and plant (farms!) | on |
| `schaf` | Sheep eat grass | on |

The vanilla game rule stays untouched at `true`; the mod only intercepts the specific cases.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/mobgriefing` | Status: table of all switches |
| `/mobgriefing on \| off` | Enable/disable (off = pure vanilla) |
| `/mobgriefing set <switch> on\|off` | Toggle a single mob type (see table) |
| `/mobgriefing reset` | Restore defaults |

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder, or
into a single-player instance. Other players need nothing.

## Configuration

`config/horsti/mobgriefing.json`, reloadable with `/mobgriefing reload`.

## Known limitation

For the wither, only explosions are covered in v1 — its block-chewing behaviour hangs off a class that
moved in the 26.x rewrite. Ravagers, silverfish and foxes stay vanilla for now and can be added later.

## Fact sheet

| | |
|---|---|
| Community demand | Medium–high — a classic feedback-portal request ("per-mob game rule") |
| Does this exist? / our edge | Scattered across datapacks and Bukkit plugins. Our edge: one command, a clear table, a small Fabric mod that does only this |
| Estimated effort | Low–medium |
| Target | SP + MP |
| Horsti priority | TBD |
