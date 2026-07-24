package com.horsti.core;

import net.minecraft.server.MinecraftServer;

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
}
