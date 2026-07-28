package com.horsti.graves;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.JsonSpeicher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class GravesMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("graves", true);
	private final IntSetting protectMinutes = settings.add(new IntSetting("protectMinutes", "owner-only protection in minutes, 0 = open immediately", 15, 0, 120));
	private final IntSetting xpKept = settings.add(new IntSetting("xpKept", "percent of your XP stored in the grave", 100, 0, 100));
	private final BoolSetting announce = settings.add(new BoolSetting("announce", "show the grave coordinates on death", true));

	private final JsonSpeicher storage = new JsonSpeicher("graves");
	private JsonObject data;

	@Override
	public void onInitialize() {
		new HorstiMod("graves", "Graves", settings)
			.extra((root, ctx) -> root.then(Commands.literal("list").executes(c -> {
				JsonArray list = graves();
				if (list.isEmpty()) {
					c.getSource().sendSuccess(() -> Component.literal("[Graves] No active graves."), false);
					return 1;
				}
				for (int i = 0; i < list.size(); i++) {
					JsonObject g = list.get(i).getAsJsonObject();
					final String line = "  " + g.get("name").getAsString() + " — "
						+ g.get("x").getAsInt() + " " + g.get("y").getAsInt() + " " + g.get("z").getAsInt();
					c.getSource().sendSuccess(() -> Component.literal(line).withStyle(ChatFormatting.GRAY), false);
				}
				return 1;
			})))
			.registrieren();

		data = storage.laden();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer sp)) {
				return;
			}
			createGrave(sp);
		});

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide() || !settings.istAktiv() || !(player instanceof ServerPlayer sp)) {
				return InteractionResult.PASS;
			}
			JsonObject grave = graveAt(hit.getBlockPos());
			if (grave == null) {
				return InteractionResult.PASS;
			}
			boolean isOwner = grave.get("owner").getAsString().equals(sp.getUUID().toString());
			long ageMinutes = (System.currentTimeMillis() - grave.get("time").getAsLong()) / 60000L;
			if (!isOwner && protectMinutes.get() > 0 && ageMinutes < protectMinutes.get()) {
				Broadcast.actionbar(sp, Component.literal("The grave of " + grave.get("name").getAsString()
					+ " stays protected for another " + (protectMinutes.get() - ageMinutes) + " min.").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			if (isOwner && grave.has("xp")) {
				int xp = grave.get("xp").getAsInt();
				if (xp > 0) {
					sp.giveExperiencePoints(xp);
					grave.addProperty("xp", 0);
					storage.speichern(data);
				}
			}
			return InteractionResult.PASS; // vanilla opens the chest
		});
	}

	private JsonArray graves() {
		if (!data.has("graves")) {
			data.add("graves", new JsonArray());
		}
		return data.getAsJsonArray("graves");
	}

	private JsonObject graveAt(BlockPos pos) {
		for (var element : graves()) {
			JsonObject g = element.getAsJsonObject();
			if (g.get("x").getAsInt() == pos.getX() && g.get("z").getAsInt() == pos.getZ()
				&& Math.abs(g.get("y").getAsInt() - pos.getY()) <= 1) {
				return g;
			}
		}
		return null;
	}

	private void createGrave(ServerPlayer sp) {
		ServerLevel level = (ServerLevel) sp.level();
		Inventory inv = sp.getInventory();

		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				items.add(stack.copy());
				inv.setItem(i, ItemStack.EMPTY);
			}
		}
		if (items.isEmpty()) {
			return;
		}

		BlockPos pos = safePosition(level, sp.blockPosition());
		// Two stacked chests hold 54 slots — more than the player inventory has.
		int stored = fill(level, pos, items, 0);
		if (stored < items.size()) {
			stored += fill(level, pos.above(), items, stored);
		}
		for (int i = stored; i < items.size(); i++) {
			sp.drop(items.get(i), false); // fallback: the rest drops like in vanilla
		}

		JsonObject grave = new JsonObject();
		grave.addProperty("owner", sp.getUUID().toString());
		grave.addProperty("name", sp.getName().getString());
		grave.addProperty("x", pos.getX());
		grave.addProperty("y", pos.getY());
		grave.addProperty("z", pos.getZ());
		grave.addProperty("time", System.currentTimeMillis());
		grave.addProperty("xp", sp.totalExperience * xpKept.get() / 100);
		graves().add(grave);
		storage.speichern(data);

		if (announce.get()) {
			sp.sendSystemMessage(Component.literal("⚰ Your grave: " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
				.withStyle(ChatFormatting.GOLD));
		}
	}

	/** Puts items from index start into a chest; returns the new index. */
	private int fill(ServerLevel level, BlockPos pos, List<ItemStack> items, int start) {
		level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof Container container)) {
			return start;
		}
		int index = start;
		for (int slot = 0; slot < container.getContainerSize() && index < items.size(); slot++, index++) {
			container.setItem(slot, items.get(index));
		}
		return index;
	}

	/** Avoid the void and lava: walk upwards to the first free, solid-enough spot. */
	private BlockPos safePosition(ServerLevel level, BlockPos start) {
		BlockPos pos = start;
		if (pos.getY() < level.getMinY() + 2) {
			pos = new BlockPos(pos.getX(), level.getMinY() + 2, pos.getZ());
		}
		for (int i = 0; i < 12; i++) {
			if (level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced()) {
				return pos;
			}
			pos = pos.above();
		}
		return pos;
	}
}
