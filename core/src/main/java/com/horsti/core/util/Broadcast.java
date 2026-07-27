package com.horsti.core.util;

import com.horsti.core.net.HudPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class Broadcast {
	private Broadcast() {
	}

	public static void chat(MinecraftServer server, Component msg) {
		server.getPlayerList().broadcastSystemMessage(msg, false);
	}

	public static void actionbar(ServerPlayer player, Component msg) {
		player.connection.send(new ClientboundSetActionBarTextPacket(msg));
	}

	/**
	 * Dauerzustand fuer den Spieler: geht an horstihud, wenn der Client ihn hat,
	 * sonst in die Actionbar wie bisher.
	 *
	 * <p>Genau das ist der Trick, mit dem der Client-Mod optional bleibt: der Server
	 * fragt einmal, ob der Kanal drueben registriert ist, und faellt sonst auf Vanilla
	 * zurueck. Kein Handshake, kein Zwang, kein Unterschied fuer Vanilla-Clients.
	 */
	public static void hud(ServerPlayer player, String abschnitt, Component msg) {
		if (ServerPlayNetworking.canSend(player, HudPayload.TYPE)) {
			ServerPlayNetworking.send(player, new HudPayload(abschnitt, msg.getString()));
		} else {
			actionbar(player, msg);
		}
	}

	/** Blendet einen HUD-Abschnitt aus (nur fuer Clients mit horstihud relevant). */
	public static void hudAus(ServerPlayer player, String abschnitt) {
		if (ServerPlayNetworking.canSend(player, HudPayload.TYPE)) {
			ServerPlayNetworking.send(player, new HudPayload(abschnitt, ""));
		}
	}

	public static void titel(ServerPlayer player, Component titel, Component untertitel) {
		player.connection.send(new ClientboundSetTitleTextPacket(titel));
		if (untertitel != null) {
			player.connection.send(new ClientboundSetSubtitleTextPacket(untertitel));
		}
	}

	public static void titelAlle(MinecraftServer server, Component titel, Component untertitel) {
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			titel(p, titel, untertitel);
		}
	}
}
