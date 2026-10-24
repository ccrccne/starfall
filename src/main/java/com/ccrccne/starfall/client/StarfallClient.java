package com.ccrccne.starfall.client;

import com.ccrccne.starfall.StarfallParticles;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class StarfallClient {
	private StarfallClient() {
	}

	public static void init(IEventBus modBus) {
		modBus.addListener(StarfallClient::onRegisterParticles);
		NeoForge.EVENT_BUS.addListener(StarfallClient::onClientTick);
	}

	private static void onRegisterParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(StarfallParticles.STAR_STREAK.get(), StarStreakParticle.Provider::new);
	}

	private static void onClientTick(ClientTickEvent.Post event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null) {
			SkyStreaks.tick(minecraft.level);
		}
	}
}
