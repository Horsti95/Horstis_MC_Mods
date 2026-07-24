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
	// Runden-Twist: startet bewusst deaktiviert und wird per /keepmoving on scharf geschaltet.
	private final ModSettings settings = new ModSettings("keepmoving", false);
	private final IntSetting karenzSek = settings.add(new IntSetting("karenzSek", "Stillstand bis zum Malus", 10, 3, 120));
	private final EnumSetting modus = settings.add(new EnumSetting("modus", "Malus-Art", "schaden", "schaden", "hunger", "wither"));
	private final IntSetting staerke = settings.add(new IntSetting("staerke", "Malus-Staerke pro Sekunde", 1, 1, 5));
	private final BoolSetting warnung = settings.add(new BoolSetting("warnung", "Actionbar-Countdown", true));

	private static final class Stand {
		double x, y, z;
		int stillSekunden = 0;
	}

	private final Map<UUID, Stand> staende = new HashMap<>();

	@Override
	public void onInitialize() {
		new HorstiMod("keepmoving", "Keepmoving", settings)
			.onToggle(staende::clear)
			.registrieren();

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private void sekundenTick(MinecraftServer server) {
		if (!settings.istAktiv()) {
			return;
		}
		for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
			if (sp.isCreative() || sp.isSpectator() || sp.isDeadOrDying()) {
				staende.remove(sp.getUUID());
				continue;
			}
			Stand stand = staende.computeIfAbsent(sp.getUUID(), u -> new Stand());
			boolean bewegt = Math.abs(sp.getX() - stand.x) > 0.05
				|| Math.abs(sp.getY() - stand.y) > 0.05
				|| Math.abs(sp.getZ() - stand.z) > 0.05;
			stand.x = sp.getX();
			stand.y = sp.getY();
			stand.z = sp.getZ();

			if (bewegt) {
				stand.stillSekunden = 0;
				continue;
			}
			stand.stillSekunden++;
			int rest = karenzSek.get() - stand.stillSekunden;
			if (rest > 0) {
				if (warnung.get() && rest <= 5) {
					Broadcast.actionbar(sp, Component.literal("Beweg dich! " + rest + "…").withStyle(ChatFormatting.RED));
				}
				continue;
			}
			switch (modus.get()) {
				case "schaden" -> sp.hurtServer(sp.serverLevel(), sp.damageSources().generic(), staerke.get());
				case "hunger" -> sp.getFoodData().addExhaustion(staerke.get() * 4.0f);
				case "wither" -> sp.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, staerke.get() - 1));
			}
			if (warnung.get()) {
				Broadcast.actionbar(sp, Component.literal("Wer rastet, der rostet!").withStyle(ChatFormatting.DARK_RED));
			}
		}
	}
}
