package net.flytre.flytre_lib.loader;

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
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Supplier;

final class LoaderAgnosticRegistryImpl implements LoaderAgnosticRegistry.Delegate {


    private static final Map<String, DeferredRegister<Block>> BLOCK_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<Item>> ITEM_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<EntityType<?>>> ENTITY_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<ScreenHandlerType<?>>> SCREEN_HANDLER_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<BlockEntityType<?>>> BLOCK_ENTITY_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<RecipeType<?>>> RECIPE_TYPE_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<ParticleType<?>>> PARTICLE_TYPE_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<SoundEvent>> SOUND_EVENT_REGISTRIES = new HashMap<>();
    private static final Map<String, DeferredRegister<Enchantment>> ENCHANTMENT_REGISTRIES = new HashMap<>();
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
                RECIPE_TYPE_REGISTRIES,
                RECIPE_SERIALIZER_REGISTRIES,
                PARTICLE_TYPE_REGISTRIES,
                SOUND_EVENT_REGISTRIES,
                ENCHANTMENT_REGISTRIES
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
    public <T extends Block> Supplier<T> registerBlock(Supplier<T> block, String mod, String id) {
        block = CachedSupplier.of(block);
        BLOCK_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.BLOCKS, mod));
        BLOCK_REGISTRIES.get(mod).register(id, block);
        return block;
    }


    @Override
    public <T extends Item> Supplier<T> registerItem(Supplier<T> item, String mod, String id) {
        item = CachedSupplier.of(item);
        ITEM_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ITEMS, mod));
        ITEM_REGISTRIES.get(mod).register(id, item);
        return item;
    }

    @Override
    public <E extends Entity, T extends EntityType<E>> Supplier<T> registerEntity(Supplier<T> entity, String mod, String id) {
        entity = CachedSupplier.of(entity);
        ENTITY_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ENTITIES, mod));
        ENTITY_REGISTRIES.get(mod).register(id, entity);
        return entity;
    }

    @Override
    public <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerSimpleScreen(SimpleScreenHandlerFactory<T> factory, String mod, String id) {
        SCREEN_HANDLER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.CONTAINERS, mod));
        Supplier<ScreenHandlerType<T>> type = CachedSupplier.of(() -> IForgeMenuType.create((syncId, playerInv, packet) -> factory.create(syncId, playerInv)));
        SCREEN_HANDLER_REGISTRIES.get(mod).register(id, type);
        return type;
    }

    @Override
    public <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> registerExtendedScreen(ExtendedScreenHandlerFactory<T> factory, String mod, String id) {
        SCREEN_HANDLER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.CONTAINERS, mod));
        Supplier<ScreenHandlerType<T>> type = CachedSupplier.of(() -> IForgeMenuType.create(factory::create));
        SCREEN_HANDLER_REGISTRIES.get(mod).register(id, type);
        return type;
    }

    @Override
    public <K extends BlockEntity> Supplier<BlockEntityType<K>> registerBlockEntityType(Supplier<BlockEntityType<K>> type, String mod, String id) {
        type = CachedSupplier.of(type);
        BLOCK_ENTITY_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, mod));
        BLOCK_ENTITY_REGISTRIES.get(mod).register(id, type);
        return type;
    }

    @Override
    public <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(Supplier<T> recipe, String mod, String id) {
        recipe = CachedSupplier.of(recipe);
        RECIPE_SERIALIZER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, mod));
        RECIPE_SERIALIZER_REGISTRIES.get(mod).register(id, recipe);
        return recipe;
    }

    @Override
    public <T extends RecipeType<?>> Supplier<T> registerRecipeType(Supplier<T> recipeType, String mod, String id) {
        recipeType = CachedSupplier.of(recipeType);
        RECIPE_TYPE_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(Registry.RECIPE_TYPE_KEY, mod));
        RECIPE_TYPE_REGISTRIES.get(mod).register(id, recipeType);
        return recipeType;
    }

    @Override
    public <T extends ParticleType<?>> Supplier<T> registerParticleType(Supplier<T> particleType, String mod, String id) {
        particleType = CachedSupplier.of(particleType);
        PARTICLE_TYPE_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(Registry.PARTICLE_TYPE_KEY, mod));
        PARTICLE_TYPE_REGISTRIES.get(mod).register(id, particleType);
        return particleType;
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSoundEvent(Supplier<T> soundEvent, String mod, String id) {
        soundEvent = CachedSupplier.of(soundEvent);
        SOUND_EVENT_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, mod));
        SOUND_EVENT_REGISTRIES.get(mod).register(id, soundEvent);
        return soundEvent;
    }

    @Override
    public <T extends Enchantment> Supplier<T> registerEnchantment(Supplier<T> enchantment, String mod, String id) {
        enchantment = CachedSupplier.of(enchantment);
        ENCHANTMENT_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, mod));
        ENCHANTMENT_REGISTRIES.get(mod).register(id, enchantment);
        return enchantment;
    }
}
