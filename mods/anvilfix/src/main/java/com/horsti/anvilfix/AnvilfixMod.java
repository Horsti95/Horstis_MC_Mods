package com.horsti.anvilfix;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import net.fabricmc.api.ModInitializer;

/**
 * Background: vanilla clears the anvil result above 40 levels ("Too Expensive").
 * The mixin lifts that threshold and caps the cost at maxCost instead (<= 39,
 * so unmodded clients are still allowed to take the result).
 */
public class AnvilfixMod implements ModInitializer {
	private static ModSettings settings;
	private static IntSetting maxCost;
	private static EnumSetting priorWork;

	@Override
	public void onInitialize() {
		settings = new ModSettings("anvilfix", true);
		maxCost = settings.add(new IntSetting("maxCost", "cost cap in levels (39 = never too expensive)", 39, 1, 39));
		priorWork = settings.add(new EnumSetting("priorWork", "penalty growth per repair", "linear", "vanilla", "linear", "frozen"));
		new HorstiMod("anvilfix", "Anvilfix", settings).registrieren();
	}

	public static boolean isEnabled() {
		return settings != null && settings.istAktiv();
	}

	/** Replaces the 40-level threshold so the result is never cleared. */
	public static int threshold(int vanillaValue) {
		return isEnabled() ? Integer.MAX_VALUE : vanillaValue;
	}

	public static int capCost(int cost) {
		return isEnabled() ? Math.min(cost, maxCost.get()) : cost;
	}

	/** null = keep vanilla behaviour. */
	public static Integer priorWorkResult(int oldValue) {
		if (!isEnabled()) {
			return null;
		}
		return switch (priorWork.get()) {
			case "frozen" -> oldValue;      // penalty stops growing
			case "linear" -> oldValue + 1;  // linear instead of vanilla's *2+1
			default -> null;
		};
	}
}
