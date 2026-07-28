package com.horsti.deathswap;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
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
	private final IntSetting intervalMinutes = settings.add(new IntSetting("intervalMinutes", "base interval in minutes", 5, 1, 30));
	private final IntSetting jitterSeconds = settings.add(new IntSetting("jitterSeconds", "± random window in seconds", 60, 0, 120));
	private final BoolSetting countdown = settings.add(new BoolSetting("countdown", "show the swap countdown", false));
	private final IntSetting minPlayers = settings.add(new IntSetting("minPlayers", "minimum participants", 2, 2, 16));

	private final Random random = new Random();
	private boolean running = false;
	private final Set<UUID> participants = new HashSet<>();
	private final Set<UUID> eliminated = new HashSet<>();
	private long nextSwapTick = 0;
	private long nowTick = 0;

	@Override
	public void onInitialize() {
		new HorstiMod("deathswap", "Deathswap", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					running = false;
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("start").executes(c -> start(c.getSource().getServer())));
				root.then(Commands.literal("stop").executes(c -> {
					finish(c.getSource().getServer(), null);
					return 1;
				}));
			})
			.registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (running && entity instanceof ServerPlayer sp && participants.remove(sp.getUUID())) {
				eliminated.add(sp.getUUID());
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " is out!").withStyle(ChatFormatting.RED));
				checkWinner(HorstiServer.get());
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((old, fresh, alive) -> {
			if (running && eliminated.contains(fresh.getUUID())) {
				fresh.setGameMode(GameType.SPECTATOR);
			}
		});

		Ticker.alleTicks(20, this::secondTick);
	}

	private int start(MinecraftServer server) {
		List<ServerPlayer> players = server.getPlayerList().getPlayers().stream()
			.filter(p -> !p.isSpectator() && !p.isCreative())
			.toList();
		if (players.size() < minPlayers.get()) {
			Broadcast.chat(server, Component.literal("[Deathswap] Not enough players (" + players.size() + "/" + minPlayers.get() + ")").withStyle(ChatFormatting.RED));
			return 0;
		}
		participants.clear();
		eliminated.clear();
		players.forEach(p -> participants.add(p.getUUID()));
		running = true;
		scheduleNextSwap();
		Broadcast.titelAlle(server, Component.literal("DEATHSWAP").withStyle(ChatFormatting.RED),
			Component.literal("Positions swap roughly every " + intervalMinutes.get() + " minutes — survive!"));
		return 1;
	}

	private void scheduleNextSwap() {
		int base = intervalMinutes.get() * 1200;
		int jitter = jitterSeconds.get() > 0 ? (random.nextInt(jitterSeconds.get() * 2 + 1) - jitterSeconds.get()) * 20 : 0;
		nextSwapTick = nowTick + Math.max(200, base + jitter);
	}

	private void secondTick(MinecraftServer server) {
		nowTick += 20;
		if (!running || !settings.istAktiv()) {
			return;
		}
		long secondsLeft = (nextSwapTick - nowTick) / 20;
		if (countdown.get() && secondsLeft > 0 && secondsLeft <= 10) {
			for (ServerPlayer sp : online(server)) {
				Broadcast.actionbar(sp, Component.literal("Swap in " + secondsLeft + "…").withStyle(ChatFormatting.YELLOW));
			}
		}
		if (nowTick >= nextSwapTick) {
			swap(server);
		}
	}

	private void swap(MinecraftServer server) {
		List<ServerPlayer> active = online(server);
		if (active.size() < 2) {
			checkWinner(server);
			return;
		}
		record Spot(ServerLevel level, double x, double y, double z, float yaw, float pitch) {
		}
		List<ServerPlayer> order = new ArrayList<>(active);
		Collections.shuffle(order, random);
		List<Spot> spots = order.stream()
			.map(p -> new Spot((ServerLevel) p.level(), p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot()))
			.toList();
		for (int i = 0; i < order.size(); i++) {
			Spot target = spots.get((i + 1) % spots.size());
			ServerPlayer sp = order.get(i);
			sp.teleportTo(target.level(), target.x(), target.y(), target.z(), Set.of(), target.yaw(), target.pitch(), false);
			Broadcast.titel(sp, Component.literal("SWAP!").withStyle(ChatFormatting.RED), null);
		}
		scheduleNextSwap();
	}

	private List<ServerPlayer> online(MinecraftServer server) {
		return server.getPlayerList().getPlayers().stream()
			.filter(p -> participants.contains(p.getUUID()) && !p.isDeadOrDying())
			.toList();
	}

	private void checkWinner(MinecraftServer server) {
		List<ServerPlayer> active = online(server);
		if (active.size() <= 1) {
			finish(server, active.isEmpty() ? null : active.get(0));
		}
	}

	private void finish(MinecraftServer server, ServerPlayer winner) {
		if (!running) {
			return;
		}
		running = false;
		if (winner != null) {
			Broadcast.titelAlle(server, Component.literal(winner.getName().getString() + " wins!").withStyle(ChatFormatting.GOLD), null);
		} else {
			Broadcast.chat(server, Component.literal("[Deathswap] Round over.").withStyle(ChatFormatting.GRAY));
		}
		// Eliminated players go back to survival
		for (UUID id : eliminated) {
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null) {
				sp.setGameMode(GameType.SURVIVAL);
			}
		}
		participants.clear();
		eliminated.clear();
	}
}
