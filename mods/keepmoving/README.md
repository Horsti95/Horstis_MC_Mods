# Horsti Keepmoving

**`horsti-keepmoving` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Rest and you rust: standing still starts to hurt after a grace period. For high-tension rounds.

## What it does

Stop moving for X seconds (position ± a small tolerance) and a penalty of your choice starts ticking:
direct damage, hunger or a wither effect. An action bar warning counts down during the grace period
("Move! 3…2…1"). Meant as a round twist, not a permanent state — which is why the mod starts **off**
and gets armed per session by command.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/keepmoving` | Status and current values |
| `/keepmoving on \| off` | Armed / disarmed (default: **off**) |
| `/keepmoving set graceSeconds <3–120>` | Standing still before the penalty (default: **10**) |
| `/keepmoving set mode damage\|hunger\|wither` | Kind of penalty (default: **damage**) |
| `/keepmoving set strength <1–5>` | Penalty strength per second (default: **1**, = half a heart) |
| `/keepmoving set warning on\|off` | Action bar countdown (default: **on**) |

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder. Other players need nothing.

## Configuration

`config/horsti/keepmoving.json`, reloadable with `/keepmoving reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Low–medium — a niche twist, but an original party effect |
| Does this exist? / our edge | Barely anything out there — effectively a gap. Our edge: a finished, tunable round format |
| Estimated effort | Low |
| Target | MP (SP for self-challenges) |
| Horsti priority | TBD |
