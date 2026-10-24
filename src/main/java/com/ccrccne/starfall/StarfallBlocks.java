package com.ccrccne.starfall;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StarfallBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Starfall.MOD_ID);

	// Lámparas de papel, una por cada color de tinte; brillan como la piedra luminosa
	public static final Map<DyeColor, DeferredHolder<Block, Block>> PAPER_LAMPS = new EnumMap<>(DyeColor.class);

	static {
		for (DyeColor color : DyeColor.values()) {
			PAPER_LAMPS.put(color, register(color.getName() + "_paper_lamp", PaperLampBlock::new, () -> BlockBehaviour.Properties.of()
					.mapColor(color.getMapColor())
					.strength(0.3F)
					.sound(SoundType.WOOL)
					.lightLevel(state -> 15)
					.noOcclusion()
					.pushReaction(PushReaction.DESTROY)));
		}
	}

	// Bloque decorativo de cielo con estrellitas; da un brillo suave
	public static final DeferredHolder<Block, Block> SKY_BLOCK = register("sky_block", Block::new, () -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_BLUE)
			.strength(0.8F)
			.sound(SoundType.AMETHYST)
			.lightLevel(state -> 4));

	// Lo que deja una estrella fugaz al caer; al romperla suelta fragmentos
	public static final DeferredHolder<Block, Block> FALLEN_STAR = register("fallen_star", FallenStarBlock::new, () -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.GOLD)
			.strength(0.5F)
			.sound(SoundType.AMETHYST_CLUSTER)
			.lightLevel(state -> 12)
			.noCollission()
			.noOcclusion()
			.pushReaction(PushReaction.DESTROY));

	// Lucecita invisible de la varita estelar; se apaga sola y no tiene ítem
	public static final DeferredHolder<Block, Block> STAR_LIGHT = BLOCKS.register("star_light", () -> new StarLightBlock(BlockBehaviour.Properties.of()
			.replaceable()
			.noCollission()
			.noOcclusion()
			.noLootTable()
			.instabreak()
			.lightLevel(state -> 15)));

	private StarfallBlocks() {
	}

	private static DeferredHolder<Block, Block> register(String name, Function<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> properties) {
		DeferredHolder<Block, Block> block = BLOCKS.register(name, () -> factory.apply(properties.get()));
		StarfallItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
		return block;
	}
}
