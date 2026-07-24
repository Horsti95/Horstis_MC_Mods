package com.horsti.sit;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SitMod implements ModInitializer {
	/** Tag am Sitz-Entity; Notfall-Aufraeumen: /kill @e[tag=horsti_sit] */
	private static final String SITZ_TAG = "horsti_sit";

	private final ModSettings settings = new ModSettings("sit", true);
	private final BoolSetting treppen = settings.add(new BoolSetting("treppen", "Treppen klickbar", true));
	private final BoolSetting stufen = settings.add(new BoolSetting("stufen", "Stufen klickbar", true));
	private final BoolSetting command = settings.add(new BoolSetting("command", "/sitz fuer alle erlauben", false));
	private final BoolSetting nurLeereHand = settings.add(new BoolSetting("nurLeereHand", "nur mit leerer Hand", true));
	private final BoolSetting aufstehenBeiSchaden = settings.add(new BoolSetting("aufstehenBeiSchaden", "bei Schaden aufstehen", true));

	private final List<ArmorStand> sitze = new ArrayList<>();
	private final Set<UUID> sitzIds = new HashSet<>();

	@Override
	public void onInitialize() {
		new HorstiMod("sit", "Sit", settings)
			.onToggle(this::alleAufstehen)
			.extra((root, ctx) -> {
			})
			.registrieren();

		// /sitz fuer alle Spieler (wenn per Setting erlaubt)
		net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("sitz").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				if (!settings.istAktiv() || !command.get()) {
					return 0;
				}
				hinsetzen(sp, sp.getX(), sp.getY(), sp.getZ());
				return 1;
			})));

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide() || hand != InteractionHand.MAIN_HAND || !settings.istAktiv()) {
				return InteractionResult.PASS;
			}
			if (!(player instanceof ServerPlayer sp) || sp.isPassenger() || sp.isShiftKeyDown()) {
				return InteractionResult.PASS;
			}
			if (nurLeereHand.get() && !sp.getMainHandItem().isEmpty()) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			BlockState state = level.getBlockState(pos);
			double sitzHoehe;
			if (state.getBlock() instanceof StairBlock && treppen.get()
				&& state.getValue(StairBlock.HALF) == Half.BOTTOM) {
				sitzHoehe = 0.3;
			} else if (state.getBlock() instanceof SlabBlock && stufen.get()
				&& state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
				sitzHoehe = 0.3;
			} else {
				return InteractionResult.PASS;
			}
			if (!level.getBlockState(pos.above()).isAir()) {
				return InteractionResult.PASS;
			}
			hinsetzen(sp, pos.getX() + 0.5, pos.getY() + sitzHoehe - 0.6, pos.getZ() + 0.5);
			return InteractionResult.SUCCESS;
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, betrag) -> {
			if (aufstehenBeiSchaden.get() && entity instanceof ServerPlayer sp
				&& sp.getVehicle() instanceof ArmorStand as && sitzIds.contains(as.getUUID())) {
				sp.stopRiding();
			}
			return true;
		});

		// Verwaiste Sitze entsorgen
		Ticker.alleTicks(10, server -> {
			sitze.removeIf(as -> {
				if (as.isRemoved()) {
					sitzIds.remove(as.getUUID());
					return true;
				}
				if (!as.isVehicle()) {
					sitzIds.remove(as.getUUID());
					as.discard();
					return true;
				}
				return false;
			});
		});
	}

	private void hinsetzen(ServerPlayer sp, double x, double y, double z) {
		ServerLevel level = sp.serverLevel();
		ArmorStand sitz = new ArmorStand(level, x, y, z);
		sitz.setInvisible(true);
		sitz.setNoGravity(true);
		sitz.setInvulnerable(true);
		sitz.setSmall(true);
		sitz.addTag(SITZ_TAG);
		level.addFreshEntity(sitz);
		sitze.add(sitz);
		sitzIds.add(sitz.getUUID());
		sp.startRiding(sitz, true);
	}

	private void alleAufstehen() {
		for (ArmorStand as : sitze) {
			if (!as.isRemoved()) {
				as.ejectPassengers();
				as.discard();
			}
		}
		sitze.clear();
		sitzIds.clear();
	}
}
