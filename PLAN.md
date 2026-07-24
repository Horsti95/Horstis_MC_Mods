# Horstis MC Mods — Research & Plan (v1)

> **Status: Wartet auf Horstis OK. Kein Mod-Code vor Freigabe.**
> Leitidee: **minimale Assets, maximaler Logik-Impact** — Regel-Logik auf vorhandenen Blöcken, Items, Mobs, Sounds.

---

## 1. Research-Ergebnisse

### 1.1 Zielversion & Toolchain (Stand Juli 2026)

- Mojang nutzt seit 2026 **Jahres-Versionsnummern**: auf 1.21.x folgte 26.1. Aktuell stabil ist **Java Edition 26.2 „Chaos Cubed“** (16. Juni 2026); 26.3 ist in Snapshots (Release Q3 2026 erwartet).
- Fabric-Toolchain für 26.2: **Fabric API 0.155.x+26.2, Loom 1.17, Gradle 9.5.x, Java 25** (Minimum für die Gradle-JVM).
- Wichtige Umstellung seit 26.1: **Yarn-Mappings werden nicht mehr unterstützt** → wir entwickeln direkt mit **Mojang Mappings (Mojmap)** und dem neuen Loom ohne Remapping.
- Für uns günstig: Der große Grafik-Umbau in 26.2 (Vulkan-Backend, Blaze3D) betrifft nur Render-Code. Unsere Mods sind **server-seitige Logik** und davon unberührt — die Leitidee minimiert also auch die Update-Bruchfläche.
- **Empfehlung:** Ziel **26.2**, Nachzug auf 26.3 nach Release. (Risiko: die 26.x-API ist neuer als mein Trainingsstand → beim Bauen konsultiere ich die offiziellen Fabric-Porting-Guides, und ein CI-Build dient als Kompilier-Sicherheitsnetz.)

### 1.2 Markt-Check der 10 Startideen

| # | Idee | Gibt’s das schon? | Verdikt |
|--:|------|-------------------|---------|
| 1 | Lifesteal | Viele Varianten, u. a. rein server-seitig („LifeSteal Server-side“, per Gamerules) | **Bauen.** Mehrwert: unsere tunebare Version — Revive-Item, Spectator statt Ban, ins Pack integriert |
| 2 | Manhunt | Mehrere gute server-seitige Mods (Compass Manhunt, Manhunt Remastered) | **Bauen.** Mehrwert: an unsere Runde angepasst (mehrere Runner, Schonfrist, dimensionsübergreifendes Tracking), geringer Aufwand |
| 3 | Verstecken | „Hide-n-Seek“ (server-seitig, Fabric) existiert; Prop-Hunt-Mods sind meist schwergewichtig | **Optional (Phase 3).** Leichtgewichtige Block-im-Helm-Variante als Mehrwert |
| 4 | Death Swap | Existiert als Datapack/Mod-Hybrid, server-seitig | **Bauen.** Geringer Aufwand, Mehrwert: integriert + konfigurierbar (Intervall, Warnung ja/nein) |
| 5 | Ticker / Tag | Als leichte Fabric-Server-Mod **kaum vorhanden** (eher Minigame-Server-Feature) | **Bauen.** Quick Win, echte Lücke |
| 6 | Random Origin | „Origins Randomiser“ existiert, braucht aber das große Origins (Client-Pflicht) | **Bauen** als eigenständiges, server-seitiges **„Gabe & Bürde“** (zufälliges Buff/Debuff-Paar pro Spieler, seed-fest, Reroll-Item) — klarer Mehrwert |
| 7 | Skalierende Schwierigkeit | „Progressive Time Difficulty“ macht exakt das: server-seitig, konfigurierbar | **Streichen** (kein ehrlicher Mehrwert). Eskalations-Ideen fließen stattdessen in Nemesis ein |
| 8 | Loot-Randomizer | Existiert mehrfach, auch seed-basiert server-seitig („Randomizer++“) | **Optional (Phase 3).** Nur mit „Lösbar-Garantie“ als Alleinstellung |
| 9 | **Nemesis** | „Nemesis System“ ist ein großes Custom-Mob-Mod (Shadow-of-Mordor-Klon); **nichts Leichtgewichtiges auf Vanilla-Mobs** | **Bauen — unser Flaggschiff.** Der Mob, der dich tötet, kehrt benannt & gebufft zurück und jagt gezielt dich. Server-seitig, Vanilla-Clients joinen |
| 10 | Bounty | Fast nur als Server-Plugin (Paper); leichte Fabric-Mod kaum vorhanden | **Bauen.** Quick Win, Lücke im Fabric-Bereich |

### 1.3 Community-Bedarf

Server-seitige Mods, bei denen **Mitspieler mit Vanilla-Client joinen können**, sind eine stark nachgefragte, gut dokumentierte Kategorie (Fabric-Wiki „Serverside Mods“, kuratierte Aternos-Listen). Die Lücke, in die wir stoßen: **Game-Night-Minigames und SMP-Twists als reine Server-Mods** — genau unsere Leitidee. Die meisten existierenden Minigame-Lösungen sind entweder Paper-Plugins (falscher Loader), Datapacks (limitierte UX) oder Client-pflichtige Groß-Mods.

---

## 2. Finale Mod-Liste

**10 feste Mods + 2 optionale.** Alle rein **server-seitig** (funktionieren damit auch im Singleplayer, da dort ein integrierter Server läuft). Konfiguration über In-Game-Commands + JSON-Config, kein Client nötig.

| Reihenfolge | Mod-ID | Was es tut | Zielgruppe | Aufwand |
|--:|--------|------------|------------|---------|
| 1 | `horsti-tag` | Fangen: „Es“ bekommt Speed+Glow, Treffer gibt weiter, Timer, Punktestand | MP | Niedrig |
| 2 | `horsti-bounty` | Kopfgeld auf Zufallsspieler (Glow), Killer kassiert Belohnung | MP | Niedrig |
| 3 | `horsti-deathswap` | Alle N Minuten Positions-Tausch; Fallen bauen | MP | Niedrig |
| 4 | `horsti-lifesteal` | Kill klaut 1 Herz; 0 Herzen = Spectator (kein Ban); Herz-Item zum Craften/Reviven | MP | Mittel |
| 5 | `horsti-manhunt` | Jäger-Kompass trackt Runner (auch über Dimensionen), Schonfrist, Sieg-Erkennung | MP | Mittel |
| 6 | `horsti-nemesis` ⭐ | Der Mob, der dich tötet, kehrt benannt & gebufft zurück und jagt dich; eskaliert bei jedem weiteren Kill | SP+MP | Mittel |
| 7 | `horsti-gabe-buerde` | Random Origin light: festes zufälliges Stärke/Schwäche-Paar pro Spieler, Reroll-Item | SP+MP | Niedrig–mittel |
| 8 | `horsti-juggernaut` *(neu, mein Vorschlag)* | Einer gegen alle: Juggernaut wird nach Spielerzahl auto-gebufft, Rest gewinnt durch seinen Tod | MP | Niedrig |
| 9 | `horsti-events` *(aus euren „Funken“)* | Zufällige Weltereignisse: Blutmond-Horde, Meteoritenregen (Vanilla-Feuerbälle), schrumpfende Weltgrenze — einzeln schaltbar | SP+MP | Mittel |
| 10 | `horsti-tweaks` *(aus euren „Funken“)* | Mini-Features mit Einzel-Toggles: Item-Magnet nach Kill, „Keep Moving“ (Stillstand = Schaden), u. ä. | SP+MP | Niedrig |
| opt. A | `horsti-verstecken` | Prop-Hunt light: Block im Helm + Stillstand = getarnt | MP | Mittel |
| opt. B | `horsti-lootrandomizer` | Seed-feste Drop-Vermischung **mit Lösbar-Garantie** | SP+MP | Mittel |

Gestrichen: **Skalierende Schwierigkeit** (siehe 1.2 — existiert bereits genau so; wir empfehlen dort „Progressive Time Difficulty“ von der Stange).

---

## 3. Repo-Struktur: Monorepo mit Gradle-Subprojekten

**Empfehlung: Monorepo** statt Submodule — ein Build, eine Toolchain, ein Versions-Bump für alle Mods; Submodule wären reiner Verwaltungs-Overhead. Jeder Mod bleibt trotzdem eine **eigene, einzeln installierbare Jar**.

```
Horstis_MC_Mods/
├─ README.md                  # Übersicht, Statustabelle, Installation
├─ PLAN.md                    # dieses Dokument
├─ settings.gradle            # listet alle Subprojekte
├─ build.gradle               # gemeinsame Konvention (Loom, MC/Fabric-Versionen zentral)
├─ gradle/ …                  # Wrapper
├─ core/                      # horsti-core: Mini-Lib (Spielphasen, Timer, Broadcasts, Config)
│                             #   wird per Jar-in-Jar in jede Mod eingebettet; Loader dedupliziert
├─ mods/
│  ├─ tag/         ├─ bounty/     ├─ deathswap/
│  ├─ lifesteal/   ├─ manhunt/    ├─ nemesis/
│  ├─ gabe-buerde/ ├─ juggernaut/ ├─ events/
│  └─ tweaks/
│     └─ je: README.md, src/main/java/com/horsti/<mod>/, fabric.mod.json
└─ .github/workflows/build.yml   # CI: alle Jars bauen, an GitHub Releases hängen
```

Jede Mod-README enthält die vereinbarten Felder: **Community-Nachfrage · Gibt’s das schon? + unser Mehrwert · geschätzter Aufwand · Horsti-Priorität (vorerst „TBD“)**.

---

## 4. Bau-Reihenfolge & Arbeitszyklus

Reihenfolge = Spalte 1 der Tabelle oben: klein anfangen (`tag` validiert die 26.2-Toolchain inkl. `core`), dann aufsteigend, Flaggschiff `nemesis` in der Mitte, wenn die Muster sitzen.

Pro Durchgang (wie von dir vorgegeben):
1. **Ein** Mod vollständig bauen (inkl. `core`-Erweiterungen falls nötig)
2. Kompilieren + Smoke-Test (Gradle-Build, ggf. Test-Server-Start)
3. Code-Review + Vereinfachungs-Pass (passt zur „minimal code“-Idee)
4. README schreiben, committen, pushen
5. **Stopp** → du prüfst Token-Verbrauch und gibst das nächste Mod frei

---

## 5. Installation / Deployment — Empfehlung

**Kombination E + C + D. Kein eigener Installer (A/B abgelehnt).**

- **(E) Server-seitig = Kern der Strategie:** Freunde joinen mit unverändertem Vanilla-Client. Installation betrifft nur den Server bzw. dich.
- **Server-Installation:** Jars aus GitHub Releases in den `mods`-Ordner (+ Fabric API). Auf **Aternos** per Datei-Upload; falls das hakt, publishen wir die Mods später auf **Modrinth** (Aternos installiert von dort per Klick).
- **(C) GitHub Releases via CI:** jede Mod einzeln als Jar **+** ein „Alles-drin“-Zip. Zusätzlich ein **`.mrpack`-Modpack** für Singleplayer/Eigenhosting — One-Click-Import in Modrinth-/Prism-Launcher; das mrpack-Format darf direkt auf GitHub-Release-URLs zeigen, wir brauchen dafür also kein Modrinth-Publishing.
- **(D) README-Fallback:** „Jar herunterladen → in `mods`-Ordner legen“ bleibt immer dokumentiert.
- **(A/B) abgelehnt:** PowerShell-Skript/Installer bedeuten SmartScreen-/Signierungs-Ärger, Pfad-Raterei über verschiedene Launcher und dauerhafte Wartung — und lösen ein Problem, das E + mrpack bereits eleganter lösen.

---

## 6. Offene Punkte für dein OK

1. **Zielversion 26.2** (aktuelle Stable) — einverstanden?
2. **Streichung** „Skalierende Schwierigkeit“ — einverstanden?
3. **Mod-Liste & Reihenfolge** (Abschnitt 2/4) — einverstanden oder umsortieren?
4. **Deployment-Kombi E + C + D** — einverstanden?

Nach deinem OK starte ich mit Durchgang 1: Gradle-Gerüst + `core` + `horsti-tag`.
