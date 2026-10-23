package com.ccrccne.starfall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starfall implements ModInitializer {
	public static final String MOD_ID = "starfall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		StarfallParticles.init();
		StarfallBlocks.init();
		StarfallItems.init();

		ServerTickEvents.END_LEVEL_TICK.register(ShootingStars::tick);

		// /starfall estrella: hace caer una estrella cerca (para probar o para presumir)
		// /starfall lluvia: salta a la próxima noche con lluvia de estrellas
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> dispatcher.register(
				Commands.literal("starfall")
						.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.then(Commands.literal("estrella").executes(context -> {
							ShootingStars.spawnNear(context.getSource().getLevel(), context.getSource().getPlayerOrException());
							context.getSource().sendSuccess(() -> Component.translatable("commands.starfall.star"), false);
							return 1;
						}))
						.then(Commands.literal("lluvia").executes(context -> {
							ServerLevel level = context.getSource().getServer().overworld();
							Holder<WorldClock> clock = level.registryAccess().getOrThrow(WorldClocks.OVERWORLD);
							context.getSource().getServer().clockManager().setTotalTicks(clock, StarShower.nextShowerStart(level));
							context.getSource().sendSuccess(() -> Component.translatable("commands.starfall.shower"), false);
							return 1;
						}))));

		LOGGER.info("Starfall: las estrellas están listas para caer");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
