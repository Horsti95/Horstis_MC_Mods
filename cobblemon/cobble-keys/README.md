# Cobblemon Keybind Helper

**`horsti-cobble-keys` · 💻 Client-side · Minecraft 1.21.1 + Cobblemon 1.7+ (Fabric)**

> Press one key and see every Cobblemon control at a glance — what it does, and whether it is bound.

## What it does

Cobblemon has a lot of keybinds, and new players discover them by accident or not at all. This mod
adds one hotkey that opens a plain overlay listing them:

- every Cobblemon keybind with its **current binding** and a one-line explanation
- unbound actions highlighted, so you notice what you are missing
- your own additions: free-text entries for server commands you use often
- a jump straight into the vanilla controls screen, filtered to Cobblemon

The overlay is text on a translucent background — **no custom sprites, no animations** (PLAN.md,
principle 2). It reads the keybind registry, so entries added by future Cobblemon versions and other
side-mods appear automatically.

## In-game control

| Keybind | Effect |
|---|---|
| `K` (rebindable) | Open/close the overlay |
| `Shift + K` | Open the vanilla controls screen, Cobblemon section |

Settings via Mod Menu + Cloth Config: position, scale, which categories are listed, whether the
overlay shows automatically on the first join.

## Installation

**Client only.** Drop the jar (+ Fabric API + Cobblemon) into your own `mods` folder. Nothing to
install on the server, and it does not matter whether other players have it.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a recurring pain point for new Cobblemon players |
| Does this exist? / our edge | No comparable mod found (the scene focuses on alerts and stat displays). Our edge: pure onboarding help, no gameplay change |
| Estimated effort | Low (read the keybind registry, draw a list) |
| Target | Client |
| Horsti priority | TBD |
