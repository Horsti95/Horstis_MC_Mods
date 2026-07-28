package com.horsti.events;

import com.horsti.core.settings.BoolSetting;
import com.horsti.core.util.Broadcast;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Impacts around the players — plain vanilla explosions, no new asset.
 * Block damage is off by default so SMP bases stay intact.
 */
public class MeteorShower implements HorstiEvent {
	private final Random random = new Random();
	private final BoolSetting blockDamage;

	public MeteorShower(BoolSetting blockDamage) {
		this.blockDamage = blockDamage;
	}

	@Override
	public String id() {
		return "meteor";
	}

	@Override
	public void announce(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("The sky is glowing — meteors incoming! Find cover.")
			.withStyle(ChatFormatting.GOLD));
	}

	@Override
	public void start(MinecraftServer server) {
		Broadcast.titelAlle(server, Component.literal("METEOR SHOWER").withStyle(ChatFormatting.GOLD),
			Component.literal("Do not stay in the open!"));
	}

	@Override
	public boolean tick(MinecraftServer server, long secondsRunning) {
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (sp.isSpectator() || !(sp.level() instanceof ServerLevel level)) {
				continue;
			}
			if (random.nextInt(3) != 0) {
				continue;
			}
			BlockPos target = level.getHeightmapPos(
				net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
				sp.blockPosition().offset(random.nextInt(41) - 20, 0, random.nextInt(41) - 20));
			level.explode(null, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 2.5f,
				blockDamage.get() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
		}
		return secondsRunning < 60;
	}

	@Override
	public void cleanUp(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("The meteor shower is over.").withStyle(ChatFormatting.GRAY));
	}
}
