package com.ccrccne.starfall;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class StarfallItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Starfall.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Starfall.MOD_ID);

	public static final RegistryObject<Item> STAR_FRAGMENT = ITEMS.register("star_fragment", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> STAR_WAND = ITEMS.register("star_wand", () -> new StarWandItem(new Item.Properties().durability(64).rarity(Rarity.UNCOMMON)));

	// Pestaña propia en el modo creativo con todo lo del mod
	public static final RegistryObject<CreativeModeTab> TAB = TABS.register("starfall", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.starfall"))
			.icon(() -> new ItemStack(STAR_FRAGMENT.get()))
			.displayItems((parameters, output) -> {
				output.accept(STAR_FRAGMENT.get());
				output.accept(STAR_WAND.get());
				output.accept(StarfallBlocks.FALLEN_STAR.get());
				StarfallBlocks.PAPER_LAMPS.values().forEach(lamp -> output.accept(lamp.get()));
				output.accept(StarfallBlocks.SKY_BLOCK.get());
			})
			.build());

	private StarfallItems() {
	}
}
