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
	private final IntSetting schutzMin = settings.add(new IntSetting("schutzMin", "Nur-Besitzer-Schutzzeit in Minuten, 0 = sofort offen", 15, 0, 120));
	private final IntSetting xpErhalt = settings.add(new IntSetting("xpErhalt", "Prozent der XP im Grab", 100, 0, 100));
	private final BoolSetting ansage = settings.add(new BoolSetting("ansage", "Todesort-Koordinaten beim Tod anzeigen", true));

	private final JsonSpeicher speicher = new JsonSpeicher("graves");
	private JsonObject daten;

	@Override
	public void onInitialize() {
		new HorstiMod("graves", "Graves", settings)
			.extra((root, ctx) -> root.then(Commands.literal("liste").executes(c -> {
				JsonArray liste = graeber();
				if (liste.isEmpty()) {
					c.getSource().sendSuccess(() -> Component.literal("[Graves] Keine aktiven Gräber."), false);
					return 1;
				}
				for (int i = 0; i < liste.size(); i++) {
					JsonObject g = liste.get(i).getAsJsonObject();
					final String zeile = "  " + g.get("name").getAsString() + " — "
						+ g.get("x").getAsInt() + " " + g.get("y").getAsInt() + " " + g.get("z").getAsInt();
					c.getSource().sendSuccess(() -> Component.literal(zeile).withStyle(ChatFormatting.GRAY), false);
				}
				return 1;
			})))
			.registrieren();

		daten = speicher.laden();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer sp)) {
				return;
			}
			grabAnlegen(sp);
		});

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide() || !settings.istAktiv() || !(player instanceof ServerPlayer sp)) {
				return InteractionResult.PASS;
			}
			JsonObject grab = grabBei(hit.getBlockPos());
			if (grab == null) {
				return InteractionResult.PASS;
			}
			boolean besitzer = grab.get("besitzer").getAsString().equals(sp.getUUID().toString());
			long alterMin = (System.currentTimeMillis() - grab.get("zeit").getAsLong()) / 60000L;
			if (!besitzer && schutzMin.get() > 0 && alterMin < schutzMin.get()) {
				Broadcast.actionbar(sp, Component.literal("Das Grab von " + grab.get("name").getAsString()
					+ " ist noch " + (schutzMin.get() - alterMin) + " Min. geschützt.").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			if (besitzer && grab.has("xp")) {
				int xp = grab.get("xp").getAsInt();
				if (xp > 0) {
					sp.giveExperiencePoints(xp);
					grab.addProperty("xp", 0);
					speicher.speichern(daten);
				}
			}
			return InteractionResult.PASS; // Vanilla oeffnet die Kiste
		});
	}

	private JsonArray graeber() {
		if (!daten.has("graeber")) {
			daten.add("graeber", new JsonArray());
		}
		return daten.getAsJsonArray("graeber");
	}

	private JsonObject grabBei(BlockPos pos) {
		for (var element : graeber()) {
			JsonObject g = element.getAsJsonObject();
			if (g.get("x").getAsInt() == pos.getX() && g.get("z").getAsInt() == pos.getZ()
				&& Math.abs(g.get("y").getAsInt() - pos.getY()) <= 1) {
				return g;
			}
		}
		return null;
	}

	private void grabAnlegen(ServerPlayer sp) {
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

		BlockPos pos = sicherePosition(level, sp.blockPosition());
		// Zwei Kisten uebereinander fassen 54 Slots — mehr als das Spielerinventar hat.
		int abgelegt = fuellen(level, pos, items, 0);
		if (abgelegt < items.size()) {
			abgelegt += fuellen(level, pos.above(), items, abgelegt);
		}
		for (int i = abgelegt; i < items.size(); i++) {
			sp.drop(items.get(i), false); // Notfall: Rest droppt wie in Vanilla
		}

		JsonObject grab = new JsonObject();
		grab.addProperty("besitzer", sp.getUUID().toString());
		grab.addProperty("name", sp.getName().getString());
		grab.addProperty("x", pos.getX());
		grab.addProperty("y", pos.getY());
		grab.addProperty("z", pos.getZ());
		grab.addProperty("zeit", System.currentTimeMillis());
		grab.addProperty("xp", sp.totalExperience * xpErhalt.get() / 100);
		graeber().add(grab);
		speicher.speichern(daten);

		if (ansage.get()) {
			sp.sendSystemMessage(Component.literal("⚰ Dein Grab: " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
				.withStyle(ChatFormatting.GOLD));
		}
	}

	/** Legt Items ab Index start in eine Kiste; gibt den neuen Index zurueck. */
	private int fuellen(ServerLevel level, BlockPos pos, List<ItemStack> items, int start) {
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

	/** Void/Lava vermeiden: nach oben bis zu einer freien, tragfaehigen Stelle wandern. */
	private BlockPos sicherePosition(ServerLevel level, BlockPos start) {
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
