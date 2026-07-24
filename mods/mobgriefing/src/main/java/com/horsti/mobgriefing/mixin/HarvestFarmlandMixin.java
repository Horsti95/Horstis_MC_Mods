package com.horsti.mobgriefing.mixin;

import com.horsti.mobgriefing.MobgriefingMod;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HarvestFarmland.class)
public abstract class HarvestFarmlandMixin {
	@Inject(method = "checkExtraStartConditions", at = @At("HEAD"), cancellable = true, require = 0)
	private void horsti$ernten(CallbackInfoReturnable<Boolean> cir) {
		if (!MobgriefingMod.erlaubt("villager")) {
			cir.setReturnValue(false);
		}
	}
}
