# Horsti Juggernaut

**`horsti-juggernaut` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> One against all — and the one is a tank. Balanced automatically by how many hunters there are.

## What it does

One player (chosen or random) becomes the Juggernaut: extra health scaled to the number of hunters,
plus resistance, plus strength that grows as the hunt goes on. A boss bar shows their health to
everyone, and they glow so the pack can find them.

The scaling is the point: a duel and a ten-player hunt both stay tense without anyone touching a
config. Hunters respawn, the Juggernaut does not. Hunters win by killing the Juggernaut; the Juggernaut
wins by taking down every hunter a configurable number of times.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/juggernaut` | Status and current values |
| `/juggernaut start random \| <player>` | Start a round |
| `/juggernaut stop` | End the round cleanly (buffs removed) |
| `/juggernaut set heartsPerHunter <1–10>` | Extra hearts per hunter (default: **4**) |
| `/juggernaut set strengthFrom <2–10>` | Strength I from this many hunters, II at double (default: **4**) |
| `/juggernaut set glow on\|off` | Juggernaut glows (default: **on**) |
| `/juggernaut set killQuota <1–5>` | Kills per hunter needed for the Juggernaut to win (default: **1**) |

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder.
Other players need nothing.

## Configuration

`config/horsti/juggernaut.json`, reloadable with `/juggernaut reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a format everyone knows from shooters and minigame servers |
| Does this exist? / our edge | Barely available as a lightweight Fabric server mod. Our edge: **auto-balancing by player count** instead of fixed numbers, so no retuning between rounds |
| Estimated effort | Medium |
| Target | MP |
| Horsti priority | TBD |
