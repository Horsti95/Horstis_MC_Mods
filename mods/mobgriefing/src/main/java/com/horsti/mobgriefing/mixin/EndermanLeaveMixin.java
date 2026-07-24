package com.horsti.mobgriefing.mixin;

import com.horsti.mobgriefing.MobgriefingMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLeaveBlockGoal")
public abstract class EndermanLeaveMixin {
	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true, require = 0)
	private void horsti$blockAbsetzen(CallbackInfoReturnable<Boolean> cir) {
		if (!MobgriefingMod.erlaubt("enderman")) {
			cir.setReturnValue(false);
		}
	}
}
