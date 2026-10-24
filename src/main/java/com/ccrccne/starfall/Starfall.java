package com.ccrccne.starfall;

import com.ccrccne.starfall.client.StarfallClient;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Starfall.MOD_ID)
public class Starfall {
	public static final String MOD_ID = "starfall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public Starfall() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		StarfallParticles.PARTICLES.register(modBus);
		StarfallBlocks.BLOCKS.register(modBus);
		StarfallItems.ITEMS.register(modBus);
		StarfallItems.TABS.register(modBus);

		MinecraftForge.EVENT_BUS.addListener(Starfall::onLevelTick);
		MinecraftForge.EVENT_BUS.addListener(Starfall::onRegisterCommands);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			StarfallClient.init(modBus);
		}

		LOGGER.info("Starfall: las estrellas están listas para caer");
	}

	private static void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel level) {
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
		return new ResourceLocation(MOD_ID, path);
	}
}
