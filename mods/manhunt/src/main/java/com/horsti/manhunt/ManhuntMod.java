package com.horsti.manhunt;

import com.horsti.core.HorstiMod;
import com.horsti.core.HorstiServer;
import com.horsti.core.settings.BoolSetting;
import com.horsti.core.settings.IntSetting;
import com.horsti.core.settings.ModSettings;
import com.horsti.core.util.Broadcast;
import com.horsti.core.util.Ticker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.horsti.core.util.Mobs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Instead of a compass needle (useless across dimensions), the held compass shows a
 * bearing: direction, distance, dimension. With horstihud it becomes a HUD line,
 * without it the plain action bar.
 */
public class ManhuntMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("manhunt", true);
	private final IntSetting graceSeconds = settings.add(new IntSetting("graceSeconds", "hunter freeze at the start", 30, 0, 300));
	private final BoolSetting runnerRespawn = settings.add(new BoolSetting("runnerRespawn", "runners may respawn", false));
	private final IntSetting bearingSeconds = settings.add(new IntSetting("bearingSeconds", "seconds between bearings", 1, 1, 10));

	private final Set<UUID> runners = new HashSet<>();
	private final Set<UUID> hunters = new HashSet<>();
	private final Set<UUID> deadRunners = new HashSet<>();
	private final Map<UUID, Integer> targetIndex = new HashMap<>();
	private boolean running = false;
	private long startTick = 0;
	private long nowTick = 0;

	@Override
	public void onInitialize() {
		new HorstiMod("manhunt", "Manhunt", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					running = false;
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("runner")
					.then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
						.executes(c -> role(c.getSource().getServer(), EntityArgument.getPlayer(c, "player"), runners, true))))
					.then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.player())
						.executes(c -> role(c.getSource().getServer(), EntityArgument.getPlayer(c, "player"), runners, false)))));
				root.then(Commands.literal("hunter")
					.then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
						.executes(c -> role(c.getSource().getServer(), EntityArgument.getPlayer(c, "player"), hunters, true))))
					.then(Commands.literal("remove").then(Commands.argument("player", EntityArgument.player())
						.executes(c -> role(c.getSource().getServer(), EntityArgument.getPlayer(c, "player"), hunters, false)))));
				root.then(Commands.literal("start").executes(c -> start(c.getSource().getServer())));
				root.then(Commands.literal("stop").executes(c -> {
					finish(c.getSource().getServer(), null);
					return 1;
				}));
			})
			.registrieren();

		// Hunters cycle their target with /target
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("target").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				if (!hunters.contains(sp.getUUID())) {
					return 0;
				}
				List<ServerPlayer> targets = livingRunners(HorstiServer.get());
				if (targets.isEmpty()) {
					return 0;
				}
				int next = (targetIndex.getOrDefault(sp.getUUID(), 0) + 1) % targets.size();
				targetIndex.put(sp.getUUID(), next);
				Broadcast.actionbar(sp, Component.literal("Target: " + targets.get(next).getName().getString())
					.withStyle(ChatFormatting.GOLD));
				return 1;
			})));

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!running || !(entity instanceof ServerPlayer sp)) {
				return;
			}
			if (runners.contains(sp.getUUID()) && !runnerRespawn.get()) {
				deadRunners.add(sp.getUUID());
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " has fallen!")
					.withStyle(ChatFormatting.RED));
				if (livingRunners(HorstiServer.get()).isEmpty()) {
					finish(HorstiServer.get(), "hunters");
				}
			}
			// Dragon win: a runner slays the ender dragon
			if (Mobs.istTyp(entity, "minecraft:ender_dragon")
				&& source.getEntity() instanceof ServerPlayer slayer && runners.contains(slayer.getUUID())) {
				finish(HorstiServer.get(), "runners");
			}
		});

		Ticker.alleTicks(20, this::secondTick);
	}

	private int role(MinecraftServer server, ServerPlayer sp, Set<UUID> group, boolean add) {
		if (add) {
			group.add(sp.getUUID());
			runners.removeIf(id -> group != runners && id.equals(sp.getUUID()));
			hunters.removeIf(id -> group != hunters && id.equals(sp.getUUID()));
			group.add(sp.getUUID());
		} else {
			group.remove(sp.getUUID());
		}
		Broadcast.chat(server, Component.literal("[Manhunt] " + sp.getName().getString() + " is "
			+ (add ? (group == runners ? "a runner" : "a hunter") : "free again")).withStyle(ChatFormatting.GRAY));
		return 1;
	}

	private int start(MinecraftServer server) {
		if (runners.isEmpty() || hunters.isEmpty()) {
			Broadcast.chat(server, Component.literal("[Manhunt] Assign runners and hunters first.").withStyle(ChatFormatting.RED));
			return 0;
		}
		running = true;
		startTick = nowTick;
		deadRunners.clear();
		for (UUID id : hunters) {
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null && !sp.getInventory().contains(new net.minecraft.world.item.ItemStack(Items.COMPASS))) {
				sp.getInventory().add(new net.minecraft.world.item.ItemStack(Items.COMPASS));
			}
		}
		Broadcast.titelAlle(server, Component.literal("MANHUNT!").withStyle(ChatFormatting.DARK_RED),
			Component.literal(graceSeconds.get() > 0
				? "The hunters wait another " + graceSeconds.get() + " seconds — run!"
				: "Go!"));
		return 1;
	}

	private List<ServerPlayer> livingRunners(MinecraftServer server) {
		List<ServerPlayer> list = new ArrayList<>();
		for (UUID id : runners) {
			if (deadRunners.contains(id)) {
				continue;
			}
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null) {
				list.add(sp);
			}
		}
		return list;
	}

	private void secondTick(MinecraftServer server) {
		nowTick += 20;
		if (!running || !settings.istAktiv()) {
			return;
		}
		long secondsRunning = (nowTick - startTick) / 20;
		boolean inGrace = secondsRunning < graceSeconds.get();
		List<ServerPlayer> targets = livingRunners(server);

		for (UUID id : hunters) {
			ServerPlayer hunter = server.getPlayerList().getPlayer(id);
			if (hunter == null) {
				continue;
			}
			if (inGrace) {
				hunter.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, true, false));
				hunter.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 250, true, false));
				Broadcast.actionbar(hunter, Component.literal("Grace period: "
					+ (graceSeconds.get() - secondsRunning) + "s left").withStyle(ChatFormatting.GRAY));
				continue;
			}
			if (targets.isEmpty() || secondsRunning % bearingSeconds.get() != 0) {
				continue;
			}
			boolean holdsCompass = hunter.getMainHandItem().is(Items.COMPASS)
				|| hunter.getOffhandItem().is(Items.COMPASS);
			if (!holdsCompass) {
				Broadcast.hudAus(hunter, "manhunt");
				continue;
			}
			ServerPlayer target = targets.get(Math.min(targetIndex.getOrDefault(id, 0), targets.size() - 1));
			// hud() instead of actionbar(): with horstihud the bearing stays on screen,
			// without it this is exactly the action bar line from before.
			Broadcast.hud(hunter, "manhunt", bearing(hunter, target));
		}
	}

	private Component bearing(ServerPlayer hunter, ServerPlayer target) {
		if (hunter.level().dimension() != target.level().dimension()) {
			return Component.literal("✦ " + target.getName().getString() + " is " + dimensionName(target.level()))
				.withStyle(ChatFormatting.LIGHT_PURPLE);
		}
		double dx = target.getX() - hunter.getX();
		double dz = target.getZ() - hunter.getZ();
		int distance = (int) Math.sqrt(dx * dx + dz * dz);
		// Angle between the view direction and the target, rounded to 8 arrows
		double targetAngle = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
		double relative = ((targetAngle - hunter.getYRot()) % 360 + 540) % 360 - 180;
		String[] arrows = {"▲", "◤", "◀", "◣", "▼", "◢", "▶", "◥"};
		String arrow = arrows[(int) Math.round((relative + 180) / 45.0) % 8];
		return Component.literal(arrow + " " + target.getName().getString() + " — " + distance + " blocks")
			.withStyle(ChatFormatting.GOLD);
	}

	private static String dimensionName(Level level) {
		if (level.dimension() == Level.NETHER) {
			return "in the Nether";
		}
		if (level.dimension() == Level.END) {
			return "in the End";
		}
		return "in the Overworld";
	}

	private void finish(MinecraftServer server, String winner) {
		if (!running) {
			return;
		}
		running = false;
		if (winner != null) {
			Broadcast.titelAlle(server, Component.literal(winner.equals("runners")
					? "The runners win!" : "The hunters win!").withStyle(ChatFormatting.GOLD),
				null);
		}
		deadRunners.clear();
	}
}
