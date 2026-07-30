# Performance- & Netzwerk-Analyse — 30.07.2026 (Rev. 4)

> **Rev. 4, Stand 16:11 Uhr — was sich geändert hat:**
> **Terralith ist gelöst.** Der 16:10-Start zeigt `Applied 1823 biome modifications to 159 of 159
> new biomes` (vorher 64) und **null** `Unknown registry key`-Fehler. Die Biom-Registry ist heil,
> Version 2.6.2 ist die richtige.
> **Die Welt lädt trotzdem nicht** — neue, quantifizierte Hypothese in 6.2: bei Sichtweite 20 sind
> es 1681 Chunks, die mit Terralith neu generiert **2–4 Minuten** brauchen. Beide Abbrüche kamen
> nach 8 bzw. 30 Sekunden.
> **Meine Iris-Empfehlung war falsch** — Iris ist jetzt auf 1.8.14-beta.1 und die Uniform-Fehler
> sind unverändert da. Ursache ist eine andere, siehe 6.4.
> **spark ist installiert** (1.10.109). **Minecraft läuft bestätigt auf der 5090**, nicht auf der iGPU.
> Alle Abschnitte unterhalb sind auf diesem Stand; Abschnitte 1–5 sind unverändert gültig.

---

# Rev. 3 (Basis)

> Messreport, **kein** Plan. Grundlage: 6 Speedtest-CSVs (13:42–15:20), F3-Screenshot,
> FRITZ!Box-DSL-Informationen, Systeminfos dreier Rechner, vollständige Modliste (41 aktiv /
> 6 deaktiviert), Startlog der Instanz, Modrinth-Java-Einstellungen und das **entpackte
> Shaderpack ComplementaryReimagined_r5.8.1**.
>
> Setup: **Singleplayer → Open to LAN → e4mc**, Link an Paul. Monitor 2560×1440, 200 Hz, G-Sync,
> DisplayPort direkt an der dGPU. Heap auf 8192 MB. Paul spielt auf der geteilten Instanz.
>
> **Rev. 3 korrigiert Rev. 2 an vier Stellen** (0.1). Alle vier Korrekturen ändern Empfehlungen.

---

## 0. Kurzfassung

1. **Dringend zuerst: Welt sichern.** Chunks, die mit deaktiviertem Terralith geladen *und
   gespeichert* wurden, können ihre Biome dauerhaft auf den Fallback verloren haben. Siehe 6.1.
2. **Warum die Welt nicht mehr lädt:** mit hoher Wahrscheinlichkeit der Terralith-Fehler-Spam.
   Tausende ERROR-Zeilen synchron auf dem Server-Thread bei Sichtweite 20. Terralith wieder
   einzuschalten sollte das Problem **beheben**, nicht verursachen. Siehe 6.2.
3. **Der größte ungenutzte fps-Hebel heißt „Entity Shadows", nicht „Shadow Distance".** Profil
   HIGH setzt `ENTITY_SHADOW=1`. Der Pack-Autor schreibt selbst dazu: *„Entity Shadows impact
   performance way more than expected, costing more than 50 % of your fps in some cases."* In
   einer Cobblemon-Welt mit 194 Entities ist das der Volltreffer. Siehe 3.2.
4. **Die Schattendistanz ist bereits richtig eingestellt** — Profil HIGH = `shadowDistance=192.0`
   = **12 Chunks**. Meine Empfehlung aus Rev. 1/2, sie auf 12–16 zu senken, ist damit erledigt.
5. **Nichts im Shaderpack ist durchgestrichen.** Das Sprachfile enthält **null** `§m`-Codes. Was
   durchgestrichen/ausgegraut aussieht, ist Complementarys eigene Warnfarbgebung. Siehe 3.3.
6. **Die Leitung ist am Anschlag und war es immer.** Der sechste Lauf liefert 50,4 Mbit/s bei
   18,7 ms Bufferbloat — das sind 92 % der Roh-Sync-Rate, also exakt der normale VDSL-Overhead.
   **Rev. 2 lag falsch:** das FRITZ!OS-Update hat die Leitung nicht um 10 % verbessert.
7. **Die Leitung kann aber viel mehr, als der Tarif freigibt:** Kapazität 79,8 / 23,3 Mbit/s
   gegen 55,2 / 11,3 provisioniert, bei 284 m und null Fehlern. Anschreiben an EWE in 9.
8. **Paul muss die Instanz duplizieren**, bevor er Mods abschaltet — sonst überschreibt Horstis
   nächstes „Push update" seine Änderungen. Pflicht-/Kür-Matrix in 7.

### 0.1 Was Rev. 3 gegenüber Rev. 2 korrigiert

| # | Rev. 2 sagte | Richtig ist |
|---|---|---|
| 1 | „Lithium, ModernFix, ImmediatelyFast, Krypton, EBE, Sodium Extra, Reese's sind alle installiert (waren es schon)" | **Nur Lithium war installiert.** Die anderen sechs wurden erst danach nachgerüstet. Der F3-Screenshot (148 fps, 9 ms MSPT, Sodium 0.6.13) stammt aus der Zeit **davor** und beschreibt nicht mehr den aktuellen Stand. |
| 2 | „Schattendistanz senken bringt +20–35 %" | Sie steht bei HIGH bereits auf **12 Chunks**. Nichts zu holen. Der Hebel ist stattdessen **Entity Shadows**. |
| 3 | „Sync ist nach dem FRITZ!OS-Update von 50,3 auf 55,17 gestiegen (+10 %)" | Zwei verschiedene Kennzahlen derselben Leitung: 55,17 ist die **Roh-Sync-Rate**, 50,3 die **verfügbare Bitrate** nach Protokoll-Overhead. Es hat sich nichts verbessert, und es war auch nichts zu erwarten. |
| 4 | „Simulationsdistanz auf 8 setzen" | Steht laut Log bereits auf 8, Sichtweite auf 20. Erledigt. |

---

## 1. Netzwerk

| # | Zeit | Setup | DL (10 MB) | UL | Idle-Latenz | Bufferbloat |
|---|---|---|---|---|---|---|
| R1 | 13:42:09 | nur WLAN, Fortnite lädt | 9,9 Mbit/s | 5,1 | 100 ms | +123 ms |
| R2 | 13:49:17 | WLAN + Kabel, Fortnite lädt | 18,5 | 5,4 | 81 ms | +118 ms |
| R3 | 13:52:51 | nur Kabel, Fortnite lädt | 9,1 | 5,1 | **22 ms** | +124 ms |
| R4 | 14:01:54 | Kabel, Fortnite gestoppt | 50,42 | 9,78 | 25 ms | +41 ms |
| R5 | 14:03:04 | Kabel | 50,72 | 9,71 | 27 ms | +18 ms |
| **R6** | **15:19:33** | **Kabel, sauber** | **50,42** (max 50,65) | **9,61** | **25,3 ms** | **+18,7 ms** |

**R6 ist der sauberste Lauf und die neue Referenz.** Der 1-MB-Block liefert 44,6 Mbit/s (in R5
waren es durch eine kurze Störung nur 15,1), die Latenz ist über alle 42 Messungen zwischen 22,8
und 32,6 ms, der Upload erreicht in allen drei Größenstufen 7,2 / 9,4 / 9,6 Mbit/s.

### 1.1 Warum 50,4 und nicht 55,2 — und warum das richtig ist

* **Roh-Sync-Rate** (Internet → DSL-Informationen → DSL): 55.168 / 11.260 kbit/s
* **Verfügbare Bitrate** (Übersichtsseite): 50,3 / 10,4 Mbit/s — das ist bereits der Wert **nach**
  Abzug von PTM-Framing, IP-, TCP- und HTTP-Overhead
* **Gemessen (R6):** 50,42 / 9,61 Mbit/s

55.168 × 0,92 ≈ **50,75**. Die Messung liegt bei 50,42. **Die Leitung ist zu ~99 % ausgereizt,
in beide Richtungen.** Es gibt hier nichts mehr zu optimieren — außer den Tarif.

### 1.2 DSL-Leitungsdaten

| Kennzahl | Empfangen | Senden |
|---|---|---|
| **Leitungskapazität** | **79.835 kbit/s** | **23.293 kbit/s** |
| DSLAM-Datenrate max. | 55.168 | 11.264 |
| Aktuelle Datenrate | 55.168 | 11.260 |
| Störabstandsmarge | 12 dB | 15 dB |
| Leitungsdämpfung | 15 dB | 15 dB |
| Leitungslänge | 284 m | |
| ES / SES / CRC | 0 / 0 / 0 | 0 / 0 / 0 |
| Profil / Trägersatz | 17a / B43 | |
| G.Vector | aus | aus |

**45 % Reserve im Downstream, 107 % im Upstream** — freigegeben wird sie nicht. Bei 284 m und
15 dB Dämpfung wären mit Profil 35b (Supervectoring) realistisch 150–250 Mbit/s down und 40 up
möglich. Anschreiben an EWE: Abschnitt 9.

### 1.3 Heimnetz

FRITZ!WLAN Repeater 310 (Wi-Fi 4, 2,4 GHz, Single-Radio, ~2013) ist das schwächste Glied —
halbiert den Durchsatz, frisst Airtime, drei Geräte hängen dahinter · 6 Geräte auf 2,4 GHz gegen
1 auf 5 GHz · OnePlus-15 klebt trotz Wi-Fi 7 auf 2,4 GHz mit 20 MHz · devolo dLAN 200 AVmini am
WAN-Port mit 100-Mbit-Link (real 40–90 Mbit, hoher Jitter) · Horsti5090 an LAN 1 mit 2,5 Gbit/s
ist perfekt.

**Maßnahmen:** Repeater ausmustern oder die Geräte dahinter direkt anbinden · getrennte SSIDs und
2,4-GHz-Dauergäste aufs 5-GHz-Band · Gaming-PC bleibt am Kabel (gemessen 22 ms statt 100 ms) ·
FRITZ!Box → Internet → Filter → Priorisierung → Horsti5090 als „Echtzeitanwendung" ·
Epic-Launcher dauerhaft auf ~30 Mbit/s drosseln.

### 1.4 Was 55/11 fürs Hosten bedeutet

Der Download ist egal, **der Upload ist die Grenze**. Grob 100–300 kbit/s pro Spieler im normalen
Spiel, 1–3 Mbit/s in Spitzen; Cobblemon liegt am oberen Ende. **11,3 Mbit/s tragen 3–4
Mitspieler**, mit Paul allein ist reichlich Luft. e4mc kostet einen Relay-Hop, typisch
+20–40 ms — die Alternative wäre eine Portfreigabe auf 25565. Der Server schickt Paul Chunks nur
bis **min(Horstis Sichtweite, Pauls Renderdistanz)**, Paul auf RD 6 ist also von sich aus billig.
Global gesetzt wird dagegen die **Simulationsdistanz** (aktuell 8).

---

## 2. Die drei Rechner

**Horsti5090** (Ultra 9 275HX, RTX 5090 Laptop, 64 GB): kein Hardware-Engpass. Offen:
Auslagerungsdatei steht auf 7,02 GB fix bei 64 GB RAM → auf „automatisch verwalten" · VBS/HVCI-
Status unbekannt, kostet ggf. 5–10 % CPU.

**Altes HP ProBook x360 435 G7** (Ryzen 7 4700U, 16 GB, Vega 7): **16 GB in 1 von 2 Slots =
Single-Channel**, das kostet die iGPU 25–40 %. Zweiter Riegel wäre das beste Upgrade überhaupt.
Sonst: RD 8–10, keine Shader, 4 GB Heap → 60–100 fps.

**Pauls MEDION S17403** (i7-10510U, 8 GB, Win 11): 8 GB gesamt, 1,04 GB frei · 15-W-CPU, hält
unter Dauerlast eher 1,6–2,2 GHz, rund Faktor 2,5 langsamer pro Kern als der 275HX · **GPU
weiterhin unbekannt** (UHD 620 oder MX330 — `dxdiag`) · BIOS von 2021 · VBS aus (gut).
Kein RAM-Upgrade geplant → alles über Software, Abschnitt 8.

---

## 3. Shader — was das entpackte Pack verrät

Das Profil **HIGH** setzt laut `shaders/shaders.properties` exakt:

```
SHADOW_QUALITY=2  shadowDistance=192.0  WATER_REFLECT_QUALITY=2  BLOCK_REFLECT_QUALITY=3
LIGHTSHAFT_QUALI_DEFINE=2  SSAO_QUALI_DEFINE=2  FXAA_DEFINE=1  DETAIL_QUALITY=2
CLOUD_QUALITY=2  ANISOTROPIC_FILTER=0  COLORED_LIGHTING=0  WORLD_SPACE_REFLECTIONS=-1
ENTITY_SHADOW=1
```

### 3.1 Die Schattendistanz ist bereits optimal

`shadowDistance=192.0` entspricht laut `lang/en_US.lang` **„12 Chunks"**. Genau der Wert, den
Rev. 1/2 empfohlen hatte. **Hier ist nichts zu holen.**

Damit ist auch die Rev.-2-Korrektur bestätigt: das `D: 26` in der F3-Zeile
`[Iris] Shadow info: C: 870/54168 D: 26` ist die **Renderdistanz**, nicht die Schattendistanz.
Die belastbare Zahl bleibt: **870 Sections im Shadow-Pass**, bei 12 Chunks Radius.

### 3.2 Der eigentliche Hebel: Entity Shadows

`ENTITY_SHADOW=1` („Regular Entities"). Der Kommentar des Pack-Autors im Sprachfile:

> *„Enables shadows cast from entities and block entities. […] §c[-]§r Entity Shadows impact
> performance way more than expected, **costing more than 50 % of your fps in some cases**."*

Das ist der Autor über sein eigenes Pack. Und der F3-Screenshot zeigt **194 Entities** in einer
Cobblemon-Welt, in der Pokémon-Modelle ohnehin 3–8× teurer sind als Vanilla-Mobs.

→ **Performance Settings → Entity Shadows → OFF.** Erwartung: der größte Einzelgewinn im ganzen
Setup, deutlich vor Renderdistanz. Optisch verliert man die Schatten unter Mobs und Kisten.

Weitere Kandidaten aus demselben Menü, in dieser Reihenfolge:

| Option | HIGH-Wert | Bemerkung |
|---|---|---|
| **Entity Shadows** | Regular Entities | → **OFF**. Größter Hebel. |
| **Block Reflect Quality** | 3 (höchste) | → 1–2. Screenspace-Reflexionen auf Blöcken. |
| **Light Shaft Quality** | 2 (Medium) | → 1 oder 0. Volumetrisches Licht ist teuer. |
| Real-Time Shadows | 2 (Medium) | Auf OFF schaltet der Pack den kompletten Shadow-Pass ab (`program.world0/shadow.enabled=false`) — und damit auch Lichtstrahlen. Nur als Notfall. |
| Advanced Color Tracing | 0 (OFF) | Bereits aus — **so lassen**, das ist einer der teuersten Effekte überhaupt. |
| World Space Reflections | −1 (OFF) | Bereits aus, gut. |

### 3.3 Warum Optionen „ausgegraut und durchgestrichen" aussehen

**Das Sprachfile des Packs enthält null `§m`-Codes** (Minecrafts Strikethrough-Formatierung) —
nachgezählt. Es wird also nirgends etwas durchgestrichen.

Was stattdessen passiert: Complementary färbt und formatiert seine Werte als Warnsystem.

* `§a` grün = billig · `§e` gelb = mittel · `§c` rot = teuer · `§4§l` dunkelrot fett = sehr teuer
* `§n` **Unterstrichen** und `§o` *kursiv* bei extremen Werten
* `§k` **obfuskiert** — flackernde Zeichen, die um die schlimmsten Werte gesetzt werden

Beispiel aus dem Pack:
```
value.shadowDistance.192.0  = §e12 Chunks              ← aktueller Wert, gelb
value.shadowDistance.768.0  = §4§l§n§o48 Chunks        ← dunkelrot, fett, unterstrichen, kursiv
value.shadowDistance.1024.0 = §4§k-§r§4§l§n§o64 Chunks§r§4§k-   ← flackernde Striche drumherum
value.ENTITY_SHADOW.2       = §4§k-§r§4§l§n§oFull§r§4§k- §r§c[-]
```

Die flackernden `§k`-Striche links und rechts sind genau das, was wie „durchgestrichen" aussieht.
Das ist Absicht des Autors und heißt „nicht anfassen" — **nicht** „gesperrt".

Zusätzlich markiert das Pack Optionen im Namen: **`[*]`** = hat eine Bedingung,
**`[-]`** = Performance-Warnung. Nur eine Option im Performance-Menü wird wirklich abgeschaltet:
Advanced Color Tracing, „will be disabled if Real-Time Shadows is OFF".

**Wo die Optionen liegen:** Videoeinstellungen → Shaderpacks → Complementary auswählen →
Shader-Einstellungen → **`Performance Settings`**. Alles aus 3.2 steht dort auf einer Seite,
zusammen mit dem Profil-Umschalter. Nicht unter „Shadows" oder „Lighting".

**Kontrolle im Spiel:** F3-Zeile `[Iris] Shadow info: C: …` — die Zahl vor dem `/` muss fallen.
Und `[Iris] Profile: HIGH` wird nach einer Änderung zu `HIGH (+1 options changed by user)`.

### 3.4 Machen Shader Sinn?

**5090: ja.** Die GPU lag bei 60 %, es ist Luft da. Mit Entity Shadows aus und RD 20 → 16 läuft
Complementary auf diesem Gerät praktisch gratis.
**ProBook: nein.** **Paul: auf keinen Fall.**
Shader sind rein clientseitig — Horsti spielt mit, Paul ohne, auf demselben Server. Null
Auswirkung aufeinander.

---

## 4. Heap und Java-Argumente

### 4.1 Warum 8192 MB richtig sind

Mehr Heap bringt **nie** fps, sobald er über dem Arbeitsbedarf liegt — er verändert nur das
GC-Verhalten, und zwar in beide Richtungen:

* Arbeitsbedarf dieser Instanz: **~2,5–4 GB** (F3: 2221 MB belegt, 126 MB/s Allocation Rate).
* **G1 sammelt bei großem Heap länger, bevor er aufräumt.** Die dann fällige Mixed-GC muss viel
  mehr Regionen scannen → **einzelne lange Pausen**. Auf einem Client ist eine 200-ms-Pause ein
  sichtbarer Freeze; vier 40-ms-Pausen merkt niemand.
* **Java gibt Speicher nur träge zurück.** Ein 16-GB-Heap reserviert 16 GB *Commit*, auch bei
  2 GB Nutzung — genau das hat den „verfügbaren virtuellen Speicher: 3,67 GB" verursacht.
* Faustregel **Arbeitsbedarf × 2** → 6–8 GB. 32 GB wären aktiv schlechter.
* **Selbstkontrolle:** F3-Zeile `Mem: xx%`. Spitzen unter ~60 % = passt. Dauerhaft über 70 % =
  auf 10 GB gehen.

### 4.2 Java-Argumente

Sie bringen **keine fps**, sondern kürzere GC-Pausen — also bessere 1 %-Lows. Der Nutzen ist auf
**kleinen** Heaps größer, weil dort öfter gesammelt wird. Für Paul lohnt es sich also mehr als
für Horsti.

**Horsti (8192 MB):**
```
-XX:+UseG1GC -XX:MaxGCPauseMillis=40 -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem
-XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=28 -XX:G1MaxNewSizePercent=45
-XX:G1HeapRegionSize=8M -XX:G1ReservePercent=15 -XX:InitiatingHeapOccupancyPercent=20
-XX:SurvivorRatio=32 -XX:MaxTenuringThreshold=1
```

**Paul (3072 MB) und altes ProBook (4096 MB):**
```
-XX:+UseG1GC -XX:MaxGCPauseMillis=40 -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem
-XX:+UseStringDeduplication -XX:G1HeapRegionSize=4M -XX:SurvivorRatio=32
```

`MaxGCPauseMillis=40` gibt G1 ein Pausenziel (öfter, dafür kürzer) · `ParallelRefProcEnabled`
verteilt die Reference-Verarbeitung · `PerfDisableSharedMem` verhindert ein bekanntes
Stotterproblem durch JVM-Statistiken auf der Platte · `G1NewSizePercent/Max` halten die junge
Generation groß, weil Minecraft fast nur kurzlebige Objekte erzeugt · `MaxTenuringThreshold=1`
hält die Survivor-Bereiche klein · `UseStringDeduplication` spart auf kleinen Heaps messbar RAM.

**Wichtig:** Der Modrinth-Speicher-Slider setzt bereits `-Xmx`/`-Xms`. **Kein zweites `-Xmx`** in
die Java-Argumente.

### 4.3 Wie viel RAM für Paul?

**3072 MB — ja, das ist der richtige Startwert.** Die Rechnung:

* 8,0 GB gesamt, Windows 11 belegt im Leerlauf ~4,5–5 GB
* Minecraft braucht **zusätzlich zum Heap** noch ~1–1,5 GB: JVM-Metaspace, GC-Strukturen,
  OpenGL-Treiber, und Cobblemons Texturatlanten liegen off-heap
* 3 GB Heap + 1,3 GB Overhead ≈ **4,3 GB für Minecraft** → knapp, aber machbar

Und dieser Modpack ist ungewöhnlich datenschwer. Aus dem Startlog:

```
Loaded 968 particle effects · 8805 animations from 975 animation groups · 1151 models
1041 posers · 2782 variations · 1025 Pokémon species · 3559 trainer teams/presets/templates
2142 recipes · 2427 advancements · 936 moves · 314 abilities · 168 marks
```

**Vorgehen:** mit 3072 starten und die F3-Zeile `Mem: xx%` beobachten. Dauerhaft über 85 % → auf
3584 hoch und dafür im Hintergrund alles schließen. Ständig unter 50 % → auf 2560 runter, das
gibt Windows Luft. **ModernFix hilft hier am meisten** (dynamisches Ressourcenladen).

---

## 5. Mods

### 5.1 Korrigierte Historie

* **Vorher installiert:** Sodium, Iris, Lithium, FerriteCore, EntityCulling, LambDynamicLights,
  Cobblemon-Stack, Xaero's, e4mc, Bibliotheken.
* **Neu nachgerüstet:** ModernFix, ImmediatelyFast, Krypton, Enhanced Block Entities,
  Sodium Extra, Reese's Sodium Options — dazu ein Sodium-Update auf `0.8.13-beta.1`.
* **Folge:** Der F3-Screenshot (148 fps, 9 ms MSPT, Sodium 0.6.13) stammt aus der Zeit **davor**.
  Er ist als Baseline für das aktuelle Setup nicht mehr gültig. **Ein neuer F3 nach dem
  Terralith-Fix ist die erste Messung, die wieder etwas aussagt.**
* Das Startlog stammt **nach** den Nachrüstungen — die Mixin-Konflikte in 6.3 beziehen sich also
  auf den aktuellen Stand.

### 5.2 Kostenklassen

🔴 groß · 🟠 mittel · 🟡 klein · ⚪ nur Ladezeit/RAM · 🟢 spart Leistung

| Mod | Klasse | Bewertung |
|---|---|---|
| **Complementary Reimagined HIGH** | 🔴🔴 | Nr. 1. Größter Einzelposten darin: **Entity Shadows** (3.2). |
| **Renderdistanz 20** | 🔴 | Skaliert quadratisch, begrenzt zugleich den Shadow-Pass. → 16. |
| **Radical Cobblemon Trainers + API** | 🔴 | 3559 Trainer-Templates, 1559 registrierte Trainer. Spawnt laufend NPCs. Hauptverdächtiger im Server-Tick. Dazu ein Versionskonflikt (6.3). |
| **Cobblemon 1.7.3** | 🟠 | Bedrock-Modelle mit Animationen, 3–8× Vanilla-Mob pro sichtbarem Pokémon. Regler: Spawn-Caps in `config/cobblemon/`. |
| **Fight or Flight Reborn** | 🟠 | Periodische Radius-Scans um Spieler. |
| **Cobblemon Trainer Battle** | 🟠 | Überlappt inhaltlich mit Radical Cobblemon Trainers — beide gleichzeitig ist doppelte Tick-Last. |
| **Xaero's Minimap** | 🟠 | Hintergrund-Chunkscan + zweite Kartenansicht pro Frame, typisch 2–6 %. World Map ist deaktiviert, richtig. |
| **Terralith 2.6.2** *(deaktiviert)* | 🔴 | Deaktiviert, aber die Welt braucht es. Siehe 6.1. |
| **Distant Horizons / Nvidium / Terrain Diffusion / Routes** *(deaktiviert)* | 🔴 | Deaktiviert lassen. Nvidium ist mit Iris-Shadern grundsätzlich inkompatibel. |
| **LambDynamicLights** | 🟡 | **Gemessen 0,031 ms von 6,76 ms = 0,46 %** im Stehen; in Bewegung 1–5 % durch Chunk-Rebuilds. Complementary hat eigenes Handheld-Licht → teilweise redundant. |
| **Cobblemon-Addons** (additions, Capture XP, Pokenav, Tim Core, PlayerXP, CobbleDollars) | 🟡 | Eventgetrieben. Zusammen geschätzt <2 % MSPT. |
| **Zoomify, Controlling, AppleSkin, IPN+libIPN, Mod Menu** | 🟡 | Nur im Menü/Inventar. |
| **Bibliotheken** (Fabric API, FLK, Architectury, Cloth, Forge Config API Port, Lithostitched, Text Placeholder API, Searchables, YACL) | ⚪ | Ladezeit und RAM, im Spiel ~0. |
| **e4mc** | ⚪/🟠 | Im SP 0. Beim Hosten Relay-Hop +20–40 ms. Verdrängt ein Krypton-Mixin (6.3). |
| **FerriteCore** | 🟢 | 30–40 % weniger BlockState-/Model-Speicher. |
| **EntityCulling** | 🟢 | **Gemessen: 34/36 Renderings, 179/194 Ticks übersprungen.** |
| **Sodium / Lithium / ModernFix / ImmediatelyFast / Krypton / EBE / Sodium Extra / Reese's** | 🟢 | Alle aktiv. Wirkung noch nicht gemessen — dafür fehlt spark. |

### 5.3 Noisium

Auf 1.21.1 stehen **NoisiumForked** (Coredex, 1,84 Mio. Downloads) und **Noisiumed** (imbavirus,
107,8 K) zur Wahl. → **NoisiumForked**, größere Basis.

**Aber nicht jetzt.** Es beschleunigt nur die Generierung *neuer* Chunks, die Welt ist weitgehend
generiert — und es patcht Worldgen-Interna, trifft also direkt auf Lithostitched + Terralith.
Erst 6.1 klären, dann messen, dann eventuell Noisium.

---

## 6. Log-Analyse

### 6.0 Stand 16:11 — was der neue Start zeigt (Rev. 4)

**Terralith ist erledigt.** Beweis aus dem 16:10-Start:

```
16:11:23  Applied 1823 biome modifications to 159 of 159 new biomes in 12.23 ms
```

Vorher waren es `616 biome modifications to 64 of 64 new biomes`. **159 statt 64 Biome, und im
ganzen Log keine einzige `Unknown registry key`-Zeile mehr.** Damit ist auch die Frage nach der
Terralith-Version beantwortet: **2.6.2 ist die richtige.** Hätte die Welt Biom-IDs aus einer
anderen Version, würden die Fehler weiterhin auftauchen. Kein Grund, 2.5.8 zu testen.

**Bestätigt:** Minecraft läuft auf der dGPU, nicht auf der iGPU —
`OpenGL Renderer: NVIDIA GeForce RTX 5090 Laptop GPU/PCIe/SSE2`, Treiber 610.74. Beide Adapter
werden erkannt, gewählt wird die richtige.

**spark ist jetzt installiert** (`spark 1.10.109`) und läuft mit Hintergrund-Profiler. Hinweis aus
dem Log: `async-profiler engine is not supported for windows11/amd64, built-in Java engine will be
used instead` — etwas gröber, für unsere Zwecke ausreichend.

**Neuer Fund — NVIDIA-Treiber-Workaround kostet Leistung:**
```
Sodium has applied one or more workarounds …: [NVIDIA_THREADED_OPTIMIZATIONS_BROKEN]
Enabling GL_DEBUG_OUTPUT_SYNCHRONOUS to force the NVIDIA driver to disable threaded command submission
```
Sodium 0.8.13-beta stuft die Threaded Optimizations des Treibers 610.74 als defekt ein und schaltet
sie ab. Das ist ein echter Leistungsverlust im Draw-Call-Pfad — genau dort, wo Minecraft
CPU-limitiert ist. **Treiberwechsel testen** (älterer Studio-/Game-Ready-Zweig), dann prüfen ob die
Zeile verschwindet.

**Nebenbei aus dem Log:** Die Instanz heißt `…\ModrinthApp\profiles\Preset_to_clone\`. Falls das
die Vorlage für Pauls Kopie ist und nicht die echte Spielinstanz — bitte prüfen, dass hier
überhaupt die richtige Welt liegt. Und: das Audiogerät heißt `OpenAL Soft on G27Q2` — der Monitor
ist ein Gigabyte G27Q2. **Dessen tatsächliche Bildwiederholrate in Windows nachsehen**, die Zahl
200 Hz ist bisher nur eine Annahme.

### 6.1 Terralith — und eine Warnung vor Datenverlust

```
Recoverable errors when loading section [-6, -4, 5]: (Unknown registry key in
ResourceKey[minecraft:root / minecraft:worldgen/biome]: terralith:cave/mantle_caves -> using default)
```

Die Welt wurde mit Terralith 2.6.2 generiert, der Mod ist deaktiviert. Pro Chunk-Section eine
ERROR-Zeile.

**⚠️ Die Warnung zuerst: das kann bereits Daten gekostet haben.** Minecraft ersetzt das unbekannte
Biom beim Laden durch einen Fallback und hält es so im Speicher. **Wird dieser Chunk danach
gespeichert, wird der Fallback zurückgeschrieben** — und die Terralith-Biome dieses Chunks sind
dauerhaft weg, auch nach dem Wiedereinschalten von Terralith. Betroffen ist alles, was während
der Terralith-losen Sessions geladen und gespeichert wurde, insbesondere die Startregion.

**Vor jedem weiteren Start:**
1. **Welt sichern** — Instanzordner → `saves/<Weltname>` komplett kopieren.
2. Falls es eine ältere Sicherung **aus der Zeit vor dem Deaktivieren** gibt: die ist wertvoller
   als alles andere. Vergleichen, bevor überschrieben wird.
3. Danach Terralith aktivieren und im Spiel mit F3 (`Biome:`) prüfen, ob in bekannten
   Terralith-Gegenden wieder Terralith-Biome stehen. Steht dort `minecraft:plains`, wo vorher ein
   Terralith-Biom war, ist dieser Bereich beschädigt.

**Wenn Terralith aktiv ist, muss Paul es ebenfalls installiert haben** — es ist ein
Worldgen-/Registry-Mod, das ist keine Wahl.

### 6.2 Warum die Welt nicht lädt — neue Hypothese nach dem 16:11-Start

Die Terralith-Erklärung aus Rev. 3 ist **widerlegt**: die Fehler sind weg, die Welt lädt trotzdem
nicht. Der neue Ablauf:

```
16:11:24  Starting integrated minecraft server / Preparing start region
16:11:25  Preparing spawn area: 0%  →  Time elapsed: 961 ms
16:11:28  Registered 1559 trainers
16:11:28  Changing view distance to 20, from 10
16:11:28  Changing simulation distance to 8, from 0
16:11:28  Can't keep up! Running 2995ms or 59 ticks behind
16:11:28  @Redirect conflict … e4mc          ← Open to LAN gestartet
16:11:58  Can't keep up! Running 492707648ms or 9854152 ticks behind
16:11:58  Shutting down culling task! / Stopping!
```

**Es gibt keinen Absturz und keine Exception.** Der Server startet sauber, die Spawn-Region ist in
961 ms fertig, die Trainer sind registriert. Dann wird die Sichtweite auf 20 gesetzt — und ab da
passiert 30 Sekunden lang nichts mehr im Log, bis `Stopping!`.

Die absurde Zahl `492707648ms` (= 5,7 Tage) steht in **derselben Sekunde** wie `Stopping!`. Das ist
kein echter Messwert, sondern ein Artefakt beim Herunterfahren, wenn der Server-Thread nach langer
Blockade zurückkommt. **Nicht die Ursache, sondern eine Folge.**

**Leitende Hypothese: die Welt lädt einfach länger, als gewartet wurde.** Bei Sichtweite 20 muss
der Server 41 × 41 = **1681 Chunks** bereitstellen. Rechnung:

| Renderdistanz | Chunks | nur laden (~2 ms) | mit Terralith neu generieren (80–150 ms) |
|---|---|---|---|
| **20** | **1681** | 3,4 s | **2,2 – 4,2 min** |
| 16 | 1089 | 2,2 s | 1,5 – 2,7 min |
| 12 | 625 | 1,2 s | 0,8 – 1,6 min |
| **8** | **289** | 0,6 s | **0,4 – 0,7 min** |

Abgebrochen wurde nach **8 Sekunden** (14:59) bzw. **30 Sekunden** (16:11). Beide Male viel zu
früh, falls Chunks neu generiert werden müssen — und das müssen sie, weil Terralith gerade erst
wieder aktiv ist und der Terralith-Generator deutlich teurer rechnet als Vanilla.

**Vorgehen, in dieser Reihenfolge:**

1. **Renderdistanz im Hauptmenü auf 8 stellen**, *bevor* die Welt geöffnet wird. Das reduziert die
   Arbeit um 83 %.
2. **Mindestens 5 Minuten warten.** Nicht 30 Sekunden.
3. **Währenddessen prüfen, ob es arbeitet oder hängt:**
   * Task-Manager → `javaw.exe`: dauerhaft hohe CPU-Last = es rechnet, weiterwarten.
     Nahe 0 % = echter Deadlock.
   * `latest.log` mit einem Editor öffnen, der live nachlädt: kommen noch Zeilen? Dann läuft es.
4. Wenn es wirklich hängt: **neue Testwelt** (kleiner Vanilla-Seed) anlegen. Lädt die sofort, liegt
   es an der bestehenden Welt und nicht an den Mods.
5. Erst dann Mods halbieren. Reihenfolge der Verdächtigen: **Xaero's Minimap** (scannt beim Join
   Chunks), **ModernFix** (greift in Weltlade- und Ressourcenpfade ein), **Radical Cobblemon
   Trainers** (1559 Trainer beim Start).
6. **Was ich dafür brauche:** das `latest.log` **während** des Hängens, nicht nach dem Beenden —
   plus die CPU-Last von `javaw.exe` aus Schritt 3.

### 6.2.1 Alte Hypothese (Rev. 3, widerlegt)

Aus dem Log, in dieser Reihenfolge:

```
14:59:09  Preparing start region for dimension minecraft:overworld
14:59:09  Recoverable errors when loading section …   (tausendfach, Server thread)
14:59:12  Registered 1559 trainers
14:59:12  Changing view distance to 20, from 10
14:59:12  Changing simulation distance to 8, from 0
14:59:12  Can't keep up! Is the server overloaded? Running 2628ms or 52 ticks behind
14:59:13  @Redirect conflict … e4mc …            ← Open to LAN wurde gestartet
14:59:20  Shutting down culling task! / Stopping!
```

**Wahrscheinlichste Ursache: der Terralith-Fehler-Spam.** Jede ERROR-Zeile wird synchron auf dem
Server-Thread formatiert und in Konsole *und* `latest.log` geschrieben. Bei Sichtweite 20 lädt
der Beitritt über 1600 Chunks mit je bis zu 24 Sections — das sind Zehntausende Log-Zeilen, die
den Server-Thread blockieren. Der Bildschirm „Welt wird geladen / Gelände wird geladen" kommt
dann nicht weiter. Das `Can't keep up! … 2628ms` passt exakt dazu.

**Terralith einzuschalten sollte das also beheben, nicht verschlimmern.**

Es steht keine Exception im Log — kein Absturz, sondern ein sauberes `Stopping!`. Das spricht
dafür, dass Minecraft nicht abgestürzt ist, sondern hing bzw. beendet wurde.

**Falls es nach dem Terralith-Fix immer noch hängt**, sauber halbieren statt raten:
1. Neue Testwelt anlegen (Vanilla-Seed). Lädt die? → das Problem liegt an der bestehenden Welt.
2. Sichtweite vor dem Beitritt auf 8 stellen.
3. Die sechs neuen Mods (ModernFix, ImmediatelyFast, Krypton, EBE, Sodium Extra, Reese's) zuerst
   alle deaktivieren, dann einzeln zuschalten. **ModernFix** ist der wahrscheinlichste Kandidat,
   weil es als einziges davon in Weltlade- und Ressourcenpfade eingreift.
4. `latest.log` **vollständig** sichern — der interessante Teil ist die letzte Minute vor dem
   Hängen, nicht der Start.

### 6.3 Konflikte

**ModernFix ↔ Lithium — harmlos, nichts tun**
```
Method overwrite conflict for removeIf in modernfix … previously written by lithium … Skipping method.
```
Beide optimieren denselben `SortedArraySet` für Chunk-Tickets, Lithium gewinnt.

**Krypton ↔ e4mc — harmlos**
```
@Redirect conflict. Skipping krypton…ServerLoginNetworkHandlerMixin … already redirected by e4mc
```
Betrifft nur den Login-Handshake, nicht den heißen Paketpfad. Kryptons Netty- und
Kompressionsoptimierungen laufen weiter.

**Radical Cobblemon Trainers ↔ RCT-API — beheben**
```
Unknown loot table called rctmod:generic/common/evolution
Unknown loot table called rctmod:generic/common/training
```
RCT `0.18.1-beta` gegen API `0.15.2-beta`. → **RCT API auf die zu 0.18.1 passende Version
aktualisieren.**

**Fehlendes Mega Showdown — kosmetisch**
```
Model validation failure for 'elite_four_lorelei_004e' — invalid held item 'mega_showdown:blue_orb'
```
Optionale Integration von RCT, der Mod ist nicht installiert. Betroffene Trainer spawnen ohne das
Item. Ignorieren oder Mega Showdown nachinstallieren (kostet zusätzliche Last).

### 6.4 Iris-Uniforms — meine Empfehlung aus Rev. 3 war falsch

```
Failed to resolve uniform inPaleGarden … Unknown variable: BIOME_PALE_GARDEN
Failed to resolve uniform endFlashFactor0 … Unknown variable: endFlashIntensity
```

Iris ist inzwischen auf **1.8.14-beta.1** — und **die Meldungen sind unverändert da**. Das Update
war also nicht die Lösung. Die tatsächliche Ursache:

* **`BIOME_PALE_GARDEN`**: Der Pale Garden ist ein Biom aus **1.21.4**. Auf **1.21.1 existiert es
  nicht**, also kann Iris die Biom-Variable nicht auflösen.
* **`endFlashIntensity`**: dasselbe Muster, ein Uniform aus einer neueren Iris-/MC-Generation.

**Complementary r5.8.1 ist für neuere Minecraft-Versionen gebaut** und referenziert Dinge, die es
auf 1.21.1 schlicht nicht gibt. Das ist **kein Fehler und nicht behebbar** — außer man nimmt eine
ältere Complementary-Version, was sich für zwei kosmetische Effekte (Pale-Garden-Nebel,
End-Blitz) nicht lohnt. **Ignorieren.**

Ebenfalls normales Rauschen und nicht behebbar: `IViewRotMat`, `Sampler2`,
`Force-disabling mixin 'features.render.world.sky.*' … added by mods [iris]` (Iris übernimmt den
Himmel von Sodium — gewollt), `[Indigo] Different rendering plugin detected` (Sodium übernimmt).

### 6.5 Was harmlos ist und ignoriert werden kann

Aus den 16:10-Logs, damit nicht weiter danach gesucht wird:

| Meldung | Bewertung |
|---|---|
| `No data fixer registered for cobblemon:pokemon` u. v. a. (ERROR!) | **Völlig normal.** Modded Entities registrieren keine DataFixer; Vanilla loggt das als ERROR. Betrifft jeden modded Server. |
| ~150× `Missing sound for event: cobblemontrainerbattle:battle.leader.*` | Der Mod referenziert Kampfmusik, die er nicht mitliefert — dafür gibt es ein separates Musik-Resourcepack. Ohne das bleibt es still. Kosmetisch. |
| `File cobblemon:sounds/…/galarian_ponyta_cry.ogg does not exist` | Fehlender Cry, kosmetisch. |
| `Unable to load model 'minecraft:track_arrow' … cobblenav:item/track_arrow` | Bug in cobblenav, kosmetisch. |
| `Found 'parent' loop while loading model 'minecraft:item/carved_pumpkin'` | Modelschleife, kosmetisch. |
| `Missing textures in model cobblemon:relic_coin_pouch` | kosmetisch. |
| `Reference map '…refmap.json' could not be read` | Normal bei Release-Builds mancher Mods. |
| `@Mixin target … was not found` für JEI, Adorn, Controlify, SuperMartijn642, quick.battle | Optionale Kompatibilitäts-Mixins für Mods, die nicht installiert sind. Genau so gedacht. |
| `Removed resource pack spark from options because it is no longer compatible` | Harmlos. |
| `Compression will use Java, encryption will use Java` | Krypton findet auf Windows keine nativen Velocity-Bibliotheken und fällt auf Java zurück. Auf Windows normal. |
| `Update available for fabric-api@0.116.14 (-> 0.116.15)` | Kann mitgenommen werden, eilt nicht. |

**Eine Beobachtung mit Substanz:** Lithium schaltet beim Start mehrere eigene Optimierungen ab:
```
Option 'mixin.entity.collisions.fluid' requires 'mixin.util.block_tracking=true' but found 'false'
Option 'mixin.experimental.entity.block_caching.*' … Setting … =false
```
Bei `150 options available, 0 override(s) found` sind das Lithiums eigene Defaults, keine
Fehlkonfiguration. Ein Teil des Entity-Block-Cachings ist damit inaktiv. Nicht dramatisch, aber
gut zu wissen, wenn das spark-Profil später Entity-Kollisionen weit oben zeigt.

**Neuer Mixin-Konflikt, harmlos:**
```
Method overwrite conflict for method_21740 in modernfix … remove_biome_temperature_cache.BiomeMixin
… previously written by lithium … Skipping method.
```
Wie schon bei `removeIf`: beide optimieren dasselbe, Lithium gewinnt.

---

## 7. Paul — Instanz und Mod-Matrix

### 7.1 Erst duplizieren, dann ändern

Paul spielt derzeit auf Horstis **geteilter** Instanz. Wenn er dort Mods abschaltet, überschreibt
Horstis nächstes **„Push update"** seine Änderungen wieder.

**Deshalb: Paul dupliziert die Instanz lokal und arbeitet ab dann auf der Kopie.** In der
Modrinth-App über das Instanz-Kontextmenü (⋮ / Rechtsklick → *Duplicate* bzw. *Kopieren*). Danach
ist er unabhängig — muss Updates aber selbst nachziehen, wenn Horsti Inhalts-Mods ändert.

> Die genaue Menübezeichnung in der aktuellen Modrinth-App habe ich nicht verifiziert. Falls es
> keine Duplizieren-Funktion gibt, ist der Weg: neue leere 1.21.1-Fabric-Instanz anlegen und die
> Mods aus 7.2 hineinkopieren.

**Solange er nur clientseitige Mods abschaltet, kann er weiter mit Horsti spielen.** Alles aus der
Pflichtspalte muss bleiben.

### 7.2 Mod-Matrix

**PFLICHT — muss identisch zu Horsti sein (Registry-/Content-Sync):**

Cobblemon · Cobblemon additions · Cobblemon Capture XP · Cobblemon Fight or Flight Reborn ·
Cobblemon Pokenav · Cobblemon Tim Core · Cobblemon Trainer Battle · Cobblemon: PlayerXP ·
CobbleDollars · Radical Cobblemon Trainers · Radical Cobblemon Trainers API ·
**Terralith** (sobald Horsti es aktiviert) · Lithostitched · Text Placeholder API · Fabric API ·
Fabric Language Kotlin · Architectury API · Cloth Config API · Forge Config API Port ·
YetAnotherConfigLib · Searchables

**BEHALTEN — reine Performance-Mods, kosten nichts, bringen viel:**

Sodium · Sodium Extra · Reese's Sodium Options · Lithium · ModernFix · FerriteCore ·
ImmediatelyFast · EntityCulling · Enhanced Block Entities · Krypton · Mod Menu · Controlling

**ABSCHALTEN bei Paul — rein clientseitige Optik:**

| Mod | Wirkung |
|---|---|
| **Iris Shaders** | Ohne Shader nutzlos; spart RAM und Ladezeit |
| **Complementary Reimagined** (Shaderpack) | rein clientseitig |
| **LambDynamicLights** | siehe 7.3 |
| **Xaero's Minimap** | optional, 2–6 % — erster Kandidat, wenn es hakt |

**EGAL:** AppleSkin, Zoomify, Inventory Profiles Next + libIPN (kosten nur im Inventar) ·
**e4mc** braucht nur der Host, schadet bei Paul aber nicht.

**DEAKTIVIERT LASSEN:** Distant Horizons · Nvidium · Terrain Diffusion · Routes ·
Xaero's World Map

### 7.3 LambDynamicLights: im Spiel aus vs. Mod aus

| | Wirkung |
|---|---|
| **Im Spiel aus** (Videoeinstellungen → Dynamic Lights → OFF) | Die Laufzeitkosten fallen praktisch auf null: kein Lichtquellen-Scan, keine eingeplanten Chunk-Rebuilds. Gemessen waren das im Stehen ohnehin nur **0,46 %**, in Bewegung 1–5 %. |
| **Mod deaktiviert** (Modrinth) | Zusätzlich: ein paar MB RAM weniger, keine Mixins im Renderpfad, etwas kürzere Ladezeit. |

**Der fps-Unterschied zwischen beiden ist vernachlässigbar** — der Sprung liegt zwischen „an" und
„aus", nicht zwischen den beiden Aus-Varianten. Für Paul lohnt das Deaktivieren trotzdem, aber
wegen der **paar MB RAM** auf einem 8-GB-System, nicht wegen der Bildrate.

### 7.4 Braucht Cobblemon Terralith?

**Nein.** Cobblemon-Spawns hängen an Biom-**Tags** (`#minecraft:is_forest`, `#c:…`) und an
expliziten Biom-IDs. Terralith bringt keine einzige zusätzliche Pokémon-Art mit.

Was Terralith bringt: rund 85–100 zusätzliche Biome, die per Tag in dieselben Kategorien fallen —
also **mehr Abwechslung, wo dieselben Pokémon vorkommen**, nicht mehr Arten. Zum Vergleich, aus
dem aktuellen Log ohne Terralith:

```
Applied 616 biome modifications to 64 of 64 new biomes
```

64 Biome. Mit Terralith wären es ein Vielfaches.

**Für diese Welt ist die Diversitätsfrage aber gar nicht der Punkt** — die Welt *wurde* mit
Terralith generiert, deshalb muss es zurück (6.1).

**Auswirkung auf Pauls Laptop:** Terralith ist ein Worldgen-Mod. Die Generierung passiert auf
Horstis Rechner, nicht auf Pauls. Bei Paul kostet es Registry-Einträge und JSON-Daten (wenig RAM)
sowie etwas mehr sichtbare Geometrie, weil Terralith-Terrain dramatischer ist. Auf Renderdistanz 6
ist das im niedrigen einstelligen Prozentbereich. **Vertretbar — und ohnehin alternativlos.**

---

## 8. Pauls Einstellungsliste

### 8.1 Zuerst: GPU klären

`Win + R` → `dxdiag` → Reiter **Anzeige**. Der MEDION S17403 existiert mit **UHD 620** und mit
**GeForce MX330**.

**Falls MX330 vorhanden:** Einstellungen → System → Anzeige → **Grafik** → Durchsuchen →
`javaw.exe` der Modrinth-Java-Installation hinzufügen → **„Hohe Leistung"**. Ohne das läuft
Minecraft auf der iGPU, obwohl eine dedizierte Karte verbaut ist — **potenziell Faktor 2**.

### 8.2 Modrinth

* Memory allocated: **3072 MB**
* Java: 21 (mitgeliefert)
* Custom Java arguments: siehe 4.2 (Paul-Variante)

### 8.3 Minecraft — Videoeinstellungen

| Einstellung | Wert |
|---|---|
| Grafik | Schnell |
| Renderdistanz | **6** (max. 8) |
| Simulationsdistanz | im MP egal, der Host setzt sie |
| Bildratenbegrenzung | 60 |
| VSync | Aus |
| Wolken | Aus |
| Partikel | Minimal |
| Mipmap-Stufen | 0 |
| Weiche Beleuchtung | Aus |
| Entfernung von Objekten | 50 % |
| Biome-Blend | **Aus / 0** — auf schwachen CPUs einer der größten Einzelposten |
| Vollbild | An (exklusives Vollbild ist auf iGPUs schneller) |
| GUI-Skalierung | 2 |
| Auflösung | notfalls 1600×900 im Vollbild |

### 8.4 Sodium Extra / Reese's — hier liegt der größte Teil des Gewinns

Animationen (Wasser, Lava, Feuer, Texturen) **alle aus** — auf iGPUs oft 10–20 % · Partikel aus ·
Wolken aus · Nebeldistanz kurz · Sterne / Sonne / Mond / Wetter aus · Vignette aus ·
Verzauberungsglanz aus

### 8.5 Windows

1. **Intel-Grafiktreiber direkt von Intel** installieren (Medion liefert oft Stände von 2020).
2. Netzteil anstecken, Windows-Leistungsregler auf **„Beste Leistung"**.
3. **Autostart aufräumen** (Task-Manager → Autostart) — auf 8 GB sind das echte Prozente.
4. **Xbox Game Bar und Hintergrundaufzeichnung aus** (Einstellungen → Gaming → Aufzeichnungen).
5. **Defender-Ausnahme** für den Modrinth-Instanzordner.
6. Browser komplett schließen, Discord-Hardwarebeschleunigung aus.
7. OneDrive pausieren, Windows-Update-Nutzungszeit setzen.
8. **Auslagerungsdatei systemverwaltet lassen** — bei 8 GB RAM notwendig, nicht abschalten.
9. BIOS/EC-Update (Stand 25.06.2021) für besseres Powerlimit-Verhalten.
10. VBS bleibt aus (ist bereits aus).

---

## 9. Anschreiben an EWE

Vorlage für den Kundenservice. Platzhalter in `<…>` ersetzen.

```
Betreff: Anfrage höhere Bandbreite / Profilwechsel — Anschluss <Kundennummer>

Sehr geehrte Damen und Herren,

ich nutze an meinem Anschluss <Kundennummer / Vertragsnummer> derzeit einen
Tarif mit 50 Mbit/s im Downstream und 10 Mbit/s im Upstream. Der Anschluss
läuft stabil, ich würde aber gerne prüfen lassen, ob eine höhere Bandbreite
möglich ist — vor allem im Upstream.

Hintergrund: Die Leitungsdaten meines Routers (FRITZ!Box 7690, FRITZ!OS 8.25)
deuten auf deutliche Reserven hin. Auszug aus den DSL-Informationen:

  Verbindungstyp:          VDSL2 17a (ITU G.993.2), Traegersatz B43
  Aktuelle Datenrate:      55.168 / 11.260 kbit/s
  DSLAM-Datenrate max.:    55.168 / 11.264 kbit/s
  Leitungskapazitaet:      79.835 / 23.293 kbit/s
  Stoerabstandsmarge:      12 dB / 15 dB
  Leitungsdaempfung:       15 dB / 15 dB
  Ungefaehre Leitungslaenge: 284 m
  G.Vector:                aus
  Fehlerzaehler ES/SES/CRC: 0 / 0 / 0 (auch ueber 24 Stunden)

Die Leitung ist also fehlerfrei, sehr kurz und laut Router zu deutlich mehr
in der Lage, als aktuell freigeschaltet ist.

Konkret meine Fragen:

1. Ist an meiner Adresse ein Tarif mit hoeherer Bandbreite verfuegbar
   (z. B. 100/40 oder 250/40 Mbit/s)?
2. Unterstuetzt der Port, an dem ich haenge, das Profil 35b
   (Supervectoring)? Aktuell laeuft 17a ohne Vectoring.
3. Falls 35b technisch moeglich ist: waere dafuer ein Portwechsel oder eine
   Umschaltung im DSLAM noetig, und was wuerde das kosten?
4. Falls kein hoeherer Tarif moeglich ist: gibt es die Option, allein den
   Upstream anzuheben?

Der Upstream ist fuer mich der wichtigere Punkt — ich hoste zeitweise
Anwendungen, fuer die 10 Mbit/s die Grenze sind.

Vielen Dank und viele Gruesse
<Name>
<Adresse>
<Kundennummer>
<Telefon / E-Mail>
```

---

## 10. Testprotokoll

### 10.1 Reihenfolge — Stand Rev. 4

1. ~~Welt sichern~~ · ~~Terralith aktivieren~~ · ~~spark installieren~~ — **erledigt**
2. **Renderdistanz auf 8 stellen, Welt öffnen, 5 Minuten warten** (6.2). Ohne das geht nichts
   weiter.
3. Drin? Dann **F3-Screenshot** — die alte Baseline ist ungültig (5.1). Und F3-Biome prüfen,
   ob die Terralith-Biome wieder stimmen (6.1).
4. **`/spark profiler start --timeout 120 --only-ticks-over 20`** laufen lassen, Report-Link
   schicken.
5. **Entity Shadows OFF** testen (3.2) — vermutlich der größte Einzelgewinn.
6. Danach Renderdistanz schrittweise wieder hoch, bis es sich falsch anfühlt.
7. Nebenbei: RCT-API-Version fixen (6.3), NVIDIA-Treiber gegen den Sodium-Workaround testen (6.0).

### 10.2 spark

Installiert als `spark 1.10.109`. Befehle:

```
/spark profiler start --timeout 120 --only-ticks-over 20   # interner Server
/sparkc profiler start --timeout 60                        # Client
/spark tps      /spark health      /spark heapsummary
```

Der Web-Report gruppiert nach Mod-Paketen (`com.cobblemon…`, `hd42.rctmod…`,
`dev.vaniron.fightorflight…`). Das ist die exakte Antwort auf „welcher Mod frisst wie viel" —
alles in 5.2 mit einer Schätzung ist bis dahin ein Modell.

### 10.3 Test A — Client-fps

Bedingungen strikt: feste Testwelt, fester Punkt, plus ein zweiter Punkt im Freien mit vielen
Pokémon · `/gamerule doDaylightCycle false` · `/time set 6000` · `/weather clear` · Vollbild,
VSync aus · pro Messung 60 s stehen (bis `Busy=00`), dann 60 s messen · erfasst werden
**avg fps, 1 %-Low, GPU %, MSPT**.

| Schritt | Änderung | Beantwortet |
|---|---|---|
| A0 | Baseline nach Terralith-Fix | neue Referenz |
| A1 | **Entity Shadows → OFF** | der Verdacht aus 3.2 |
| A2 | Shader komplett aus | Gesamtkosten der Shader |
| A3 | Renderdistanz 20 → 16 | Renderdistanz inkl. Shadow-Pass |
| A4 | Block Reflect Quality 3 → 1 | |
| A5 | Light Shaft Quality 2 → 0 | |
| A6 | EntityCulling aus | was der Mod spart |
| A7 | Xaero's Minimap aus | Client-Kosten der Karte |
| A8 | Iris 1.8.14-beta.1 | ob die Uniform-Warnungen weg sind |
| A9 | Spawn-Feld, dann `/kill @e[type=cobblemon:pokemon]` | Kosten der Pokémon |

### 10.4 Test B — Server-Tick

Welt mit realistischer Population, 5 Minuten laufen lassen, dann
`/spark profiler start --timeout 120 --only-ticks-over 20`. Erwartete Rangliste zum Abgleich:
**Radical Cobblemon Trainers**, Cobblemon, Fight or Flight, Trainer Battle, Chunk-Ticking.
Danach `/spark heapsummary`. **Erst nach dem Terralith-Fix messen** — der Fehler-Spam verfälscht
sonst alles.

### 10.5 Test C — Netzwerk

Bufferbloat auf `waveform.com/tools/bufferbloat`, 3× Kabel und 3× WLAN 5 GHz · Priorisierung in
der FRITZ!Box setzen und wiederholen · `iperf3` im LAN (Kabel↔Kabel, Kabel↔WLAN) zur Trennung von
Heimnetz und DSL · in-game F3-Ping und `/spark ping` mit Paul, einmal über e4mc, einmal über
Portfreigabe 25565 — die Differenz ist der Relay-Aufschlag · EWE anschreiben (9).

---

## 11. Was noch offen ist (Stand Rev. 4)

**Blockiert alles andere:**
1. **Lädt die Welt mit Renderdistanz 8 und 5 Minuten Geduld?** (6.2)
2. Falls nein: `latest.log` **während** des Hängens + CPU-Last von `javaw.exe` aus dem
   Task-Manager.

**Danach:**
3. **Neuer F3-Screenshot** — die alte Baseline ist ungültig (5.1).
4. **spark-Report** (`/spark profiler start --timeout 120 --only-ticks-over 20`).
5. **F3-Biomecheck** in bekannten Terralith-Gegenden — steht dort wieder ein Terralith-Biom, oder
   `minecraft:plains`? (6.1)

**Unabhängig davon:**
6. **`dxdiag` von Paul** — UHD 620 oder MX330? Blockiert 8.1.
7. **Tatsächliche Bildwiederholrate des Gigabyte G27Q2** in Windows (6.0).
8. **Video-Einstellungen des 5090** als Screenshot.
9. **HWiNFO-Log** vom 5090 über 10 Minuten: CPU-/GPU-Takt, Temperaturen, Power-Limit.
10. **VBS/HVCI-Status** des 5090.
11. **`config/cobblemon/`** — die Spawn-Caps sind ein direkter Performance-Regler.
12. Liegt in der Instanz `Preset_to_clone` überhaupt die richtige Welt? (6.0)
