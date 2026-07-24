package com.horsti.mobgriefing;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.WitherSkull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Semantik: Schalter on = Vanilla-Verhalten (Mob darf Bloecke veraendern),
 * off = dieser Mob-Typ laesst die Welt in Ruhe. Die Vanilla-Gamerule bleibt
 * unangetastet auf true; die Mixins verneinen nur gezielt.
 */
public class MobgriefingMod implements ModInitializer {
	private static ModSettings settings;
	private static final Map<String, BoolSetting> SCHALTER = new LinkedHashMap<>();

	@Override
	public void onInitialize() {
		settings = new ModSettings("mobgriefing", true);
		schalter("creeper", "Creeper-Explosionen zerstoeren Bloecke", false);
		schalter("enderman", "Endermen tragen Bloecke weg", false);
		schalter("ghast", "Ghast-Feuerbaelle zerstoeren Bloecke", true);
		schalter("wither", "Wither zerstoert Bloecke", true);
		schalter("villager", "Villager ernten und saeen", true);
		schalter("schaf", "Schafe fressen Gras", true);
		new HorstiMod("mobgriefing", "Mobgriefing", settings).registrieren();
	}

	private static void schalter(String key, String beschreibung, boolean defaultWert) {
		SCHALTER.put(key, settings.add(new BoolSetting(key, beschreibung, defaultWert)));
	}

	/** Zentrale Abfrage der Mixins: darf dieser Typ die Welt veraendern? */
	public static boolean erlaubt(String key) {
		if (settings == null || !settings.istAktiv()) {
			return true; // Mod aus = pures Vanilla
		}
		BoolSetting s = SCHALTER.get(key);
		return s == null || s.get();
	}

	/** Explosions-Quelle einem Schalter zuordnen; null = nicht unser Fall. */
	public static String explosionsTyp(Entity quelle) {
		if (quelle instanceof Creeper) {
			return "creeper";
		}
		if (quelle instanceof WitherSkull || quelle instanceof WitherBoss) {
			return "wither";
		}
		if (quelle instanceof Fireball) {
			return "ghast";
		}
		return null;
	}
}
