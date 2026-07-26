package com.horsti.core.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/** Attribut-Modifier setzen/entfernen mit eigener Namespace-ID (mod-verträglich). */
public final class Attribute {
	private Attribute() {
	}

	public static Identifier id(String mod, String was) {
		return Identifier.fromNamespaceAndPath("horsti_" + mod, was);
	}

	/** Setzt (oder ersetzt) einen transienten Modifier; 0 entfernt ihn. */
	public static void setzen(LivingEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribut,
			Identifier id, double wert, AttributeModifier.Operation operation) {
		AttributeInstance instanz = entity.getAttribute(attribut);
		if (instanz == null) {
			return;
		}
		instanz.removeModifier(id);
		if (wert != 0) {
			instanz.addOrUpdateTransientModifier(new AttributeModifier(id, wert, operation));
		}
	}

	public static void addieren(LivingEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribut,
			Identifier id, double wert) {
		setzen(entity, attribut, id, wert, AttributeModifier.Operation.ADD_VALUE);
	}

	public static void prozent(LivingEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribut,
			Identifier id, double anteil) {
		setzen(entity, attribut, id, anteil, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	public static void entfernen(LivingEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribut, Identifier id) {
		AttributeInstance instanz = entity.getAttribute(attribut);
		if (instanz != null) {
			instanz.removeModifier(id);
		}
	}
}
