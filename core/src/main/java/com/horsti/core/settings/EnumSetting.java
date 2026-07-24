package com.horsti.core.settings;

import java.util.List;

/** String-Auswahl aus festen Optionen (z. B. "magnet"|"inventar"). */
public class EnumSetting extends Setting<String> {
	private final List<String> optionen;

	public EnumSetting(String key, String beschreibung, String defaultValue, String... optionen) {
		super(key, beschreibung, defaultValue);
		this.optionen = List.of(optionen);
	}

	@Override
	public String format(String wert) {
		return wert;
	}

	@Override
	public List<String> literalWerte() {
		return optionen;
	}

	@Override
	public String parse(String eingabe) {
		String e = eingabe.toLowerCase();
		if (!optionen.contains(e)) {
			throw new IllegalArgumentException("Erwartet: " + String.join("|", optionen));
		}
		return e;
	}
}
