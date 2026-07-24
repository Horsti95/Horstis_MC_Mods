package com.horsti.core;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HorstiCore implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			// /<mod> fuer jeden angemeldeten Mod
			for (HorstiMod mod : HorstiRegistry.alle()) {
				HorstiCommands.registrieren(dispatcher, ctx, mod);
			}

			// /horsti = Uebersicht
			dispatcher.register(Commands.literal("horsti")
				.requires(Commands.hasPermission(HorstiCommands.OP_LEVEL))
				.executes(c -> {
					MutableComponent msg = Component.literal("Horstis Mods:").withStyle(ChatFormatting.GOLD);
					for (HorstiMod mod : HorstiRegistry.alle()) {
						boolean an = mod.settings.istAktiv();
						msg.append(Component.literal("\n  " + mod.anzeigeName + " ").withStyle(ChatFormatting.WHITE))
							.append(Component.literal(an ? "[an]" : "[aus]")
								.withStyle(an ? ChatFormatting.GREEN : ChatFormatting.RED))
							.append(Component.literal("  → /" + mod.commandName).withStyle(ChatFormatting.GRAY));
					}
					final Component fertig = msg;
					c.getSource().sendSuccess(() -> fertig, false);
					return 1;
				}));
		});
	}
}
