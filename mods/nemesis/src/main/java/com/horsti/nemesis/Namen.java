package com.horsti.nemesis;

import java.util.List;
import java.util.Random;

/** Name generator built from syllable lists — no assets, just text. */
public final class Namen {
	private static final List<String> FIRST_NAMES = List.of(
		"Klaus", "Brunhilde", "Egon", "Gundula", "Horst", "Sieglinde", "Rudi", "Waltraud",
		"Detlef", "Roswitha", "Gunther", "Hilde", "Norbert", "Erna", "Bodo", "Frieda");
	private static final List<String> TITLES = List.of(
		"the Bonebreaker", "the Relentless", "the Shadowless", "the Restless",
		"the Spiteful", "the Patient", "the Unyielding", "the Grim",
		"the Revenant", "the Nameless", "the Nightmare", "the Vengeful",
		"the Persistent", "the Lurking", "the Grumbling", "the Cursed");

	private Namen() {
	}

	public static String roll(Random random) {
		return FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()))
			+ " " + TITLES.get(random.nextInt(TITLES.size()));
	}
}
