package com.ccrccne.starfall;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

// Estrellas fugaces que caen de verdad, solo durante la lluvia de estrellas
// (las del cielo son solo visuales, ver SkyStreaks en el cliente).
// No son entidades: el servidor mueve un punto y dibuja su estela con partículas visibles de lejos.
public final class ShootingStars {
	// Durante la lluvia, cada jugador tiene 1 entre CHANCE de que le caiga una cerca en cada tick (de media, una cada 45 segundos)
	private static final int CHANCE = 900;
	private static final int FALL_TICKS = 50;
	private static final List<Star> ACTIVE = new ArrayList<>();
	private static long announcedDay = Long.MIN_VALUE;

	private ShootingStars() {
	}

	public static void tick(ServerLevel level) {
		if (StarShower.isActive(level)) {
			announce(level);
			RandomSource random = level.getRandom();
			for (ServerPlayer player : level.players()) {
				if (!player.isSpectator() && random.nextInt(CHANCE) == 0) {
					spawnNear(level, player);
				}
			}
		}

		Iterator<Star> it = ACTIVE.iterator();
		while (it.hasNext()) {
			Star star = it.next();
			if (star.level == level && star.tick()) {
				it.remove();
			}
		}
	}

	// Lanza una estrella que caerá a unos bloques del jugador
	public static void spawnNear(ServerLevel level, ServerPlayer player) {
		RandomSource random = level.getRandom();
		double angle = random.nextDouble() * Math.PI * 2;
		double distance = 12 + random.nextDouble() * 20;
		int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
		int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		BlockPos target = new BlockPos(x, y, z);

		// Viene desde arriba, en la misma dirección que el resto de la lluvia
		double direction = StarShower.direction(StarShower.day(level));
		Vec3 end = Vec3.atBottomCenterOf(target);
		Vec3 start = end.add(-Math.cos(direction) * 60, 70, -Math.sin(direction) * 60);
		ACTIVE.add(new Star(level, start, end.subtract(start).scale(1.0 / FALL_TICKS), target));
	}

	// Una vez por noche de lluvia: mensaje y una campanita para que todos miren al cielo
	private static void announce(ServerLevel level) {
		long day = StarShower.day(level);
		if (day == announcedDay) {
			return;
		}
		announcedDay = day;
		for (ServerPlayer player : level.players()) {
			player.sendSystemMessage(Component.translatable("message.starfall.shower").withStyle(ChatFormatting.AQUA));
			level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 1.0F, 1.4F);
		}
	}

	private static final class Star {
		final ServerLevel level;
		final Vec3 velocity;
		final BlockPos target;
		Vec3 pos;
		int ticks;

		Star(ServerLevel level, Vec3 pos, Vec3 velocity, BlockPos target) {
			this.level = level;
			this.pos = pos;
			this.velocity = velocity;
			this.target = target;
		}

		// Devuelve true cuando ya cayó
		boolean tick() {
			Vec3 prev = pos;
			pos = pos.add(velocity);
			ticks++;

			// Estela: varios puntos entre la posición anterior y la nueva para que no tenga huecos
			for (int i = 0; i < 6; i++) {
				Vec3 p = prev.lerp(pos, i / 6.0);
				particles(StarfallParticles.STAR_STREAK.get(), p, 1, 0.0, 0.0);
			}
			particles(ParticleTypes.FIREWORK, pos, 1, 0.15, 0.02);

			if (ticks >= FALL_TICKS) {
				land();
				return true;
			}
			return false;
		}

		void land() {
			Vec3 at = Vec3.atBottomCenterOf(target);
			particles(ParticleTypes.FIREWORK, at.add(0, 0.5, 0), 40, 0.3, 0.15);
			particles(ParticleTypes.END_ROD, at.add(0, 0.5, 0), 20, 0.4, 0.05);
			level.playSound(null, target, SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR, SoundSource.AMBIENT, 2.0F, 1.2F);
			level.playSound(null, target, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5F, 1.0F);

			if (!level.isLoaded(target)) {
				return;
			}

			BlockState here = level.getBlockState(target);
			BlockPos below = target.below();
			boolean canPlace = here.canBeReplaced() && here.getFluidState().isEmpty()
					&& level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
			if (canPlace) {
				level.setBlock(target, StarfallBlocks.FALLEN_STAR.get().defaultBlockState(), Block.UPDATE_ALL);
				for (ServerPlayer player : level.players()) {
					if (player.blockPosition().closerThan(target, 64)) {
						player.displayClientMessage(Component.translatable("message.starfall.star_fell"), true);
					}
				}
			}
		}

		// Partículas que se ven desde lejos (como los fuegos artificiales)
		void particles(SimpleParticleType type, Vec3 p, int count, double spread, double speed) {
			for (ServerPlayer player : level.players()) {
				level.sendParticles(player, type, true, p.x, p.y, p.z, count, spread, spread, spread, speed);
			}
		}
	}
}
