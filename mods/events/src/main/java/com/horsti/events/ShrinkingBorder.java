package com.horsti.events;

import com.horsti.core.HorstiServer;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.util.Broadcast;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.border.WorldBorder;

/**
 * Battle-royale feel for round-based play: the world border closes in.
 * We interpolate ourselves (setSize once per second) — that is independent of
 * vanilla's lerp signatures and can be cancelled cleanly.
 */
public class ShrinkingBorder implements HorstiEvent {
	private final IntSetting durationMinutes;
	private final IntSetting targetRadius;
	private double startSize = -1;

	public ShrinkingBorder(IntSetting durationMinutes, IntSetting targetRadius) {
		this.durationMinutes = durationMinutes;
		this.targetRadius = targetRadius;
	}

	@Override
	public String id() {
		return "border";
	}

	@Override
	public void announce(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("The world is closing in — one minute until it starts.")
			.withStyle(ChatFormatting.AQUA));
	}

	@Override
	public void start(MinecraftServer server) {
		startSize = border(server).getSize();
		Broadcast.titelAlle(server, Component.literal("THE BORDER IS CLOSING").withStyle(ChatFormatting.AQUA),
			Component.literal("Target: " + targetRadius.get() + " block radius in " + durationMinutes.get() + " minutes"));
	}

	@Override
	public boolean tick(MinecraftServer server, long secondsRunning) {
		long total = durationMinutes.get() * 60L;
		double target = targetRadius.get() * 2.0;
		double progress = Math.min(1.0, (double) secondsRunning / total);
		border(server).setSize(startSize + (target - startSize) * progress);
		return secondsRunning < total;
	}

	@Override
	public void cleanUp(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("The border holds still.").withStyle(ChatFormatting.GRAY));
	}

	/** Restores the world border (used by /events cancel). */
	public void reset(MinecraftServer server) {
		if (startSize > 0) {
			border(server).setSize(startSize);
			startSize = -1;
		}
	}

	private static WorldBorder border(MinecraftServer server) {
		return HorstiServer.oberwelt(server).getWorldBorder();
	}
}
