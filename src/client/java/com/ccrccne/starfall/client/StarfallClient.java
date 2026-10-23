package com.ccrccne.starfall.client;

import com.ccrccne.starfall.StarfallParticles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class StarfallClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(StarfallParticles.STAR_STREAK, StarStreakParticle.Provider::new);
		ClientTickEvents.END_WORLD_TICK.register(SkyStreaks::tick);
	}
}
