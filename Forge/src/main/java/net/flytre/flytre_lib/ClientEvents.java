package net.flytre.flytre_lib;

import net.flytre.flytre_lib.impl.loader.ClientLoaderPropertyInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FlytreLibConstants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers registerRenderersEvent) {
        for (var entry : ClientLoaderPropertyInitializer.ENTITY_RENDERERS) {
            unpackAndRegister(registerRenderersEvent, entry);
        }

        for (var entry : ClientLoaderPropertyInitializer.BLOCK_ENTITY_RENDERERS) {
            unpackAndRegister(registerRenderersEvent, entry);
        }
    }


    public static <T extends Entity> void unpackAndRegister(EntityRenderersEvent.RegisterRenderers registerRenderersEvent, ClientLoaderPropertyInitializer.EntityRendererEntry<T> entry) {
        EntityType<? extends T> type = entry.type();
        EntityRendererFactory<T> factory = entry.factory();
        registerRenderersEvent.registerEntityRenderer(type, factory);
    }

    public static <E extends BlockEntity> void unpackAndRegister(EntityRenderersEvent.RegisterRenderers registerRenderersEvent, ClientLoaderPropertyInitializer.BlockEntityRendererEntry<E> entry) {
        BlockEntityType<E> type = entry.blockEntityType();
        BlockEntityRendererFactory<? super E> factory = entry.blockEntityRendererFactory();
        registerRenderersEvent.registerBlockEntityRenderer(type, factory);
    }
}
