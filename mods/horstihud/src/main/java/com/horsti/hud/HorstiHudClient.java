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
 * Der optionale Begleiter zu unseren Server-Mods.
 *
 * <p>Er fuegt dem Spiel nichts hinzu — er zeigt nur an, was der Server ohnehin schon weiss,
 * als Textblock statt als flüchtige Actionbar-Zeile. Wer ihn nicht installiert, bekommt
 * weiterhin die Actionbar; der Server merkt selbst, welcher Weg gilt (siehe
 * {@code Broadcast#hud}). Deshalb darf dieser Mod **nicht** auf den Server.
 */
public class HorstiHudClient implements ClientModInitializer {
	private static final ModSettings SETTINGS = new ModSettings("hud", true);
	private static final BoolSetting COMPACT =
		SETTINGS.add(new BoolSetting("compact", "one line per section, without labels", false));
	private static final IntSetting X_PROZENT =
		SETTINGS.add(new IntSetting("x", "horizontal position in % of the screen", 2, 0, 100));
	private static final IntSetting Y_PROZENT =
		SETTINGS.add(new IntSetting("y", "vertical position in % of the screen", 40, 0, 100));

	@Override
	public void onInitializeClient() {
		// Keine Commands: auf einem fremden Server koennten wir sie nicht registrieren.
		// Die Einstellungen leben in config/horsti/hud.json.
		SETTINGS.laden();

		ClientPlayNetworking.registerGlobalReceiver(HudPayload.TYPE, (payload, context) ->
			context.client().execute(() -> HudState.setzen(payload.section(), payload.text())));

		// Beim Server-Wechsel nichts Altes stehen lassen.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> HudState.leeren());

		HudElementRegistry.addLast(
			Identifier.fromNamespaceAndPath("horsti_hud", "overlay"),
			new HudOverlay(COMPACT, X_PROZENT, Y_PROZENT));
	}

	/** Globaler Schalter aus der Config. */
	public static boolean sichtbar() {
		return SETTINGS.istAktiv();
	}
}
