package net.flytre.flytre_lib.impl.config;

import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.network.SyncedConfig;
import net.minecraft.server.PlayerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Register configs here to allow loading/reloading to be handled automatically by FlytreLib
 */
@ApiStatus.Internal
public final class ConfigRegistryImpl {
    private static final List<ConfigHandler<?>> SERVER_CONFIGS;
    private static final List<ConfigHandler<?>> CLIENT_CONFIGS;

    static {
        SERVER_CONFIGS = new ArrayList<>();
        CLIENT_CONFIGS = new ArrayList<>();
    }

    private ConfigRegistryImpl() {
    }


    public static void registerClientConfig(ConfigHandler<?> handler) {
        CLIENT_CONFIGS.add(handler);
    }

    public static void registerServerConfig(ConfigHandler<?> handler) {
        SERVER_CONFIGS.add(handler);
    }


    public static int reloadServerConfigs(@Nullable PlayerManager manager) {
        for (ConfigHandler<?> configHandler : SERVER_CONFIGS) {
            configHandler.handle();
        }

        if (manager != null)
            getServerConfigs().forEach(i -> {
                if (i.getConfig() instanceof SyncedConfig)
                    manager.getPlayerList().forEach(player -> player.networkHandler.sendPacket(new ConfigS2CPacket(i)));
            });

        return SERVER_CONFIGS.size();
    }


    public static int reloadClientConfigs() {
        for (ConfigHandler<?> configHandler : CLIENT_CONFIGS)
            configHandler.handle();
        return CLIENT_CONFIGS.size();
    }


    public static List<ConfigHandler<?>> getServerConfigs() {
        return Collections.unmodifiableList(SERVER_CONFIGS);
    }

    public static List<ConfigHandler<?>> getClientConfigs() {
        return Collections.unmodifiableList(CLIENT_CONFIGS);
    }
}
