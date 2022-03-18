package net.flytre.flytre_lib.api.config.network;


/**
 * A synced config is a server side config whose values are synced *to* the client, so on the client the values of the server config
 * are the same as on the server.
 * <p>
 * Note that a synced config needs to be registered as a server side config on both the client and the server (so for example in the main entrypoint,
 * as that runs on both the client and the server, but not on only the client or only server the entry point)
 */
public interface SyncedConfig {
}
