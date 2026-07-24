package com.horsti.mobgriefing.mixin;

import com.horsti.mobgriefing.MobgriefingMod;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin {
	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true, require = 0)
	private void horsti$grasFressen(CallbackInfoReturnable<Boolean> cir) {
		if (!MobgriefingMod.erlaubt("schaf")) {
			cir.setReturnValue(false);
		}
	}
}
