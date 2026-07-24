package com.horsti.core;

import com.horsti.core.settings.ModSettings;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

/**
 * Anmeldung eines Horsti-Mods bei core: liefert Command-Name, Settings und
 * optionale Zusatz-Subcommands. Aus einer Instanz generiert core den kompletten
 * /<mod>-Baum und den Eintrag in /horsti.
 */
public class HorstiMod {
	public final String commandName;
	public final String anzeigeName;
	public final ModSettings settings;
	private ExtraCommands extra;
	private Runnable onToggle;

	public interface ExtraCommands {
		void anhaengen(LiteralArgumentBuilder<CommandSourceStack> root, CommandBuildContext ctx);
	}

	public HorstiMod(String commandName, String anzeigeName, ModSettings settings) {
		this.commandName = commandName;
		this.anzeigeName = anzeigeName;
		this.settings = settings;
	}

	/** Zusatz-Subcommands wie /tag start (optional). */
	public HorstiMod extra(ExtraCommands extra) {
		this.extra = extra;
		return this;
	}

	/** Wird nach jedem on/off-Wechsel gerufen (Aufraeumen bei off). */
	public HorstiMod onToggle(Runnable r) {
		this.onToggle = r;
		return this;
	}

	public ExtraCommands extraCommands() {
		return extra;
	}

	public void toggled() {
		if (onToggle != null) {
			onToggle.run();
		}
	}

	/** Settings laden und Mod bei /horsti + Command-Registrierung anmelden. */
	public HorstiMod registrieren() {
		settings.laden();
		HorstiRegistry.anmelden(this);
		return this;
	}
}
