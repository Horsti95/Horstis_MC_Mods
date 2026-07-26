package com.horsti.gabe;

import com.horsti.core.util.Attribute;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Gaben und Buerden — ausschliesslich Vanilla-Attribute (plus zwei Tick-Buerden,
 * die GabeMod behandelt). Neue Eigenschaft = eine Zeile im jeweiligen Array.
 */
public record Eigenschaft(String id, String name, Wirkung wirkung) {
	public interface Wirkung {
		void anwenden(ServerPlayer sp, int staerke);
	}

	private static Identifier id(String was) {
		return Attribute.id("gabe", was);
	}

	private static final Identifier HP = id("hp");
	private static final Identifier SPEED = id("speed");
	private static final Identifier ARMOR = id("armor");
	private static final Identifier SCHADEN = id("schaden");
	private static final Identifier ABBAU = id("abbau");
	private static final Identifier FALL = id("fall");
	private static final Identifier FALLSCHADEN = id("fallschaden");
	private static final Identifier ABSORB = id("absorb");

	public static final Eigenschaft[] GABEN = {
		new Eigenschaft("zaeh", "Zäh", (sp, s) -> Attribute.addieren(sp, Attributes.MAX_HEALTH, HP, 2.0 * s)),
		new Eigenschaft("flink", "Flink", (sp, s) -> Attribute.prozent(sp, Attributes.MOVEMENT_SPEED, SPEED, 0.10 * s)),
		new Eigenschaft("gepanzert", "Gepanzert", (sp, s) -> Attribute.addieren(sp, Attributes.ARMOR, ARMOR, 1.0 * s)),
		new Eigenschaft("schlaeger", "Schlagkräftig", (sp, s) -> Attribute.addieren(sp, Attributes.ATTACK_DAMAGE, SCHADEN, 0.5 * s)),
		new Eigenschaft("bergmann", "Bergmann", (sp, s) -> Attribute.prozent(sp, Attributes.BLOCK_BREAK_SPEED, ABBAU, 0.25 * s)),
		new Eigenschaft("federleicht", "Federleicht", (sp, s) -> Attribute.addieren(sp, Attributes.SAFE_FALL_DISTANCE, FALL, 3.0 * s)),
		new Eigenschaft("gepolstert", "Gepolstert", (sp, s) -> Attribute.addieren(sp, Attributes.MAX_ABSORPTION, ABSORB, 2.0 * s)),
		new Eigenschaft("standhaft", "Standhaft", (sp, s) -> Attribute.addieren(sp, Attributes.KNOCKBACK_RESISTANCE, id("kb"), 0.15 * s)),
	};

	public static final Eigenschaft[] BUERDEN = {
		new Eigenschaft("zerbrechlich", "zerbrechlich", (sp, s) -> Attribute.addieren(sp, Attributes.MAX_HEALTH, HP, -2.0 * s)),
		new Eigenschaft("traege", "träge", (sp, s) -> Attribute.prozent(sp, Attributes.MOVEMENT_SPEED, SPEED, -0.08 * s)),
		new Eigenschaft("weich", "weich", (sp, s) -> Attribute.addieren(sp, Attributes.ARMOR, ARMOR, -1.0 * s)),
		new Eigenschaft("schwaechlich", "schwächlich", (sp, s) -> Attribute.addieren(sp, Attributes.ATTACK_DAMAGE, SCHADEN, -0.5 * s)),
		new Eigenschaft("stumpf", "stumpf beim Graben", (sp, s) -> Attribute.prozent(sp, Attributes.BLOCK_BREAK_SPEED, ABBAU, -0.20 * s)),
		new Eigenschaft("glasknochen", "glasknöchrig", (sp, s) -> Attribute.prozent(sp, Attributes.FALL_DAMAGE_MULTIPLIER, FALLSCHADEN, 0.25 * s)),
		new Eigenschaft("hungrig", "verfressen", (sp, s) -> {
		}),
		new Eigenschaft("wasserscheu", "wasserscheu", (sp, s) -> {
		}),
	};

	public static Eigenschaft finde(Eigenschaft[] pool, String id) {
		for (Eigenschaft e : pool) {
			if (e.id().equals(id)) {
				return e;
			}
		}
		return null;
	}

	public void anwenden(ServerPlayer sp, int staerke) {
		wirkung.anwenden(sp, staerke);
	}

	/** Alle Modifier dieses Mods loeschen (vor Neuzuweisung / bei /gabe off). */
	public static void entferneAlle(ServerPlayer sp) {
		Attribute.entfernen(sp, Attributes.MAX_HEALTH, HP);
		Attribute.entfernen(sp, Attributes.MOVEMENT_SPEED, SPEED);
		Attribute.entfernen(sp, Attributes.ARMOR, ARMOR);
		Attribute.entfernen(sp, Attributes.ATTACK_DAMAGE, SCHADEN);
		Attribute.entfernen(sp, Attributes.BLOCK_BREAK_SPEED, ABBAU);
		Attribute.entfernen(sp, Attributes.SAFE_FALL_DISTANCE, FALL);
		Attribute.entfernen(sp, Attributes.FALL_DAMAGE_MULTIPLIER, FALLSCHADEN);
		Attribute.entfernen(sp, Attributes.MAX_ABSORPTION, ABSORB);
		Attribute.entfernen(sp, Attributes.KNOCKBACK_RESISTANCE, id("kb"));
		if (sp.getHealth() > sp.getMaxHealth()) {
			sp.setHealth(sp.getMaxHealth());
		}
	}
}
