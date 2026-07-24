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

	// Die "Zu teuer!"-Schwelle (40) aufheben, damit das Ergebnis nicht geleert wird.
	@ModifyConstant(method = "createResult", constant = @Constant(intValue = 40), require = 0)
	private int horsti$schwelle(int original) {
		return AnvilfixMod.schwelle(original);
	}

	// Danach die realen Kosten auf den Deckel klemmen (<=39, Vanilla-Client-kompatibel).
	@Inject(method = "createResult", at = @At("TAIL"), require = 0)
	private void horsti$deckel(CallbackInfo ci) {
		int gedeckelt = AnvilfixMod.kostenDeckel(this.cost.get());
		if (gedeckelt != this.cost.get()) {
			this.cost.set(gedeckelt);
		}
	}

	// Prior-Work-Strafe: vanilla = kosten*2+1, wir optional linear oder eingefroren.
	@Inject(method = "calculateIncreasedRepairCost", at = @At("HEAD"), cancellable = true, require = 0)
	private static void horsti$priorWork(int alterWert, CallbackInfoReturnable<Integer> cir) {
		Integer neu = AnvilfixMod.priorWorkErgebnis(alterWert);
		if (neu != null) {
			cir.setReturnValue(neu);
		}
	}
}
