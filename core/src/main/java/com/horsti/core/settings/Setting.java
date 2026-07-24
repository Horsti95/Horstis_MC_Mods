package com.horsti.core.settings;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ein einzelner, live veraenderbarer Parameter eines Horsti-Mods.
 * Wird einmal deklariert; Command-Baum, Config-Persistenz und Validierung
 * entstehen daraus automatisch.
 */
public abstract class Setting<T> {
	public final String key;
	public final String beschreibung;
	public final T defaultValue;
	private T value;
	private Consumer<T> onChange;

	protected Setting(String key, String beschreibung, T defaultValue) {
		this.key = key;
		this.beschreibung = beschreibung;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public T get() {
		return value;
	}

	public void set(T neu) {
		this.value = neu;
		if (onChange != null) {
			onChange.accept(neu);
		}
	}

	public Setting<T> onChange(Consumer<T> listener) {
		this.onChange = listener;
		return this;
	}

	public void reset() {
		set(defaultValue);
	}

	/** Anzeige im Status ("on", "5", "magnet"). */
	public abstract String format(T wert);

	/** Gueltige Eingaben fuer den Command-Baum (Literal-Zweige). Leere Liste = freier Integer. */
	public abstract List<String> literalWerte();

	/** Parst eine Command-/Config-Eingabe; wirft IllegalArgumentException bei Unsinn. */
	public abstract T parse(String eingabe);

	public String formatted() {
		return format(get());
	}

	public void writeJson(JsonObject obj) {
		obj.addProperty(key, format(get()));
	}

	public void readJson(JsonObject obj) {
		if (obj.has(key)) {
			try {
				set(parse(obj.get(key).getAsString()));
			} catch (IllegalArgumentException ignored) {
				// Kaputter Config-Wert -> Default behalten.
			}
		}
	}
}
