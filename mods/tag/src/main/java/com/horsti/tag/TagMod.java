package com.horsti.tag;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class TagMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("tag", true);
	private final IntSetting roundMinutes = settings.add(new IntSetting("roundMinutes", "round length in minutes", 10, 1, 60));
	private final IntSetting itSpeed = settings.add(new IntSetting("itSpeed", "speed level for 'It' (0 = off)", 1, 0, 2));
	private final BoolSetting itGlow = settings.add(new BoolSetting("itGlow", "'It' glows", true));
	private final IntSetting graceSeconds = settings.add(new IntSetting("graceSeconds", "grace period after a handover", 5, 0, 30));
	private final BoolSetting damage = settings.add(new BoolSetting("damage", "tag hits deal real damage", false));

	private final Random random = new Random();
	private boolean running = false;
	private UUID it = null;
	private long endTick = 0;
	private long graceUntilTick = 0;
	private long nowTick = 0;
	private final Set<UUID> participants = new HashSet<>();
	private final Map<UUID, Integer> points = new HashMap<>();
	private ServerBossEvent bossBar;

	@Override
	public void onInitialize() {
		new HorstiMod("tag", "Tag", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					stop(HorstiServer.get(), false);
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("start").executes(c -> start(c.getSource().getServer())));
				root.then(Commands.literal("stop").executes(c -> {
					stop(c.getSource().getServer(), true);
					return 1;
				}));
			})
			.registrieren();

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
			if (!running || !(player instanceof ServerPlayer attacker) || !(entity instanceof ServerPlayer target)) {
				return InteractionResult.PASS;
			}
			if (!attacker.getUUID().equals(it) || !participants.contains(target.getUUID()) || target.isSpectator()) {
				return InteractionResult.PASS;
			}
			if (nowTick < graceUntilTick) {
				Broadcast.actionbar(attacker, Component.literal("Still protected!").withStyle(ChatFormatting.GRAY));
				return InteractionResult.FAIL;
			}
			handOver(attacker, target);
			return damage.get() ? InteractionResult.PASS : InteractionResult.FAIL;
		});

		Ticker.alleTicks(20, this::secondTick);
	}

	private int start(MinecraftServer server) {
		List<ServerPlayer> players = server.getPlayerList().getPlayers().stream()
			.filter(p -> !p.isSpectator())
			.toList();
		if (players.size() < 2) {
			Broadcast.chat(server, Component.literal("[Tag] At least 2 players needed.").withStyle(ChatFormatting.RED));
			return 0;
		}
		participants.clear();
		points.clear();
		players.forEach(p -> {
			participants.add(p.getUUID());
			points.put(p.getUUID(), 0);
		});
		running = true;
		endTick = nowTick + roundMinutes.get() * 1200L;
		ServerPlayer first = players.get(random.nextInt(players.size()));
		setIt(first);
		bossBar = new ServerBossEvent(UUID.randomUUID(), Component.literal("Tag"),
			BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
		players.forEach(bossBar::addPlayer);
		Broadcast.titelAlle(server, Component.literal("TAG!").withStyle(ChatFormatting.YELLOW),
			Component.literal(first.getName().getString() + " is It — run!"));
		return 1;
	}

	private void setIt(ServerPlayer player) {
		it = player.getUUID();
		graceUntilTick = nowTick + graceSeconds.get() * 20L;
		Broadcast.titel(player, Component.literal("YOU ARE IT!").withStyle(ChatFormatting.RED), null);
	}

	private void handOver(ServerPlayer previous, ServerPlayer next) {
		clearEffects(previous);
		setIt(next);
		Broadcast.chat(HorstiServer.get(), Component.literal(next.getName().getString() + " is It now!")
			.withStyle(ChatFormatting.YELLOW));
	}

	private void clearEffects(ServerPlayer player) {
		player.removeEffect(MobEffects.SPEED);
		player.removeEffect(MobEffects.GLOWING);
	}

	private void secondTick(MinecraftServer server) {
		nowTick += 20;
		if (!running || !settings.istAktiv()) {
			return;
		}
		ServerPlayer itPlayer = it == null ? null : server.getPlayerList().getPlayer(it);
		if (itPlayer == null || itPlayer.isSpectator()) {
			// "It" went offline or dropped out — draw a new one
			List<ServerPlayer> candidates = server.getPlayerList().getPlayers().stream()
				.filter(p -> participants.contains(p.getUUID()) && !p.isSpectator())
				.toList();
			if (candidates.isEmpty()) {
				stop(server, false);
				return;
			}
			setIt(candidates.get(random.nextInt(candidates.size())));
			itPlayer = server.getPlayerList().getPlayer(it);
		}

		// Refresh effects with a short duration so they expire after the round
		if (itSpeed.get() > 0) {
			itPlayer.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, itSpeed.get() - 1, true, false));
		}
		if (itGlow.get()) {
			itPlayer.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
		}

		// One point per second for everyone who is not It
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (participants.contains(player.getUUID()) && !player.getUUID().equals(it) && !player.isSpectator()) {
				points.merge(player.getUUID(), 1, Integer::sum);
			}
		}

		long secondsLeft = Math.max(0, (endTick - nowTick) / 20);
		if (bossBar != null) {
			bossBar.setName(Component.literal("Tag — " + secondsLeft / 60 + ":" + String.format("%02d", secondsLeft % 60) + " left"));
			bossBar.setProgress(Math.max(0f, (float) secondsLeft / (roundMinutes.get() * 60f)));
		}
		if (nowTick >= endTick) {
			stop(server, true);
		}
	}

	private void stop(MinecraftServer server, boolean withScores) {
		if (!running || server == null) {
			return;
		}
		running = false;
		ServerPlayer itPlayer = it == null ? null : server.getPlayerList().getPlayer(it);
		if (itPlayer != null) {
			clearEffects(itPlayer);
		}
		if (bossBar != null) {
			bossBar.removeAllPlayers();
			bossBar = null;
		}
		if (withScores) {
			String loser = itPlayer != null ? itPlayer.getName().getString() : "?";
			Broadcast.titelAlle(server, Component.literal("Round over!").withStyle(ChatFormatting.GOLD),
				Component.literal(loser + " was left as It and loses."));
			List<Map.Entry<UUID, Integer>> best = points.entrySet().stream()
				.sorted(Comparator.<Map.Entry<UUID, Integer>>comparingInt(Map.Entry::getValue).reversed())
				.limit(3)
				.toList();
			StringBuilder sb = new StringBuilder("[Tag] Best runners: ");
			for (int i = 0; i < best.size(); i++) {
				ServerPlayer p = server.getPlayerList().getPlayer(best.get(i).getKey());
				String name = p != null ? p.getName().getString() : "?";
				sb.append(i + 1).append(". ").append(name).append(" (").append(best.get(i).getValue()).append("s)  ");
			}
			Broadcast.chat(server, Component.literal(sb.toString()).withStyle(ChatFormatting.YELLOW));
		}
		it = null;
		participants.clear();
	}
}
