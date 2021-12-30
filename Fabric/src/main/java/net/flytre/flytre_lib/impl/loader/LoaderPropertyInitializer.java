package net.flytre.flytre_lib.impl.loader;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.flytre.flytre_lib.api.base.registry.EntityAttributeRegistry;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LoaderPropertyInitializer {


    /**
     * Called before anything else happens in the game via mixin.
     */
    public static void init() {
        LoaderProperties.setDevEnvironment(FabricLoader.getInstance().isDevelopmentEnvironment());
        LoaderProperties.setModIdToName((id) -> FabricLoader.getInstance().getModContainer(id).map(ModContainer::getMetadata).map(ModMetadata::getName).orElse(id));
        LoaderProperties.setModConfigDirectory(FabricLoader.getInstance().getConfigDir());
        LoaderProperties.setBlockRegisterer(LoaderPropertyInitializer::register);
        LoaderProperties.setItemRegisterer(LoaderPropertyInitializer::register);
        LoaderProperties.setEntityRegister(LoaderPropertyInitializer::register);
        LoaderProperties.setEntityAttributeRegisterer(EntityAttributeRegistry::register);
    }

    public static <T extends Block> T register(T block, String mod, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(mod, id), block);
    }

    public static <T extends Item> T register(T item, String mod, String id) {
        return Registry.register(Registry.ITEM, new Identifier(mod, id), item);
    }

    public static <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(mod, id), entity);
    }

}
