package com.horsti.todesort;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class TodesortMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("todesort", true);
	private final BoolSetting oeffentlich = settings.add(new BoolSetting("oeffentlich", "Koordinaten fuer alle sichtbar", false));
	private final BoolSetting dimension = settings.add(new BoolSetting("dimension", "Dimension mit anzeigen", true));

	@Override
	public void onInitialize() {
		new HorstiMod("todesort", "Todesort", settings).registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv() || !(entity instanceof ServerPlayer sp)) {
				return;
			}
			String koordinaten = sp.getBlockX() + " " + sp.getBlockY() + " " + sp.getBlockZ();
			MutableComponent msg = Component.literal("☠ ").withStyle(ChatFormatting.RED)
				.append(Component.literal(sp.getName().getString() + " starb bei ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(koordinaten)
					.withStyle(style -> style.withColor(ChatFormatting.YELLOW)
						.withClickEvent(new ClickEvent.CopyToClipboard(koordinaten))));
			if (dimension.get()) {
				msg.append(Component.literal(" (" + dimensionsName(sp.level()) + ")").withStyle(ChatFormatting.GRAY));
			}
			if (oeffentlich.get()) {
				Broadcast.chat(sp.getServer(), msg);
			} else {
				sp.sendSystemMessage(msg);
			}
		});
	}

	private static String dimensionsName(Level level) {
		ResourceLocation id = level.dimension().location();
		return switch (id.toString()) {
			case "minecraft:overworld" -> "Oberwelt";
			case "minecraft:the_nether" -> "Nether";
			case "minecraft:the_end" -> "Ende";
			default -> id.getPath();
		};
	}
}
