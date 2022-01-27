package net.flytre.flytre_lib.impl.loader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.flytre.flytre_lib.loader.registry.ScreenHandlerRegisterer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;

public class LoaderPropertyInitializer {

    public static Map<String, DeferredRegister<Block>> BLOCK_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<Item>> ITEM_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<EntityType<?>>> ENTITY_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<ScreenHandlerType<?>>> SCREEN_HANDLER_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<BlockEntityType<?>>> BLOCK_ENTITY_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRIES = new HashMap<>();
    public static List<EntityAttributeEntries> ENTITY_ATTRIBUTES = new ArrayList<>();
    public static Set<String> REGISTERED_MODS = new HashSet<>();

    public static void init(String[] args) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        final ArgumentAcceptingOptionSpec<Path> gameDir = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING)).defaultsTo(Path.of("."));
        parser.allowsUnrecognizedOptions();
        final OptionSet optionSet = parser.parse(args);
        var configDir = Paths.get(optionSet.valueOf(gameDir).toString(), FMLPaths.CONFIGDIR.relative().toString());

        LoaderProperties.setModConfigDirectory(configDir);
        LoaderProperties.setDevEnvironment(!FMLEnvironment.production);
        LoaderProperties.setModIdToName(id -> ModList.get().getModContainerById(id)
                .map(modContainer -> modContainer.getModInfo().getDisplayName())
                .orElse(StringUtils.capitalize(id)));
        LoaderProperties.setBlockRegisterer(LoaderPropertyInitializer::register);
        LoaderProperties.setItemRegisterer(LoaderPropertyInitializer::register);
        LoaderProperties.setEntityRegister(LoaderPropertyInitializer::register);
        LoaderProperties.setEntityAttributeRegisterer((entityType, attributes) -> ENTITY_ATTRIBUTES.add(new EntityAttributeEntries(entityType, attributes)));
        LoaderProperties.setScreenHandlerRegisterer(new ScreenHandlerRegisterer() {
            @Override
            public <T extends ScreenHandler> ScreenHandlerType<T> register(SimpleFactory<T> factory, String mod, String id) {
                SCREEN_HANDLER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.CONTAINERS, mod));
                ScreenHandlerType<T> type = IForgeMenuType.create((syncId, playerInv, packet) -> factory.create(syncId, playerInv));
                SCREEN_HANDLER_REGISTRIES.get(mod).register(id, () -> type);
                return type;
            }

            @Override
            public <T extends ScreenHandler> ScreenHandlerType<T> register(ExtendedFactory<T> factory, String mod, String id) {
                SCREEN_HANDLER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.CONTAINERS, mod));
                ScreenHandlerType<T> type = IForgeMenuType.create(factory::create);
                SCREEN_HANDLER_REGISTRIES.get(mod).register(id, () -> type);
                return type;
            }
        });
        LoaderProperties.setBlockEntityRegisterer(LoaderPropertyInitializer::register);
        LoaderProperties.setRecipeSerializerRegisterer(LoaderPropertyInitializer::register);
    }

    public static <T extends Block> T register(T block, String mod, String id) {
        BLOCK_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.BLOCKS, mod));
        BLOCK_REGISTRIES.get(mod).register(id, () -> block);
        return block;
    }

    public static <T extends Item> T register(T item, String mod, String id) {
        ITEM_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ITEMS, mod));
        ITEM_REGISTRIES.get(mod).register(id, () -> item);
        return item;
    }

    public static <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id) {
        ENTITY_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.ENTITIES, mod));
        ENTITY_REGISTRIES.get(mod).register(id, () -> entity);
        return entity;
    }


    public static <K extends BlockEntity> BlockEntityType<K> register(BlockEntityType<K> type, String mod, String id) {
        BLOCK_ENTITY_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, mod));
        BLOCK_ENTITY_REGISTRIES.get(mod).register(id, () -> type);
        return type;
    }

    public static <T extends RecipeSerializer<?>> T register(T recipe, String mod, String id) {
        RECIPE_SERIALIZER_REGISTRIES.putIfAbsent(mod, DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, mod));
        RECIPE_SERIALIZER_REGISTRIES.get(mod).register(id, () -> recipe);
        return recipe;
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
     * For mods to manually register themselves after running their init funtion
     */
    public static void register(String mod) {
        if (REGISTERED_MODS.contains(mod))
            return;
        REGISTERED_MODS.add(mod);
        getRegistries().forEach(map ->
                Optional.ofNullable(map.get(mod)).ifPresent(
                        reg -> reg.register(FMLJavaModLoadingContext.get().getModEventBus()
                        )
                )
        );
    }

    public record EntityAttributeEntries(EntityType<? extends LivingEntity> entityType,
                                         Supplier<DefaultAttributeContainer.Builder> attributes) {
    }
}
