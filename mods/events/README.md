# Horsti Events

**`horsti-events` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> The world strikes back: blood moon hordes, meteor showers, a closing border — as switchable modules.

## What it does

A lean event scheduler plus three independent event modules, each toggleable and each **extractable**
(see modularity below):

1. **Blood moon** — a marked phase (red title, wither sound): every 10 seconds a wave of aggressive mobs
   spawns around each player and immediately targets them. After the configured duration the blood moon
   ends with bonus XP for every survivor.
2. **Meteor shower** — for one minute, impacts land around each player (vanilla explosions, no new asset).
   Block damage is **off** by default so SMP bases stay intact — it is still dangerous to stand in the open.
3. **Shrinking border** — the world border closes evenly onto the target radius (battle-royale feel for
   round-based play). Manual start only; `/events cancel` restores the previous border.

Events fire randomly (automation, **off** by default) or manually by command. Multiplayer-fair: every event
announces itself 60 seconds ahead.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/events` | Status and current values |
| `/events on \| off` | Random automation on/off (default: **off**) |
| `/events trigger bloodmoon\|meteor\|border` | Start an event right away |
| `/events cancel` | End the running event cleanly |
| `/events set checkIntervalMinutes <5–240>` | Minutes between two rolls of the automation (default: **20**) |
| `/events set bloodMoonChance <0–100>` | % chance per roll (default: **15**) |
| `/events set meteorChance <0–100>` | % chance per roll (default: **10**) |
| `/events set bloodMoonMinutes <1–30>` | Blood moon duration (default: **8**) |
| `/events set bloodMoonPerWave <1–10>` | Mobs per wave and player (default: **3**) |
| `/events set meteorBlockDamage on\|off` | Impacts damage blocks (default: **off**) |
| `/events set borderMinutes <5–120>` + `borderTargetRadius <16–512>` | Shrink parameters (defaults: **30**, **64**) |

*Note: the automation rolls on real-time intervals rather than on day/night changes — 26.x no longer has a
public time-of-day API we could find. Upside: it works regardless of sleeping and `/time set`.*

## Modularity / extracting a module

Every event is its own module behind an interface (`HorstiEvent`: announce → start → tick → cleanUp) with no
cross-references — a module can be lifted into a standalone mod of its own in under an hour (say
"horsti-bloodmoon") if it deserves to shine alone. The scheduler is the shared infrastructure, which is why
these three are bundled here (the bundling rule from PLAN.md).

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder, or into a single-player instance.
Other players need nothing.

## Configuration

`config/horsti/events.json`, reloadable with `/events reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — blood moon mods are popular, and "a living world" is a standing wish |
| Does this exist? / our edge | Blood moon exists on its own, often with client visuals. Our edge: three events server-side, announced fairly, modular |
| Estimated effort | Medium |
| Target | SP + MP |
| Horsti priority | TBD |
