package com.horsti.deathpoint;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class DeathpointMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("deathpoint", true);
	private final BoolSetting broadcast = settings.add(new BoolSetting("broadcast", "show the coordinates to everyone", false));
	private final BoolSetting dimension = settings.add(new BoolSetting("dimension", "include the dimension", true));

	@Override
	public void onInitialize() {
		new HorstiMod("deathpoint", "Deathpoint", settings).registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer sp)) {
				return;
			}
			String coordinates = sp.getBlockX() + " " + sp.getBlockY() + " " + sp.getBlockZ();
			MutableComponent msg = Component.literal("☠ ").withStyle(ChatFormatting.RED)
				.append(Component.literal(sp.getName().getString() + " died at ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(coordinates)
					.withStyle(style -> style.withColor(ChatFormatting.YELLOW)
						.withClickEvent(new ClickEvent.CopyToClipboard(coordinates))));
			if (dimension.get()) {
				msg.append(Component.literal(" (" + dimensionName(sp.level()) + ")").withStyle(ChatFormatting.GRAY));
			}
			if (broadcast.get()) {
				Broadcast.chat(HorstiServer.get(), msg);
			} else {
				sp.sendSystemMessage(msg);
			}
		});
	}

	private static String dimensionName(Level level) {
		if (level.dimension() == Level.NETHER) {
			return "Nether";
		}
		if (level.dimension() == Level.END) {
			return "End";
		}
		if (level.dimension() == Level.OVERWORLD) {
			return "Overworld";
		}
		return "another dimension";
	}
}
