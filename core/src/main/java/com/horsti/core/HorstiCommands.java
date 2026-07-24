package com.horsti.core;

import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.Setting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** Generiert aus einem HorstiMod den kompletten /<mod>-Command-Baum. */
public final class HorstiCommands {
	public static final int OP_LEVEL = 2;

	private HorstiCommands() {
	}

	public static void registrieren(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, HorstiMod mod) {
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(mod.commandName)
			.requires(Commands.hasPermission(OP_LEVEL))
			.executes(c -> status(c.getSource(), mod));

		root.then(Commands.literal("on").executes(c -> setzeAktiv(c.getSource(), mod, true)));
		root.then(Commands.literal("off").executes(c -> setzeAktiv(c.getSource(), mod, false)));

		LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal("set");
		for (Setting<?> s : mod.settings.alle()) {
			if (s == mod.settings.aktiv) {
				continue; // on/off gibt es schon direkt am Root
			}
			LiteralArgumentBuilder<CommandSourceStack> param = Commands.literal(s.key);
			if (s instanceof IntSetting is) {
				param.then(Commands.argument("wert", IntegerArgumentType.integer(is.min, is.max))
					.executes(c -> setzeWert(c.getSource(), mod, s, String.valueOf(IntegerArgumentType.getInteger(c, "wert")))));
			} else if (!s.literalWerte().isEmpty()) {
				for (String literal : s.literalWerte()) {
					param.then(Commands.literal(literal)
						.executes(c -> setzeWert(c.getSource(), mod, s, literal)));
				}
			} else {
				continue; // Freitext-Settings bekommen mod-eigene Subcommands (extra)
			}
			set.then(param);
		}
		root.then(set);

		root.then(Commands.literal("reset").executes(c -> {
			mod.settings.resetAlle();
			c.getSource().sendSuccess(() -> praefix(mod).append("alle Werte auf Default zurueckgesetzt"), true);
			return 1;
		}));

		root.then(Commands.literal("reload").executes(c -> {
			mod.settings.laden();
			c.getSource().sendSuccess(() -> praefix(mod).append("Config neu geladen"), true);
			return 1;
		}));

		if (mod.extraCommands() != null) {
			mod.extraCommands().anhaengen(root, ctx);
		}

		dispatcher.register(root);
	}

	private static int status(CommandSourceStack src, HorstiMod mod) {
		MutableComponent msg = Component.literal(mod.anzeigeName + " ")
			.withStyle(ChatFormatting.GOLD)
			.append(Component.literal(mod.settings.istAktiv() ? "[an]" : "[aus]")
				.withStyle(mod.settings.istAktiv() ? ChatFormatting.GREEN : ChatFormatting.RED));
		for (Setting<?> s : mod.settings.alle()) {
			if (s == mod.settings.aktiv) {
				continue;
			}
			msg.append(Component.literal("\n  " + s.key + " = ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(s.formatted()).withStyle(ChatFormatting.WHITE))
				.append(Component.literal("  (" + s.beschreibung + ")").withStyle(ChatFormatting.DARK_GRAY));
		}
		final Component fertig = msg;
		src.sendSuccess(() -> fertig, false);
		return 1;
	}

	private static int setzeAktiv(CommandSourceStack src, HorstiMod mod, boolean an) {
		mod.settings.aktiv.set(an);
		mod.settings.speichern();
		mod.toggled();
		src.sendSuccess(() -> praefix(mod).append(an ? "aktiviert" : "deaktiviert"), true);
		return 1;
	}

	private static int setzeWert(CommandSourceStack src, HorstiMod mod, Setting<?> setting, String eingabe) {
		try {
			setzeGeneric(setting, eingabe);
		} catch (IllegalArgumentException e) {
			src.sendFailure(praefix(mod).append(setting.key + ": " + e.getMessage()));
			return 0;
		}
		mod.settings.speichern();
		src.sendSuccess(() -> praefix(mod).append(setting.key + " = " + setting.formatted()), true);
		return 1;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void setzeGeneric(Setting<?> setting, String eingabe) {
		((Setting) setting).set(setting.parse(eingabe));
	}

	static MutableComponent praefix(HorstiMod mod) {
		return Component.literal("[" + mod.anzeigeName + "] ").withStyle(ChatFormatting.GOLD);
	}
}
