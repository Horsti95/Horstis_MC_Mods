package com.horsti.toolguard.mixin;

import com.horsti.toolguard.ToolguardMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bricht den Haltbarkeitsverbrauch ab, bevor der Gegenstand zerbricht.
 * require = 0: faellt bei API-Drift still aus, statt den Server zu stoppen.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackDamageMixin {
	@Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
		at = @At("HEAD"), cancellable = true, require = 0)
	private void horsti$schuetzen(int schaden, ServerLevel level, LivingEntity traeger,
			java.util.function.Consumer<net.minecraft.world.item.Item> beimZerbrechen, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		ServerPlayer sp = traeger instanceof ServerPlayer p ? p : null;
		if (sp == null) {
			return; // Mobs und Rahmen bleiben Vanilla
		}
		if (!ToolguardMod.darfSchadenNehmen(stack, schaden, sp)) {
			ci.cancel();
		}
	}
}
