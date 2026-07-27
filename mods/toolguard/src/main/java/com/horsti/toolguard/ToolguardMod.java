package com.horsti.toolguard;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

/**
 * Breakage protection: the mixin asks here whether one more point of damage is
 * allowed. All rules live in this class so the mixin stays tiny — and so API
 * drift only ever breaks one place.
 */
public class ToolguardMod implements ModInitializer {
	private static ModSettings settings;
	private static IntSetting threshold;
	private static IntSetting warnFrom;
	private static BoolSetting enchantedOnly;
	private static BoolSetting armor;
	private static BoolSetting sound;

	@Override
	public void onInitialize() {
		settings = new ModSettings("toolguard", true);
		threshold = settings.add(new IntSetting("threshold", "remaining durability at which protection kicks in", 5, 1, 50));
		warnFrom = settings.add(new IntSetting("warnFrom", "warn (without blocking) from this durability", 50, 0, 200));
		enchantedOnly = settings.add(new BoolSetting("enchantedOnly", "protect enchanted items only", false));
		armor = settings.add(new BoolSetting("armor", "protect armour too", true));
		sound = settings.add(new BoolSetting("sound", "play a warning sound", true));
		new HorstiMod("toolguard", "Toolguard", settings).registrieren();
	}

	/** Durability left after this damage. */
	private static int remainingAfter(ItemStack stack, int damage) {
		return stack.getMaxDamage() - stack.getDamageValue() - damage;
	}

	private static boolean isProtected(ItemStack stack) {
		if (settings == null || !settings.istAktiv() || !stack.isDamageableItem()) {
			return false;
		}
		return !enchantedOnly.get() || stack.isEnchanted();
	}

	/**
	 * Called by the mixin: may this item take damage right now?
	 * false = the damage is discarded and the item survives.
	 */
	public static boolean mayTakeDamage(ItemStack stack, int damage, ServerPlayer player) {
		if (!isProtected(stack)) {
			return true;
		}
		int remaining = remainingAfter(stack, damage);
		if (remaining > threshold.get()) {
			warnIfLow(stack, remaining, player);
			return true;
		}
		if (player != null) {
			Broadcast.actionbar(player, Component.literal("⚠ " + stack.getHoverName().getString()
				+ " is about to break — repair it!").withStyle(ChatFormatting.RED));
			if (sound.get()) {
				player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND,
					SoundSource.PLAYERS, 0.3f, 1.8f);
			}
		}
		return false;
	}

	private static void warnIfLow(ItemStack stack, int remaining, ServerPlayer player) {
		if (player == null || warnFrom.get() <= 0 || remaining > warnFrom.get()) {
			return;
		}
		// Only report on round thresholds so the action bar does not flicker.
		if (remaining % 10 != 0) {
			return;
		}
		Broadcast.actionbar(player, Component.literal(stack.getHoverName().getString()
			+ ": " + remaining + " durability left").withStyle(ChatFormatting.YELLOW));
	}

	public static boolean armorProtected() {
		return settings != null && settings.istAktiv() && armor.get();
	}
}
