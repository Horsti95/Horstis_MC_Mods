package com.horsti.holzsaege;

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
 * Die Rezepte stecken in drei eingebauten Datapacks (basis / tueren / redstone).
 * Toggles schalten die Packs um und laden die Rezepte live neu — Vanilla-Clients
 * bekommen die aktualisierte Rezeptliste automatisch gesynct.
 */
public class HolzsaegeMod implements ModInitializer {
	private static final String[] PACKS = {"basis", "tueren", "redstone"};

	private final ModSettings settings = new ModSettings("holzsaege", true);
	private final BoolSetting tueren = settings.add(new BoolSetting("tueren", "auch Tueren/Falltueren", true));
	private final BoolSetting redstone = settings.add(new BoolSetting("redstone", "auch Knoepfe/Druckplatten", true));

	private MinecraftServer server;

	@Override
	public void onInitialize() {
		ModContainer container = FabricLoader.getInstance().getModContainer("horsti_holzsaege").orElseThrow();
		for (String pack : PACKS) {
			ResourceManagerHelper.registerBuiltinResourcePack(
				Identifier.fromNamespaceAndPath("horsti_holzsaege", pack),
				container,
				Component.literal("Horsti Holzsäge (" + pack + ")"),
				ResourcePackActivationType.DEFAULT_ENABLED);
		}

		new HorstiMod("holzsaege", "Holzsäge", settings)
			.onToggle(this::anwenden)
			.registrieren();
		tueren.onChange(w -> anwenden());
		redstone.onChange(w -> anwenden());

		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			this.server = s;
			anwenden();
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> this.server = null);
	}

	private boolean packGewuenscht(String pack) {
		if (!settings.istAktiv()) {
			return false;
		}
		return switch (pack) {
			case "tueren" -> tueren.get();
			case "redstone" -> redstone.get();
			default -> true;
		};
	}

	private void anwenden() {
		if (server == null) {
			return;
		}
		List<String> auswahl = new ArrayList<>(server.getPackRepository().getSelectedIds());
		boolean geaendert = false;
		for (String pack : PACKS) {
			String id = "horsti_holzsaege:" + pack;
			boolean soll = packGewuenscht(pack);
			if (soll && !auswahl.contains(id) && server.getPackRepository().getAvailableIds().contains(id)) {
				auswahl.add(id);
				geaendert = true;
			} else if (!soll && auswahl.contains(id)) {
				auswahl.remove(id);
				geaendert = true;
			}
		}
		if (geaendert) {
			HorstiLog.info("holzsaege: Rezept-Packs neu geladen (" + String.join(", ", auswahl) + ")");
			server.reloadResources(auswahl);
		}
	}
}
