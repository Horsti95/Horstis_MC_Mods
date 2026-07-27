package com.horsti.hud;

import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Zeichnet die Server-Zustaende als Textblock.
 *
 * <p>26.2 hat die HUD-API umgebaut: es gibt kein {@code HudRenderCallback} und kein
 * {@code GuiGraphics#drawString} mehr. Ein HUD-Element traegt jetzt seinen Zustand in einen
 * {@link GuiGraphicsExtractor} ein, gezeichnet wird spaeter zentral. Fuer uns heisst das nur:
 * andere Klasse, andere Methodennamen — {@code text(...)} statt {@code drawString(...)}.
 *
 * <p>Keine eigenen Grafiken: Vanilla-Font, ein halbtransparentes Rechteck, sonst nichts
 * (PLAN.md, Prinzip 2).
 */
public class HudOverlay implements HudElement {
	private static final int WEISS = 0xFFFFFFFF;
	private static final int HINTERGRUND = 0x90101010;
	private static final int ZEILENHOEHE = 10;
	private static final int RAND = 3;

	/** Farbe je Abschnitt — bewusst dieselben Toene wie in der Actionbar der Server-Mods. */
	private static final Map<String, Integer> FARBEN = Map.of(
		"manhunt", 0xFFFFAA00,
		"nemesis", 0xFFFF5555,
		"bounty", 0xFFFFFF55,
		"lifesteal", 0xFFFF5555,
		"tag", 0xFF55FFFF,
		"juggernaut", 0xFFFFAA00,
		"deathswap", 0xFF55FF55);

	private final BoolSetting compact;
	private final IntSetting xProzent;
	private final IntSetting yProzent;

	public HudOverlay(BoolSetting compact, IntSetting xProzent, IntSetting yProzent) {
		this.compact = compact;
		this.xProzent = xProzent;
		this.yProzent = yProzent;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || !HorstiHudClient.sichtbar()) {
			return;
		}
		List<HudState.Zeile> zeilen = HudState.sichtbar();
		if (zeilen.isEmpty()) {
			return;
		}

		int breite = 0;
		for (HudState.Zeile zeile : zeilen) {
			breite = Math.max(breite, client.font.width(text(zeile)));
		}

		int x = graphics.guiWidth() * xProzent.get() / 100;
		int y = graphics.guiHeight() * yProzent.get() / 100;
		// Innerhalb des Bildschirms halten, egal welche Prozentwerte eingestellt sind.
		x = Math.min(x, Math.max(0, graphics.guiWidth() - breite - 2 * RAND));
		y = Math.min(y, Math.max(0, graphics.guiHeight() - zeilen.size() * ZEILENHOEHE - 2 * RAND));

		graphics.fill(x, y, x + breite + 2 * RAND, y + zeilen.size() * ZEILENHOEHE + 2 * RAND, HINTERGRUND);

		int zeilenY = y + RAND;
		for (HudState.Zeile zeile : zeilen) {
			graphics.text(client.font, Component.literal(text(zeile)), x + RAND, zeilenY, farbe(zeile.abschnitt()));
			zeilenY += ZEILENHOEHE;
		}
	}

	private String text(HudState.Zeile zeile) {
		return compact.get() ? zeile.text() : label(zeile.abschnitt()) + " " + zeile.text();
	}

	/** "manhunt" → "Manhunt" — der Abschnittsname ist gleichzeitig sein Label. */
	private static String label(String abschnitt) {
		if (abschnitt.isEmpty()) {
			return abschnitt;
		}
		return abschnitt.substring(0, 1).toUpperCase(Locale.ROOT) + abschnitt.substring(1) + ":";
	}

	private static int farbe(String abschnitt) {
		return FARBEN.getOrDefault(abschnitt, WEISS);
	}
}
