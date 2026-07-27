package com.horsti.cobblekeys;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

/**
 * Cobblemon has a lot of keybinds and no place that lists them. This adds that place:
 * one hotkey, one screen, no gameplay change.
 *
 * <p>Client-side only, and with no compile-time dependency on Cobblemon — the list comes
 * from the game's own keybind registry.
 */
public class CobbleKeysClient implements ClientModInitializer {
	public static KeyMapping OPEN_KEY;

	private KeysConfig config;

	@Override
	public void onInitializeClient() {
		config = KeysConfig.load();

		OPEN_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.horsti_cobble_keys.open",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_K,
			"key.categories.horsti_cobble_keys"));

		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(Minecraft client) {
		while (OPEN_KEY.consumeClick()) {
			if (Screen.hasShiftDown()) {
				KeyListScreen.openControls(client);
			} else {
				client.setScreen(new KeyListScreen(config));
			}
		}
		showOnceOnFirstJoin(client);
	}

	/**
	 * New players are exactly the ones who do not know the hotkey exists, so the list
	 * introduces itself once — and then never again.
	 */
	private void showOnceOnFirstJoin(Minecraft client) {
		if (!config.showOnFirstJoin || config.firstJoinDone || client.player == null || client.screen != null) {
			return;
		}
		config.firstJoinDone = true;
		config.save();
		client.setScreen(new KeyListScreen(config));
	}
}
