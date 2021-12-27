package net.flytre.flytre_lib.loader;

import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.impl.config.init.FlytreLibConfig;
import net.flytre.flytre_lib.loader.registry.BlockRegisterer;
import net.flytre.flytre_lib.loader.registry.EntityRegisterer;
import net.flytre.flytre_lib.loader.registry.ItemRegisterer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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

    public static <T extends Block> T registerBlock(T block, String mod, String id) {
        assert BLOCK_REGISTERER != null;
        return BLOCK_REGISTERER.register(block, mod, id);
    }

    public static <T extends Item> T registerItem(T item, String mod, String id) {
        assert ITEM_REGISTERER != null;
        return ITEM_REGISTERER.register(item, mod, id);
    }

    public static <E extends Entity, T extends EntityType<E>> T registerEntity(T entity, String mod, String id) {
        assert ENTITY_REGISTER != null;
        return ENTITY_REGISTER.register(entity, mod, id);
    }


}
