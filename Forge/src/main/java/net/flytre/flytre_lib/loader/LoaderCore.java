package net.flytre.flytre_lib.loader;

public final class LoaderCore {

    private LoaderCore() {
    }

    /**
     * All forge mods should call this method in the initializer if they register anything custom!
     */
    public static void registerForgeMod(String mod, Runnable initializer) {
        initializer.run();
        LoaderAgnosticRegistryImpl.register(mod);
    }

}
