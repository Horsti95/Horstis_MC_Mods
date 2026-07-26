# Cobblemon XP Readout

**`horsti-cobble-xp` · 💻 Client-side · Minecraft 1.21.1 + Cobblemon 1.7+ (Fabric)**

> How close is your lead Pokémon to the next level? Now you can see it without opening a menu.

## What it does

Adds a compact readout for your active Pokémon:

- **level and XP progress** to the next level, as a number and a vanilla-style bar
- **name and current HP**, so you notice a low-health Pokémon before it faints
- optional: the same line for the whole party, folded out on a keybind
- a short highlight when a Pokémon levels up or is about to evolve

Drawn with vanilla widgets and text only — **no custom sprites or animations**. Pokémon icons, where
shown, are the ones Cobblemon already ships.

## In-game control

| Keybind | Effect |
|---|---|
| `X` (rebindable) | Cycle: lead only / full party / hidden |

Settings via Mod Menu + Cloth Config: position, scale, what is shown, whether it hides itself outside
of battle.

## Installation

**Client only.** Drop the jar (+ Fabric API + Cobblemon) into your own `mods` folder. Nothing on the
server.

## Fact sheet

| | |
|---|---|
| Community demand | Medium (Horsti's request; a common convenience in the mainline games) |
| Does this exist? / our edge | No dedicated XP HUD found — the "Capture XP" mod does something different (XP for catching), and IV/EV mods show other stats. Verify against the live scene before building |
| Estimated effort | Low–medium (read party state, draw a bar) |
| Target | Client |
| Horsti priority | TBD |
