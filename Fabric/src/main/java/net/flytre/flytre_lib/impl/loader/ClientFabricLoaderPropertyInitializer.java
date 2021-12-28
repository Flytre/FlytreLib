package net.flytre.flytre_lib.impl.loader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.flytre.flytre_lib.api.base.registry.EntityRendererRegistry;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.nio.channels.Pipe;

@Environment(EnvType.CLIENT)
public class ClientFabricLoaderPropertyInitializer {


    public static void init() {
        LoaderProperties.setBlockEntityRendererRegisterer(ClientFabricLoaderPropertyInitializer::register);
    }

    public static <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
        BlockEntityRendererRegistry.register(blockEntityType, blockEntityRendererFactory);
    }

}
