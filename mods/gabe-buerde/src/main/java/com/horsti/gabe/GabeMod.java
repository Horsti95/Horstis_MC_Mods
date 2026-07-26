package com.horsti.gabe;

import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.JsonSpeicher;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Random;

public class GabeMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("gabe-buerde", true);
	private final IntSetting staerke = settings.add(new IntSetting("staerke", "Wirkungsgrad aller Gaben/Buerden", 1, 1, 3));
	private final EnumSetting rerollKosten = settings.add(new EnumSetting("rerollKosten", "Spieler-Reroll-Preis", "xp30", "xp30", "netherstern", "aus"));
	private final BoolSetting ansage = settings.add(new BoolSetting("ansage", "Titel-Ansage beim Join", true));

	private final JsonSpeicher speicher = new JsonSpeicher("gabe-buerde");
	private JsonObject daten;
	private final Random random = new Random();

	@Override
	public void onInitialize() {
		new HorstiMod("gabe-buerde", "Gabe & Bürde", settings)
			.onToggle(this::alleAktualisieren)
			.extra((root, ctx) -> {
				root.then(Commands.literal("reroll")
					.then(Commands.argument("spieler", EntityArgument.player()).executes(c -> {
						ServerPlayer sp = EntityArgument.getPlayer(c, "spieler");
						zuweisen(sp, true);
						c.getSource().sendSuccess(() -> Component.literal("[Gabe] Neu gewuerfelt fuer " + sp.getName().getString()
							+ ": " + beschreibung(sp)), true);
						return 1;
					})));
			})
			.registrieren();
		staerke.onChange(w -> alleAktualisieren());

		daten = speicher.laden();

		// /gabe zeig fuer alle Spieler (eigene Kombination)
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("meinegabe").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				sp.sendSystemMessage(Component.literal("Du bist: " + beschreibung(sp)).withStyle(ChatFormatting.GOLD));
				return 1;
			})));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer sp = handler.getPlayer();
			if (!settings.istAktiv()) {
				return;
			}
			if (!hatZuweisung(sp)) {
				zuweisen(sp, false);
			} else {
				anwenden(sp);
			}
			if (ansage.get()) {
				Broadcast.titel(sp, Component.literal("Du bist:").withStyle(ChatFormatting.GRAY),
					Component.literal(beschreibung(sp)).withStyle(ChatFormatting.GOLD));
			}
		});

		// Attribute nach Respawn neu setzen (neue Entity-Instanz)
		ServerPlayerEvents.AFTER_RESPAWN.register((alt, neu, lebt) -> {
			if (settings.istAktiv()) {
				anwenden(neu);
			}
		});

		// Tick-basierte Buerden (Hunger, Wasserscheu)
		Ticker.alleTicks(40, this::tick);
	}

	private String schluessel(ServerPlayer sp) {
		return sp.getUUID().toString();
	}

	private boolean hatZuweisung(ServerPlayer sp) {
		return daten.has(schluessel(sp));
	}

	private void zuweisen(ServerPlayer sp, boolean neu) {
		Eigenschaft.entferneAlle(sp);
		JsonObject eintrag = new JsonObject();
		eintrag.addProperty("gabe", Eigenschaft.GABEN[random.nextInt(Eigenschaft.GABEN.length)].id());
		eintrag.addProperty("buerde", Eigenschaft.BUERDEN[random.nextInt(Eigenschaft.BUERDEN.length)].id());
		daten.add(schluessel(sp), eintrag);
		speicher.speichern(daten);
		anwenden(sp);
		if (neu) {
			Broadcast.titel(sp, Component.literal("Neu gewuerfelt!").withStyle(ChatFormatting.GOLD),
				Component.literal(beschreibung(sp)));
		}
	}

	private Eigenschaft gabe(ServerPlayer sp) {
		JsonObject e = daten.getAsJsonObject(schluessel(sp));
		return e == null ? null : Eigenschaft.finde(Eigenschaft.GABEN, e.get("gabe").getAsString());
	}

	private Eigenschaft buerde(ServerPlayer sp) {
		JsonObject e = daten.getAsJsonObject(schluessel(sp));
		return e == null ? null : Eigenschaft.finde(Eigenschaft.BUERDEN, e.get("buerde").getAsString());
	}

	private String beschreibung(ServerPlayer sp) {
		Eigenschaft g = gabe(sp);
		Eigenschaft b = buerde(sp);
		if (g == null || b == null) {
			return "noch nichts";
		}
		return g.name() + ", aber " + b.name();
	}

	private void anwenden(ServerPlayer sp) {
		Eigenschaft.entferneAlle(sp);
		if (!settings.istAktiv()) {
			return;
		}
		Eigenschaft g = gabe(sp);
		Eigenschaft b = buerde(sp);
		if (g != null) {
			g.anwenden(sp, staerke.get());
		}
		if (b != null) {
			b.anwenden(sp, staerke.get());
		}
	}

	private void alleAktualisieren() {
		MinecraftServer server = com.horsti.core.HorstiServer.get();
		if (server == null) {
			return;
		}
		server.getPlayerList().getPlayers().forEach(this::anwenden);
	}

	private void tick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			Eigenschaft b = buerde(sp);
			if (b == null) {
				continue;
			}
			switch (b.id()) {
				case "hungrig" -> sp.getFoodData().addExhaustion(0.5f * staerke.get());
				case "wasserscheu" -> {
					if (sp.isInWater()) {
						sp.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, staerke.get() - 1, true, false));
					}
				}
				default -> {
				}
			}
		}
	}
}
