package net.flytre.flytre_lib.impl.loader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.flytre_lib.api.base.registry.EntityRendererRegistry;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
public class ClientLoaderPropertyInitializer {


    /**
     * Called before anything else happens in the game via mixin.
     */
    public static void init() {
        LoaderProperties.setEntityRendererRegisterer(ClientLoaderPropertyInitializer::register);
        if (FabricLoader.getInstance().isModLoaded("fabric"))
            ClientFabricLoaderPropertyInitializer.init();
    }

    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        EntityRendererRegistry.register(type, factory);
    }

}
