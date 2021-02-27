package net.flytre.flytre_lib.config;

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

    public static void registerClientConfig(ConfigHandler<?> handler) {
        CLIENT_CONFIGS.add(handler);
    }

    public static int reloadClientConfigs() {
        for (ConfigHandler<?> configHandler : CLIENT_CONFIGS) {
            configHandler.handle();
        }
        return CLIENT_CONFIGS.size();
    }
}
