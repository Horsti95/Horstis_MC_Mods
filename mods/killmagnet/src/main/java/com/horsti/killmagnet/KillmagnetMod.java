package com.horsti.killmagnet;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.EnumSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class KillmagnetMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("killmagnet", true);
	private final EnumSetting mode = settings.add(new EnumSetting("mode", "magnet = drops fly to you, inventory = straight into it", "magnet", "magnet", "inventory"));
	private final BoolSetting xp = settings.add(new BoolSetting("xp", "pull in experience too", true));
	private final BoolSetting playerKills = settings.add(new BoolSetting("playerKills", "also on PvP kills", false));

	@Override
	public void onInitialize() {
		new HorstiMod("killmagnet", "Killmagnet", settings).registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv()) {
				return;
			}
			if (entity instanceof Player && !playerKills.get()) {
				return;
			}
			if (!(source.getEntity() instanceof ServerPlayer killer)) {
				return;
			}
			if (!(entity.level() instanceof ServerLevel level)) {
				return;
			}
			Vec3 spot = entity.position();
			// The drops appear in the same tick, just after this event — so collect a tick later.
			Ticker.nachTicks(2, () -> collect(level, spot, killer));
		});
	}

	private void collect(ServerLevel level, Vec3 spot, ServerPlayer killer) {
		if (killer.isRemoved() || killer.hasDisconnected()) {
			return;
		}
		AABB box = AABB.ofSize(spot, 5, 5, 5);
		boolean intoInventory = mode.get().equals("inventory");

		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box, i -> i.isAlive() && i.tickCount <= 3)) {
			if (intoInventory) {
				if (killer.getInventory().add(item.getItem()) && item.getItem().isEmpty()) {
					item.discard();
				} else {
					// Inventory full (or a leftover stack): send it to the player instead
					toPlayer(item, killer);
				}
			} else {
				toPlayer(item, killer);
			}
		}
		if (xp.get()) {
			for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, box, o -> o.isAlive() && o.tickCount <= 3)) {
				orb.setPos(killer.getX(), killer.getY() + 0.5, killer.getZ());
			}
		}
	}

	private static void toPlayer(ItemEntity item, ServerPlayer killer) {
		item.setPos(killer.getX(), killer.getY() + 0.4, killer.getZ());
		item.setPickUpDelay(0);
	}
}
