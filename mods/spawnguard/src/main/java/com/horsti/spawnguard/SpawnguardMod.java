package com.horsti.spawnguard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.JsonSpeicher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Marks spheres in which hostile mobs no longer spawn naturally.
 *
 * <p>Everything else stays vanilla on purpose: mobs already inside stay, mobs can walk in,
 * and you can still fight them. Only the spawn attempt itself is refused — see
 * {@link com.horsti.spawnguard.mixin.MobSpawnRulesMixin}.
 */
public class SpawnguardMod implements ModInitializer {
	private static SpawnguardMod instance;

	private final ModSettings settings = new ModSettings("spawnguard", true);
	private final IntSetting maxRadius = settings.add(new IntSetting("maxRadius", "maximum radius per zone", 32, 8, 128));
	private final IntSetting maxZones = settings.add(new IntSetting("maxZones", "maximum number of zones", 10, 1, 50));
	private final BoolSetting spawner = settings.add(new BoolSetting("spawner", "also suppress monster spawners", false));

	private final JsonSpeicher storage = new JsonSpeicher("spawnguard");
	private final List<Zone> zones = new ArrayList<>();

	@Override
	public void onInitialize() {
		instance = this;
		load();

		new HorstiMod("spawnguard", "Spawnguard", settings)
			.extra((root, ctx) -> {
				root.then(Commands.literal("here")
					.then(Commands.argument("radius", IntegerArgumentType.integer(1, 128))
						.executes(c -> create(c.getSource().getPlayerOrException(),
							IntegerArgumentType.getInteger(c, "radius")))));
				root.then(Commands.literal("list").executes(c -> {
					if (zones.isEmpty()) {
						c.getSource().sendSuccess(() -> Component.literal("[Spawnguard] No zones yet — try /spawnguard here 24")
							.withStyle(ChatFormatting.GRAY), false);
						return 1;
					}
					for (int i = 0; i < zones.size(); i++) {
						Zone z = zones.get(i);
						final String line = "  " + (i + 1) + ". " + z.x() + " " + z.y() + " " + z.z()
							+ "  r=" + z.radius() + "  " + z.shortDimension() + "  (" + z.owner() + ")";
						c.getSource().sendSuccess(() -> Component.literal(line).withStyle(ChatFormatting.GRAY), false);
					}
					return 1;
				}));
				root.then(Commands.literal("remove")
					.then(Commands.argument("nr", IntegerArgumentType.integer(1, 50))
						.executes(c -> remove(c.getSource(), IntegerArgumentType.getInteger(c, "nr")))));
			})
			.registrieren();
	}

	private int create(ServerPlayer player, int radius) {
		if (zones.size() >= maxZones.get()) {
			player.sendSystemMessage(Component.literal("[Spawnguard] Zone limit reached (" + maxZones.get()
				+ "). Remove one first or raise maxZones.").withStyle(ChatFormatting.RED));
			return 0;
		}
		int capped = Math.min(radius, maxRadius.get());
		BlockPos pos = player.blockPosition();
		zones.add(new Zone(dimensionOf(player), pos.getX(), pos.getY(), pos.getZ(), capped, player.getName().getString()));
		save();
		player.sendSystemMessage(Component.literal("[Spawnguard] Zone " + zones.size() + " created — radius "
			+ capped + (capped < radius ? " (capped by maxRadius)" : "")).withStyle(ChatFormatting.GREEN));
		return 1;
	}

	private int remove(net.minecraft.commands.CommandSourceStack src, int nr) {
		if (nr > zones.size()) {
			src.sendFailure(Component.literal("[Spawnguard] There is no zone " + nr + "."));
			return 0;
		}
		Zone gone = zones.remove(nr - 1);
		save();
		src.sendSuccess(() -> Component.literal("[Spawnguard] Zone at " + gone.x() + " " + gone.y() + " "
			+ gone.z() + " removed.").withStyle(ChatFormatting.GRAY), true);
		return 1;
	}

	/**
	 * The dimension id as a plain string, e.g. "minecraft:the_nether".
	 *
	 * <p>{@code ResourceKey#location()} is gone in 26.x and its replacement is not confirmed,
	 * so we read the id back out of {@code toString()}, which renders as
	 * "ResourceKey[minecraft:dimension / minecraft:the_nether]". Ugly, but it cannot break.
	 */
	private static String dimensionOf(Entity entity) {
		String raw = entity.level().dimension().toString();
		int start = raw.lastIndexOf(" / ");
		int end = raw.lastIndexOf(']');
		return start >= 0 && end > start ? raw.substring(start + 3, end) : raw;
	}

	private void load() {
		zones.clear();
		JsonObject data = storage.laden();
		if (!data.has("zones")) {
			return;
		}
		for (var element : data.getAsJsonArray("zones")) {
			try {
				zones.add(Zone.fromJson(element.getAsJsonObject()));
			} catch (RuntimeException ignored) {
				// A broken entry costs one zone, not the whole file.
			}
		}
	}

	private void save() {
		JsonArray array = new JsonArray();
		for (Zone z : zones) {
			array.add(z.toJson());
		}
		JsonObject data = new JsonObject();
		data.add("zones", array);
		storage.speichern(data);
	}

	/**
	 * Asked by the mixin before a mob is allowed to spawn.
	 * Kept static so the mixin stays a two-liner.
	 */
	public static boolean blocks(Entity mob, EntitySpawnReason reason) {
		SpawnguardMod self = instance;
		if (self == null || !self.settings.istAktiv() || self.zones.isEmpty()) {
			return false;
		}
		if (mob.getType().getCategory() != MobCategory.MONSTER) {
			return false; // animals, ambient mobs and villagers stay untouched
		}
		if (reason != EntitySpawnReason.NATURAL && !(reason == EntitySpawnReason.SPAWNER && self.spawner.get())) {
			return false;
		}
		String dimension = dimensionOf(mob);
		for (Zone zone : self.zones) {
			if (zone.contains(dimension, mob.getX(), mob.getY(), mob.getZ())) {
				return true;
			}
		}
		return false;
	}
}
