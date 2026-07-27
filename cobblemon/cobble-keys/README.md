# Cobblemon Keybind Helper

**`horsti-cobble-keys` · 💻 Client-side · Minecraft 1.21.1 + Cobblemon 1.7+ (Fabric)**

> Press one key and see every Cobblemon control at a glance — what it does, and whether it is bound.

## What it does

Cobblemon has a lot of keybinds, and new players discover them by accident or not at all. This mod
adds one hotkey that opens a plain list of them:

- every Cobblemon keybind with its **current binding** and, where we ship one, a short explanation
- unbound actions marked in red, so you notice what you are missing
- your own additions: free-text lines for commands you keep forgetting
- a jump straight into the vanilla controls screen, so rebinding is one keypress away
- shown once automatically on your first join — the players who need it are exactly the ones who do
  not know the hotkey exists

The list is text on a translucent rectangle — **no custom sprites, no animations** (PLAN.md,
principle 2). It reads the game's keybind registry and has **no compile-time dependency on
Cobblemon**, so keybinds added by future Cobblemon versions and by other side-mods appear on their
own, and the mod cannot break when Cobblemon updates.

## In-game control

| Keybind | Effect |
|---|---|
| `K` (rebindable) | Open/close the list (mouse wheel scrolls) |
| `Shift + K` | Open the vanilla controls screen |

## Configuration

`config/horsti/cobble-keys.json` — plain JSON, no config library needed:

| Key | Effect |
|---|---|
| `namespace` | Which mod's keybinds are listed (default: **cobblemon**) |
| `showOnFirstJoin` | Show the list once on the first join (default: **true**) |
| `notes` | Your own lines at the bottom of the list |

## Installation

**Client only.** Drop the jar (+ Fabric API + Cobblemon) into your own `mods` folder. Nothing to
install on the server, and it does not matter whether other players have it.

## Fact sheet

| | |
|---|---|
| Community demand | Medium — a recurring pain point for new Cobblemon players |
| Does this exist? / our edge | No comparable mod found (the scene focuses on alerts and stat displays). Our edge: pure onboarding help, no gameplay change, and no dependency on Cobblemon's API |
| Estimated effort | Low (read the keybind registry, draw a list) |
| Target | Client |
| Horsti priority | TBD |
