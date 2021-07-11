package net.flytre.flytre_lib.config;


/**
 * Config events. Currently just allows custom code, i.e. caching, to happen on config reload.
 */
public interface ConfigEventAcceptor {
    void onReload();
}
