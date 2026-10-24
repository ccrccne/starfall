package com.ccrccne.starfall.client;

import com.ccrccne.starfall.StarfallParticles;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class StarfallClient {
	private StarfallClient() {
	}

	public static void init(IEventBus modBus) {
		modBus.addListener(StarfallClient::onRegisterParticles);
		MinecraftForge.EVENT_BUS.addListener(StarfallClient::onClientTick);
	}

	private static void onRegisterParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(StarfallParticles.STAR_STREAK.get(), StarStreakParticle.Provider::new);
	}

	private static void onClientTick(TickEvent.ClientTickEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (event.phase == TickEvent.Phase.END && minecraft.level != null) {
			SkyStreaks.tick(minecraft.level);
		}
	}
}
