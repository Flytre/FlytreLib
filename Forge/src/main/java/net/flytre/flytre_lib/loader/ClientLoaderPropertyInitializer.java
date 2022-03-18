package net.flytre.flytre_lib.loader;

public final class ClientLoaderPropertyInitializer {

    private ClientLoaderPropertyInitializer() {
    }

    public static void init() {
        LoaderAgnosticClientRegistryImpl.init();
    }

}
