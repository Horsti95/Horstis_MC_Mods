package com.horsti.core.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Alle Settings eines Mods + Persistenz in config/horsti/<id>.json.
 * "aktiv" (on/off) ist als erstes Setting immer dabei.
 */
public class ModSettings {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final String configName;
	private final Map<String, Setting<?>> settings = new LinkedHashMap<>();
	public final BoolSetting aktiv;

	public ModSettings(String configName, boolean defaultAktiv) {
		this.configName = configName;
		this.aktiv = add(new BoolSetting("aktiv", "Mod an/aus", defaultAktiv));
	}

	public <S extends Setting<?>> S add(S setting) {
		settings.put(setting.key, setting);
		return setting;
	}

	public Setting<?> get(String key) {
		return settings.get(key);
	}

	public Iterable<Setting<?>> alle() {
		return settings.values();
	}

	public boolean istAktiv() {
		return aktiv.get();
	}

	private Path pfad() {
		return FabricLoader.getInstance().getConfigDir().resolve("horsti").resolve(configName + ".json");
	}

	public void speichern() {
		JsonObject obj = new JsonObject();
		for (Setting<?> s : settings.values()) {
			s.writeJson(obj);
		}
		try {
			Files.createDirectories(pfad().getParent());
			Files.writeString(pfad(), GSON.toJson(obj));
		} catch (IOException e) {
			HorstiLog.warn(configName + ": Config konnte nicht gespeichert werden: " + e.getMessage());
		}
	}

	public void laden() {
		Path p = pfad();
		if (!Files.exists(p)) {
			speichern(); // Defaults anlegen, damit die Datei zum Editieren da ist
			return;
		}
		try {
			JsonObject obj = JsonParser.parseString(Files.readString(p)).getAsJsonObject();
			for (Setting<?> s : settings.values()) {
				s.readJson(obj);
			}
		} catch (Exception e) {
			HorstiLog.warn(configName + ": Config unlesbar, Defaults bleiben aktiv: " + e.getMessage());
		}
	}

	public void resetAlle() {
		for (Setting<?> s : settings.values()) {
			s.reset();
		}
		speichern();
	}
}
