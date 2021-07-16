package net.flytre.flytre_lib.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.flytre.flytre_lib.config.network.ConfigS2CPacket;
import net.flytre.flytre_lib.config.network.SyncedConfig;
import net.minecraft.server.PlayerManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Register configs here to allow loading/reloading to be handled automatically by FlytreLib
 */
public class ConfigRegistry {
    private static final List<ConfigHandler<?>> SERVER_CONFIGS;
    private static final List<ConfigHandler<?>> CLIENT_CONFIGS;

    static {
        SERVER_CONFIGS = new ArrayList<>();
        CLIENT_CONFIGS = new ArrayList<>();
    }


    /**
     * Remember, if you want data on the config to be sent to the client, you must call this method in a place where
     * both the client and server see it (i.e. the main initializer, not the server initializer)
     */
    public static void registerServerConfig(ConfigHandler<?> handler) {
        SERVER_CONFIGS.add(handler);
    }

    public static int reloadServerConfigs(@Nullable PlayerManager manager) {
        for (ConfigHandler<?> configHandler : SERVER_CONFIGS) {
            configHandler.handle();
        }

        if (manager != null)
            ConfigRegistry.getServerConfigs().forEach(i -> {
                if (i.getConfig() instanceof SyncedConfig)
                    manager.getPlayerList().forEach(player -> ServerPlayNetworking.send(player, ConfigS2CPacket.PACKET_ID, new ConfigS2CPacket(i).toPacket()));
            });

        return SERVER_CONFIGS.size();
    }


    @Environment(EnvType.CLIENT)
    public static void registerClientConfig(ConfigHandler<?> handler) {
        CLIENT_CONFIGS.add(handler);
        handler.handle();
    }

    @Environment(EnvType.CLIENT)
    public static int reloadClientConfigs() {
        for (ConfigHandler<?> configHandler : CLIENT_CONFIGS)
            configHandler.handle();
        return CLIENT_CONFIGS.size();
    }


    public static List<ConfigHandler<?>> getServerConfigs() {
        return Collections.unmodifiableList(SERVER_CONFIGS);
    }
}
