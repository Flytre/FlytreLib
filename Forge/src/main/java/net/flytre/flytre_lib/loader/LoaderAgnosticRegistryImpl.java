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
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

final class LoaderAgnosticRegistryImpl implements LoaderAgnosticRegistry.Delegate {


    private static final Map<String, DeferredRegister<Block>> BLOCK_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<Item>> ITEM_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<EntityType<?>>> ENTITY_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<ScreenHandlerType<?>>> SCREEN_HANDLER_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<BlockEntityType<?>>> BLOCK_ENTITY_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRIES = new HashMap<>();
    private static final Set<String> REGISTERED_MODS = new HashSet<>();

    private LoaderAgnosticRegistryImpl() {

    }

    public static void init() {
        LoaderAgnosticRegistry.setDelegate(new LoaderAgnosticRegistryImpl());
    }

    private static List<Map<String, ? extends DeferredRegister<?>>> getRegistries() {
        return List.of(
                BLOCK_REGISTRIES,
                ITEM_REGISTRIES,
                ENTITY_REGISTRIES,
                SCREEN_HANDLER_REGISTRIES,
                BLOCK_ENTITY_REGISTRIES,
                RECIPE_SERIALIZER_REGISTRIES
        );
    }

    /**
     * For mods to manually register themselves after running their init function
     */
    public static void register(String mod) {
        if (REGISTERED_MODS.contains(mod))
            return;
        REGISTERED_MODS.add(mod);
        getRegistries().forEach(map ->
                Optional.ofNullable(map.get(mod)).ifPresent(
                        reg -> reg.register(FMLJavaModLoadingContext.get().getModEventBus())
                )
        );
    }

    @Override
    public <T extends Block> T register(T block, String mod, String id) {
        BLOCK_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.BLOCKS, mod));
        BLOCK_REGISTRIES.get(mod).register(id, () -> block);
        return block;
    }

    @Override
    public <T extends Item> T register(T item, String mod, String id) {
        ITEM_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ITEMS, mod));
        ITEM_REGISTRIES.get(mod).register(id, () -> item);
        return item;
    }

    @Override
    public <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id) {
        ENTITY_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ENTITIES, mod));
        ENTITY_REGISTRIES.get(mod).register(id, () -> entity);
        return entity;
    }

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        SCREEN_HANDLER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.CONTAINERS, mod));
        ScreenHandlerType<T> type = IForgeMenuType.create((syncId, playerInv, packet) -> factory.create(syncId, playerInv));
        SCREEN_HANDLER_REGISTRIES.get(mod).register(id, () -> type);
        return type;
    }

    @Override
    public <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        SCREEN_HANDLER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.CONTAINERS, mod));
        ScreenHandlerType<T> type = IForgeMenuType.create(factory::create);
        SCREEN_HANDLER_REGISTRIES.get(mod).register(id, () -> type);
        return type;
    }

    @Override
    public <K extends BlockEntity> BlockEntityType<K> register(BlockEntityType<K> type, String mod, String id) {
        BLOCK_ENTITY_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, mod));
        BLOCK_ENTITY_REGISTRIES.get(mod).register(id, () -> type);
        return type;
    }

    @Override
    public <T extends RecipeSerializer<?>> T register(T recipe, String mod, String id) {
        RECIPE_SERIALIZER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, mod));
        RECIPE_SERIALIZER_REGISTRIES.get(mod).register(id, () -> recipe);
        return recipe;
    }
}
