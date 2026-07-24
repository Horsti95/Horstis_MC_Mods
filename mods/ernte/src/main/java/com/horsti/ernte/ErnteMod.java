package com.horsti.ernte;

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

public class ErnteMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("ernte", true);
	private final BoolSetting nether = settings.add(new BoolSetting("nether", "Netherwarze einschliessen", true));
	private final BoolSetting kakao = settings.add(new BoolSetting("kakao", "Kakao einschliessen", true));
	private final BoolSetting fortune = settings.add(new BoolSetting("fortune", "Fortune wirkt bei Klick-Ernte", true));
	private final BoolSetting sound = settings.add(new BoolSetting("sound", "Ernte-Sound", true));

	@Override
	public void onInitialize() {
		new HorstiMod("ernte", "Ernte", settings).registrieren();

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide() || hand != InteractionHand.MAIN_HAND || !settings.istAktiv()) {
				return InteractionResult.PASS;
			}
			if (!(player instanceof ServerPlayer sp) || !(level instanceof ServerLevel serverLevel)) {
				return InteractionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			BlockState state = serverLevel.getBlockState(pos);
			BlockState neuerZustand = reifUndNeuzustand(state);
			if (neuerZustand == null) {
				return InteractionResult.PASS;
			}

			ItemStack werkzeug = fortune.get() ? sp.getMainHandItem() : ItemStack.EMPTY;
			List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null, sp, werkzeug);

			// Ein Exemplar der Saat wird fuers Nachpflanzen einbehalten.
			Item saat = state.getBlock().asItem();
			boolean saatEinbehalten = false;
			for (ItemStack drop : drops) {
				if (!saatEinbehalten && drop.is(saat)) {
					drop.shrink(1);
					saatEinbehalten = true;
				}
			}

			serverLevel.setBlock(pos, neuerZustand, 3);
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

	/** null = nicht erntbar; sonst der Zustand nach dem Nachpflanzen. */
	private BlockState reifUndNeuzustand(BlockState state) {
		if (state.getBlock() instanceof CropBlock crop) {
			return crop.isMaxAge(state) ? crop.getStateForAge(0) : null;
		}
		if (state.is(Blocks.NETHER_WART) && nether.get()) {
			return state.getValue(NetherWartBlock.AGE) >= 3
				? state.setValue(NetherWartBlock.AGE, 0) : null;
		}
		if (state.is(Blocks.COCOA) && kakao.get()) {
			return state.getValue(CocoaBlock.AGE) >= 2
				? state.setValue(CocoaBlock.AGE, 0) : null; // FACING bleibt erhalten
		}
		return null;
	}
}
