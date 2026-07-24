package com.horsti.mobgriefing.mixin;

import com.horsti.mobgriefing.MobgriefingMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Faengt Mob-Explosionen (Creeper, Ghast, Wither-Skull) ab, indem die
 * ExplosionInteraction MOB auf NONE gedreht wird, wenn der Typ-Schalter aus ist.
 * Signatur-agnostisch: die Parameter werden ueber ihren Typ gegriffen
 * (require=0 -> bei API-Drift faellt nur dieses Feature aus, nicht der Server).
 */
@Mixin(ServerLevel.class)
public abstract class LevelExplosionMixin {
	private static final ThreadLocal<Entity> horsti$quelle = new ThreadLocal<>();

	@ModifyVariable(method = "explode", at = @At("HEAD"), argsOnly = true, require = 0)
	private Entity horsti$quelleMerken(Entity quelle) {
		horsti$quelle.set(quelle);
		return quelle;
	}

	@ModifyVariable(method = "explode", at = @At("HEAD"), argsOnly = true, require = 0)
	private Level.ExplosionInteraction horsti$interaktion(Level.ExplosionInteraction interaktion) {
		Entity quelle = horsti$quelle.get();
		horsti$quelle.remove();
		if (interaktion != Level.ExplosionInteraction.MOB || quelle == null) {
			return interaktion;
		}
		String typ = MobgriefingMod.explosionsTyp(quelle);
		if (typ != null && !MobgriefingMod.erlaubt(typ)) {
			return Level.ExplosionInteraction.NONE;
		}
		return interaktion;
	}
}
