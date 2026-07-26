package com.horsti.wrapped;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/** Die eingebauten Kategorien plus die Registrierungsstelle fuer fremde Mods. */
public final class Kategorien {
	private static final List<StatKategorie> REGISTRIERT = new ArrayList<>();

	private Kategorien() {
	}

	private static StatKategorie custom(String id, String titel, String beschreibung,
			Identifier stat, StatKategorie.Formatierer f) {
		return new StatKategorie(id, titel, beschreibung,
			sp -> sp.getStats().getValue(Stats.CUSTOM.get(stat)), f);
	}

	/** Zentimeter -> Bloecke (Vanilla zaehlt Bewegung in cm). */
	private static String bloecke(int cm) {
		return String.format("%,d Blöcke", cm / 100);
	}

	private static String stueck(int n) {
		return String.format("%,d", n);
	}

	/** Ticks -> Stunden und Minuten. */
	private static String zeit(int ticks) {
		int minuten = ticks / 20 / 60;
		return minuten / 60 + " h " + minuten % 60 + " min";
	}

	/** Vanilla zaehlt Schaden in Zehntel-Herzpunkten; 20 Einheiten = 1 Herz. */
	private static String herzen(int einheiten) {
		return String.format("%,.1f Herzen", einheiten / 20.0);
	}

	/** "Abgebaut" ist keine Custom-Statistik, sondern eine pro Block-Typ — also summieren. */
	private static int abgebauteBloecke(net.minecraft.server.level.ServerPlayer sp) {
		int summe = 0;
		for (Block block : BuiltInRegistries.BLOCK) {
			summe += sp.getStats().getValue(Stats.BLOCK_MINED.get(block));
		}
		return summe;
	}

	public static List<StatKategorie> alle() {
		List<StatKategorie> liste = new ArrayList<>(List.of(
			custom("wanderer", "🥾 Wanderer", "die meisten Blöcke zu Fuß",
				Stats.WALK_ONE_CM, Kategorien::bloecke),
			new StatKategorie("bergmann", "⛏️ Bergmann", "die meisten Blöcke abgebaut",
				Kategorien::abgebauteBloecke, Kategorien::stueck),
			custom("pechvogel", "☠️ Pechvogel", "die meisten Tode",
				Stats.DEATHS, Kategorien::stueck),
			custom("jaeger", "⚔️ Jäger", "die meisten Mobs erlegt",
				Stats.MOB_KILLS, Kategorien::stueck),
			custom("nachteule", "🕐 Nachteule", "die meiste Spielzeit",
				Stats.PLAY_TIME, Kategorien::zeit),
			custom("angler", "🐟 Angler", "die meisten Fische gefangen",
				Stats.FISH_CAUGHT, Kategorien::stueck),
			custom("huepfer", "🦘 Hüpfer", "die meisten Sprünge",
				Stats.JUMP, Kategorien::stueck),
			custom("prellbock", "💔 Prellbock", "der meiste eingesteckte Schaden",
				Stats.DAMAGE_TAKEN, Kategorien::herzen)
		));
		liste.addAll(REGISTRIERT);
		return liste;
	}

	/** Fuer andere Mods: eigene Kategorie beisteuern (beim Mod-Start aufrufen). */
	public static void registriere(StatKategorie kategorie) {
		REGISTRIERT.add(kategorie);
	}
}
