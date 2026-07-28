package com.horsti.woodcutter;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.settings.HorstiLog;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * The recipes live in three built-in datapacks (base / doors / redstone).
 * The toggles switch the packs and reload the recipes live — vanilla clients get
 * the updated recipe list synced automatically.
 */
public class WoodcutterMod implements ModInitializer {
	/** Must match the directory names under resources/resourcepacks/. */
	private static final String[] PACKS = {"base", "doors", "redstone"};
	/** Must match the mod id in fabric.mod.json and the data/ directory inside each pack. */
	private static final String MOD_ID = "horsti_woodcutter";

	private final ModSettings settings = new ModSettings("woodcutter", true);
	private final BoolSetting doors = settings.add(new BoolSetting("doors", "also doors and trapdoors", true));
	private final BoolSetting redstone = settings.add(new BoolSetting("redstone", "also buttons and pressure plates", true));

	private MinecraftServer server;

	@Override
	public void onInitialize() {
		ModContainer container = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
		for (String pack : PACKS) {
			ResourceManagerHelper.registerBuiltinResourcePack(
				Identifier.fromNamespaceAndPath(MOD_ID, pack),
				container,
				Component.literal("Horsti Woodcutter (" + pack + ")"),
				ResourcePackActivationType.DEFAULT_ENABLED);
		}

		new HorstiMod("woodcutter", "Woodcutter", settings)
			.onToggle(this::apply)
			.registrieren();
		doors.onChange(v -> apply());
		redstone.onChange(v -> apply());

		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			this.server = s;
			apply();
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> this.server = null);
	}

	private boolean packWanted(String pack) {
		if (!settings.istAktiv()) {
			return false;
		}
		return switch (pack) {
			case "doors" -> doors.get();
			case "redstone" -> redstone.get();
			default -> true;
		};
	}

	private void apply() {
		if (server == null) {
			return;
		}
		List<String> selected = new ArrayList<>(server.getPackRepository().getSelectedIds());
		boolean changed = false;
		for (String pack : PACKS) {
			String id = MOD_ID + ":" + pack;
			boolean wanted = packWanted(pack);
			if (wanted && !selected.contains(id) && server.getPackRepository().getAvailableIds().contains(id)) {
				selected.add(id);
				changed = true;
			} else if (!wanted && selected.contains(id)) {
				selected.remove(id);
				changed = true;
			}
		}
		if (changed) {
			HorstiLog.info("woodcutter: recipe packs reloaded (" + String.join(", ", selected) + ")");
			server.reloadResources(selected);
		}
	}
}
