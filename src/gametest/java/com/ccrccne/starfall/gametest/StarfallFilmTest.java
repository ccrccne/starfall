package com.ccrccne.starfall.gametest;

import java.nio.file.Path;
import java.util.Locale;

import com.ccrccne.starfall.StarShower;
import com.ccrccne.starfall.client.SkyStreaks;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

// "Rodaje" del README: abre el juego, monta una escena de noche con lluvia de estrellas
// y guarda capturas reales (fotogramas para el GIF y fotos fijas).
public class StarfallFilmTest implements FabricClientGameTest {
	private static final Path OUT = Path.of("starfall-capturas");
	private static final String[] LAMPS = {"light_blue", "pink", "yellow", "white", "purple", "lime", "magenta", "cyan"};

	@Override
	public void runTest(ClientGameTestContext ctx) {
		ctx.getInput().resizeWindow(1280, 720);

		try (TestSingleplayerContext sp = ctx.worldBuilder()
				.setUseConsistentSettings(true)
				.adjustSettings(s -> {
					s.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
					s.setDifficulty(Difficulty.PEACEFUL);
					s.setAllowCommands(true);
					s.setSeed("starfall");
				})
				.create()) {
			sp.getConnection().waitForChunksRender();

			// Noche 0 (siempre tiene lluvia), medianoche, con el reloj parado y cielo despejado
			sp.getServer().runOnServer(server -> {
				Holder<WorldClock> clock = server.overworld().registryAccess().getOrThrow(WorldClocks.OVERWORLD);
				server.clockManager().setTotalTicks(clock, 18000);
				server.clockManager().setPaused(clock, true);
			});
			sp.getServer().runCommand("weather clear");
			sp.getServer().runCommand("gamerule send_command_feedback false");

			// La cámara mira de lado respecto a la dirección de la lluvia, para que las estrellas crucen la imagen
			double direction = StarShower.direction(0);
			float yaw = (float) Math.toDegrees(direction);

			// Escena (mundo plano, cielo abierto): un claro con lámparas de colores y una estrella caída delante
			BlockPos origin = sp.getServer().computeOnServer(server -> {
				ServerLevel level = server.overworld();
				BlockPos p = sp.getConnection().getServerPlayer().blockPosition();
				int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p.getX(), p.getZ());
				return new BlockPos(p.getX(), y, p.getZ());
			});
			int x = origin.getX(), y = origin.getY(), z = origin.getZ();
			Vec3 forward = new Vec3(-Math.sin(direction), 0, Math.cos(direction));
			Vec3 right = new Vec3(-forward.z, 0, forward.x);

			sp.getServer().runCommand(String.format(Locale.ROOT, "fill %d %d %d %d %d %d air", x - 14, y, z - 14, x + 14, y + 20, z + 14));
			sp.getServer().runCommand(String.format(Locale.ROOT, "fill %d %d %d %d %d %d grass_block", x - 14, y - 1, z - 14, x + 14, y - 1, z + 14));
			sp.getServer().runCommand(String.format(Locale.ROOT, "fill %d %d %d %d %d %d dirt", x - 14, y - 4, z - 14, x + 14, y - 2, z + 14));

			BlockPos star = at(origin, forward, right, 11, 0.5);
			sp.getServer().runCommand(String.format(Locale.ROOT, "setblock %d %d %d starfall:fallen_star", star.getX(), star.getY(), star.getZ()));
			for (int i = 0; i < LAMPS.length; i++) {
				double side = (i - (LAMPS.length - 1) / 2.0) * 2.0;
				double depth = 7 + Math.abs(side) * 0.35;
				BlockPos lamp = at(origin, forward, right, depth, side);
				sp.getServer().runCommand(String.format(Locale.ROOT, "setblock %d %d %d starfall:%s_paper_lamp", lamp.getX(), lamp.getY(), lamp.getZ(), LAMPS[i]));
			}

			sp.getServer().runCommand(String.format(Locale.ROOT, "tp @p %.1f %d %.1f %.1f -18", x + 0.5, y, z + 0.5, yaw));
			ctx.waitTicks(40);
			sp.getConnection().waitForChunksRender();
			BlockPos lampCheck = at(origin, forward, right, 7, -1.0);
			int light = sp.getServer().computeOnServer(server -> server.overworld().getBrightness(LightLayer.BLOCK, lampCheck.below().relative(Direction.NORTH).above()));
			System.out.println("[starfall-test] luz de bloque junto a la lampara: " + light + " en " + lampCheck);
			ctx.getInput().pressKey(options -> options.keyToggleGui); // esconde la interfaz
			ctx.waitTicks(5);

			// Fotogramas del GIF: 4 segundos, lanzando estrellas que cruzan por delante de la cámara
			RandomSource random = RandomSource.create(7);
			int[] launches = {0, 14, 27, 38, 52, 63};
			for (int frame = 0; frame < 80; frame++) {
				for (int launch : launches) {
					if (frame == launch) {
						ctx.runOnClient(mc -> {
							Vec3 eye = mc.gameRenderer.mainCamera().position();
							double spread = (random.nextDouble() - 0.5) * 36;
							double distance = 45 + random.nextDouble() * 15;
							Vec3 point = eye.add(forward.scale(distance)).add(right.scale(spread)).add(0, 22 + random.nextDouble() * 14, 0);
							SkyStreaks.spawnThrough(point, direction, random);
						});
					}
				}
				ctx.takeScreenshot(TestScreenshotOptions.of(String.format(Locale.ROOT, "lluvia_%03d", frame)).disableCounterPrefix().withDestinationDir(OUT.resolve("gif")));
				ctx.waitTick();
			}

			// Foto: la lluvia con más estrellas a la vez
			for (int i = 0; i < 5; i++) {
				ctx.runOnClient(mc -> {
					Vec3 eye = mc.gameRenderer.mainCamera().position();
					Vec3 point = eye.add(forward.scale(50)).add(right.scale((random.nextDouble() - 0.5) * 50)).add(0, 20 + random.nextDouble() * 16, 0);
					SkyStreaks.spawnThrough(point, direction, random);
				});
				ctx.waitTicks(3);
			}
			ctx.waitTicks(8);
			ctx.takeScreenshot(TestScreenshotOptions.of("lluvia").disableCounterPrefix().withDestinationDir(OUT));

			// Foto: las lámparas de cerca
			sp.getServer().runCommand(String.format(Locale.ROOT, "tp @p %.1f %d %.1f %.1f 12", x + 0.5 + forward.x * 2, y, z + 0.5 + forward.z * 2, yaw));
			ctx.waitTicks(20);
			ctx.takeScreenshot(TestScreenshotOptions.of("lamparas").disableCounterPrefix().withDestinationDir(OUT));

			// Foto: la estrella caída de cerca
			Vec3 near = Vec3.atBottomCenterOf(star).subtract(forward.scale(2.2)).add(right.scale(0.8));
			sp.getServer().runCommand(String.format(Locale.ROOT, "tp @p %.2f %d %.2f %.1f 35", near.x, y, near.z, yaw - 20));
			ctx.waitTicks(20);
			ctx.takeScreenshot(TestScreenshotOptions.of("estrella_caida").disableCounterPrefix().withDestinationDir(OUT));

			// Foto: la varita estelar en la mano, lanzando su rayo
			ctx.getInput().pressKey(options -> options.keyToggleGui); // vuelve la interfaz para ver la mano
			sp.getServer().runCommand("clear @p");
			sp.getServer().runCommand("item replace entity @p hotbar.0 with starfall:star_wand");
			sp.getServer().runCommand(String.format(Locale.ROOT, "tp @p %.1f %d %.1f %.1f -8", x + 0.5, y, z + 0.5, yaw + 25));
			ctx.waitTicks(50);
			ctx.getInput().pressKey(options -> options.keyUse);
			ctx.waitTicks(3);
			ctx.takeScreenshot(TestScreenshotOptions.of("varita").disableCounterPrefix().withDestinationDir(OUT));
		}
	}

	private static BlockPos at(BlockPos origin, Vec3 forward, Vec3 right, double depth, double side) {
		Vec3 p = Vec3.atBottomCenterOf(origin).add(forward.scale(depth)).add(right.scale(side));
		return BlockPos.containing(p);
	}
}
