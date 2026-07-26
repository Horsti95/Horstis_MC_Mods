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
	private static final Identifier HERZEN_ID = Attribute.id("juggernaut", "herzen");

	private final ModSettings settings = new ModSettings("juggernaut", true);
	private final IntSetting herzenProGegner = settings.add(new IntSetting("herzenProGegner", "Extra-Herzen je Jaeger", 4, 1, 10));
	private final IntSetting staerkeAb = settings.add(new IntSetting("staerkeAb", "Staerke I ab X Jaegern, II ab 2X", 4, 2, 10));
	private final BoolSetting glow = settings.add(new BoolSetting("glow", "Juggernaut leuchtet", true));
	private final IntSetting killQuote = settings.add(new IntSetting("killQuote", "Kills pro Jaeger fuer Juggernaut-Sieg", 1, 1, 5));

	private final Random random = new Random();
	private boolean laeuft = false;
	private UUID juggernaut = null;
	private final Set<UUID> jaeger = new HashSet<>();
	private final Map<UUID, Integer> kills = new HashMap<>();
	private ServerBossEvent bossBar;

	@Override
	public void onInitialize() {
		new HorstiMod("juggernaut", "Juggernaut", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					beenden(HorstiServer.get(), null);
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("start")
					.then(Commands.literal("random").executes(c -> start(c.getSource().getServer(), null)))
					.then(Commands.argument("spieler", EntityArgument.player())
						.executes(c -> start(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler")))));
				root.then(Commands.literal("stop").executes(c -> {
					beenden(c.getSource().getServer(), null);
					return 1;
				}));
			})
			.registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!laeuft || !(entity instanceof ServerPlayer opfer)) {
				return;
			}
			MinecraftServer server = HorstiServer.get();
			if (opfer.getUUID().equals(juggernaut)) {
				beenden(server, "jaeger");
				return;
			}
			if (jaeger.contains(opfer.getUUID()) && source.getEntity() instanceof ServerPlayer killer
				&& killer.getUUID().equals(juggernaut)) {
				kills.merge(opfer.getUUID(), 1, Integer::sum);
				boolean alleErledigt = jaeger.stream()
					.allMatch(id -> kills.getOrDefault(id, 0) >= killQuote.get());
				if (alleErledigt) {
					beenden(server, "juggernaut");
				}
			}
		});

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private int start(MinecraftServer server, ServerPlayer gewuenscht) {
		List<ServerPlayer> spieler = server.getPlayerList().getPlayers().stream()
			.filter(p -> !p.isSpectator())
			.toList();
		if (spieler.size() < 2) {
			Broadcast.chat(server, Component.literal("[Juggernaut] Mindestens 2 Spieler noetig.").withStyle(ChatFormatting.RED));
			return 0;
		}
		ServerPlayer jug = gewuenscht != null ? gewuenscht : spieler.get(random.nextInt(spieler.size()));
		juggernaut = jug.getUUID();
		jaeger.clear();
		kills.clear();
		spieler.stream().filter(p -> !p.getUUID().equals(juggernaut)).forEach(p -> jaeger.add(p.getUUID()));
		laeuft = true;

		buffen(jug, jaeger.size());
		bossBar = new ServerBossEvent(UUID.randomUUID(), Component.literal("Juggernaut"),
			BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
		spieler.forEach(bossBar::addPlayer);
		Broadcast.titelAlle(server, Component.literal("JUGGERNAUT!").withStyle(ChatFormatting.DARK_PURPLE),
			Component.literal(jug.getName().getString() + " gegen " + jaeger.size() + " Jaeger"));
		return 1;
	}

	private void buffen(ServerPlayer jug, int gegnerzahl) {
		Attribute.addieren(jug, Attributes.MAX_HEALTH, HERZEN_ID, gegnerzahl * herzenProGegner.get() * 2.0);
		jug.setHealth(jug.getMaxHealth());
	}

	private void sekundenTick(MinecraftServer server) {
		if (!laeuft || !settings.istAktiv()) {
			return;
		}
		ServerPlayer jug = juggernaut == null ? null : server.getPlayerList().getPlayer(juggernaut);
		if (jug == null) {
			beenden(server, "jaeger");
			return;
		}
		int lebendeJaeger = (int) jaeger.stream()
			.map(id -> server.getPlayerList().getPlayer(id))
			.filter(p -> p != null && !p.isSpectator())
			.count();
		if (lebendeJaeger == 0) {
			beenden(server, "juggernaut");
			return;
		}
		// Staerke skaliert live mit der Jaegerzahl
		int stufe = Math.min(2, lebendeJaeger / Math.max(1, staerkeAb.get()));
		if (stufe > 0) {
			jug.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60, stufe - 1, true, false));
		}
		jug.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0, true, false));
		if (glow.get()) {
			jug.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
		}
		if (bossBar != null) {
			bossBar.setName(Component.literal(jug.getName().getString() + " — " + (int) jug.getHealth() + "/" + (int) jug.getMaxHealth() + " HP"));
			bossBar.setProgress(Math.max(0f, jug.getHealth() / jug.getMaxHealth()));
		}
	}

	private void beenden(MinecraftServer server, String sieger) {
		if (!laeuft || server == null) {
			return;
		}
		laeuft = false;
		ServerPlayer jug = juggernaut == null ? null : server.getPlayerList().getPlayer(juggernaut);
		if (jug != null) {
			Attribute.entfernen(jug, Attributes.MAX_HEALTH, HERZEN_ID);
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
		if (sieger != null) {
			String text = sieger.equals("juggernaut")
				? (jug != null ? jug.getName().getString() : "Der Juggernaut") + " haelt die Stellung!"
				: "Die Jaeger haben den Juggernaut gestellt!";
			Broadcast.titelAlle(server, Component.literal(text).withStyle(ChatFormatting.GOLD), null);
		}
		juggernaut = null;
		jaeger.clear();
		kills.clear();
	}
}
