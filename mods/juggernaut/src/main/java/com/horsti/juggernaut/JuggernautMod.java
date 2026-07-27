package com.horsti.juggernaut;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Attribute;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class JuggernautMod implements ModInitializer {
	private static final Identifier HEALTH_ID = Attribute.id("juggernaut", "health");

	private final ModSettings settings = new ModSettings("juggernaut", true);
	private final IntSetting heartsPerHunter = settings.add(new IntSetting("heartsPerHunter", "extra hearts per hunter", 4, 1, 10));
	private final IntSetting strengthFrom = settings.add(new IntSetting("strengthFrom", "Strength I from this many hunters, II at double", 4, 2, 10));
	private final BoolSetting glow = settings.add(new BoolSetting("glow", "juggernaut glows", true));
	private final IntSetting killQuota = settings.add(new IntSetting("killQuota", "kills per hunter needed for a juggernaut win", 1, 1, 5));

	private final Random random = new Random();
	private boolean running = false;
	private UUID juggernaut = null;
	private final Set<UUID> hunters = new HashSet<>();
	private final Map<UUID, Integer> kills = new HashMap<>();
	private ServerBossEvent bossBar;

	@Override
	public void onInitialize() {
		new HorstiMod("juggernaut", "Juggernaut", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					stop(HorstiServer.get(), null);
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("start")
					.then(Commands.literal("random").executes(c -> start(c.getSource().getServer(), null)))
					.then(Commands.argument("player", EntityArgument.player())
						.executes(c -> start(c.getSource().getServer(), EntityArgument.getPlayer(c, "player")))));
				root.then(Commands.literal("stop").executes(c -> {
					stop(c.getSource().getServer(), null);
					return 1;
				}));
			})
			.registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!running || !(entity instanceof ServerPlayer victim)) {
				return;
			}
			MinecraftServer server = HorstiServer.get();
			if (victim.getUUID().equals(juggernaut)) {
				stop(server, "hunters");
				return;
			}
			if (hunters.contains(victim.getUUID()) && source.getEntity() instanceof ServerPlayer killer
				&& killer.getUUID().equals(juggernaut)) {
				kills.merge(victim.getUUID(), 1, Integer::sum);
				boolean allDown = hunters.stream().allMatch(id -> kills.getOrDefault(id, 0) >= killQuota.get());
				if (allDown) {
					stop(server, "juggernaut");
				}
			}
		});

		Ticker.alleTicks(20, this::secondTick);
	}

	private int start(MinecraftServer server, ServerPlayer chosen) {
		List<ServerPlayer> players = server.getPlayerList().getPlayers().stream()
			.filter(p -> !p.isSpectator())
			.toList();
		if (players.size() < 2) {
			Broadcast.chat(server, Component.literal("[Juggernaut] At least 2 players needed.").withStyle(ChatFormatting.RED));
			return 0;
		}
		ServerPlayer jug = chosen != null ? chosen : players.get(random.nextInt(players.size()));
		juggernaut = jug.getUUID();
		hunters.clear();
		kills.clear();
		players.stream().filter(p -> !p.getUUID().equals(juggernaut)).forEach(p -> hunters.add(p.getUUID()));
		running = true;

		buff(jug, hunters.size());
		bossBar = new ServerBossEvent(UUID.randomUUID(), Component.literal("Juggernaut"),
			BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
		players.forEach(bossBar::addPlayer);
		Broadcast.titelAlle(server, Component.literal("JUGGERNAUT!").withStyle(ChatFormatting.DARK_PURPLE),
			Component.literal(jug.getName().getString() + " against " + hunters.size() + " hunters"));
		return 1;
	}

	private void buff(ServerPlayer jug, int hunterCount) {
		Attribute.addieren(jug, Attributes.MAX_HEALTH, HEALTH_ID, hunterCount * heartsPerHunter.get() * 2.0);
		jug.setHealth(jug.getMaxHealth());
	}

	private void secondTick(MinecraftServer server) {
		if (!running || !settings.istAktiv()) {
			return;
		}
		ServerPlayer jug = juggernaut == null ? null : server.getPlayerList().getPlayer(juggernaut);
		if (jug == null) {
			stop(server, "hunters");
			return;
		}
		int aliveHunters = (int) hunters.stream()
			.map(id -> server.getPlayerList().getPlayer(id))
			.filter(p -> p != null && !p.isSpectator())
			.count();
		if (aliveHunters == 0) {
			stop(server, "juggernaut");
			return;
		}
		// Strength scales live with how many hunters are still standing
		int level = Math.min(2, aliveHunters / Math.max(1, strengthFrom.get()));
		if (level > 0) {
			jug.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60, level - 1, true, false));
		}
		jug.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0, true, false));
		if (glow.get()) {
			jug.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
		}
		if (bossBar != null) {
			bossBar.setName(Component.literal(jug.getName().getString() + " — "
				+ (int) jug.getHealth() + "/" + (int) jug.getMaxHealth() + " HP"));
			bossBar.setProgress(Math.max(0f, jug.getHealth() / jug.getMaxHealth()));
		}
	}

	private void stop(MinecraftServer server, String winner) {
		if (!running || server == null) {
			return;
		}
		running = false;
		ServerPlayer jug = juggernaut == null ? null : server.getPlayerList().getPlayer(juggernaut);
		if (jug != null) {
			Attribute.entfernen(jug, Attributes.MAX_HEALTH, HEALTH_ID);
			jug.removeEffect(MobEffects.STRENGTH);
			jug.removeEffect(MobEffects.RESISTANCE);
			jug.removeEffect(MobEffects.GLOWING);
			if (jug.getHealth() > jug.getMaxHealth()) {
				jug.setHealth(jug.getMaxHealth());
			}
		}
		if (bossBar != null) {
			bossBar.removeAllPlayers();
			bossBar = null;
		}
		if (winner != null) {
			String text = winner.equals("juggernaut")
				? (jug != null ? jug.getName().getString() : "The juggernaut") + " holds the line!"
				: "The hunters brought the juggernaut down!";
			Broadcast.titelAlle(server, Component.literal(text).withStyle(ChatFormatting.GOLD), null);
		}
		juggernaut = null;
		hunters.clear();
		kills.clear();
	}
}
