package net.flytre.flytre_lib.api.config;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Config events. Implement this interface to execute code on events.
 */
public interface ConfigEventAcceptor {

    /**
     * Triggers whenever the config is reloaded / done being edited. Useful for caching and processing config data
     */
    default void onReload() {}

    /**
     * Triggers whenever the client joins a server, leaves a server, or switches servers using a plugin (i.e. switching lobbies)
     */
    @Environment(EnvType.CLIENT)
    default void onServerStatusChanged() {}
}
