package com.ccrccne.starfall;

import net.minecraft.world.level.Level;

// Lluvia de estrellas: un evento que ocurre solo algunas noches.
// Se decide con una fórmula a partir del número de día, así el servidor y todos los
// jugadores saben, sin enviarse nada, qué noches hay lluvia y hacia dónde van las estrellas.
public final class StarShower {
	public static final long DAY_TICKS = 24000;
	public static final long NIGHT_START = 13000;
	private static final long NIGHT_END = 23000;
	// Una de cada SHOWER_EVERY noches, de media
	private static final int SHOWER_EVERY = 3;

	private StarShower() {
	}

	public static long day(Level level) {
		return Math.floorDiv(level.getOverworldClockTime(), DAY_TICKS);
	}

	public static long timeOfDay(Level level) {
		return Math.floorMod(level.getOverworldClockTime(), DAY_TICKS);
	}

	// La primera noche de un mundo siempre hay lluvia, para descubrir el evento pronto
	public static boolean isShowerNight(long day) {
		return day == 0 || Math.floorMod(hash(day), SHOWER_EVERY) == 0;
	}

	// ¿Está pasando ahora mismo?
	public static boolean isActive(Level level) {
		return level.dimension() == Level.OVERWORLD && level.isDarkOutside() && isShowerNight(day(level));
	}

	// Dirección (en radianes) hacia la que viajan todas las estrellas de esa noche
	public static double direction(long day) {
		return Math.floorMod(hash(day * 31 + 7), 360) * Math.PI / 180.0;
	}

	// Primer momento con lluvia a partir de ahora (para el comando de prueba)
	public static long nextShowerStart(Level level) {
		long day = day(level);
		long time = timeOfDay(level);
		if (isShowerNight(day) && time < NIGHT_END) {
			return day * DAY_TICKS + Math.max(time, NIGHT_START);
		}
		long next = day + 1;
		while (!isShowerNight(next)) {
			next++;
		}
		return next * DAY_TICKS + NIGHT_START;
	}

	private static int hash(long value) {
		long h = value * 0x9E3779B97F4A7C15L;
		h ^= h >>> 31;
		h *= 0xBF58476D1CE4E5B9L;
		h ^= h >>> 29;
		return (int) (h & 0x7FFFFFFF);
	}
}
