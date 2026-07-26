package com.horsti.nemesis;

import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Attribute;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.JsonSpeicher;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import com.horsti.core.util.Mobs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;
import java.util.UUID;

public class NemesisMod implements ModInitializer {
	private static final String TAG = "horsti_nemesis";
	private static final Identifier HP_ID = Attribute.id("nemesis", "hp");
	private static final Identifier SCHADEN_ID = Attribute.id("nemesis", "schaden");

	private final ModSettings settings = new ModSettings("nemesis", true);
	private final IntSetting maxLevel = settings.add(new IntSetting("maxLevel", "Eskalations-Deckel", 5, 1, 10));
	private final IntSetting hpProLevel = settings.add(new IntSetting("hpProLevel", "% Bonus-HP je Level", 25, 10, 50));
	private final IntSetting schadenProLevel = settings.add(new IntSetting("schadenProLevel", "% Bonus-Schaden je Level", 20, 10, 50));
	private final IntSetting rueckkehrMin = settings.add(new IntSetting("rueckkehrMin", "Minuten bis zur Rueckkehr", 10, 1, 60));
	private final BoolSetting ansagen = settings.add(new BoolSetting("ansagen", "Server-Ansagen", true));

	private final JsonSpeicher speicher = new JsonSpeicher("nemesis");
	private JsonObject daten;
	private final Random random = new Random();

	@Override
	public void onInitialize() {
		new HorstiMod("nemesis", "Nemesis", settings)
			.extra((root, ctx) -> {
				root.then(Commands.literal("liste").executes(c -> {
					if (daten.isEmpty()) {
						c.getSource().sendSuccess(() -> Component.literal("[Nemesis] Noch keine Erzfeinde."), false);
						return 1;
					}
					for (String key : daten.keySet()) {
						JsonObject n = daten.getAsJsonObject(key);
						final String zeile = "  " + n.get("besitzerName").getAsString() + " → "
							+ n.get("name").getAsString() + " (Level " + n.get("level").getAsInt() + ", "
							+ kurz(n.get("typ").getAsString()) + ")";
						c.getSource().sendSuccess(() -> Component.literal(zeile).withStyle(ChatFormatting.GRAY), false);
					}
					return 1;
				}));
				root.then(Commands.literal("begnadige")
					.then(Commands.argument("spieler", EntityArgument.player()).executes(c -> {
						ServerPlayer sp = EntityArgument.getPlayer(c, "spieler");
						entfernen(sp.getUUID());
						c.getSource().sendSuccess(() -> Component.literal("[Nemesis] Erzfeind von "
							+ sp.getName().getString() + " begnadigt."), true);
						return 1;
					})));
			})
			.registrieren();

		daten = speicher.laden();

		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("meinnemesis").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				JsonObject n = daten.getAsJsonObject(sp.getUUID().toString());
				if (n == null) {
					sp.sendSystemMessage(Component.literal("Du hast (noch) keinen Erzfeind.").withStyle(ChatFormatting.GRAY));
					return 1;
				}
				sp.sendSystemMessage(Component.literal("Dein Erzfeind: " + n.get("name").getAsString()
					+ " — Level " + n.get("level").getAsInt() + " (" + kurz(n.get("typ").getAsString()) + ")")
					.withStyle(ChatFormatting.DARK_RED));
				return 1;
			})));

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv()) {
				return;
			}
			if (entity instanceof ServerPlayer opfer) {
				spielerGestorben(opfer, source.getEntity());
			} else if (source.getEntity() instanceof ServerPlayer sieger) {
				nemesisBesiegt(sieger, entity);
			}
		});

		Ticker.alleTicks(100, this::rueckkehrPruefen);
	}

	private void spielerGestorben(ServerPlayer opfer, Entity toeter) {
		if (!(toeter instanceof LivingEntity) || toeter instanceof Player) {
			return; // nur Mobs werden zum Erzfeind
		}
		String key = opfer.getUUID().toString();
		JsonObject n = daten.getAsJsonObject(key);
		long rueckkehr = System.currentTimeMillis() + rueckkehrMin.get() * 60_000L;

		if (n != null && n.has("lebendId")
			&& n.get("lebendId").getAsString().equals(toeter.getUUID().toString())) {
			// Der eigene Erzfeind war erfolgreich: er steigt auf.
			int level = Math.min(maxLevel.get(), n.get("level").getAsInt() + 1);
			n.addProperty("level", level);
			n.addProperty("rueckkehrAb", rueckkehr);
			n.remove("lebendId");
			toeter.discard(); // verschwindet und kehrt spaeter staerker zurueck
			if (ansagen.get()) {
				Broadcast.chat(HorstiServer.get(), Component.literal(n.get("name").getAsString()
					+ " steigt auf Level " + level + " auf!").withStyle(ChatFormatting.DARK_RED));
			}
		} else if (n == null) {
			n = new JsonObject();
			n.addProperty("typ", Mobs.typId(toeter));
			n.addProperty("name", Namen.wuerfeln(random));
			n.addProperty("level", 1);
			n.addProperty("besitzerName", opfer.getName().getString());
			n.addProperty("rueckkehrAb", rueckkehr);
			daten.add(key, n);
			if (ansagen.get()) {
				Broadcast.titel(opfer, Component.literal(n.get("name").getAsString()).withStyle(ChatFormatting.DARK_RED),
					Component.literal("hat dich getötet — und wird zurückkommen."));
			}
		} else {
			// Ein anderer Mob war schneller; der bestehende Erzfeind bleibt.
			return;
		}
		speicher.speichern(daten);
	}

	private void nemesisBesiegt(ServerPlayer sieger, Entity nemesis) {
		String key = sieger.getUUID().toString();
		JsonObject n = daten.getAsJsonObject(key);
		if (n == null || !n.has("lebendId")
			|| !n.get("lebendId").getAsString().equals(nemesis.getUUID().toString())) {
			return;
		}
		if (ansagen.get()) {
			Broadcast.chat(HorstiServer.get(), Component.literal(sieger.getName().getString()
				+ " hat " + n.get("name").getAsString() + " endgültig besiegt!").withStyle(ChatFormatting.GOLD));
		}
		sieger.giveExperiencePoints(50 * n.get("level").getAsInt());
		entfernen(sieger.getUUID());
	}

	private void entfernen(UUID spieler) {
		daten.remove(spieler.toString());
		speicher.speichern(daten);
	}

	private void rueckkehrPruefen(MinecraftServer server) {
		if (!settings.istAktiv() || daten.isEmpty()) {
			return;
		}
		long jetzt = System.currentTimeMillis();
		for (String key : daten.keySet().toArray(new String[0])) {
			JsonObject n = daten.getAsJsonObject(key);
			ServerPlayer besitzer = server.getPlayerList().getPlayer(UUID.fromString(key));
			if (besitzer == null || besitzer.isSpectator() || jetzt < n.get("rueckkehrAb").getAsLong()) {
				continue;
			}
			if (n.has("lebendId") && lebt(server, n.get("lebendId").getAsString())) {
				continue;
			}
			spawnen(besitzer, n);
		}
	}

	private boolean lebt(MinecraftServer server, String id) {
		UUID uuid = UUID.fromString(id);
		for (ServerLevel level : server.getAllLevels()) {
			Entity e = level.getEntity(uuid);
			if (e != null && e.isAlive()) {
				return true;
			}
		}
		return false;
	}

	private void spawnen(ServerPlayer besitzer, JsonObject n) {
		if (!(besitzer.level() instanceof ServerLevel level)) {
			return;
		}
		// 24–40 Bloecke entfernt, damit die Ankunft nicht ins Gesicht springt
		double winkel = random.nextDouble() * Math.PI * 2;
		double distanz = 24 + random.nextInt(17);
		BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			besitzer.blockPosition().offset((int) (Math.cos(winkel) * distanz), 0, (int) (Math.sin(winkel) * distanz)));

		if (!(Mobs.spawnen(level, n.get("typ").getAsString(), pos) instanceof Mob mob)) {
			return;
		}
		int level_ = n.get("level").getAsInt();
		mob.addTag(TAG);
		mob.setCustomName(Component.literal(n.get("name").getAsString() + " [" + level_ + "]")
			.withStyle(ChatFormatting.DARK_RED));
		mob.setCustomNameVisible(true);
		mob.setPersistenceRequired();
		Attribute.prozent(mob, Attributes.MAX_HEALTH, HP_ID, hpProLevel.get() / 100.0 * level_);
		Attribute.prozent(mob, Attributes.ATTACK_DAMAGE, SCHADEN_ID, schadenProLevel.get() / 100.0 * level_);
		mob.setHealth(mob.getMaxHealth());
		mob.setTarget(besitzer);
		// Kurzer Glow-Moment als Ankunfts-Signal
		mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, true, false));

		n.addProperty("lebendId", mob.getUUID().toString());
		speicher.speichern(daten);

		if (ansagen.get()) {
			Broadcast.titel(besitzer, Component.literal(n.get("name").getAsString()).withStyle(ChatFormatting.DARK_RED),
				Component.literal("ist zurück — Level " + level_));
		}
	}

	private static String kurz(String id) {
		int i = id.indexOf(':');
		return i >= 0 ? id.substring(i + 1) : id;
	}
}
