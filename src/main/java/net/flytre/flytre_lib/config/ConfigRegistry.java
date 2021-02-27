package net.flytre.flytre_lib.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;

public class ConfigRegistry {
    private static final List<ConfigHandler<?>> SERVER_CONFIGS;
    private static final List<ConfigHandler<?>> CLIENT_CONFIGS;

    static {
        SERVER_CONFIGS = new ArrayList<>();
        CLIENT_CONFIGS = new ArrayList<>();
    }

    public static void registerServerConfig(ConfigHandler<?> handler) {
        SERVER_CONFIGS.add(handler);
    }

    public static int reloadServerConfigs() {
        for (ConfigHandler<?> configHandler : SERVER_CONFIGS) {
            configHandler.handle();
        }
        return SERVER_CONFIGS.size();
    }


    @Environment(EnvType.CLIENT)
    public static void registerClientConfig(ConfigHandler<?> handler) {
        CLIENT_CONFIGS.add(handler);
        handler.handle();
    }

    @Environment(EnvType.CLIENT)
    public static void reloadClientConfigs() {
        for (ConfigHandler<?> configHandler : CLIENT_CONFIGS) {
            configHandler.handle();
        }
    }
}
