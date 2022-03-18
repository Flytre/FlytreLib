package net.flytre.flytre_lib.loader;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * Used to register client sided things
 */
public final class LoaderAgnosticClientRegistry {

    private static Delegate DELEGATE;

    private LoaderAgnosticClientRegistry() {
        throw new AssertionError();
    }

    public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, ScreenFactory<H, S> screenFactory) {
        DELEGATE.register(type, screenFactory);
    }

    public static <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
        DELEGATE.register(blockEntityType, blockEntityRendererFactory);
    }

    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        DELEGATE.register(type, factory);
    }

    static void setDelegate(Delegate delegate) {
        LoaderAgnosticClientRegistry.DELEGATE = delegate;
    }

    interface Delegate {
        <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, ScreenFactory<H, S> screenFactory);

        <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory);

        <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory);

    }
}
