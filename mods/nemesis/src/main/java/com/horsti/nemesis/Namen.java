package com.horsti.nemesis;

import java.util.List;
import java.util.Random;

/** Namensgenerator aus Silben — kein Asset, nur Text. */
public final class Namen {
	private static final List<String> VORNAMEN = List.of(
		"Klaus", "Brunhilde", "Egon", "Gundula", "Horst", "Sieglinde", "Rudi", "Waltraud",
		"Detlef", "Roswitha", "Günther", "Hilde", "Norbert", "Erna", "Bodo", "Frieda");
	private static final List<String> TITEL = List.of(
		"der Knochenbrecher", "die Unerbittliche", "der Schattenlose", "die Rastlose",
		"der Nachtragende", "die Geduldige", "der Zähe", "die Grimmige",
		"der Wiedergänger", "die Namenlose", "der Alptraum", "die Rache",
		"der Beharrliche", "die Lauernde", "der Grollende", "die Verfluchte");

	private Namen() {
	}

	public static String wuerfeln(Random random) {
		return VORNAMEN.get(random.nextInt(VORNAMEN.size())) + " " + TITEL.get(random.nextInt(TITEL.size()));
	}
}
