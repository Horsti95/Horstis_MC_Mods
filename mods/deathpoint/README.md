# Horsti Deathpoint

**`horsti-deathpoint` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> When you die you get your death coordinates in chat — privately, and clickable to copy.

## What it does

Die, and a line appears (only for you) like `☠ You died at 120 64 -338 (Overworld)` — one click copies
the coordinates to your clipboard. It complements the vanilla recovery compass rather than replacing it:
the line stays in your chat history, even after dying several times over.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/deathpoint` | Status and current values |
| `/deathpoint on \| off` | Enable/disable |
| `/deathpoint set broadcast on\|off` | Show the coordinates to everyone instead of privately (default: **off**) |
| `/deathpoint set dimension on\|off` | Include the dimension (default: **on**) |
| `/deathpoint reset` | Restore the defaults |

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder, or
into a single-player instance. Other players need nothing.

## Configuration

`config/horsti/deathpoint.json` — created automatically; file edits apply with `/deathpoint reload`.
Every value is also reachable by command (see above).

## Fact sheet

| | |
|---|---|
| Community demand | Medium — the evergreen "where did I die?" |
| Does this exist? / our edge | Part of larger mods, rarely as a mini standalone. Our edge: a 5 KB mod, toggleable, with clickable coordinates |
| Estimated effort | Trivial |
| Target | SP + MP |
| Horsti priority | TBD |
