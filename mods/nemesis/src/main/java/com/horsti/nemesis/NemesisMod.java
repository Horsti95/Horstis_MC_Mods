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
import com.horsti.core.util.Mobs;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
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
	private static final Identifier HEALTH_ID = Attribute.id("nemesis", "health");
	private static final Identifier DAMAGE_ID = Attribute.id("nemesis", "damage");

	private final ModSettings settings = new ModSettings("nemesis", true);
	private final IntSetting maxLevel = settings.add(new IntSetting("maxLevel", "escalation cap", 5, 1, 10));
	private final IntSetting hpPerLevel = settings.add(new IntSetting("hpPerLevel", "% bonus health per level", 25, 10, 50));
	private final IntSetting damagePerLevel = settings.add(new IntSetting("damagePerLevel", "% bonus damage per level", 20, 10, 50));
	private final IntSetting returnMinutes = settings.add(new IntSetting("returnMinutes", "minutes until it returns", 10, 1, 60));
	private final BoolSetting announce = settings.add(new BoolSetting("announce", "server announcements", true));

	private final JsonSpeicher storage = new JsonSpeicher("nemesis");
	private JsonObject data;
	private final Random random = new Random();

	@Override
	public void onInitialize() {
		new HorstiMod("nemesis", "Nemesis", settings)
			.extra((root, ctx) -> {
				root.then(Commands.literal("list").executes(c -> {
					if (data.isEmpty()) {
						c.getSource().sendSuccess(() -> Component.literal("[Nemesis] No arch-enemies yet."), false);
						return 1;
					}
					for (String key : data.keySet()) {
						JsonObject n = data.getAsJsonObject(key);
						final String line = "  " + n.get("ownerName").getAsString() + " → "
							+ n.get("name").getAsString() + " (level " + n.get("level").getAsInt() + ", "
							+ shortId(n.get("type").getAsString()) + ")";
						c.getSource().sendSuccess(() -> Component.literal(line).withStyle(ChatFormatting.GRAY), false);
					}
					return 1;
				}));
				root.then(Commands.literal("pardon")
					.then(Commands.argument("player", EntityArgument.player()).executes(c -> {
						ServerPlayer player = EntityArgument.getPlayer(c, "player");
						remove(player.getUUID());
						c.getSource().sendSuccess(() -> Component.literal("[Nemesis] Pardoned the arch-enemy of "
							+ player.getName().getString() + "."), true);
						return 1;
					})));
			})
			.registrieren();

		data = storage.laden();

		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("mynemesis").executes(c -> {
				ServerPlayer player = c.getSource().getPlayerOrException();
				JsonObject n = data.getAsJsonObject(player.getUUID().toString());
				if (n == null) {
					player.sendSystemMessage(Component.literal("You have no arch-enemy (yet).").withStyle(ChatFormatting.GRAY));
					return 1;
				}
				player.sendSystemMessage(Component.literal("Your arch-enemy: " + n.get("name").getAsString()
					+ " — level " + n.get("level").getAsInt() + " (" + shortId(n.get("type").getAsString()) + ")")
					.withStyle(ChatFormatting.DARK_RED));
				return 1;
			})));

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv()) {
				return;
			}
			if (entity instanceof ServerPlayer victim) {
				playerDied(victim, source.getEntity());
			} else if (source.getEntity() instanceof ServerPlayer victor) {
				nemesisDefeated(victor, entity);
			}
		});

		Ticker.alleTicks(100, this::checkReturns);
	}

	private void playerDied(ServerPlayer victim, Entity killer) {
		if (!(killer instanceof LivingEntity) || killer instanceof Player) {
			return; // only mobs become arch-enemies
		}
		String key = victim.getUUID().toString();
		JsonObject n = data.getAsJsonObject(key);
		long returnAt = System.currentTimeMillis() + returnMinutes.get() * 60_000L;

		if (n != null && n.has("liveId")
			&& n.get("liveId").getAsString().equals(killer.getUUID().toString())) {
			// Their own arch-enemy succeeded: it ranks up.
			int level = Math.min(maxLevel.get(), n.get("level").getAsInt() + 1);
			n.addProperty("level", level);
			n.addProperty("returnAt", returnAt);
			n.remove("liveId");
			killer.discard(); // vanishes and comes back stronger later
			if (announce.get()) {
				Broadcast.chat(HorstiServer.get(), Component.literal(n.get("name").getAsString()
					+ " rises to level " + level + "!").withStyle(ChatFormatting.DARK_RED));
			}
		} else if (n == null) {
			n = new JsonObject();
			n.addProperty("type", Mobs.typId(killer));
			n.addProperty("name", Namen.roll(random));
			n.addProperty("level", 1);
			n.addProperty("ownerName", victim.getName().getString());
			n.addProperty("returnAt", returnAt);
			data.add(key, n);
			if (announce.get()) {
				Broadcast.titel(victim, Component.literal(n.get("name").getAsString()).withStyle(ChatFormatting.DARK_RED),
					Component.literal("killed you — and will come back."));
			}
		} else {
			// Another mob got there first; the existing arch-enemy stays.
			return;
		}
		storage.speichern(data);
	}

	private void nemesisDefeated(ServerPlayer victor, Entity nemesis) {
		String key = victor.getUUID().toString();
		JsonObject n = data.getAsJsonObject(key);
		if (n == null || !n.has("liveId")
			|| !n.get("liveId").getAsString().equals(nemesis.getUUID().toString())) {
			return;
		}
		if (announce.get()) {
			Broadcast.chat(HorstiServer.get(), Component.literal(victor.getName().getString()
				+ " has defeated " + n.get("name").getAsString() + " for good!").withStyle(ChatFormatting.GOLD));
		}
		victor.giveExperiencePoints(50 * n.get("level").getAsInt());
		remove(victor.getUUID());
	}

	private void remove(UUID player) {
		data.remove(player.toString());
		storage.speichern(data);
	}

	private void checkReturns(MinecraftServer server) {
		if (!settings.istAktiv() || data.isEmpty()) {
			return;
		}
		long now = System.currentTimeMillis();
		for (String key : data.keySet().toArray(new String[0])) {
			JsonObject n = data.getAsJsonObject(key);
			ServerPlayer owner = server.getPlayerList().getPlayer(UUID.fromString(key));
			if (owner == null || owner.isSpectator() || now < n.get("returnAt").getAsLong()) {
				continue;
			}
			if (n.has("liveId") && isAlive(server, n.get("liveId").getAsString())) {
				continue;
			}
			spawn(owner, n);
		}
	}

	private boolean isAlive(MinecraftServer server, String id) {
		UUID uuid = UUID.fromString(id);
		for (ServerLevel level : server.getAllLevels()) {
			Entity entity = level.getEntity(uuid);
			if (entity != null && entity.isAlive()) {
				return true;
			}
		}
		return false;
	}

	private void spawn(ServerPlayer owner, JsonObject n) {
		if (!(owner.level() instanceof ServerLevel level)) {
			return;
		}
		// 24–40 blocks away, so the arrival does not jump into their face
		double angle = random.nextDouble() * Math.PI * 2;
		double distance = 24 + random.nextInt(17);
		BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
			owner.blockPosition().offset((int) (Math.cos(angle) * distance), 0, (int) (Math.sin(angle) * distance)));

		if (!(Mobs.spawnen(level, n.get("type").getAsString(), pos) instanceof Mob mob)) {
			return;
		}
		int level_ = n.get("level").getAsInt();
		mob.addTag(TAG);
		mob.setCustomName(Component.literal(n.get("name").getAsString() + " [" + level_ + "]")
			.withStyle(ChatFormatting.DARK_RED));
		mob.setCustomNameVisible(true);
		mob.setPersistenceRequired();
		Attribute.prozent(mob, Attributes.MAX_HEALTH, HEALTH_ID, hpPerLevel.get() / 100.0 * level_);
		Attribute.prozent(mob, Attributes.ATTACK_DAMAGE, DAMAGE_ID, damagePerLevel.get() / 100.0 * level_);
		mob.setHealth(mob.getMaxHealth());
		mob.setTarget(owner);
		// A brief glow as an arrival signal
		mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, true, false));

		n.addProperty("liveId", mob.getUUID().toString());
		storage.speichern(data);

		if (announce.get()) {
			Broadcast.titel(owner, Component.literal(n.get("name").getAsString()).withStyle(ChatFormatting.DARK_RED),
				Component.literal("is back — level " + level_));
		}
	}

	private static String shortId(String id) {
		int colon = id.indexOf(':');
		return colon >= 0 ? id.substring(colon + 1) : id;
	}
}
