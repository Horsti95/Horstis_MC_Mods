# Horstis MC Mods — Plan & Status (v3) · SINGLE SOURCE OF TRUTH

> **Dies ist die einzige Wahrheitsquelle** für aktuellen Stand, Mod-Liste und nächste Schritte.
> Mod-Ordner-READMEs = reine Nutzungs-/Spec-Doku (kein Status). Keine Ideen in verstreuten .md-Dateien.
>
> **Status: Durchgang 1–13 gebaut** (Gradle-Monorepo, `core` mit Settings-Registry, Mods Rang 1–13, CI).
> Kompilierung läuft über GitHub Actions (der Build-Container hier blockiert Mojang-/Fabric-Downloads,
> die CI-Runner nicht). Nächster Schritt nach grünem CI: Horstis Playtest + GO für Rang 14–21.

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

## 3. DIE MOD-LISTE (Status + Bau-Reihenfolge = Ranking leicht → schwer)

Kategorien: **QoL** = Community-Wunsch/Quality-of-Life · **Spiel** = Minigame · **Twist** = SMP-Regeländerung.
„Öffentlich?“ = Startvorschlag; finale Entscheidung trifft Horsti nach Playtest (Weg: Copy in Public-Repo).

| Rang | Mod (Ordner) | Kat. | Kurzbeschreibung | Aufwand | Öffentlich? | Status |
|--:|--------------|------|------------------|---------|-------------|--------|
| 1 | `todesort` | QoL | Todeskoordinaten privat im Chat (klickbar) | Trivial | Kandidat | 🔨 gebaut, CI-Check |
| 2 | `afk` | QoL | AFK-Markierung in der Tab-Liste | Trivial | Kandidat | 🔨 gebaut, CI-Check |
| 3 | `killmagnet` | Twist | Drops deiner Kills fliegen zu dir | Trivial | Kandidat | 🔨 gebaut, CI-Check |
| 4 | `anvilfix` | QoL | „Too Expensive“ aus, Kosten regelbar | Leicht | **Kandidat ⭐** | 🔨 gebaut, CI-Check |
| 5 | `keepmoving` | Twist | Stillstand = Schaden (nach Karenz) | Leicht | Kandidat (Lücke) | 🔨 gebaut, CI-Check |
| 6 | `totem` | QoL | Totem wirkt aus dem Inventar | Leicht | Kandidat | 🔨 gebaut, CI-Check |
| 7 | `holzsaege` | QoL | Steinsäge verarbeitet Holz | Leicht | Kandidat | 🔨 gebaut, CI-Check |
| 8 | `deathswap` | Spiel | Alle N Min. Positions-Tausch | Leicht | erst Playtest | 🔨 gebaut, CI-Check |
| 9 | `tag` | Spiel | Fangen: „Es“ mit Speed+Glow, Timer, Punkte | Leicht–mittel | Kandidat (Lücke) | 🔨 gebaut, CI-Check |
| 10 | `sit` | QoL | Sitzen auf Treppen/Stufen + /sit | Leicht–mittel | erst Playtest | 🔨 gebaut, CI-Check |
| 11 | `bounty` | Spiel | Kopfgeld auf Zufallsspieler (Glow) | Leicht–mittel | Kandidat (Lücke) | 🔨 gebaut, CI-Check |
| 12 | `mobgriefing` | QoL | mobGriefing pro Mob-Typ statt global | Leicht–mittel | Kandidat | 🔨 gebaut, CI-Check |
| 13 | `ernte` | QoL | Rechtsklick-Ernte + Auto-Replant | Leicht–mittel | Kandidat | 🔨 gebaut, CI-Check |
| 14 | `juggernaut` | Spiel | Einer gegen alle, auto-balanciert | Mittel | Kandidat (Lücke) | 📋 geplant |
| 15 | `pets` | QoL | /pets find·stay·follow + Friendly-Fire-Schutz | Mittel | Kandidat | 📋 geplant |
| 16 | `gabe-buerde` | Twist | Zufälliges Stärke/Schwäche-Paar pro Spieler | Mittel | Kandidat | 📋 geplant |
| 17 | `lifesteal` | Twist | Kill klaut Herz, Spectator statt Ban, Revive | Mittel | erst Playtest | 📋 geplant |
| 18 | `graves` | QoL | Grab statt Item-Despawn (Schutzzeit, Verfall) | Mittel | erst Playtest | 📋 geplant |
| 19 | `manhunt` | Spiel | Jäger-Kompass trackt Runner (cross-dim) | Mittel | erst Playtest | 📋 geplant |
| 20 | `events` | Twist | Weltereignisse: Blutmond / Meteor / Grenze (je Modul) | Mittel | Kandidat | 📋 geplant |
| 21 | `nemesis` ⭐ | Twist | Dein Mob-Killer kehrt benannt & stärker zurück | Mittel–schwer | **Kandidat (Flaggschiff)** | 📋 geplant |
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

## 6. Veröffentlichung (privat → öffentlich)

Alle Mods entstehen hier (privates Test-Repo). Horsti playtestet; bei „OK/GO“ pro Mod → Copy des
Sub-Folders in ein eigenes Public-Repo (dank Prinzip 5 ohne Umbau möglich), dann optional
Modrinth-Publishing (hilft auch Aternos-Ein-Klick). „Erst Playtest“-Mods bleiben ggf. dauerhaft privat.

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
