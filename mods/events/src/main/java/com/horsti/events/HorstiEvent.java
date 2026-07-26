package com.horsti.events;

import net.minecraft.server.MinecraftServer;

/**
 * Ein Weltereignis. Bewusst ohne Querbezuege zu den anderen Events — so laesst
 * sich jedes Modul in einen eigenen Standalone-Mod heben (siehe PLAN.md, Prinzip 6).
 */
public interface HorstiEvent {
	String id();

	/** Ankuendigung 60 Sekunden vor dem Start. */
	void ankuendigen(MinecraftServer server);

	void starten(MinecraftServer server);

	/** Jede Sekunde waehrend das Event laeuft; false = Event ist zu Ende. */
	boolean tick(MinecraftServer server, long laufSekunden);

	void aufraeumen(MinecraftServer server);
}
