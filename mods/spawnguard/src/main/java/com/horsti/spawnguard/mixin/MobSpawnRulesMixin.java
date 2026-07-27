package com.horsti.spawnguard.mixin;

import com.horsti.spawnguard.SpawnguardMod;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The single point where a spawn is refused. Natural spawning asks every mob whether it
 * may appear here — inside a guarded zone the answer becomes "no".
 * require = 0: fails silently on API drift instead of taking the server down.
 */
@Mixin(Mob.class)
public abstract class MobSpawnRulesMixin {
	@Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true, require = 0)
	private void horsti$guard(LevelAccessor level, EntitySpawnReason reason, CallbackInfoReturnable<Boolean> cir) {
		if (SpawnguardMod.blocks((Mob) (Object) this, reason)) {
			cir.setReturnValue(false);
		}
	}
}
