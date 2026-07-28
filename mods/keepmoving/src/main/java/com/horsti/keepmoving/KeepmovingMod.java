package com.horsti.keepmoving;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeepmovingMod implements ModInitializer {
	// Round twist: deliberately starts disabled and is armed with /keepmoving on.
	private final ModSettings settings = new ModSettings("keepmoving", false);
	private final IntSetting graceSeconds = settings.add(new IntSetting("graceSeconds", "standing still before the penalty", 10, 3, 120));
	private final EnumSetting mode = settings.add(new EnumSetting("mode", "kind of penalty", "damage", "damage", "hunger", "wither"));
	private final IntSetting strength = settings.add(new IntSetting("strength", "penalty strength per second", 1, 1, 5));
	private final BoolSetting warning = settings.add(new BoolSetting("warning", "action bar countdown", true));

	private static final class Spot {
		double x, y, z;
		int stillSeconds = 0;
	}

	private final Map<UUID, Spot> spots = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("keepmoving", "Keepmoving", settings)
			.onToggle(spots::clear)
			.registrieren();

		Ticker.alleTicks(20, this::secondTick);
	}

	private void secondTick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (sp.isCreative() || sp.isSpectator() || sp.isDeadOrDying()) {
				spots.remove(sp.getUUID());
				continue;
			}
			Spot spot = spots.computeIfAbsent(sp.getUUID(), u -> new Spot());
			boolean moved = Math.abs(sp.getX() - spot.x) > 0.05
				|| Math.abs(sp.getY() - spot.y) > 0.05
				|| Math.abs(sp.getZ() - spot.z) > 0.05;
			spot.x = sp.getX();
			spot.y = sp.getY();
			spot.z = sp.getZ();

			if (moved) {
				spot.stillSeconds = 0;
				continue;
			}
			spot.stillSeconds++;
			int left = graceSeconds.get() - spot.stillSeconds;
			if (left > 0) {
				if (warning.get() && left <= 5) {
					Broadcast.actionbar(sp, Component.literal("Move! " + left + "…").withStyle(ChatFormatting.RED));
				}
				continue;
			}
			switch (mode.get()) {
				case "damage" -> sp.hurtServer((net.minecraft.server.level.ServerLevel) sp.level(), sp.damageSources().generic(), strength.get());
				case "hunger" -> sp.getFoodData().addExhaustion(strength.get() * 4.0f);
				case "wither" -> sp.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, strength.get() - 1));
			}
			if (warning.get()) {
				Broadcast.actionbar(sp, Component.literal("Rest and you rust!").withStyle(ChatFormatting.DARK_RED));
			}
		}
	}
}
