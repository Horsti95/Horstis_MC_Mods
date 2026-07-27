# Horsti HUD

**`horsti-hud` · 💻 Client-side companion · optional · Minecraft 26.2 (Fabric)**

> The optional companion for the Horsti server mods: shows what the server already knows —
> as a compact overlay instead of a line of action-bar text that vanishes.

## What it does

Every Horsti server mod works completely on its own. This client mod adds nothing to the gameplay —
it only presents the same information more comfortably: a small text block on the left of the screen,
one line per active section, each in the colour that section uses in chat.

**No new graphics.** Everything is the vanilla font on a translucent rectangle — no custom sprites,
no animations (see PLAN.md, principle 2).

If the server does not run a matching mod, its section simply never appears. If you do not install
this mod, everything keeps working exactly as before.

### Sections

| Section | Status |
|---|---|
| **Manhunt** — bearing, distance and dimension of your target | ✅ wired up |
| **Nemesis** — your arch-enemy's name, level and time until it returns | 🔜 one line in the server mod |
| **Bounty** — remaining hunt time and current target | 🔜 |
| **Lifesteal** — your heart count and the current leader | 🔜 |
| **Tag / Juggernaut / Deathswap** — round timer and role | 🔜 |

The channel and the colours for all of these already exist. Wiring a section up is a single call
changed from `Broadcast.actionbar(...)` to `Broadcast.hud(player, "<section>", ...)` in the server
mod — the remaining ones are held back until the server mods have been playtested.

## Configuration

`config/horsti/hud.json` — plain JSON, read at startup:

| Key | Effect |
|---|---|
| `aktiv` | Master switch (default: **on**) |
| `compact` | One line per section without the label (default: **off**) |
| `x` | Horizontal position in % of the screen width (default: **2**) |
| `y` | Vertical position in % of the screen height (default: **40**) |

No commands: on someone else's server a client mod cannot register any. No Mod Menu or Cloth Config
dependency either — four values do not justify pulling in a config library.

**A rebindable hotkey is not in this version.** 26.2 removed Fabric's `KeyBindingHelper` from its old
package and we have not confirmed where key registration moved to; shipping a guess would mean
shipping a crash. Until then the master switch in the config file does the same job.

## Technical note

The server mods send their state through a custom payload channel (`horsti:hud`, defined in `core`
so both sides share one definition). Before sending, the server asks whether the client actually
registered that channel — if not, it sends action-bar text instead. That check is the whole reason
this mod can stay optional: no handshake, no version negotiation, and vanilla clients notice nothing.

Sections expire three seconds after the last update, so a finished round clears itself without the
server having to send an "end" packet.

## Installation

**Client only.** Drop the jar (+ [Fabric API](https://modrinth.com/mod/fabric-api)) into your own
`mods` folder. Do **not** install it on the server. Your friends decide for themselves whether they
want it.

## Fact sheet

| | |
|---|---|
| Community demand | Only relevant for users of our server mods |
| Does this exist? / our edge | Specific to our mods, so by definition new |
| Estimated effort | Higher than expected — 26.2 replaced the whole HUD API (`HudRenderCallback` and `GuiGraphics#drawString` are gone, HUD elements now extract a render state) |
| Target | Client, optional |
| Horsti priority | TBD |
