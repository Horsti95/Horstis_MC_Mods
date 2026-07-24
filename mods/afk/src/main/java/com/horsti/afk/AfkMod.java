package com.horsti.afk;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import net.minecraft.world.scores.Scoreboard;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

public class AfkMod implements ModInitializer {
	private static final String TEAM_NAME = "horsti_afk";

	private final ModSettings settings = new ModSettings("afk", true);
	private final IntSetting minuten = settings.add(new IntSetting("minuten", "Inaktivitaet bis AFK", 5, 1, 60));
	private final BoolSetting ansage = settings.add(new BoolSetting("ansage", "Chat-Ansage bei AFK/Rueckkehr", true));
	private final IntSetting kickMinuten = settings.add(new IntSetting("kickMinuten", "danach kicken, 0 = nie", 0, 0, 120));

	private static final class Zustand {
		double x, y, z;
		float rotX, rotY;
		long inaktivTicks = 0;
		boolean afk = false;
	}

	private final Map<UUID, Zustand> spieler = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("afk", "AFK", settings)
			.onToggle(() -> {
			}).registrieren();

		// /afk! fuer alle Spieler (manuell AFK setzen)
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("afk!").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				setzeAfk(sp, true);
				return 1;
			})));

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> aktivitaet(sender));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			Zustand z = spieler.remove(handler.getPlayer().getUUID());
			if (z != null && z.afk) {
				teamEntfernen(server, handler.getPlayer());
			}
		});

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private void sekundenTick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			Zustand z = spieler.computeIfAbsent(sp.getUUID(), u -> new Zustand());
			boolean bewegt = Math.abs(sp.getX() - z.x) > 0.01 || Math.abs(sp.getY() - z.y) > 0.01
				|| Math.abs(sp.getZ() - z.z) > 0.01 || sp.getXRot() != z.rotX || sp.getYRot() != z.rotY;
			z.x = sp.getX();
			z.y = sp.getY();
			z.z = sp.getZ();
			z.rotX = sp.getXRot();
			z.rotY = sp.getYRot();

			if (bewegt) {
				z.inaktivTicks = 0;
				if (z.afk) {
					setzeAfk(sp, false);
				}
				continue;
			}
			z.inaktivTicks += 20;
			if (!z.afk && z.inaktivTicks >= minuten.get() * 1200L) {
				setzeAfk(sp, true);
			}
			if (z.afk && kickMinuten.get() > 0
				&& z.inaktivTicks >= (minuten.get() + kickMinuten.get()) * 1200L) {
				sp.connection.disconnect(Component.literal("Zu lange AFK"));
			}
		}
	}

	private void aktivitaet(ServerPlayer sp) {
		Zustand z = spieler.get(sp.getUUID());
		if (z != null) {
			z.inaktivTicks = 0;
			if (z.afk) {
				setzeAfk(sp, false);
			}
		}
	}

	private void setzeAfk(ServerPlayer sp, boolean afk) {
		Zustand z = spieler.computeIfAbsent(sp.getUUID(), u -> new Zustand());
		if (z.afk == afk) {
			return;
		}
		z.afk = afk;
		if (afk) {
			z.inaktivTicks = Math.max(z.inaktivTicks, minuten.get() * 1200L);
			teamHinzufuegen(HorstiServer.get(), sp);
			if (ansage.get()) {
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " ist jetzt AFK").withStyle(ChatFormatting.GRAY));
			}
		} else {
			z.inaktivTicks = 0;
			teamEntfernen(HorstiServer.get(), sp);
			if (ansage.get()) {
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " ist zurueck").withStyle(ChatFormatting.GRAY));
			}
		}
	}

	private static PlayerTeam team(MinecraftServer server) {
		Scoreboard scoreboard = server.getScoreboard();
		PlayerTeam team = scoreboard.getPlayerTeam(TEAM_NAME);
		if (team == null) {
			team = scoreboard.addPlayerTeam(TEAM_NAME);
			team.setColor(Optional.of(TeamColor.GRAY));
		}
		return team;
	}

	private static void teamHinzufuegen(MinecraftServer server, ServerPlayer sp) {
		server.getScoreboard().addPlayerToTeam(sp.getScoreboardName(), team(server));
	}

	private static void teamEntfernen(MinecraftServer server, ServerPlayer sp) {
		PlayerTeam team = server.getScoreboard().getPlayerTeam(TEAM_NAME);
		if (team != null) {
			server.getScoreboard().removePlayerFromTeam(sp.getScoreboardName(), team);
		}
	}
}
