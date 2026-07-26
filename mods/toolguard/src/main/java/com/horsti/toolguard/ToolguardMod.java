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
 * Schutz vor dem Zerbrechen: der Mixin fragt hier nach, ob ein Schadenspunkt
 * noch erlaubt ist. Alle Regeln liegen in dieser Klasse, damit der Mixin
 * winzig bleibt (und bei API-Drift nur eine Stelle bricht).
 */
public class ToolguardMod implements ModInitializer {
	private static ModSettings settings;
	private static IntSetting schwelle;
	private static IntSetting warnungAb;
	private static BoolSetting nurVerzaubert;
	private static BoolSetting ruestung;
	private static BoolSetting sound;

	@Override
	public void onInitialize() {
		settings = new ModSettings("toolguard", true);
		schwelle = settings.add(new IntSetting("schwelle", "Rest-Haltbarkeit, ab der geschuetzt wird", 5, 1, 50));
		warnungAb = settings.add(new IntSetting("warnungAb", "ab dieser Rest-Haltbarkeit warnen", 50, 0, 200));
		nurVerzaubert = settings.add(new BoolSetting("nurVerzaubert", "nur verzauberte Gegenstaende schuetzen", false));
		ruestung = settings.add(new BoolSetting("ruestung", "Ruestung mitschuetzen", true));
		sound = settings.add(new BoolSetting("sound", "Warnton", true));
		new HorstiMod("toolguard", "Toolguard", settings).registrieren();
	}

	/** Rest-Haltbarkeit nach diesem Schaden. */
	private static int restNach(ItemStack stack, int schaden) {
		return stack.getMaxDamage() - stack.getDamageValue() - schaden;
	}

	private static boolean geschuetzt(ItemStack stack) {
		if (settings == null || !settings.istAktiv() || !stack.isDamageableItem()) {
			return false;
		}
		if (nurVerzaubert.get() && !stack.isEnchanted()) {
			return false;
		}
		return true;
	}

	/**
	 * Vom Mixin aufgerufen: darf dieser Gegenstand jetzt Schaden nehmen?
	 * false = Schaden wird verworfen, der Gegenstand ueberlebt.
	 */
	public static boolean darfSchadenNehmen(ItemStack stack, int schaden, ServerPlayer sp) {
		if (!geschuetzt(stack)) {
			return true;
		}
		int rest = restNach(stack, schaden);
		if (rest > schwelle.get()) {
			warnenFallsKnapp(stack, rest, sp);
			return true;
		}
		if (sp != null) {
			Broadcast.actionbar(sp, Component.literal("⚠ " + stack.getHoverName().getString()
				+ " ist am Ende — reparieren!").withStyle(ChatFormatting.RED));
			if (sound.get()) {
				sp.level().playSound(null, sp.blockPosition(), SoundEvents.ANVIL_LAND,
					SoundSource.PLAYERS, 0.3f, 1.8f);
			}
		}
		return false;
	}

	private static void warnenFallsKnapp(ItemStack stack, int rest, ServerPlayer sp) {
		if (sp == null || warnungAb.get() <= 0 || rest > warnungAb.get()) {
			return;
		}
		// Nur an runden Schwellen melden, damit die Actionbar nicht dauerflackert
		if (rest % 10 != 0) {
			return;
		}
		Broadcast.actionbar(sp, Component.literal(stack.getHoverName().getString()
			+ ": noch " + rest + " Haltbarkeit").withStyle(ChatFormatting.YELLOW));
	}

	public static boolean ruestungGeschuetzt() {
		return settings != null && settings.istAktiv() && ruestung.get();
	}
}
