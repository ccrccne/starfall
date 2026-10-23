package com.ccrccne.starfall;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class StarfallItems {
	public static final Item STAR_FRAGMENT = register("star_fragment", new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
	public static final Item STAR_WAND = register("star_wand", new StarWandItem(new Item.Properties().durability(64).rarity(Rarity.UNCOMMON)));

	private StarfallItems() {
	}

	private static Item register(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, Starfall.id(name), item);
	}

	// Pestaña propia en el modo creativo con todo lo del mod
	public static void init() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Starfall.id("starfall"), FabricItemGroup.builder()
				.title(Component.translatable("itemGroup.starfall"))
				.icon(() -> new ItemStack(STAR_FRAGMENT))
				.displayItems((parameters, output) -> {
					output.accept(STAR_FRAGMENT);
					output.accept(STAR_WAND);
					output.accept(StarfallBlocks.FALLEN_STAR);
					StarfallBlocks.PAPER_LAMPS.values().forEach(output::accept);
					output.accept(StarfallBlocks.SKY_BLOCK);
				})
				.build());
	}
}
