package com.horsti.core;

import java.util.ArrayList;
import java.util.List;

/** Statische Liste aller installierten Horsti-Mods (fuer /horsti und die Command-Registrierung). */
public final class HorstiRegistry {
	private static final List<HorstiMod> MODS = new ArrayList<>();

	private HorstiRegistry() {
	}

	public static void anmelden(HorstiMod mod) {
		MODS.add(mod);
	}

	public static List<HorstiMod> alle() {
		return List.copyOf(MODS);
	}
}
