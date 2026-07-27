# Horsti's MC Mods

Server-side Minecraft mods (Java, **Fabric, MC 26.2**) built on one idea:
**minimal assets, maximum logic impact** — rules and systems built from what the game already has.
Most of them run on the server alone, so **your friends join with a plain vanilla client**.

➡️ **[PLAN.md](PLAN.md) is the single source of truth**: mod list, status, roadmap (written in German).

## Install

1. **Get the jars:** latest [Actions build](../../actions/workflows/build.yml) → download the
   **`horsti-mods`** artifact and unpack it (or build locally with `./gradlew build`; jars land in
   `mods/<name>/build/libs/`).
2. **Put them on the server:** the `horsti-*.jar` files you want, plus
   [Fabric API](https://modrinth.com/mod/fabric-api), into the `mods` folder.
   On Aternos: install Fabric API, then upload the Horsti jars.
3. **Done.** Every mod runs immediately with sensible defaults, and other players install **nothing**.
   For single player, drop the jars into your own 26.2 Fabric instance.

Install only what you want — each mod stands alone and bundles `horsti-core` itself.

## In-game control

| Command | Effect |
|---|---|
| `/horsti` | Overview: every installed Horsti mod and its state |
| `/<mod>` | Status and current values of one mod |
| `/<mod> on` · `/<mod> off` | Enable/disable live |
| `/<mod> set <param> <value>` | Change a parameter live |
| `/<mod> reset` · `/<mod> reload` | Restore defaults / reload the config file |

Everything is OP level 2 and works from the server console (including the Aternos web console).
Values are stored in `config/horsti/<mod>.json`, saved game state in `config/horsti/daten/`.

> **Note:** commands and settings currently use German names (`/holzsaege`, `/gabe`, `rundenMin`).
> They will be renamed to English before any public release — see PLAN.md section 10.

## The mods

**Built and compiling (21)**

*Quality of life* — [`anvilfix`](mods/anvilfix) (no more "Too Expensive") ·
[`ernte`](mods/ernte) (right-click harvest) · [`graves`](mods/graves) (a grave instead of lost items) ·
[`sit`](mods/sit) (sit on stairs) · [`holzsaege`](mods/holzsaege) (stonecutter for wood) ·
[`mobgriefing`](mods/mobgriefing) (per mob type) · [`totem`](mods/totem) (works from the inventory) ·
[`pets`](mods/pets) · [`todesort`](mods/todesort) (death coordinates) · [`afk`](mods/afk)

*Minigames* — [`tag`](mods/tag) · [`deathswap`](mods/deathswap) · [`manhunt`](mods/manhunt) ·
[`juggernaut`](mods/juggernaut) · [`bounty`](mods/bounty)

*SMP twists* — [`nemesis`](mods/nemesis) ⭐ (the mob that killed you comes back named and stronger) ·
[`lifesteal`](mods/lifesteal) · [`giftburden`](mods/giftburden) (a random gift and burden per player) ·
[`events`](mods/events) (blood moon, meteors, shrinking border) · [`killmagnet`](mods/killmagnet) ·
[`keepmoving`](mods/keepmoving)

**Planned — README only, no code yet (5)**

[`wrapped`](mods/wrapped) ⭐ (weekly server stats highlights) · [`toolguard`](mods/toolguard)
(tools refuse to break) · [`horstihud`](mods/horstihud) 💻 (optional client overlay) ·
[`refill`](mods/refill) · [`spawnguard`](mods/spawnguard)

**Cobblemon add-ons — separate branch for MC 1.21.1 (3)**

See [`cobblemon/`](cobblemon) — [`cobble-keys`](cobblemon/cobble-keys) 💻,
[`cobble-league`](cobblemon/cobble-league), [`cobble-xp`](cobblemon/cobble-xp) 💻

Every folder has its own README with commands, defaults and a fact sheet.
🖥️ = server-side · 💻 = client-side

## License

MIT — see [LICENSE](LICENSE).
