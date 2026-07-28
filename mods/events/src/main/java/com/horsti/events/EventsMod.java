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
	// The random automation starts disabled on purpose — events are loud.
	private final ModSettings settings = new ModSettings("events", false);
	private final IntSetting checkIntervalMinutes = settings.add(new IntSetting("checkIntervalMinutes", "minutes between two rolls", 20, 5, 240));
	private final IntSetting bloodMoonChance = settings.add(new IntSetting("bloodMoonChance", "% chance per roll", 15, 0, 100));
	private final IntSetting meteorChance = settings.add(new IntSetting("meteorChance", "% chance per roll", 10, 0, 100));
	private final IntSetting bloodMoonPerWave = settings.add(new IntSetting("bloodMoonPerWave", "mobs per wave and player", 3, 1, 10));
	private final IntSetting bloodMoonMinutes = settings.add(new IntSetting("bloodMoonMinutes", "blood moon duration in minutes", 8, 1, 30));
	private final BoolSetting meteorBlockDamage = settings.add(new BoolSetting("meteorBlockDamage", "meteors damage blocks", false));
	private final IntSetting borderMinutes = settings.add(new IntSetting("borderMinutes", "shrink duration in minutes", 30, 5, 120));
	private final IntSetting borderTargetRadius = settings.add(new IntSetting("borderTargetRadius", "target radius in blocks", 64, 16, 512));

	private final Random random = new Random();
	private final ShrinkingBorder shrinkingBorder = new ShrinkingBorder(borderMinutes, borderTargetRadius);
	private HorstiEvent[] events;

	private HorstiEvent active = null;
	private long startSecond = 0;
	private long nowSecond = 0;
	private long announcedUntil = 0;
	private HorstiEvent planned = null;
	private long lastRoll = 0;

	@Override
	public void onInitialize() {
		events = new HorstiEvent[]{
			new BloodMoon(bloodMoonPerWave, bloodMoonMinutes),
			new MeteorShower(meteorBlockDamage),
			shrinkingBorder
		};

		new HorstiMod("events", "Events", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					cancel(HorstiServer.get());
				}
			})
			.extra((root, ctx) -> {
				var trigger = Commands.literal("trigger");
				for (HorstiEvent event : events) {
					trigger.then(Commands.literal(event.id()).executes(c -> {
						startNow(c.getSource().getServer(), event);
						return 1;
					}));
				}
				root.then(trigger);
				root.then(Commands.literal("cancel").executes(c -> {
					cancel(c.getSource().getServer());
					return 1;
				}));
			})
			.registrieren();

		Ticker.alleTicks(20, this::secondTick);
	}

	private void secondTick(MinecraftServer server) {
		nowSecond++;

		if (active != null) {
			if (!active.tick(server, nowSecond - startSecond)) {
				HorstiEvent done = active;
				active = null;
				done.cleanUp(server);
			}
			return;
		}
		if (planned != null) {
			if (nowSecond >= announcedUntil) {
				HorstiEvent starting = planned;
				planned = null;
				active = starting;
				startSecond = nowSecond;
				starting.start(server);
			}
			return;
		}
		if (!settings.istAktiv() || server.getPlayerList().getPlayers().isEmpty()) {
			return;
		}
		// Random automation: roll the dice every checkIntervalMinutes minutes
		if (nowSecond - lastRoll < checkIntervalMinutes.get() * 60L) {
			return;
		}
		lastRoll = nowSecond;
		if (random.nextInt(100) < bloodMoonChance.get()) {
			announce(server, events[0]);
		} else if (random.nextInt(100) < meteorChance.get()) {
			announce(server, events[1]);
		}
	}

	private void announce(MinecraftServer server, HorstiEvent event) {
		planned = event;
		announcedUntil = nowSecond + 60;
		event.announce(server);
	}

	private void startNow(MinecraftServer server, HorstiEvent event) {
		cancel(server);
		announce(server, event);
		server.getPlayerList().broadcastSystemMessage(
			Component.literal("[Events] " + event.id() + " starts in 60 seconds."), false);
	}

	private void cancel(MinecraftServer server) {
		if (server == null) {
			return;
		}
		planned = null;
		if (active != null) {
			HorstiEvent previous = active;
			active = null;
			previous.cleanUp(server);
		}
		shrinkingBorder.reset(server);
	}
}
