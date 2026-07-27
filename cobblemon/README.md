# Horsti Cobblemon Add-ons

**Separate build branch — targets Minecraft 1.21.1, not 26.2.**

Cobblemon (v1.7.3, Jan 2026) runs on **Minecraft 1.21.1**, and the upcoming 1.8.0 targets 1.21.1 as
well. Our main mods target **26.2**. These are two different APIs — 1.21.1 still has
`ResourceLocation`, `Entity#getServer()` and the old permission system, all of which were replaced in
the 26.x rewrite.

**Consequence:** the mods in this folder are built against 1.21.1 with their own Gradle settings and
their own Fabric API version. They are add-ons for the *current* Cobblemon — we are not porting
Cobblemon to a newer Minecraft.

## Planned add-ons

| Mod | Environment | What it does |
|---|---|---|
| [`cobble-keys`](cobble-keys) | 💻 Client | A hotkey shows every Cobblemon keybind with an explanation — onboarding help |
| [`cobble-league`](cobble-league) | 🖥️ Server | Tournament and league logic: sign-up, pairings, standings, per-player catch stats |
| [`cobble-xp`](cobble-xp) | 💻 Client | Active Pokémon's XP and level progress as a permanent HUD readout |

Suggested order: `cobble-keys` (small, clear gap) → `cobble-league` (plays to our server-logic
strength) → `cobble-xp`.

## Market situation (checked July 2026)

The Cobblemon side-mod scene is **busy**, and we only build where there is a real gap:

- **Well covered, we stay out:** spawn and shiny alerts (Cobblemon Spawn Alerts with 1M+ downloads,
  Poke-Notifier, Spawn Notification, Chiselmon), IV/EV display (MoreCobblemonTweaks, Cobblemon
  Utility+, plus Cobblemon 1.7 shows IVs/EVs natively), Pokédex (Cobbledex), UI polish
  (Cobblemon UI Tweaks), mega evolution (Mega Showdown), riding, pasture automation.
- **Thin:** competition and tournament logic, keybind onboarding, a persistent XP readout.

## Build

This folder is its **own Gradle build** (`cobblemon/settings.gradle`), deliberately not wired into the
root `settings.gradle` — 1.21.1 and 26.2 must never share a classpath. It uses Mojang mappings like the
main build, so class and method names read the same in both branches.

```bash
cd cobblemon && ../gradlew build
```

CI builds it in a separate job (`.github/workflows/build-cobblemon.yml`) that only runs when something
in this folder changes.

## Status

| Mod | Status |
|---|---|
| `cobble-keys` | ✅ Built, CI green — client-side, no Cobblemon dependency, untested in-game |
| `cobble-league` | 📋 Planned — README only |
| `cobble-xp` | 📋 Planned — README only |

`cobble-league` and `cobble-xp` need Cobblemon's own API (battle events, Pokémon experience), so they
will add the ImpactDev maven dependency that `cobble-keys` does without.
