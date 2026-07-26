# Horstis MC Mods

21 eigene Minecraft-Mods (Java, **Fabric, MC 26.2**) nach der Leitidee
**„minimale Assets, maximaler Logik-Impact“** — alle server-seitig, **Mitspieler joinen mit Vanilla-Client**.

➡️ **[PLAN.md](PLAN.md) ist die Single Source of Truth**: Mod-Liste, Status, Roadmap.

## Installation

1. **Jars holen:** neuester [Actions-Build](../../actions/workflows/build.yml) → Artefakt **`horsti-mods`**
   herunterladen und entpacken (oder lokal `./gradlew build`, Jars landen in `mods/<name>/build/libs/`).
2. **Auf den Server legen:** gewünschte `horsti-*.jar` + [Fabric API](https://modrinth.com/mod/fabric-api)
   in den `mods`-Ordner. Aternos: Fabric API installieren, Horsti-Jars per Datei-Upload dazu.
3. **Fertig.** Jeder Mod läuft sofort mit sinnvollen Defaults; Freunde brauchen **keine** Installation.
   Für Singleplayer die Jars in den `mods`-Ordner der eigenen 26.2-Fabric-Instanz.

Nur die Mods installieren, die du willst — jeder ist eigenständig, `horsti-core` bringt jede Jar selbst mit.

## Steuerung im Spiel

| Command | Wirkung |
|---|---|
| `/horsti` | Übersicht: alle installierten Horsti-Mods + Status |
| `/<mod>` | Status und aktuelle Werte eines Mods |
| `/<mod> on` · `/<mod> off` | Mod live an-/abschalten |
| `/<mod> set <param> <wert>` | Parameter live ändern |
| `/<mod> reset` · `/<mod> reload` | Defaults bzw. Config-Datei neu laden |

Alles OP-Level 2, funktioniert auch über die Server-Konsole (Aternos-Webkonsole).
Werte landen automatisch in `config/horsti/<mod>.json`, Spielstände in `config/horsti/daten/`.

## Die Mods

**Quality of Life** — [`anvilfix`](mods/anvilfix) (kein „Zu teuer!“ mehr) ·
[`ernte`](mods/ernte) (Rechtsklick-Ernte) · [`graves`](mods/graves) (Grab statt Item-Verlust) ·
[`sit`](mods/sit) (auf Treppen sitzen) · [`holzsaege`](mods/holzsaege) (Steinsäge für Holz) ·
[`mobgriefing`](mods/mobgriefing) (pro Mob-Typ) · [`totem`](mods/totem) (aus dem Inventar) ·
[`pets`](mods/pets) · [`todesort`](mods/todesort) · [`afk`](mods/afk)

**Minigames** — [`tag`](mods/tag) (Fangen) · [`deathswap`](mods/deathswap) ·
[`manhunt`](mods/manhunt) · [`juggernaut`](mods/juggernaut) · [`bounty`](mods/bounty)

**SMP-Twists** — [`nemesis`](mods/nemesis) ⭐ (dein Mob-Killer kehrt benannt & stärker zurück) ·
[`lifesteal`](mods/lifesteal) · [`gabe-buerde`](mods/gabe-buerde) · [`events`](mods/events)
(Blutmond, Meteoritenregen, Schrumpfgrenze) · [`killmagnet`](mods/killmagnet) · [`keepmoving`](mods/keepmoving)

Jeder Ordner hat eine eigene README mit allen Commands, Defaults und einem Steckbrief.

## Lizenz

MIT — siehe [LICENSE](LICENSE).
