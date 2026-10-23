package com.ccrccne.starfall.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ccrccne.starfall.StarShower;
import com.ccrccne.starfall.StarfallParticles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

// Estrellas fugaces solo visuales durante la lluvia de estrellas: todas cruzan el cielo
// en la misma dirección esa noche. Se dibujan solo en tu pantalla; no tocan el mundo.
public final class SkyStreaks {
	// Durante la lluvia, de media una estrella cada CHANCE ticks (20 ticks = 1 segundo)
	private static final int CHANCE = 22;
	// A veces salen varias seguidas
	private static final int BURST_CHANCE = 400;
	private static final List<Streak> ACTIVE = new ArrayList<>();

	private SkyStreaks() {
	}

	public static void tick(ClientLevel level) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.isPaused()) {
			return;
		}

		if (StarShower.isActive(level) && !level.isRaining()) {
			RandomSource random = level.getRandom();
			Vec3 camera = minecraft.gameRenderer.mainCamera().position();
			double direction = StarShower.direction(StarShower.day(level));
			if (random.nextInt(CHANCE) == 0) {
				ACTIVE.add(Streak.random(camera, direction, random));
			}
			if (random.nextInt(BURST_CHANCE) == 0) {
				for (int i = 0; i < 3 + random.nextInt(4); i++) {
					ACTIVE.add(Streak.random(camera, direction, random).delayed(i * (4 + random.nextInt(6))));
				}
			}
		}

		Iterator<Streak> it = ACTIVE.iterator();
		while (it.hasNext()) {
			if (it.next().tick(level)) {
				it.remove();
			}
		}
	}

	private static final class Streak {
		Vec3 pos;
		final Vec3 velocity;
		int ticksLeft;
		int delay;

		Streak(Vec3 pos, Vec3 velocity, int ticks) {
			this.pos = pos;
			this.velocity = velocity;
			this.ticksLeft = ticks;
		}

		// Pasa por un punto al azar del cielo, viajando en la dirección de la lluvia (con un poquito de variación)
		static Streak random(Vec3 camera, double direction, RandomSource random) {
			double angle = random.nextDouble() * Math.PI * 2;
			double distance = 60 + random.nextDouble() * 50;
			double height = 60 + random.nextDouble() * 35;
			Vec3 through = camera.add(Math.cos(angle) * distance, height, Math.sin(angle) * distance);

			double dir = direction + (random.nextDouble() - 0.5) * 0.25;
			double speed = 3.2 + random.nextDouble() * 1.3;
			Vec3 velocity = new Vec3(Math.cos(dir) * speed, -speed * (0.10 + random.nextDouble() * 0.10), Math.sin(dir) * speed);

			int ticks = 24 + random.nextInt(12);
			Vec3 start = through.subtract(velocity.scale(ticks / 2.0));
			return new Streak(start, velocity, ticks);
		}

		Streak delayed(int ticks) {
			this.delay = ticks;
			return this;
		}

		// Devuelve true cuando ya terminó
		boolean tick(ClientLevel level) {
			if (delay > 0) {
				delay--;
				return false;
			}

			Vec3 prev = pos;
			pos = pos.add(velocity);
			// Muchos puntitos por tick para que la estela sea una línea fina y continua
			for (int i = 0; i < 14; i++) {
				Vec3 p = prev.lerp(pos, i / 14.0);
				level.addParticle(StarfallParticles.STAR_STREAK, true, true, p.x, p.y, p.z, 0.0, 0.0, 0.0);
			}
			return --ticksLeft <= 0;
		}
	}
}
