package net.flytre.flytre_lib.api.loader;

import net.flytre.flytre_lib.impl.loader.LoaderPropertyInitializer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;

public class RegistryReceiver {


    public static DeferredRegister<Block> getBlockDeferredRegister(String mod) {
        return LoaderPropertyInitializer.BLOCK_REGISTRIES.get(mod);
    }

    public static DeferredRegister<Item> getItemDeferredRegister(String mod) {
        return LoaderPropertyInitializer.ITEM_REGISTRIES.get(mod);
    }

    public static DeferredRegister<EntityType<?>> getEntityDeferredRegister(String mod) {
        return LoaderPropertyInitializer.ENTITY_REGISTRIES.get(mod);
    }
}
