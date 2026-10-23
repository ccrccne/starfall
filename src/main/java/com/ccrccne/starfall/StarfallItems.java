package com.ccrccne.starfall;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class StarfallItems {
	public static final Item STAR_FRAGMENT = register("star_fragment", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item STAR_WAND = register("star_wand", StarWandItem::new, new Item.Properties().durability(64).repairable(STAR_FRAGMENT).rarity(Rarity.UNCOMMON));

	// Pestaña propia en el modo creativo con todo lo del mod
	public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Starfall.id("starfall"));

	private StarfallItems() {
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Starfall.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}

	public static void init() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, FabricCreativeModeTab.builder()
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
