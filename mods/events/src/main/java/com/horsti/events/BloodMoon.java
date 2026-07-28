package com.horsti.events;

import com.horsti.core.settings.IntSetting;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Mobs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Random;

/** A marked phase: more and more aggressive spawns around the players. */
public class BloodMoon implements HorstiEvent {
	private static final List<String> HORDE = List.of(
		"minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:husk");

	private final Random random = new Random();
	private final IntSetting perWave;
	private final IntSetting durationMinutes;

	public BloodMoon(IntSetting perWave, IntSetting durationMinutes) {
		this.perWave = perWave;
		this.durationMinutes = durationMinutes;
	}

	@Override
	public String id() {
		return "bloodmoon";
	}

	@Override
	public void announce(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("The sky is turning red… a blood moon is rising.")
			.withStyle(ChatFormatting.DARK_RED));
	}

	@Override
	public void start(MinecraftServer server) {
		Broadcast.titelAlle(server, Component.literal("BLOOD MOON").withStyle(ChatFormatting.DARK_RED),
			Component.literal("Survive the next " + durationMinutes.get() + " minutes!"));
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			sp.level().playSound(null, sp.blockPosition(), SoundEvents.WITHER_SPAWN,
				SoundSource.HOSTILE, 0.6f, 0.6f);
		}
	}

	@Override
	public boolean tick(MinecraftServer server, long secondsRunning) {
		if (secondsRunning % 10 == 0) {
			for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
				if (!sp.isSpectator() && sp.level() instanceof ServerLevel level) {
					spawnWave(level, sp);
				}
			}
		}
		return secondsRunning < durationMinutes.get() * 60L;
	}

	private void spawnWave(ServerLevel level, ServerPlayer sp) {
		for (int i = 0; i < perWave.get(); i++) {
			BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				sp.blockPosition().offset(random.nextInt(33) - 16, 0, random.nextInt(33) - 16));
			if (!level.getBlockState(pos).isAir()) {
				continue;
			}
			Entity mob = Mobs.spawnen(level, HORDE.get(random.nextInt(HORDE.size())), pos);
			if (mob instanceof Mob m) {
				m.setTarget(sp);
			}
		}
	}

	@Override
	public void cleanUp(MinecraftServer server) {
		Broadcast.titelAlle(server, Component.literal("The blood moon fades.").withStyle(ChatFormatting.GOLD), null);
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (!sp.isSpectator()) {
				sp.giveExperiencePoints(100);
			}
		}
	}
}
