package com.horsti.harvest;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.ModSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HarvestMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("harvest", true);
	private final BoolSetting netherWart = settings.add(new BoolSetting("netherWart", "include nether wart", true));
	private final BoolSetting cocoa = settings.add(new BoolSetting("cocoa", "include cocoa beans", true));
	private final BoolSetting fortune = settings.add(new BoolSetting("fortune", "Fortune applies to click harvesting", true));
	private final BoolSetting sound = settings.add(new BoolSetting("sound", "play a harvest sound", true));

	@Override
	public void onInitialize() {
		new HorstiMod("harvest", "Harvest", settings).registrieren();

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide() || hand != InteractionHand.MAIN_HAND || !settings.istAktiv()) {
				return InteractionResult.PASS;
			}
			if (!(player instanceof ServerPlayer sp) || !(level instanceof ServerLevel serverLevel)) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			BlockState state = serverLevel.getBlockState(pos);
			BlockState replanted = ripeAndReplantState(state);
			if (replanted == null) {
				return InteractionResult.PASS;
			}

			ItemStack tool = fortune.get() ? sp.getMainHandItem() : ItemStack.EMPTY;
			List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null, sp, tool);

			// One seed is held back for the replant.
			Item seed = state.getBlock().asItem();
			boolean seedKept = false;
			for (ItemStack drop : drops) {
				if (!seedKept && drop.is(seed)) {
					drop.shrink(1);
					seedKept = true;
				}
			}

			serverLevel.setBlock(pos, replanted, 3);
			for (ItemStack drop : drops) {
				if (!drop.isEmpty()) {
					Block.popResource(serverLevel, pos, drop);
				}
			}
			if (sound.get()) {
				serverLevel.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
			}
			return InteractionResult.SUCCESS;
		});
	}

	/** null = not harvestable; otherwise the state to leave behind after replanting. */
	private BlockState ripeAndReplantState(BlockState state) {
		if (state.getBlock() instanceof CropBlock crop) {
			return crop.isMaxAge(state) ? crop.getStateForAge(0) : null;
		}
		if (state.is(Blocks.NETHER_WART) && netherWart.get()) {
			return state.getValue(NetherWartBlock.AGE) >= 3
				? state.setValue(NetherWartBlock.AGE, 0) : null;
		}
		if (state.is(Blocks.COCOA) && cocoa.get()) {
			return state.getValue(CocoaBlock.AGE) >= 2
				? state.setValue(CocoaBlock.AGE, 0) : null; // FACING is preserved
		}
		return null;
	}
}
