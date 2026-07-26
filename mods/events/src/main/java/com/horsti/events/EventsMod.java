package com.horsti.events;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.Random;

public class EventsMod implements ModInitializer {
	// Zufalls-Automatik startet bewusst deaktiviert — Events sind laut.
	private final ModSettings settings = new ModSettings("events", false);
	private final IntSetting blutmondChance = settings.add(new IntSetting("blutmondChance", "%-Chance pro Nacht", 5, 0, 20));
	private final IntSetting meteorChance = settings.add(new IntSetting("meteorChance", "%-Chance pro Tag", 3, 0, 20));
	private final IntSetting blutmondProWelle = settings.add(new IntSetting("blutmondProWelle", "Mobs je Welle und Spieler", 3, 1, 10));
	private final BoolSetting meteorBlockschaden = settings.add(new BoolSetting("meteorBlockschaden", "Meteoriten beschaedigen Bloecke", false));
	private final IntSetting grenzeDauerMin = settings.add(new IntSetting("grenzeDauerMin", "Schrumpf-Dauer in Minuten", 30, 5, 120));
	private final IntSetting grenzeZielRadius = settings.add(new IntSetting("grenzeZielRadius", "Ziel-Radius in Bloecken", 64, 16, 512));

	private final Random random = new Random();
	private final Schrumpfgrenze schrumpfgrenze = new Schrumpfgrenze(grenzeDauerMin, grenzeZielRadius);
	private HorstiEvent[] events;

	private HorstiEvent aktiv = null;
	private long startSek = 0;
	private long jetztSek = 0;
	private long angekuendigtBis = 0;
	private HorstiEvent geplant = null;
	private long letztePruefung = 0;

	@Override
	public void onInitialize() {
		events = new HorstiEvent[]{
			new Blutmond(blutmondProWelle),
			new Meteorregen(meteorBlockschaden),
			schrumpfgrenze
		};

		new HorstiMod("events", "Events", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					abbrechen(HorstiServer.get());
				}
			})
			.extra((root, ctx) -> {
				var trigger = Commands.literal("trigger");
				for (HorstiEvent event : events) {
					trigger.then(Commands.literal(event.id()).executes(c -> {
						starten(c.getSource().getServer(), event);
						return 1;
					}));
				}
				root.then(trigger);
				root.then(Commands.literal("abbrechen").executes(c -> {
					abbrechen(c.getSource().getServer());
					return 1;
				}));
			})
			.registrieren();

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private void sekundenTick(MinecraftServer server) {
		jetztSek++;

		if (aktiv != null) {
			if (!aktiv.tick(server, jetztSek - startSek)) {
				HorstiEvent fertig = aktiv;
				aktiv = null;
				fertig.aufraeumen(server);
			}
			return;
		}
		if (geplant != null) {
			if (jetztSek >= angekuendigtBis) {
				HorstiEvent start = geplant;
				geplant = null;
				aktiv = start;
				startSek = jetztSek;
				start.starten(server);
			}
			return;
		}
		if (!settings.istAktiv()) {
			return;
		}
		// Zufalls-Automatik: einmal pro Minecraft-Tag/Nacht-Wechsel wuerfeln
		long tageszeit = server.overworld().getDayTime() % 24000L;
		boolean nacht = tageszeit >= 13000L && tageszeit < 13100L;
		boolean tag = tageszeit >= 1000L && tageszeit < 1100L;
		if ((nacht || tag) && jetztSek - letztePruefung > 60) {
			letztePruefung = jetztSek;
			if (nacht && random.nextInt(100) < blutmondChance.get()) {
				ankuendigen(server, events[0]);
			} else if (tag && random.nextInt(100) < meteorChance.get()) {
				ankuendigen(server, events[1]);
			}
		}
	}

	private void ankuendigen(MinecraftServer server, HorstiEvent event) {
		geplant = event;
		angekuendigtBis = jetztSek + 60;
		event.ankuendigen(server);
	}

	private void starten(MinecraftServer server, HorstiEvent event) {
		abbrechen(server);
		ankuendigen(server, event);
		server.getPlayerList().broadcastSystemMessage(
			Component.literal("[Events] " + event.id() + " startet in 60 Sekunden."), false);
	}

	private void abbrechen(MinecraftServer server) {
		if (server == null) {
			return;
		}
		geplant = null;
		if (aktiv != null) {
			HorstiEvent altes = aktiv;
			aktiv = null;
			altes.aufraeumen(server);
		}
		schrumpfgrenze.zuruecksetzen(server);
	}
}
