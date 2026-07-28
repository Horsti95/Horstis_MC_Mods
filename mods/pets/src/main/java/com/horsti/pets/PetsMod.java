package com.horsti.pets;

import com.horsti.core.HorstiMod;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PetsMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("pets", true);
	private final BoolSetting protect = settings.add(new BoolSetting("protect", "friendly-fire protection", true));
	private final BoolSetting sneakBypass = settings.add(new BoolSetting("sneakBypass", "sneak + hit bypasses the protection", true));
	private final IntSetting findGlowSeconds = settings.add(new IntSetting("findGlowSeconds", "glow duration for /pets find", 30, 5, 60));
	private final IntSetting radius = settings.add(new IntSetting("radius", "range for find/stay/follow", 48, 16, 128));

	@Override
	public void onInitialize() {
		new HorstiMod("pets", "Pets", settings).registrieren();

		// Friendly-fire protection: your own hit on your own pet is cancelled
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!settings.istAktiv() || !protect.get() || !(entity instanceof TamableAnimal pet)) {
				return true;
			}
			Entity attacker = source.getEntity();
			if (!(attacker instanceof ServerPlayer sp) || !isOwner(pet, sp)) {
				return true;
			}
			if (sneakBypass.get() && sp.isShiftKeyDown()) {
				return true; // deliberate slaughtering stays possible
			}
			Broadcast.actionbar(sp, Component.literal("That is your pet! (sneak + hit to do it anyway)")
				.withStyle(ChatFormatting.GRAY));
			return false;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("pets")
				.then(Commands.literal("list").executes(c -> list(c.getSource().getPlayerOrException())))
				.then(Commands.literal("find").executes(c -> find(c.getSource().getPlayerOrException())))
				.then(Commands.literal("stay").executes(c -> orderToSit(c.getSource().getPlayerOrException(), true)))
				.then(Commands.literal("follow").executes(c -> orderToSit(c.getSource().getPlayerOrException(), false)))));
	}

	/** The one owner check — our only touch point with the Tamable API. */
	private static boolean isOwner(TamableAnimal pet, Player player) {
		return pet.isTame() && pet.getOwner() == player;
	}

	private List<TamableAnimal> myPets(ServerPlayer sp) {
		AABB box = sp.getBoundingBox().inflate(radius.get());
		return ((ServerLevel) sp.level()).getEntitiesOfClass(TamableAnimal.class, box, t -> isOwner(t, sp));
	}

	private int list(ServerPlayer sp) {
		if (!settings.istAktiv()) {
			return 0;
		}
		List<TamableAnimal> pets = myPets(sp);
		if (pets.isEmpty()) {
			sp.sendSystemMessage(Component.literal("[Pets] No pets of yours within " + radius.get() + " blocks.")
				.withStyle(ChatFormatting.GRAY));
			return 1;
		}
		sp.sendSystemMessage(Component.literal("[Pets] " + pets.size() + " pet(s) nearby:").withStyle(ChatFormatting.GOLD));
		for (TamableAnimal pet : pets) {
			String name = pet.getCustomName() != null ? pet.getCustomName().getString() : pet.getName().getString();
			int distance = (int) Math.sqrt(pet.distanceToSqr(sp));
			sp.sendSystemMessage(Component.literal("  " + name + " — " + distance + " blocks"
				+ (pet.isOrderedToSit() ? " (sitting)" : "")).withStyle(ChatFormatting.GRAY));
		}
		return 1;
	}

	private int find(ServerPlayer sp) {
		if (!settings.istAktiv()) {
			return 0;
		}
		List<TamableAnimal> pets = myPets(sp);
		for (TamableAnimal pet : pets) {
			pet.addEffect(new MobEffectInstance(MobEffects.GLOWING, findGlowSeconds.get() * 20, 0, true, false));
		}
		Broadcast.actionbar(sp, Component.literal(pets.size() + " pet(s) are glowing now").withStyle(ChatFormatting.GOLD));
		return 1;
	}

	private int orderToSit(ServerPlayer sp, boolean sit) {
		if (!settings.istAktiv()) {
			return 0;
		}
		int count = 0;
		for (TamableAnimal pet : myPets(sp)) {
			pet.setOrderedToSit(sit);
			count++;
		}
		Broadcast.actionbar(sp, Component.literal(count + " pet(s) " + (sit ? "wait here" : "follow you"))
			.withStyle(ChatFormatting.GOLD));
		return 1;
	}
}
