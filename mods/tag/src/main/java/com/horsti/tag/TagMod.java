package com.horsti.tag;

import com.horsti.core.HorstiMod;
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
	private final IntSetting rundenMin = settings.add(new IntSetting("rundenMin", "Rundenlaenge in Minuten", 10, 1, 60));
	private final IntSetting esSpeed = settings.add(new IntSetting("esSpeed", "Speed-Stufe fuer 'Es' (0 = aus)", 1, 0, 2));
	private final BoolSetting esGlow = settings.add(new BoolSetting("esGlow", "'Es' leuchtet", true));
	private final IntSetting schutzSek = settings.add(new IntSetting("schutzSek", "Rueckgabe-Schutz nach Uebergabe", 5, 0, 30));
	private final BoolSetting schaden = settings.add(new BoolSetting("schaden", "Schlag macht echten Schaden", false));

	private final Random random = new Random();
	private boolean laeuft = false;
	private UUID es = null;
	private long endTick = 0;
	private long schutzBisTick = 0;
	private long jetztTick = 0;
	private final Set<UUID> teilnehmer = new HashSet<>();
	private final Map<UUID, Integer> punkte = new HashMap<>();
	private ServerBossEvent bossBar;

	@Override
	public void onInitialize() {
		new HorstiMod("tag", "Tag", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					// laufende Runde sauber beenden
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("start").executes(c -> start(c.getSource().getServer())));
				root.then(Commands.literal("stop").executes(c -> {
					beenden(c.getSource().getServer(), true);
					return 1;
				}));
			})
			.registrieren();

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
			if (!laeuft || !(player instanceof ServerPlayer angreifer) || !(entity instanceof ServerPlayer ziel)) {
				return InteractionResult.PASS;
			}
			if (!angreifer.getUUID().equals(es) || !teilnehmer.contains(ziel.getUUID()) || ziel.isSpectator()) {
				return InteractionResult.PASS;
			}
			if (jetztTick < schutzBisTick) {
				Broadcast.actionbar(angreifer, Component.literal("Noch geschützt!").withStyle(ChatFormatting.GRAY));
				return InteractionResult.FAIL;
			}
			uebergeben(angreifer, ziel);
			return schaden.get() ? InteractionResult.PASS : InteractionResult.FAIL;
		});

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private int start(MinecraftServer server) {
		List<ServerPlayer> spieler = server.getPlayerList().getPlayers().stream()
			.filter(p -> !p.isSpectator())
			.toList();
		if (spieler.size() < 2) {
			Broadcast.chat(server, Component.literal("[Tag] Mindestens 2 Spieler noetig.").withStyle(ChatFormatting.RED));
			return 0;
		}
		teilnehmer.clear();
		punkte.clear();
		spieler.forEach(p -> {
			teilnehmer.add(p.getUUID());
			punkte.put(p.getUUID(), 0);
		});
		laeuft = true;
		endTick = jetztTick + rundenMin.get() * 1200L;
		ServerPlayer erster = spieler.get(random.nextInt(spieler.size()));
		setzeEs(erster);
		bossBar = new ServerBossEvent(Component.literal("Tag"), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
		spieler.forEach(bossBar::addPlayer);
		Broadcast.titelAlle(server, Component.literal("FANGEN!").withStyle(ChatFormatting.YELLOW),
			Component.literal(erster.getName().getString() + " ist Es — lauft!"));
		return 1;
	}

	private void setzeEs(ServerPlayer neu) {
		es = neu.getUUID();
		schutzBisTick = jetztTick + schutzSek.get() * 20L;
		Broadcast.titel(neu, Component.literal("DU BIST ES!").withStyle(ChatFormatting.RED), null);
	}

	private void uebergeben(ServerPlayer alt, ServerPlayer neu) {
		effekteEntfernen(alt);
		setzeEs(neu);
		Broadcast.chat(alt.getServer(), Component.literal(neu.getName().getString() + " ist jetzt Es!").withStyle(ChatFormatting.YELLOW));
	}

	private void effekteEntfernen(ServerPlayer sp) {
		sp.removeEffect(MobEffects.SPEED);
		sp.removeEffect(MobEffects.GLOWING);
	}

	private void sekundenTick(MinecraftServer server) {
		jetztTick += 20;
		if (!laeuft || !settings.istAktiv()) {
			return;
		}
		ServerPlayer esSpieler = es == null ? null : server.getPlayerList().getPlayer(es);
		if (esSpieler == null || esSpieler.isSpectator()) {
			// Es ist offline/raus -> neues Es ziehen
			List<ServerPlayer> kandidaten = server.getPlayerList().getPlayers().stream()
				.filter(p -> teilnehmer.contains(p.getUUID()) && !p.isSpectator())
				.toList();
			if (kandidaten.isEmpty()) {
				beenden(server, false);
				return;
			}
			setzeEs(kandidaten.get(random.nextInt(kandidaten.size())));
			esSpieler = server.getPlayerList().getPlayer(es);
		}

		// Effekte auffrischen (kurz halten, damit sie nach Rundenende auslaufen)
		if (esSpeed.get() > 0) {
			esSpieler.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, esSpeed.get() - 1, true, false));
		}
		if (esGlow.get()) {
			esSpieler.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false));
		}

		// Punkte: jede Sekunde nicht-Es zaehlt
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (teilnehmer.contains(sp.getUUID()) && !sp.getUUID().equals(es) && !sp.isSpectator()) {
				punkte.merge(sp.getUUID(), 1, Integer::sum);
			}
		}

		long restSek = Math.max(0, (endTick - jetztTick) / 20);
		if (bossBar != null) {
			bossBar.setName(Component.literal("Tag — noch " + restSek / 60 + ":" + String.format("%02d", restSek % 60)));
			bossBar.setProgress(Math.max(0f, (float) restSek / (rundenMin.get() * 60f)));
		}
		if (jetztTick >= endTick) {
			beenden(server, true);
		}
	}

	private void beenden(MinecraftServer server, boolean mitWertung) {
		if (!laeuft) {
			return;
		}
		laeuft = false;
		ServerPlayer esSpieler = es == null ? null : server.getPlayerList().getPlayer(es);
		if (esSpieler != null) {
			effekteEntfernen(esSpieler);
		}
		if (bossBar != null) {
			bossBar.removeAllPlayers();
			bossBar = null;
		}
		if (mitWertung) {
			String verlierer = esSpieler != null ? esSpieler.getName().getString() : "?";
			Broadcast.titelAlle(server, Component.literal("Runde vorbei!").withStyle(ChatFormatting.GOLD),
				Component.literal(verlierer + " ist Es geblieben und verliert."));
			List<Map.Entry<UUID, Integer>> beste = punkte.entrySet().stream()
				.sorted(Comparator.<Map.Entry<UUID, Integer>>comparingInt(Map.Entry::getValue).reversed())
				.limit(3)
				.toList();
			StringBuilder sb = new StringBuilder("[Tag] Beste Läufer: ");
			for (int i = 0; i < beste.size(); i++) {
				ServerPlayer p = server.getPlayerList().getPlayer(beste.get(i).getKey());
				String name = p != null ? p.getName().getString() : "?";
				sb.append(i + 1).append(". ").append(name).append(" (").append(beste.get(i).getValue()).append("s)  ");
			}
			Broadcast.chat(server, Component.literal(sb.toString()).withStyle(ChatFormatting.YELLOW));
		}
		es = null;
		teilnehmer.clear();
	}
}
