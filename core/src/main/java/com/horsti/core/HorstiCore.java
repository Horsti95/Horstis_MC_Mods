package com.horsti.core;

import com.horsti.core.net.HudPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class HorstiCore implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(HorstiServer::setzen);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> HorstiServer.setzen(null));

		// HUD-Kanal: muss auf beiden Seiten angemeldet sein. Auf einem Vanilla-Client
		// passiert nichts weiter — er meldet den Kanal schlicht nicht an, und der Server
		// schickt dann Actionbar-Text (siehe Broadcast#hud).
		PayloadTypeRegistry.clientboundPlay().register(HudPayload.TYPE, HudPayload.CODEC);

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
