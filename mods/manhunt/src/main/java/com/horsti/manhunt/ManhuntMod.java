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
import net.minecraft.world.entity.EntityType;
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
 * Statt einer Kompass-Nadel (die zwischen Dimensionen nutzlos wird) zeigt der
 * gehaltene Kompass eine Peilung in der Actionbar: Richtung, Entfernung, Dimension.
 */
public class ManhuntMod implements ModInitializer {
	private final ModSettings settings = new ModSettings("manhunt", true);
	private final IntSetting schonfristSek = settings.add(new IntSetting("schonfristSek", "Jaeger-Freeze am Start", 30, 0, 300));
	private final BoolSetting runnerRespawn = settings.add(new BoolSetting("runnerRespawn", "Runner duerfen respawnen", false));
	private final IntSetting peilungSek = settings.add(new IntSetting("peilungSek", "Sekunden zwischen Peilungen", 1, 1, 10));

	private final Set<UUID> runner = new HashSet<>();
	private final Set<UUID> jaeger = new HashSet<>();
	private final Set<UUID> toteRunner = new HashSet<>();
	private final Map<UUID, Integer> zielIndex = new HashMap<>();
	private boolean laeuft = false;
	private long startTick = 0;
	private long jetztTick = 0;

	@Override
	public void onInitialize() {
		new HorstiMod("manhunt", "Manhunt", settings)
			.onToggle(() -> {
				if (!settings.istAktiv()) {
					laeuft = false;
				}
			})
			.extra((root, ctx) -> {
				root.then(Commands.literal("runner")
					.then(Commands.literal("add").then(Commands.argument("spieler", EntityArgument.player())
						.executes(c -> rolle(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler"), runner, true))))
					.then(Commands.literal("remove").then(Commands.argument("spieler", EntityArgument.player())
						.executes(c -> rolle(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler"), runner, false)))));
				root.then(Commands.literal("hunter")
					.then(Commands.literal("add").then(Commands.argument("spieler", EntityArgument.player())
						.executes(c -> rolle(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler"), jaeger, true))))
					.then(Commands.literal("remove").then(Commands.argument("spieler", EntityArgument.player())
						.executes(c -> rolle(c.getSource().getServer(), EntityArgument.getPlayer(c, "spieler"), jaeger, false)))));
				root.then(Commands.literal("start").executes(c -> start(c.getSource().getServer())));
				root.then(Commands.literal("stop").executes(c -> {
					beenden(c.getSource().getServer(), null);
					return 1;
				}));
			})
			.registrieren();

		// Jaeger schalten ihr Ziel per /ziel durch
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) ->
			dispatcher.register(Commands.literal("ziel").executes(c -> {
				ServerPlayer sp = c.getSource().getPlayerOrException();
				if (!jaeger.contains(sp.getUUID())) {
					return 0;
				}
				List<ServerPlayer> ziele = lebendeRunner(sp.getServer());
				if (ziele.isEmpty()) {
					return 0;
				}
				int neu = (zielIndex.getOrDefault(sp.getUUID(), 0) + 1) % ziele.size();
				zielIndex.put(sp.getUUID(), neu);
				Broadcast.actionbar(sp, Component.literal("Ziel: " + ziele.get(neu).getName().getString())
					.withStyle(ChatFormatting.GOLD));
				return 1;
			})));

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!laeuft || !(entity instanceof ServerPlayer sp)) {
				return;
			}
			if (runner.contains(sp.getUUID()) && !runnerRespawn.get()) {
				toteRunner.add(sp.getUUID());
				Broadcast.chat(HorstiServer.get(), Component.literal(sp.getName().getString() + " ist gefallen!")
					.withStyle(ChatFormatting.RED));
				if (lebendeRunner(HorstiServer.get()).isEmpty()) {
					beenden(HorstiServer.get(), "jaeger");
				}
			}
			// Drachen-Sieg: der Enderdrache wird von einem Runner erlegt
			if (entity.getType() == EntityType.ENDER_DRAGON
				&& source.getEntity() instanceof ServerPlayer toeter && runner.contains(toeter.getUUID())) {
				beenden(HorstiServer.get(), "runner");
			}
		});

		Ticker.alleTicks(20, this::sekundenTick);
	}

	private int rolle(MinecraftServer server, ServerPlayer sp, Set<UUID> menge, boolean hinzu) {
		if (hinzu) {
			menge.add(sp.getUUID());
			runner.removeIf(id -> menge != runner && id.equals(sp.getUUID()));
			jaeger.removeIf(id -> menge != jaeger && id.equals(sp.getUUID()));
			menge.add(sp.getUUID());
		} else {
			menge.remove(sp.getUUID());
		}
		Broadcast.chat(server, Component.literal("[Manhunt] " + sp.getName().getString() + " ist "
			+ (hinzu ? (menge == runner ? "Runner" : "Jäger") : "wieder frei")).withStyle(ChatFormatting.GRAY));
		return 1;
	}

	private int start(MinecraftServer server) {
		if (runner.isEmpty() || jaeger.isEmpty()) {
			Broadcast.chat(server, Component.literal("[Manhunt] Erst Runner und Jäger festlegen.").withStyle(ChatFormatting.RED));
			return 0;
		}
		laeuft = true;
		startTick = jetztTick;
		toteRunner.clear();
		for (UUID id : jaeger) {
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null && !sp.getInventory().contains(new net.minecraft.world.item.ItemStack(Items.COMPASS))) {
				sp.getInventory().add(new net.minecraft.world.item.ItemStack(Items.COMPASS));
			}
		}
		Broadcast.titelAlle(server, Component.literal("MANHUNT!").withStyle(ChatFormatting.DARK_RED),
			Component.literal(schonfristSek.get() > 0
				? "Die Jäger warten noch " + schonfristSek.get() + " Sekunden — lauft!"
				: "Los!"));
		return 1;
	}

	private List<ServerPlayer> lebendeRunner(MinecraftServer server) {
		List<ServerPlayer> liste = new ArrayList<>();
		for (UUID id : runner) {
			if (toteRunner.contains(id)) {
				continue;
			}
			ServerPlayer sp = server.getPlayerList().getPlayer(id);
			if (sp != null) {
				liste.add(sp);
			}
		}
		return liste;
	}

	private void sekundenTick(MinecraftServer server) {
		jetztTick += 20;
		if (!laeuft || !settings.istAktiv()) {
			return;
		}
		long laufSek = (jetztTick - startTick) / 20;
		boolean schonfrist = laufSek < schonfristSek.get();
		List<ServerPlayer> ziele = lebendeRunner(server);

		for (UUID id : jaeger) {
			ServerPlayer jaegerSp = server.getPlayerList().getPlayer(id);
			if (jaegerSp == null) {
				continue;
			}
			if (schonfrist) {
				jaegerSp.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, true, false));
				jaegerSp.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 250, true, false));
				Broadcast.actionbar(jaegerSp, Component.literal("Schonfrist: noch "
					+ (schonfristSek.get() - laufSek) + "s").withStyle(ChatFormatting.GRAY));
				continue;
			}
			if (ziele.isEmpty() || laufSek % peilungSek.get() != 0) {
				continue;
			}
			boolean haeltKompass = jaegerSp.getMainHandItem().is(Items.COMPASS)
				|| jaegerSp.getOffhandItem().is(Items.COMPASS);
			if (!haeltKompass) {
				continue;
			}
			ServerPlayer ziel = ziele.get(Math.min(zielIndex.getOrDefault(id, 0), ziele.size() - 1));
			Broadcast.actionbar(jaegerSp, peilung(jaegerSp, ziel));
		}
	}

	private Component peilung(ServerPlayer jaegerSp, ServerPlayer ziel) {
		if (jaegerSp.level().dimension() != ziel.level().dimension()) {
			return Component.literal("✦ " + ziel.getName().getString() + " ist in " + dimensionsName(ziel.level()))
				.withStyle(ChatFormatting.LIGHT_PURPLE);
		}
		double dx = ziel.getX() - jaegerSp.getX();
		double dz = ziel.getZ() - jaegerSp.getZ();
		int entfernung = (int) Math.sqrt(dx * dx + dz * dz);
		// Winkel zwischen Blickrichtung und Ziel, auf 8 Pfeile gerundet
		double zielWinkel = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
		double relativ = ((zielWinkel - jaegerSp.getYRot()) % 360 + 540) % 360 - 180;
		String[] pfeile = {"▲", "◤", "◀", "◣", "▼", "◢", "▶", "◥"};
		String pfeil = pfeile[(int) Math.round((relativ + 180) / 45.0) % 8];
		return Component.literal(pfeil + " " + ziel.getName().getString() + " — " + entfernung + " Blöcke")
			.withStyle(ChatFormatting.GOLD);
	}

	private static String dimensionsName(Level level) {
		if (level.dimension() == Level.NETHER) {
			return "im Nether";
		}
		if (level.dimension() == Level.END) {
			return "im Ende";
		}
		return "in der Oberwelt";
	}

	private void beenden(MinecraftServer server, String sieger) {
		if (!laeuft) {
			return;
		}
		laeuft = false;
		if (sieger != null) {
			Broadcast.titelAlle(server, Component.literal(sieger.equals("runner")
					? "Die Runner gewinnen!" : "Die Jäger gewinnen!").withStyle(ChatFormatting.GOLD),
				null);
		}
		toteRunner.clear();
	}
}
