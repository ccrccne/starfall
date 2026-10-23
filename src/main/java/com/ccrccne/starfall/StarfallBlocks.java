package com.ccrccne.starfall;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class StarfallBlocks {
	// Lámparas de papel, una por cada color de tinte; brillan como la piedra luminosa
	public static final Map<DyeColor, Block> PAPER_LAMPS = new EnumMap<>(DyeColor.class);

	static {
		for (DyeColor color : DyeColor.values()) {
			PAPER_LAMPS.put(color, register(color.getName() + "_paper_lamp", PaperLampBlock::new, BlockBehaviour.Properties.of()
					.mapColor(color.getMapColor())
					.strength(0.3F)
					.sound(SoundType.WOOL)
					.lightLevel(state -> 15)
					.noOcclusion()
					.pushReaction(PushReaction.POPPED)));
		}
	}

	// Bloque decorativo de cielo con estrellitas; da un brillo suave
	public static final Block SKY_BLOCK = register("sky_block", Block::new, BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_BLUE)
			.strength(0.8F)
			.sound(SoundType.AMETHYST)
			.lightLevel(state -> 4));

	// Lo que deja una estrella fugaz al caer; al romperla suelta fragmentos
	public static final Block FALLEN_STAR = register("fallen_star", FallenStarBlock::new, BlockBehaviour.Properties.of()
			.mapColor(MapColor.GOLD)
			.strength(0.5F)
			.sound(SoundType.AMETHYST_CLUSTER)
			.lightLevel(state -> 12)
			.noCollision()
			.noOcclusion()
			.pushReaction(PushReaction.POPPED));

	private StarfallBlocks() {
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Starfall.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Starfall.id(name));
		Item.Properties itemProperties = new Item.Properties().setId(itemKey).useBlockDescriptionPrefix();
		Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, itemProperties));

		return block;
	}

	public static void init() {
	}
}
