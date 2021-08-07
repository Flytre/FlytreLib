package net.flytre.flytre_lib.api.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.impl.config.ConfigRegistryImpl;
import net.minecraft.server.PlayerManager;
import org.jetbrains.annotations.Nullable;


/**
 * Register configs here for FlytreLib to take care of all processing;
 */
public final class ConfigRegistry {
    /**
     * Remember, if you want data on the config to be sent to the client, you must call this method in a place where
     * both the client and server see it (i.e. the main initializer, not the server initializer)
     */
    public static void registerServerConfig(ConfigHandler<?> handler) {
        ConfigRegistryImpl.registerServerConfig(handler);
    }

    public static int reloadServerConfigs(@Nullable PlayerManager manager) {
        return ConfigRegistryImpl.reloadServerConfigs(manager);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientConfig(ConfigHandler<?> handler) {
        ConfigRegistryImpl.registerClientConfig(handler);
        handler.handle();
    }

    @Environment(EnvType.CLIENT)
    public static int reloadClientConfigs() {
        return ConfigRegistryImpl.reloadClientConfigs();
    }
}
