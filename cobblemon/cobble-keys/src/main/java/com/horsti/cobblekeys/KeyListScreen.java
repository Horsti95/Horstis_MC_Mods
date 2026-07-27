package com.horsti.cobblekeys;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * The list itself: every keybind whose name or category mentions the configured namespace.
 *
 * <p>Nothing here is drawn from an image — it is vanilla text on a translucent rectangle
 * (PLAN.md, principle 2). Because it reads the live keybind registry, keybinds added by a
 * future Cobblemon version or by another side-mod show up on their own.
 */
public class KeyListScreen extends Screen {
	private static final int TEXT = 0xFFFFFFFF;
	private static final int DIM = 0xFFAAAAAA;
	private static final int ACCENT = 0xFFFFAA00;
	private static final int WARN = 0xFFFF5555;
	private static final int PANEL = 0xC0101010;
	private static final int ROW_HEIGHT = 12;

	private final KeysConfig config;
	private final List<KeyMapping> entries = new ArrayList<>();
	private int scroll = 0;

	public KeyListScreen(KeysConfig config) {
		super(Component.translatable("screen.horsti_cobble_keys.title"));
		this.config = config;
	}

	@Override
	protected void init() {
		entries.clear();
		String needle = config.namespace.toLowerCase(Locale.ROOT);
		for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
			if (matches(mapping, needle)) {
				entries.add(mapping);
			}
		}
		entries.sort(Comparator.comparing(KeyMapping::getCategory).thenComparing(KeyMapping::getName));
	}

	private static boolean matches(KeyMapping mapping, String needle) {
		return mapping.getName().toLowerCase(Locale.ROOT).contains(needle)
			|| mapping.getCategory().toLowerCase(Locale.ROOT).contains(needle);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);

		int panelWidth = Math.min(320, width - 40);
		int left = (width - panelWidth) / 2;
		int top = 30;
		int bottom = height - 30;
		graphics.fill(left, top, left + panelWidth, bottom, PANEL);

		graphics.drawCenteredString(font, title, width / 2, top + 6, ACCENT);
		int y = top + 22 - scroll;

		if (entries.isEmpty()) {
			graphics.drawString(font, Component.translatable("screen.horsti_cobble_keys.empty", config.namespace),
				left + 8, y, WARN);
			y += ROW_HEIGHT * 2;
		}

		for (KeyMapping mapping : entries) {
			if (y > top + 16 && y < bottom - ROW_HEIGHT) {
				drawRow(graphics, mapping, left, panelWidth, y);
			}
			y += ROW_HEIGHT;
		}

		if (!config.notes.isEmpty()) {
			y += 6;
			if (y > top + 16 && y < bottom - ROW_HEIGHT) {
				graphics.drawString(font, Component.translatable("screen.horsti_cobble_keys.notes"), left + 8, y, ACCENT);
			}
			y += ROW_HEIGHT;
			for (String note : config.notes) {
				if (y > top + 16 && y < bottom - ROW_HEIGHT) {
					graphics.drawString(font, note, left + 8, y, DIM);
				}
				y += ROW_HEIGHT;
			}
		}

		graphics.drawCenteredString(font, Component.translatable("screen.horsti_cobble_keys.footer"),
			width / 2, bottom - 11, DIM);
	}

	private void drawRow(GuiGraphics graphics, KeyMapping mapping, int left, int panelWidth, int y) {
		String label = I18n.get(mapping.getName());
		graphics.drawString(font, label, left + 8, y, TEXT);

		String description = describe(mapping);
		if (description != null) {
			int labelEnd = left + 8 + font.width(label) + 6;
			graphics.drawString(font, description, labelEnd, y, DIM);
		}

		Component key = mapping.isUnbound()
			? Component.translatable("screen.horsti_cobble_keys.unbound").withStyle(ChatFormatting.RED)
			: mapping.getTranslatedKeyMessage();
		int keyX = left + panelWidth - 8 - font.width(key);
		graphics.drawString(font, key, keyX, y, mapping.isUnbound() ? WARN : ACCENT);
	}

	/** Our own one-liner for a keybind, if we ship one for it. */
	private static String describe(KeyMapping mapping) {
		String key = "horsti_cobble_keys.desc." + mapping.getName();
		return I18n.exists(key) ? I18n.get(key) : null;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		scroll = Math.max(0, scroll - (int) (scrollY * ROW_HEIGHT));
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// The same key that opened it closes it again.
		if (CobbleKeysClient.OPEN_KEY.matches(keyCode, scanCode)) {
			onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	/** Straight into the vanilla controls screen, so a rebind is one click away. */
	public static void openControls(Minecraft client) {
		client.setScreen(new KeyBindsScreen(client.screen, client.options));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
