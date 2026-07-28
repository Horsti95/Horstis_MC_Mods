# Horsti Killmagnet

**`horsti-killmagnet` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> What you kill is yours: the drops from your kills come straight to you.

## What it does

Kill a mob and its drops and XP do not land on the ground — they come to you, either as a "magnet"
(items visibly fly over) or straight into your inventory (full inventory = they drop normally).
This applies to **your** kills only; ambient drops from mining or other causes stay vanilla.
It is not a vacuum-cleaner mod.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/killmagnet` | Status and current values |
| `/killmagnet on \| off` | Enable/disable |
| `/killmagnet set mode magnet\|inventory` | Flight animation or straight pickup (default: **magnet**) |
| `/killmagnet set xp on\|off` | Pull in experience too (default: **on**) |
| `/killmagnet set playerKills on\|off` | Also on PvP kills (default: **off**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/killmagnet.json`, reloadable with `/killmagnet reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium–high — auto-pickup is a popular QoL feature |
| Does this exist? / our edge | Item magnets exist as permanent vacuums. Our edge: **tied to kills** — original, and much easier on the balance |
| Estimated effort | Trivial |
| Target | SP + MP |
| Horsti priority | TBD |
