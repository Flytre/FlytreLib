package net.flytre.flytre_lib_test;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

@FunctionalInterface
public interface IconMaker<T extends Block> {

    IconMaker<Block> STANDARD = (block) -> new BlockItem(block, new Item.Settings().group(Registry.TAB));

    BlockItem create(T block);
}
