package com.horsti.cobblekeys;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * config/horsti/cobble-keys.json — plain JSON, edited by hand.
 *
 * <p>No Cloth Config on purpose: this mod should not drag a config-library dependency
 * behind it just to hold four values.
 */
public class KeysConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/** Which mod's keybinds are listed — matched against the keybind and category names. */
	public String namespace = "cobblemon";
	/** Open the list once automatically, the first time you join a world. */
	public boolean showOnFirstJoin = true;
	/** Already shown once — set by the mod, not by you. */
	public boolean firstJoinDone = false;
	/** Your own lines at the bottom of the list: commands you keep forgetting. */
	public List<String> notes = new ArrayList<>(List.of(
		"/pc — open your Pokémon storage",
		"/pokedex — open the Pokédex"));

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("horsti").resolve("cobble-keys.json");
	}

	public static KeysConfig load() {
		Path p = path();
		if (!Files.exists(p)) {
			KeysConfig fresh = new KeysConfig();
			fresh.save();
			return fresh;
		}
		try {
			KeysConfig loaded = GSON.fromJson(Files.readString(p), KeysConfig.class);
			return loaded != null ? loaded : new KeysConfig();
		} catch (Exception e) {
			// A broken config costs the defaults, never the game.
			return new KeysConfig();
		}
	}

	public void save() {
		try {
			Files.createDirectories(path().getParent());
			Files.writeString(path(), GSON.toJson(this));
		} catch (IOException ignored) {
			// Read-only config dir: run with defaults rather than complain every launch.
		}
	}
}
