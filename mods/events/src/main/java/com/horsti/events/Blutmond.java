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

/** Eine markierte Phase: mehr und aggressivere Spawns rund um die Spieler. */
public class Blutmond implements HorstiEvent {
	private static final List<String> HORDE = List.of(
		"minecraft:zombie", "minecraft:skeleton", "minecraft:spider", "minecraft:husk");

	private final Random random = new Random();
	private final IntSetting proWelle;
	private final IntSetting dauerMin;

	public Blutmond(IntSetting proWelle, IntSetting dauerMin) {
		this.proWelle = proWelle;
		this.dauerMin = dauerMin;
	}

	@Override
	public String id() {
		return "blutmond";
	}

	@Override
	public void ankuendigen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("Der Himmel färbt sich rot… ein Blutmond zieht auf.")
			.withStyle(ChatFormatting.DARK_RED));
	}

	@Override
	public void starten(MinecraftServer server) {
		Broadcast.titelAlle(server, Component.literal("BLUTMOND").withStyle(ChatFormatting.DARK_RED),
			Component.literal("Überlebe die nächsten " + dauerMin.get() + " Minuten!"));
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			sp.level().playSound(null, sp.blockPosition(), SoundEvents.WITHER_SPAWN,
				SoundSource.HOSTILE, 0.6f, 0.6f);
		}
	}

	@Override
	public boolean tick(MinecraftServer server, long laufSekunden) {
		if (laufSekunden % 10 == 0) {
			for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
				if (!sp.isSpectator() && sp.level() instanceof ServerLevel level) {
					welleSpawnen(level, sp);
				}
			}
		}
		return laufSekunden < dauerMin.get() * 60L;
	}

	private void welleSpawnen(ServerLevel level, ServerPlayer sp) {
		for (int i = 0; i < proWelle.get(); i++) {
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
	public void aufraeumen(MinecraftServer server) {
		Broadcast.titelAlle(server, Component.literal("Der Blutmond verblasst.").withStyle(ChatFormatting.GOLD), null);
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (!sp.isSpectator()) {
				sp.giveExperiencePoints(100);
			}
		}
	}
}
