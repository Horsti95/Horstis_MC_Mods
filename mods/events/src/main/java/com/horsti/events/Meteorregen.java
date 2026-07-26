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
 * Einschlaege rund um die Spieler — reine Vanilla-Explosionen, kein neues Asset.
 * Blockschaden ist per Default aus, damit SMP-Basen heil bleiben.
 */
public class Meteorregen implements HorstiEvent {
	private final Random random = new Random();
	private final BoolSetting blockschaden;

	public Meteorregen(BoolSetting blockschaden) {
		this.blockschaden = blockschaden;
	}

	@Override
	public String id() {
		return "meteor";
	}

	@Override
	public void ankuendigen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("Der Himmel glüht — Meteoriten im Anflug! Sucht Deckung.")
			.withStyle(ChatFormatting.GOLD));
	}

	@Override
	public void starten(MinecraftServer server) {
		Broadcast.titelAlle(server, Component.literal("METEORITENREGEN").withStyle(ChatFormatting.GOLD),
			Component.literal("Bleibt nicht im Freien!"));
	}

	@Override
	public boolean tick(MinecraftServer server, long laufSekunden) {
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (sp.isSpectator() || !(sp.level() instanceof ServerLevel level)) {
				continue;
			}
			if (random.nextInt(3) != 0) {
				continue;
			}
			BlockPos ziel = level.getHeightmapPos(
				net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
				sp.blockPosition().offset(random.nextInt(41) - 20, 0, random.nextInt(41) - 20));
			level.explode(null, ziel.getX() + 0.5, ziel.getY() + 0.5, ziel.getZ() + 0.5, 2.5f,
				blockschaden.get() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE);
		}
		return laufSekunden < 60;
	}

	@Override
	public void aufraeumen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("Der Meteoritenregen ist vorüber.").withStyle(ChatFormatting.GRAY));
	}
}
