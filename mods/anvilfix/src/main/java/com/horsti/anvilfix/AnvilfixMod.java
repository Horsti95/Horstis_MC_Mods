package com.horsti.anvilfix;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import net.fabricmc.api.ModInitializer;

/**
 * Hintergrund: Vanilla leert ab 40 Leveln das Amboss-Ergebnis ("Zu teuer!").
 * Der Mixin hebt diese Schwelle auf und deckelt stattdessen die Kosten auf
 * maxKosten (<=39, damit auch Vanilla-Clients das Ergebnis nehmen duerfen).
 */
public class AnvilfixMod implements ModInitializer {
	private static ModSettings settings;
	private static IntSetting maxKosten;
	private static EnumSetting priorWork;

	@Override
	public void onInitialize() {
		settings = new ModSettings("anvilfix", true);
		maxKosten = settings.add(new IntSetting("maxKosten", "Kostendeckel in Leveln (39 = nie 'Zu teuer')", 39, 1, 39));
		priorWork = settings.add(new EnumSetting("priorWork", "Verdopplungs-Strafe je Reparatur", "halb", "vanilla", "halb", "aus"));
		new HorstiMod("anvilfix", "Anvilfix", settings).registrieren();
	}

	public static boolean istAktiv() {
		return settings != null && settings.istAktiv();
	}

	/** Ersetzt die 40er-Schwelle: praktisch nie "Zu teuer". */
	public static int schwelle(int vanillaWert) {
		return istAktiv() ? Integer.MAX_VALUE : vanillaWert;
	}

	public static int kostenDeckel(int kosten) {
		return istAktiv() ? Math.min(kosten, maxKosten.get()) : kosten;
	}

	/** null = Vanilla-Verhalten beibehalten. */
	public static Integer priorWorkErgebnis(int alterWert) {
		if (!istAktiv()) {
			return null;
		}
		return switch (priorWork.get()) {
			case "aus" -> alterWert;            // Strafe waechst nicht mehr
			case "halb" -> alterWert + 1;       // linear statt Verdopplung (vanilla: *2+1)
			default -> null;
		};
	}
}
