# Horsti Nemesis ⭐

**`horsti-nemesis` · 🖥️ Server-side · vanilla clients supported · Minecraft 26.2 (Fabric)**

> The mob that killed you does not forget. It gets a name — and it comes back.

## What it does

When a mob kills you, it becomes **your nemesis**: it gets a generated name ("Klaus the Bonebreaker"),
it is remembered, and it never despawns. After a configurable delay it reappears 24–40 blocks away from
you — buffed, its level shown in its name, glowing briefly as it arrives — and it **hunts you
specifically**.

Kill you again, and it gains a level (up to a cap): more health, more damage. Beat it, and you get a
server-wide announcement and bonus XP (50 per level). Each player has exactly one nemesis at a time —
if another mob kills you while your arch-enemy is alive, the existing one stays, and only it can climb
the ranks.

Nemeses survive server restarts. Everything is built from vanilla mobs, vanilla effects and vanilla
attributes — no new entities, no textures, no models. The drama comes purely from logic: persistence,
naming, targeting and escalation.

## In-game control (OP level 2)

| Command | Effect |
|---|---|
| `/nemesis` | Status and current values |
| `/nemesis on \| off` | Enable/disable (existing nemeses go dormant) |
| `/nemesis list` | All active nemeses (owner, mob, level) |
| `/nemesis pardon <player>` | Remove a player's nemesis for good |
| `/nemesis set maxLevel <1–10>` | Escalation cap (default: **5**) |
| `/nemesis set hpPerLevel <10–50>` | % bonus health per level (default: **25**) |
| `/nemesis set damagePerLevel <10–50>` | % bonus damage per level (default: **20**) |
| `/nemesis set returnMinutes <1–60>` | Minutes until it returns (default: **10**) |
| `/nemesis set announce on\|off` | Server announcements (default: **on**) |

Any player: **`/mynemesis`** shows their own nemesis (name, level, mob type).

## Installation

Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into the server's `mods` folder, or
into a single-player instance. Other players need nothing — they can join with a plain vanilla client.

## Configuration

`config/horsti/nemesis.json`, reloadable with `/nemesis reload`.
Nemesis data lives in `config/horsti/daten/nemesis.json`.

## Fact sheet

| | |
|---|---|
| Community demand | Niche but loud — "a nemesis system in Minecraft" is a recurring wish |
| Does this exist? / our edge | Only as a large custom-mob mod (Shadow-of-Mordor style, client-heavy). **As a lightweight server mod built on vanilla mobs: essentially unique — the biggest edge in this project** |
| Estimated effort | Medium–high (persistence, respawn logic, targeting, escalation) |
| Target | SP + MP |
| Horsti priority | TBD |
