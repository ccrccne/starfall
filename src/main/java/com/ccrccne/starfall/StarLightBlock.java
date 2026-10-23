package com.ccrccne.starfall;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Lucecita flotante que deja la varita estelar: ilumina un rato y luego se apaga sola.
// Es invisible como el bloque de luz de Minecraft; solo se ven sus destellos.
public class StarLightBlock extends Block {
	public static final MapCodec<StarLightBlock> CODEC = simpleCodec(StarLightBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}
	// 60 segundos
	private static final int DURATION = 20 * 60;

	public StarLightBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
		level.scheduleTick(pos, this, DURATION);
	}

	@Override
	protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
		level.removeBlock(pos, false);
	}

	@Override
	protected RenderShape getRenderShape(final BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
		if (random.nextInt(3) == 0) {
			double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
			double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
			double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
			level.addParticle(StarfallParticles.STAR_STREAK, x, y, z, 0.0, 0.01, 0.0);
		}
	}
}
