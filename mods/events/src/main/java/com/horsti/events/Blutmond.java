package com.horsti.events;

import com.horsti.core.settings.IntSetting;
import com.horsti.core.util.Broadcast;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Random;

/** Eine markierte Nacht: mehr und aggressivere Spawns rund um die Spieler. */
public class Blutmond implements HorstiEvent {
	private static final List<EntityType<?>> HORDE = List.of(
		EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.HUSK);

	private final Random random = new Random();
	private final IntSetting proWelle;

	public Blutmond(IntSetting proWelle) {
		this.proWelle = proWelle;
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
			Component.literal("Überlebe bis zum Morgengrauen!"));
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			sp.level().playSound(null, sp.blockPosition(), SoundEvents.WITHER_SPAWN,
				SoundSource.HOSTILE, 0.6f, 0.6f);
		}
	}

	@Override
	public boolean tick(MinecraftServer server, long laufSekunden) {
		if (laufSekunden % 10 == 0) {
			for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
				if (sp.isSpectator() || !(sp.level() instanceof ServerLevel level)) {
					continue;
				}
				welleSpawnen(level, sp);
			}
		}
		// Ende bei Tagesanbruch (Vanilla-Tagzeit 0–12000 = Tag)
		if (server.overworld().getDayTime() % 24000L < 12000L && laufSekunden > 30) {
			return false;
		}
		return laufSekunden < 600; // Sicherheitsnetz: max. 10 Minuten
	}

	private void welleSpawnen(ServerLevel level, ServerPlayer sp) {
		for (int i = 0; i < proWelle.get(); i++) {
			BlockPos pos = sp.blockPosition().offset(
				random.nextInt(33) - 16, 0, random.nextInt(33) - 16);
			pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
			if (!level.getBlockState(pos).isAir()) {
				continue;
			}
			EntityType<?> typ = HORDE.get(random.nextInt(HORDE.size()));
			if (typ.spawn(level, pos, EntitySpawnReason.EVENT) instanceof Mob mob) {
				mob.setTarget(sp);
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
