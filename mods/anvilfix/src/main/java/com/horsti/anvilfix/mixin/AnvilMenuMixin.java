package com.horsti.anvilfix.mixin;

import com.horsti.anvilfix.AnvilfixMod;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	@Shadow
	@Final
	private DataSlot cost;

	// Lift the "Too Expensive" threshold (40) so the result is not cleared.
	@ModifyConstant(method = "createResult", constant = @Constant(intValue = 40), require = 0)
	private int horsti$threshold(int original) {
		return AnvilfixMod.threshold(original);
	}

	// Then clamp the real cost to the cap (<= 39, so vanilla clients accept it).
	@Inject(method = "createResult", at = @At("TAIL"), require = 0)
	private void horsti$cap(CallbackInfo ci) {
		int capped = AnvilfixMod.capCost(this.cost.get());
		if (capped != this.cost.get()) {
			this.cost.set(capped);
		}
	}

	// Prior work penalty: vanilla is cost*2+1, we optionally make it linear or frozen.
	@Inject(method = "calculateIncreasedRepairCost", at = @At("HEAD"), cancellable = true, require = 0)
	private static void horsti$priorWork(int oldValue, CallbackInfoReturnable<Integer> cir) {
		Integer replacement = AnvilfixMod.priorWorkResult(oldValue);
		if (replacement != null) {
			cir.setReturnValue(replacement);
		}
	}
}
