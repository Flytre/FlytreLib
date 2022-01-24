package net.flytre.flytre_lib.impl.loader;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.flytre.flytre_lib.loader.registry.ScreenRegisterer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.client.event.EntityRenderersEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientLoaderPropertyInitializer {


    public static List<BlockEntityRendererEntry<?>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
    public static List<EntityRendererEntry<?>> ENTITY_RENDERERS = new ArrayList<>();


    public static void init() {
        LoaderProperties.setEntityRendererRegisterer(ClientLoaderPropertyInitializer::register);
        LoaderProperties.setBlockEntityRendererRegisterer(ClientLoaderPropertyInitializer::register);
        LoaderProperties.setScreenRegisterer(ClientLoaderPropertyInitializer::register);

    }

    public static <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
        BLOCK_ENTITY_RENDERERS.add(new BlockEntityRendererEntry<>(blockEntityType, blockEntityRendererFactory));
    }

    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        ENTITY_RENDERERS.add(new EntityRendererEntry<>(type, factory));
    }


    public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, ScreenRegisterer.Factory<H, S> screenFactory) {
        HandledScreens.register(type, screenFactory::create);
    }

    public record BlockEntityRendererEntry<E extends BlockEntity>(BlockEntityType<E> blockEntityType,
                                                           BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
    }

    public record EntityRendererEntry<T extends Entity>(EntityType<? extends T> type, EntityRendererFactory<T> factory) {

    }
}
