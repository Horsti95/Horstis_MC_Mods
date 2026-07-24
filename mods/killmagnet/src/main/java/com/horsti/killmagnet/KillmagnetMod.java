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
	private final EnumSetting modus = settings.add(new EnumSetting("modus", "magnet = fliegt zu dir, inventar = direkt rein", "magnet", "magnet", "inventar"));
	private final BoolSetting xp = settings.add(new BoolSetting("xp", "XP ebenfalls einziehen", true));
	private final BoolSetting spielerKills = settings.add(new BoolSetting("spielerKills", "auch bei PvP-Kills", false));

	@Override
	public void onInitialize() {
		new HorstiMod("killmagnet", "Killmagnet", settings).registrieren();

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!settings.istAktiv()) {
				return;
			}
			if (entity instanceof Player && !spielerKills.get()) {
				return;
			}
			if (!(source.getEntity() instanceof ServerPlayer killer)) {
				return;
			}
			if (!(entity.level() instanceof ServerLevel level)) {
				return;
			}
			Vec3 ort = entity.position();
			// Drops entstehen im selben Tick nach dem Event -> einen Tick spaeter einsammeln.
			Ticker.nachTicks(2, () -> einsammeln(level, ort, killer));
		});
	}

	private void einsammeln(ServerLevel level, Vec3 ort, ServerPlayer killer) {
		if (killer.isRemoved() || killer.hasDisconnected()) {
			return;
		}
		AABB box = AABB.ofSize(ort, 5, 5, 5);
		boolean insInventar = modus.get().equals("inventar");

		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box, i -> i.isAlive() && i.tickCount <= 3)) {
			if (insInventar) {
				if (killer.getInventory().add(item.getItem()) && item.getItem().isEmpty()) {
					item.discard();
				} else {
					// Inventar voll (oder Reststack): zum Spieler schicken
					zumSpieler(item, killer);
				}
			} else {
				zumSpieler(item, killer);
			}
		}
		if (xp.get()) {
			for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, box, o -> o.isAlive() && o.tickCount <= 3)) {
				orb.setPos(killer.getX(), killer.getY() + 0.5, killer.getZ());
			}
		}
	}

	private static void zumSpieler(ItemEntity item, ServerPlayer killer) {
		item.setPos(killer.getX(), killer.getY() + 0.4, killer.getZ());
		item.setPickUpDelay(0);
	}
}
