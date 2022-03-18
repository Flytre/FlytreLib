package net.flytre.flytre_lib.api.config;

import net.flytre.flytre_lib.impl.config.ConfigRegistryImpl;
import net.minecraft.server.PlayerManager;
import org.jetbrains.annotations.Nullable;


/**
 * Register configs here for FlytreLib to take care of all processing;
 */
public final class ConfigRegistry {
    private ConfigRegistry() {
    }

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


    public static void registerClientConfig(ConfigHandler<?> handler) {
        ConfigRegistryImpl.registerClientConfig(handler);
        handler.handle();
    }


    public static int reloadClientConfigs() {
        return ConfigRegistryImpl.reloadClientConfigs();
    }
}
