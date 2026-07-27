package com.horsti.wrapped;

import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.JsonSpeicher;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class WrappedMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("wrapped", true);
	private final IntSetting intervalDays = settings.add(new IntSetting("intervalDays", "days between announcements", 7, 1, 30));
	private final IntSetting categories = settings.add(new IntSetting("categories", "how many categories to announce", 6, 3, 8));
	private final IntSetting minPlayers = settings.add(new IntSetting("minPlayers", "minimum players before announcing", 2, 1, 16));
	private final BoolSetting includeZero = settings.add(new BoolSetting("includeZero", "include categories nobody scored in", false));

	private final JsonSpeicher storage = new JsonSpeicher("wrapped");
	private JsonObject data;

	/** A player and their gain in one category. */
	private record Standing(String name, int value) {
	}

	@Override
	public void onInitialize() {
		new HorstiMod("wrapped", "Wrapped", settings)
			.extra((root, ctx) -> root.then(Commands.literal("now").executes(c -> {
				announce(c.getSource().getServer());
				startPeriod(c.getSource().getServer());
				return 1;
			})))
			.registrieren();

		data = storage.laden();

		// /wrapped without permissions: current standings for everyone
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("wrapped").executes(c -> {
				showStandings(c.getSource());
				return 1;
			})));

		// New players get a baseline immediately so their week starts at zero
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (settings.istAktiv() && !baselines().has(handler.getPlayer().getUUID().toString())) {
				captureBaseline(handler.getPlayer());
				storage.speichern(data);
			}
		});

		// Once a minute is plenty — the period runs in days
		Ticker.alleTicks(1200, this::minuteTick);
	}

	private JsonObject baselines() {
		if (!data.has("baselines")) {
			data.add("baselines", new JsonObject());
		}
		return data.getAsJsonObject("baselines");
	}

	private void minuteTick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		if (!data.has("periodStart")) {
			startPeriod(server);
			return;
		}
		long due = data.get("periodStart").getAsLong() + intervalDays.get() * 86_400_000L;
		if (System.currentTimeMillis() >= due) {
			if (server.getPlayerList().getPlayers().size() >= minPlayers.get()) {
				announce(server);
			}
			startPeriod(server);
		}
	}

	/** Records every online player's current counters as the new zero point. */
	private void startPeriod(MinecraftServer server) {
		data.addProperty("periodStart", System.currentTimeMillis());
		data.add("baselines", new JsonObject());
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			captureBaseline(player);
		}
		storage.speichern(data);
	}

	private void captureBaseline(ServerPlayer player) {
		JsonObject values = new JsonObject();
		for (StatKategorie category : Kategorien.all()) {
			values.addProperty(category.id(), category.value().read(player));
		}
		baselines().add(player.getUUID().toString(), values);
	}

	/** Gain since the period started; without a baseline the full count applies (newly joined). */
	private int gain(ServerPlayer player, StatKategorie category) {
		int now = category.value().read(player);
		JsonObject baseline = baselines().getAsJsonObject(player.getUUID().toString());
		if (baseline == null || !baseline.has(category.id())) {
			return now;
		}
		return Math.max(0, now - baseline.get(category.id()).getAsInt());
	}

	/** Every player tied for the highest value (empty = nobody scored). */
	private List<Standing> winners(MinecraftServer server, StatKategorie category) {
		List<Standing> best = new ArrayList<>();
		int highest = 0;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			int value = gain(player, category);
			if (value > highest) {
				highest = value;
				best.clear();
				best.add(new Standing(player.getName().getString(), value));
			} else if (value == highest && value > 0) {
				best.add(new Standing(player.getName().getString(), value));
			}
		}
		return best;
	}

	private void announce(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("═══ This week on the server ═══").withStyle(ChatFormatting.GOLD));
		int shown = 0;
		for (StatKategorie category : Kategorien.all()) {
			if (shown >= categories.get()) {
				break;
			}
			List<Standing> best = winners(server, category);
			if (best.isEmpty() && !includeZero.get()) {
				continue;
			}
			Broadcast.chat(server, line(category, best));
			shown++;
		}
		if (shown == 0) {
			Broadcast.chat(server, Component.literal("  (nothing happened this week)").withStyle(ChatFormatting.GRAY));
		}
	}

	private void showStandings(CommandSourceStack source) {
		MinecraftServer server = source.getServer();
		source.sendSuccess(() -> Component.literal("═══ Current standings ═══").withStyle(ChatFormatting.GOLD), false);
		for (StatKategorie category : Kategorien.all()) {
			List<Standing> best = winners(server, category);
			if (best.isEmpty() && !includeZero.get()) {
				continue;
			}
			final Component text = line(category, best);
			source.sendSuccess(() -> text, false);
		}
	}

	private Component line(StatKategorie category, List<Standing> best) {
		if (best.isEmpty()) {
			return Component.literal(category.title() + " — nobody").withStyle(ChatFormatting.DARK_GRAY);
		}
		String names = String.join(" & ", best.stream().map(Standing::name).toList());
		return Component.literal(category.title() + " ").withStyle(ChatFormatting.YELLOW)
			.append(Component.literal(names).withStyle(ChatFormatting.WHITE))
			.append(Component.literal(" — " + category.formatValue(best.get(0).value())).withStyle(ChatFormatting.GRAY))
			.append(Component.literal("  (" + category.description() + ")").withStyle(ChatFormatting.DARK_GRAY));
	}
}
