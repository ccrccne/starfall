package com.ccrccne.starfall;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class StarfallParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Starfall.MOD_ID);

	// Destello grande y brillante de las estrellas fugaces; se ve aunque esté lejos (en el cielo)
	public static final RegistryObject<SimpleParticleType> STAR_STREAK = PARTICLES.register("star_streak", () -> new SimpleParticleType(true));

	private StarfallParticles() {
	}
}
