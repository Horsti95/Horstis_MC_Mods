package com.horsti.core.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Der gemeinsame Vertrag zwischen unseren Server-Mods und horstihud.
 *
 * <p>Liegt in core, damit beide Seiten dieselbe Kanal-ID und denselben Codec benutzen —
 * und damit jede Mod ihn per Jar-in-Jar automatisch dabei hat.
 *
 * <p>Bewusst nur zwei Strings: ein Abschnitt ("manhunt", "nemesis" …) und der fertige Text.
 * Kein serialisiertes Component, keine Zahlenfelder — so bleibt der Kanal stabil, auch wenn
 * eine Mod ihre Anzeige umbaut.
 */
public record HudPayload(String section, String text) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<HudPayload> TYPE =
		new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("horsti", "hud"));

	public static final StreamCodec<RegistryFriendlyByteBuf, HudPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, HudPayload::section,
		ByteBufCodecs.STRING_UTF8, HudPayload::text,
		HudPayload::new);

	/** Leerer Text = Abschnitt ausblenden. */
	public boolean istLeer() {
		return text == null || text.isEmpty();
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
