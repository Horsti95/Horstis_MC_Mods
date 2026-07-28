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
 * Draws the server-side state as a block of text.
 *
 * <p>26.2 hat die HUD-API umgebaut: es gibt kein {@code HudRenderCallback} und kein
 * {@code GuiGraphics#drawString} mehr. Ein HUD-Element traegt jetzt seinen Zustand in einen
 * {@link GuiGraphicsExtractor} ein, gezeichnet wird spaeter zentral. Fuer uns heisst das nur:
 * andere Klasse, andere Methodennamen — {@code text(...)} statt {@code drawString(...)}.
 *
 * <p>No graphics of our own: the vanilla font, a translucent rectangle, nothing else
 * (PLAN.md, principle 2).
 */
public class HudOverlay implements HudElement {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BACKGROUND = 0x90101010;
	private static final int LINE_HEIGHT = 10;
	private static final int PADDING = 3;

	/** Colour per section — deliberately the same tones the server mods use in the action bar. */
	private static final Map<String, Integer> COLORS = Map.of(
		"manhunt", 0xFFFFAA00,
		"nemesis", 0xFFFF5555,
		"bounty", 0xFFFFFF55,
		"lifesteal", 0xFFFF5555,
		"tag", 0xFF55FFFF,
		"juggernaut", 0xFFFFAA00,
		"deathswap", 0xFF55FF55);

	private final BoolSetting compact;
	private final IntSetting xPercent;
	private final IntSetting yPercent;

	public HudOverlay(BoolSetting compact, IntSetting xPercent, IntSetting yPercent) {
		this.compact = compact;
		this.xPercent = xPercent;
		this.yPercent = yPercent;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || !HorstiHudClient.visible()) {
			return;
		}
		List<HudState.Line> lines = HudState.visible();
		if (lines.isEmpty()) {
			return;
		}

		int width = 0;
		for (HudState.Line line : lines) {
			width = Math.max(width, client.font.width(text(line)));
		}

		int x = graphics.guiWidth() * xPercent.get() / 100;
		int y = graphics.guiHeight() * yPercent.get() / 100;
		// Keep it on screen no matter what percentages are configured.
		x = Math.min(x, Math.max(0, graphics.guiWidth() - width - 2 * PADDING));
		y = Math.min(y, Math.max(0, graphics.guiHeight() - lines.size() * LINE_HEIGHT - 2 * PADDING));

		graphics.fill(x, y, x + width + 2 * PADDING, y + lines.size() * LINE_HEIGHT + 2 * PADDING, BACKGROUND);

		int lineY = y + PADDING;
		for (HudState.Line line : lines) {
			graphics.text(client.font, Component.literal(text(line)), x + PADDING, lineY, color(line.section()));
			lineY += LINE_HEIGHT;
		}
	}

	private String text(HudState.Line line) {
		return compact.get() ? line.text() : label(line.section()) + " " + line.text();
	}

	/** "manhunt" → "Manhunt" — the section name doubles as its label. */
	private static String label(String section) {
		if (section.isEmpty()) {
			return section;
		}
		return section.substring(0, 1).toUpperCase(Locale.ROOT) + section.substring(1) + ":";
	}

	private static int color(String section) {
		return COLORS.getOrDefault(section, WHITE);
	}
}
