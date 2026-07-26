package com.horsti.wrapped;

import net.minecraft.server.level.ServerPlayer;

/**
 * Eine Auswertungs-Kategorie: Titel, abzulesender Wert, Formatierung.
 * Neue Kategorie = eine Zeile in Kategorien.ALLE — und andere Mods koennen
 * ueber registriere(...) eigene beisteuern (siehe README, Cross-Mod-Abschnitt).
 */
public record StatKategorie(String id, String titel, String beschreibung,
		Wert wert, Formatierer formatierer) {

	public interface Wert {
		/** Roher Zaehlerstand des Spielers (Lebenszeit — die Differenz bildet WrappedMod). */
		int lesen(ServerPlayer sp);
	}

	public interface Formatierer {
		String formatieren(int wert);
	}

	public String formatiere(int wert) {
		return formatierer.formatieren(wert);
	}
}
