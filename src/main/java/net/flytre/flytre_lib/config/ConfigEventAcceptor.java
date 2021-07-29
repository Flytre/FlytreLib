package net.flytre.flytre_lib.config;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * FlytreLibConfig events.
 */
public interface ConfigEventAcceptor {

    /**
     * Triggers whenever the config is reloaded / done being edited. Useful for caching and processing config data
     */
    default void onReload() {}

    /**
     * Triggers whenever the client joins a server, leaves a servers, or switches servers using a plugin (i.e. switching lobbies)
     */
    @Environment(EnvType.CLIENT)
    default void onServerStatusChanged() {}
}
