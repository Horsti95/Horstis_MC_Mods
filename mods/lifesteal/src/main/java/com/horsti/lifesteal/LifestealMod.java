package com.horsti.lifesteal;

import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.settings.StringSetting;
import com.horsti.core.util.Attribute;
import com.horsti.core.util.Broadcast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.UUID;

public class LifestealMod implements ModInitializer {
	private static final Identifier HERZ_ID = Attribute.id("lifesteal", "herzen");

	private final ModSettings settings = new ModSettings("lifesteal", true);
	private final IntSetting startHerzen = settings.add(new IntSetting("startHerzen", "Start-Maximum in Herzen", 10, 5, 30));
	private final IntSetting maxHerzen = settings.add(new IntSetting("maxHerzen", "Obergrenze in Herzen", 20, 10, 40));
	private final IntSetting minHerzen = settings.add(new IntSetting("minHerzen", "Ausscheide-Schwelle", 0, 0, 5));
	private final EnumSetting natTod = settings.add(new EnumSetting("natTod", "kostet ein Nicht-PvP-Tod ein Herz?", "frei", "frei", "herz"));
	private final StringSetting herzItem = settings.add(new StringSetting("herzItem", "Item, das als Herz zaehlt", "minecraft:nether_star"));

	private final com.horsti.core.util.JsonSpeicher speicher = new com.horsti.core.util.JsonSpeicher("lifesteal");
	private JsonObject daten;

	@Override
	public void onInitialize() {
		new HorstiMod("lifesteal", "Lifesteal", settings)
			.onToggle(this::alleAktualisieren)
			.extra((root, ctx) -> {
				root.then(Commands.literal("revive")
					.then(Commands.argument("spieler", EntityArgument.player()).executes(c ->
						wiederbeleben(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler"), null))));
				root.then(Commands.literal("setze")
					.then(Commands.argument("spieler", EntityArgument.player())
						.then(Commands.argument("herzen", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 40))
							.executes(c -> {
								ServerPlayer sp = EntityArgument.getPlayer(c, "spieler");
								setzeHerzen(sp, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(c, "herzen"));
								c.getSource().sendSuccess(() -> Component.literal("[Lifesteal] " + sp.getName().getString()
									+ " hat jetzt " + herzen(sp) + " Herzen"), true);
								return 1;
							}))));
			})
			.registrieren();
		maxHerzen.onChange(w -> alleAktualisieren());

		daten = speicher.laden();

		// Spieler-Commands: eigener Stand + Wiederbelebung gegen ein Herz-Item
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			dispatcher.register(Commands.literal("herzen").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				sp.sendSystemMessage(Component.literal("Du hast " + herzen(sp) + " Herzen (max " + maxHerzen.get() + ").")
					.withStyle(ChatFormatting.GOLD));
				return 1;
			}));
			dispatcher.register(Commands.literal("revive")
				.then(Commands.argument("spieler", EntityArgument.player()).executes(c ->
					wiederbeleben(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler"),
						c.getSource().getPlayerOrException()))));
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer sp = handler.getPlayer();
			if (!settings.istAktiv()) {
				return;
			}
			if (!daten.has(sp.getUUID().toString())) {
				setzeHerzen(sp, startHerzen.get());
			} else {
				anwenden(sp);
			}
			if (herzen(sp) <= minHerzen.get()) {
				sp.setGameMode(GameType.SPECTATOR);
			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((alt, neu, lebt) -> {
			if (!settings.istAktiv()) {
				return;
			}
			anwenden(neu);
			if (herzen(neu) <= minHerzen.get()) {
				neu.setGameMode(GameType.SPECTATOR);
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer opfer)) {
				return;
			}
			MinecraftServer server = HorstiServer.get();
			boolean pvp = source.getEntity() instanceof ServerPlayer killer && !killer.getUUID().equals(opfer.getUUID());
			if (pvp) {
				ServerPlayer killer = (ServerPlayer) source.getEntity();
				aendern(opfer, -1);
				aendern(killer, +1);
				Broadcast.chat(server, Component.literal(killer.getName().getString() + " klaut "
					+ opfer.getName().getString() + " ein Herz! (" + herzen(killer) + " / " + herzen(opfer) + ")")
					.withStyle(ChatFormatting.RED));
			} else if (natTod.get().equals("herz")) {
				aendern(opfer, -1);
			}
			if (herzen(opfer) <= minHerzen.get()) {
				ausscheiden(server, opfer);
			}
		});
	}

	private int herzen(ServerPlayer sp) {
		return daten.has(sp.getUUID().toString())
			? daten.get(sp.getUUID().toString()).getAsInt()
			: startHerzen.get();
	}

	private void setzeHerzen(ServerPlayer sp, int wert) {
		daten.addProperty(sp.getUUID().toString(), Math.max(0, Math.min(maxHerzen.get(), wert)));
		speicher.speichern(daten);
		anwenden(sp);
	}

	private void aendern(ServerPlayer sp, int delta) {
		setzeHerzen(sp, herzen(sp) + delta);
	}

	private void anwenden(ServerPlayer sp) {
		if (!settings.istAktiv()) {
			Attribute.entfernen(sp, Attributes.MAX_HEALTH, HERZ_ID);
			return;
		}
		// Vanilla-Basis sind 20 HP = 10 Herzen; wir modifizieren die Differenz.
		double ziel = herzen(sp) * 2.0;
		Attribute.addieren(sp, Attributes.MAX_HEALTH, HERZ_ID, ziel - 20.0);
		if (sp.getHealth() > sp.getMaxHealth()) {
			sp.setHealth(sp.getMaxHealth());
		}
	}

	private void alleAktualisieren() {
		MinecraftServer server = HorstiServer.get();
		if (server != null) {
			server.getPlayerList().getPlayers().forEach(this::anwenden);
		}
	}

	private void ausscheiden(MinecraftServer server, ServerPlayer sp) {
		sp.setGameMode(GameType.SPECTATOR);
		Broadcast.titelAlle(server, Component.literal(sp.getName().getString() + " ist ausgeschieden!")
			.withStyle(ChatFormatting.DARK_RED), Component.literal("Wiederbelebung: /revive " + sp.getName().getString()));
	}

	private int wiederbeleben(MinecraftServer server, ServerPlayer ziel, ServerPlayer spender) {
		if (herzen(ziel) > minHerzen.get() && ziel.gameMode() != GameType.SPECTATOR) {
			return 0;
		}
		if (spender != null) {
			// Spieler zahlen mit einem Herz-Item aus dem Inventar
			if (!herzItemAbziehen(spender)) {
				spender.sendSystemMessage(Component.literal("[Lifesteal] Du brauchst ein Herz-Item ("
					+ herzItem.get() + ") dafuer.").withStyle(ChatFormatting.RED));
				return 0;
			}
		}
		setzeHerzen(ziel, minHerzen.get() + 1);
		ziel.setGameMode(GameType.SURVIVAL);
		Broadcast.titelAlle(server, Component.literal(ziel.getName().getString() + " ist zurueck!")
			.withStyle(ChatFormatting.GOLD), null);
		return 1;
	}

	private boolean herzItemAbziehen(ServerPlayer sp) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(herzItem.get()));
		if (item == Items.AIR) {
			return false;
		}
		for (int slot = 0; slot < sp.getInventory().getContainerSize(); slot++) {
			ItemStack stack = sp.getInventory().getItem(slot);
			if (stack.is(item)) {
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}
}
