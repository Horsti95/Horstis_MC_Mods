# Horsti Tag

**`horsti-tag` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Tag, in Minecraft: "It" glows, runs faster — and really wants to touch you.

## What it does

One player is picked at random to be **It** (glowing and faster, announced to everyone). Hit another
player and the role passes on, with a short grace period so it does not ping-pong. A boss bar shows the
remaining round time. Whoever is It when the round ends loses; everyone else scores points for the time
they spent *not* being It, and the top three are announced.

The tag hit does not have to hurt — by default the damage is cancelled and only the touch counts.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/tag` | Status and current values |
| `/tag start` | Start a round, random "It" |
| `/tag stop` | End the round cleanly (effects removed, scores announced) |
| `/tag set rundenMin <1–60>` | Round length in minutes (default: **10**) |
| `/tag set esSpeed <0–2>` | Speed level for "It", 0 = off (default: **1**) |
| `/tag set esGlow on\|off` | "It" glows (default: **on**) |
| `/tag set schutzSek <0–30>` | Grace period after a handover (default: **5**) |
| `/tag set schaden on\|off` | Tag hits deal real damage (default: **off**) |

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder.
Other players need nothing — one command and the game night starts.

## Configuration

`config/horsti/tag.json`, reloadable with `/tag reload`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a party classic everyone understands instantly |
| Does this exist? / our edge | **Barely available as a lightweight Fabric server mod** (usually a minigame-server feature). Our edge: one command to start, playable on any survival world, no lobby or arena needed |
| Estimated effort | Low–medium |
| Target | MP |
| Horsti priority | TBD |
