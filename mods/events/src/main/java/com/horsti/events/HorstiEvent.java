package com.horsti.events;

import net.minecraft.server.MinecraftServer;

/**
 * A world event. Deliberately without any cross-references to the other events, so each
 * module can be lifted into a standalone mod of its own (see PLAN.md, principle 6).
 */
public interface HorstiEvent {
	String id();

	/** Announcement 60 seconds before the start. */
	void announce(MinecraftServer server);

	void start(MinecraftServer server);

	/** Every second while the event runs; false = the event is over. */
	boolean tick(MinecraftServer server, long secondsRunning);

	void cleanUp(MinecraftServer server);
}
