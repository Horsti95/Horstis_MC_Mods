# Horsti Toolguard

**`horsti-toolguard` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Your netherite pickaxe will not break on the next swing. It simply refuses to be used —
> and tells you why.

## What it does

When a tool, weapon or piece of armour drops below a configurable durability threshold, the mod stops
it from being used for block breaking and attacks, and shows a warning in the action bar. The item
survives; you walk home and repair it.

Because this runs **on the server**, it protects everyone on the server — including friends who play
with a plain vanilla client. Existing solutions for this are almost all client-side, which means every
single player has to install and configure them separately.

Optional: only protect enchanted or high-tier items, so a stone shovel can still break normally.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/toolguard` | Status and current values |
| `/toolguard on \| off` | Enable/disable |
| `/toolguard set threshold <1–50>` | Remaining durability at which protection kicks in (default: **5**) |
| `/toolguard set enchantedOnly on\|off` | Protect only enchanted items (default: **off**) |
| `/toolguard set armor on\|off` | Protect armour too (default: **on**) |
| `/toolguard set warnFrom <0–200>` | Warn (without blocking) from this durability (default: **50**) |
| `/toolguard set sound on\|off` | Play a warning sound (default: **on**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/toolguard.json`, reloadable with `/toolguard reload`.

## Fact sheet

| | |
|---|---|
| Community demand | High — losing a maxed tool is a classic frustration |
| Does this exist? / our edge | Yes, but **almost exclusively client-side** (Anti Tool Break, Durability Alerts, Stop Breaking Tools). Our edge: server-side = protects everyone, configured once by the admin |
| Estimated effort | Low–medium |
| Target | SP + MP |
| Horsti priority | TBD |
