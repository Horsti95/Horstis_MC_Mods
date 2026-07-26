package com.horsti.events;

import com.horsti.core.settings.IntSetting;
import com.horsti.core.util.Broadcast;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.border.WorldBorder;

/** Battle-Royale-Gefuehl fuer Rundenspiele: die Weltgrenze zieht sich zusammen. */
public class Schrumpfgrenze implements HorstiEvent {
	private final IntSetting dauerMin;
	private final IntSetting zielRadius;
	private double alterRadius = -1;

	public Schrumpfgrenze(IntSetting dauerMin, IntSetting zielRadius) {
		this.dauerMin = dauerMin;
		this.zielRadius = zielRadius;
	}

	@Override
	public String id() {
		return "grenze";
	}

	@Override
	public void ankuendigen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("Die Welt zieht sich zusammen — in einer Minute geht es los.")
			.withStyle(ChatFormatting.AQUA));
	}

	@Override
	public void starten(MinecraftServer server) {
		WorldBorder grenze = server.overworld().getWorldBorder();
		alterRadius = grenze.getSize();
		grenze.lerpSizeBetween(grenze.getSize(), zielRadius.get() * 2.0, dauerMin.get() * 60_000L);
		Broadcast.titelAlle(server, Component.literal("DIE GRENZE SCHRUMPFT").withStyle(ChatFormatting.AQUA),
			Component.literal("Ziel: " + zielRadius.get() + " Blöcke Radius in " + dauerMin.get() + " Minuten"));
	}

	@Override
	public boolean tick(MinecraftServer server, long laufSekunden) {
		return laufSekunden < dauerMin.get() * 60L;
	}

	@Override
	public void aufraeumen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("Die Grenze steht still.").withStyle(ChatFormatting.GRAY));
	}

	/** Stellt die Weltgrenze wieder her (bei /events abbrechen). */
	public void zuruecksetzen(MinecraftServer server) {
		if (alterRadius > 0) {
			server.overworld().getWorldBorder().setSize(alterRadius);
			alterRadius = -1;
		}
	}
}
