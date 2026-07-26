package com.horsti.wrapped;

import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
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
	private final IntSetting intervallTage = settings.add(new IntSetting("intervallTage", "Tage zwischen Ansagen", 7, 1, 30));
	private final IntSetting kategorien = settings.add(new IntSetting("kategorien", "Anzahl angesagter Kategorien", 6, 3, 8));
	private final IntSetting minSpieler = settings.add(new IntSetting("minSpieler", "Mindest-Spielerzahl fuer die Ansage", 2, 1, 16));
	private final BoolSetting nullwerte = settings.add(new BoolSetting("nullwerte", "Kategorien ohne Punkte mitnehmen", false));

	private final JsonSpeicher speicher = new JsonSpeicher("wrapped");
	private JsonObject daten;

	/** Ein Spieler mit seinem Zuwachs in einer Kategorie. */
	private record Platz(String name, int wert) {
	}

	@Override
	public void onInitialize() {
		new HorstiMod("wrapped", "Wrapped", settings)
			.extra((root, ctx) -> root.then(Commands.literal("jetzt").executes(c -> {
				ansagen(c.getSource().getServer());
				periodeStarten(c.getSource().getServer());
				return 1;
			})))
			.registrieren();

		daten = speicher.laden();

		// /wrapped ohne Rechte: aktueller Zwischenstand fuer alle
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("wrapped").executes(c -> {
				zwischenstand(c.getSource());
				return 1;
			})));

		// Neue Spieler bekommen sofort einen Startwert, damit ihre Woche bei null beginnt
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (settings.istAktiv() && !basiswerte().has(handler.getPlayer().getUUID().toString())) {
				spielerErfassen(handler.getPlayer());
				speicher.speichern(daten);
			}
		});

		// Einmal pro Minute reicht — die Periode laeuft in Tagen
		Ticker.alleTicks(1200, this::minutenTick);
	}

	private JsonObject basiswerte() {
		if (!daten.has("basis")) {
			daten.add("basis", new JsonObject());
		}
		return daten.getAsJsonObject("basis");
	}

	private void minutenTick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		if (!daten.has("periodeStart")) {
			periodeStarten(server);
			return;
		}
		long faellig = daten.get("periodeStart").getAsLong() + intervallTage.get() * 86_400_000L;
		if (System.currentTimeMillis() >= faellig) {
			if (server.getPlayerList().getPlayers().size() >= minSpieler.get()) {
				ansagen(server);
			}
			periodeStarten(server);
		}
	}

	/** Merkt sich fuer jeden Online-Spieler den aktuellen Zaehlerstand als Nullpunkt. */
	private void periodeStarten(MinecraftServer server) {
		daten.addProperty("periodeStart", System.currentTimeMillis());
		daten.add("basis", new JsonObject());
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			spielerErfassen(sp);
		}
		speicher.speichern(daten);
	}

	private void spielerErfassen(ServerPlayer sp) {
		JsonObject werte = new JsonObject();
		for (StatKategorie k : Kategorien.alle()) {
			werte.addProperty(k.id(), k.wert().lesen(sp));
		}
		basiswerte().add(sp.getUUID().toString(), werte);
	}

	/** Zuwachs seit Periodenbeginn; ohne Basiswert zaehlt der volle Stand (frisch dabei). */
	private int zuwachs(ServerPlayer sp, StatKategorie k) {
		int jetzt = k.wert().lesen(sp);
		JsonObject basis = basiswerte().getAsJsonObject(sp.getUUID().toString());
		if (basis == null || !basis.has(k.id())) {
			return jetzt;
		}
		return Math.max(0, jetzt - basis.get(k.id()).getAsInt());
	}

	/** Alle Spieler mit dem Höchstwert einer Kategorie (leer = niemand hat Punkte). */
	private List<Platz> gewinner(MinecraftServer server, StatKategorie k) {
		List<Platz> beste = new ArrayList<>();
		int hoechster = 0;
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			int wert = zuwachs(sp, k);
			if (wert > hoechster) {
				hoechster = wert;
				beste.clear();
				beste.add(new Platz(sp.getName().getString(), wert));
			} else if (wert == hoechster && wert > 0) {
				beste.add(new Platz(sp.getName().getString(), wert));
			}
		}
		return beste;
	}

	private void ansagen(MinecraftServer server) {
		Broadcast.chat(server, Component.literal("═══ Die Woche auf dem Server ═══").withStyle(ChatFormatting.GOLD));
		int gezeigt = 0;
		for (StatKategorie k : Kategorien.alle()) {
			if (gezeigt >= kategorien.get()) {
				break;
			}
			List<Platz> beste = gewinner(server, k);
			if (beste.isEmpty() && !nullwerte.get()) {
				continue;
			}
			Broadcast.chat(server, zeile(k, beste));
			gezeigt++;
		}
		if (gezeigt == 0) {
			Broadcast.chat(server, Component.literal("  (diese Woche ist nichts passiert)").withStyle(ChatFormatting.GRAY));
		}
	}

	private void zwischenstand(CommandSourceStack quelle) {
		MinecraftServer server = quelle.getServer();
		quelle.sendSuccess(() -> Component.literal("═══ Zwischenstand ═══").withStyle(ChatFormatting.GOLD), false);
		for (StatKategorie k : Kategorien.alle()) {
			List<Platz> beste = gewinner(server, k);
			if (beste.isEmpty() && !nullwerte.get()) {
				continue;
			}
			final Component zeile = zeile(k, beste);
			quelle.sendSuccess(() -> zeile, false);
		}
	}

	private Component zeile(StatKategorie k, List<Platz> beste) {
		if (beste.isEmpty()) {
			return Component.literal(k.titel() + " — niemand").withStyle(ChatFormatting.DARK_GRAY);
		}
		String namen = String.join(" & ", beste.stream().map(Platz::name).toList());
		return Component.literal(k.titel() + " ").withStyle(ChatFormatting.YELLOW)
			.append(Component.literal(namen).withStyle(ChatFormatting.WHITE))
			.append(Component.literal(" — " + k.formatiere(beste.get(0).wert())).withStyle(ChatFormatting.GRAY))
			.append(Component.literal("  (" + k.beschreibung() + ")").withStyle(ChatFormatting.DARK_GRAY));
	}
}
