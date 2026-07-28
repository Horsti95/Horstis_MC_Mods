package com.horsti.core.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * The shared contract between our server mods and horstihud.
 *
 * <p>It lives in core so both sides use the same channel id and the same codec — and so
 * every mod carries it automatically via jar-in-jar.
 *
 * <p>Deliberately just two strings: a section ("manhunt", "nemesis" …) and the finished
 * text. No serialised Component, no numeric fields — that keeps the channel stable even
 * when a mod reworks its own display.
 */
public record HudPayload(String section, String text) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<HudPayload> TYPE =
		new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("horsti", "hud"));

	public static final StreamCodec<RegistryFriendlyByteBuf, HudPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, HudPayload::section,
		ByteBufCodecs.STRING_UTF8, HudPayload::text,
		HudPayload::new);

	/** Empty text = hide the section. */
	public boolean isEmpty() {
		return text == null || text.isEmpty();
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
