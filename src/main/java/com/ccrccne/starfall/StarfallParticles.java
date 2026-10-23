package com.ccrccne.starfall;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class StarfallParticles {
	// Destello grande y brillante de las estrellas fugaces; se ve aunque esté lejos (en el cielo)
	public static final SimpleParticleType STAR_STREAK = Registry.register(
			BuiltInRegistries.PARTICLE_TYPE, Starfall.id("star_streak"), FabricParticleTypes.simple(true));

	private StarfallParticles() {
	}

	public static void init() {
	}
}
