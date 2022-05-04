package net.flytre.flytre_lib.loader;

import com.google.common.collect.ImmutableList;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class LoaderAgnosticClientRegistryImpl implements LoaderAgnosticClientRegistry.Delegate {

    private static final List<BlockEntityRendererEntry<?>> BLOCK_ENTITY_RENDERERS = new ArrayList<>();
    private static final List<EntityRendererEntry<?>> ENTITY_RENDERERS = new ArrayList<>();
    private static final List<ScreenRegistryEntry<?,?>>  SCREEN_REGISTRY_ENTRIES = new ArrayList<>();

    private LoaderAgnosticClientRegistryImpl() {

    }

    public static List<BlockEntityRendererEntry<?>> getBlockEntityRenderers() {
        return ImmutableList.copyOf(BLOCK_ENTITY_RENDERERS);
    }

    public static List<EntityRendererEntry<?>> getEntityRenderers() {
        return ImmutableList.copyOf(ENTITY_RENDERERS);
    }

    public static List<ScreenRegistryEntry<?, ?>> getScreenRegistryEntries() {
        return SCREEN_REGISTRY_ENTRIES;
    }

    public static void init() {
        LoaderAgnosticClientRegistry.setDelegate(new LoaderAgnosticClientRegistryImpl());
    }


    @Override
    public <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(Supplier<ScreenHandlerType<? extends H>> type, ScreenFactory<H, S> screenFactory) {
        SCREEN_REGISTRY_ENTRIES.add(new ScreenRegistryEntry<>(type, screenFactory));
    }

    @Override
    public <E extends BlockEntity> void register(Supplier<BlockEntityType<E>> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
        BLOCK_ENTITY_RENDERERS.add(new BlockEntityRendererEntry<>(blockEntityType, blockEntityRendererFactory));
    }

    @Override
    public <T extends Entity> void register(Supplier<EntityType<? extends T>> type, EntityRendererFactory<T> factory) {
        ENTITY_RENDERERS.add(new EntityRendererEntry<>(type, factory));

    }


    record BlockEntityRendererEntry<E extends BlockEntity>(Supplier<BlockEntityType<E>> blockEntityType,
                                                           BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
    }

    record EntityRendererEntry<T extends Entity>(Supplier<EntityType<? extends T>> type,
                                                 EntityRendererFactory<T> factory) {

    }

    record ScreenRegistryEntry<H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>>(Supplier<ScreenHandlerType<? extends H>> type, ScreenFactory<H, S> screenFactory) {

    }
}
