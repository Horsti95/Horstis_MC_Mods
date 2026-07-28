package com.horsti.hud;

import com.horsti.core.net.HudPayload;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

/**
 * The optional companion to our server mods.
 *
 * <p>It adds nothing to the game — it only shows what the server already knows, as a text
 * block instead of a fleeting action bar line. Anyone who does not install it keeps the
 * action bar; the server works out which path applies on its own (see {@code Broadcast#hud}).
 * That is why this mod must <b>not</b> go on the server.
 */
public class HorstiHudClient implements ClientModInitializer {
	private static final ModSettings SETTINGS = new ModSettings("hud", true);
	private static final BoolSetting COMPACT =
		SETTINGS.add(new BoolSetting("compact", "one line per section, without labels", false));
	private static final IntSetting X_PERCENT =
		SETTINGS.add(new IntSetting("x", "horizontal position in % of the screen", 2, 0, 100));
	private static final IntSetting Y_PERCENT =
		SETTINGS.add(new IntSetting("y", "vertical position in % of the screen", 40, 0, 100));

	@Override
	public void onInitializeClient() {
		// No commands: on someone else's server we could not register them anyway.
		// The settings live in config/horsti/hud.json.
		SETTINGS.laden();

		ClientPlayNetworking.registerGlobalReceiver(HudPayload.TYPE, (payload, context) ->
			context.client().execute(() -> HudState.set(payload.section(), payload.text())));

		// Do not leave stale lines behind when switching servers.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> HudState.clear());

		HudElementRegistry.addLast(
			Identifier.fromNamespaceAndPath("horsti_hud", "overlay"),
			new HudOverlay(COMPACT, X_PERCENT, Y_PERCENT));
	}

	/** Master switch from the config. */
	public static boolean visible() {
		return SETTINGS.istAktiv();
	}
}
