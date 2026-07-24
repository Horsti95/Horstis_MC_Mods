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
	private final EnumSetting bereich = settings.add(new EnumSetting("bereich", "wo Totems zaehlen", "inventar", "inventar", "hotbar"));
	private final IntSetting cooldownSek = settings.add(new IntSetting("cooldownSek", "Sperrzeit nach Ausloesung, 0 = aus", 0, 0, 600));
	private final BoolSetting ansage = settings.add(new BoolSetting("ansage", "Actionbar-Meldung", true));

	private final Map<UUID, Long> letzteRettung = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("totem", "Totem", settings).registrieren();

		// Feuert nur, wenn der Vanilla-Totem-Check (Haende) schon fehlgeschlagen ist.
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, betrag) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer sp)) {
				return true;
			}
			if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				return true; // /kill und Void retten wie in Vanilla nicht
			}
			long jetzt = sp.level().getGameTime();
			Long letzte = letzteRettung.get(sp.getUUID());
			if (cooldownSek.get() > 0 && letzte != null && jetzt - letzte < cooldownSek.get() * 20L) {
				return true;
			}
			if (!totemVerbrauchen(sp)) {
				return true;
			}
			letzteRettung.put(sp.getUUID(), jetzt);
			sp.setHealth(1.0f);
			sp.removeAllEffects();
			sp.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
			sp.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
			sp.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
			sp.level().broadcastEntityEvent(sp, (byte) 35); // Vanilla-Totem-Animation + Sound
			if (ansage.get()) {
				Broadcast.actionbar(sp, Component.literal("Totem aus dem Inventar!").withStyle(ChatFormatting.GOLD));
			}
			return false;
		});
	}

	private boolean totemVerbrauchen(ServerPlayer sp) {
		Inventory inv = sp.getInventory();
		int grenze = bereich.get().equals("hotbar") ? 9 : inv.getContainerSize();
		for (int slot = 0; slot < grenze; slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.is(Items.TOTEM_OF_UNDYING)) {
				stack.shrink(1);
				return true;
			}
		}
		return false;
	}
}
