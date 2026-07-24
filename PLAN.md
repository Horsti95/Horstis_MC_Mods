# Horstis MC Mods — Research & Plan (v2)

> **Status: Plan v1 von Horsti freigegeben** (Zielversion 26.2 ✓ · Streichung „Skalierende Schwierigkeit“ ✓ · Liste/Reihenfolge ✓ · Deployment E+C+D ✓).
> **v2 ergänzt auf Horstis Auftrag:** In-Game-Anpassbarkeit als Kern-Prinzip, Community-Wunschzettel-Research (2 neue Mods), Kompatibilitäts-Prinzip, Veröffentlichungs-Strategie (privat/öffentlich), Future Work (Kombinationen, Portierungs-Evaluation).
> Leitidee: **minimale Assets, maximaler Logik-Impact.**

---

## 0. Kern-Prinzip: In-Game-Anpassbarkeit

Jeder Mod ist **im Spiel** an/aus-schaltbar und in allen Parametern regelbar — ohne Dateien anfassen zu müssen. Einheitliches Schema für alle Mods (server-seitig via Brigadier, damit Aternos-tauglich und ohne Client-Installation):

```
/<mod>                  → Status + aktuelle Werte anzeigen
/<mod> on | off         → Mod live an-/abschalten
/<mod> set <param> <wert> → Parameter live ändern (mit Validierung + Grenzen)
/<mod> reset [<param>]  → auf Defaults zurück
/horsti                 → Übersicht: alle installierten Horsti-Mods + Status (aus core)
```

- **Sinnvolle Defaults:** jeder Mod läuft sofort nach Installation ohne jede Konfiguration.
- **Rechte:** OP-Level 2 (per Config änderbar); funktioniert auch über die Server-Konsole (Aternos-Webkonsole).
- **Persistenz:** alle Werte landen automatisch in `config/horsti/<mod>.json`; Datei-Edits greifen live per `/<mod> reload`. Command und Config sind zwei Wege zum selben Wert.
- **Technik im `core`:** Parameter werden einmal deklariert (Name, Typ, Bereich, Default, Beschreibung) — Brigadier-Command, Config-Datei, Validierung und `/horsti`-Übersicht werden daraus **automatisch generiert**. Ein neuer Regler = eine Zeile Code. Das hält jeden Mod schlank (Leitidee!).
- **Toggle-Semantik:** Passive Mods (z. B. Wunschzettel-Features) wirken sofort. Minigames beenden bei `off` die laufende Runde sauber (Effekte entfernen, Ansage, Scoreboard einfrieren).
- **Client-GUI:** nicht nötig, da alles server-seitig ist. Falls je ein Mod einen Client-Teil bekommt, bekommt er zusätzlich Mod Menu + Cloth Config.
- **Gibt es Mods, bei denen In-Game-Toggling nicht sauber geht?** Nach aktuellem Entwurf: nein — alle 12 Mods sind command-steuerbar. Einzige Nuance: Rezept-Features (Wunschzettel → „Holz-Steinsäge“) müssen nach dem Toggle die Rezeptliste neu an die Clients senden; das erledigt der Mod automatisch, dauert einen Tick. Fallback (Config + `/reload`) existiert überall zusätzlich.

---

## 1. Research-Ergebnisse

### 1.1 Zielversion & Toolchain (Stand Juli 2026) — ✅ freigegeben

- Mojang nutzt seit 2026 **Jahres-Versionsnummern**: auf 1.21.x folgte 26.1. Aktuell stabil: **Java Edition 26.2 „Chaos Cubed“** (16. Juni 2026); 26.3 in Snapshots (Q3 2026).
- Fabric-Toolchain für 26.2: **Fabric API 0.155.x+26.2, Loom 1.17, Gradle 9.5.x, Java 25**.
- Seit 26.1: **Yarn-Mappings eingestellt** → wir nutzen **Mojang Mappings (Mojmap)** mit dem neuen Loom ohne Remapping.
- Der Grafik-Umbau in 26.2 (Vulkan, Blaze3D) betrifft nur Render-Code — unsere server-seitigen Logik-Mods sind unberührt.
- **Ziel: 26.2**, Nachzug auf 26.3 nach Release. Risiko-Mitigation: Fabric-Porting-Guides beim Bauen konsultieren, CI-Build als Kompilier-Sicherheitsnetz.

### 1.2 Markt-Check der 10 Startideen — ✅ freigegeben

| # | Idee | Gibt’s das schon? | Verdikt |
|--:|------|-------------------|---------|
| 1 | Lifesteal | Viele Varianten, u. a. rein server-seitig | **Bauen.** Mehrwert: tunebar — Revive-Item, Spectator statt Ban, integriert |
| 2 | Manhunt | Mehrere gute server-seitige Mods | **Bauen.** Mehrwert: an unsere Runde angepasst (mehrere Runner, Schonfrist, Cross-Dimension) |
| 3 | Verstecken | „Hide-n-Seek“ (server-seitig) existiert | **Optional.** Leichte Block-im-Helm-Variante |
| 4 | Death Swap | Existiert als Datapack/Mod | **Bauen.** Gering Aufwand, integriert + konfigurierbar |
| 5 | Ticker / Tag | Als leichte Fabric-Server-Mod kaum vorhanden | **Bauen.** Quick Win, echte Lücke |
| 6 | Random Origin | „Origins Randomiser“ braucht Origins (Client-Pflicht) | **Bauen** als eigenständiges „Gabe & Bürde“ — klarer Mehrwert |
| 7 | Skalierende Schwierigkeit | „Progressive Time Difficulty“ macht exakt das | **Gestrichen** ✅; Eskalation fließt in Nemesis ein |
| 8 | Loot-Randomizer | Existiert mehrfach, auch seeded | **Optional.** Nur mit „Lösbar-Garantie“ |
| 9 | **Nemesis** | Nur als großes Custom-Mob-Mod; nichts Leichtes auf Vanilla-Mobs | **Bauen — Flaggschiff.** |
| 10 | Bounty | Fast nur Paper-Plugins | **Bauen.** Quick Win, Lücke im Fabric-Bereich |

### 1.3 Community-Bedarf: server-seitige Mods

Server-seitige Mods, bei denen Mitspieler mit **Vanilla-Client** joinen, sind eine stark nachgefragte Kategorie (Fabric-Wiki „Serverside Mods“, Aternos-Listen, Polymer-Ökosystem). Unsere Lücke: **Game-Night-Minigames und SMP-Twists als reine Server-Mods.**

### 1.4 NEU — Research „Was wünscht sich die Community seit Jahren?“ (Auftrag v2)

Quellen: Mojang-Feedback-Portal (inkl. „Previously Considered Suggestions“), r/minecraftsuggestions, Community-„Mojang please add“-Listen. Abgeglichen mit dem, was 26.1/26.2 tatsächlich brachten (26.1: Baby-Mob-Modelle, craftbare Namensschilder; 26.2: Schwefelhöhlen, Sulfur Cubes, Vulkan-Renderer, Friends-List) — **die Evergreen-QoL-Wünsche sind weiterhin offen.**

Bewertung der Dauerbrenner gegen unsere Leitidee (nur Logik, keine neuen Assets):

| Wunsch (seit Jahren offen) | Nachfrage | Passt zur Leitidee? | Als Mod machbar | Aufwand |
|---|---|---|---|---|
| Anvil: „Too Expensive“ weg / Cap regelbar | Sehr hoch (Dauer-Top-Thread) | ✅ reine Logik | Server-seitig, Anvil-Menü ist Server-Logik | Niedrig |
| Auf Treppen/Stufen **sitzen** | Hoch (Evergreen) | ✅ (unsichtbares Sitz-Entity) | Server-seitig (existiert als „Polysit“ → wir: integriert + togglebar) | Niedrig |
| **Rechtsklick-Ernte** + Auto-Replant | Sehr hoch (QoL-Klassiker) | ✅ | Server-seitig (Use-Block-Event) | Niedrig |
| **Gräber/Death-Chest** statt Item-Despawn | Sehr hoch | ✅ (vorhandene Blöcke + Spielerkopf) | Server-seitig (existiert als „Universal Graves“ → wir: schlanke integrierte Variante) | Mittel |
| `mobGriefing` **pro Mob-Typ** (Creeper ≠ Enderman ≠ Villager) | Mittel-hoch | ✅ | Server-seitig | Niedrig |
| **Totem wirkt aus dem Inventar** | Mittel | ✅ | Server-seitig, Default aus (Balance) | Niedrig |
| **Todeskoordinaten** im Chat | Mittel | ✅ | Trivial | Trivial |
| **Haustier-Befehle** (/pets find/stay/follow, Friendly-Fire-Schutz) | Mittel | ✅ | Server-seitig | Niedrig |
| **Steinsäge für Holz** (Wood-Cutter) | Mittel-hoch | ✅ (nur Rezepte, Vanilla-UI) | Server-seitig, Rezepte syncen automatisch zu Vanilla-Clients | Niedrig |
| AFK-Anzeige in der Tab-Liste | Mittel | ✅ | Server-seitig | Niedrig |
| Dynamisches Licht (Fackel in Hand) | Hoch | ⚠️ grenzwertig (Licht-Block-Trick, Perf-Kosten) | Server-seitig möglich, Default aus | Mittel |
| Vertikale Slabs, Möbel, neue Mobs, Shader | Hoch | ❌ neue Assets/Client-Rendering | — nicht unser Feld | — |

**Konsequenz — 2 neue Mods in der Liste:**

1. **`horsti-wunschzettel`** — *ein* Mod, der die besten offenen Community-Wünsche als **einzeln togglebare Module** bündelt (alle Zeilen mit ✅ oben außer Gräber). Genau unser Kern-Prinzip: `/wunsch set anvilCap off`, `/wunsch set sit on`, … Jedes Modul einzeln an/aus, sinnvolle Defaults. Öffentlich sehr attraktiv: „Die Vanilla-Wunschliste als Server-Mod, Mitspieler joinen vanilla.“
2. **`horsti-graves`** — Gräber separat (konflikt-sensibler Bereich um den Todes-Event, gehört nicht in ein Sammel-Mod). Playtest gegen „Universal Graves“; öffentlich nur bei echtem Mehrwert.

---

## 2. Finale Mod-Liste (v2: 12 feste + 2 optionale)

Alle rein **server-seitig**, alle nach dem Anpassbarkeits-Schema aus Abschnitt 0. „Öffentlich?“ = Veröffentlichungs-Strategie aus Abschnitt 6.

| Bau-Nr. | Mod-ID | Was es tut | Ziel | Aufwand | Beispiel-Regler | Öffentlich? |
|--:|--------|------------|------|---------|------------------|-------------|
| 1 | `horsti-tag` | Fangen: „Es“ mit Speed+Glow, Treffer gibt weiter, Timer, Punkte | MP | Niedrig | `/tag set rundenzeit 300`, `/tag set esSpeed 1` | Kandidat (Lücke) |
| 2 | `horsti-bounty` | Kopfgeld auf Zufallsspieler (Glow), Killer kassiert | MP | Niedrig | `/bounty set intervall 30m`, `/bounty set belohnung diamant 5` | Kandidat (Lücke) |
| 3 | `horsti-deathswap` | Alle N Min. Positions-Tausch | MP | Niedrig | `/deathswap set intervall 5m`, `/deathswap set warnung off` | erst Playtest |
| 4 | `horsti-lifesteal` | Kill klaut Herz; 0 Herzen = Spectator; Revive-Item | MP | Mittel | `/lifesteal set maxHerzen 20`, `/lifesteal set unterNull spectator` | erst Playtest |
| 5 | `horsti-manhunt` | Jäger-Kompass trackt Runner (cross-dim), Schonfrist, Siegerkennung | MP | Mittel | `/manhunt runner add Horsti`, `/manhunt set schonfrist 60s` | erst Playtest |
| 6 | `horsti-nemesis` ⭐ | Dein Mob-Killer kehrt benannt & gebufft zurück und jagt dich; eskaliert | SP+MP | Mittel | `/nemesis set maxLevel 5`, `/nemesis set buffProKill 1` | **Kandidat (Flaggschiff)** |
| 7 | `horsti-wunschzettel` ⭐ | Community-Wünsche als Toggle-Module: Anvil-Fix, Sitzen, Rechtsklick-Ernte, mobGriefing pro Mob, Totem-Pocket, Todeskoordinaten, Pets, Holz-Säge, AFK | SP+MP | Mittel | `/wunsch set anvilCap off`, `/wunsch set ernte on`, `/wunsch set sit on` | **Kandidat** |
| 8 | `horsti-gabe-buerde` | Zufälliges festes Stärke/Schwäche-Paar pro Spieler, Reroll-Item | SP+MP | Niedrig–mittel | `/gabe set staerke 2`, `/gabe reroll Horsti` | Kandidat |
| 9 | `horsti-juggernaut` | Einer gegen alle, auto-balanciert nach Spielerzahl | MP | Niedrig | `/juggernaut start random`, `/juggernaut set buffProGegner 0.5` | Kandidat (Lücke) |
| 10 | `horsti-graves` | Grab (Spielerkopf + Kiste) statt Item-Despawn, Schutzzeit, Kompass | SP+MP | Mittel | `/graves set schutzzeit 10m`, `/graves set xpErhalt 50` | erst Playtest |
| 11 | `horsti-events` | Weltereignisse: Blutmond-Horde, Meteoritenregen, Schrumpfgrenze | SP+MP | Mittel | `/events set blutmondChance 5`, `/events trigger meteor` | Kandidat |
| 12 | `horsti-tweaks` | Gameplay-Twists einzeln togglebar: Kill-Magnet, „Keep Moving“ | SP+MP | Niedrig | `/tweaks set killMagnet on` | erst Playtest |
| opt. A | `horsti-verstecken` | Prop-Hunt light: Block im Helm + Stillstand = getarnt | MP | Mittel | `/verstecken set suchzeit 300` | erst Playtest |
| opt. B | `horsti-lootrandomizer` | Seed-feste Drop-Vermischung mit Lösbar-Garantie | SP+MP | Mittel | `/lootrandom set seed 42` | erst Playtest |

Reihenfolge-Logik: 1–3 validieren Toolchain + `core` mit Quick Wins; 6+7 sind die Flaggschiffe, sobald die Muster sitzen. **Wunschzettel (7) kann auf Wunsch vorgezogen werden** (höchster Alltagsnutzen fürs SMP) — sag beim GO einfach „Wunschzettel zuerst“.

---

## 3. Kompatibilitäts-Prinzip (v2, Horstis Punkt 3)

1. **Vanilla-first:** Mitspieler joinen immer mit unverändertem Vanilla-Client (alle Mods server-seitig; nur vorhandene Blöcke/Items/Effekte/Sounds).
2. **Mod-freundlich:** Die Mods laufen als normale Fabric-Mods **neben anderen Fabric-Mods** — Standard-Fabric-API-Events statt invasiver Mixins wo immer möglich, alles unter eigenem Namespace, keine globale Vanilla-Mechanik wird ohne Toggle verändert. Jede Funktion ist abschaltbar → Konflikte mit anderen Mods lassen sich immer per Toggle entschärfen.
3. **Untereinander kombinierbar:** alle Horsti-Mods sind gleichzeitig installierbar (gemeinsames `core` wird per Jar-in-Jar mitgeliefert, Loader dedupliziert auf die neueste Version).

---

## 4. Repo-Struktur: Monorepo mit Gradle-Subprojekten — ✅ freigegeben

```
Horstis_MC_Mods/
├─ README.md / PLAN.md
├─ settings.gradle / build.gradle      # Versionen zentral (MC 26.2, Fabric API, Loom 1.17)
├─ gradle/ …                           # Wrapper
├─ core/                               # horsti-core: Settings-Registry (→ Abschnitt 0),
│                                      #   Spielphasen, Timer, Broadcasts; Jar-in-Jar
├─ mods/
│  ├─ tag/  bounty/  deathswap/  lifesteal/  manhunt/  nemesis/
│  ├─ wunschzettel/  gabe-buerde/  juggernaut/  graves/  events/  tweaks/
│  └─ je: README.md, src/main/java/com/horsti/<mod>/, fabric.mod.json
└─ .github/workflows/build.yml         # CI: Jars bauen, Releases bestücken
```

Jede Mod-README: **Community-Nachfrage · Gibt’s das schon? + Mehrwert · Aufwand · In-Game-Steuerung · Horsti-Priorität (TBD)**.

---

## 5. Arbeitszyklus pro Durchgang — ✅ freigegeben

Ein Mod pro Durchgang: bauen → kompilieren/Smoke-Test → Review + Vereinfachungs-Pass → README → Commit/Push → **Stopp** (Horstis Token-Check, dann GO für den nächsten).

---

## 6. Veröffentlichungs-Strategie: privat vs. öffentlich (v2, Horstis Punkt 4)

**Alle** Mods landen im Repo und in den GitHub Releases → du kannst **jede** Version privat playtesten, auch dort, wo es fremde Alternativen gibt (Vergleich: „Was kann unsere Version?“).

- **Öffentliche Kandidaten** (echter Mehrwert / Marktlücke): `nemesis`, `wunschzettel`, `tag`, `bounty`, `juggernaut`, `gabe-buerde`, `events`.
- **Erstmal privat** (existiert schon gut; öffentlich nur, wenn der Playtest echten Mehrwert zeigt): `deathswap`, `lifesteal`, `manhunt`, `graves`, `tweaks`, `verstecken`, `lootrandomizer`.
- Öffentlich = späteres Modrinth-Publishing (hilft zugleich der Aternos-Ein-Klick-Installation). Entscheidung pro Mod **nach** deinem Playtest — Spalte „Öffentlich?“ in der Tabelle ist der Startvorschlag, du entscheidest final.

---

## 7. Installation / Deployment — ✅ freigegeben (E + C + D)

- **(E)** Server-seitig = Kern: Freunde joinen vanilla; Installation nur auf dem Server (Aternos: Datei-Upload; alternativ später Modrinth).
- **(C)** GitHub Releases via CI: jede Mod einzeln + „Alles-drin“-Zip + **`.mrpack`** für Singleplayer/Eigenhosting (One-Click in Prism/Modrinth-Launcher; mrpack darf direkt auf GitHub-Release-URLs zeigen).
- **(D)** README-Fallback: „Jar in den `mods`-Ordner“.
- (A/B) Installer/PowerShell: abgelehnt (SmartScreen, Pfad-Raterei, Wartung — unnötig dank E+C).

---

## 8. Future Work (v2, Horstis Punkte 5 + 6)

### 8.1 Mod-Kombinationen & Full-Stack-Packs (nach den Basis-Mods)

Die Mods sind absichtlich modular — Kombinationen entstehen über Cross-Mod-Hooks im `core` (jeder Mod meldet Events wie „Spieler getötet“, „Event gestartet“):

1. **„Horstis Chaos-SMP“ (Full-Stack-Pack):** Lifesteal + Bounty + Nemesis + Events + Gabe&Bürde + Wunschzettel als ein abgestimmtes Server-Paket (+ `.mrpack`). Synergien: Kopfgeld landet automatisch auf dem Herz-Leader (Bounty×Lifesteal); beim Blutmond spawnen die Nemeses aller Spieler als Welle (Nemesis×Events); Reroll-Items als Bounty-Belohnung (Gabe&Bürde×Bounty).
2. **„Game-Night-Hub“ (eigenes Meta-Mod):** `/gamenight start` rotiert Tag → DeathSwap → Manhunt → Juggernaut (→ Verstecken), gemeinsames Punktekonto über alle Spiele, Gesamtsieger-Ehrung. Lobby/Teleport/Countdown aus dem `core`.
3. **„Hardcore, aber fair“:** Graves + Totem-Pocket + Nemesis = Hardcore-Gefühl mit zweiter Chance — dein Killer-Mob wird zum persönlichen Endgegner.
4. **„Battle-Royale light“:** Events-Schrumpfgrenze + Bounty + Juggernaut-Endgame als 30-Minuten-Runde.

### 8.2 Evaluation: „Client-Mods zu Server-Mods portieren“ als Community-QoL-Initiative (nur bewertet, nicht umgesetzt)

- **Technisch:** Die Mehrheit populärer Client-Mods (Minimaps, HUD, Shader, Inventar-UI) ist **prinzipiell nicht** server-seitig portierbar — sie rendern. Portierbar ist die Teilmenge „Gameplay-Logik / Welt-Interaktion / Commands“; dafür existiert mit **Polymer** sogar ein Framework, das Vanilla-Clients server-seitige „Custom-Inhalte“ vorgaukelt.
- **Szene existiert bereits:** Das Polymer-Ökosystem (u. a. Universal Graves, Polysit) und die kuratierten Serverside-Listen (Fabric-Wiki, Aternos-Community) decken viele Top-Wünsche ab — es gibt also schon eine aktive Community, die genau das tut; eine „Lücke, die nur KI füllen kann“, ist das nicht.
- **Rechtlich:** Fremde Mods „portieren“ scheitert oft an Lizenzen (viele sind All-Rights-Reserved). Sauber ist nur die **Clean-Room-Re-Implementation der Funktion** — also genau das, was wir mit `wunschzettel`/`graves` ohnehin tun.
- **KI-Kosten (Größenordnung bei Frontier-Modellen via API):** kleine Funktions-Re-Implementation ~5–20 $, mittlere ~20–80 $ pro Anlauf (mehrstündige Agent-Session); mit Flatrate-Abos faktisch abgedeckt. Die **echten** Kosten sind Testen + Maintenance über MC-Versionen hinweg — nicht die Tokens.
- **Verdikt:** Als breites „Portierungs-Programm“ **nicht sinnvoll** (Lizenzlage, Wartungslast, Client-gebundene Mehrheit, Szene existiert). **Sinnvoll als selektive Future Work:** einzelne hoch nachgefragte, technisch portierbare Funktionen ohne gutes server-seitiges Äquivalent gezielt nachbauen — Kandidaten sammeln wir, sobald die Basis-Mods stehen. Unser `wunschzettel` erledigt die Top-QoL-Wünsche bereits in genau diesem Geist.

---

## 9. Nächster Schritt

**GO von Horsti** → Durchgang 1: Gradle-Gerüst + `core` (Settings-Registry) + `horsti-tag`. (Alternativ: „Wunschzettel zuerst“.)
