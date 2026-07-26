package com.horsti.core;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Haelt die laufende Server-Instanz (seit 26.x gibt es Entity#getServer nicht mehr). */
public final class HorstiServer {
	private static MinecraftServer server;

	private HorstiServer() {
	}

	static void setzen(MinecraftServer s) {
		server = s;
	}

	public static MinecraftServer get() {
		return server;
	}

	/** Die Oberwelt — MinecraftServer#overworld() gibt es in 26.x nicht mehr. */
	public static ServerLevel oberwelt(MinecraftServer server) {
		for (ServerLevel level : server.getAllLevels()) {
			if (level.dimension() == Level.OVERWORLD) {
				return level;
			}
		}
		return server.getAllLevels().iterator().next();
	}
}
