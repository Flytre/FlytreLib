package net.flytre.flytre_lib.impl.loader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import net.flytre.flytre_lib.loader.LoaderProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderPropertyInitializer {

    public static Map<String, DeferredRegister<Block>> BLOCK_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<Item>> ITEM_REGISTRIES = new HashMap<>();
    public static Map<String, DeferredRegister<EntityType<?>>> ENTITY_REGISTRIES = new HashMap<>();
    public static List<EntityAttributeEntries> ENTITY_ATTRIBUTES = new ArrayList<>();

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
        LoaderProperties.setEntityAttributeRegisterer((entityType, attributes) -> ENTITY_ATTRIBUTES.add(new EntityAttributeEntries(entityType,attributes)));
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


    public static void register() {
        for (var reg : BLOCK_REGISTRIES.values()) {
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        for (var reg : ITEM_REGISTRIES.values()) {
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        for (var reg : ENTITY_REGISTRIES.values()) {
            reg.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    public record EntityAttributeEntries(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer.Builder attributes) {}
}
