# Horsti Gift & Burden

**`horsti-giftburden` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Every player gets one fixed random pair: a gift and a burden. Who are you?

## What it does

On first join, every player draws a **gift** and a **burden** from two separate pools — permanent,
persisted, and announced with a title screen ("You are: Nimble, but greedy"). Everything is built from
plain vanilla attributes, so no client installation and no new content:

**Gifts:** Tough (+health) · Nimble (+speed) · Armoured (+armour) · Hard-hitting (+attack damage) ·
Miner (+block break speed) · Feather-light (+safe fall distance) · Padded (+absorption) ·
Steadfast (+knockback resistance)

**Burdens:** fragile (−health) · sluggish (−speed) · soft (−armour) · weak (−attack damage) ·
blunt at digging (−break speed) · glass-boned (+fall damage) · greedy (drains hunger faster) ·
hydrophobic (slowness in water)

That is 64 combinations. Players can reroll for a configurable price, admins can reroll anyone for free.
Think Origins, but without Origins, without a client mod and without new assets.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/gift` | Status and current values |
| `/gift on \| off` | Enable/disable (attributes are cleanly removed and restored) |
| `/gift reroll <player>` | Admin reroll, free |
| `/gift set strength <1–3>` | Strength multiplier for all gifts and burdens (default: **1**) |
| `/gift set rerollCost xp30\|netherstar\|off` | Player reroll price (default: **xp30**) |
| `/gift set announce on\|off` | Title announcement on join (default: **on**) |

Any player: **`/mygift`** (see your own pair) and **`/giftreroll`** (reroll for the set price).

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder, or
into a single-player instance. Other players need nothing.

## Configuration

`config/horsti/gift.json`, reloadable with `/gift reload`.
Player assignments live in `config/horsti/daten/gift.json`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — Origins is huge, and "random light" variants are popular for SMP starts |
| Does this exist? / our edge | "Origins Randomiser" requires Origins and therefore a client mod. Our edge: dependency-free, vanilla clients, persistent, reroll as a gameplay mechanic |
| Estimated effort | Medium |
| Target | SP + MP |
| Horsti priority | TBD |
