# Horstis MC Mods — Plan & Status (v3) · SINGLE SOURCE OF TRUTH

> **Dies ist die einzige Wahrheitsquelle** für aktuellen Stand, Mod-Liste und nächste Schritte.
> Mod-Ordner-READMEs = reine Nutzungs-/Spec-Doku (kein Status). Keine Ideen in verstreuten .md-Dateien.
>
> **Status: ALLE 21 MODS GEBAUT ✅ — CI grün** (Run #10). Gradle-Monorepo, `core` mit Settings-Registry,
> JSON-Persistenz und Attribut-/Mob-Helfern; 21 Mods kompilieren auf MC 26.2. Alle Jars liegen als
> Artefakt „horsti-mods“ am Actions-Run.
> Kompilierung läuft über GitHub Actions (der Build-Container der Claude-Session blockiert
> Mojang-/Fabric-Downloads, die CI-Runner nicht).
> **Welle 3 läuft** (Abschnitt 9): `wrapped`, `toolguard`, `refill` und `spawnguard` sind **gebaut** —
> damit 25 Mods. `horstihud` hängt an einem 26.2-API-Umbau (siehe 9.4).
> **Cobblemon-Zweig gestartet** (Abschnitt 9.1): eigener 1.21.1-Build, `cobble-keys` gebaut.
> **Nächster Schritt: Horstis Playtest der fertigen Mods → pro Mod entscheiden: privat behalten
> oder veröffentlichen (Abschnitt 6.1).**

---

## 1. Prinzipien

1. **Leitidee:** minimale Assets, maximaler Logik-Impact. Nur vorhandene Blöcke/Items/Mobs/Effekte/Sounds.
2. **No-Asset-Regel (unverändert gültig):** keine eigenen Texturen, Sprites, Modelle, Animationen, Shader.
   Damit bleiben gestrichen: vertikale Slabs, Möbel, neue Mobs/Blöcke/Items mit eigener Grafik.
   **Erlaubt sind:** Text, Vanilla-Widgets (Balken, Icons vorhandener Items), Farben, Töne aus Vanilla.
3. **Client erlaubt, Server bevorzugt** *(geändert am 26.07.2026 auf Horstis Ansage)*:
   - **Server-seitig bleibt der Default**, wo eine Funktion server-seitig sauber geht — dann joinen
     Mitspieler weiter mit Vanilla-Client (SP, Eigenhosting, Aternos, VPS).
   - **Client-seitig ist jetzt zulässig**, wenn eine Funktion nur dort möglich ist (HUD, Overlays,
     Keybinds, Menü-Erweiterungen) — solange sie **ohne neue Grafik-Assets** auskommt (Regel 2).
   - **Bevorzugtes Muster: „Server-Mod + optionaler Client-Begleiter“.** Der Server-Mod funktioniert
     allein vollständig; der Client-Mod macht es nur hübscher (z. B. Peilung als HUD-Pfeil statt
     Actionbar-Text). Niemand wird zur Installation gezwungen.
   - Jeder Mod deklariert seine Umgebung in `fabric.mod.json` (`environment`: `*` / `server` / `client`)
     und trägt sie sichtbar in der ersten README-Zeile.
4. **In-Game-Anpassbarkeit (Kern-Prinzip):** Jeder Mod sofort lauffähig mit sinnvollen Defaults und im Spiel
   regelbar: `/<mod>` (Status), `/<mod> on|off`, `/<mod> set <param> <wert>`, `/<mod> reset`, `/horsti`
   (Übersicht aller Horsti-Mods, aus `core`). OP-Level 2, Konsole/Aternos-tauglich. Persistenz in
   `config/horsti/<mod>.json`, live per `/<mod> reload`. Die `core`-Settings-Registry generiert Commands +
   Config + Validierung aus einer Parameter-Deklaration (ein Regler = eine Zeile Code).
5. **Einzeln shippable:** Jeder Mod = eigener Sub-Folder, in sich abgeschlossen (eigene README, eigenes
   Build-File, kein Import aus anderen Mods, nur `core` als Dependency, das per Jar-in-Jar mitgeliefert wird).
   → Horsti kann jeden Ordner nach Playtest per Copy in ein eigenes Public-Repo heben.
6. **Mechanik-Modularität (Gegenstück zu Kombinationen):** Hat ein Mod mehrere Mechaniken, ist jede ein
   eigenes Modul hinter eigenem Toggle und rauslösbar (Beispiel: `events` = Scheduler + je Event ein Modul).
   Umgekehrt gilt die **Bündel-Regel:** nichts zusammenwerfen, was nicht zusammengehört — gebündelt wird nur,
   was gemeinsame Infrastruktur teilt oder zusammen etwas Neues ergibt. (Deshalb wurde das v2-Sammel-Mod
   „Wunschzettel“ in 9 einzelne Mini-Mods aufgelöst, ebenso „Tweaks“ in `killmagnet` + `keepmoving`.)
7. **Mod-Kompatibilität:** Standard-Fabric-API-Events statt invasiver Mixins wo möglich, eigener Namespace,
   jede Funktion abschaltbar (Konflikte per Toggle entschärfbar), alle Horsti-Mods gleichzeitig installierbar.

## 2. Rahmendaten (Research-Kern, Juli 2026)

- **Ziel: Minecraft Java 26.2 „Chaos Cubed“** (Jahres-Versionierung seit 2026; 26.3 in Snapshots).
  Toolchain: **Fabric API 0.155.x+26.2, Loom 1.17, Gradle 9.5.x, Java 25, Mojang Mappings** (Yarn seit 26.1 tot).
  Grafik-Umbau (Vulkan/Blaze3D) betrifft uns nicht (kein Render-Code).
- Markt: server-seitige Mods für Vanilla-Clients = nachgefragte Kategorie (Fabric-Wiki, Aternos-Listen,
  Polymer-Szene). Echte Lücken bei uns: Tag, Bounty, Juggernaut, **Nemesis** (Flaggschiff), Keep-Moving.
  Community-Evergreens (nie in Vanilla gekommen, auch nicht in 26.1/26.2): Anvil-Fix, Sitzen,
  Rechtsklick-Ernte, Gräber, mobGriefing pro Mob, Totem-Pocket, Todeskoordinaten, Pet-Befehle, Holz-Säge, AFK.
- Gestrichen: „Skalierende Schwierigkeit“ (Progressive Time Difficulty existiert genau so).

**26.x-API-Notizen aus Durchgang 1–13** (wichtig für Rang 14–21; per CI-javap-Dump verifiziert):
`ResourceLocation` → `net.minecraft.resources.Identifier` · `CommandSourceStack.hasPermission(int)` →
`.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))` (PermissionCheck-Konstanten) ·
`Entity#getServer`/`ServerPlayer#serverLevel` entfernt → `HorstiServer.get()` (core) bzw. Cast von `level()` ·
`displayClientMessage` → `ClientboundSetActionBarTextPacket` · `ServerBossEvent` braucht UUID als 1. Argument ·
`PlayerTeam.setColor(Optional<TeamColor>)`, `setSuffix` existiert nicht mehr · `ClickEvent` = Interface mit
Records (`new ClickEvent.CopyToClipboard(...)`) · `ItemInput.createItemStack(int)` ohne bool ·
`ArmorStand#setSmall` privat · `startRiding(Entity)` bzw. 3-arg-Variante · Projektil-Klassen verschoben →
Typvergleich über `BuiltInRegistries.ENTITY_TYPE.getKey(...)` · `GameRules` umgezogen (Ort unbekannt,
WitherBoss-Redirect deshalb v1 gestrichen) · `ResourceKey#location` umbenannt → Dimensionsvergleich über
`Level.OVERWORLD/NETHER/END`. Bei neuen unsicheren APIs: deaktivierten javap-Dump-Schritt im CI-Workflow
reaktivieren (`if: false` entfernen, Klassenliste anpassen).

**Ergänzungen aus Durchgang 14–21:** `EntityType` hat keine statischen Konstanten mehr und kein `spawn()` →
`core`-Helfer `Mobs.typ/spawnen/istTyp/typId` (Registry + `create()` + `addFreshEntity`) ·
`MinecraftServer#overworld()` entfernt → `HorstiServer.oberwelt(server)` ·
**keine Tageszeit-API** mehr auffindbar (`getDayTime` weg) → zeitgesteuerte Events statt Tag/Nacht-Erkennung ·
`WorldBorder.lerpSizeBetween` hat 4 Parameter → wir interpolieren selbst per `setSize` ·
`Entity#getTags()` weg → Identität über gespeicherte UUIDs ·
`TamableAnimal.getOwnerReference()` ist die neue Form (`getOwner()` funktioniert weiterhin) ·
`ServerPlayer.gameMode()`/`setGameMode` und `Container.setItem/getItem/getContainerSize` sind unverändert.

## 3. DIE MOD-LISTE (Status + Bau-Reihenfolge = Ranking leicht → schwer)

Kategorien: **QoL** = Community-Wunsch/Quality-of-Life · **Spiel** = Minigame · **Twist** = SMP-Regeländerung.
„Öffentlich?“ = Startvorschlag; finale Entscheidung trifft Horsti nach Playtest (Weg: Copy in Public-Repo).

| Rang | Mod (Ordner) | Umgebung | Kat. | Kurzbeschreibung | Aufwand | Öffentlich? | Status |
|--:|--------------|---|------|------------------|---------|-------------|--------|
| 1 | `todesort` | 🖥️ | QoL | Todeskoordinaten privat im Chat (klickbar) | Trivial | Kandidat | ✅ gebaut (CI grün) |
| 2 | `afk` | 🖥️ | QoL | AFK-Markierung in der Tab-Liste | Trivial | Kandidat | ✅ gebaut (CI grün) |
| 3 | `killmagnet` | 🖥️ | Twist | Drops deiner Kills fliegen zu dir | Trivial | Kandidat | ✅ gebaut (CI grün) |
| 4 | `anvilfix` | 🖥️ | QoL | „Too Expensive“ aus, Kosten regelbar | Leicht | **Kandidat ⭐** | ✅ gebaut (CI grün) |
| 5 | `keepmoving` | 🖥️ | Twist | Stillstand = Schaden (nach Karenz) | Leicht | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 6 | `totem` | 🖥️ | QoL | Totem wirkt aus dem Inventar | Leicht | Kandidat | ✅ gebaut (CI grün) |
| 7 | `holzsaege` | 🖥️ | QoL | Steinsäge verarbeitet Holz | Leicht | Kandidat | ✅ gebaut (CI grün) |
| 8 | `deathswap` | 🖥️ | Spiel | Alle N Min. Positions-Tausch | Leicht | erst Playtest | ✅ gebaut (CI grün) |
| 9 | `tag` | 🖥️ | Spiel | Fangen: „Es“ mit Speed+Glow, Timer, Punkte | Leicht–mittel | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 10 | `sit` | 🖥️ | QoL | Sitzen auf Treppen/Stufen + /sit | Leicht–mittel | erst Playtest | ✅ gebaut (CI grün) |
| 11 | `bounty` | 🖥️ | Spiel | Kopfgeld auf Zufallsspieler (Glow) | Leicht–mittel | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 12 | `mobgriefing` | 🖥️ | QoL | mobGriefing pro Mob-Typ statt global | Leicht–mittel | Kandidat | ✅ gebaut (CI grün) |
| 13 | `ernte` | 🖥️ | QoL | Rechtsklick-Ernte + Auto-Replant | Leicht–mittel | Kandidat | ✅ gebaut (CI grün) |
| 14 | `juggernaut` | 🖥️ | Spiel | Einer gegen alle, auto-balanciert | Mittel | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 15 | `pets` | 🖥️ | QoL | /pets find·stay·follow + Friendly-Fire-Schutz | Mittel | Kandidat | ✅ gebaut (CI grün) |
| 16 | `giftburden` | 🖥️ | Twist | Zufälliges Stärke/Schwäche-Paar pro Spieler | Mittel | Kandidat | ✅ gebaut (CI grün) |
| 17 | `lifesteal` | 🖥️ | Twist | Kill klaut Herz, Spectator statt Ban, Revive | Mittel | erst Playtest | ✅ gebaut (CI grün) |
| 18 | `graves` | 🖥️ | QoL | Grab statt Item-Despawn (Schutzzeit, Verfall) | Mittel | erst Playtest | ✅ gebaut (CI grün) |
| 19 | `manhunt` | 🖥️ | Spiel | Jäger-Kompass trackt Runner (cross-dim) | Mittel | erst Playtest | ✅ gebaut (CI grün) |
| 20 | `events` | 🖥️ | Twist | Weltereignisse: Blutmond / Meteor / Grenze (je Modul) | Mittel | Kandidat | ✅ gebaut (CI grün) |
| 21 | `nemesis` ⭐ | 🖥️ | Twist | Dein Mob-Killer kehrt benannt & stärker zurück | Mittel–schwer | **Kandidat (Flaggschiff)** | ✅ gebaut (CI grün) |
| opt. | `verstecken` | 🖥️ | Spiel | Prop-Hunt light | Mittel–schwer | unbestätigt | kein Ordner |
| opt. | `lootrandomizer` | 🖥️ | Twist | Seed-feste Drops mit Lösbar-Garantie | Mittel | unbestätigt | kein Ordner |

Status-Legende: 📋 geplant → 🔨 in Arbeit → ✅ gebaut (kompiliert) → 🧪 im Playtest → 🌍 öffentlich.
Umgebung: 🖥️ server-seitig (Vanilla-Clients joinen) · 💻 client-seitig · 🔗 beides.

**Welle 3 (Abschnitt 9):** `wrapped` ✅, `toolguard` ✅, `refill` ✅, `spawnguard` ✅,
`horstihud` ⛔ (Abschnitt 9.4).
**Cobblemon-Zweig (Abschnitt 9.1):** `cobble-keys` ✅, `cobble-league` 📋, `cobble-xp` 📋.

## 4. Repo-Struktur

```
Horstis_MC_Mods/
├─ README.md            # Mini-Einstieg, verweist hierher
├─ PLAN.md              # DIESE Datei: Status, Liste, Roadmap
├─ settings.gradle / build.gradle / gradle/   # Versionen zentral
├─ core/                # Settings-Registry, Timer, Broadcasts, Persistenz (Jar-in-Jar)
├─ mods/<name>/         # je Mod: README.md (Spec/Nutzung), build.gradle, src/…, fabric.mod.json
└─ cobblemon/           # ZWEITER, EIGENSTÄNDIGER BUILD auf MC 1.21.1
   ├─ settings.gradle / build.gradle / gradle.properties / gradlew   # eigene Toolchain
   └─ <name>/           # gleicher Aufbau wie mods/<name>/
```

`cobblemon/` hängt bewusst **nicht** im Root-`settings.gradle`: 1.21.1 und 26.2 dürfen sich weder
Klassenpfad noch Loom-Version teilen (Details in 9.1). Gebaut wird dort mit `cd cobblemon &&
./gradlew build`, in CI über einen eigenen Job.

### 4.1 Warum **kein** `client/`- und `server/`-Ordner (Antwort auf Horstis Frage 0.3)

**Empfehlung: flach lassen.** Drei Gründe:

1. **Viele Mods sind beides.** Das bevorzugte Muster ist „Server-Mod + optionaler Client-Begleiter“
   (Prinzip 3). Ein Mod mit beiden Teilen müsste in beide Ordner — die Trennung wäre sofort falsch.
2. **Jeder Mod soll selbsterklärend sein** (Horstis Ziel). Das erreicht die **Kennzeichnung**, nicht der
   Pfad: `environment` in `fabric.mod.json` + Badge in der ersten README-Zeile + Spalte „Umgebung“ in
   der Mod-Liste unten. Wer den Ordner öffnet, sieht es sofort — egal wo er liegt.
3. **Ein Umbau kostet ohne Gegenwert:** Gradle-Pfade, CI-Globs, Git-Historie und die
   „ein Ordner = ein Public-Repo“-Regel müssten angefasst werden.

**Kennzeichnungs-Schema in jeder README-Kopfzeile:**
`🖥️ Server` (Vanilla-Clients joinen) · `💻 Client` (nur lokal) · `🔗 Server + Client-Begleiter`

## 5. Arbeitszyklus & Reihenfolge

- Bau-Reihenfolge = Rang-Spalte (leicht → schwer): validiert Toolchain mit Trivial-Mods, Flaggschiff zum
  Schluss, wenn alle Muster sitzen.
- **Pro Durchgang ein Mod** (bauen → kompilieren/Smoke-Test → Review → Vereinfachen → README finalisieren →
  Push → **Stopp** für Horstis Token-Check). Ausnahme auf Wunsch: Rang 1–3 (Trivial) als ein Durchgang.
- Durchgang 1 enthält zusätzlich: Gradle-Gerüst + `core` + CI-Workflow.

## 6. Veröffentlichung (privat → öffentlich) + Modrinth

Alle Mods entstehen hier (privates Test-Repo). Horsti playtestet; bei „OK/GO“ pro Mod → Copy des
Sub-Folders in ein eigenes Public-Repo (dank Prinzip 5 ohne Umbau möglich), dann Modrinth-Publishing
(hilft auch der Aternos-Ein-Klick-Installation). „Erst Playtest“-Mods bleiben ggf. dauerhaft privat.

### 6.1 Modrinth-Upload — geprüft, machbar (Stand Juli 2026)

**Zulässig:** Kein Freigabe-Prozess für normale Mods (nur Modpacks mit fremden Inhalten werden geprüft).
Modrinths Content Rules verlangen: eigene Rechte am Inhalt ✅ (alles selbst geschrieben, MIT-Lizenz),
keine Cheats/Unfair-Advantage ✅ (server-seitige Mods, vom Admin gesteuert), **englischsprachige
Projektbeschreibung** ⚠️ (unsere READMEs sind deutsch → englische Beschreibung nötig).

**Automatisierbar:** GitHub Action `cloudnode-pro/modrinth-publish@v2` lädt Jars nach jedem Release hoch.
Braucht: Modrinth-Account, pro Mod ein Projekt (einmalig manuell anlegen → Projekt-ID), einen
API-Token mit „Create versions“-Scope als GitHub-Secret `MODRINTH_TOKEN`.

**Offene To-dos vor dem ersten Upload** (nichts davon ist ein Blocker, alles Fleißarbeit):
1. Pro Mod eine **englische Kurzbeschreibung** (~5 Sätze) + Feature-Liste.
2. **Projekt-Icons** — Modrinth zeigt sonst einen Platzhalter. Achtung No-Asset-Regel: Icons sind
   Store-Grafik, kein Spiel-Asset — trotzdem braucht es 21× ein Bild (oder ein gemeinsames Logo).
3. Entscheidung **Einzelprojekte vs. Sammelprojekt**: 21 Einzelprojekte = maximale Auffindbarkeit,
   aber 21× Pflege. Empfehlung: **erst 3–5 Flaggschiffe einzeln** (nemesis, anvilfix, tag, bounty,
   juggernaut), Rest später oder als Sammel-Modpack.
4. Versions-Schema festlegen (z. B. `0.2.0+26.2`) und `mod_version` in `gradle.properties` pflegen.

**Wichtig:** Der Upload ist eine öffentliche, schwer rückholbare Aktion (Modrinth cached/indexiert).
Er passiert erst nach Horstis ausdrücklichem OK pro Mod — und nach dem Playtest.

## 7. Installation / Deployment (freigegeben: E + C + D)

Server-seitig (E): nur Server braucht die Jars (+ Fabric API); Freunde joinen vanilla. GitHub Releases via
CI (C): Einzel-Jars + Sammel-Zip + `.mrpack` für SP/Prism (darf auf GitHub-Release-URLs zeigen).
README-Fallback (D). Installer/PowerShell: abgelehnt (SmartScreen/Pfade/Wartung).

## 8. Future Work (nicht vor Abschluss der Basis-Mods)

1. **Kombinations-Packs** (nur wo Synergie echt ist, vgl. Prinzip 6): „Horstis Chaos-SMP“ (Lifesteal+Bounty+
   Nemesis+Events+Gabe&Bürde: Kopfgeld auf Herz-Leader, Nemesis-Welle im Blutmond), „Game-Night-Hub“
   (`/gamenight`-Rotation mit Gesamtwertung), „Hardcore aber fair“ (Graves+Totem+Nemesis), „Battle-Royale
   light“ (Grenze+Bounty+Juggernaut). Basis: Cross-Mod-Events im `core`.
2. **QoL-Bundle** der Mini-Mods als ein Paket (mrpack/Wrapper), sobald sie einzeln stabil sind.
3. **Client→Server-Portierungs-Initiative — evaluiert, Verdikt:** kein breites Programm (Mehrheit der
   Client-Mods rendert = nicht portierbar; Polymer-Szene deckt viel ab; Lizenzen erlauben meist nur
   Clean-Room-Nachbau der Funktion; KI-Kosten wären nicht der Engpass [~5–20 $ klein / 20–80 $ mittel pro
   Anlauf via API, mit Flatrate inklusive], sondern Test/Maintenance). **Selektiv ja:** Einzelfälle mit
   echter Lücke sammeln wir nach den Basis-Mods.

## 9. WELLE 3 — geplant (Ordner + README stehen, kein Code)

Nach der Client-Freigabe (Prinzip 3, 26.07.2026) neu bewertet. Alle Ordner liegen unter `mods/` bzw.
`cobblemon/`, alle READMEs sind **auf Englisch** (Modrinth-tauglich, siehe Abschnitt 10).

| Mod | Umgebung | Was | Gibt’s das schon? | Priorität |
|---|---|---|---|---|
| **`wrapped`** ⭐ | 🖥️ Server | Wöchentliche Server-Highlights aus Vanilla-Statistiken + `/wrapped` jederzeit; Kategorien als Module, andere Mods können eigene registrieren | Nur Bukkit-Plugins + Web-Tools; **als Fabric-Mod nichts gefunden** | ✅ **gebaut (CI grün)** |
| **`toolguard`** | 🖥️ Server | Werkzeug blockiert bei kritischer Haltbarkeit statt zu zerbrechen | Existiert, aber **fast nur client-seitig** | ✅ **gebaut (CI grün)** |
| **`refill`** | 🖥️ Server | Leerer Block-Stack wird aus dem Inventar nachgefüllt | Meist client-seitig | ✅ **gebaut** |
| **`spawnguard`** | 🖥️ Server | Konfigurierbare Anti-Mob-Spawn-Zone um Basen (Fackel-Ersatz) | Teils vorhanden | ✅ **gebaut** |
| **`horstihud`** | 💻 Client | Begleiter für unsere Server-Mods: Nemesis-Status, Manhunt-Peilung als Pfeil, Bounty-Timer — nur Text + Vanilla-Widgets | — (spezifisch für unsere Mods) | ⛔ **blockiert, siehe 9.4** |
| ~~`heim`~~ | — | Homes/Warps/TPA | Essentials-Territorium, gut abgedeckt | **Nur privat, falls überhaupt** |
| ~~`wegpunkte`~~ | — | Waypoints | **Dreifach server-seitig vorhanden** | **Gestrichen** |

### 9.1 Cobblemon-Zweig (`cobblemon/`) — Ziel: Minecraft 1.21.1

**Kein Cobblemon-Port.** Wir liefern Addons für die **aktuelle** Cobblemon-Version (v1.7.3, Jan 2026,
läuft auf **MC 1.21.1**). Das kommende 1.8.0 zielt ebenfalls auf 1.21.1 und war im Juli 2026 noch nicht
draußen. Konsequenz: **eigener Build-Zweig auf 1.21.1** — andere Toolchain als unsere 26.2-Mods
(dort gilt noch `ResourceLocation`, `getServer()` etc., also die *alte* API vor dem 26.x-Umbau).

**Marktlage (dicht besetzt, ehrlich bewertet):** Spawn-/Shiny-Alerts sind mehrfach abgedeckt (Cobblemon
Spawn Alerts mit 1 Mio.+ Downloads, Poke-Notifier, Spawn Notification, Chiselmon). IV/EV-Anzeige gibt es
via MoreCobblemonTweaks und Cobblemon Utility+ — und Cobblemon 1.7 zeigt IVs/EVs inzwischen selbst.
Pokédex: Cobbledex. UI: Cobblemon UI Tweaks.

**Verbleibende Lücken → unsere drei Kandidaten:**

| Mod | Umgebung | Was | Lücken-Einschätzung |
|---|---|---|---|
| **`cobble-keys`** | 💻 Client | Hotkey blendet alle Cobblemon-Tastenbelegungen samt Erklärung ein — Einsteigerhilfe | Nichts Vergleichbares gefunden; Cobblemon hat viele Keybinds, die niemand kennt |
| **`cobble-xp`** | 💻 Client | XP-/Level-Fortschritt des aktiven Pokémon dauerhaft im HUD (Text + Vanilla-Balken) | Kein reiner XP-HUD gefunden (der „Capture XP“-Mod macht etwas anderes) |
| **`cobble-league`** | 🖥️ Server | Turnier-/Liga-Logik: Anmeldung, Paarungen, Tabelle, Fang-Statistiken je Spieler | Alert-Ecke ist voll, **Wettkampf-Logik kaum besetzt** — passt zu unserer Server-Stärke |

Reihenfolge-Vorschlag: erst `cobble-keys` (klein, klare Lücke), dann `cobble-league` (unsere Stärke),
`cobble-xp` als drittes. Erst nach Horstis GO und getrennt vom 26.2-Build.

**Stand:** `cobble-keys` ist geschrieben, die **Toolchain war die eigentliche Arbeit.** Zwei Hürden,
beide per CI-Probe geklärt (der Session-Container kommt nicht an `maven.fabricmc.net`):

1. **Loom 1.17 kann diesen Zweig gar nicht bauen.** `officialMojangMappings()` wirft
   „Cannot use Mojang mappings in a non-obfuscated environment" — 26.x wird unobfuskiert
   ausgeliefert, 1.21.1 nicht. Also eine ältere Loom-Zeile: **Loom 1.9.2**, dazu ein eigener
   Wrapper mit **Gradle 8.12** und **Java 21** (Gradle 8.12 läuft nicht auf 25).
2. **Die Plugin-Marker der alten Loom-Versionen sind abgeräumt.** `plugins { id
   'net.fabricmc.fabric-loom' version '1.7-SNAPSHOT' }` findet nichts mehr; die Marker-Liste beginnt
   erst bei ~1.16. Das **Artefakt** `net.fabricmc:fabric-loom:1.9.2` liegt aber weiter im Maven —
   also über `buildscript { classpath ... }` statt über die Plugin-ID.

Dazu ein eigener CI-Job, der nur bei Änderungen in `cobblemon/` läuft. Fabric API für 1.21.1 steht
bei `0.116.14+1.21.1` (per Probe bestätigt, nicht geraten).

`cobble-keys` kommt bewusst **ohne Cobblemon-Abhängigkeit** aus: es liest die Keybind-Registry des
Spiels und filtert nach Namensraum. Damit überlebt es jedes Cobblemon-Update und zeigt auch Keybinds
anderer Side-Mods. `cobble-league` und `cobble-xp` brauchen Cobblemons API (Battle-Events,
Pokémon-XP) und ziehen dann das ImpactDev-Maven dazu.

### 9.4 `horstihud`: 26.2 hat die Client-HUD-API komplett umgebaut

Der CI-API-Dump (27.07.2026) zeigt: **das, wogegen ein HUD-Mod normalerweise gebaut wird, gibt es
in 26.2 nicht mehr.**

| Was wir brauchen | Stand in 26.2 |
|---|---|
| `HudRenderCallback` (Fabric) | **weg** — ersatzlos entfernt |
| `GuiGraphics#drawString` / `#fill` | **weg** — die Klasse hat keine Zeichen-Methoden mehr |
| `KeyBindingHelper` (Fabric) | **weg** aus `…client.keybinding.v1` |
| `HudElement` (Fabric) | da, aber neue Signatur: `extractRenderState(GuiGraphicsExtractor, DeltaTracker)` |
| `KeyMapping` | da, Kategorie ist jetzt ein Record `KeyMapping.Category` mit `register(Identifier)` |
| Netzwerk (`PayloadTypeRegistry`, `ServerPlayNetworking.canSend`, `ClientPlayNetworking`) | **unverändert nutzbar** ✅ |

Mojang ist auf eine **Extract-/Render-State-Pipeline** umgestiegen: ein HUD-Element sammelt erst
seinen Zustand ein, gezeichnet wird später zentral. Das ist kein Umbenennen, das ist ein anderes
Modell — der Aufwand für `horstihud` liegt damit **über** der „Mittel"-Schätzung aus der README.

**Nächster Schritt:** zweiter API-Dump (läuft) mit vollem `GuiGraphics`, `GuiGraphicsExtractor`,
`Options.keyMappings` und dem Klassen-Index (findet, wohin `KeyBindingHelper` gewandert ist).
Danach entscheiden — die Netzwerkseite ist der stabile Teil und kann unabhängig gebaut werden.

### 9.2 Community-Wünsche, zweite Runde

Der Wunschzettel aus Welle 2 hat sich bestätigt — die Evergreens (Anvil, Ernte, Gräber, Sitzen) sind
gebaut. Neu aufgefallen ist die **Server-Admin-Ecke** (Homes/Warps/TPA), die aber dicht besetzt ist.
Die Client-Freigabe eröffnet vor allem **HUD-Begleiter** für eigene Server-Logik — genau das, was
`horstihud` und die beiden Cobblemon-Client-Mods abdecken.

## 9.3 PUBLISH-EMPFEHLUNG (Research-Runde 3, Juli 2026)

Horstis Haltung: „dicht besetzt ist positiv — was gut existiert, nutzen wir einfach.“ Genau danach
sortiert. Veröffentlicht wird nur, wo wir etwas liefern, das es so **nicht** gibt.

### ✅ Veröffentlichen (7) — englische README fertig

| Mod | Warum es eine Lücke füllt |
|---|---|
| **`nemesis`** ⭐ | Als leichtes Server-Mod auf Vanilla-Mobs **praktisch einzigartig**. Die Konkurrenz ist ein großes Custom-Mob-Mod. Unser stärkstes Argument überhaupt |
| **`wrapped`** ⭐ | Kein Fabric-Server-Mod gefunden — nur Bukkit-Plugins und Web-Tools. Dazu erweiterbar (Cobblemon-Saison) |
| **`toolguard`** | Existiert, aber **fast nur client-seitig**. Server-seitig = schützt alle, einmal vom Admin gesetzt |
| **`anvilfix`** | Der meistgenannte Vanilla-Ärger. Konkurrenz sind große Rework-Mods, oft mit Client-Teil — wir sind ein Ein-Zweck-Mod |
| **`giftburden`** | Origins-Randomiser braucht Origins **und** Client. Unsere Variante ist dependency-frei |
| **`tag`** | Als leichte Fabric-Server-Mod kaum vorhanden (sonst Minigame-Server-Feature) |
| **`juggernaut`** | Dito — plus Auto-Balancing nach Spielerzahl als eigener Dreh |

### 🔒 Privat behalten (14) — gut abgedeckt, wir nutzen selbst was da ist

`graves` (Universal Graves ist stark) · `sit` (Polysit) · `ernte` (Right Click Harvest) ·
`todesort`, `afk`, `killmagnet` (Teil größerer QoL-Pakete) · `deathswap`, `manhunt` (mehrere gute
Versionen) · `lifesteal` (viele Varianten) · `holzsaege` (Datapacks) · `totem` (Datapacks) ·
`events` (Blutmond existiert) · `keepmoving` (Nische ohne Publikum) ·
**`bounty`** (neu bewertet: „Bounty Hunt“ und „Spoorn Bounty Mobs“ decken das ab → **von Kandidat auf privat**)

Diese laufen bei uns weiter — sie sind gebaut, getestet, kosten nichts. Sie brauchen nur keine
Modrinth-Seite, die niemand besucht.

### 🔜 Später entscheiden (5)

`refill`, `spawnguard` (gebaut, aber erst nach Playtest bewertbar), `horstihud` (blockiert, 9.4) und
die drei Cobblemon-Addons (eigener 1.21.1-Zweig, siehe 9.1).

Vorläufige Einschätzung nach dem Bauen:
- **`refill`** — Konkurrenz ist client-seitig (Inventory Profiles Next & Co.). Unser Dreh: server-seitig,
  ein Toggle, gilt für alle. Ordentliches Argument, aber kein Alleinstellungsmerkmal.
- **`spawnguard`** — steckt sonst in Claim-Plugins. Als eigenständiges Mod ohne Claim-System dünner
  besetzt als erwartet → **eher Kandidat als gedacht.**
- **`cobble-keys`** — nichts Vergleichbares gefunden, und ohne Cobblemon-Abhängigkeit gebaut, also
  update-fest. Klein, aber echte Lücke → **Kandidat.**

## 10. Sprache: Deutsch → Englisch (Stand 26.07.2026)

Für Modrinth ist eine **englische Projektbeschreibung Pflicht**. Der Umfang ist gestaffelt:

| Ebene | Status | Nötig für Modrinth? |
|---|---|---|
| Root-README | Horsti stellt selbst um | Ja |
| Mod-READMEs (21 Stück) | **Deutsch** — Umstellung offen | Ja (jede README = eine Projektbeschreibung) |
| READMEs Welle 3 + Cobblemon | **Englisch** ✅ (ab sofort Standard) | — |
| Command-Namen (`/holzsaege`, `/gabe`, `/herzen`, `/ziel`) | **Deutsch** | Ja, sonst unbenutzbar für internationale Spieler |
| Setting-Keys (`rundenMin`, `schutzSek`) | **Deutsch** | Ja (stehen in der Config und im Command) |
| In-Game-Texte | **Deutsch** | Ja |
| Code-Bezeichner + Kommentare | **Deutsch** | Nein (intern, kein Nutzer sieht das) |
| PLAN.md | **Deutsch** (Horstis Arbeitsdokument) | Nein |

**Empfehlung:** Nicht alle 21 Mods auf Verdacht übersetzen, sondern **nur die, die tatsächlich öffentlich
gehen** — und die dann *vollständig* (README + Commands + Settings + Texte). Halbe Übersetzungen sind
schlimmer als gar keine. Ordnernamen wie `holzsaege`/`todesort` würden dabei ebenfalls englisch
(`woodcutter`/`deathpoint`). Achtung: Command- und Setting-Umbenennungen brechen bestehende
Configs — deshalb sinnvollerweise **vor** dem ersten öffentlichen Release, nicht danach.
