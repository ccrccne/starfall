package com.ccrccne.starfall.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

// Puntito de luz que se apaga y encoge rápido; muchos seguidos forman la estela de una estrella fugaz
public class StarStreakParticle extends TextureSheetParticle {
	private float startSize;

	StarStreakParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
		super(level, x, y, z);
		this.pickSprite(sprites);
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
		this.hasPhysics = false;
		this.gravity = 0.0F;
		this.friction = 1.0F;
		// Cuanto más dura cada punto, más larga se ve la estela
		this.lifetime = 22 + this.random.nextInt(8);

		// Casi todas blancas con un toque amarillo; algunas azul cielo
		if (this.random.nextInt(4) == 0) {
			this.setColor(0.72F, 0.88F, 1.0F);
		} else {
			this.setColor(1.0F, 0.97F, 0.85F);
		}
		this.startSize = this.quadSize;
	}

	@Override
	public void tick() {
		super.tick();
		float life = (float) this.age / this.lifetime;
		this.setAlpha(1.0F - life);
		// La cola se afina hacia el final, como una estrella fugaz de verdad
		this.quadSize = this.startSize * (1.0F - life * 0.75F);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880;
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z,
				double xd, double yd, double zd) {
			StarStreakParticle particle = new StarStreakParticle(level, x, y, z, xd, yd, zd, this.sprites);
			// Más grande cuanto más lejos, para que se vea igual de bien en el cielo que cerca
			double distance = Math.sqrt(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z));
			// Fina: grosor de una línea delgada aunque esté lejos
			particle.quadSize = (float) Math.max(0.08, distance * 0.0055) * (0.85F + level.random.nextFloat() * 0.3F);
			particle.startSize = particle.quadSize;
			return particle;
		}
	}
}
