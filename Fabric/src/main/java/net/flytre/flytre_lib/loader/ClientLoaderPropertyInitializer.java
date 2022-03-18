package net.flytre.flytre_lib.loader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientLoaderPropertyInitializer {


    private ClientLoaderPropertyInitializer() {
        throw new AssertionError();
    }

    /**
     * Called before anything else happens in the game via mixin.
     */
    public static void init() {
        LoaderAgnosticClientRegistryImpl.init();
    }


}
