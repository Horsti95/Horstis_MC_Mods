package com.horsti.giftburden;

import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.JsonSpeicher;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

public class GiftMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("gift", true);
	private final IntSetting strength = settings.add(new IntSetting("strength", "multiplier for all gifts and burdens", 1, 1, 3));
	private final EnumSetting rerollCost = settings.add(new EnumSetting("rerollCost", "player reroll price", "xp30", "xp30", "netherstar", "off"));
	private final BoolSetting announce = settings.add(new BoolSetting("announce", "title announcement on join", true));

	private final JsonSpeicher storage = new JsonSpeicher("gift");
	private JsonObject data;
	private final Random random = new Random();

	@Override
	public void onInitialize() {
		new HorstiMod("gift", "Gift & Burden", settings)
			.onToggle(this::refreshAll)
			.extra((root, ctx) -> root.then(Commands.literal("reroll")
				.then(Commands.argument("player", EntityArgument.player()).executes(c -> {
					ServerPlayer player = EntityArgument.getPlayer(c, "player");
					assign(player, true);
					c.getSource().sendSuccess(() -> Component.literal("[Gift] Rerolled for "
						+ player.getName().getString() + ": " + describe(player)), true);
					return 1;
				}))))
			.registrieren();
		strength.onChange(value -> refreshAll());

		data = storage.laden();

		// Player commands: view your own pair and reroll for the configured price
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			dispatcher.register(Commands.literal("mygift").executes(c -> {
				ServerPlayer player = c.getSource().getPlayerOrException();
				player.sendSystemMessage(Component.literal("You are: " + describe(player)).withStyle(ChatFormatting.GOLD));
				return 1;
			}));
			dispatcher.register(Commands.literal("giftreroll").executes(c ->
				selfReroll(c.getSource().getPlayerOrException())));
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();
			if (!settings.istAktiv()) {
				return;
			}
			if (!hasAssignment(player)) {
				assign(player, false);
			} else {
				apply(player);
			}
			if (announce.get()) {
				Broadcast.titel(player, Component.literal("You are:").withStyle(ChatFormatting.GRAY),
					Component.literal(describe(player)).withStyle(ChatFormatting.GOLD));
			}
		});

		// Reapply attributes after respawn (new entity instance)
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (settings.istAktiv()) {
				apply(newPlayer);
			}
		});

		// Tick-based burdens (hunger, hydrophobia)
		Ticker.alleTicks(40, this::tick);
	}

	/** The player rerolls and pays the configured price. */
	private int selfReroll(ServerPlayer player) {
		if (!settings.istAktiv()) {
			return 0;
		}
		switch (rerollCost.get()) {
			case "xp30" -> {
				if (player.experienceLevel < 30) {
					player.sendSystemMessage(Component.literal("[Gift] You need 30 XP levels for that.")
						.withStyle(ChatFormatting.RED));
					return 0;
				}
				player.giveExperienceLevels(-30);
			}
			case "netherstar" -> {
				if (!takeItem(player, Items.NETHER_STAR)) {
					player.sendSystemMessage(Component.literal("[Gift] You need a nether star for that.")
						.withStyle(ChatFormatting.RED));
					return 0;
				}
			}
			default -> {
				player.sendSystemMessage(Component.literal("[Gift] Self-reroll is disabled.")
					.withStyle(ChatFormatting.RED));
				return 0;
			}
		}
		assign(player, true);
		return 1;
	}

	private static boolean takeItem(ServerPlayer player, Item item) {
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			ItemStack stack = player.getInventory().getItem(slot);
			if (stack.is(item)) {
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}

	private String key(ServerPlayer player) {
		return player.getUUID().toString();
	}

	private boolean hasAssignment(ServerPlayer player) {
		return data.has(key(player));
	}

	private void assign(ServerPlayer player, boolean isReroll) {
		Trait.clearAll(player);
		JsonObject entry = new JsonObject();
		entry.addProperty("gift", Trait.GIFTS[random.nextInt(Trait.GIFTS.length)].id());
		entry.addProperty("burden", Trait.BURDENS[random.nextInt(Trait.BURDENS.length)].id());
		data.add(key(player), entry);
		storage.speichern(data);
		apply(player);
		if (isReroll) {
			Broadcast.titel(player, Component.literal("Rerolled!").withStyle(ChatFormatting.GOLD),
				Component.literal(describe(player)));
		}
	}

	private Trait gift(ServerPlayer player) {
		JsonObject entry = data.getAsJsonObject(key(player));
		return entry == null ? null : Trait.find(Trait.GIFTS, entry.get("gift").getAsString());
	}

	private Trait burden(ServerPlayer player) {
		JsonObject entry = data.getAsJsonObject(key(player));
		return entry == null ? null : Trait.find(Trait.BURDENS, entry.get("burden").getAsString());
	}

	private String describe(ServerPlayer player) {
		Trait g = gift(player);
		Trait b = burden(player);
		if (g == null || b == null) {
			return "nothing yet";
		}
		return g.name() + ", but " + b.name();
	}

	private void apply(ServerPlayer player) {
		Trait.clearAll(player);
		if (!settings.istAktiv()) {
			return;
		}
		Trait g = gift(player);
		Trait b = burden(player);
		if (g != null) {
			g.apply(player, strength.get());
		}
		if (b != null) {
			b.apply(player, strength.get());
		}
	}

	private void refreshAll() {
		MinecraftServer server = HorstiServer.get();
		if (server == null) {
			return;
		}
		server.getPlayerList().getPlayers().forEach(this::apply);
	}

	private void tick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			Trait b = burden(player);
			if (b == null) {
				continue;
			}
			switch (b.id()) {
				case "greedy" -> player.getFoodData().addExhaustion(0.5f * strength.get());
				case "hydrophobic" -> {
					if (player.isInWater()) {
						player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, strength.get() - 1, true, false));
					}
				}
				default -> {
				}
			}
		}
	}
}
