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
	/** Tag on the seat entity; emergency cleanup: /kill @e[tag=horsti_sit] */
	private static final String SEAT_TAG = "horsti_sit";

	private final ModSettings settings = new ModSettings("sit", true);
	private final BoolSetting stairs = settings.add(new BoolSetting("stairs", "stairs are clickable", true));
	private final BoolSetting slabs = settings.add(new BoolSetting("slabs", "slabs are clickable", true));
	private final BoolSetting command = settings.add(new BoolSetting("command", "allow /sitdown for everyone", false));
	private final BoolSetting emptyHandOnly = settings.add(new BoolSetting("emptyHandOnly", "only with an empty hand", true));
	private final BoolSetting standUpOnDamage = settings.add(new BoolSetting("standUpOnDamage", "stand up when damaged", true));

	private final List<ArmorStand> seats = new ArrayList<>();
	private final Set<UUID> seatIds = new HashSet<>();

	@Override
	public void onInitialize() {
		new HorstiMod("sit", "Sit", settings)
			.onToggle(this::standEveryoneUp)
			.extra((root, ctx) -> {
			})
			.registrieren();

		// /sitdown for every player (when the setting allows it)
		net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("sitdown").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				if (!settings.istAktiv() || !command.get()) {
					return 0;
				}
				sitDown(sp, sp.getX(), sp.getY(), sp.getZ());
				return 1;
			})));

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide() || hand != InteractionHand.MAIN_HAND || !settings.istAktiv()) {
				return InteractionResult.PASS;
			}
			if (!(player instanceof ServerPlayer sp) || sp.isPassenger() || sp.isShiftKeyDown()) {
				return InteractionResult.PASS;
			}
			if (emptyHandOnly.get() && !sp.getMainHandItem().isEmpty()) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			BlockState state = level.getBlockState(pos);
			double seatHeight;
			if (state.getBlock() instanceof StairBlock && stairs.get()
				&& state.getValue(StairBlock.HALF) == Half.BOTTOM) {
				seatHeight = 0.3;
			} else if (state.getBlock() instanceof SlabBlock && slabs.get()
				&& state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
				seatHeight = 0.3;
			} else {
				return InteractionResult.PASS;
			}
			if (!level.getBlockState(pos.above()).isAir()) {
				return InteractionResult.PASS;
			}
			sitDown(sp, pos.getX() + 0.5, pos.getY() + seatHeight - 1.05, pos.getZ() + 0.5);
			return InteractionResult.SUCCESS;
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (standUpOnDamage.get() && entity instanceof ServerPlayer sp
				&& sp.getVehicle() instanceof ArmorStand as && seatIds.contains(as.getUUID())) {
				sp.stopRiding();
			}
			return true;
		});

		// Clean up orphaned seats
		Ticker.alleTicks(10, server -> {
			seats.removeIf(as -> {
				if (as.isRemoved()) {
					seatIds.remove(as.getUUID());
					return true;
				}
				if (!as.isVehicle()) {
					seatIds.remove(as.getUUID());
					as.discard();
					return true;
				}
				return false;
			});
		});
	}

	private void sitDown(ServerPlayer sp, double x, double y, double z) {
		ServerLevel level = (ServerLevel) sp.level();
		ArmorStand seat = new ArmorStand(level, x, y, z);
		seat.setInvisible(true);
		seat.setNoGravity(true);
		seat.setInvulnerable(true);
		seat.addTag(SEAT_TAG);
		level.addFreshEntity(seat);
		seats.add(seat);
		seatIds.add(seat.getUUID());
		sp.startRiding(seat);
	}

	private void standEveryoneUp() {
		for (ArmorStand as : seats) {
			if (!as.isRemoved()) {
				as.ejectPassengers();
				as.discard();
			}
		}
		seats.clear();
		seatIds.clear();
	}
}
