package com.horsti.core.settings;

import java.util.List;

public class BoolSetting extends Setting<Boolean> {
	public BoolSetting(String key, String beschreibung, boolean defaultValue) {
		super(key, beschreibung, defaultValue);
	}

	@Override
	public String format(Boolean wert) {
		return wert ? "on" : "off";
	}

	@Override
	public List<String> literalWerte() {
		return List.of("on", "off");
	}

	@Override
	public Boolean parse(String eingabe) {
		return switch (eingabe.toLowerCase()) {
			case "on", "an", "true" -> true;
			case "off", "aus", "false" -> false;
			default -> throw new IllegalArgumentException("on|off erwartet, nicht '" + eingabe + "'");
		};
	}
}
