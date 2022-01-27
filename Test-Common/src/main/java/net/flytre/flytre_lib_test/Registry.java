package net.flytre.flytre_lib_test;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class Registry {


    public static Block BASIC_BLOCK;

    public static void init() {
        BASIC_BLOCK = register(new Block(AbstractBlock.Settings.of(Material.STONE)), ItemGroup.BUILDING_BLOCKS, "basic_block");
        System.out.println("RAN!");
    }


    public static <T extends Block> T register(T block, ItemGroup tab, String id) {
        var tmp = LoaderProperties.register(block, Constants.MOD_ID, id);
        LoaderProperties.register(new BlockItem(block, new Item.Settings().group(tab)), Constants.MOD_ID, id);
        return tmp;
    }

    private static <T extends Item> T register(T item, String id) {
        return LoaderProperties.register(item, Constants.MOD_ID, id);
    }
}
