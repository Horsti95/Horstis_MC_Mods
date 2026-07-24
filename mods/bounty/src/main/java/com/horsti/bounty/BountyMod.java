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
	private final IntSetting intervallMin = settings.add(new IntSetting("intervallMin", "Abstand zwischen Kopfgeldern (Min.)", 45, 10, 240));
	private final IntSetting dauerMin = settings.add(new IntSetting("dauerMin", "Jagd-Frist (Min.)", 15, 5, 60));
	private final BoolSetting glow = settings.add(new BoolSetting("glow", "Ziel leuchtet", true));
	private final IntSetting minSpieler = settings.add(new IntSetting("minSpieler", "Automatik erst ab X Spielern", 3, 2, 16));
	private final BoolSetting ueberlebensPraemie = settings.add(new BoolSetting("ueberlebensPraemie", "Ziel kassiert bei Ueberleben", true));
	private final StringSetting belohnungItem = settings.add(new StringSetting("belohnungItem", "Belohnungs-Item", "minecraft:diamond"));
	private final IntSetting belohnungAnzahl = settings.add(new IntSetting("belohnungAnzahl", "Belohnungs-Anzahl", 3, 1, 64));

	private final Random random = new Random();
	private final Map<UUID, Long> joinTick = new HashMap<>();
	private UUID ziel = null;
	private UUID letztesZiel = null;
	private long endTick = 0;
	private long naechsteBountyTick = 0;
	private long jetztTick = 0;
	private ServerBossEvent bossBar;

	@Override
	public void onInitialize() {
		new HorstiMod("bounty", "Bounty", settings)
			.onToggle(() -> {
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("jetzt")
					.executes(c -> starten(c.getSource().getServer(), null))
					.then(Commands.argument("spieler", EntityArgument.player())
						.executes(c -> starten(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler")))));
				root.then(Commands.literal("belohnung")
					.then(Commands.argument("item", ItemArgument.item(ctx))
						.then(Commands.argument("anzahl", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 64))
							.executes(c -> {
								ItemStack stack = ItemArgument.getItem(c, "item").createItemStack(1);
								belohnungItem.set(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
								belohnungAnzahl.set(com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(c, "anzahl"));
								settings.speichern();
								c.getSource().sendSuccess(() -> Component.literal("[Bounty] Belohnung: " + belohnungAnzahl.get() + "× " + belohnungItem.get()), true);
								return 1;
							}))));
			})
			.registrieren();

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
			joinTick.put(handler.getPlayer().getUUID(), jetztTick));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.getPlayer().getUUID().equals(ziel)) {
				beenden(server, null, false);
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (ziel == null || !(entity instanceof ServerPlayer opfer) || !opfer.getUUID().equals(ziel)) {
				return;
			}
			ServerPlayer killer = source.getEntity() instanceof ServerPlayer k && !k.getUUID().equals(ziel) ? k : null;
			beenden(HorstiServer.get(), killer, false);
		});

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private void sekundenTick(MinecraftServer server) {
		jetztTick += 20;
		if (!settings.istAktiv()) {
			return;
		}
		if (ziel == null) {
			if (naechsteBountyTick == 0) {
				naechsteBountyTick = jetztTick + intervallMin.get() * 1200L;
			}
			if (jetztTick >= naechsteBountyTick
				&& server.getPlayerList().getPlayers().size() >= minSpieler.get()) {
				starten(server, null);
			}
			return;
		}
		ServerPlayer zielSpieler = server.getPlayerList().getPlayer(ziel);
		if (zielSpieler == null) {
			beenden(server, null, false);
			return;
		}
		if (glow.get()) {
			zielSpieler.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
		}
		long restSek = Math.max(0, (endTick - jetztTick) / 20);
		if (bossBar != null) {
			bossBar.setName(Component.literal("Kopfgeld auf " + zielSpieler.getName().getString()
				+ " — " + restSek / 60 + ":" + String.format("%02d", restSek % 60)));
			bossBar.setProgress(Math.max(0f, (float) restSek / (dauerMin.get() * 60f)));
		}
		if (jetztTick >= endTick) {
			beenden(server, null, true);
		}
	}

	private int starten(MinecraftServer server, ServerPlayer gewuenscht) {
		if (ziel != null) {
			return 0;
		}
		ServerPlayer neuesZiel = gewuenscht;
		if (neuesZiel == null) {
			List<ServerPlayer> kandidaten = server.getPlayerList().getPlayers().stream()
				.filter(p -> !p.isSpectator())
				.filter(p -> !p.getUUID().equals(letztesZiel))
				.filter(p -> jetztTick - joinTick.getOrDefault(p.getUUID(), 0L) >= 6000L) // 5 Min. Karenz
				.toList();
			if (kandidaten.isEmpty()) {
				naechsteBountyTick = jetztTick + 6000L; // in 5 Min. nochmal versuchen
				return 0;
			}
			neuesZiel = kandidaten.get(random.nextInt(kandidaten.size()));
		}
		ziel = neuesZiel.getUUID();
		endTick = jetztTick + dauerMin.get() * 1200L;
		bossBar = new ServerBossEvent(java.util.UUID.randomUUID(), Component.literal("Kopfgeld!"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
		server.getPlayerList().getPlayers().forEach(bossBar::addPlayer);
		Broadcast.titelAlle(server, Component.literal("KOPFGELD!").withStyle(ChatFormatting.RED),
			Component.literal(neuesZiel.getName().getString() + " ist zum Abschuss freigegeben — " + belohnungAnzahl.get() + "× " + hübsch(belohnungItem.get())));
		return 1;
	}

	private void beenden(MinecraftServer server, ServerPlayer killer, boolean ueberlebt) {
		UUID alterZiel = ziel;
		ziel = null;
		letztesZiel = alterZiel;
		naechsteBountyTick = jetztTick + intervallMin.get() * 1200L;
		if (bossBar != null) {
			bossBar.removeAllPlayers();
			bossBar = null;
		}
		ServerPlayer zielSpieler = alterZiel != null ? server.getPlayerList().getPlayer(alterZiel) : null;
		if (zielSpieler != null) {
			zielSpieler.removeEffect(MobEffects.GLOWING);
		}
		if (killer != null) {
			auszahlen(killer);
			Broadcast.titelAlle(server, Component.literal(killer.getName().getString() + " kassiert das Kopfgeld!").withStyle(ChatFormatting.GOLD), null);
		} else if (ueberlebt && ueberlebensPraemie.get() && zielSpieler != null) {
			auszahlen(zielSpieler);
			Broadcast.titelAlle(server, Component.literal(zielSpieler.getName().getString() + " hat überlebt und kassiert selbst!").withStyle(ChatFormatting.GOLD), null);
		}
	}

	private void auszahlen(ServerPlayer empfaenger) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(belohnungItem.get()));
		if (item == Items.AIR) {
			item = Items.DIAMOND;
		}
		ItemStack stack = new ItemStack(item, belohnungAnzahl.get());
		if (!empfaenger.getInventory().add(stack)) {
			empfaenger.drop(stack, false);
		}
	}

	private static String hübsch(String itemId) {
		int doppelpunkt = itemId.indexOf(':');
		return doppelpunkt >= 0 ? itemId.substring(doppelpunkt + 1) : itemId;
	}
}
