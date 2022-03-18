package net.flytre.flytre_lib.loader;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * Used to register server sided things
 */
public final class LoaderAgnosticRegistry {

    private static Delegate DELEGATE;

    private LoaderAgnosticRegistry() {
        throw new AssertionError();
    }

    public static void setDelegate(Delegate delegate) {
        LoaderAgnosticRegistry.DELEGATE = delegate;
    }

    public static <T extends Block> T register(T block, String mod, String id) {
        return DELEGATE.register(block, mod, id);
    }

    public static <T extends Item> T register(T item, String mod, String id) {
        return DELEGATE.register(item, mod, id);
    }

    public static <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id) {
        return DELEGATE.register(entity, mod, id);
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        return DELEGATE.register(factory, mod, id);
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        return DELEGATE.register(factory, mod, id);
    }

    public static <K extends BlockEntity> BlockEntityType<K> register(BlockEntityType<K> type, String mod, String id) {
        return DELEGATE.register(type, mod, id);
    }

    public static <T extends RecipeSerializer<?>> T register(T recipe, String mod, String id) {
        return DELEGATE.register(recipe, mod, id);
    }

    interface Delegate {

        <T extends Block> T register(T block, String mod, String id);

        <T extends Item> T register(T item, String mod, String id);

        <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id);

        <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleScreenHandlerFactory<T> factory, String mod, String id);

        <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedScreenHandlerFactory<T> factory, String mod, String id);

        <K extends BlockEntity> BlockEntityType<K> register(BlockEntityType<K> type, String mod, String id);

        <T extends RecipeSerializer<?>> T register(T recipe, String mod, String id);

    }
}
