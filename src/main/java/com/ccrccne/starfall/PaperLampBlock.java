package com.ccrccne.starfall;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Farolito de papel: cuerpo redondeado con tapas de madera y un asa
public class PaperLampBlock extends Block {
	public static final MapCodec<PaperLampBlock> CODEC = simpleCodec(PaperLampBlock::new);

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}
	private static final VoxelShape SHAPE = Shapes.or(Block.box(3.0, 2.0, 3.0, 13.0, 10.0, 13.0), Block.box(4.0, 0.0, 4.0, 12.0, 12.0, 12.0));

	public PaperLampBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return SHAPE;
	}

	// De vez en cuando sube una chispita desde el farolito
	@Override
	public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
		if (random.nextInt(4) != 0) {
			return;
		}

		double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
		double y = pos.getY() + 0.8;
		double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
		level.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.02, 0.0);
	}
}
