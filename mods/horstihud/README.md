# Horsti HUD

**`horsti-hud` · 💻 Client-side companion · optional · Minecraft 26.2 (Fabric)**

> The optional companion for the Horsti server mods: shows what the server already knows —
> as a compact overlay instead of action-bar text.

## What it does

Every Horsti server mod works completely on its own. This client mod adds nothing to the gameplay —
it only presents the same information more comfortably:

- **Manhunt** — the bearing to your target as a real on-screen arrow instead of an action-bar line,
  with distance and dimension underneath
- **Nemesis** — your arch-enemy's name, level and time until it returns
- **Bounty** — remaining hunt time and current target
- **Lifesteal** — your heart count and the current leader
- **Tag / Juggernaut / Deathswap** — round timer and role at a glance

**No new graphics.** Everything is drawn with plain text, vanilla widgets and existing item icons —
no custom sprites, no animations (see PLAN.md, principle 2).

If the server does not run the matching mod, that section simply stays hidden. If you do not install
this mod, everything keeps working exactly as before.

## In-game control

Configuration happens in-game through Mod Menu + Cloth Config (position, scale, which sections are
shown), plus a keybind to fold the overlay away.

| Keybind | Effect |
|---|---|
| `H` (rebindable) | Show/hide the overlay |
| `Shift + H` | Cycle compact / full / off |

## Installation

**Client only.** Drop the jar (+ Fabric API) into your own `mods` folder. Do **not** install it on the
server. Your friends decide for themselves whether they want it.

## Technical note

The server mods send their state through a plain custom payload channel. If the client mod is missing,
the server falls back to action-bar text — so both directions stay compatible.

## Fact sheet

| | |
|---|---|
| Community demand | Only relevant for users of our server mods |
| Does this exist? / our edge | Specific to our mods, so by definition new |
| Estimated effort | Medium (client rendering + networking) — first client mod in the project |
| Target | Client, optional |
| Horsti priority | TBD |
