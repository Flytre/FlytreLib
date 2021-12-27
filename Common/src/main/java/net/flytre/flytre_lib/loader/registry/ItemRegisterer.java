package net.flytre.flytre_lib.loader.registry;


import net.minecraft.item.Item;

@FunctionalInterface
public interface ItemRegisterer {

    <T extends Item> T register(T item, String mod, String id);
}
