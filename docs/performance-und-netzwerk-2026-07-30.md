# Performance- & Netzwerk-Analyse — 30.07.2026 (Rev. 2)

> Messreport, **kein** Plan. Grundlage: 5 Speedtest-CSVs (13:42–14:04), F3-Screenshot der
> Cobblemon-Instanz, FRITZ!Box-7690-Auszüge inkl. **DSL-Informationen**, Systeminfos von drei
> Rechnern, **vollständige Modliste (41 aktiv / 6 deaktiviert)**, Log-Auszüge des Instanzstarts,
> Modrinth-Java-Einstellungen.
>
> **Rev. 2 korrigiert Rev. 1 an zwei Stellen** (siehe Abschnitt 5.0 und 3.1) — beide entstanden,
> weil die Modliste in Rev. 1 bei „libIPN" abgeschnitten war.
>
> Setup laut Horsti: **Singleplayer → Open to LAN → e4mc**, Link an Paul. Monitor: 2560×1440,
> 200 Hz, G-Sync, DisplayPort direkt an der dGPU. Heap inzwischen von 16 auf **8192 MB** gesenkt.

---

## 0. Kurzfassung

1. **Größter Einzelfund: Terralith ist deaktiviert, aber die Welt wurde damit generiert.** Das Log
   ist voll von `Unknown registry key … terralith:birch_taiga -> using default`. Folge: falsche
   Biome → **falsche Cobblemon-Spawns**, falsche Farben, Nahtstellen im Terrain und Fehler-Spam
   auf dem Server-Thread. Muss entschieden werden (Abschnitt 6.1).
2. **Zweitgrößter Fund: die DSL-Leitung wird verschenkt.** Leitungskapazität **79,8 / 23,3 Mbit/s**,
   gebucht/geliefert **55,2 / 11,3**. 284 m Leitung, 15 dB Dämpfung, 12/15 dB SNR-Marge,
   **0 Fehler**. Das ist eine sehr gute kurze Leitung an einem zu kleinen Tarif.
3. **Korrektur zu Rev. 1:** Lithium, ModernFix, ImmediatelyFast, Krypton, Enhanced Block Entities,
   Sodium Extra und Reese's sind **alle installiert**. Die 9 ms MSPT waren also *schon mit*
   Lithium. Der Tick-Kostenverdacht verschiebt sich damit auf **Radical Cobblemon Trainers**,
   Fight or Flight und die Simulationsdistanz.
4. **Korrektur zu Rev. 1:** `[Iris] Shadow info: … D: 26` ist die **Renderdistanz**, nicht die
   Schattendistanz des Packs. Die belastbare Zahl ist **870 gerenderte Sections im Shadow-Pass**.
   Der Hebel bleibt derselbe, aber er heißt **Renderdistanz**, nicht Schatten-Slider.
5. **Monitor-Frage ist geklärt und die Lösung ist eindeutig:** VSync **aus**, G-Sync **an**,
   Limit **190 fps**. Behebt das 148-fps-Pendeln.
6. **8192 MB Heap ist richtig.** Mehr wäre messbar schlechter (Abschnitt 4).
7. **Paul bekommt Horstis Mods nicht automatisch.** Open to LAN + e4mc überträgt keine Mods.
   Paul braucht eine eigene Instanz mit passender Modliste — Shader darf er einfach weglassen.
8. **spark ist nicht installiert.** `/spark profiler` kann daher noch nicht funktionieren.

---

## 1. Netzwerk — die Messung

| # | Zeit | Setup | DL (10 MB) | UL | Idle-Latenz | Bufferbloat |
|---|---|---|---|---|---|---|
| R1 | 13:42:09 | nur WLAN, Fortnite lädt | **9,9** Mbit/s | 5,1 | 100 ms | **+123 ms** |
| R2 | 13:49:17 | WLAN + Kabel, Fortnite lädt | **18,5** | 5,4 | 81 ms | **+118 ms** |
| R3 | 13:52:51 | nur Kabel, Fortnite lädt | **9,1** | 5,1 | **22 ms** | **+124 ms** |
| R4 | 14:01:54 | Kabel, Fortnite gestoppt, FRITZ!OS frisch | **50,42** | **9,78** | 25 ms | +41 ms |
| R5 | 14:03:04 | Kabel, Fortnite „fortgesetzt" | **50,72** | **9,71** | 27 ms | **+18 ms** |

Median über 4–6 Wiederholungen. Bufferbloat = Latenz-p95 unter Last minus Idle-Minimum.

**Die Oberfläche hatte recht.** R4/R5 liefern 101 % bzw. 94 % der damals angezeigten Sync-Rate
(50,3 / 10,4). R1–R3 waren durch den parallelen Download verdorben, nicht durch WLAN vs. LAN:
alle drei teilen denselben Bufferbloat (~120 ms) und denselben halbierten Upload (~5 statt 9,7),
und keiner erreichte die 10-MB-Upload-Stufe des Tests (die schaltet nur bei schneller Leitung frei).

**Was das Kabel wirklich brachte:** Grundlatenz **100 ms → 20–22 ms** bei identischer Störlast
(R3 gegen R1). Für Minecraft ist genau das die relevante Zahl.

**Warum R5 der beste Lauf ist, obwohl Fortnite „wieder an" war:** 50,72 Mbit/s sind 100 % der
Leitung — parallel zu einem echten Download physikalisch unmöglich. Es gibt in R5 genau ein
gestörtes Fenster (14:03:07–11, Einbruch auf 15,1 Mbit/s, Latenz 81–145 ms), das nach kurzem
Anlaufen des Epic-Downloaders aussieht. Danach war er wieder weg. Das FRITZ!OS-Update hat nichts
repariert, es fiel nur mit dem Ende der Störlast zusammen.

### 1.1 DSL-Informationen — die Leitung ist besser als der Tarif

| Kennzahl | Empfangen | Senden | Bewertung |
|---|---|---|---|
| **Leitungskapazität** | **79.835 kbit/s** | **23.293 kbit/s** | Was die Leitung physikalisch könnte |
| DSLAM-Datenrate max. | 55.168 | 11.264 | Was der Anbieter freischaltet |
| Aktuelle Datenrate | 55.168 | 11.260 | Voll ausgeschöpft |
| Störabstandsmarge | 12 dB | 15 dB | Gesund (6 dB wäre die Grenze) |
| Leitungsdämpfung | 15 dB | 15 dB | Sehr gut |
| Leitungslänge | 284 m | | Kurz |
| Latenz (Interleaving) | 7 ms | 4 ms | Gut |
| ES / SES / CRC | **0 / 0 / 0** | **0 / 0 / 0** | Fehlerfrei, auch über 15 min |
| Profil / Trägersatz | 17a / B43 | | VDSL2 17a |
| **G.Vector** | **aus** | **aus** | |
| G.INP | aus | aus | Bei 0 Fehlern unkritisch |

**Auswertung:** Die Leitung hat **45 % Reserve im Downstream und 107 % im Upstream**, die der
Tarif nicht freigibt. Bei 284 m und 15 dB Dämpfung ist das eine der besten Ausgangslagen, die man
haben kann. Mit **Profil 35b (Supervectoring)** wären auf dieser Länge realistisch 150–250 Mbit/s
down und 40 Mbit/s up drin — G.Vector ist aktuell aus, das Profil ist 17a.

→ **Konkrete Maßnahme: bei EWE/swb/osnatel nach einem schnelleren Tarif fragen.** Nicht wegen des
Downloads, sondern wegen des **Uploads**: 11 → 40 Mbit/s würde die Hosting-Situation komplett
verändern (Abschnitt 1.3).

**Nebenbei:** Die Sync-Rate ist nach dem FRITZ!OS-Update von 50,3/10,4 auf **55,17/11,26**
gestiegen (+10 %). Ein neuer Speedtest sollte jetzt ~54 Mbit/s zeigen — die alten 50,7 waren
gegen die alte Sync-Rate gemessen.

### 1.2 Schwachstellen im Heimnetz

| Fund | Bewertung |
|---|---|
| **FRITZ!WLAN Repeater 310** — Wi-Fi 4, nur 2,4 GHz, 144 Mbit/s, 20 MHz, Baujahr ~2013 | Schwächstes Glied. Single-Radio-Repeater **halbieren** den Durchsatz und fressen Airtime für alle 2,4-GHz-Geräte. Drei Geräte hängen dahinter. |
| 6 Geräte auf 2,4 GHz, 1 auf 5 GHz | Erklärt die 80–135 ms WLAN-Latenz in R1/R2. |
| OnePlus-15: Wi-Fi 7 + MLO — und trotzdem 2,4 GHz, 20 MHz, ↑6 Mbit/s | Band-Steering greift nicht. |
| Android: 5 GHz, 80 MHz, 2×2, aber nur 288 Mbit/s | Bei 80 MHz/2×2 wären ~1200 möglich → niedriger MCS (Entfernung/Wand). |
| devolo dLAN 200 AVmini am WAN-Port (auf LAN umgestellt), 100 Mbit-Link | HomePlug AV 1. Generation: real 40–90 Mbit mit hohem Jitter. Für Minecraft ungeeignet. |
| Horsti5090 an LAN 1 mit 2,5 Gbit/s | Perfekt, nichts zu tun. |

**Sofortmaßnahmen:** Repeater 310 ausmustern oder die Geräte dahinter direkt auf die Box holen ·
2,4-GHz-Dauergäste per getrennter SSID aufs 5-GHz-Band zwingen · Gaming-PC bleibt am Kabel ·
FRITZ!Box → *Internet → Filter → Priorisierung* → Horsti5090 als „Echtzeitanwendung" (schützt
genau gegen den Fall R1–R3) · Epic-Launcher dauerhaft auf ~30 Mbit/s drosseln.

### 1.3 Was 55/11 für das e4mc-Hosting bedeutet

Der Download ist egal, der **Upload ist die harte Grenze**.

* Grob **100–300 kbit/s Upload pro Spieler** im normalen Spiel, **1–3 Mbit/s in Spitzen**
  (Beitritt, Chunk-Streaming, schnelles Reisen). Cobblemon liegt am oberen Ende.
* **11,3 Mbit/s tragen realistisch 3–4 Mitspieler.** Mit Paul allein ist reichlich Luft.
* **e4mc leitet über einen Relay-Server** → ein zusätzlicher Hop, typisch **+20–40 ms** für Paul.
  Bei 25 ms Grundlatenz ist das eine Verdopplung. Für zwei Leute akzeptabel; die Alternative ist
  eine Portfreigabe auf 25565 in der FRITZ!Box (direkt, ohne Relay).
* **Jeder parallele Upload killt die Session:** OneDrive, Cloud-Backup, Steam-Upload, Stream.
* Wichtig zur Bandbreite: der Server schickt Paul Chunks nur bis
  **min(Horstis Renderdistanz, Pauls Renderdistanz)**. Paul auf RD 6 ist also von sich aus billig —
  Horstis RD 26 kostet vor allem **Horstis eigenen Client und den internen Server**, nicht Pauls
  Leitung.
* **Was Horsti dagegen für alle setzt, ist die Simulationsdistanz.** Die ist global und der
  eigentliche Tick-Treiber.

---

## 2. Die drei Rechner

### 2.1 Horsti5090 — HP OMEN MAX 16 (Ultra 9 275HX, RTX 5090 Laptop, 64 GB)

Kein Hardware-Engpass. Was bleibt:

* **Auslagerungsdatei ist auf 7,02 GB fix** bei 64 GB RAM, und der verfügbare virtuelle Speicher
  lag bei 3,67 GB. Mit dem alten 16-GB-Heap lief das Commit-Limit voll → OOM-Meldungen ohne echten
  RAM-Mangel. Mit 8 GB Heap ist es entspannter, aber die Auslagerungsdatei sollte trotzdem auf
  **„automatisch verwalten"** oder fix 32 GB.
* **VBS/HVCI-Status prüfen** — Pauls Dump meldet „nicht aktiviert", Horstis Dump sagt nichts dazu.
  Kostet 5–10 % CPU.
* Monitor via DP direkt an der dGPU, 200 Hz, G-Sync → siehe 3.1.

### 2.2 Altes HP ProBook x360 435 G7 (Ryzen 7 4700U, 16 GB, Vega 7)

**16 GB in 1 von 2 Slots = Single-Channel.** Die Vega-7-iGPU hängt komplett an der
RAM-Bandbreite; Single-Channel kostet sie **25–40 %**. Ein zweiter SO-DIMM ist das beste
Preis-Leistungs-Upgrade an allen drei Geräten. Realistisch sonst: RD 8–10, keine Shader,
4 GB Heap → 60–100 fps frei, 35–55 im Spawn-Gewusel.

### 2.3 Pauls Laptop — MEDION S17403 (i7-10510U, 8 GB, Win 11 26200)

Kein RAM-Upgrade geplant → alles muss über Software laufen. Ausgangslage:

* **8 GB gesamt, 1,04 GB frei.** Windows braucht ~5 GB. Max. sinnvoller Heap: **3072 MB**.
* **i7-10510U**, 4C/8T, 15 W, aktuell 2301 MHz. Boost bis 4,9 GHz, hält im dünnen Medion aber
  eher 1,6–2,2 GHz. Gegenüber dem 275HX rund **Faktor 2,5 langsamer pro Kern** — und Minecraft
  ist single-thread-limitiert.
* **GPU unbekannt** — der S17403 existiert mit UHD 620 *und* mit GeForce MX330. Faktor 2
  Unterschied, und bei MX330 muss `javaw.exe` in den Windows-Grafikeinstellungen explizit auf
  „Hohe Leistung" gestellt werden, sonst läuft Minecraft auf der iGPU. **`dxdiag` klären.**
* BIOS von 25.06.2021, EC-Version 255.255 → BIOS/EC-Update kann das Powerlimit-Verhalten
  merkbar verbessern.
* VBS „nicht aktiviert", Kernel-DMA-Schutz aus → gut, so lassen.
* App Control for Business erzwungen, Benutzermodusrichtlinie aus → nur Kernelmodus-Treiber
  betroffen, Java/Modrinth laufen. Falls der Launcher doch blockiert wird, hier nachsehen.

**Erwartung mit der Liste aus Abschnitt 7:** 40–70 fps im freien Feld, 25–40 in Kämpfen.

---

## 3. Der F3-Screenshot

**Was gut aussieht:** interner Server bei 9,0/50,0 ms · Chunk Builder alle 10 Threads idle
(`Busy=00`) · EntityCulling überspringt **34 von 36** Entities beim Rendern und **179 von 194**
beim Ticken · LambDynamicLights kostet gemessen **0,031 ms von 6,76 ms = 0,46 %** im Stehen ·
Heap damals 2221/16384 MB bei 126 MB/s Allocation Rate → kein GC-Druck.

**Was kostet:** `C: 2213/54168 D: 26` (Renderdistanz 26) · `[Iris] Shadow info: C: 870/…` — **870
Sections im Shadow-Pass, ein zweiter kompletter Geometriedurchlauf pro Frame** ·
`Chunks[S] W: 6241` (der interne Server hält 6241 Chunks → Simulationsdistanz prüfen) ·
`Geometry Pool: 1055/1813 MiB` (1 GB VRAM nur für Terrain, Folge von RD 26) ·
`Rendered Block Entities: 207` (dafür ist Enhanced Block Entities da — ist installiert, greift
hier offenbar nur teilweise) · `Transfer Queue: Mapped (16/16 MiB)` (voll).

**Achtung — Screenshot ist nicht mehr aktuell:** Im F3 steht `Sodium Renderer (0.6.13+mc1.21.1)`,
die Modliste zeigt `0.8.13-beta.1`. Zwischen Screenshot und Modliste wurde Sodium aktualisiert.
Die Zahlen oben gelten also für 0.6.13. Siehe dazu 6.3.

### 3.1 Korrektur zu Rev. 1: das `D: 26` in der Iris-Zeile

In Rev. 1 stand „Schattendistanz 26". Das war zu weit gegriffen: **`D:` in der Iris-Shadow-Zeile
spiegelt die Renderdistanz**, nicht zwingend den Shadow-Distance-Wert des Packs. Dass der Shadow-
Pass nur 870 statt 2213 Sections rendert, spricht sogar dafür, dass das Schattenvolumen bereits
kleiner ist als die Renderdistanz.

**Was sich dadurch nicht ändert:** Iris begrenzt den Shadow-Pass auf die Renderdistanz. Eine
niedrigere Renderdistanz senkt also **beide** Zahlen gleichzeitig. Der Hebel ist derselbe, er
heißt nur **Renderdistanz** statt „Schatten-Slider" — und das ist praktisch, weil dieser Slider
nicht ausgegraut ist.

### 3.2 Maßnahmen für den 5090-Laptop, nach Wirkung sortiert

| # | Maßnahme | Erwartung |
|---|---|---|
| 1 | **Renderdistanz 26 → 16** | **+25 bis +40 %** fps. Senkt gleichzeitig den Shadow-Pass, den Geometry Pool (−~600 MB) und die Server-Chunkzahl. Cobblemon-Spawns funktionieren bei 16 einwandfrei. |
| 2 | **VSync AUS, G-Sync AN, Limit 190 fps** | Kein fps-Gewinn, aber die Frameausgabe wird glatt. Behebt das 148-Pendeln. Details unten. |
| 3 | **Simulationsdistanz auf 8** | MSPT runter, keine optische Änderung, gilt auch für Paul. |
| 4 | **Terralith-Situation klären** (6.1) | Beseitigt Fehler-Spam auf dem Server-Thread und die falschen Spawns. |
| 5 | Heap 8192 MB (**erledigt**), Auslagerungsdatei auf automatisch | Kürzere GC-Pausen, kein Commit-Limit-Problem. |
| 6 | **Iris auf 1.8.14-beta.1** aktualisieren | Behebt die Shader-Uniform-Warnungen und passt zur Sodium-Beta (6.3). |
| 7 | Schattendistanz im Pack senken, **falls** der Slider erreichbar ist (6.4) | Zusätzlich, aber nicht der Haupthebel. |
| 8 | VBS/HVCI-Status prüfen | 5–10 % CPU. |

**Konkret für Monitor + G-Sync (DP direkt an der dGPU, 200 Hz):**

1. Windows → System → Anzeige → Erweiterte Anzeige → **200 Hz** wirklich eingestellt?
2. NVIDIA App → Grafik/3D-Einstellungen: **G-SYNC aktivieren** für Vollbild *und* Fenstermodus ·
   **Modus mit geringer Latenz: Ein** (nicht „Ultra", das arbeitet gegen G-Sync) ·
   **Max. Bildrate: 190**
3. Minecraft: **VSync aus**, Bildratenbegrenzung „Unbegrenzt" (der Treiber-Cap greift) oder 190.
4. Warum 190 und nicht 200: G-Sync arbeitet nur *unterhalb* der Bildwiederholrate. Ein Limit
   3–10 fps darunter hält den Bildschirm dauerhaft im VRR-Bereich — das ist der Unterschied
   zwischen „glatt" und „gelegentlich ruckelt es".

---

## 4. Warum 8 GB Heap besser sind als 16 oder 32

Mehr Heap bringt **niemals** fps, sobald er über dem Arbeitsbedarf liegt. Er verändert nur das
GC-Verhalten, und zwar in beide Richtungen:

* **Der Arbeitsbedarf dieser Instanz liegt bei ~2,5–4 GB.** Im F3 waren 2221 MB belegt, die
  Allocation Rate lag bei 126 MB/s. Das ist entspannt.
* **G1 sammelt bei großem Heap länger, bevor er aufräumt.** Wenn dann eine Mixed- oder Full-GC
  kommt, muss sie viel mehr Regionen scannen → **einzelne, lange Pausen**. Auf einem Server ist
  das egal, auf einem Client ist eine 200-ms-Pause ein sichtbarer Freeze. Vier 40-ms-Pausen
  merkt niemand. Genau das sind die Ruckler, die man sonst nicht erklären kann.
* **Java gibt Speicher nur träge ans Betriebssystem zurück.** Ein 16-GB-Heap reserviert 16 GB
  *Commit* (Adressraum + Auslagerungsdatei-Backing), auch wenn nur 2 GB benutzt werden. Genau das
  hat den „verfügbaren virtuellen Speicher: 3,67 GB" verursacht.
* **Faustregel: Arbeitsbedarf × 2.** 2,5–4 GB × 2 = 6–8 GB. **8192 MB ist genau richtig.**
  32 GB wären aktiv schlechter.
* **Selbstkontrolle im Spiel:** F3-Zeile `Mem: xx%`. Bleibt der Spitzenwert unter ~60 %, ist der
  Heap richtig dimensioniert. Läuft er nach längeren Sessions regelmäßig über 70 %, auf 10 GB
  gehen — aber erst dann.

### 4.1 Java-Argumente in Modrinth — bringt das was?

Ehrliche Einordnung: **Java-Argumente bringen keine fps.** Sie verkürzen GC-Pausen, also die
**1 %-Lows** und die gefühlte Glätte. Der Nutzen ist echt, aber klein — und er ist auf kleinen
Heaps *größer* als auf großen, weil dort öfter gesammelt wird.

**Horsti (8192 MB):** lohnt sich mäßig. Ohne die Flags läuft es auch.

```
-XX:+UseG1GC -XX:MaxGCPauseMillis=40 -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem
-XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=28 -XX:G1MaxNewSizePercent=45
-XX:G1HeapRegionSize=8M -XX:G1ReservePercent=15 -XX:InitiatingHeapOccupancyPercent=20
-XX:SurvivorRatio=32 -XX:MaxTenuringThreshold=1
```

**Paul (3072 MB) — hier lohnt es sich wirklich**, weil bei kleinem Heap ständig gesammelt wird:

```
-XX:+UseG1GC -XX:MaxGCPauseMillis=40 -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem
-XX:+UseStringDeduplication -XX:G1HeapRegionSize=4M -XX:SurvivorRatio=32
```

**Altes ProBook (4096 MB):** dieselben Flags wie bei Paul.

Was die Flags tun, in einem Satz: `MaxGCPauseMillis=40` gibt G1 ein Pausenziel vor (er sammelt
öfter, dafür kürzer) · `ParallelRefProcEnabled` verteilt die Reference-Verarbeitung auf mehrere
Threads · `PerfDisableSharedMem` verhindert ein bekanntes Stotterproblem durch JVM-Statistiken
auf der Platte · `G1NewSizePercent/MaxNewSizePercent` halten die junge Generation groß, weil
Minecraft fast nur kurzlebige Objekte erzeugt · `MaxTenuringThreshold=1` befördert Objekte
schneller und hält die Survivor-Bereiche klein · `UseStringDeduplication` spart auf kleinen Heaps
messbar RAM.

**Wichtig:** In Modrinth setzt der Speicher-Slider bereits `-Xmx`/`-Xms`. **Nicht** noch einmal
`-Xmx` in die Java-Argumente schreiben, sonst gibt es zwei widersprechende Werte.

Nicht empfohlen, aber erwähnt: ZGC (`-XX:+UseZGC`) hätte auf dem 5090 sub-millisekunden-Pausen,
kostet aber ~10 % Durchsatz und mehr RAM. Nur als Experiment, nicht als Default.

---

## 5. Die Mods — vollständige Liste (41 aktiv / 6 deaktiviert)

### 5.0 Korrektur zu Rev. 1

Rev. 1 behauptete, Lithium, ModernFix, ImmediatelyFast und Krypton würden fehlen. **Falsch** —
alle vier sind installiert, dazu Enhanced Block Entities, Sodium Extra und Reese's Sodium Options.
Die Liste war bei „libIPN" abgeschnitten.

**Was daraus folgt:** Die Prognose „Lithium würde die MSPT von 9 auf 4–5 ms senken" ist
gegenstandslos — **die 9 ms sind das Ergebnis *mit* Lithium.** Der Tick-Kostenverdacht verschiebt
sich damit auf die Inhaltsmods, allen voran **Radical Cobblemon Trainers**, sowie auf die
Simulationsdistanz. Ohne spark-Profil ist das eine Hypothese, keine Messung.

### 5.1 Kostenklassen

🔴 groß · 🟠 mittel · 🟡 klein · ⚪ nur Ladezeit/RAM · 🟢 spart Leistung

| Mod | Klasse | Wo | Bewertung |
|---|---|---|---|
| **Complementary Reimagined r5.8.1 (HIGH)** | 🔴🔴 | GPU | Nr. 1. Geschätzt 50–60 % der Frametime, davon ein großer Teil der Shadow-Pass (870 Sections/Frame). Ohne Shader liefe dieselbe Szene bei ~350–450 fps. |
| **Renderdistanz 26** (Einstellung) | 🔴 | GPU+CPU | Skaliert quadratisch; 26 statt 16 ist ~2,6× Chunk-Geometrie. Begrenzt zugleich den Shadow-Pass. |
| **Radical Cobblemon Trainers 0.18.1 + API 0.15.2** | 🔴 | Server-Tick | **Neuer Hauptverdächtiger.** Spawnt fortlaufend Trainer-NPCs im Umkreis der Spieler, gestaffelt nach Level. Echte Entities mit KI und Kampflogik. Dazu zwei Fehler im Log (6.2). |
| **Cobblemon 1.7.3** | 🟠 | beides | Bedrock-Modelle mit Animationen: pro sichtbarem Pokémon grob 3–8× die Renderkosten eines Vanilla-Mobs, plus Tick-Kosten. Direkter Regler: Spawn-Caps in `config/cobblemon/`. |
| **Cobblemon Fight or Flight Reborn 0.10.9** | 🟠 | Server-Tick | Periodische Radius-Scans um Spieler. Skaliert mit Entity-Zahl. |
| **Cobblemon Trainer Battle 1.11.12** | 🟠 | Server-Tick | NPC-Trainer + Battle-Logik. **Überlappt inhaltlich mit Radical Cobblemon Trainers** — beide gleichzeitig ist Redundanz, die doppelt tickt. |
| **Xaero's Minimap 26.4.2** | 🟠 | Client | Scannt und cached Chunks im Hintergrund und rendert jedes Frame eine zweite Kartenansicht. Typisch 2–6 %. Xaero's World Map ist deaktiviert — richtig. |
| **Terralith 2.6.2** *(deaktiviert)* | 🔴 | Worldgen | **Deaktiviert, aber die Welt braucht es.** Siehe 6.1. |
| **Distant Horizons 3.2.0** *(deaktiviert)* | 🔴 | GPU+CPU | Deaktiviert — richtig. Mit Iris-Shadern der teuerste Mod im Ökosystem. |
| **Nvidium 0.4.1** *(deaktiviert)* | — | GPU | Deaktiviert — **muss so bleiben**, inkompatibel mit Iris-Shadern. |
| **Terrain Diffusion 2.2.0-windows / Routes 1.0.0** *(deaktiviert)* | — | — | Experimentell. Deaktiviert lassen. |
| **Iris Shaders 1.8.8** | 🟡 | GPU | Der Renderer selbst ist billig; die Kosten kommen aus dem Pack. Update auf 1.8.14-beta.1 verfügbar (6.3). |
| **LambDynamicLights 4.8.10** | 🟡 | GPU+CPU | **Gemessen 0,46 % im Stehen**, in Bewegung 1–5 % durch eingeplante Chunk-Rebuilds. Complementary hat eigenes Handheld-Licht → teilweise redundant. Erster Kandidat zum Wegkürzen bei Paul. |
| **Zoomify, Controlling, AppleSkin, IPN + libIPN, Mod Menu, Searchables, YACL** | 🟡 | Client-GUI | Kosten nur im Menü/Inventar. Vernachlässigbar. |
| **Cobblemon additions / Capture XP / Pokenav / Tim Core / PlayerXP / CobbleDollars** | 🟡 | Server-Tick | Eventgetrieben (Fang, Kill, XP, Shop). Zusammen geschätzt unter 2 % MSPT. Keine Verdächtigen. |
| **Fabric API, FLK, Architectury, Cloth Config, Forge Config API Port, Lithostitched, Text Placeholder API** | ⚪ | Ladezeit/RAM | Bibliotheken. Im Spiel ~0. FLK ist der dickste. **Lithostitched ist Terraliths Abhängigkeit** — es ist aktiv, während Terralith aus ist. |
| **e4mc 6.2.1** | ⚪ / 🟠 | Netzwerk | Im SP 0. Beim Hosten: Relay-Hop, +20–40 ms für Paul, gesamter Traffic über 11,3 Mbit Upload. Kollidiert mit einem Krypton-Mixin (6.2). |
| **FerriteCore 7.0.3** | 🟢 | RAM | 30–40 % weniger BlockState-/Model-Speicher. Auf Pauls 8 GB bares Geld. |
| **EntityCulling 1.10.5** | 🟢 | GPU+CPU | **Gemessen: 34/36 Renderings und 179/194 Ticks übersprungen.** In Cobblemon-Szenen +10 bis +25 %. |
| **Sodium 0.8.13-beta.1** | 🟢🟢 | GPU | 2–4× der Vanilla-Renderer. Beta-Version, siehe 6.3. |
| **Lithium 0.15.4** | 🟢🟢 | Server-Tick | Läuft. Ein Mixin-Konflikt mit ModernFix, harmlos (6.2). |
| **ModernFix 5.25.1** | 🟢 | RAM/Start | Läuft. |
| **ImmediatelyFast 1.6.11** | 🟢 | Client | Läuft. Batcht HUD-/Text-/Partikel-Rendering — bei Cobblemons vielen Overlays überdurchschnittlich wertvoll. |
| **Krypton 0.2.8** | 🟢 | Netzwerk | Läuft, aber ein Mixin wird von e4mc verdrängt (6.2). |
| **Enhanced Block Entities 0.10.2** | 🟢 | GPU | Läuft. Trotzdem 207 gerenderte Block-Entities im Screenshot — Wirkung im spark-Profil prüfen. |
| **Sodium Extra + Reese's Sodium Options** | 🟢 | GPU | Installiert. **Auf Pauls Rechner die wichtigsten Regler überhaupt** (Abschnitt 7.3). |

### 5.2 Noisium — welches?

Auf 1.21.1 gibt es zwei Forks: **NoisiumForked** (Coredex, 1,84 Mio. Downloads, 669 Follower)
und **Noisiumed** (imbavirus, 107,8 K). → **NoisiumForked von Coredex**, deutlich größere
Installationsbasis, also besser getestet.

**Aber: nicht jetzt.** Drei Gründe:

1. Noisium beschleunigt nur die **Generierung neuer Chunks**. Die Welt ist weitgehend generiert —
   im Alltag merkt man davon nichts.
2. Es patcht Worldgen-Interna und trifft damit genau auf **Lithostitched + Terralith**. Ein
   Worldgen-Optimierer auf eine kaputte Biom-Registry zu setzen, ist die falsche Reihenfolge.
3. Erst 6.1 klären, dann messen, dann ggf. Noisium.

---

## 6. Log-Analyse — vier konkrete Fehler

### 6.1 Terralith ist aus, die Welt braucht es (kritisch)

```
Recoverable errors when loading section [-9, 8, 9]: (Unknown registry key in
ResourceKey[minecraft:root / minecraft:worldgen/biome]: terralith:birch_taiga -> using default)
```

Solche Zeilen kommen **pro Chunk-Section**, hunderte- bis tausendfach. Die Welt wurde mit
Terralith 2.6.2 generiert, der Mod ist jetzt deaktiviert. Folgen:

* **Falsche Biome** → falsche Mob-Spawns, falsche Gras-/Wasserfarben. **Und weil Cobblemon-Spawns
  biomabhängig sind: falsche Pokémon.** Das ist der spielerisch schlimmste Teil.
* **Fehler-Spam auf dem Server-Thread.** String-Bau und Log-I/O beim Chunkladen sind nicht
  gratis. Passt zum `Can't keep up! Running 2628ms or 52 ticks behind` direkt danach.
* **Nahtstellen**: neu generierte Chunks nutzen Vanilla-Worldgen und stoßen sichtbar an
  Terralith-Terrain.

**Entscheidung nötig:**
* **Empfohlen: Terralith 2.6.2 wieder aktivieren** und dauerhaft drin lassen (Lithostitched ist
  ohnehin schon aktiv, es ist die Abhängigkeit). → **Dann muss Paul Terralith + Lithostitched
  ebenfalls installiert haben**, sonst kommt er nicht auf den Server.
* Alternative: neue Welt ohne Terralith. Nur sinnvoll, wenn noch nicht viel gebaut wurde.
* Nicht empfohlen: so weiterlaufen lassen.

### 6.2 Drei Mixin-/Versionskonflikte

**a) ModernFix ↔ Lithium — harmlos, nichts tun**
```
Method overwrite conflict for removeIf in modernfix … previously written by lithium … Skipping method.
```
Beide optimieren denselben `SortedArraySet` für Chunk-Tickets. Lithium gewinnt, ModernFix' Kopie
wird übersprungen. Kein Funktionsverlust.

**b) Krypton ↔ e4mc — harmlos, erklärt aber die Frage „läuft Krypton?"**
```
@Redirect conflict. Skipping krypton…ServerLoginNetworkHandlerMixin … already redirected by e4mc
```
e4mc hat denselben Cipher-Redirect zuerst belegt. **Betroffen ist nur der Login-Handshake, nicht
der heiße Paketpfad.** Kryptons Netty- und Kompressionsoptimierungen laufen weiter. Nichts zu tun.

**c) Radical Cobblemon Trainers ↔ RCT-API — sollte behoben werden**
```
Unknown loot table called rctmod:generic/common/evolution
Unknown loot table called rctmod:generic/common/training
```
RCT ist auf **0.18.1-beta**, die API auf **0.15.2-beta**. Versions-Mismatch → fehlende Loot-Tables.
→ **RCT API auf die zu 0.18.1 passende Version aktualisieren.**

**d) Fehlende Mega-Showdown-Items — kosmetisch**
```
Model validation failure for 'elite_four_lorelei_004e' — invalid held item 'mega_showdown:blue_orb'
```
RCT hat eine optionale Mega-Showdown-Integration; der Mod ist nicht installiert. Die betroffenen
Trainer spawnen ohne das Item. Entweder ignorieren oder Mega Showdown installieren (bringt aber
zusätzliche Last).

### 6.3 Sodium/Iris-Versionspaarung

* F3-Screenshot: `Sodium Renderer (0.6.13+mc1.21.1)`
* Modliste heute: `Sodium mc1.21.1-0.8.13-beta.1-fabric` — eine **Beta**
* Iris: `1.8.8` stabil, **Update auf 1.8.14-beta.1 verfügbar**

Dazu im Log:
```
Failed to resolve uniform inPaleGarden, reason: Unknown variable: BIOME_PALE_GARDEN
Failed to resolve uniform endFlashFactor0, reason: Unknown variable: endFlashIntensity
The following uniforms won't work …: endFlashFactor1, endFlashIntensityM
```
Complementary r5.8.1 erwartet Uniforms, die dieses Iris nicht bereitstellt. Ergebnis: einzelne
Shader-Features (Pale-Garden-Nebel, End-Blitz) funktionieren nicht.

→ **Iris auf 1.8.14-beta.1 aktualisieren**, damit stabile und Beta-Komponenten wieder zueinander
passen. Danach Log erneut prüfen. Die Warnungen zu `IViewRotMat` und `Sampler2` sind normales
Iris-Rauschen und können ignoriert werden.

Nebenbei: `Game took 24.796 seconds to start` und `Initial datapack load took 6.168 s` sind für
41 Mods mit Cobblemon-Datapacks normal.

### 6.4 Warum der Schatten-Slider durchgestrichen ist

In Iris werden Shader-Optionen **durchgestrichen/ausgegraut dargestellt, wenn sie in der aktuellen
Konfiguration keine Wirkung haben** — praktisch immer, weil eine übergeordnete Option sie
abschaltet. Das ist kein Fehler und keine Sperre durch das Profil: das F3 zeigt
`[Iris] Profile: HIGH (+0 options changed by user)`, also *könnten* Werte geändert werden.

**Zuverlässiger Weg, unabhängig vom Menü:**
1. Videoeinstellungen → Shaderpacks → Complementary auswählen → **Shader-Pack-Einstellungen** →
   Kategorie **Shadows**. Die übergeordnete Option dort suchen (Schatten-Qualität/-Auflösung);
   ist die aus oder auf einem Sonderwert, sind die Kinder durchgestrichen.
2. Falls das nicht weiterhilft: Iris speichert die Pack-Einstellungen als **Klartextdatei** neben
   dem Pack, im Instanzordner unter `shaderpacks/ComplementaryReimagined_r5.8.1.zip.txt`. Datei
   öffnen, nach `SHADOW` suchen, Wert setzen, Minecraft neu starten.
3. **Kontrolle im Spiel:** F3-Zeile `[Iris] Shadow info: C: …`. Wenn die Zahl vor dem `/` fällt,
   hat es gewirkt. Das ist die Messung, alles andere ist Vermutung.

**Wichtiger als der Slider:** Renderdistanz 26 → 16 senkt den Shadow-Pass ohnehin mit, weil Iris
ihn auf die Renderdistanz begrenzt. **Muss Paul nicht anfassen** — er fährt ohne Shader.

---

## 7. Paul — die vollständige Einstellungsliste

### 7.1 Was Open to LAN + e4mc überträgt (und was nicht)

**Mods werden nicht übertragen.** Open to LAN öffnet nur den integrierten Server; e4mc macht ihn
von außen erreichbar. Paul braucht eine **eigene Instanz mit passender Modliste**.

Der bequeme Weg ist schon halb fertig: Die Instanz ist als **„Shared"** markiert und Modrinth
zeigt „Your local instance is ahead… **Push update**". Also:

1. Horsti drückt **Push update** → Paul zieht die Instanz.
2. Paul **deaktiviert bei sich** nur die Client-Optik-Mods (Liste unten).
3. Alles andere bleibt **identisch**.

**Was bei Paul identisch bleiben MUSS** (registriert Items/Entities/Biome → Registry-Sync,
sonst Kick oder Fehler): Cobblemon · alle Cobblemon-Addons (additions, Capture XP, Pokenav,
Tim Core, Trainer Battle, PlayerXP, CobbleDollars, Fight or Flight) · Radical Cobblemon Trainers
+ API · Lithostitched · **Terralith, falls Horsti es wieder aktiviert** · Text Placeholder API ·
Forge Config API Port · Architectury · Cloth Config · Fabric API · Fabric Language Kotlin ·
Searchables · YACL

> Korrektur zu Rev. 1: Dort stand, Paul solle Trainer Battle und Fight or Flight weglassen. **Für
> eine gemeinsame LAN-Runde ist das falsch** — die müssen zum Host passen. Wenn diese Mods raus
> sollen, dann bei *beiden* gleichzeitig.

**Was Paul bei sich deaktivieren soll:**

| Mod | Warum |
|---|---|
| **Iris Shaders** | Ohne Shader braucht er ihn nicht. Deaktivieren spart RAM und Ladezeit. |
| **Complementary Reimagined** (Shaderpack) | Rein clientseitig. |
| **LambDynamicLights** | 1–5 % auf einer iGPU sind bei ihm echtes Geld. |
| **Xaero's Minimap** | 2–6 % Client-Kosten. Wenn er die Karte will, kann sie bleiben — es ist der erste Kandidat, wenn es hakt. |
| Distant Horizons, Nvidium, Terrain Diffusion, Routes, Xaero's World Map | Sind ohnehin deaktiviert — so lassen. |

**Was Paul behalten muss, weil es hilft:** Sodium · Sodium Extra · Reese's Sodium Options ·
Lithium · ModernFix · FerriteCore · ImmediatelyFast · EntityCulling · Enhanced Block Entities ·
Krypton · Mod Menu · Controlling · AppleSkin · IPN + libIPN · Zoomify

**Und zur direkten Frage:** Ja — **Paul kann den Shader einfach weglassen, und das hat null
Auswirkung auf Horsti oder den Server.** Shader sind zu 100 % clientseitig. Horsti spielt mit
Complementary, Paul ohne, beide auf demselben Server. Umgekehrt gilt genauso: Horstis
Shader-Einstellungen liegen in Horstis Instanz, Paul kann seine eigenen setzen (wenn er wollte).

### 7.2 Modrinth-Einstellungen bei Paul

* **Memory allocated: 3072 MB** (nicht mehr — bei 8 GB Gesamt-RAM würde alles darüber auslagern)
* **Java: 21** (das mitgelieferte reicht)
* **Custom Java arguments:**
  `-XX:+UseG1GC -XX:MaxGCPauseMillis=40 -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem -XX:+UseStringDeduplication -XX:G1HeapRegionSize=4M -XX:SurvivorRatio=32`

### 7.3 Minecraft-Videoeinstellungen bei Paul

**Grafik (Video Settings)**

| Einstellung | Wert |
|---|---|
| Grafik | **Schnell** |
| Renderdistanz | **6** (max. 8) |
| Simulationsdistanz | im Multiplayer irrelevant, der Host setzt sie |
| Bildratenbegrenzung | **60** |
| VSync | **Aus** |
| Wolken | **Aus** |
| Partikel | **Minimal** |
| Mipmap-Stufen | **0** |
| Weiche Beleuchtung | **Aus** |
| Entfernung von Objekten (Entity Distance) | **50 %** |
| Biome-Blend | **Aus / 0** — auf schwachen CPUs einer der größten Einzelposten |
| Vollbild | **An** (exklusives Vollbild ist auf iGPUs schneller als Fenster) |
| GUI-Skalierung | **2** (nicht Auto — jeder GUI-Pixel kostet) |
| Auflösung | notfalls **1600×900** im Vollbild → auf einer UHD 620 fast linear mehr fps |

**Sodium Extra / Reese's Sodium Options — hier liegt der größte Teil des Gewinns**

| Einstellung | Wert |
|---|---|
| **Animationen** (Wasser, Lava, Feuer, Texturen) | **Alle aus** — auf iGPUs oft 10–20 % |
| Partikel | Alle aus |
| Wolken | Aus |
| Nebel (Fog Distance) | **kurz** — reduziert die gerenderte Geometrie zusätzlich |
| Sterne / Sonne / Mond / Wetter | Aus |
| Vignette | Aus |
| Enchantment Glint | Aus |
| Verschwendete Frames / Frame-Limit im Menü | begrenzen |

### 7.4 Windows-Optimierungen bei Paul (ohne Hardware)

1. **Grafiktreiber direkt von Intel** installieren (Medion liefert oft Stände von 2020; bei
   UHD 620 bringt ein aktueller Treiber messbar was).
2. **Falls MX330 vorhanden:** Windows → System → Anzeige → **Grafikeinstellungen** → `javaw.exe`
   der Modrinth-Java-Installation hinzufügen → **„Hohe Leistung"**. Sonst läuft Minecraft auf der
   iGPU, obwohl eine dGPU verbaut ist. **Das ist potenziell Faktor 2 — bitte als erstes prüfen.**
3. **Energieoptionen:** Netzteil anstecken, Windows-Leistungsregler auf **„Beste Leistung"**.
4. **Autostart aufräumen** (Task-Manager → Autostart): alles außer Audio/Touchpad deaktivieren.
   Auf 8 GB sind das echte Prozente.
5. **Xbox Game Bar / Hintergrundaufzeichnung aus** (Einstellungen → Gaming → Aufzeichnungen).
6. **Defender-Ausnahme** für den Modrinth-Instanzordner — der Echtzeitschutz scannt sonst
   tausende Chunk-Dateien beim Speichern.
7. **Browser komplett schließen** beim Spielen. Discord: Hardwarebeschleunigung aus.
8. **OneDrive pausieren**, Windows-Update-Nutzungszeit setzen.
9. **Auslagerungsdatei systemverwaltet** auf der SSD lassen — bei 8 GB RAM ist sie kein Feind,
   sondern notwendig. Nicht abschalten.
10. **BIOS/EC-Update** (Stand 25.06.2021) kann das Powerlimit-Verhalten verbessern.
11. **VBS aus lassen** (ist bereits aus) — nicht versehentlich aktivieren.

---

## 8. Das Testprotokoll

### 8.1 Voraussetzung: spark installieren

**spark ist nicht in der Modliste.** `/spark profiler --timeout 120` kann daher nicht
funktionieren. Also zuerst **spark** (lucko) für Fabric 1.21.1 installieren — auf beiden Rechnern.
Danach:

```
/spark profiler start --timeout 120 --only-ticks-over 20   # interner Server
/sparkc profiler start --timeout 60                        # Client
/spark tps      /spark health      /spark heapsummary
```

Der Web-Report gruppiert den Aufrufbaum nach Mod-Paketen (`com.cobblemon…`, `hd42.rctmod…`,
`dev.vaniron.fightorflight…`). **Das ist die exakte Antwort auf „welcher Mod frisst wie viel" —
alles in Abschnitt 5.1 mit einer Schätzung ist bis dahin ein Modell.**

### 8.2 Test A — Client-fps, eine Änderung pro Schritt

**Bedingungen strikt einhalten**, sonst ist alles Rauschen: feste Testwelt, fester Punkt
(z. B. `XYZ 9.5 / 71 / 480`, Blick Süd) plus ein zweiter Punkt im Freien mit vielen Pokémon ·
`/gamerule doDaylightCycle false` · `/time set 6000` · `/weather clear` · Vollbild, VSync aus,
kein Fortnite/Browser · pro Messung **60 s stehen** (bis `Busy=00`), **dann 60 s messen** ·
erfasst werden **avg fps, 1 %-Low, GPU %, MSPT** plus spark-Profil.

| Schritt | Änderung | Beantwortet |
|---|---|---|
| A0 | Baseline: alles an, RD 26 | Referenz |
| A1 | Shader aus | Gesamtkosten der Shader |
| A2 | **RD 26 → 16** | Kosten der Renderdistanz **inkl. Shadow-Pass** |
| A3 | Simulationsdistanz → 8 | MSPT-Anteil der Simulationsdistanz |
| A4 | EntityCulling aus | was der Mod tatsächlich spart |
| A5 | LambDynamicLights aus | ob Wegkürzen lohnt |
| A6 | Xaero's Minimap aus | Client-Kosten der Karte |
| A7 | Iris 1.8.14-beta.1 statt 1.8.8 | ob die Uniform-Warnungen weg sind |
| A8 | Punkt 2 (Spawn-Feld), A0 wiederholen | Cobblemon-Entity-Kosten |
| A9 | dort `/kill @e[type=cobblemon:pokemon]`, sofort messen | Differenz zu A8 = Kosten der Pokémon |

Für 1 %-Lows CapFrameX oder PresentMon — der F3-Durchschnitt versteckt genau die Ruckler,
die stören.

### 8.3 Test B — Server-Tick

1. Welt mit realistischer Population, `doMobSpawning` an, 5 Minuten laufen lassen.
2. `/spark profiler start --timeout 120 --only-ticks-over 20` → Web-Report.
3. Erwartete Rangliste zum Abgleich: **Radical Cobblemon Trainers**, Cobblemon, Fight or Flight,
   Trainer Battle, Chunk-Ticking (Simulationsdistanz).
4. `/spark heapsummary` → Speicher pro Mod.
5. Test wiederholen **nach** der Terralith-Entscheidung (6.1) — der Fehler-Spam verfälscht sonst
   die Messung.
6. Auf **allen drei Rechnern** wiederholen, sobald sie dieselbe Welt laden.

### 8.4 Test C — Netzwerk

1. Störquellen aus (Epic dauerhaft auf ~30 Mbit/s drosseln), dann **Speedtest neu** — nach dem
   Resync auf 55,17 Mbit/s sollten jetzt ~54 Mbit/s ankommen statt 50,7.
2. **Bufferbloat:** `waveform.com/tools/bufferbloat`, 3× Kabel, 3× WLAN 5 GHz. Ziel A/B.
3. **Priorisierung** in der FRITZ!Box für Horsti5090 setzen, dann Schritt 2 wiederholen.
4. **iperf3 im LAN** (Kabel↔Kabel und Kabel↔WLAN) → misst das Heimnetz ohne DSL und zeigt, was
   Repeater und 2,4-GHz-Gedränge kosten.
5. **In-Game:** F3-Ping + `/spark ping` mit Paul drauf — einmal über e4mc, einmal über eine
   Portfreigabe 25565. Die Differenz ist der Relay-Aufschlag.
6. **Tarif:** bei EWE/osnatel nach Profil 35b / Supervectoring fragen (siehe 1.1).

---

## 9. Was noch offen ist

1. **`dxdiag` von Paul** — UHD 620 oder MX330? Entscheidet über Punkt 2 in 7.4 und über sein
   ganzes Profil.
2. **spark-Reports** aus Test A und B — ersetzt die Schätzungen in 5.1 durch Messwerte.
3. **Screenshot der Shader-Pack-Einstellungen → Kategorie Shadows**, damit die durchgestrichene
   Option benannt werden kann (6.4).
4. **Entscheidung zu Terralith** (6.1) — blockiert Test B, weil der Fehler-Spam die Messung
   verfälscht.
5. **Video-Einstellungen des 5090 als Screenshot** (Renderdistanz, Simulationsdistanz,
   Entity-Distanz, Biome-Blend, Bildratenbegrenzung).
6. **HWiNFO-Log** vom 5090 über 10 Minuten Spielzeit: CPU-/GPU-Takt, Temperaturen, Power-Limit.
   HP OMEN hat mehrere Leistungsprofile, und Laptops drosseln.
7. **VBS/HVCI-Status** des 5090.
8. **`config/cobblemon/`** — die Spawn-Caps sind ein direkter Performance-Regler.
9. **Vollständiges `latest.log`** — die gezeigten Auszüge waren aussagekräftig, aber
   ausschnitthaft.
