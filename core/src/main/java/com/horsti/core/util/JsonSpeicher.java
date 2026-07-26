package com.horsti.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.horsti.core.settings.HorstiLog;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Einfache JSON-Persistenz fuer Mod-Spielstaende (Herzen, Nemesis, Graeber …).
 * Liegt unter config/horsti/daten/<name>.json — bewusst kein NBT/Welt-Speicher,
 * damit die Mods unabhaengig von Mojangs Serialisierungs-Umbauten bleiben.
 * Hinweis: die Datei gilt pro Server-Instanz, nicht pro Welt.
 */
public final class JsonSpeicher {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final String name;

	public JsonSpeicher(String name) {
		this.name = name;
	}

	private Path pfad() {
		return FabricLoader.getInstance().getConfigDir().resolve("horsti").resolve("daten").resolve(name + ".json");
	}

	public JsonObject laden() {
		Path p = pfad();
		if (!Files.exists(p)) {
			return new JsonObject();
		}
		try {
			return JsonParser.parseString(Files.readString(p)).getAsJsonObject();
		} catch (Exception e) {
			HorstiLog.warn(name + ": Daten unlesbar, starte leer: " + e.getMessage());
			return new JsonObject();
		}
	}

	public void speichern(JsonObject daten) {
		try {
			Files.createDirectories(pfad().getParent());
			Files.writeString(pfad(), GSON.toJson(daten));
		} catch (IOException e) {
			HorstiLog.warn(name + ": Daten konnten nicht gespeichert werden: " + e.getMessage());
		}
	}
}
