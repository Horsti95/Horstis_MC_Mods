package com.horsti.core.settings;

import java.util.List;

/**
 * Freitext-Setting (z. B. eine Item-ID). Bekommt keinen automatischen
 * set-Command — der Mod haengt dafuer einen eigenen Subcommand mit passendem
 * Argument-Typ an (siehe HorstiMod.extra).
 */
public class StringSetting extends Setting<String> {
	public StringSetting(String key, String beschreibung, String defaultValue) {
		super(key, beschreibung, defaultValue);
	}

	@Override
	public String format(String wert) {
		return wert;
	}

	@Override
	public List<String> literalWerte() {
		return List.of();
	}

	@Override
	public String parse(String eingabe) {
		return eingabe;
	}
}
