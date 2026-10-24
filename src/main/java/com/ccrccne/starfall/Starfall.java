package com.ccrccne.starfall;

import com.ccrccne.starfall.client.StarfallClient;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Starfall.MOD_ID)
public class Starfall {
	public static final String MOD_ID = "starfall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public Starfall(IEventBus modBus) {
		StarfallParticles.PARTICLES.register(modBus);
		StarfallBlocks.BLOCKS.register(modBus);
		StarfallItems.ITEMS.register(modBus);
		StarfallItems.TABS.register(modBus);

		NeoForge.EVENT_BUS.addListener(Starfall::onLevelTick);
		NeoForge.EVENT_BUS.addListener(Starfall::onRegisterCommands);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			StarfallClient.init(modBus);
		}

		LOGGER.info("Starfall: las estrellas están listas para caer");
	}

	private static void onLevelTick(LevelTickEvent.Post event) {
		if (event.getLevel() instanceof ServerLevel level) {
			ShootingStars.tick(level);
		}
	}

	// /starfall estrella: hace caer una estrella cerca (para probar o para presumir)
	// /starfall lluvia: salta a la próxima noche con lluvia de estrellas
	private static void onRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(
				Commands.literal("starfall")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("estrella").executes(context -> {
							ShootingStars.spawnNear(context.getSource().getLevel(), context.getSource().getPlayerOrException());
							context.getSource().sendSuccess(() -> Component.translatable("commands.starfall.star"), false);
							return 1;
						}))
						.then(Commands.literal("lluvia").executes(context -> {
							ServerLevel level = context.getSource().getServer().overworld();
							level.setDayTime(StarShower.nextShowerStart(level));
							context.getSource().sendSuccess(() -> Component.translatable("commands.starfall.shower"), false);
							return 1;
						})));
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
