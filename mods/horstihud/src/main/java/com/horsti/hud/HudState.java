package com.horsti.hud;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What the server last sent, per section.
 *
 * <p>Every entry expires by itself: the server mods send once per second, and anything
 * silent for three seconds disappears. That means no "end" packet is needed when a round
 * finishes or a mod is switched off — the display cleans itself up.
 */
public final class HudState {
	private static final long LIFETIME_MS = 3000;

	private record Entry(String text, long received) {
	}

	private static final Map<String, Entry> SECTIONS = new LinkedHashMap<>();

	private HudState() {
	}

	public static synchronized void set(String section, String text) {
		if (text == null || text.isEmpty()) {
			SECTIONS.remove(section);
			return;
		}
		SECTIONS.put(section, new Entry(text, System.currentTimeMillis()));
	}

	public static synchronized void clear() {
		SECTIONS.clear();
	}

	/** All still-valid lines, in the order they arrived. */
	public static synchronized List<Line> visible() {
		long now = System.currentTimeMillis();
		SECTIONS.entrySet().removeIf(e -> now - e.getValue().received() > LIFETIME_MS);
		List<Line> lines = new ArrayList<>(SECTIONS.size());
		for (Map.Entry<String, Entry> e : SECTIONS.entrySet()) {
			lines.add(new Line(e.getKey(), e.getValue().text()));
		}
		return lines;
	}

	public record Line(String section, String text) {
	}
}
