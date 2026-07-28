# Horsti Deathswap

**`horsti-deathswap` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Every few minutes everyone swaps positions. Build the trap before you are standing in it yourself.

## What it does

The classic death-swap format: after a slightly randomised interval every participating player swaps
position at the same time, in a ring (A→B→C→A, shuffled — never back onto yourself). Die and you are out
(spectator); the last one standing wins. The countdown is optionally visible or hidden (surprise mode).
`/deathswap stop` mid-round ends it cleanly — announcement, spectators back to survival.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/deathswap` | Status and current values |
| `/deathswap start` | Start a round with every survival-mode player |
| `/deathswap stop` | End the round cleanly |
| `/deathswap set intervalMinutes <1–30>` | Base interval (default: **5**) |
| `/deathswap set jitterSeconds <0–120>` | ± random window on the interval (default: **60**) |
| `/deathswap set countdown on\|off` | Show the swap countdown (default: **off** — surprise!) |
| `/deathswap set minPlayers <2–16>` | Minimum participants (default: **2**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder. Other players need nothing.

## Configuration

`config/horsti/deathswap.json`, reloadable with `/deathswap reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium–high — a YouTube classic |
| Does this exist? / our edge | Exists as a datapack/mod hybrid. Our edge: live-tunable in the Horsti command scheme, and a ring swap that works for more than two players |
| Estimated effort | Low |
| Target | MP |
| Horsti priority | TBD |
