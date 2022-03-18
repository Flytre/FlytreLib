package net.flytre.flytre_lib.api.config;


/**
 * Config events. Implement this interface to execute code on events.
 */
public interface ConfigEventAcceptor {

    /**
     * Triggers whenever the config is reloaded / done being edited. Useful for caching and processing config data
     */
    default void onReload() {
    }

    /**
     * Triggers whenever the client joins a server, leaves a server, or switches servers using a plugin (i.e. switching lobbies)
     */

    default void onServerStatusChanged() {
    }
}
