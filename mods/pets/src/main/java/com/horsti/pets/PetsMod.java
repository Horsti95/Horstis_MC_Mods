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
	private final BoolSetting schutz = settings.add(new BoolSetting("schutz", "Friendly-Fire-Schutz", true));
	private final BoolSetting schutzSneak = settings.add(new BoolSetting("schutzSneak", "Sneak + Schlag umgeht den Schutz", true));
	private final IntSetting findGlowSek = settings.add(new IntSetting("findGlowSek", "Glow-Dauer bei /pets find", 30, 5, 60));
	private final IntSetting radius = settings.add(new IntSetting("radius", "Wirkradius fuer find/stay/follow", 48, 16, 128));

	@Override
	public void onInitialize() {
		new HorstiMod("pets", "Pets", settings).registrieren();

		// Friendly-Fire-Schutz: eigener Schlag auf eigenes Tier wird abgebrochen
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, betrag) -> {
			if (!settings.istAktiv() || !schutz.get() || !(entity instanceof TamableAnimal tier)) {
				return true;
			}
			Entity angreifer = source.getEntity();
			if (!(angreifer instanceof ServerPlayer sp) || !istBesitzer(tier, sp)) {
				return true;
			}
			if (schutzSneak.get() && sp.isShiftKeyDown()) {
				return true; // bewusstes Schlachten erlauben
			}
			Broadcast.actionbar(sp, Component.literal("Das ist dein Tier! (Sneak + Schlag zum Trotzdem-Schlagen)")
				.withStyle(ChatFormatting.GRAY));
			return false;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("pets")
				.then(Commands.literal("liste").executes(c -> liste(c.getSource().getPlayerOrException())))
				.then(Commands.literal("find").executes(c -> find(c.getSource().getPlayerOrException())))
				.then(Commands.literal("stay").executes(c -> sitzen(c.getSource().getPlayerOrException(), true)))
				.then(Commands.literal("follow").executes(c -> sitzen(c.getSource().getPlayerOrException(), false)))));
	}

	/** Zentrale Besitzer-Pruefung — einziger Beruehrungspunkt mit der Tamable-API. */
	private static boolean istBesitzer(TamableAnimal tier, Player spieler) {
		return tier.isTame() && tier.getOwner() == spieler;
	}

	private List<TamableAnimal> meineTiere(ServerPlayer sp) {
		AABB box = sp.getBoundingBox().inflate(radius.get());
		return ((ServerLevel) sp.level()).getEntitiesOfClass(TamableAnimal.class, box, t -> istBesitzer(t, sp));
	}

	private int liste(ServerPlayer sp) {
		if (!settings.istAktiv()) {
			return 0;
		}
		List<TamableAnimal> tiere = meineTiere(sp);
		if (tiere.isEmpty()) {
			sp.sendSystemMessage(Component.literal("[Pets] Keine eigenen Tiere in " + radius.get() + " Bloecken.")
				.withStyle(ChatFormatting.GRAY));
			return 1;
		}
		sp.sendSystemMessage(Component.literal("[Pets] " + tiere.size() + " Tier(e) in der Naehe:").withStyle(ChatFormatting.GOLD));
		for (TamableAnimal tier : tiere) {
			String name = tier.getCustomName() != null ? tier.getCustomName().getString() : tier.getName().getString();
			int entfernung = (int) Math.sqrt(tier.distanceToSqr(sp));
			sp.sendSystemMessage(Component.literal("  " + name + " — " + entfernung + " Bloecke"
				+ (tier.isOrderedToSit() ? " (sitzt)" : "")).withStyle(ChatFormatting.GRAY));
		}
		return 1;
	}

	private int find(ServerPlayer sp) {
		if (!settings.istAktiv()) {
			return 0;
		}
		List<TamableAnimal> tiere = meineTiere(sp);
		for (TamableAnimal tier : tiere) {
			tier.addEffect(new MobEffectInstance(MobEffects.GLOWING, findGlowSek.get() * 20, 0, true, false));
		}
		Broadcast.actionbar(sp, Component.literal(tiere.size() + " Tier(e) leuchten jetzt").withStyle(ChatFormatting.GOLD));
		return 1;
	}

	private int sitzen(ServerPlayer sp, boolean sitzen) {
		if (!settings.istAktiv()) {
			return 0;
		}
		int anzahl = 0;
		for (TamableAnimal tier : meineTiere(sp)) {
			tier.setOrderedToSit(sitzen);
			anzahl++;
		}
		Broadcast.actionbar(sp, Component.literal(anzahl + " Tier(e) " + (sitzen ? "warten hier" : "folgen dir"))
			.withStyle(ChatFormatting.GOLD));
		return 1;
	}
}
