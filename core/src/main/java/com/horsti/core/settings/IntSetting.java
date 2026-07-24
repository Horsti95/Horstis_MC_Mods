package com.horsti.core.settings;

import java.util.List;

public class IntSetting extends Setting<Integer> {
	public final int min;
	public final int max;

	public IntSetting(String key, String beschreibung, int defaultValue, int min, int max) {
		super(key, beschreibung, defaultValue);
		this.min = min;
		this.max = max;
	}

	@Override
	public String format(Integer wert) {
		return String.valueOf(wert);
	}

	@Override
	public List<String> literalWerte() {
		return List.of(); // freier Integer-Parameter mit Bereichspruefung
	}

	@Override
	public Integer parse(String eingabe) {
		int wert;
		try {
			wert = Integer.parseInt(eingabe);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Zahl erwartet, nicht '" + eingabe + "'");
		}
		if (wert < min || wert > max) {
			throw new IllegalArgumentException("Wert muss zwischen " + min + " und " + max + " liegen");
		}
		return wert;
	}
}
