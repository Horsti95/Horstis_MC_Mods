# Horstis MC Mods — Plan & Status (v3) · SINGLE SOURCE OF TRUTH

> **Dies ist die einzige Wahrheitsquelle** für aktuellen Stand, Mod-Liste und nächste Schritte.
> Mod-Ordner-READMEs = reine Nutzungs-/Spec-Doku (kein Status). Keine Ideen in verstreuten .md-Dateien.
>
> **Status: ALLE 21 MODS GEBAUT ✅ — CI grün** (Run #10). Gradle-Monorepo, `core` mit Settings-Registry,
> JSON-Persistenz und Attribut-/Mob-Helfern; 21 Mods kompilieren auf MC 26.2. Alle Jars liegen als
> Artefakt „horsti-mods“ am Actions-Run.
> Kompilierung läuft über GitHub Actions (der Build-Container der Claude-Session blockiert
> Mojang-/Fabric-Downloads, die CI-Runner nicht).
> **Nächster Schritt: Horstis Playtest → pro Mod entscheiden: privat behalten oder ins Public-Repo
> heben. Danach optional Future Work (Abschnitt 8).**

---

## 1. Prinzipien

1. **Leitidee:** minimale Assets, maximaler Logik-Impact. Nur vorhandene Blöcke/Items/Mobs/Effekte/Sounds.
2. **Niemals bauen (No-Asset-Regel):** vertikale Slabs, Möbel, neue Mobs/Blöcke/Items mit eigenen Texturen,
   Shader, dynamisches Licht (Fackel in Hand — gestrichen wegen Perf/Grenzfall). Diese Liste bleibt hier als
   dauerhafte Erinnerung stehen.
3. **Server-seitig, Vanilla-first:** Mitspieler joinen mit unverändertem Vanilla-Client. Läuft in SP (interner
   Server), Eigenhosting, Aternos, VPS.
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

| Rang | Mod (Ordner) | Kat. | Kurzbeschreibung | Aufwand | Öffentlich? | Status |
|--:|--------------|------|------------------|---------|-------------|--------|
| 1 | `todesort` | QoL | Todeskoordinaten privat im Chat (klickbar) | Trivial | Kandidat | ✅ gebaut (CI grün) |
| 2 | `afk` | QoL | AFK-Markierung in der Tab-Liste | Trivial | Kandidat | ✅ gebaut (CI grün) |
| 3 | `killmagnet` | Twist | Drops deiner Kills fliegen zu dir | Trivial | Kandidat | ✅ gebaut (CI grün) |
| 4 | `anvilfix` | QoL | „Too Expensive“ aus, Kosten regelbar | Leicht | **Kandidat ⭐** | ✅ gebaut (CI grün) |
| 5 | `keepmoving` | Twist | Stillstand = Schaden (nach Karenz) | Leicht | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 6 | `totem` | QoL | Totem wirkt aus dem Inventar | Leicht | Kandidat | ✅ gebaut (CI grün) |
| 7 | `holzsaege` | QoL | Steinsäge verarbeitet Holz | Leicht | Kandidat | ✅ gebaut (CI grün) |
| 8 | `deathswap` | Spiel | Alle N Min. Positions-Tausch | Leicht | erst Playtest | ✅ gebaut (CI grün) |
| 9 | `tag` | Spiel | Fangen: „Es“ mit Speed+Glow, Timer, Punkte | Leicht–mittel | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 10 | `sit` | QoL | Sitzen auf Treppen/Stufen + /sit | Leicht–mittel | erst Playtest | ✅ gebaut (CI grün) |
| 11 | `bounty` | Spiel | Kopfgeld auf Zufallsspieler (Glow) | Leicht–mittel | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 12 | `mobgriefing` | QoL | mobGriefing pro Mob-Typ statt global | Leicht–mittel | Kandidat | ✅ gebaut (CI grün) |
| 13 | `ernte` | QoL | Rechtsklick-Ernte + Auto-Replant | Leicht–mittel | Kandidat | ✅ gebaut (CI grün) |
| 14 | `juggernaut` | Spiel | Einer gegen alle, auto-balanciert | Mittel | Kandidat (Lücke) | ✅ gebaut (CI grün) |
| 15 | `pets` | QoL | /pets find·stay·follow + Friendly-Fire-Schutz | Mittel | Kandidat | ✅ gebaut (CI grün) |
| 16 | `gabe-buerde` | Twist | Zufälliges Stärke/Schwäche-Paar pro Spieler | Mittel | Kandidat | ✅ gebaut (CI grün) |
| 17 | `lifesteal` | Twist | Kill klaut Herz, Spectator statt Ban, Revive | Mittel | erst Playtest | ✅ gebaut (CI grün) |
| 18 | `graves` | QoL | Grab statt Item-Despawn (Schutzzeit, Verfall) | Mittel | erst Playtest | ✅ gebaut (CI grün) |
| 19 | `manhunt` | Spiel | Jäger-Kompass trackt Runner (cross-dim) | Mittel | erst Playtest | ✅ gebaut (CI grün) |
| 20 | `events` | Twist | Weltereignisse: Blutmond / Meteor / Grenze (je Modul) | Mittel | Kandidat | ✅ gebaut (CI grün) |
| 21 | `nemesis` ⭐ | Twist | Dein Mob-Killer kehrt benannt & stärker zurück | Mittel–schwer | **Kandidat (Flaggschiff)** | ✅ gebaut (CI grün) |
| opt. | `verstecken` | Spiel | Prop-Hunt light | Mittel–schwer | unbestätigt | kein Ordner |
| opt. | `lootrandomizer` | Twist | Seed-feste Drops mit Lösbar-Garantie | Mittel | unbestätigt | kein Ordner |

Status-Legende: 📋 geplant → 🔨 in Arbeit → ✅ gebaut (kompiliert) → 🧪 im Playtest → 🌍 öffentlich.
Optionale bekommen Ordner erst nach Horstis Bestätigung.

## 4. Repo-Struktur

```
Horstis_MC_Mods/
├─ README.md            # Mini-Einstieg, verweist hierher
├─ PLAN.md              # DIESE Datei: Status, Liste, Roadmap
├─ settings.gradle / build.gradle / gradle/   # Versionen zentral (ab Durchgang 1)
├─ core/                # Settings-Registry, Timer/Spielphasen, Broadcasts (Jar-in-Jar)
└─ mods/<name>/         # je Mod: README.md (Spec/Nutzung), build.gradle, src/…, fabric.mod.json
```

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

## 9. Research-Runde 2 (Juli 2026) — Kandidaten für Welle 3

### 9.1 Neue eigene QoL-Ideen (geprüft gegen den Markt)

| Idee | Was | Nachfrage | Gibt’s das schon? | Verdikt |
|---|---|---|---|---|
| **`wrapped`** ⭐ | Wöchentliche Server-Highlights aus Vanilla-Statistiken: „meiste Blöcke gelaufen“, „meiste Tode“, „größter Bergmann“ — Ansage im Chat + `/wrapped` jederzeit | Mittel, aber hoher Wow-Effekt | Nur Bukkit-Plugins (PlayerStats) + externe Web-Tools; **als Fabric-Server-Mod nichts gefunden** | **Bauen — größte neue Lücke** |
| **`werkzeugschutz`** | Werkzeug blockiert bei kritischer Haltbarkeit + Warnung, statt zu zerbrechen | Hoch (Dauerärgernis) | Existiert — aber **fast alles client-seitig** (jeder Spieler muss selbst installieren) | **Bauen** — server-seitig = gilt für alle, echter Mehrwert |
| **`heim`** | `/heim`, `/warp`, `/tpa`, `/zurueck` (nach Tod/Teleport) | Sehr hoch („jeder Server braucht das“) | Viel vorhanden (Essentials-artig, auch für Fabric) | **Nur privat** — Mehrwert wäre nur die Integration ins Horsti-Schema |
| **`nachschub`** | Leerer Block-Stack wird automatisch aus dem Inventar nachgefüllt | Mittel-hoch | Meist client-seitig | Kandidat, zweite Reihe |
| **`spawnschutz`** | Konfigurierbare Anti-Mob-Spawn-Zone um Basen (Fackel-Ersatz) | Mittel | Teils vorhanden | Kandidat, zweite Reihe |
| ~~`wegpunkte`~~ | Waypoints + Peilung für Vanilla-Clients | Hoch | **Mehrfach server-seitig vorhanden** (ServerPoints, Better Waypoints, Server-Side Waypoints) | **Gestrichen** — kein Mehrwert |

### 9.2 Cobblemon — geprüft, aber blockiert

**Kernbefund: Cobblemon läuft auf Minecraft 1.21.1, nicht auf unserer Zielversion 26.2.** Aktuell ist
v1.7.3 (Jan 2026); das kommende 1.8.0 (TMs, Alpha-Pokémon, Habitate) war im Juli 2026 noch nicht
veröffentlicht und zielt ebenfalls auf 1.21.1. Ein Cobblemon-Addon von uns bräuchte also einen
**eigenen 1.21.1-Build-Zweig** — zweite Toolchain, zweite Testumgebung, doppelte Pflege.

**Zweiter Befund:** Horstis konkrete Wünsche (XP-Leiste, Keybind-Übersicht per Hotkey) sind
**HUD-Rendering = reine Client-Mods**. Das widerspricht Prinzip 3 (server-seitig, Vanilla-Clients) und
wäre eine komplett neue Mod-Kategorie mit Mod-Menu/Cloth-Config und Client-Rendering-Code — genau der
Bereich, den der 26.2-Grafikumbau (Vulkan/Blaze3D) instabil macht.

**Vorhandene Sidemods** (Auswahl): Cobbledex (Pokédex-Infos), Mega Showdown, Capture XP, Myths and
Legends, Server-Side Commands, Rider, Pasture Collector — die Szene ist aktiv und deckt viel ab.

**Verdikt: zurückgestellt.** Empfehlung: abwarten, bis Cobblemon auf eine 26.x-Version zieht. Falls
Horsti trotzdem will, ist der sinnvollste Einstieg ein **server-seitiges** Cobblemon-Addon für 1.21.1
(z. B. Team-Wettkampf-Logik, Fang-Statistiken, Turnier-Modus) statt HUD-Features.

### 9.3 Community-Wünsche, zweite Runde

Der Wunschzettel-Research aus v2 hat sich bestätigt — die Evergreens (Anvil, Ernte, Gräber, Sitzen)
haben wir gebaut. Neu aufgefallen ist nur die **Server-Admin-Ecke** (Homes/Warps/TPA/Back als
„jeder Server braucht das“), die aber gut abgedeckt ist → siehe `heim` oben: privat ja, öffentlich nein.
