package com.ccrccne.starfall;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StarfallParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Starfall.MOD_ID);

	// Destello grande y brillante de las estrellas fugaces; se ve aunque esté lejos (en el cielo)
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STAR_STREAK = PARTICLES.register("star_streak", () -> new SimpleParticleType(true) {
	});

	private StarfallParticles() {
	}
}
