package com.horsti.wrapped;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/** The built-in categories plus the registration hook for other mods. */
public final class Kategorien {
	private static final List<StatKategorie> REGISTERED = new ArrayList<>();

	private Kategorien() {
	}

	private static StatKategorie custom(String id, String title, String description,
			Identifier stat, StatKategorie.Formatter formatter) {
		return new StatKategorie(id, title, description,
			player -> player.getStats().getValue(Stats.CUSTOM.get(stat)), formatter);
	}

	/** Centimetres to blocks (vanilla counts movement in cm). */
	private static String blocks(int cm) {
		return String.format("%,d blocks", cm / 100);
	}

	private static String count(int n) {
		return String.format("%,d", n);
	}

	/** Ticks to hours and minutes. */
	private static String time(int ticks) {
		int minutes = ticks / 20 / 60;
		return minutes / 60 + " h " + minutes % 60 + " min";
	}

	/** Vanilla counts damage in tenths of a health point; 20 units = 1 heart. */
	private static String hearts(int units) {
		return String.format("%,.1f hearts", units / 20.0);
	}

	/** "Blocks mined" is not a custom stat but one per block type — so sum them up. */
	private static int minedBlocks(ServerPlayer player) {
		int sum = 0;
		for (Block block : BuiltInRegistries.BLOCK) {
			sum += player.getStats().getValue(Stats.BLOCK_MINED.get(block));
		}
		return sum;
	}

	public static List<StatKategorie> all() {
		List<StatKategorie> list = new ArrayList<>(List.of(
			custom("wanderer", "🥾 Wanderer", "most blocks walked",
				Stats.WALK_ONE_CM, Kategorien::blocks),
			new StatKategorie("miner", "⛏️ Miner", "most blocks mined",
				Kategorien::minedBlocks, Kategorien::count),
			custom("unlucky", "☠️ Unlucky One", "most deaths",
				Stats.DEATHS, Kategorien::count),
			custom("hunter", "⚔️ Hunter", "most mobs killed",
				Stats.MOB_KILLS, Kategorien::count),
			custom("nightowl", "🕐 Night Owl", "most time played",
				Stats.PLAY_TIME, Kategorien::time),
			custom("angler", "🐟 Angler", "most fish caught",
				Stats.FISH_CAUGHT, Kategorien::count),
			custom("jumper", "🦘 Jumping Bean", "most jumps",
				Stats.JUMP, Kategorien::count),
			custom("punchingbag", "💔 Punching Bag", "most damage taken",
				Stats.DAMAGE_TAKEN, Kategorien::hearts)
		));
		list.addAll(REGISTERED);
		return list;
	}

	/** For other mods: contribute your own category (call during mod init). */
	public static void register(StatKategorie category) {
		REGISTERED.add(category);
	}
}
