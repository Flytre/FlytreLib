package net.flytre.flytre_lib.loader;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

final class LoaderAgnosticRegistryImpl implements LoaderAgnosticRegistry.Delegate {

    private LoaderAgnosticRegistryImpl() {

    }

    public static void init() {
        LoaderAgnosticRegistry.setDelegate(new LoaderAgnosticRegistryImpl());
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(Supplier<T> block, String mod, String id) {
        block = CachedSupplier.of(block);
        Registry.register(Registry.BLOCK, new Identifier(mod, id), block.get());
        return block;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(Supplier<T> item, String mod, String id) {
        item = CachedSupplier.of(item);
        Registry.register(Registry.ITEM, new Identifier(mod, id), item.get());
        return item;
    }

    @Override
    public <E extends Entity, T extends EntityType<E>> Supplier<T> registerEntity(Supplier<T> entity, String mod, String id) {
        entity = CachedSupplier.of(entity);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(mod, id), entity.get());
        return entity;
    }

    @Override
    public <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerSimpleScreen(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            return FabricLoaderAgnosticRegistry.register(factory, mod, id);
        } else
            throw new FabricApiNotInstalledError();
    }

    @Override
    public <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerExtendedScreen(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            return FabricLoaderAgnosticRegistry.register(factory, mod, id);
        } else
            throw new FabricApiNotInstalledError();
    }

    @Override
    public <K extends BlockEntity> Supplier<BlockEntityType<K>> registerBlockEntityType(Supplier<BlockEntityType<K>> type, String mod, String id) {
        type = CachedSupplier.of(type);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(mod, id), type.get());
        return type;
    }

    @Override
    public <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(Supplier<T> recipe, String mod, String id) {
        recipe = CachedSupplier.of(recipe);
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(mod, id), recipe.get());
        return recipe;
    }

    @Override
    public <T extends RecipeType<?>> Supplier<T> registerRecipeType(Supplier<T> recipeType, String mod, String id) {
        recipeType = CachedSupplier.of(recipeType);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(mod, id), recipeType.get());
        return recipeType;
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticleType(Supplier<T> particleType, String mod, String id) {
        particleType = CachedSupplier.of(particleType);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(mod, id), particleType.get());
        return particleType;
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSoundEvent(Supplier<T> soundEvent, String mod, String id) {
        soundEvent = CachedSupplier.of(soundEvent);
        Registry.register(Registry.SOUND_EVENT, new Identifier(mod, id), soundEvent.get());
        return soundEvent;
    }

    @Override
    public <T extends Enchantment> Supplier<T> registerEnchantment(Supplier<T> enchantment, String mod, String id) {
        enchantment = CachedSupplier.of(enchantment);
        Registry.register(Registry.ENCHANTMENT, new Identifier(mod, id), enchantment.get());
        return enchantment;
    }
}
