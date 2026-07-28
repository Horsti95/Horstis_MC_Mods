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
	private static final Identifier HEART_ID = Attribute.id("lifesteal", "hearts");

	private final ModSettings settings = new ModSettings("lifesteal", true);
	private final IntSetting startHearts = settings.add(new IntSetting("startHearts", "starting maximum in hearts", 10, 5, 30));
	private final IntSetting maxHearts = settings.add(new IntSetting("maxHearts", "upper limit in hearts", 20, 10, 40));
	private final IntSetting minHearts = settings.add(new IntSetting("minHearts", "elimination threshold", 0, 0, 5));
	private final EnumSetting naturalDeath = settings.add(new EnumSetting("naturalDeath", "does a non-PvP death cost a heart?", "free", "free", "heart"));
	private final StringSetting heartItem = settings.add(new StringSetting("heartItem", "item that counts as a heart", "minecraft:nether_star"));

	private final com.horsti.core.util.JsonSpeicher storage = new com.horsti.core.util.JsonSpeicher("lifesteal");
	private JsonObject data;

	@Override
	public void onInitialize() {
		new HorstiMod("lifesteal", "Lifesteal", settings)
			.onToggle(this::refreshAll)
			.extra((root, ctx) -> {
				root.then(Commands.literal("revive")
					.then(Commands.argument("player", EntityArgument.player()).executes(c ->
						revive(c.getSource().getServer(), EntityArgument.getPlayer(c, "player"), null))));
				// "hearts", not "set": core already generates a /lifesteal set <param> branch.
				root.then(Commands.literal("hearts")
					.then(Commands.argument("player", EntityArgument.player())
						.then(Commands.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 40))
							.executes(c -> {
								ServerPlayer sp = EntityArgument.getPlayer(c, "player");
								setHearts(sp, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(c, "amount"));
								c.getSource().sendSuccess(() -> Component.literal("[Lifesteal] " + sp.getName().getString()
									+ " now has " + hearts(sp) + " hearts"), true);
								return 1;
							}))));
			})
			.registrieren();
		maxHearts.onChange(v -> refreshAll());

		data = storage.laden();

		// Player commands: your own standing + reviving someone for a heart item
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			dispatcher.register(Commands.literal("hearts").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				sp.sendSystemMessage(Component.literal("You have " + hearts(sp) + " hearts (max " + maxHearts.get() + ").")
					.withStyle(ChatFormatting.GOLD));
				return 1;
			}));
			dispatcher.register(Commands.literal("revive")
				.then(Commands.argument("player", EntityArgument.player()).executes(c ->
					revive(c.getSource().getServer(), EntityArgument.getPlayer(c, "player"),
						c.getSource().getPlayerOrException()))));
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer sp = handler.getPlayer();
			if (!settings.istAktiv()) {
				return;
			}
			if (!data.has(sp.getUUID().toString())) {
				setHearts(sp, startHearts.get());
			} else {
				apply(sp);
			}
			if (hearts(sp) <= minHearts.get()) {
				sp.setGameMode(GameType.SPECTATOR);
			}
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((old, fresh, alive) -> {
			if (!settings.istAktiv()) {
				return;
			}
			apply(fresh);
			if (hearts(fresh) <= minHearts.get()) {
				fresh.setGameMode(GameType.SPECTATOR);
			}
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer victim)) {
				return;
			}
			MinecraftServer server = HorstiServer.get();
			boolean pvp = source.getEntity() instanceof ServerPlayer killer && !killer.getUUID().equals(victim.getUUID());
			if (pvp) {
				ServerPlayer killer = (ServerPlayer) source.getEntity();
				change(victim, -1);
				change(killer, +1);
				Broadcast.chat(server, Component.literal(killer.getName().getString() + " steals a heart from "
					+ victim.getName().getString() + "! (" + hearts(killer) + " / " + hearts(victim) + ")")
					.withStyle(ChatFormatting.RED));
			} else if (naturalDeath.get().equals("heart")) {
				change(victim, -1);
			}
			if (hearts(victim) <= minHearts.get()) {
				eliminate(server, victim);
			}
		});
	}

	private int hearts(ServerPlayer sp) {
		return data.has(sp.getUUID().toString())
			? data.get(sp.getUUID().toString()).getAsInt()
			: startHearts.get();
	}

	private void setHearts(ServerPlayer sp, int value) {
		data.addProperty(sp.getUUID().toString(), Math.max(0, Math.min(maxHearts.get(), value)));
		storage.speichern(data);
		apply(sp);
	}

	private void change(ServerPlayer sp, int delta) {
		setHearts(sp, hearts(sp) + delta);
	}

	private void apply(ServerPlayer sp) {
		if (!settings.istAktiv()) {
			Attribute.entfernen(sp, Attributes.MAX_HEALTH, HEART_ID);
			return;
		}
		// The vanilla base is 20 HP = 10 hearts; we modify the difference.
		double goal = hearts(sp) * 2.0;
		Attribute.addieren(sp, Attributes.MAX_HEALTH, HEART_ID, goal - 20.0);
		if (sp.getHealth() > sp.getMaxHealth()) {
			sp.setHealth(sp.getMaxHealth());
		}
	}

	private void refreshAll() {
		MinecraftServer server = HorstiServer.get();
		if (server != null) {
			server.getPlayerList().getPlayers().forEach(this::apply);
		}
	}

	private void eliminate(MinecraftServer server, ServerPlayer sp) {
		sp.setGameMode(GameType.SPECTATOR);
		Broadcast.titelAlle(server, Component.literal(sp.getName().getString() + " is out!")
			.withStyle(ChatFormatting.DARK_RED), Component.literal("Revive with: /revive " + sp.getName().getString()));
	}

	private int revive(MinecraftServer server, ServerPlayer target, ServerPlayer donor) {
		if (hearts(target) > minHearts.get() && target.gameMode() != GameType.SPECTATOR) {
			return 0;
		}
		if (donor != null) {
			// Players pay with a heart item from their inventory
			if (!takeHeartItem(donor)) {
				donor.sendSystemMessage(Component.literal("[Lifesteal] You need a heart item ("
					+ heartItem.get() + ") for that.").withStyle(ChatFormatting.RED));
				return 0;
			}
		}
		setHearts(target, minHearts.get() + 1);
		target.setGameMode(GameType.SURVIVAL);
		Broadcast.titelAlle(server, Component.literal(target.getName().getString() + " is back!")
			.withStyle(ChatFormatting.GOLD), null);
		return 1;
	}

	private boolean takeHeartItem(ServerPlayer sp) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(heartItem.get()));
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
