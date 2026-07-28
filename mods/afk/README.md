# Horsti AFK

**`horsti-afk` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> Stand still for a while and the tab list says so.

## What it does

No movement, no chat, no click for X minutes → the player name in the tab list turns grey and gets an
`[AFK]` suffix, plus an optional low-key chat message. Any activity clears the status immediately.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/afk` | Status and current values |
| `/afk on \| off` | Enable/disable |
| `/afk set minutes <1–60>` | Inactivity before AFK (default: **5**) |
| `/afk set announce on\|off` | Chat message on going AFK / returning (default: **on**) |
| `/afk set kickMinutes <0–120>` | Kick after that many more minutes, 0 = never (default: **0**) |

Any player can mark themselves AFK by hand with `/afk!`.

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder.
Other players need nothing.

## Configuration

`config/horsti/afk.json`, reloadable with `/afk reload`. Every value is also reachable by command.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a standard SMP request |
| Does this exist? / our edge | Part of Essentials-style bundles. Our edge: a tiny standalone with no baggage, purely tab-list based |
| Estimated effort | Trivial |
| Target | MP |
| Horsti priority | TBD |
