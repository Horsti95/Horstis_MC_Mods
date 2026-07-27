package com.horsti.hud;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Was der Server zuletzt geschickt hat, pro Abschnitt.
 *
 * <p>Jeder Eintrag verfaellt von selbst: die Server-Mods senden im Sekundentakt, wer
 * drei Sekunden schweigt, wird ausgeblendet. Damit braucht es kein „Ende"-Paket, wenn
 * eine Runde vorbei ist oder der Mod abgeschaltet wird — die Anzeige raeumt sich auf.
 */
public final class HudState {
	private static final long LEBENSDAUER_MS = 3000;

	private record Eintrag(String text, long empfangen) {
	}

	private static final Map<String, Eintrag> ABSCHNITTE = new LinkedHashMap<>();

	private HudState() {
	}

	public static synchronized void setzen(String abschnitt, String text) {
		if (text == null || text.isEmpty()) {
			ABSCHNITTE.remove(abschnitt);
			return;
		}
		ABSCHNITTE.put(abschnitt, new Eintrag(text, System.currentTimeMillis()));
	}

	public static synchronized void leeren() {
		ABSCHNITTE.clear();
	}

	/** Alle noch gueltigen Zeilen, in Empfangsreihenfolge. */
	public static synchronized List<Zeile> sichtbar() {
		long jetzt = System.currentTimeMillis();
		ABSCHNITTE.entrySet().removeIf(e -> jetzt - e.getValue().empfangen() > LEBENSDAUER_MS);
		List<Zeile> zeilen = new ArrayList<>(ABSCHNITTE.size());
		for (Map.Entry<String, Eintrag> e : ABSCHNITTE.entrySet()) {
			zeilen.add(new Zeile(e.getKey(), e.getValue().text()));
		}
		return zeilen;
	}

	public record Zeile(String abschnitt, String text) {
	}
}
