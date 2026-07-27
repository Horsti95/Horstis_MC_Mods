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
 * Cancels durability loss before the item breaks.
 * require = 0: fails silently on API drift instead of taking the server down.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackDamageMixin {
	@Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
		at = @At("HEAD"), cancellable = true, require = 0)
	private void horsti$protect(int damage, ServerLevel level, LivingEntity holder,
			java.util.function.Consumer<net.minecraft.world.item.Item> onBreak, CallbackInfo ci) {
		ItemStack stack = (ItemStack) (Object) this;
		ServerPlayer player = holder instanceof ServerPlayer p ? p : null;
		if (player == null) {
			return; // mobs and item frames stay vanilla
		}
		if (!ToolguardMod.mayTakeDamage(stack, damage, player)) {
			ci.cancel();
		}
	}
}
