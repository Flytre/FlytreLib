package net.flytre.flytre_lib.loader;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;

import java.util.function.Supplier;

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

    public static <T extends Block> Supplier<T> registerBlock(Supplier<T> block, String mod, String id) {
        return DELEGATE.registerBlock(block, mod, id);
    }

    public static <T extends Item> Supplier<T> registerItem(Supplier<T> item, String mod, String id) {
        return DELEGATE.registerItem(item, mod, id);
    }

    public static <E extends Entity, T extends EntityType<E>> Supplier<T> registerEntity(Supplier<T> entity, String mod, String id) {
        return DELEGATE.registerEntity(entity, mod, id);
    }

    public static <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerSimpleScreen(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        return DELEGATE.registerSimpleScreen(factory, mod, id);
    }

    public static <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerExtendedScreen(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        return DELEGATE.registerExtendedScreen(factory, mod, id);
    }

    public static <K extends BlockEntity> Supplier<BlockEntityType<K>> registerBlockEntityType(Supplier<BlockEntityType<K>> type, String mod, String id) {
        return DELEGATE.registerBlockEntityType(type, mod, id);
    }

    public static <T extends RecipeType<?>> Supplier<T> registerRecipeType(Supplier<T> recipeType, String mod, String id) {
        return DELEGATE.registerRecipeType(recipeType, mod, id);
    }

    //TODO: rename recipe serializer
    public static <T extends RecipeSerializer<?>> Supplier<T> registerRecipe(Supplier<T> recipe, String mod, String id) {
        return DELEGATE.registerRecipe(recipe, mod, id);
    }

    public static <T extends ParticleType<?>> Supplier<T> registerParticleType(Supplier<T> particleType, String mod, String id) {
        return DELEGATE.registerParticleType(particleType, mod, id);
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(Supplier<T> soundEvent, String mod, String id) {
        return DELEGATE.registerSoundEvent(soundEvent, mod, id);
    }


    interface Delegate {

        <T extends Block> Supplier<T> registerBlock(Supplier<T> block, String mod, String id);

        <T extends Item> Supplier<T> registerItem(Supplier<T> item, String mod, String id);

        <E extends Entity, T extends EntityType<E>> Supplier<T> registerEntity(Supplier<T> entity, String mod, String id);

        <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerSimpleScreen(SimpleScreenHandlerFactory<T> factory, String mod, String id);

        <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerExtendedScreen(ExtendedScreenHandlerFactory<T> factory, String mod, String id);

        <K extends BlockEntity> Supplier<BlockEntityType<K>> registerBlockEntityType(Supplier<BlockEntityType<K>> type, String mod, String id);

        <T extends RecipeSerializer<?>> Supplier<T> registerRecipe(Supplier<T> recipe, String mod, String id);

        <T extends RecipeType<?>> Supplier<T> registerRecipeType(Supplier<T> recipeType, String mod, String id);

        <T extends ParticleType<?>> Supplier<T> registerParticleType(Supplier<T> particleType, String mod, String id);

        <T extends SoundEvent> Supplier<T> registerSoundEvent(Supplier<T> soundEvent, String mod, String id);
    }
}