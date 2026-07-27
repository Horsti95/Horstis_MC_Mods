package com.horsti.refill;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Refills the main-hand stack from the inventory the moment it runs out.
 *
 * <p>Deliberately mixin-free. Hooking every place/use path would mean hooking a lot of
 * methods that Mojang keeps rearranging. Instead the mod remembers, per player, the item
 * that was down to its last unit. If the hand is empty on the next tick, that was the
 * moment it got consumed — and that is exactly when we put the next stack in.
 */
public class RefillMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("refill", true);
	private final BoolSetting blocks = settings.add(new BoolSetting("blocks", "refill placeable blocks", true));
	private final BoolSetting food = settings.add(new BoolSetting("food", "refill food", true));
	private final BoolSetting tools = settings.add(new BoolSetting("tools", "swap in an identical tool when one breaks", false));
	private final BoolSetting hotbarOnly = settings.add(new BoolSetting("hotbarOnly", "only pull from the hotbar", false));

	/** Per player: the item that was last held as its final unit. */
	private final Map<UUID, Item> lastSingle = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("refill", "Refill", settings)
			.onToggle(lastSingle::clear)
			.registrieren();

		Ticker.alleTicks(1, server -> {
			if (!settings.istAktiv()) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tick(player);
			}
		});
	}

	private void tick(ServerPlayer player) {
		ItemStack held = player.getMainHandItem();
		UUID id = player.getUUID();

		if (held.isEmpty()) {
			Item used = lastSingle.remove(id);
			// With a container open the hand can empty because of dragging, not because of use.
			if (used != null && player.containerMenu == player.inventoryMenu) {
				refill(player, used);
			}
			return;
		}

		// Only the final unit matters — remembering anything above that would be wasted work.
		if (held.getCount() == 1) {
			lastSingle.put(id, held.getItem());
		} else {
			lastSingle.remove(id);
		}
	}

	/** Whether the settings allow this item to be refilled. */
	private boolean allowed(Item item) {
		ItemStack sample = new ItemStack(item);
		if (sample.isDamageableItem()) {
			return tools.get();
		}
		if (item instanceof BlockItem) {
			return blocks.get();
		}
		if (sample.has(DataComponents.FOOD)) {
			return food.get();
		}
		// Arrows, ender pearls, buckets … everything else stackable stays on.
		return true;
	}

	private void refill(ServerPlayer player, Item item) {
		if (!allowed(item)) {
			return;
		}
		Inventory inv = player.getInventory();
		int limit = hotbarOnly.get() ? Math.min(9, inv.getContainerSize()) : inv.getContainerSize();
		for (int slot = 0; slot < limit; slot++) {
			ItemStack candidate = inv.getItem(slot);
			if (candidate.isEmpty() || !candidate.is(item)) {
				continue;
			}
			inv.setItem(slot, ItemStack.EMPTY);
			player.setItemInHand(InteractionHand.MAIN_HAND, candidate);
			if (candidate.isDamageableItem()) {
				// Worth saying out loud for tools — otherwise nobody notices the swap.
				Broadcast.actionbar(player, Component.literal("↻ swapped in " + candidate.getHoverName().getString())
					.withStyle(ChatFormatting.GRAY));
			}
			return;
		}
	}
}
