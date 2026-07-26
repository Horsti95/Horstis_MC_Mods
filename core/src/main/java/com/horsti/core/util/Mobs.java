package com.horsti.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;

/**
 * Mob-Erzeugung ueber die Registry statt ueber statische EntityType-Felder —
 * ein einziger Beruehrungspunkt, falls Mojang hier weiter umbaut.
 */
public final class Mobs {
	private Mobs() {
	}

	public static EntityType<?> typ(String id) {
		return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(id));
	}

	public static boolean istTyp(Entity entity, String id) {
		return EntityType.getKey(entity.getType()).toString().equals(id);
	}

	public static String typId(Entity entity) {
		return EntityType.getKey(entity.getType()).toString();
	}

	/** Erzeugt und setzt einen Mob in die Welt; null wenn der Typ unbekannt ist. */
	public static Entity spawnen(ServerLevel level, String id, BlockPos pos) {
		EntityType<?> typ = typ(id);
		if (typ == null) {
			return null;
		}
		Entity entity = typ.create(level, EntitySpawnReason.EVENT);
		if (entity == null) {
			return null;
		}
		entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		return level.addFreshEntity(entity) ? entity : null;
	}
}
