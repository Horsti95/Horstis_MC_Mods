package com.horsti.totem;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TotemMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("totem", true);
	private final EnumSetting scope = settings.add(new EnumSetting("scope", "where totems count", "inventory", "inventory", "hotbar"));
	private final IntSetting cooldownSeconds = settings.add(new IntSetting("cooldownSeconds", "lockout after a save, 0 = off", 0, 0, 600));
	private final BoolSetting announce = settings.add(new BoolSetting("announce", "action bar message", true));

	private final Map<UUID, Long> lastSave = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("totem", "Totem", settings).registrieren();

		// Only fires once the vanilla totem check (the hands) has already failed.
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer sp)) {
				return true;
			}
			if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				return true; // /kill and the void are not survivable, same as vanilla
			}
			long now = sp.level().getGameTime();
			Long last = lastSave.get(sp.getUUID());
			if (cooldownSeconds.get() > 0 && last != null && now - last < cooldownSeconds.get() * 20L) {
				return true;
			}
			if (!consumeTotem(sp)) {
				return true;
			}
			lastSave.put(sp.getUUID(), now);
			sp.setHealth(1.0f);
			sp.removeAllEffects();
			sp.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
			sp.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
			sp.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
			sp.level().broadcastEntityEvent(sp, (byte) 35); // vanilla totem animation + sound
			if (announce.get()) {
				Broadcast.actionbar(sp, Component.literal("Totem from your inventory!").withStyle(ChatFormatting.GOLD));
			}
			return false;
		});
	}

	private boolean consumeTotem(ServerPlayer sp) {
		Inventory inv = sp.getInventory();
		int limit = scope.get().equals("hotbar") ? 9 : inv.getContainerSize();
		for (int slot = 0; slot < limit; slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.is(Items.TOTEM_OF_UNDYING)) {
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}
}
