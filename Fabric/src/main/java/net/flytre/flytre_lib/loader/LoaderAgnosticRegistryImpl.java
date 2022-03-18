package net.flytre.flytre_lib.loader;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

final class LoaderAgnosticRegistryImpl implements LoaderAgnosticRegistry.Delegate {

    private LoaderAgnosticRegistryImpl() {

    }

    public static void init() {
        LoaderAgnosticRegistry.setDelegate(new LoaderAgnosticRegistryImpl());
    }

    @Override
    public <T extends Block> T register(T block, String mod, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(mod, id), block);
    }

    @Override
    public <T extends Item> T register(T item, String mod, String id) {
        return Registry.register(Registry.ITEM, new Identifier(mod, id), item);
    }

    @Override
    public <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(mod, id), entity);
    }

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            return FabricLoaderAgnosticRegistry.register(factory, mod, id);
        } else
            throw new FabricApiNotInstalledError();
    }

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            return FabricLoaderAgnosticRegistry.register(factory, mod, id);
        } else
            throw new FabricApiNotInstalledError();
    }

    @Override
    public <K extends BlockEntity> BlockEntityType<K> register(BlockEntityType<K> type, String mod, String id) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(mod, id), type);

    }

    @Override
    public <T extends RecipeSerializer<?>> T register(T recipe, String mod, String id) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(mod, id), recipe);

    }
}
