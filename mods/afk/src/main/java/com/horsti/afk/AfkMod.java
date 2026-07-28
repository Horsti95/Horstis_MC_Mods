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
	private final IntSetting minutes = settings.add(new IntSetting("minutes", "inactivity before AFK", 5, 1, 60));
	private final BoolSetting announce = settings.add(new BoolSetting("announce", "chat message on going AFK / returning", true));
	private final IntSetting kickMinutes = settings.add(new IntSetting("kickMinutes", "kick after that many more minutes, 0 = never", 0, 0, 120));

	private static final class State {
		double x, y, z;
		float rotX, rotY;
		long idleTicks = 0;
		boolean afk = false;
	}

	private final Map<UUID, State> players = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("afk", "AFK", settings)
			.onToggle(() -> {
			}).registrieren();

		// /afk! for every player (mark yourself AFK by hand)
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("afk!").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				setAfk(sp, true);
				return 1;
			})));

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> activity(sender));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			State z = players.remove(handler.getPlayer().getUUID());
			if (z != null && z.afk) {
				teamRemove(server, handler.getPlayer());
			}
		});

		Ticker.alleTicks(20, this::secondTick);
	}

	private void secondTick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			State z = players.computeIfAbsent(sp.getUUID(), u -> new State());
			boolean moved = Math.abs(sp.getX() - z.x) > 0.01 || Math.abs(sp.getY() - z.y) > 0.01
				|| Math.abs(sp.getZ() - z.z) > 0.01 || sp.getXRot() != z.rotX || sp.getYRot() != z.rotY;
			z.x = sp.getX();
			z.y = sp.getY();
			z.z = sp.getZ();
			z.rotX = sp.getXRot();
			z.rotY = sp.getYRot();

			if (moved) {
				z.idleTicks = 0;
				if (z.afk) {
					setAfk(sp, false);
				}
				continue;
			}
			z.idleTicks += 20;
			if (!z.afk && z.idleTicks >= minutes.get() * 1200L) {
				setAfk(sp, true);
			}
			if (z.afk && kickMinutes.get() > 0
				&& z.idleTicks >= (minutes.get() + kickMinutes.get()) * 1200L) {
				sp.connection.disconnect(Component.literal("AFK for too long"));
			}
		}
	}

	private void activity(ServerPlayer sp) {
		State z = players.get(sp.getUUID());
		if (z != null) {
			z.idleTicks = 0;
			if (z.afk) {
				setAfk(sp, false);
			}
		}
	}

	private void setAfk(ServerPlayer sp, boolean afk) {
		State z = players.computeIfAbsent(sp.getUUID(), u -> new State());
		if (z.afk == afk) {
			return;
		}
		z.afk = afk;
		if (afk) {
			z.idleTicks = Math.max(z.idleTicks, minutes.get() * 1200L);
			teamAdd(HorstiServer.get(), sp);
			if (announce.get()) {
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " is now AFK").withStyle(ChatFormatting.GRAY));
			}
		} else {
			z.idleTicks = 0;
			teamRemove(HorstiServer.get(), sp);
			if (announce.get()) {
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " is back").withStyle(ChatFormatting.GRAY));
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

	private static void teamAdd(MinecraftServer server, ServerPlayer sp) {
		server.getScoreboard().addPlayerToTeam(sp.getScoreboardName(), team(server));
	}

	private static void teamRemove(MinecraftServer server, ServerPlayer sp) {
		PlayerTeam team = server.getScoreboard().getPlayerTeam(TEAM_NAME);
		if (team != null) {
			server.getScoreboard().removePlayerFromTeam(sp.getScoreboardName(), team);
		}
	}
}
