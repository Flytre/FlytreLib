package net.flytre.flytre_lib.loader;

import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.impl.config.init.FlytreLibConfig;
import net.flytre.flytre_lib.loader.registry.*;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.Item;

import java.nio.file.Path;
import java.util.function.Function;

public final class LoaderProperties {
    public static ConfigHandler<FlytreLibConfig> HANDLER = null;
    private static boolean DEV_ENVIRONMENT = false;
    private static Function<String, String> MOD_ID_TO_NAME = null;
    private static Path MOD_CONFIG_DIRECTORY;

    private static BlockRegisterer BLOCK_REGISTERER;
    private static EntityRegisterer ENTITY_REGISTER;
    private static ItemRegisterer ITEM_REGISTERER;
    private static BlockEntityRendererRegisterer BLOCK_ENTITY_RENDERER_REGISTERER;
    private static EntityRendererRegisterer ENTITY_RENDERER_REGISTERER;
    private static EntityAttributeRegisterer ENTITY_ATTRIBUTE_REGISTERER;

    public static Path getModConfigDirectory() {
        return MOD_CONFIG_DIRECTORY;
    }

    public static void setModConfigDirectory(Path modConfigDirectory) {
        MOD_CONFIG_DIRECTORY = modConfigDirectory;
    }

    public static String getModName(String modId) {
        if (MOD_ID_TO_NAME == null)
            throw new AssertionError("");
        else
            return MOD_ID_TO_NAME.apply(modId);
    }

    public static void setModIdToName(Function<String, String> modIdToName) {
        MOD_ID_TO_NAME = modIdToName;
    }

    public static boolean isDevEnvironment() {
        return DEV_ENVIRONMENT;
    }

    public static void setDevEnvironment(boolean isDevEnvironment) {
        DEV_ENVIRONMENT = isDevEnvironment;
    }

    public static void setBlockRegisterer(BlockRegisterer blockRegisterer) {
        BLOCK_REGISTERER = blockRegisterer;
    }

    public static void setEntityRegister(EntityRegisterer entityRegister) {
        ENTITY_REGISTER = entityRegister;
    }

    public static void setItemRegisterer(ItemRegisterer itemRegisterer) {
        ITEM_REGISTERER = itemRegisterer;
    }

    public static void setBlockEntityRendererRegisterer(BlockEntityRendererRegisterer blockEntityRendererRegisterer) {
        BLOCK_ENTITY_RENDERER_REGISTERER = blockEntityRendererRegisterer;
    }

    public static void setEntityRendererRegisterer(EntityRendererRegisterer entityRendererRegisterer) {
        ENTITY_RENDERER_REGISTERER = entityRendererRegisterer;
    }

    public static void setEntityAttributeRegisterer(EntityAttributeRegisterer entityAttributeRegisterer) {
        ENTITY_ATTRIBUTE_REGISTERER = entityAttributeRegisterer;
    }

    public static void register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer.Builder attributes) {
        assert ENTITY_ATTRIBUTE_REGISTERER != null;
        ENTITY_ATTRIBUTE_REGISTERER.register(entityType, attributes);
    }

    public static <T extends Block> T register(T block, String mod, String id) {
        assert BLOCK_REGISTERER != null;
        return BLOCK_REGISTERER.register(block, mod, id);
    }

    public static <T extends Item> T register(T item, String mod, String id) {
        assert ITEM_REGISTERER != null;
        return ITEM_REGISTERER.register(item, mod, id);
    }

    public static <E extends Entity, T extends EntityType<E>> T register(T entity, String mod, String id) {
        assert ENTITY_REGISTER != null;
        return ENTITY_REGISTER.register(entity, mod, id);
    }

    public static <T extends Entity> void register(EntityType<? extends T> type, EntityRendererFactory<T> factory) {
        assert ENTITY_RENDERER_REGISTERER != null;
        ENTITY_RENDERER_REGISTERER.register(type, factory);
    }

    public static <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRendererFactory<? super E> blockEntityRendererFactory) {
        assert BLOCK_ENTITY_RENDERER_REGISTERER != null;
        BLOCK_ENTITY_RENDERER_REGISTERER.register(blockEntityType, blockEntityRendererFactory);
    }


}
