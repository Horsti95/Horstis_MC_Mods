package com.horsti.deathswap;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class DeathswapMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("deathswap", true);
	private final IntSetting intervallMin = settings.add(new IntSetting("intervallMin", "Basis-Intervall in Minuten", 5, 1, 30));
	private final IntSetting zufallSek = settings.add(new IntSetting("zufallSek", "± Zufallsfenster in Sekunden", 60, 0, 120));
	private final BoolSetting countdown = settings.add(new BoolSetting("countdown", "Tausch-Countdown sichtbar", false));
	private final IntSetting minSpieler = settings.add(new IntSetting("minSpieler", "Mindestteilnehmer", 2, 2, 16));

	private final Random random = new Random();
	private boolean laeuft = false;
	private final Set<UUID> teilnehmer = new HashSet<>();
	private final Set<UUID> ausgeschieden = new HashSet<>();
	private long naechsterTauschTick = 0;
	private long jetztTick = 0;

	@Override
	public void onInitialize() {
		new HorstiMod("deathswap", "Deathswap", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					laeuft = false;
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("start").executes(c -> start(c.getSource().getServer())));
				root.then(Commands.literal("stop").executes(c -> {
					beenden(c.getSource().getServer(), null);
					return 1;
				}));
			})
			.registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (laeuft && entity instanceof ServerPlayer sp && teilnehmer.remove(sp.getUUID())) {
				ausgeschieden.add(sp.getUUID());
				Broadcast.chat(sp.getServer(), Component.literal(sp.getName().getString() + " ist raus!").withStyle(ChatFormatting.RED));
				pruefeSieg(sp.getServer());
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((alt, neu, lebt) -> {
			if (laeuft && ausgeschieden.contains(neu.getUUID())) {
				neu.setGameMode(GameType.SPECTATOR);
			}
		});

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private int start(MinecraftServer server) {
		List<ServerPlayer> spieler = server.getPlayerList().getPlayers().stream()
			.filter(p -> !p.isSpectator() && !p.isCreative())
			.toList();
		if (spieler.size() < minSpieler.get()) {
			Broadcast.chat(server, Component.literal("[Deathswap] Zu wenige Spieler (" + spieler.size() + "/" + minSpieler.get() + ")").withStyle(ChatFormatting.RED));
			return 0;
		}
		teilnehmer.clear();
		ausgeschieden.clear();
		spieler.forEach(p -> teilnehmer.add(p.getUUID()));
		laeuft = true;
		planeNaechstenTausch();
		Broadcast.titelAlle(server, Component.literal("DEATHSWAP").withStyle(ChatFormatting.RED),
			Component.literal("Positions-Tausch alle ~" + intervallMin.get() + " Minuten — überlebe!"));
		return 1;
	}

	private void planeNaechstenTausch() {
		int basis = intervallMin.get() * 1200;
		int zufall = zufallSek.get() > 0 ? (random.nextInt(zufallSek.get() * 2 + 1) - zufallSek.get()) * 20 : 0;
		naechsterTauschTick = jetztTick + Math.max(200, basis + zufall);
	}

	private void sekundenTick(MinecraftServer server) {
		jetztTick += 20;
		if (!laeuft || !settings.istAktiv()) {
			return;
		}
		long restSek = (naechsterTauschTick - jetztTick) / 20;
		if (countdown.get() && restSek > 0 && restSek <= 10) {
			for (ServerPlayer sp : online(server)) {
				Broadcast.actionbar(sp, Component.literal("Tausch in " + restSek + "…").withStyle(ChatFormatting.YELLOW));
			}
		}
		if (jetztTick >= naechsterTauschTick) {
			tauschen(server);
		}
	}

	private void tauschen(MinecraftServer server) {
		List<ServerPlayer> aktive = online(server);
		if (aktive.size() < 2) {
			pruefeSieg(server);
			return;
		}
		record Ort(ServerLevel level, double x, double y, double z, float yaw, float pitch) {
		}
		List<ServerPlayer> reihenfolge = new ArrayList<>(aktive);
		Collections.shuffle(reihenfolge, random);
		List<Ort> orte = reihenfolge.stream()
			.map(p -> new Ort(p.serverLevel(), p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot()))
			.toList();
		for (int i = 0; i < reihenfolge.size(); i++) {
			Ort ziel = orte.get((i + 1) % orte.size());
			ServerPlayer sp = reihenfolge.get(i);
			sp.teleportTo(ziel.level(), ziel.x(), ziel.y(), ziel.z(), Set.of(), ziel.yaw(), ziel.pitch(), false);
			Broadcast.titel(sp, Component.literal("TAUSCH!").withStyle(ChatFormatting.RED), null);
		}
		planeNaechstenTausch();
	}

	private List<ServerPlayer> online(MinecraftServer server) {
		return server.getPlayerList().getPlayers().stream()
			.filter(p -> teilnehmer.contains(p.getUUID()) && !p.isDeadOrDying())
			.toList();
	}

	private void pruefeSieg(MinecraftServer server) {
		List<ServerPlayer> aktive = online(server);
		if (aktive.size() <= 1) {
			beenden(server, aktive.isEmpty() ? null : aktive.get(0));
		}
	}

	private void beenden(MinecraftServer server, ServerPlayer sieger) {
		if (!laeuft) {
			return;
		}
		laeuft = false;
		if (sieger != null) {
			Broadcast.titelAlle(server, Component.literal(sieger.getName().getString() + " gewinnt!").withStyle(ChatFormatting.GOLD), null);
		} else {
			Broadcast.chat(server, Component.literal("[Deathswap] Runde beendet.").withStyle(ChatFormatting.GRAY));
		}
		// Ausgeschiedene zurueck in den Ueberlebensmodus
		for (UUID id : ausgeschieden) {
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null) {
				sp.setGameMode(GameType.SURVIVAL);
			}
		}
		teilnehmer.clear();
		ausgeschieden.clear();
	}
}
