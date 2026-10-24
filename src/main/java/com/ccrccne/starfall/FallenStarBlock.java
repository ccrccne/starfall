package com.ccrccne.starfall;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// Estrellita que queda en el suelo tras caer; brilla y suelta destellos
public class FallenStarBlock extends Block {
	private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

	public FallenStarBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
		double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
		double y = pos.getY() + 0.3 + random.nextDouble() * 0.6;
		double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
		level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.03, 0.0);
	}
}
