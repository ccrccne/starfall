package com.ccrccne.starfall;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

// Varita estelar: lanza un rayo de destellos y deja una lucecita flotante donde apuntas
public class StarWandItem extends Item {
	private static final double RANGE = 24.0;
	private static final int COOLDOWN = 20;

	public StarWandItem(Item.Properties properties) {
		super(properties);
	}

	// Se repara en el yunque con fragmentos de estrella
	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
		return repair.is(StarfallItems.STAR_FRAGMENT.get());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResultHolder.success(stack);
		}

		Vec3 eye = player.getEyePosition();
		Vec3 end = eye.add(player.getViewVector(1.0F).scale(RANGE));
		BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

		// La luz va justo delante del bloque al que apuntas, o al final del rayo si apuntas al aire
		BlockPos target = hit.getType() == HitResult.Type.BLOCK
				? hit.getBlockPos().relative(hit.getDirection())
				: BlockPos.containing(end);
		Vec3 to = hit.getType() == HitResult.Type.BLOCK ? hit.getLocation() : end;

		// Rayo de destellos desde la varita
		Vec3 from = eye.add(player.getViewVector(1.0F).scale(0.8)).add(0, -0.2, 0);
		int steps = (int) (from.distanceTo(to) * 2);
		for (int i = 0; i <= steps; i++) {
			Vec3 p = from.lerp(to, (double) i / Math.max(steps, 1));
			for (ServerPlayer viewer : serverLevel.players()) {
				serverLevel.sendParticles(viewer, StarfallParticles.STAR_STREAK.get(), true, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
			}
		}
		serverLevel.sendParticles(ParticleTypes.END_ROD, to.x, to.y, to.z, 12, 0.2, 0.2, 0.2, 0.05);
		level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.8F, 1.6F);

		BlockState current = level.getBlockState(target);
		if (current.isAir() || current.is(StarfallBlocks.STAR_LIGHT.get())) {
			level.setBlock(target, StarfallBlocks.STAR_LIGHT.get().defaultBlockState(), Block.UPDATE_ALL);
			level.playSound(null, target, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.2F);
		}

		player.getCooldowns().addCooldown(this, COOLDOWN);
		stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
		return InteractionResultHolder.success(stack);
	}
}
