package com.horsti.giftburden;

import com.horsti.core.util.Attribute;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Gifts and burdens — plain vanilla attributes only (plus two tick-based burdens
 * handled by GiftMod). A new trait is one line in the matching array.
 */
public record Trait(String id, String name, Effect effect) {
	public interface Effect {
		void apply(ServerPlayer player, int strength);
	}

	private static Identifier id(String what) {
		return Attribute.id("gift", what);
	}

	private static final Identifier HEALTH = id("health");
	private static final Identifier SPEED = id("speed");
	private static final Identifier ARMOR = id("armor");
	private static final Identifier DAMAGE = id("damage");
	private static final Identifier MINING = id("mining");
	private static final Identifier FALL = id("fall");
	private static final Identifier FALL_DAMAGE = id("falldamage");
	private static final Identifier ABSORPTION = id("absorption");
	private static final Identifier KNOCKBACK = id("knockback");

	public static final Trait[] GIFTS = {
		new Trait("tough", "Tough", (p, s) -> Attribute.addieren(p, Attributes.MAX_HEALTH, HEALTH, 2.0 * s)),
		new Trait("nimble", "Nimble", (p, s) -> Attribute.prozent(p, Attributes.MOVEMENT_SPEED, SPEED, 0.10 * s)),
		new Trait("armoured", "Armoured", (p, s) -> Attribute.addieren(p, Attributes.ARMOR, ARMOR, 1.0 * s)),
		new Trait("hardhitting", "Hard-hitting", (p, s) -> Attribute.addieren(p, Attributes.ATTACK_DAMAGE, DAMAGE, 0.5 * s)),
		new Trait("miner", "Miner", (p, s) -> Attribute.prozent(p, Attributes.BLOCK_BREAK_SPEED, MINING, 0.25 * s)),
		new Trait("featherlight", "Feather-light", (p, s) -> Attribute.addieren(p, Attributes.SAFE_FALL_DISTANCE, FALL, 3.0 * s)),
		new Trait("padded", "Padded", (p, s) -> Attribute.addieren(p, Attributes.MAX_ABSORPTION, ABSORPTION, 2.0 * s)),
		new Trait("steadfast", "Steadfast", (p, s) -> Attribute.addieren(p, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK, 0.15 * s)),
	};

	public static final Trait[] BURDENS = {
		new Trait("fragile", "fragile", (p, s) -> Attribute.addieren(p, Attributes.MAX_HEALTH, HEALTH, -2.0 * s)),
		new Trait("sluggish", "sluggish", (p, s) -> Attribute.prozent(p, Attributes.MOVEMENT_SPEED, SPEED, -0.08 * s)),
		new Trait("soft", "soft", (p, s) -> Attribute.addieren(p, Attributes.ARMOR, ARMOR, -1.0 * s)),
		new Trait("weak", "weak", (p, s) -> Attribute.addieren(p, Attributes.ATTACK_DAMAGE, DAMAGE, -0.5 * s)),
		new Trait("blunt", "blunt at digging", (p, s) -> Attribute.prozent(p, Attributes.BLOCK_BREAK_SPEED, MINING, -0.20 * s)),
		new Trait("glassboned", "glass-boned", (p, s) -> Attribute.prozent(p, Attributes.FALL_DAMAGE_MULTIPLIER, FALL_DAMAGE, 0.25 * s)),
		new Trait("greedy", "greedy", (p, s) -> {
		}),
		new Trait("hydrophobic", "hydrophobic", (p, s) -> {
		}),
	};

	public static Trait find(Trait[] pool, String id) {
		for (Trait trait : pool) {
			if (trait.id().equals(id)) {
				return trait;
			}
		}
		return null;
	}

	public void apply(ServerPlayer player, int strength) {
		effect.apply(player, strength);
	}

	/** Clears every modifier this mod owns (before reassigning / on /gift off). */
	public static void clearAll(ServerPlayer player) {
		Attribute.entfernen(player, Attributes.MAX_HEALTH, HEALTH);
		Attribute.entfernen(player, Attributes.MOVEMENT_SPEED, SPEED);
		Attribute.entfernen(player, Attributes.ARMOR, ARMOR);
		Attribute.entfernen(player, Attributes.ATTACK_DAMAGE, DAMAGE);
		Attribute.entfernen(player, Attributes.BLOCK_BREAK_SPEED, MINING);
		Attribute.entfernen(player, Attributes.SAFE_FALL_DISTANCE, FALL);
		Attribute.entfernen(player, Attributes.FALL_DAMAGE_MULTIPLIER, FALL_DAMAGE);
		Attribute.entfernen(player, Attributes.MAX_ABSORPTION, ABSORPTION);
		Attribute.entfernen(player, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK);
		if (player.getHealth() > player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
	}
}
