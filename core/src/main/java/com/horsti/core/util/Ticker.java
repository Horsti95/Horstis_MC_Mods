package com.horsti.core.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/** Kleiner Tick-Helfer: wiederkehrende Aufgaben und Einmal-Verzoegerungen. */
public final class Ticker {
	private record Wiederkehrend(int intervall, Consumer<MinecraftServer> aufgabe) {
	}

	private static final class Einmalig {
		int restTicks;
		final Runnable aufgabe;

		Einmalig(int restTicks, Runnable aufgabe) {
			this.restTicks = restTicks;
			this.aufgabe = aufgabe;
		}
	}

	private static final List<Wiederkehrend> WIEDERKEHREND = new ArrayList<>();
	private static final List<Einmalig> EINMALIG = new ArrayList<>();
	private static long tick = 0;
	private static boolean registriert = false;

	private Ticker() {
	}

	private static void sicherstellen() {
		if (registriert) {
			return;
		}
		registriert = true;
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			tick++;
			for (Wiederkehrend w : List.copyOf(WIEDERKEHREND)) {
				if (tick % w.intervall == 0) {
					w.aufgabe.accept(server);
				}
			}
			Iterator<Einmalig> it = EINMALIG.iterator();
			List<Runnable> faellig = new ArrayList<>();
			while (it.hasNext()) {
				Einmalig e = it.next();
				if (--e.restTicks <= 0) {
					faellig.add(e.aufgabe);
					it.remove();
				}
			}
			faellig.forEach(Runnable::run);
		});
	}

	/** Alle N Ticks (server-weit). Registrierung ist dauerhaft. */
	public static void alleTicks(int intervall, Consumer<MinecraftServer> aufgabe) {
		sicherstellen();
		WIEDERKEHREND.add(new Wiederkehrend(Math.max(1, intervall), aufgabe));
	}

	/** Einmalig nach N Ticks. */
	public static void nachTicks(int ticks, Runnable aufgabe) {
		sicherstellen();
		EINMALIG.add(new Einmalig(Math.max(1, ticks), aufgabe));
	}
}
