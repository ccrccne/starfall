package com.ccrccne.starfall.client;

import com.ccrccne.starfall.StarfallBlocks;
import com.ccrccne.starfall.StarfallParticles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import net.minecraft.client.renderer.RenderType;

public class StarfallClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(StarfallParticles.STAR_STREAK, StarStreakParticle.Provider::new);
		ClientTickEvents.END_WORLD_TICK.register(SkyStreaks::tick);

		// Las lámparas y la estrella caída tienen partes transparentes
		BlockRenderLayerMap.INSTANCE.putBlock(StarfallBlocks.FALLEN_STAR, RenderType.cutout());
		StarfallBlocks.PAPER_LAMPS.values().forEach(lamp -> BlockRenderLayerMap.INSTANCE.putBlock(lamp, RenderType.cutout()));
	}
}
