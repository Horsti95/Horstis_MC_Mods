package com.horsti.core.util;

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
