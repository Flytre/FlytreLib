package net.flytre.flytre_lib.api.loader;

import net.flytre.flytre_lib.impl.loader.LoaderPropertyInitializer;

public class LoaderCore {

    /**
     * All forge mods should call this method in the initializer if they register anything custom!
     */
    public static void registerForgeMod(String mod, Runnable initializer) {
        initializer.run();
        LoaderPropertyInitializer.register(mod);
    }

}
