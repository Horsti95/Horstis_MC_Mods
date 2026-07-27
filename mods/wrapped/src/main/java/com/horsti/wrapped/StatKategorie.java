package com.horsti.wrapped;

import net.minecraft.server.level.ServerPlayer;

/**
 * One recap category: title, the value to read, and how to format it.
 * A new category is a single line in Categories.all() — and other mods can
 * contribute their own via Categories.register(...) (see README, cross-mod section).
 */
public record StatKategorie(String id, String title, String description,
		Value value, Formatter formatter) {

	public interface Value {
		/** Raw lifetime counter for this player — WrappedMod computes the delta. */
		int read(ServerPlayer player);
	}

	public interface Formatter {
		String format(int value);
	}

	public String formatValue(int raw) {
		return formatter.format(raw);
	}
}
