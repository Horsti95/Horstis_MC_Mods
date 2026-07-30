# Performance- & Netzwerk-Analyse — 30.07.2026

> Messreport, **kein** Plan. Grundlage: 5 Speedtest-CSVs (13:42–14:04 Uhr), F3-Screenshot der
> Cobblemon-Instanz auf dem 5090-Laptop, FRITZ!Box-7690-Auszüge (vor dem OS-Update auf 8.25),
> Systeminfos von drei Rechnern, Modrinth-Modliste (A–L, Rest fehlt).
> Alles was hier als **gemessen** steht, kommt aus den Daten. Alles was als **Schätzung** steht,
> ist ein Modell und wird durch das Testprotokoll in Abschnitt 7 überprüft.

---

## 0. Kurzfassung

1. **Die FRITZ!Box hat recht.** Die Leitung liefert exakt das, was die Oberfläche anzeigt:
   gemessen **50,7 Mbit/s down** und **9,78 Mbit/s up** gegen angezeigte 50,3 / 10,4 Mbit/s.
   Das sind **101 % bzw. 94 % der Sync-Rate** — besser geht auf einer VDSL-50-Leitung nicht.
2. **Die ersten drei Tests waren kaputt, nicht die Leitung.** Nicht WLAN-vs-LAN war der Grund,
   sondern der parallele Fortnite-Download. Er hat die Leitung dichtgemacht (Downstream) und
   Bufferbloat von **+120 ms** erzeugt.
3. **Das LAN-Kabel hat trotzdem etwas gebracht** — nur nicht bei der Bandbreite: die
   Grundlatenz fiel von **~100 ms (WLAN) auf 20–22 ms (Kabel)**, bei identischer Störlast.
   Für Minecraft ist genau das die relevante Zahl, nicht die Mbit/s.
4. **Der eigentliche Flaschenhals im Heimnetz ist nicht die Leitung, sondern das 2,4-GHz-Band**
   (6 Geräte, 20 MHz) plus ein **FRITZ!WLAN Repeater 310 von 2013** (Wi-Fi 4, Single-Radio) und
   ein **devolo dLAN 200 AVmini** am WAN-Port mit 100 Mbit-Link.
5. **Für Minecraft ist der Upload das Limit, nicht der Download.** 10,4 Mbit/s hoch reichen für
   3–4 Mitspieler bei `view-distance=8`. Jeder parallele Upload killt die Session sofort.
6. **Der 5090-Laptop ist nicht am Limit — die Einstellungen sind es.** 148 fps bei GPU 60 %,
   Renderdistanz 26 **und Schattendistanz 26**. Die zwei Zahlen sind der ganze Grund.
7. **Shader:** auf dem 5090 ja, auf dem ProBook nein, auf Pauls Laptop auf keinen Fall.
8. **Es fehlen die vier wichtigsten Performance-Mods** (Lithium, ModernFix, ImmediatelyFast,
   Krypton). Auf Pauls 8-GB-Laptop ist das der Unterschied zwischen spielbar und nicht spielbar.

---

## 1. Netzwerk — die Messung

Alle fünf Läufe stammen vom 30.07.2026. Reihenfolge nach Zeitstempel, Zuordnung nach
Horstis Beschreibung:

| # | Datei | Uhrzeit | Setup | DL (10 MB) | UL | Idle-Latenz | Bufferbloat |
|---|---|---|---|---|---|---|---|
| R1 | `52f07c86` | 13:42:09 | nur WLAN, Fortnite lädt | **9,9** Mbit/s | 5,1 | 100 ms (min 30) | **+123 ms** |
| R2 | `26aff8c2` | 13:49:17 | WLAN + Kabel, Fortnite lädt | **18,5** Mbit/s | 5,4 | 81 ms (min 25) | **+118 ms** |
| R3 | `b212dc9c` | 13:52:51 | nur Kabel, Fortnite lädt | **9,1** Mbit/s | 5,1 | **22 ms** (min 20) | **+124 ms** |
| R4 | `b97a7099` | 14:01:54 | Kabel, Fortnite **gestoppt**, FRITZ!OS 8.22 frisch | **50,42** Mbit/s | **9,78** | 25 ms | +41 ms |
| R5 | `1c51e2f2` | 14:03:04 | Kabel, Fortnite „fortgesetzt" | **50,72** Mbit/s | **9,71** | 27 ms | **+18 ms** |

Werte = Median über 4–6 Wiederholungen je Block. „Bufferbloat" = Latenz-p95 unter Last minus
Idle-Latenz-Minimum. Unter 30 ms ist gut, über 100 ms ist die Leitung unbenutzbar für alles
Interaktive.

### 1.1 Warum die ersten drei Läufe nichts über WLAN vs. LAN aussagen

R1–R3 haben alle drei **denselben Bufferbloat von ~120 ms** und alle drei einen Upload von
~5 Mbit/s statt 9,7. Wenn WLAN die Ursache wäre, müsste R3 (nur Kabel) sauber sein — ist er
aber nicht. Umgekehrt: wenn nur die Leitung dicht wäre, müssten alle drei dieselbe Idle-Latenz
haben — haben sie auch nicht.

Es sind **zwei getrennte Effekte, die sich überlagert haben:**

* **WAN-Sättigung (R1, R2, R3 gleichermaßen):** ein Download außerhalb des Speedtests hat den
  Downstream belegt. Beweis: die Testsoftware ist in R1–R3 gar nicht erst zur 10-MB-Upload-Stufe
  vorgedrungen — die schaltet sie nur frei, wenn die Leitung schnell genug ist. In R4/R5 wurde
  die Stufe erreicht und lieferte 9,7–9,8 Mbit/s.
* **WLAN-Latenz (nur R1, R2):** die Grundlatenz lag bei 80–135 ms statt 20–22 ms. Das sind
  **+60 bis +110 ms allein durch die Funkstrecke**. Das ist selbst für 2,4 GHz viel und passt zu
  einem überfüllten Band (siehe 1.3), nicht zu einer normalen 5-GHz-Verbindung.

Die klarste Einzelzahl im ganzen Datensatz: **R3 hat mit Kabel 20 ms Grundlatenz erreicht,
während dieselbe Störlast noch lief.** Das Kabel hat also genau das gebracht, was ein Kabel
bringt — Latenz, nicht Bandbreite.

### 1.2 Warum R5 trotz „Fortnite wieder an" der beste Lauf ist

R5 ist mit 50,72 Mbit/s und +18 ms Bufferbloat der sauberste Lauf überhaupt. Wenn Fortnite
zu diesem Zeitpunkt wirklich mit voller Rate geladen hätte, wäre das physikalisch unmöglich —
50,7 Mbit/s sind 100 % der Leitung, da bleibt nichts für Epic übrig.

Zwei Beobachtungen dazu:

* Es gibt in R5 **ein einziges gestörtes Fenster**: der 1-MB-Download-Block um 14:03:07–14:03:11
  bricht auf 15,1 Mbit/s ein und die Latenz springt auf 81–145 ms — mitten in einem sonst
  perfekten Lauf. Das sieht nach einem **kurzen Anlaufen des Epic-Downloaders** aus, der danach
  wieder verschwunden ist (Queue leer, Installation/Verify-Phase, oder Download war fertig).
* R4 hat dieselbe Signatur schwächer (Bufferbloat +41 ms statt +18 ms).

**Konsequenz:** der Fortnite-Download war zum Zeitpunkt von R5 nicht mehr aktiv am Ziehen.
Der FRITZ!OS-Neustart hat die Leitung nicht „repariert" — er hat nur zeitlich zusammengefallen
mit dem Ende der Störlast. Wer das sauber trennen will: Abschnitt 7.3.

### 1.3 Schwachstellen im Heimnetz (unabhängig von der Leitung)

| Fund | Bewertung |
|---|---|
| **FRITZ!WLAN Repeater 310**, Wi-Fi 4, nur 2,4 GHz, 144 Mbit/s, 20 MHz | Das schwächste Glied. Single-Radio-Repeater **halbieren** den Durchsatz (empfangen und senden auf demselben Kanal) und fressen Airtime für alle anderen 2,4-GHz-Geräte. Baujahr ~2013. Drei Geräte hängen dahinter (Drucker, Pixel-8-Pro, Streaming-Adapter). |
| **6 Geräte auf 2,4 GHz, 1 Gerät auf 5 GHz** | Das Band ist überfüllt, der Repeater erzwingt 20 MHz. Erklärt die 80–135 ms WLAN-Latenz in R1/R2. |
| **OnePlus-15: Wi-Fi 7, MLO — und trotzdem auf 2,4 GHz, 20 MHz, ↑6 Mbit/s** | Ein Wi-Fi-7-Handy an einer Wi-Fi-7-Box, das auf dem langsamsten Band klebt. Band-Steering greift nicht. |
| **Android: 5 GHz, 80 MHz, 2×2 — aber nur 288 Mbit/s** | Bei 80 MHz und 2×2 wären ~1200 Mbit/s möglich. 288 heißt niedriger MCS → Entfernung, Wand oder Störung. |
| **devolo dLAN 200 AVmini am WAN-Port (auf LAN umgestellt), 100 Mbit-Link** | HomePlug AV der ersten Generation. Nominal 200 Mbit, real 40–90 Mbit mit hohem Jitter. Alles was da dranhängt, ist für Minecraft ungeeignet. |
| **Horsti5090 an LAN 1 mit 2,5 Gbit/s** | Perfekt. Nichts zu tun. Hier gibt es lokal null Engpass. |
| FRITZ!Box 7690, WAN-Port als LAN, alle Ports „Power Mode" | Sauber konfiguriert. |

**Wichtig zur Einordnung:** die FRITZ!Box 7690 kann Supervectoring (bis ~300 Mbit/s). Dass hier
50,3/10,4 anliegt, ist eine **Tarif-/Leitungsgrenze bei EWE/osnatel**, keine Hardwaregrenze der
Box. Ob mehr geht, sagt nur *Internet → DSL-Informationen* (Störabstandsmarge, max. erreichbare
Datenrate) — siehe Abschnitt 8.

### 1.4 Was 50/10 für Minecraft bedeutet

Der Download ist für Minecraft **völlig egal**. Relevant sind Upload und Latenz.

* Ein Minecraft-Server verbraucht pro Spieler grob **100–300 kbit/s Upload** im normalen Spiel
  und **1–3 Mbit/s in Spitzen** (Beitritt, Chunk-Streaming, schnelles Fliegen/Reisen).
* Cobblemon liegt am oberen Ende: viele Entities, viele Entity-Metadata-Pakete, Battle-Sync.
* **10,4 Mbit/s hoch tragen realistisch 3–4 Mitspieler** — aber nur mit `view-distance=8`,
  `simulation-distance=6` und `network-compression-threshold=256`.
* **Jeder parallele Upload ist tödlich**: Cloud-Sync, OneDrive, ein Steam-Upload, ein Twitch-
  Stream. Der Upload ist die knappe Ressource, und Bufferbloat trifft ihn zuerst.
* **e4mc** (in der Modliste) leitet über einen Relay-Server. Das kostet einen zusätzlichen Hop,
  typisch **+20–40 ms** gegenüber einer direkten Portfreigabe. Bei 25 ms Grundlatenz ist das eine
  Verdopplung. Für 2–3 Freunde trotzdem in Ordnung; wenn es zählt, lieber Portfreigabe 25565.

---

## 2. Die drei Rechner

### 2.1 Horsti5090 — HP OMEN MAX 16 (Ultra 9 275HX, RTX 5090 Laptop, 64 GB)

Für Minecraft massiv überdimensioniert. Der 275HX hat 24 Kerne (8P + 16E, kein HT) — Minecraft
nutzt davon effektiv 1–2 für den Client-Hauptthread und den integrierten Server, plus die
Sodium-Chunk-Builder. **Kein Hardware-Engpass.** Die Bremse sind Einstellungen und ein paar
Windows-Details:

| Fund | Bewertung |
|---|---|
| **Verfügbarer virtueller Speicher: 3,67 GB** bei 70,4 GB gesamt | Das ist knapp vor dem Anschlag. Die Auslagerungsdatei ist auf **7,02 GB fix** gesetzt bei 64 GB RAM. Mit 16 GB Java-Heap + Fortnite + Browser läuft das Commit-Limit voll → Out-of-Memory-Meldungen ohne echten RAM-Mangel. **→ Auslagerungsdatei auf „automatisch verwalten" oder fix 32 GB.** |
| **Verfügbarer physischer Speicher: 19,4 GB von 63,4 GB** | 44 GB belegt. Bei laufendem Download/Browser plausibel, aber der 16-GB-Heap ist daran mitschuldig. |
| **Java-Heap 16384 MB, davon 2221 MB (13 %) benutzt**, Allocation Rate 126 MB/s | 16 GB sind für diese Instanz **acht mal zu viel**. Große G1-Heaps machen die GC-Pausen seltener, aber deutlich länger → genau die Ruckler, die man nicht erklären kann. **→ 8 GB (`-Xmx8G -Xms8G`).** |
| BIOS Insyde F.22 (29.04.2026), aktuell | ok |
| Java 21.0.12 | Korrekt für 1.21.1. |
| **VBS/HVCI-Status nicht im Dump** | Sollte geprüft werden — virtualisierungsbasierte Sicherheit kostet 5–10 % CPU. Pauls Rechner meldet sie explizit als „nicht aktiviert", der 5090 sagt nichts. |

### 2.2 Altes HP ProBook x360 435 G7 (Ryzen 7 4700U, 16 GB, Vega 7)

Solide Mittelklasse für Vanilla, mit **einem** großen, billig behebbaren Fehler:

* **16 GB in 1 von 2 Slots = Single-Channel.** Die Vega-7-iGPU hat keinen eigenen Speicher und
  hängt komplett an der RAM-Bandbreite. Single-Channel kostet eine integrierte GPU je nach Last
  **25–40 % Leistung**. Ein zweiter identischer SO-DIMM (auch 8 GB, dann läuft 8+8 im Dual-
  Channel-Modus für die ersten 16 GB) ist das mit Abstand beste Preis-Leistungs-Upgrade an allen
  drei Rechnern.
* 4700U = 8 Kerne / 8 Threads, 15 W. Reicht für den Client-Thread, kein Problem.
* **Realistisch:** Cobblemon, Renderdistanz 8–10, keine Shader, 4 GB Heap → 60–100 fps im
  freien Feld, 35–55 fps im Spawn-Gewusel. Mit zweitem RAM-Riegel eher 80–130 / 50–70.

### 2.3 Pauls Laptop — MEDION S17403 (i7-10510U, 8 GB, Win 11 26200)

**Das ist der kritische Rechner, und der Engpass ist eindeutig der Arbeitsspeicher.**

| Fund | Bewertung |
|---|---|
| **8,00 GB RAM, davon 1,04 GB frei** | Windows 11 belegt allein ~5 GB. Cobblemon braucht 3–4 GB Heap. Das geht nicht auf: **er wird zwangsläufig auslagern**, und Auslagern von Java-Heap ist der schlimmste Ruckler den es gibt. Max. sinnvoll: `-Xmx3G`. |
| Auslagerungsdatei 8,5 GB, virtueller Speicher 6,32 GB frei | Läuft schon jetzt über die Platte. |
| **i7-10510U**, 4C/8T, 15 W, aktuell 2301 MHz | Comet Lake-U von 2019. Boost bis 4,9 GHz, aber in einem dünnen Medion-Gehäuse hält er unter Dauerlast eher **1,6–2,2 GHz**. Gegenüber dem 275HX rund **Faktor 2,5 langsamer pro Kern** — und Minecraft ist single-thread-limitiert. |
| **GPU nicht im Dump** | Der S17403 existiert mit UHD 620 *und* mit GeForce MX330. Das ist ein Unterschied von ungefähr Faktor 2. **Muss geklärt werden** (dxdiag). |
| BIOS von 25.06.2021, „Version des eingebetteten Controllers 255.255" | Fünf Jahre alt. Ein BIOS/EC-Update kann bei diesen Geräten das Powerlimit-Verhalten deutlich verbessern. |
| Virtualisierungsbasierte Sicherheit: **nicht aktiviert**, Kernel-DMA-Schutz: **aus** | Gut für die Performance. So lassen. |
| App Control for Business: **erzwungen**, Benutzermodusrichtlinie: aus | Nur Kernelmodus-Treiber betroffen, Java/Modrinth sollten laufen. Falls der Launcher trotzdem blockiert wird, hier nachsehen. |

**Realistisch mit optimiertem Setup:** Renderdistanz 6, Simulationsdistanz 5, keine Shader,
3 GB Heap, Sodium + Lithium + ModernFix + FerriteCore + ImmediatelyFast + EntityCulling →
**40–70 fps im freien Feld, 25–40 fps in Cobblemon-Kämpfen**. Ohne diese Mods eher die Hälfte.

**Das einzige Upgrade das wirklich zählt:** von 8 auf 16 GB. Ob der S17403 einen freien Slot hat
oder verlötet ist, muss aufgeschraubt/nachgeschaut werden — falls ja, kostet es ~35 € und
verdoppelt die Spielbarkeit.

### 2.4 Direktvergleich (Schätzung, wird durch Test A überprüft)

| | 5090-Laptop | ProBook 4700U | Pauls S17403 |
|---|---|---|---|
| Single-Thread-Leistung | 100 % | ~45 % | ~35 % |
| GPU für MC | 100 % | ~12 % (Vega 7) | ~6 % (UHD 620) / ~12 % (MX330) |
| Nutzbarer Java-Heap | 8–16 GB | 4–6 GB | **3 GB (hartes Limit)** |
| Sinnvolle Renderdistanz | 16–26 | 8–10 | **6–8** |
| Shader | ja | nein | nein |
| Erwartete fps (Cobblemon, Overworld) | 150–300 | 60–100 | 40–70 |

---

## 3. Der F3-Screenshot, Zeile für Zeile

Aufgenommen auf dem 5090-Laptop, 2560×1440, Complementary Reimagined r5.8.1 Profil HIGH.

**Was gut aussieht:**

* `Integrated server @ 9.0/50.0 ms` — der interne Server braucht 9 ms von 50 ms Budget. TPS 20,
  kein Server-Problem. (Mit Lithium wären es ~4–5 ms.)
* `Chunk Builder: Permits=00 | Busy=00 | Total=10` — alle 10 Bau-Threads idle, das Terrain ist
  fertig. Kein Chunk-Stottern in diesem Moment.
* `[Culling] Rendered Entities: 2 Skipped: 34` und `Ticked Entities: 15 Skipped: 179` —
  **EntityCulling arbeitet und spart 94 % der Entity-Renderings und 92 % der Entity-Ticks.**
  Der Mod verdient seinen Platz zehnfach.
* `[LDL] Compute Spatial Lookup Timing: 0,025ms · Scheduled Chunk Rebuilds: 0/16 · 0,006ms` —
  LambDynamicLights kostet hier **0,031 ms von 6,76 ms Frametime = 0,46 %**. Im Stehen. Gemessen.
* `Mem: 13% 2221/16384MB`, Allocation Rate 126 MB/s — sehr entspannt, kein GC-Druck.
* Distant Horizons ist `.disabled`. **Richtig so** — DH 3.2 zusammen mit Iris-Shadern auf 1.21.1
  ist der teuerste Kombination im ganzen Ökosystem.

**Was die fps kostet:**

* `C: 2213/54168 D: 26` — **Renderdistanz 26**. 2213 sichtbare Chunk-Sections.
* `[Iris] Shadow info: C: 870/54168 D: 26` — **Schattendistanz ebenfalls 26**. Das ist der
  teuerste Einzelposten im Bild: der Shadow-Pass ist ein **zweiter kompletter Geometriedurchlauf**
  über 870 Sections, jedes Frame. Complementary rechnet den in voller Renderdistanz, weil er
  ihr folgt.
* `Chunks[S] W: 6241` — der interne Server hält 6241 Chunks. Simulationsdistanz sollte geprüft
  werden; sie ist der Hauptgrund für die 9 ms MSPT.
* `Geometry Pool: 1055/1813 MiB (111 buffers)` — 1 GB VRAM allein für Terrain-Geometrie, direkte
  Folge von RD 26. Auf einer 5090 unkritisch, aber es zeigt die Größenordnung.
* `[Culling] Rendered Block Entities: 207 Skipped: 6` — **207 gerenderte Block-Entities** (Kisten,
  Schilder, Betten im Bild). Das ist viel und wird nicht geculled. Genau dafür gibt es
  *Enhanced Block Entities*.
* `Transfer Queue: Mapped (16/16 MiB)` — voll. Kleiner Hinweis auf Upload-Druck zur GPU.

**Der auffälligste Fund:**

```
148 fps  T: inf  vsync  fancy  B: 2  GPU: 60%
```

* `T: inf` = Bildratenbegrenzung „Unbegrenzt", **gleichzeitig ist VSync an**.
* Der Monitor läuft laut Angabe mit **200 Hz** (= 5,0 ms pro Bild). Ein Frame dauert bei 148 fps
  aber **6,76 ms**. Mit VSync und ohne VRR muss jedes Frame auf das nächste Interval warten →
  effektive Ausgabe pendelt zwischen 200 und 100 fps. Der Durchschnitt 148 ist genau dieses
  Pendeln. **Das ist kein Leistungs-, sondern ein Frame-Pacing-Problem — es fühlt sich
  schlechter an als die Zahl aussagt.**
* `GPU: 60 %` bedeutet: 40 % der Frametime hängt *nicht* an der GPU. Die 5090 langweilt sich zu
  einem Drittel.

**Zu klären:** läuft der externe Monitor wirklich mit 200 Hz und hängt er direkt an der dGPU
(MUX / Advanced Optimus) oder wird über die iGPU kopiert? Beides ändert die Zahlen deutlich.

### 3.1 Konkrete Maßnahmen für den 5090-Laptop, nach Wirkung sortiert

| # | Maßnahme | Erwartung |
|---|---|---|
| 1 | **Schattendistanz in Complementary auf 12–16** (Shader-Optionen → Shadows → Shadow Distance) | **+20 bis +35 %** fps. Optisch praktisch nicht sichtbar, weil Schatten in der Ferne ohnehin verschwimmen. Größter Hebel im ganzen Setup. |
| 2 | **VSync aus**, Bildratenbegrenzung auf 190, G-Sync/VRR im NVIDIA-Treiber aktivieren (falls der Monitor es kann) | Kein fps-Gewinn, aber spürbar glattere Frameausgabe. Behebt das 148-fps-Pendeln. |
| 3 | **Renderdistanz 26 → 16** | **+25 bis +40 %** fps, −600 MB VRAM, deutlich weniger Server-MSPT. Cobblemon-Spawns funktionieren bei 16 einwandfrei. |
| 4 | **Simulationsdistanz auf 8** prüfen/setzen | MSPT von 9 ms auf ~5 ms, keine optische Änderung. |
| 5 | **Heap 16 GB → 8 GB**, Auslagerungsdatei auf automatisch | Kürzere GC-Pausen, 8 GB RAM zurück fürs System, behebt das Commit-Limit-Problem. |
| 6 | **Lithium + ImmediatelyFast + ModernFix + Krypton** nachrüsten | MSPT halbiert, HUD-/Textrendering deutlich billiger (relevant bei Cobblemon-Overlays). |
| 7 | Enhanced Block Entities | Zielt direkt auf die 207 Block-Entities. |
| 8 | Prüfen ob VBS/HVCI aus ist | 5–10 % CPU. |

Punkt 1 + 3 zusammen sollten die Instanz von 148 auf **220–280 fps** bringen, bei praktisch
identischer Optik.

---

## 4. Machen Shader Sinn?

**Auf dem 5090-Laptop: ja, eindeutig.** Die GPU ist zu 60 % ausgelastet, es ist Luft da, und
Complementary Reimagined ist einer der effizientesten Packs überhaupt. Der Fehler liegt nicht
darin *dass* Shader laufen, sondern *wie*: Schattendistanz 26 verdoppelt den Geometrieaufwand
ohne sichtbaren Gegenwert. Mit Schattendistanz 12–16 und RD 16 laufen die Shader auf diesem
Gerät praktisch gratis.

**Auf dem ProBook (Vega 7, Single-Channel): nein.** Eine iGPU an halbierter Speicherbandbreite
bricht mit jedem Shader auf einstellige fps ein. Wenn es unbedingt sein muss: *MakeUp Ultra Fast*
oder *Sildur's Vibrant Lite* bei RD 6 — für Screenshots, nicht zum Spielen.

**Auf Pauls Laptop: auf keinen Fall.** Mit UHD 620 und 1 GB freiem RAM ist schon Vanilla knapp.

**Wichtig für gemeinsame Runden:** Shader sind rein clientseitig. Horsti kann mit Complementary
spielen, während Paul ohne spielt — das beeinflusst weder Server noch die anderen. Was die
anderen beeinflusst, ist ausschließlich Horstis **Upload**, wenn er hostet (Abschnitt 1.4).

---

## 5. Die Mods — wer kostet was

⚠️ Die Modrinth-Liste ist bei **libIPN abgeschnitten** (alphabetisch A–L). Sodium ist im F3
sichtbar, steht aber nicht in der Liste — es fehlt also alles ab „M". Die folgende Tabelle
bewertet, was sichtbar ist.

Kostenklassen: 🔴 groß · 🟠 mittel · 🟡 klein · ⚪ nur Ladezeit/RAM · 🟢 **spart** Leistung

| Mod | Klasse | Wo | Begründung |
|---|---|---|---|
| **Complementary Reimagined r5.8.1 (HIGH)** | 🔴🔴 | GPU | Mit Abstand Nr. 1. Schätzung: **50–60 % der Frametime**, davon rund die Hälfte allein der Shadow-Pass bei D:26. Ohne Shader liefe dieselbe Szene bei ~350–450 fps. |
| **Renderdistanz 26** (Einstellung, kein Mod) | 🔴 | GPU+CPU | Skaliert quadratisch. 26 statt 16 ist ~2,6× so viel Chunk-Geometrie. |
| **Distant Horizons 3.2.0** | 🔴 | GPU+CPU | **Deaktiviert — richtig.** Zusammen mit Iris-Shadern der teuerste Mod im Ökosystem. Nur zusammen mit stark reduzierter normaler RD sinnvoll. |
| **Cobblemon 1.7.3** | 🟠 | beides | Pokémon sind Bedrock-Modelle mit Animationen — pro sichtbarem Pokémon grob **3–8× die Renderkosten eines Vanilla-Mobs**, dazu Tick-Kosten für KI und Animation. In einem Spawn-Feld mit 30 Pokémon ist das die zweitgrößte Position nach den Shadern. **Direkter Regler: Spawn-Caps in der Cobblemon-Config.** |
| **Cobblemon Fight or Flight Reborn** | 🟠 | Server-Tick | Scannt periodisch Radien um Spieler nach aggressionsfähigen Pokémon. Skaliert mit Entity-Zahl. |
| **Cobblemon Trainer Battle** | 🟠 | Server-Tick | Spawnt NPC-Trainer als echte Entities plus Battle-Logik. |
| **Iris Shaders 1.8.8** | 🟡 | GPU | Der Renderer selbst ist billig; die Kosten kommen aus dem Shaderpack. Ohne Pack ~0. |
| **LambDynamicLights 4.8.10** | 🟡 | GPU+CPU | **Gemessen 0,46 % im Stehen.** In Bewegung mit Fackel/Leuchtitem plant er Chunk-Rebuilds ein (aktuell 0/16) → realistisch **1–5 % in Bewegung**. Hinweis: Complementary hat eigenes Handheld-Light; LDL bringt hier vor allem Licht von *anderen* Entities/Items. |
| **Cobblemon Additions / TimCore / PokéNav / CaptureXP / PlayerXP / CobbleDollars** | 🟡 | Server-Tick | Alle sechs eventgetrieben (Fang, Kill, XP, Shop). Zusammen schätzungsweise unter 2 % MSPT. Keiner davon ist ein Verdächtiger. |
| **AppleSkin** | 🟡 | Client-HUD | Ein paar Overlay-Draws. Vernachlässigbar. |
| **Inventory Profiles Next + libIPN** | 🟡 | Client-GUI | Kostet nur bei geöffnetem Inventar. |
| **Controlling** | ⚪ | — | Nur im Optionsmenü aktiv. |
| **Fabric API, Fabric Language Kotlin, Architectury, Cloth Config, Forge Config API Port** | ⚪ | Ladezeit/RAM | Bibliotheken. Kosten Startzeit und RAM, im Spiel ~0. FLK ist mit ~40 MB der dickste davon. |
| **e4mc 6.2.1** | ⚪ / 🟠 | Netzwerk | Im Singleplayer 0. Beim Hosten: **Relay-Hop, +20–40 ms für alle Mitspieler**, und der gesamte Traffic läuft über Horstis 10,4-Mbit-Upload. |
| **FerriteCore 7.0.3** | 🟢 | RAM | Spart **30–40 % des BlockState-/Model-Speichers**. Auf Pauls 8-GB-Kiste bares Geld. |
| **EntityCulling 1.10.5** | 🟢 | GPU+CPU | **Gemessen: 34 von 36 Entities beim Rendern übersprungen, 179 von 194 beim Ticken.** In Entity-schweren Szenen (= Cobblemon) locker **+10 bis +25 %**. |
| **Sodium 0.6.13** | 🟢🟢 | GPU | 2–4× der Vanilla-Renderer. Ohne ihn wäre RD 26 mit Shadern gar nicht denkbar. |

### 5.1 Was fehlt

Die vier wichtigsten Performance-Mods für 1.21.1 sind **nicht installiert**:

| Mod | Wirkung | Für wen am wichtigsten |
|---|---|---|
| **Lithium** | Optimiert die Server-Tick-Logik (Mob-KI, Redstone, Kollision, Chunk-Ticking). **−30 bis −50 % MSPT**, ohne Verhaltensänderung. | Alle drei. Auf Pauls Laptop und beim Hosten der größte Einzelgewinn. |
| **ModernFix** | Startzeit und RAM-Verbrauch, dynamisches Ressourcen-Laden. | **Paul** (8 GB) und ProBook. |
| **ImmediatelyFast** | Batcht Immediate-Mode-Rendering: HUD, Text, Partikel, Items in GUIs. Cobblemon hat viele Overlays → überdurchschnittlicher Gewinn. | Alle drei. |
| **Krypton** | Optimiert den Netzwerk-Stack (Netty). Weniger CPU pro Paket, relevant bei knapper Leitung. | Beim Hosten. |

Zweite Reihe, lohnt sich zusätzlich:

* **Enhanced Block Entities** — zielt direkt auf die 207 Block-Entities im Screenshot.
* **Noisium** — schnellere Weltgenerierung (Cobblemon-Welten werden viel erkundet).
* **Sodium Extra** + **Reese's Sodium Options** — schaltbare Details (Nebel, Partikel, Wolken,
  Himmel). Auf Pauls Rechner sind das nochmal 10–15 %, weil man Dinge einzeln abschalten kann,
  die Vanilla nur im Paket anbietet.
* **Cull Less Leaves** / **MoreCulling** — Laubkosten. Vor dem Einsatz Kompatibilität mit
  Sodium 0.6.13 prüfen.

**Nicht installieren:** *Nvidium* (inkompatibel mit Iris-Shadern), *OptiFine* (kollidiert mit
allem hier).

### 5.2 Drei fertige Profile

**Profil „Horsti 5090" — Optik voll, aber sauber**
Sodium · Iris + Complementary Reimagined HIGH, **Schattendistanz 14** · Lithium · FerriteCore ·
ModernFix · ImmediatelyFast · Krypton · EntityCulling · Enhanced Block Entities ·
LambDynamicLights · Cobblemon-Stack komplett · **RD 16, Sim 8, VSync aus, 190 fps Cap, 8 GB Heap**

**Profil „ProBook 4700U" — flüssig ohne Kompromisse beim Inhalt**
Sodium (+ Sodium Extra, Reese's) · **keine Shader** · Lithium · FerriteCore · ModernFix ·
ImmediatelyFast · EntityCulling · Cobblemon-Stack komplett · Grafik „Schnell", Wolken aus,
Partikel minimal, Entity-Distanz 75 % · **RD 8, Sim 6, 4 GB Heap** · zweiter RAM-Riegel!

**Profil „Paul S17403" — Überlebensmodus**
Sodium (+ Sodium Extra, Reese's) · **keine Shader, kein Distant Horizons** · Lithium ·
FerriteCore · ModernFix · ImmediatelyFast · EntityCulling · Cobblemon **ohne** Trainer Battle
und **ohne** Fight or Flight (die beiden zuerst rauswerfen, wenn es hakt) · Grafik „Schnell",
Wolken aus, Partikel minimal, Entity-Distanz 50 %, Mipmap 0, Helle Farben aus ·
**RD 6, Sim 5, `-Xmx3G -Xms3G`** · alles andere auf dem Laptop schließen

---

## 6. Bewertung der Mod-Kombinationen

* **Sodium + Iris + EntityCulling + FerriteCore** — saubere, konfliktfreie Basis. Genau richtig.
* **Iris + Distant Horizons** — potenziell der teuerste Konflikt im Setup, deshalb gut, dass DH
  deaktiviert ist. Wenn DH doch mal an soll: normale RD parallel auf 8 runter, sonst rendert man
  alles doppelt.
* **Complementary + LambDynamicLights** — teilweise redundant (Handheld-Licht macht Complementary
  selbst). LDL bleibt sinnvoll für Licht von Entities und Items am Boden, kostet aber Rebuilds.
  Kandidat zum Wegkürzen, wenn es eng wird.
* **Cobblemon + Fight or Flight + Trainer Battle + Additions** — inhaltlich super, aber das ist
  die Kombination, die aus einem ruhigen Server einen entity-schweren macht. Genau hier lohnt
  Lithium am meisten.
* **Architectury + Cloth Config + Forge Config API Port + FLK** — vier Bibliotheken für eine
  Handvoll Mods. Kein Performanceproblem, aber Ladezeit und RAM. Auf Pauls 8 GB relevant.
* **e4mc + 10,4 Mbit Upload** — funktioniert für 2–3 Freunde, ist aber die Stelle, an der
  Netzwerk und Mods aufeinandertreffen. Bei mehr als 3 Mitspielern wird Aternos oder ein
  günstiger VPS ehrlicher als die eigene DSL-Leitung.

---

## 7. Das Testprotokoll

Ohne Messung bleiben die Zahlen in Abschnitt 5 ein Modell. Die folgenden drei Tests machen daraus
harte Werte. **Test A und B liefern die Antwort auf „welcher Mod frisst wie viel" exakt.**

### 7.1 Test A — Client-fps, Mod für Mod

**Vorbereitung (einmalig, unbedingt einhalten — sonst ist alles Rauschen):**

1. Eine feste Testwelt, ein fester Punkt. Der Screenshot-Punkt geht: `XYZ 9.5 / 71 / 480`,
   Blickrichtung Süd. Besser zusätzlich ein zweiter Punkt im Freien mit vielen Pokémon.
2. `/gamerule doDaylightCycle false` · `/time set 6000` · `/weather clear` ·
   `/gamerule doMobSpawning false` (für die reinen Render-Messungen)
3. Vollbild auf dem externen Monitor, **VSync aus**, Bildrate unbegrenzt, kein Fortnite/Browser.
4. Pro Messung: **60 s stehen lassen** (Chunk-Builder muss auf `Busy=00`), **dann 60 s messen**.
5. Gemessen wird: **avg fps, 1 % low, GPU %, MSPT** (aus F3), plus ein spark-Profil.

**Werkzeuge:** *spark* (Client **und** Server), im Spiel:

```
/sparkc profiler start --timeout 60      # Client-Profiler → Web-Report mit Aufrufbaum
/spark  profiler start --timeout 60      # interner Server
/spark  tps      /spark health           # MSPT-Verteilung
```
Für 1 % lows zusätzlich CapFrameX oder PresentMon — der F3-Durchschnitt versteckt genau die
Ruckler, die stören.

**Messreihe (jeweils *eine* Änderung gegen die Baseline, dann zurück):**

| Schritt | Änderung | Beantwortet |
|---|---|---|
| A0 | Baseline: alles an, RD 26, Schatten 26 | Referenz (erwartet ~148 fps) |
| A1 | Shader aus (Iris → „Shaders disabled") | **Gesamtkosten der Shader** |
| A2 | Shader an, Schattendistanz 14 | Kosten des Shadow-Passes allein — **die wichtigste Messung** |
| A3 | RD 26 → 16 (Schatten wieder 26) | Kosten der Renderdistanz |
| A4 | RD 16 **und** Schatten 14 | die empfohlene Zielkonfiguration |
| A5 | EntityCulling aus | was der Mod tatsächlich spart |
| A6 | LambDynamicLights aus | ob sich das Wegkürzen lohnt |
| A7 | Enhanced Block Entities dazu | Wirkung auf die 207 Block-Entities |
| A8 | Punkt 2 (Spawn-Feld) statt Haus, A0 wiederholen | **Cobblemon-Entity-Kosten** |
| A9 | dort `/kill @e[type=cobblemon:pokemon]`, sofort messen | Differenz zu A8 = Kosten der Pokémon |

Tabelle mit avg/1%-low/GPU% füllen — daraus fällt die Rangliste von selbst heraus.

### 7.2 Test B — Server-Tick, Mod für Mod

Hier gibt es keine Schätzung nötig, spark liefert es direkt:

1. Testwelt mit realistischer Cobblemon-Population, `doMobSpawning` **an**, 5 Minuten laufen
   lassen.
2. `/spark profiler start --timeout 120 --only-ticks-over 20`
3. Den Web-Report öffnen → der Aufrufbaum ist nach Mod-Paketen gruppiert
   (`com.cobblemon…`, `dev.vaniron.fightorflight…`, …). **Das ist die exakte Antwort auf
   „welcher Mod frisst wie viel Tick-Zeit".**
4. Anschließend `/spark heapsummary` → Speicher pro Mod.
5. Danach Lithium installieren und Schritt 2–3 wiederholen. Differenz = Lithium-Gewinn.

Das Ganze auf **allen drei Rechnern** wiederholen, sobald sie dieselbe Welt laden — Pauls
Ergebnisse werden ganz anders aussehen und genau das ist die interessante Information.

### 7.3 Test C — Netzwerk, sauber diesmal

Die bisherigen Läufe sind wegen der Störlast nur eingeschränkt vergleichbar. Sauberes Vorgehen:

1. **Störquellen aus:** Epic/Steam/Windows Update/OneDrive beenden (Epic hat unter
   *Einstellungen → Downloads* eine Drosselung — dauerhaft auf ~30 Mbit/s setzen, dann passiert
   das Ganze nie wieder).
2. **Bufferbloat messen:** `waveform.com/tools/bufferbloat` — die einzige Messung, die für
   Minecraft wirklich zählt. Ziel: Grade A/B. Jeweils 3× mit Kabel, 3× mit WLAN 5 GHz.
3. **Leitungswahrheit:** FRITZ!Box → *Internet → DSL-Informationen*: Störabstandsmarge (SNR),
   maximal erreichbare Datenrate, Fehlerzähler (ES/SES/CRC) über 24 h. Das zeigt, ob 50/10 der
   Tarif oder die Leitung ist — und ob mehr buchbar wäre.
4. **Priorisierung:** FRITZ!Box → *Internet → Filter → Priorisierung* → Horsti5090 als
   „Echtzeitanwendung" eintragen. Das schützt die Minecraft-Session gegen genau den Fall von R1–R3.
5. **WLAN vs. WAN trennen:** `iperf3` zwischen zwei Rechnern **im LAN** laufen lassen (einmal
   Kabel↔Kabel, einmal Kabel↔WLAN). Das misst das Heimnetz ohne DSL und zeigt, was der Repeater
   und das 2,4-GHz-Gedränge wirklich kosten.
6. **In-Game:** F3 → Ping-Anzeige, plus `/spark ping` beim Hosten mit 2–3 Freunden. Einmal mit
   e4mc, einmal mit direkter Portfreigabe 25565 — die Differenz ist der Relay-Aufschlag.
7. Alles **je einmal mit und einmal ohne** parallelen Download wiederholen. Erst dann sind die
   Zahlen vergleichbar.

**Sofortmaßnahmen im Heimnetz, unabhängig vom Test:**

* Den **Repeater 310 ausmustern** oder zumindest die Geräte dahinter direkt auf die Box holen.
  Ein aktueller Repeater (oder besser: Mesh über LAN/den vorhandenen devolo) beseitigt gleich
  drei Probleme (Airtime, 20 MHz, Halbierung).
* **OnePlus-15 und die anderen 2,4-GHz-Dauergäste aufs 5-GHz-Band zwingen** —
  *WLAN → Funknetz → „Unterschiedliche Namen für WLAN-Funknetze verwenden"* aktivieren und die
  Geräte gezielt ins 5-GHz-Netz stecken. Das entlastet 2,4 GHz für die Geräte, die dort bleiben
  müssen (Kamera, Drucker, Streaming-Adapter).
* **Gaming-PC bleibt am Kabel.** Das ist gemessen der Unterschied zwischen 22 ms und 100 ms.

---

## 8. Was noch fehlt

Zum Analysieren reicht das Vorhandene für alles oben. Für harte statt geschätzter Zahlen fehlt:

**Hoch relevant**

1. **Die Modliste ab „M"** — sie bricht bei libIPN ab. Sodium ist laut F3 da, taucht aber nicht
   auf; alles zwischen M und Z fehlt komplett.
2. **Video-Einstellungen als Screenshot** (Renderdistanz, Simulationsdistanz, Grafik,
   Entity-Distanz, Mipmap, Wolken, Bildratenbegrenzung, VSync, GUI-Skalierung).
3. **Java-Argumente pro Instanz** in Modrinth (Xmx/Xms, GC-Flags).
4. **Pauls GPU** — `dxdiag` → speichern. UHD 620 oder MX330 entscheidet über sein ganzes Profil.
5. **spark-Reports** aus Test A und B. Das ist die eigentliche Antwort auf „welcher Mod frisst
   wie viel" — alles andere ist Modell.

**Mittel relevant**

6. **Läuft der 200-Hz-Monitor wirklich mit 200 Hz** (Windows → System → Anzeige → Erweitert),
   und hängt er an der dGPU oder an der iGPU (NVIDIA App → System → Anzeige / „GPU-Betriebsmodus")?
   Kann der Monitor G-Sync/FreeSync?
7. **FRITZ!Box → Internet → DSL-Informationen** (Störabstandsmarge, max. Datenrate, Fehlerzähler).
   Die Screenshots nach dem 8.25-Update wären gut, aber vor allem diese Seite.
8. **Wie wird gespielt?** Singleplayer, e4mc-Host, Aternos oder VPS? Das ändert die
   Netzwerkbewertung komplett.
9. **Auf welchem Band hing der 5090 bei R1/R2** (2,4 oder 5 GHz)? Die 80–135 ms sprechen für
   2,4 GHz.
10. **Hat der MEDION S17403 einen freien RAM-Slot** oder ist der Speicher verlötet?
11. **HWiNFO-Log** vom 5090 während einer 10-Minuten-Session: CPU-/GPU-Takt, Temperaturen,
    Power-Limit. Laptops drosseln, und HP OMEN hat mehrere Leistungsprofile.

**Nice to have**

12. Ein `latest.log` der Cobblemon-Instanz (zeigt Warnungen, Mixin-Konflikte, Ladezeiten pro Mod).
13. Cobblemon-Config (`config/cobblemon/main.json`) — die Spawn-Caps sind ein direkter
    Performance-Regler.
