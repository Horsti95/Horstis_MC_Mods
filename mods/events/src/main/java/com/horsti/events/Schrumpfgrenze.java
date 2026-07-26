package com.horsti.events;

import com.horsti.core.HorstiServer;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.util.Broadcast;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.border.WorldBorder;

/**
 * Battle-Royale-Gefuehl fuer Rundenspiele: die Weltgrenze zieht sich zusammen.
 * Die Interpolation rechnen wir selbst (setSize pro Sekunde) — das ist von
 * Vanillas lerp-Signaturen unabhaengig und laesst sich sauber abbrechen.
 */
public class Schrumpfgrenze implements HorstiEvent {
	private final IntSetting dauerMin;
	private final IntSetting zielRadius;
	private double startGroesse = -1;

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
		startGroesse = grenze(server).getSize();
		Broadcast.titelAlle(server, Component.literal("DIE GRENZE SCHRUMPFT").withStyle(ChatFormatting.AQUA),
			Component.literal("Ziel: " + zielRadius.get() + " Blöcke Radius in " + dauerMin.get() + " Minuten"));
	}

	@Override
	public boolean tick(MinecraftServer server, long laufSekunden) {
		long gesamt = dauerMin.get() * 60L;
		double ziel = zielRadius.get() * 2.0;
		double anteil = Math.min(1.0, (double) laufSekunden / gesamt);
		grenze(server).setSize(startGroesse + (ziel - startGroesse) * anteil);
		return laufSekunden < gesamt;
	}

	@Override
	public void aufraeumen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("Die Grenze steht still.").withStyle(ChatFormatting.GRAY));
	}

	/** Stellt die Weltgrenze wieder her (bei /events abbrechen). */
	public void zuruecksetzen(MinecraftServer server) {
		if (startGroesse > 0) {
			grenze(server).setSize(startGroesse);
			startGroesse = -1;
		}
	}

	private static WorldBorder grenze(MinecraftServer server) {
		return HorstiServer.oberwelt(server).getWorldBorder();
	}
}
