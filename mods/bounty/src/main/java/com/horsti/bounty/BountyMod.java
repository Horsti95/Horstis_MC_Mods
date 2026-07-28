package com.horsti.bounty;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.settings.StringSetting;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BountyMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("bounty", true);
	private final IntSetting intervalMinutes = settings.add(new IntSetting("intervalMinutes", "gap between bounties (min)", 45, 10, 240));
	private final IntSetting durationMinutes = settings.add(new IntSetting("durationMinutes", "hunting window (min)", 15, 5, 60));
	private final BoolSetting glow = settings.add(new BoolSetting("glow", "the target glows", true));
	private final IntSetting minPlayers = settings.add(new IntSetting("minPlayers", "automatic start needs this many players", 3, 2, 16));
	private final BoolSetting survivalReward = settings.add(new BoolSetting("survivalReward", "the target collects if it survives", true));
	private final StringSetting rewardItem = settings.add(new StringSetting("rewardItem", "reward item", "minecraft:diamond"));
	private final IntSetting rewardAmount = settings.add(new IntSetting("rewardAmount", "reward amount", 3, 1, 64));

	private final Random random = new Random();
	private final Map<UUID, Long> joinTick = new HashMap<>();
	private UUID target = null;
	private UUID previousTarget = null;
	private long endTick = 0;
	private long nextBountyTick = 0;
	private long nowTick = 0;
	private ServerBossEvent bossBar;

	@Override
	public void onInitialize() {
		new HorstiMod("bounty", "Bounty", settings)
			.onToggle(() -> {
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("now")
					.executes(c -> start(c.getSource().getServer(), null))
					.then(Commands.argument("player", EntityArgument.player())
						.executes(c -> start(c.getSource().getServer(), EntityArgument.getPlayer(c, "player")))));
				root.then(Commands.literal("reward")
					.then(Commands.argument("item", ItemArgument.item(ctx))
						.then(Commands.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 64))
							.executes(c -> {
								ItemStack stack = ItemArgument.getItem(c, "item").createItemStack(1);
								rewardItem.set(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
								rewardAmount.set(com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(c, "amount"));
								settings.speichern();
								c.getSource().sendSuccess(() -> Component.literal("[Bounty] Reward: " + rewardAmount.get() + "× " + rewardItem.get()), true);
								return 1;
							}))));
			})
			.registrieren();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			joinTick.put(handler.getPlayer().getUUID(), nowTick));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.getPlayer().getUUID().equals(target)) {
				finish(server, null, false);
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (target == null || !(entity instanceof ServerPlayer victim) || !victim.getUUID().equals(target)) {
				return;
			}
			ServerPlayer killer = source.getEntity() instanceof ServerPlayer k && !k.getUUID().equals(target) ? k : null;
			finish(HorstiServer.get(), killer, false);
		});

		Ticker.alleTicks(20, this::secondTick);
	}

	private void secondTick(MinecraftServer server) {
		nowTick += 20;
		if (!settings.istAktiv()) {
			return;
		}
		if (target == null) {
			if (nextBountyTick == 0) {
				nextBountyTick = nowTick + intervalMinutes.get() * 1200L;
			}
			if (nowTick >= nextBountyTick
				&& server.getPlayerList().getPlayers().size() >= minPlayers.get()) {
				start(server, null);
			}
			return;
		}
		ServerPlayer targetPlayer = server.getPlayerList().getPlayer(target);
		if (targetPlayer == null) {
			finish(server, null, false);
			return;
		}
		if (glow.get()) {
			targetPlayer.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
		}
		long secondsLeft = Math.max(0, (endTick - nowTick) / 20);
		if (bossBar != null) {
			bossBar.setName(Component.literal("Bounty on " + targetPlayer.getName().getString()
				+ " — " + secondsLeft / 60 + ":" + String.format("%02d", secondsLeft % 60)));
			bossBar.setProgress(Math.max(0f, (float) secondsLeft / (durationMinutes.get() * 60f)));
		}
		if (nowTick >= endTick) {
			finish(server, null, true);
		}
	}

	private int start(MinecraftServer server, ServerPlayer wanted) {
		if (target != null) {
			return 0;
		}
		ServerPlayer newTarget = wanted;
		if (newTarget == null) {
			List<ServerPlayer> candidates = server.getPlayerList().getPlayers().stream()
				.filter(p -> !p.isSpectator())
				.filter(p -> !p.getUUID().equals(previousTarget))
				.filter(p -> nowTick - joinTick.getOrDefault(p.getUUID(), 0L) >= 6000L) // 5 min grace after joining
				.toList();
			if (candidates.isEmpty()) {
				nextBountyTick = nowTick + 6000L; // try again in 5 min
				return 0;
			}
			newTarget = candidates.get(random.nextInt(candidates.size()));
		}
		target = newTarget.getUUID();
		endTick = nowTick + durationMinutes.get() * 1200L;
		bossBar = new ServerBossEvent(java.util.UUID.randomUUID(), Component.literal("Bounty!"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
		server.getPlayerList().getPlayers().forEach(bossBar::addPlayer);
		Broadcast.titelAlle(server, Component.literal("BOUNTY!").withStyle(ChatFormatting.RED),
			Component.literal(newTarget.getName().getString() + " is fair game — " + rewardAmount.get() + "× " + pretty(rewardItem.get())));
		return 1;
	}

	private void finish(MinecraftServer server, ServerPlayer killer, boolean survived) {
		UUID oldTarget = target;
		target = null;
		previousTarget = oldTarget;
		nextBountyTick = nowTick + intervalMinutes.get() * 1200L;
		if (bossBar != null) {
			bossBar.removeAllPlayers();
			bossBar = null;
		}
		ServerPlayer targetPlayer = oldTarget != null ? server.getPlayerList().getPlayer(oldTarget) : null;
		if (targetPlayer != null) {
			targetPlayer.removeEffect(MobEffects.GLOWING);
		}
		if (killer != null) {
			payOut(killer);
			Broadcast.titelAlle(server, Component.literal(killer.getName().getString() + " collects the bounty!").withStyle(ChatFormatting.GOLD), null);
		} else if (survived && survivalReward.get() && targetPlayer != null) {
			payOut(targetPlayer);
			Broadcast.titelAlle(server, Component.literal(targetPlayer.getName().getString() + " survived and collects it themselves!").withStyle(ChatFormatting.GOLD), null);
		}
	}

	private void payOut(ServerPlayer recipient) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(rewardItem.get()));
		if (item == Items.AIR) {
			item = Items.DIAMOND;
		}
		ItemStack stack = new ItemStack(item, rewardAmount.get());
		if (!recipient.getInventory().add(stack)) {
			recipient.drop(stack, false);
		}
	}

	private static String pretty(String itemId) {
		int colon = itemId.indexOf(':');
		return colon >= 0 ? itemId.substring(colon + 1) : itemId;
	}
}
