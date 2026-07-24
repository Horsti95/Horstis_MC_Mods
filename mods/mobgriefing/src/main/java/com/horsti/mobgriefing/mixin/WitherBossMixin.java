package com.horsti.mobgriefing.mixin;

import com.horsti.mobgriefing.MobgriefingMod;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Block-Zerbeissen des Withers (der "Break-Blocks-um-mich"-Tick) abschaltbar machen. */
@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {
	@Redirect(
		method = "aiStep",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
		),
		require = 0
	)
	private boolean horsti$witherGriefing(GameRules regeln, GameRules.Key<GameRules.BooleanValue> key) {
		boolean vanilla = regeln.getBoolean(key);
		return vanilla && MobgriefingMod.erlaubt("wither");
	}
}
