# Horsti Totem

**`horsti-totem` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> The Totem of Undying works from your inventory — not just from your hand.

## What it does

In vanilla a totem only saves you from the main hand or off hand. With this mod either the hotbar or
the whole inventory counts; the totem is consumed as usual (first one found, vanilla effects including
particles and sound). An optional cooldown prevents totem chains in PvP rounds.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/totem` | Status and current values |
| `/totem on \| off` | Enable/disable |
| `/totem set scope hotbar\|inventory` | Where totems count (default: **inventory**) |
| `/totem set cooldownSeconds <0–600>` | Lockout after a save, 0 = off (default: **0**) |
| `/totem set announce on\|off` | Short action bar message "Totem from your inventory!" (default: **on**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/totem.json`, reloadable with `/totem reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a recurring wish that splits balance purists, hence toggleable + cooldown |
| Does this exist? / our edge | Widespread as a datapack. Our edge: cleanly toggleable, cooldown option, PvP-ready |
| Estimated effort | Low |
| Target | SP + MP |
| Horsti priority | TBD |
