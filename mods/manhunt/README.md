# Horsti Manhunt

**`horsti-manhunt` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Runners want to slay the dragon. Hunters carry a compass that always points at the prey.

## What it does

The Dream format for your own round: hand out roles by command, and hunters get a **compass** at the start.
While a hunter holds it, they get a **bearing** every second: a direction arrow relative to their own view,
the distance in blocks — and if a runner is in another dimension, "is in the Nether/End" instead. `/target`
cycles through multiple runners.

*Why no compass needle? A needle points nowhere across dimensions and gives away no distance. The bearing
works everywhere, needs no item components, and is therefore more update-proof.*

There is a grace period at the start (hunters blinded and frozen, configurable). Win detection: the ender
dragon slain by a runner → runners win; all runners dead → hunters win (runner respawn is toggleable).

With [`horsti-hud`](../horstihud) installed the bearing becomes a permanent HUD line instead of an action
bar message. Without it, nothing changes.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/manhunt` | Status: roles and current values |
| `/manhunt runner add\|remove <player>` | Assign runners |
| `/manhunt hunter add\|remove <player>` | Assign hunters |
| `/manhunt start` | Start the game (grace period runs) |
| `/manhunt stop` | End the game cleanly |
| `/manhunt set graceSeconds <0–300>` | Hunter freeze at the start (default: **30**) |
| `/manhunt set runnerRespawn on\|off` | Runners may respawn (default: **off** = classic) |
| `/manhunt set bearingSeconds <1–10>` | Seconds between two bearings (default: **1**) |

For hunters: **`/target`** cycles to the next runner.

## Installation

Drop the jar (+ Fabric API) into the server's `mods` folder. Other players need nothing.

## Configuration

`config/horsti/manhunt.json`, reloadable with `/manhunt reload`.

## Fact sheet

| | |
|---|---|
| Community demand | High — a YouTube staple |
| Does this exist? / our edge | Several good server mods (Compass Manhunt among them). Our edge: tuned to our round — target cycling, cross-dimension bearing, grace period, respawn modes. Only worth publishing if the playtest shows real added value |
| Estimated effort | Medium |
| Target | MP |
| Horsti priority | TBD |
