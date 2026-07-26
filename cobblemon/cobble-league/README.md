# Cobblemon League

**`horsti-cobble-league` · 🖥️ Server-side · vanilla clients supported · Minecraft 1.21.1 + Cobblemon 1.7+ (Fabric)**

> Turn your Cobblemon server into a league: sign up, get paired, climb the table.

## What it does

Cobblemon gives you battles. This adds the competition around them:

- **Tournaments** — `/league tournament start`, players sign up, the mod pairs them, announces
  matches and advances the bracket as battles resolve
- **Season table** — every battle counts towards a persistent standings table with wins, losses and
  a simple rating
- **Catch statistics** — per player: species caught, shinies, legendaries, first-catch records —
  queryable with `/league stats`, and a server-wide `/league top`
- **Badges** — admins can award named badges for beating a gym leader (just a stored label plus an
  announcement, no new items)

Server-side, so participants join with a plain client. Everything is chat and command driven.

## In-game control

Players: `/league join` · `/league leave` · `/league stats [player]` · `/league top`

| Command (OP level 2) | Effect |
|---|---|
| `/league` | Status and current values |
| `/league on \| off` | Enable/disable |
| `/league tournament start \| stop` | Run a tournament |
| `/league badge give <player> <name>` | Award a badge |
| `/league season reset` | Start a new season (archives the old table) |
| `/league set anmeldungMin <1–60>` | Sign-up window in minutes (default: **10**) |
| `/league set minTeilnehmer <2–32>` | Minimum participants (default: **4**) |

## Installation

Drop the jar (+ Fabric API + Cobblemon) into the server's `mods` folder. Players need nothing beyond
Cobblemon itself.

## Configuration

`config/horsti/cobble-league.json`; standings and stats in `config/horsti/daten/cobble-league.json`.

## Fact sheet

| | |
|---|---|
| Community demand | Medium–high for community servers (a league is the reason to keep playing) |
| Does this exist? / our edge | The side-mod scene concentrates on alerts, stats and content. **Competition logic is thinly covered** — and it plays directly to our server-logic strength |
| Estimated effort | Medium–high (hooking Cobblemon's battle events, bracket state, persistence) |
| Target | MP |
| Horsti priority | TBD |
