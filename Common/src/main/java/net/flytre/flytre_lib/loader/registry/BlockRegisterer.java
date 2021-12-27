package net.flytre.flytre_lib.loader.registry;


import net.minecraft.block.Block;

@FunctionalInterface
public interface BlockRegisterer {

    <T extends Block> T register(T block, String mod, String id);
}
